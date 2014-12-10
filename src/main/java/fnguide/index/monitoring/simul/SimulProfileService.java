package fnguide.index.monitoring.simul;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fnguide.index.monitoring.stock.StockCommonService;
import fnguide.index.monitoring.utility.conv;
import fnguide.index.monitoring.utility.Ut;
import fnguide.index.monitoring.utility.math.Stat;
import fnguide.index.monitoring.utility.math.Stat.FreqType;

@Service
public class SimulProfileService extends SqlSessionDaoSupport {
	
	@Autowired
	StockCommonService stockCommonService;
	
	public enum IntervalType
	{
		Days,
		Months
	}
	
	/**
	 * 기능 : 리스크프로파일 제이슨 리턴 
	 * @param u_cd		종목코드
	 * @param u_cd_bm	벤치마크 종목코드
	 * @param t0		투입데이터 초기시점
	 * @param t1		투입데이터 마지막시점
	 * @return
	 */
	public JSONObject GetRiskProfileJson(String simul_id, String u_cd, String u_cd_bm, String t0, String t1){
		
		JSONObject jsonResult = new JSONObject();
		HashMap<String, Object> timeSeriesData = GetIdxMonthlyTimeSeriesWithBm(simul_id, u_cd, u_cd_bm, t0, t1);
		Double[] yield = (Double[])timeSeriesData.get("YIELD");
		Double[] yield_bm = (Double[])timeSeriesData.get("YIELD_BM");
		Double[] yield_rf = (Double[])timeSeriesData.get("RF");
		
		Double gr = Stat.GetGeometricReturn(yield, FreqType.Monthly);		
		Double grBm = Stat.GetGeometricReturn(yield_bm, FreqType.Monthly);
		Double vol = Stat.GetStd(yield) * Math.sqrt(12);
		Double volBm = Stat.GetStd(yield_bm)  * Math.sqrt(12);
		
		Double[] excessReturn = new Double[yield.length];		
		Double[] excessReturnBm = new Double[yield.length];
		
		for(int i=0; i<excessReturn.length; i++){
			excessReturn[i] = yield[i] - yield_rf[i];
			excessReturnBm[i] = yield_bm[i] - yield_rf[i];
		}
		
		// Sharp Ratio 는 조금 더 고민해 볼것!
		Double sharpRatio = Stat.GetAvg(excessReturn) / Stat.GetStd(excessReturn) * Math.sqrt(12);
		Double sharpRatioBm = Stat.GetAvg(excessReturnBm) / Stat.GetStd(excessReturnBm) * Math.sqrt(12);
		
		ArrayList<Double> downSideReturn = new ArrayList<Double>();
		for(int i=0; i<yield.length; i++){
			if(yield[i] < yield_bm[i]){
				downSideReturn.add(yield[i]);
			}
		}
		
		Double downSideVol = Stat.GetStd(downSideReturn.toArray(new Double[downSideReturn.size()])) * Math.sqrt(12);
		Double beta = Stat.GetCov(yield, yield_bm) / Stat.GetVar(yield_bm);
		Double alpha = Stat.GetAlpha(yield, yield_bm, yield_rf, beta) * 12;
		Double te = Stat.GetTe(yield, yield_bm, yield_rf, beta) * Math.sqrt(12);
		Double ir = alpha / te;
		
		
		// json 세팅
		JSONObject jsonIdx = new JSONObject();
		jsonIdx.put("GR", Ut.df2(gr));
		jsonIdx.put("VOL", Ut.df2(vol));
		jsonIdx.put("SR", Ut.df3(sharpRatio));
		jsonIdx.put("DSD", Ut.df2(downSideVol));
		jsonIdx.put("BETA", Ut.df3(beta));
		jsonIdx.put("ALPHA", Ut.df2(alpha));
		jsonIdx.put("TE", Ut.df2(te));
		jsonIdx.put("IR", Ut.df3(ir));
		
		JSONObject jsonBm = new JSONObject();
		jsonBm.put("GR", Ut.df2(grBm));
		jsonBm.put("VOL", Ut.df2(volBm));
		jsonBm.put("SR", Ut.df3(sharpRatioBm));
		
		jsonResult.put("인덱스", jsonIdx);
		jsonResult.put("벤치마크", jsonBm);		
		jsonResult.put("지수수익률", yield);
		jsonResult.put("벤치마크수익률", yield_bm);
		
		return jsonResult;
	}
	
