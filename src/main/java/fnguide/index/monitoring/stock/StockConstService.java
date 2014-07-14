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
	
	public enum UnivType{
		FNI_MFI_U_MAP_HIST,
		FNI_STYLE_UNIV
	}
	
	public List<HashMap<String, Object>> GetConstitutionInfo(String u_cd, String dt_univ, String dt_prc, String dt_stk, PriceType prcType, UnivType univType) {
		
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("u_cd", u_cd);
		input.put("dt_prc", dt_prc);
		input.put("dt_univ", dt_univ);
		input.put("dt_stk", dt_stk);
		
		List<HashMap<String, Object>> result = null;
		if(prcType == PriceType.CLS_PRC){
			if(univType == univType.FNI_MFI_U_MAP_HIST)
				result = format(getSqlSession().selectList("StockQueryMapper.selectConstFromFniMfiClsPrc", input));
			else if(univType == univType.FNI_STYLE_UNIV)
				result = format(getSqlSession().selectList("StockQueryMapper.selectConstFromFniStyleClsPrc", input));
		}	
		else if(prcType == PriceType.STD_PRC){
			if(univType == univType.FNI_MFI_U_MAP_HIST)
				result = format(getSqlSession().selectList("StockQueryMapper.selectConstFromFniMfiStdPrc", input));
			else if(univType == univType.FNI_STYLE_UNIV)
				result = format(getSqlSession().selectList("StockQueryMapper.selectConstFromFniStyleStdPrc", input));
		}	

		return result;
	}
	
	public String GetConstitutionInfo2Json(String u_cd, String dt_univ, String dt_prc, String dt_stk, PriceType prcType, UnivType univType) {
		
		List<HashMap<String, Object>> dbOutput = GetConstitutionInfo(u_cd, dt_univ, dt_prc, dt_stk, prcType, univType);
		
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
		
		if(univType == UnivType.FNI_MFI_U_MAP_HIST)		
			obj.put("헤더", "<thead><th>종목코드</th><th>종목이름</th><th>상장주식수</th><th>예정주식수</th><th>유동비율</th><th>지수채용주식수</th><th>지수채용시총</th><th>주가</th><th>비중</th></thead>");
		else
			obj.put("헤더", "<thead><th>종목코드</th><th>종목이름</th><th>상장주식수</th><th>예정주식수</th><th>유동비율</th><th>IIF</th><th>지수채용주식수</th><th>지수채용시총</th><th>주가</th><th>비중</th></thead>");
		
		return obj.toString();		
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
