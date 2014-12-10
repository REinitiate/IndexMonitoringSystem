package fnguide.index.monitoring.simul.nps;

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

@Service
public class NpsStatGenerator extends SqlSessionDaoSupport {
	
	@Autowired
	StockCommonService stockCommonService;
	
	public JSONObject GetStats(String simul_id, String u_cd, String u_cd_bm, String t0, String t1){		
		
		JSONObject jsonResult = new JSONObject();
		
		/*
		 * 	누적 연환산 초과수익률
		 *  TE
		 *  IR
		 *  BM 대비 초과 수익률  
		 */
		
		// 전일자의 전일자까지
		String t0_1 = (String) getSqlSession().selectOne("NpsSimulProfileQueryMapper.selectTrdDtPday", t0);
		if(t0.compareTo("20010102") == 0){
			t0_1 = "20010102";
		}
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("simul_id", simul_id);
		param.put("u_cd", u_cd);
		param.put("u_cd_bm", u_cd_bm);
		param.put("t0", t0_1); // 전일자를 넣는다.
		param.put("t1", t1);
		
		List<HashMap> dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectDailyTsWithBm", param);
		
		Integer size = dbOutput.size()-1;
		
		Double[] dr = new Double[size];   // 일별수익률
		Double[] drBm = new Double[size]; // 일별수익률BM
		Double[] exr = new Double[size];  // 초과수익률
		
		Double[] retKospi = new Double[size];
		Double[] retKospi100 = new Double[size];
		Double[] retKospi200 = new Double[size];
				
		Double[] exrKospi = new Double[size];
		Double[] exrKospi100 = new Double[size];
		Double[] exrKospi200 = new Double[size];
		
		// t0
		
		for(int i=0; i<size; i++){
			Double y = null;
			Double yBm = null;
			Double yKospi = null;
			Double yKospi100 = null;
			Double yKospi200 = null;
			
			y = conv.BigDecimal2Double(dbOutput.get(i+1).get("CLS_PRC")) / conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC")) - 1;
			yBm = conv.BigDecimal2Double(dbOutput.get(i+1).get("CLS_PRC_BM")) / conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC_BM")) - 1;
			yKospi = conv.BigDecimal2Double(dbOutput.get(i+1).get("KOSPI")) / conv.BigDecimal2Double(dbOutput.get(i).get("KOSPI")) - 1;
			yKospi100 = conv.BigDecimal2Double(dbOutput.get(i+1).get("KOSPI100")) / conv.BigDecimal2Double(dbOutput.get(i).get("KOSPI100")) - 1;
			yKospi200 = conv.BigDecimal2Double(dbOutput.get(i+1).get("KOSPI200")) / conv.BigDecimal2Double(dbOutput.get(i).get("KOSPI200")) - 1;				
			
			dr[i] = y;
			drBm[i] = yBm;
			exr[i] = y - yBm;
			exrKospi[i] = y - yKospi;
			exrKospi100[i] = y - yKospi100;
			exrKospi200[i] = y - yKospi200;
		}
		
		Double te = std(exr) * Math.sqrt(250);
		Double gr = gr(exr);
		Double ir = gr/te;		
		Double teNps = TeNpS(exr);
		Double irNps = gr / teNps;
		
		jsonResult.put("te", te);
		jsonResult.put("te_nps", teNps);
		jsonResult.put("gr", gr);
		jsonResult.put("ir", ir);
		jsonResult.put("ir_nps", irNps);
		jsonResult.put("corr_kospi", corr(exr, exrKospi));
		jsonResult.put("corr_kospi100", corr(exr, exrKospi100));
		jsonResult.put("corr_kospi200", corr(exr, exrKospi200));
		
		/*
		 * Hit ratio
		 */		
		ArrayList<String> endDt = new ArrayList<String>();
		ArrayList<Double> rList = new ArrayList<Double>();
		ArrayList<Double> rBmList = new ArrayList<Double>();		
		exr = new Double[dbOutput.size()]; // 초과수익률
		Integer cntHit = 0;
		
		Double[] prePrc = new Double[2]; // 0:지수, 1:BM
		prePrc[0] = conv.BigDecimal2Double(dbOutput.get(0).get("CLS_PRC"));
		prePrc[1] = conv.BigDecimal2Double(dbOutput.get(0).get("CLS_PRC_BM"));		
		for(int i=1; i<dbOutput.size(); i++){
			String sig = (String)dbOutput.get(i).get("M");
			if(sig.compareTo("X") != 0){ // 월말
				Double prc = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC"));
				Double bm = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC_BM"));
				rList.add(prc/prePrc[0]-1);
				rBmList.add(bm/prePrc[1]-1);
				prePrc[0] = prc;
				prePrc[1] = bm;
			}			
		}
		for(int i=0; i<rList.size(); i++){
			if(rList.get(i) > rBmList.get(i)) cntHit++;
		}
		Double hitRatio = cntHit.doubleValue() / new Double(rList.size());
		jsonResult.put("hit_ratio", hitRatio);
		
		/*
		 * Maximum drawdown
		 */
		ArrayList<String> dtList = new ArrayList<String>();
		ArrayList<Double> exr2 = new ArrayList<Double>();
		prePrc = new Double[2]; // 0:지수, 1:BM
		prePrc[0] = conv.BigDecimal2Double(dbOutput.get(0).get("CLS_PRC"));
		prePrc[1] = conv.BigDecimal2Double(dbOutput.get(0).get("CLS_PRC_BM"));		
		for(int i=1; i<dbOutput.size(); i++){
			String sig = (String)dbOutput.get(i).get("W");
			if(sig.compareTo("X") != 0){ // 월말
				Double prc = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC"));
				Double bm = conv.BigDecimal2Double(dbOutput.get(i).get("CLS_PRC_BM"));
				rList.add(prc/prePrc[0]-1);
				rBmList.add(bm/prePrc[1]-1);
				exr2.add(rList.get(rList.size()-1) - rBmList.get(rBmList.size()-1));
				prePrc[0] = prc;
				prePrc[1] = bm;
				dtList.add(sig);				
			}
		}		
		Integer idx = FindMinimum(dtList, exr2);
		jsonResult.put("maximum_drawdown", exr2.get(idx));	
		jsonResult.put("maximum_drawdown_dt", exr2.get(idx));
				
		return jsonResult;
	}
	
	
	public JSONObject GetTurnover(String simul_id, String u_cd, String u_cd_bm, String t0, String t1){
		JSONObject jsonResult = new JSONObject();
		
		/*
		 * 회전율 평균
		 */
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("simul_id", simul_id);
		param.put("u_cd", u_cd);
		param.put("u_cd_bm", u_cd_bm);
		param.put("t0", t0);
		param.put("t1", t1);
		
		JSONArray weightList = new JSONArray();  
		
		List<HashMap> dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectRebalDt", param);
		String[] revalDtList = new String[dbOutput.size()-1];
		String[] revalPreDtList = new String[dbOutput.size()-1];
		
		for(int i=0; i<dbOutput.size()-1; i++){
			revalDtList[i] = (String) dbOutput.get(i+1).get("START_DT");
			revalPreDtList[i] = (String) getSqlSession().selectOne("NpsSimulProfileQueryMapper.selectTrdDtPday", revalDtList[i]);		
		}
		
		Double turnOverSum = 0.0;
		JSONArray turnOverJsonList = new JSONArray(); 
		for(int i=0; i<revalDtList.length; i++){
			
			HashMap<String, Double> h1 = new HashMap<String, Double>();
			HashMap<String, Double> h2 = new HashMap<String, Double>();
			
			param.put("dt", revalPreDtList[i]);
			dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectIndexWeightClsPrc", param);
			for(int j=0; j<dbOutput.size(); j++){				
				String gicode = (String)dbOutput.get(j).get("GICODE");
				Double weight = conv.BigDecimal2Double(dbOutput.get(j).get("WEIGHT"));
				h1.put(gicode, weight);
			}
			
			param.put("dt", revalDtList[i]);
			dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectIndexWeightStrtPrc", param);
			for(int j=0; j<dbOutput.size(); j++){				
				String gicode = (String)dbOutput.get(j).get("GICODE");
				Double weight = conv.BigDecimal2Double(dbOutput.get(j).get("WEIGHT"));
				h2.put(gicode, weight);
			}
			JSONObject turnOverJson = new JSONObject();			
			Double turnOver = Turnover(h1, h2);
			turnOverJson.put("turnover", turnOver);
			turnOverJson.put("start_dt", revalDtList[i]);
			turnOverJsonList.put(turnOverJson);
			
			turnOverSum += turnOver;
		}
		
		jsonResult.put("turnover_average", turnOverSum/new Double(revalDtList.length));
		jsonResult.put("turnover_list", turnOverJsonList);
		return jsonResult;
	}