	/**
	 * 기능 : 리턴 프로파일 제이슨 반환
	 * @param u_cd		종목코드
	 * @param u_cd_bm	벤치마크 종목코드
	 * @param t0		투입데이터 초기시점
	 * @param t1		투입데이터 마지막시점
	 * @return
	 */
	public JSONObject GetReturnProfileJson(String simul_id, String u_cd, String u_cd_bm, String t0, String t1){
				
		JSONObject jsonResult = new JSONObject();
		
		//서식용
		DecimalFormat df = new DecimalFormat("0.00%");
		
		//String dt_w1, dt_m1, dt_m3, dt_m6, dt_y1, dt_y3, dt_y5, total;
		String[] dtList = new String[8];		
		String min_dt, max_dt;

		//지수 유효 시계열 검색 start_dt, end_dt 검색		
		String[] arrTemp = GetMinMaxDt(simul_id, u_cd);
		min_dt = arrTemp[0];
		max_dt = arrTemp[1];
		
		dtList[0] = GetAvailableDt(t1, 7, IntervalType.Days);
		dtList[1] = GetAvailableDt(t1, 1, IntervalType.Months);
		dtList[2] = GetAvailableDt(t1, 3, IntervalType.Months);
		dtList[3] = GetAvailableDt(t1, 6, IntervalType.Months);
		dtList[4] = GetAvailableDt(t1, 12, IntervalType.Months);
		dtList[5] = GetAvailableDt(t1, 36, IntervalType.Months);
		dtList[6] = GetAvailableDt(t1, 60, IntervalType.Months);
		dtList[7] = min_dt;

		// 시계열 최소, 최대 시점 결과값 저장
		JSONObject jsonInfo = new JSONObject();
		jsonInfo.put("지수코드", u_cd);
		jsonInfo.put("벤치마크지수코드", u_cd_bm);
		jsonInfo.put("최소", min_dt);
		jsonInfo.put("최대", max_dt);
		jsonResult.put("정보", jsonInfo);
		
		// 해당 지수 리턴프로파일 계산
		Double currentPrc = GetIdxClsPrcFromRes(simul_id, u_cd, t1);
		Double currentPrcBm = GetIdxClsPrc(u_cd_bm, t1);
		List<Double[]> clsPrcListArray = GetIdxClsPrcWithBmMultiPoint(simul_id, u_cd, u_cd_bm, dtList);
		
		Double[] returnList = new Double[8];
		Double[] returnListBm = new Double[8];
		
		// DtList 지수 종가 가져오기		
		for(int i=0; i<8; i++){			
			returnList[i] = currentPrc / clsPrcListArray.get(0)[i] - 1;
			returnListBm[i] = currentPrcBm / clsPrcListArray.get(1)[i] - 1;
		}
		returnList[7] =  currentPrc / GetIdxClsPrcFromRes(simul_id, u_cd, min_dt) - 1;
		
		JSONObject objReturnProfile = new JSONObject();
		objReturnProfile.put("W1", df.format(returnList[0]));
		objReturnProfile.put("M1", df.format(returnList[1]));
		objReturnProfile.put("M3", df.format(returnList[2]));
		objReturnProfile.put("M6", df.format(returnList[3]));
		objReturnProfile.put("Y1", df.format(returnList[4]));
		objReturnProfile.put("Y3", df.format(returnList[5]));
		objReturnProfile.put("Y5", df.format(returnList[6]));
		objReturnProfile.put("TOTAL", df.format(returnList[7]));		
		jsonResult.put("인덱스", objReturnProfile);
		
		JSONObject objReturnProfileBm = new JSONObject();
		objReturnProfileBm.put("W1", df.format(returnListBm[0]));
		objReturnProfileBm.put("M1", df.format(returnListBm[1]));
		objReturnProfileBm.put("M3", df.format(returnListBm[2]));
		objReturnProfileBm.put("M6", df.format(returnListBm[3]));
		objReturnProfileBm.put("Y1", df.format(returnListBm[4]));
		objReturnProfileBm.put("Y3", df.format(returnListBm[5]));
		objReturnProfileBm.put("Y5", df.format(returnListBm[6]));
		objReturnProfileBm.put("TOTAL", df.format(returnListBm[7]));		
		jsonResult.put("벤치마크", objReturnProfileBm);
		
		return jsonResult;
	}
	
