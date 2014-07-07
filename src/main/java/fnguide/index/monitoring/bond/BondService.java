package fnguide.index.monitoring.bond;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fnguide.index.monitoring.utility.Ut;

@Service
public class BondService {
	
	public enum IndexType{
			KTB,
			CASH
	};
	
	@Autowired
	private BondDaoImpl dao;
	
	// 채권지수 구성정보 반환
	public List<HashMap<String, Object>> GetBndIdxItemList(IndexType type, String trd_dt){
		String idx_cd = null;
		if(type == IndexType.CASH){
			idx_cd = "FBI.KRW.01";
		}
		else if(type == IndexType.KTB){
			idx_cd = "FBI.KTB.01";
		}
		
		List<HashMap<String, Object>> outItemHist =  dao.getBndIdxItem(trd_dt, idx_cd);
		
		// dt 부다 일주일 내에 있으면 체크 컬럼명 '경고'
		Calendar cal = new GregorianCalendar();
		cal.setTime(Ut.str2date(trd_dt));
		cal.add(Calendar.DATE, 7);
		Date t1_upper = cal.getTime();
		
		for(int i=0; i<outItemHist.size(); i++){
			
			if(((String)outItemHist.get(i).get("최근이자지급일")).compareTo("할인채") != 0){
				Date exp_dt = Ut.str2date((String)outItemHist.get(i).get("만기일"));
				Date int_dt = Ut.str2date((String)outItemHist.get(i).get("최근이자지급일"));
				
				if(exp_dt.before(t1_upper) || int_dt.before(t1_upper)) {
					outItemHist.get(i).put("경고", "경고");
				}
				else{
					outItemHist.get(i).put("경고", "없음");
				}
			}
			else{
				outItemHist.get(i).put("경고", "없음");
			}
		}				
				
		return outItemHist;
	}
	
	// 채권 시계열 정보 반환
	public List<HashMap<String, Object>> GetBndIdxTimeSeries(String idx_cd, String trd_dt, Integer interval){
		return dao.getBndIdxTimeSeries(trd_dt, interval, idx_cd);
	}
	
	
}