	/*
	 * 포트폴리오 유동화 소요일수 & 평균 종목수 & 복제율
	 */
	public JSONObject GetLiquidationAndCnt(String simul_id, String u_cd, String u_cd_bm, String t0, String t1, Double fundSize){
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("simul_id", simul_id);
		param.put("u_cd", u_cd);
		param.put("u_cd_bm", u_cd_bm);
		param.put("t0", t0);
		param.put("t1", t1);
		
		JSONObject result = new JSONObject();
		List<HashMap> dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectLastDayInYear", param);
		String[] lastDtList = new String[dbOutput.size()]; // 연말일자
		for(int i=0; i<dbOutput.size(); i++){lastDtList[i] = (String) dbOutput.get(i).get("MAX");} // 연말 일자
		
		Double cntAvg = 0.0; // 종목개수평균
		Double liquDayAvg = 0.0; //
		Double dupRateAvg = 0.0;
		Integer gicodeCnt = 0;
				
		gicodeCnt = 0;
		
		for(int i=0; i<lastDtList.length; i++){// 날짜 루프
			ArrayList<HashMap> port = GetPortfolioClsPrc(simul_id, u_cd, lastDtList[i]);
			Integer cnt = port.size();
			
			Double liquDayCnt = 0.0;
						
			HashMap<String, Double> portWeight = new HashMap<String, Double>(); // 포트폴리오 비중
			
			for(int j=0; j<port.size(); j++){// 종목 루프				
				String gicode = (String)port.get(j).get("GICODE");
				Double weight = (Double)port.get(j).get("WEIGHT");
				Double trdAmt = (Double)port.get(j).get("TRD_AMT");				
				Double sellAmount = fundSize * weight;
				Double time2sold = 0.0;
				if(trdAmt != null && trdAmt != 0.0){
					time2sold = sellAmount / (0.3 * trdAmt);
					liquDayCnt += time2sold;
					portWeight.put(gicode, weight);
					gicodeCnt++;
				}
			}
			liquDayAvg += liquDayCnt;
			cntAvg += new Double(port.size());
			
			
			// 복제율 추가------------------------------------			
			HashMap<String, Double> bmWeight = GetBmWeight(simul_id, u_cd_bm, lastDtList[i]);
			Double dupRate = DupRate(portWeight, bmWeight);		
			dupRateAvg += dupRate;
			// -----------------------------------------------
		}		
		
		result.put("liqu_day", liquDayAvg / new Double(gicodeCnt));
		result.put("avg_cnt", cntAvg / new Double(lastDtList.length));
		result.put("dup_rate", dupRateAvg / new Double(lastDtList.length));
		
		return result;
	}
	