	public JSONObject GetInfoJson(String simul_id, String u_cd, String u_cd_bm, String t0, String t1){
		JSONObject result = new JSONObject();
		
		HashMap<String, Object> timeSeriesData = GetIdxMonthlyTimeSeriesWithBm(simul_id, u_cd, u_cd_bm, t0, t1);
		ArrayList<Object[]> source = new ArrayList<Object[]>();
		ArrayList<Object[]> source2 = new ArrayList<Object[]>();
		
		String[] dateList = (String[])timeSeriesData.get("DATE");
		Double[] clsPrc = (Double[])timeSeriesData.get("CLS_PRC");
		Double[] clsPrcBm = (Double[])timeSeriesData.get("CLS_PRC_BM");		
		for(int i=0; i<dateList.length; i++){
			Object[] data = new Object[2];
			data[0] = conv.GetUtc(dateList[i]);
			data[1] = clsPrc[i];
			
			Object[] data2 = new Object[2];
			data2[0] = conv.GetUtc(dateList[i]);
			data2[1] = clsPrcBm[i];
			
			source.add(data);
			source2.add(data2);
		}
		
		result.put("SERIES1", source);
		result.put("SERIES2", source2);
		result.put("지수이름", stockCommonService.GetUnmByUcd(u_cd));
		result.put("벤치마크이름", stockCommonService.GetUnmByUcd(u_cd_bm));
		result.put("T0", conv.Date2Date(t0, "/"));
		result.put("T1", conv.Date2Date(t1, "/"));
				
		return result;
	}
	
	/**
	 * 기능 : 해당시점 이전의 유효한 날짜 반환
	 * @param dt		해당일자
	 * @param interval	간격(예 : 6개월 전)
	 * @param type		간격구분(월별, 일별)
	 * @return
	 */
	public String GetAvailableDt(String dt, Integer interval, IntervalType type){
		
		List<HashMap<String, String>> dbOutput = null;
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("dt", dt);
		input.put("interval", interval);
		
		if(type == IntervalType.Days)
			dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectPastAvailableDateByDays", input);
		else if(type == IntervalType.Months)
			dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectPastAvailableDateByMonths", input);
		
		return dbOutput.get(0).get("TRD_DT");
	}
	
	public String[] GetMinMaxDt(String simul_id, String u_cd){
		
		String[] result = new String[2];
		
		List<HashMap<String, String>> dbOutput = null;
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("simul_id", simul_id);
		
		dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectMinMaxDtUcd", input);
		result[0] = dbOutput.get(0).get("MIN");
		result[1] = dbOutput.get(0).get("MAX");
		
		return result;
	}
	
