package fnguide.index.monitoring.dashboard;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Service;

@Service
public class DashBoardService extends SqlSessionDaoSupport {
	
	// 종목 코드 리스트를 String 배열로 반환
	public List<HashMap<String, Object>> GetEventItem(String dt){
		String t1 = dt;
		String t0 = (String) getSqlSession().selectOne("DashBoardQueryMapper.selectTrdDtPday", dt);
		
		List<HashMap<String, Object>> dbOutput = null;
		HashMap<String, String> input = new HashMap<String, String>();
		input.put("t0", t0);
		input.put("t1", t1);		
		dbOutput = getSqlSession().selectList("DashBoardQueryMapper.selectEventItem", input);		
		return dbOutput;
	}
	
}