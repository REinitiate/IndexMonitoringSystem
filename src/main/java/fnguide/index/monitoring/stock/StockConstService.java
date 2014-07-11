package fnguide.index.monitoring.stock;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockConstService extends SqlSessionDaoSupport {
	
	public enum PriceType{
		CLS_PRC,
		STD_PRC
	}
	
	public List<HashMap<String, Object>> GetConstitutionInfo(String u_cd, String dt_univ, String dt_prc, PriceType prcType) {
		
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("dt_prc", dt_prc);
		input.put("dt_univ", dt_univ);
		
		List<HashMap<String, Object>> result = null;
		if(prcType == PriceType.CLS_PRC)
			result = format(getSqlSession().selectList("StockQueryMapper.selectConstFromFniMfiClsPrc", input));
		else if(prcType == PriceType.STD_PRC)
			result = format(getSqlSession().selectList("StockQueryMapper.selectConstFromFniMfiStdPrc", input));
		return result;
	}
	
	public String GetConstitutionInfo2Json(String u_cd, String dt_univ, String dt_prc, PriceType prcType) {
		
		List<HashMap<String, Object>> dbOutput = GetConstitutionInfo(u_cd, dt_univ, dt_prc, prcType);
		
		for(int i=0; i<dbOutput.size(); i++){			
		}
		
		JSONObject obj = new JSONObject();		
		JSONArray objArray = new JSONArray();		
		for(int i=0; i<dbOutput.size(); i++){
			objArray.put(i, dbOutput.get(i));
		}
		obj.put("종목", objArray);
		
		String u_nm = (String) getSqlSession().selectOne("StockQueryMapper.selectUnmWithUcd", u_cd);
		JSONObject obj2 = new JSONObject();
		obj2.put("지수코드", u_cd);
		obj2.put("지수이름", u_nm);
		obj2.put("종목개수", dbOutput.size());
		obj2.put("유니브기준일자", dt_univ);
		obj2.put("주가기준일자", dt_prc);
		if(prcType == PriceType.CLS_PRC)
			obj2.put("주가구분", "종가");
		else if(prcType == PriceType.STD_PRC)
			obj2.put("주가구분", "기준가");		
		
		obj.put("정보", obj2);
		return obj.toString();		
	}
	
	private List<HashMap<String, Object>> format(List<HashMap<String, Object>> input){
		
		DecimalFormat df = new DecimalFormat("#,###");
		DecimalFormat df2 = new DecimalFormat("0.00%");
		
		for(int i=0; i<input.size(); i++){			
			input.get(i).put("지수채용주식수", df.format(input.get(i).get("지수채용주식수")));
			input.get(i).put("지수채용시가총액", df.format(input.get(i).get("지수채용시가총액")));
			input.get(i).put("주가", df.format(input.get(i).get("주가")));		
			input.get(i).put("비중", df2.format(input.get(i).get("비중")));
		}
		
		return input;	
	}
}