	// 한 시점의 종가 가져오기	
	public Double GetIdxClsPrc(String u_cd, String trd_dt){
		
		Object result = null;		
		List<HashMap<String, String>> dbOutput = null;
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("trd_dt", trd_dt);		
		dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectClsPrice", input);
		if(dbOutput.size() == 0){			
			try {
				throw new Exception("해당 날짜에 종가가 없습니다. " + u_cd + " : " + trd_dt );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		result = dbOutput.get(0).get("CLS_PRC");
		if(result == null)
			try {
				throw new Exception("해당 날짜에 종가가 없습니다. " + u_cd + " : " + trd_dt );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return ((BigDecimal)result).doubleValue();
	}
	
	// 한 시점의 종가 가져오기	
		public Double GetIdxClsPrcFromRes(String simul_id, String u_cd, String trd_dt){
			
			Object result = null;		
			List<HashMap<String, String>> dbOutput = null;
			HashMap<String, Object> input = new HashMap<String, Object>();
			input.put("u_cd", u_cd);
			input.put("trd_dt", trd_dt);
			input.put("simul_id", simul_id);
						
			dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectClsPriceRes", input);
			if(dbOutput.size() == 0){			
				try {
					throw new Exception("해당 날짜에 종가가 없습니다. " + u_cd + " : " + trd_dt );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			result = dbOutput.get(0).get("CLS_PRC");
			if(result == null)
				try {
					throw new Exception("해당 날짜에 종가가 없습니다. " + u_cd + " : " + trd_dt );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return ((BigDecimal)result).doubleValue();
		}
		
	/** 
	 * @param u_cd			지수코드
	 * @param u_cd_bm		벤치마크지수 코드
	 * @param timePoint		시점 배열(종가를 가져오는 시점)	
	 * @return 해당지수와, 벤치마크지수의 종가를 가져옴.
	 */
	public ArrayList<Double[]> GetIdxClsPrcWithBmMultiPoint(String simul_id, String u_cd, String u_cd_bm, String[] timePoint){
		ArrayList<Double[]> result = new ArrayList<Double[]>();		
		List<HashMap<String, Double>> dbOutput = null;
		
		//(#{w1}, #{m1}, #{m3}, #{m6}, #{y1}, #{y3}, #{y5}} 
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("u_cd_bm", u_cd_bm);
		
		input.put("w1", timePoint[0]);
		input.put("m1", timePoint[1]);
		input.put("m3", timePoint[2]);
		input.put("m6", timePoint[3]);
		input.put("y1", timePoint[4]);
		input.put("y3", timePoint[5]);
		input.put("y5", timePoint[6]);
		input.put("total", timePoint[7]);
		input.put("simul_id", simul_id);
		
		dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectClsPriceWithBmMultiPoint", input);
		
		Double[] idxList = new Double[dbOutput.size()];
		Double[] idxListBm = new Double[dbOutput.size()];
		
		for(int i=0; i<dbOutput.size(); i++){
			idxList[i] = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC"));
			idxListBm[i] = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC_BM"));
		}
		
		result.add(idxList);
		result.add(idxListBm);
		
		return result;
	}
	
	/**
	 * 기능 : 지수 주별 시계열을 벤치마크와 함께 가져옴.
	 * @param u_cd		지수코드
	 * @param u_cd_bm	벤치마크지수코드
	 * @param t0		start 시점
	 * @param t1		end 시점
	 * @return			HashMap CLS_PRC, CLS_PRC_BM, YIELD, YIELD_BM, DATE 
	 */
	public HashMap<String, Object> GetIdxMonthlyTimeSeriesWithBm(String simul_id, String u_cd, String u_cd_bm, String t0, String t1){
		
		HashMap<String, Object> result = new HashMap<String, Object>();		
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("u_cd_bm", u_cd_bm);
		input.put("t0", t0);
		input.put("t1", t1);
		input.put("simul_id", simul_id);
		
		List<HashMap<String, Object>> dbOutput = getSqlSession().selectList("SimulProfileQueryMapper.selectIdxMonthlyTimeSeriesWithBm", input);
		
		Integer size = dbOutput.size();
		String[] dateList = new String[size];
		Double[] clsPrcList = new Double[size];
		Double[] yieldList = new Double[size-1]; // 종가로부터 수익률을 구해야 하므로 배열 길이가 하나 작음.
		Double[] clsPrcBmList = new Double[size];
		Double[] yieldBmList = new Double[size-1];
		Double[] riskFreeRateList = new Double[size];
		Double[] riskFreeRateList2 = new Double[size-1]; // 무위험이자율 하나 뺀거
		
		for(int i=0; i<dbOutput.size(); i++){
			dateList[i] = (String)dbOutput.get(i).get("YMD");
			clsPrcList[i] = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC"));			
			clsPrcBmList[i] = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC_BM"));
			riskFreeRateList[i] = conv.BigDecimal2Double(dbOutput.get(i).get("RF"));
		}
		
		for(int i=0; i<yieldList.length; i++){
			yieldList[i] = clsPrcList[i+1] / clsPrcList[i] -1;
			yieldBmList[i] = clsPrcBmList[i+1] / clsPrcBmList[i] - 1;
			riskFreeRateList2[i] = riskFreeRateList[i+1];
		}
		
		result.put("DATE", dateList);
		result.put("CLS_PRC", clsPrcList);
		result.put("YIELD", yieldList);
		result.put("CLS_PRC_BM", clsPrcBmList);
		result.put("YIELD_BM", yieldBmList);
		result.put("RF", riskFreeRateList2);
		
		return result;
	}
	
	public Double[] GetIdxClsPrcWithBm(String u_cd, String u_cd_bm, String trd_dt){
		
		Object[] result = new Object[2];		
		List<HashMap<String, String>> dbOutput = null;
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("u_cd_bm", u_cd_bm);
		input.put("trd_dt", trd_dt);		
		
		dbOutput = getSqlSession().selectList("StockProfileQueryMapper.selectClsPriceWithBm", input);
		if(dbOutput.size() == 0){			
			try {
				throw new Exception("해당 날짜에 종가가 없습니다. " + u_cd + " : " + trd_dt );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		result[0] = dbOutput.get(0).get("CLS_PRC");
		result[1] = dbOutput.get(1).get("CLS_PRC_BM");
		if(result == null)
			try {
				throw new Exception("해당 날짜에 종가가 없습니다. " + u_cd + " : " + trd_dt );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Double[] result2 = new Double[2];
		result2[0] = ((BigDecimal)result[0]).doubleValue();
		result2[1] = ((BigDecimal)result[1]).doubleValue();
		
		return result2;
	}
		
	private List<HashMap<String, Object>> format(List<HashMap<String, Object>> input){
		
		DecimalFormat df = new DecimalFormat("#,###");		
		DecimalFormat df2 = new DecimalFormat("0.00%");
		DecimalFormat df3 = new DecimalFormat("0.0000");
		
		for(int i=0; i<input.size(); i++){			
			input.get(i).put("지수채용주식수", df.format(input.get(i).get("지수채용주식수")));
			input.get(i).put("지수채용시가총액", df.format(input.get(i).get("지수채용시가총액")));
			input.get(i).put("주가", df.format(input.get(i).get("주가")));		
			input.get(i).put("비중", df2.format(input.get(i).get("비중")));
			input.get(i).put("상장주식수", df.format(input.get(i).get("상장주식수")));
			input.get(i).put("상장예정주식수", df.format(input.get(i).get("상장예정주식수")));			
		}		
		return input;	
	}

	
}
