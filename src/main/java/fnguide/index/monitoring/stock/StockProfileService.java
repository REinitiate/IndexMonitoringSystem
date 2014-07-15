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
public class StockProfileService extends SqlSessionDaoSupport {
	
	public enum IntervalType
	{
		Days,
		Months
	}
	
	public String GetAvailableDt(String dt, Integer interval, IntervalType type){
		
		List<HashMap<String, String>> dbOutput = null;
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("dt", dt);
		input.put("interval", interval);
		
		if(type == IntervalType.Days)
			dbOutput = getSqlSession().selectList("StockProfileQueryMapper.selectPastAvailableDateByDays", input);
		else if(type == IntervalType.Months)
			dbOutput = getSqlSession().selectList("StockProfileQueryMapper.selectPastAvailableDateByMonths", input);
		
		return dbOutput.get(0).get("TRD_DT");
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
