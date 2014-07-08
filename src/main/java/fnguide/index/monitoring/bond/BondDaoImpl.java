package fnguide.index.monitoring.bond;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BondDaoImpl implements BondDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public List<HashMap<String, Object>> getBndIdxItem(String trd_dt,
			String idx_cd) {
		HashMap<String, String> input = new HashMap<String, String>();		
		input.put("idx_cd", idx_cd);		
		BondMapper bondmapper = sqlSession.getMapper(BondMapper.class);		
		List<HashMap<String, Object>> outItemHist = bondmapper.selectBndItemHist(trd_dt, idx_cd);  
		return outItemHist;
	}

	@Override
	public List<HashMap<String, Object>> getBndIdxTimeSeries(String trd_dt, Integer interval, String idx_cd) {	
		
		HashMap<String, String> input = new HashMap<String, String>();		
		input.put("idx_cd", idx_cd);		
		BondMapper bondmapper = sqlSession.getMapper(BondMapper.class);		
		List<HashMap<String, Object>> outIdxDl = bondmapper.selectBndIdxTimeSeries(trd_dt, idx_cd, interval);  
		return outIdxDl;
	}	
}
