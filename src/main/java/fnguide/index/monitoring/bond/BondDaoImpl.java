package fnguide.index.monitoring.bond;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class BondDaoImpl extends SqlSessionDaoSupport implements BondDao {

	@Override
	public List<HashMap<String, Object>> getBndIdxItem(String trd_dt,
			String idx_cd) {
		HashMap<String, String> input = new HashMap<String, String>();		
		input.put("idx_cd", idx_cd);
		input.put("trd_dt", trd_dt);
		List<HashMap<String, Object>> outItemHist = getSqlSession().selectList("BondQueryMapper.selectBndItemHist" , input);
		return outItemHist;
	}

	@Override
	public List<HashMap<String, Object>> getBndIdxTimeSeries(String trd_dt,
			Integer interval, String idx_cd) {		
		HashMap input = new HashMap<String, String>();		
		input.put("trd_dt", trd_dt);
		input.put("idx_cd", idx_cd);
		input.put("interval", interval);
		List<HashMap<String, Object>> outIdxDl = getSqlSession().selectList("BondQueryMapper.selectBndIdxTimeSeries" , input);
		return outIdxDl;
	}	
}