	private ArrayList<HashMap> GetPortfolioClsPrc(String simul_id, String u_cd, String dt){
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("simul_id", simul_id);
		param.put("u_cd", u_cd);
		param.put("dt", dt);		
		List<HashMap> dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectIndexWeightClsPrc", param);
		
		ArrayList<HashMap> result = new ArrayList<HashMap>();
		
		for(int i=0; i<dbOutput.size(); i++){
			HashMap item = new HashMap<String, Object>();
			
			String gicode = (String)dbOutput.get(i).get("GICODE");
			String itemabbrnm = (String)dbOutput.get(i).get("ITEMABBRNM");
			Double weight = conv.BigDecimal2Double(dbOutput.get(i).get("WEIGHT"));
			Double trdAmt = conv.BigDecimal2Double(dbOutput.get(i).get("TRD_AMT"));
			
			item.put("GICODE", gicode);
			item.put("ITEMABBRNM", itemabbrnm);
			item.put("WEIGHT", weight);
			item.put("TRD_AMT", trdAmt);
			
			result.add(item);
		}
		
		return result;
	}
	
	// 벤치마크 지수 종목비중 반환
	private HashMap<String, Double> GetBmWeight(String simul_id, String u_cd, String dt){
		
		HashMap<String, Double> result = new HashMap<String, Double>();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("simul_id", simul_id);
		param.put("u_cd", u_cd);
		param.put("dt", dt);
		
		List<HashMap> dbOutput = getSqlSession().selectList("NpsSimulProfileQueryMapper.selectBmWeightClsPrc", param);
		for(int i=0; i<dbOutput.size(); i++){
			String gicode = (String)dbOutput.get(i).get("GICODE");
			Double weight = conv.BigDecimal2Double(dbOutput.get(i).get("WEIGHT"));
			result.put(gicode,  weight);			
		}		
		return result;
	}
	
