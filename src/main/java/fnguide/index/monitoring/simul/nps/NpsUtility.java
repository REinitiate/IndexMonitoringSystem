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
public class NpsUtility extends SqlSessionDaoSupport {
	
	@Autowired
	StockCommonService stockCommonService;
	
	public List<HashMap> GetDateList(String t0, String t1){
		HashMap param = new HashMap<String, Object>();
		param.put("t0", t0);
		param.put("t1", t1);
		return getSqlSession().selectList("NpsSimulProfileQueryMapper.selectLastDayInYear", param);		
	}
}


