package fnguide.index.monitoring.bond;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fnguide.index.monitoring.utility.Converter;
import fnguide.index.monitoring.utility.Ut;

@Service
public class BondService {
	
	/**
	 * 기능 : 테스트 
	 * @param args
	 */
	public static void main(String args[]){
		
	}
	
	public enum IndexName{
			KTB,
			CASH
	};
	
	public enum IndexType{
		PRICE,
		TOTAL_RETURN
	};
	
	@Autowired
	private BondDaoImpl dao;
	
	// 채권지수 구성정보 반환
	public List<HashMap<String, Object>> GetBndIdxItemList(IndexName type, String trd_dt){
				
		DecimalFormat df = new DecimalFormat("0.00");
		
		String idx_cd = null;
		if(type == IndexName.CASH){
			idx_cd = "FBI.KRW.01";
		}
		else if(type == IndexName.KTB){
			idx_cd = "FBI.KTB.01";
		}
		
		List<HashMap<String, Object>> outItemHist =  dao.getBndIdxItem(trd_dt, idx_cd);
		
		// dt 부다 일주일 내에 있으면 체크 컬럼명 '경고'
		Calendar cal = new GregorianCalendar();
		cal.setTime(Ut.str2date(trd_dt));
		cal.add(Calendar.DATE, 7);
		Date t1_upper = cal.getTime();
		
		for(int i=0; i<outItemHist.size(); i++){
	
			Date exp_dt = Ut.str2date((String)outItemHist.get(i).get("만기일"));
			if(exp_dt.before(t1_upper)) {
				outItemHist.get(i).put("만기경고", "경고");
			}
			else{
				outItemHist.get(i).put("만기경고", "없음");
			}
				
			if(((String)outItemHist.get(i).get("최근이자지급일")).compareTo("할인채") != 0){				
				
				Date int_dt = Ut.str2date((String)outItemHist.get(i).get("최근이자지급일"));
				if(int_dt.before(t1_upper)) {
					outItemHist.get(i).put("이자경고", "경고");
				}
				else{
					outItemHist.get(i).put("이자경고", "없음");
				}
				
			}
			else{
				outItemHist.get(i).put("경고", "없음");
			}
			outItemHist.get(i).put("평가가격", df.format(outItemHist.get(i).get("평가가격")));
			outItemHist.get(i).put("NAV평가가격", df.format(outItemHist.get(i).get("NAV평가가격")));
		}
		
		for(int i=0; i<outItemHist.size(); i++){
			if(((String)outItemHist.get(i).get("최근이자지급일")).compareTo("할인채") != 0){
				outItemHist.get(i).put("최근이자지급일", Converter.Date2Date((String)outItemHist.get(i).get("최근이자지급일"), "/"));
			}
			outItemHist.get(i).put("만기일", Converter.Date2Date((String)outItemHist.get(i).get("만기일"), "/"));
		}
		
		return outItemHist;
	}
	
	// 채권 시계열 정보 반환
	public List<HashMap<String, Object>> GetBndIdxTimeSeries(IndexName name, IndexType type, String trd_dt, Integer interval){
		
		String idx_cd = null;
		if(name == IndexName.CASH){
			if(type == IndexType.PRICE)
				idx_cd = "FBI.KRW.01.1";
			else if(type == IndexType.TOTAL_RETURN)
				idx_cd = "FBI.KRW.01.2";
		}
		else if(name == IndexName.KTB){
			if(type == IndexType.PRICE)
				idx_cd = "FBI.KTB.01.1";
			else if(type == IndexType.TOTAL_RETURN)
				idx_cd = "FBI.KTB.01.2";
		}
		
		return dao.getBndIdxTimeSeries(trd_dt, interval, idx_cd);
	}
	
	// 채권지수 시계열 가지고 온다.
	public String GetBndTimeSeriesJsonString(IndexName name, IndexType type, String trd_dt, Integer interval){ 
		
		List<HashMap<String, Object>> outIdxDl = GetBndIdxTimeSeries(name, type, trd_dt, interval);
		
		// 여기에 배열을 담음..
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0; i<outIdxDl.size(); i++){
			sb.append("[");
			sb.append(Converter.GetUtc(outIdxDl.get(i).get("TRD_DT").toString()));			
			sb.append(",");			
			sb.append(outIdxDl.get(i).get("CLS_PRC").toString());
			sb.append("]");
			if(i != outIdxDl.size()-1)
				sb.append(",");
		}
		sb.append("]");		
		return sb.toString();
	}
	
	
}