	// 턴오버 구하는 함수
	private Double Turnover(HashMap<String, Double> h1, HashMap<String, Double> h2){
		// h1 : 앞에 포트폴리오 h2 : 뒤에 포트폴리오
		ArrayList<String> union = new ArrayList<String>();
		for(String key : h1.keySet())
			if(!union.contains(key)) union.add(key);
		for(String key : h2.keySet())
			if(!union.contains(key)) union.add(key);
		
		Double weightSum = 0.0;
		for(int i=0; i<union.size(); i++){
			String gicode = union.get(i);
			if(h1.containsKey(gicode) && h2.containsKey(gicode)){
				weightSum += Math.abs(h1.get(gicode) - h2.get(gicode));
			}
			else if(h1.containsKey(gicode)){
				// 편출
				weightSum += h1.get(gicode);
			}
			else if(h2.containsKey(gicode)){
				// 신규
				weightSum += h2.get(gicode);
			}			
		}
		return weightSum / 2.0;
	}
	
		// 복제율
		private Double DupRate(HashMap<String, Double> p1, HashMap<String, Double> p2){
			// h1 : 앞에 포트폴리오 h2 : 뒤에 포트폴리오
			ArrayList<String> union = new ArrayList<String>();
			for(String key : p1.keySet())
				if(!union.contains(key)) union.add(key);
			for(String key : p2.keySet())
				if(!union.contains(key)) union.add(key);
			
			Double weightSum = 0.0;
			for(int i=0; i<union.size(); i++){
				String gicode = union.get(i);
				if(p1.containsKey(gicode) && p2.containsKey(gicode)){
					weightSum += Math.min(p1.get(gicode), p2.get(gicode));
				}							
			}
			return weightSum;
		}
	
	private Double cov(Double[] r1, Double[] r2){
		Double result = 0.0;
		Double r1Avg = avg(r1);
		Double r2Avg = avg(r2);		
		int size = r1.length;
		try {
			if(r1.length != r2.length)
			throw new Exception();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0; i<r1.length; i++){
			result += (r1[i] - r1Avg)*(r2[i] - r2Avg);
		}
		return result/new Double(size - 1);
	}
	
	private Double corr(Double[] r1, Double[] r2){
		Double cov = cov(r1, r2);
		return cov / (std(r1) * std(r2));
	}
	
	private Double var(Double[] d){
		Double result = 0.0;
		Double avg = avg(d);
		for(int i=0; i<d.length; i++){
			result += (d[i] - avg) * (d[i] - avg); 
		}		
		return result / new Double(d.length - 1);
	}
	
	private Double avg(Double[] d){
		Double result = 0.0;
		for(int i=0; i<d.length; i++){
			result += d[i];
		}
		return result / new Double(d.length);
	}
	
	private Double std(Double[] d){
		Double avg = avg(d);
		Double result = 0.0;
		for(int i=0; i<d.length; i++){
			result += Math.pow(d[i] - avg, 2);
		}
		return Math.sqrt(result / new Double(d.length-1));
	}
	
	private Double gr(Double[] ret){
		Double result = 1.0;
		for(int i=0; i<ret.length; i++){
			result = result * (1+ret[i]);
		}
		return Math.pow(result, 250.0/new Double(ret.length)) - 1;
	}
	
	private Double TeNpS(Double[] er){
		
		if(er.length >= 250){ // 시계열 길이가 250 이상일 경우는 이동평균 TE를 반환
			
			Double[] temp = new Double[250];
			ArrayList teList = new ArrayList<Double>();
			Double teSum = 0.0;
			Integer cnt = 0;
			for(int i=249; i<er.length; i++){
				for(int j=0; j<250; j++){
					temp[j] = er[i-j];
				}
				teList.add(std(temp));
				teSum += std(temp);
				cnt++;
			}
					
			return Math.sqrt(250) * teSum / cnt.doubleValue();
		}
		else // 아닐경우는 단순 TE를 반환
		{
			return Math.sqrt(250) * std(er);
		}
	}
	
	Integer FindMinimum(ArrayList<String> dt, ArrayList<Double> er){
		HashMap<String, Object> result = new HashMap<String, Object>();
		Integer idx = 0;
		Double temp = er.get(0);
		for(int i=1; i<er.size(); i++){
			if(er.get(i) < temp){				
				idx = i;
				temp = er.get(i);
			}
		}
		return idx;
	}
	
	public String getMaxDayBofore(String dt){
		return (String)getSqlSession().selectOne("NpsSimulProfileQueryMapper.selectBeforeMaxDay", dt);
	}
	
	public String getMinDayAfter(String dt){
		return (String)getSqlSession().selectOne("NpsSimulProfileQueryMapper.selectAfterMinDay", dt);
	}
}

