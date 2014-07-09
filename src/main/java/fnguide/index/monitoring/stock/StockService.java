package fnguide.index.monitoring.stock;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Service;

@Service
public class StockService extends SqlSessionDaoSupport implements StockDao {

	@Override
	public List<HashMap<String, String>> selectUcdList() {
		List<HashMap<String, String>> result = null;
		HashMap<String, String> input = new HashMap<String, String>();
		result = getSqlSession().selectList("StockQueryMapper.selectUcdList", input);		
		return result;
	}
	
	
	// 종목 코드 리스트를 String 배열로 반환
	public String GetUcdListByJson(String type){
		List<HashMap<String, String>> dbOutput = null;
		HashMap<String, String> input = new HashMap<String, String>();
		dbOutput = getSqlSession().selectList("StockQueryMapper.selectUcdList", input);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\"source\" : [");
		for(int i=0; i<dbOutput.size(); i++){		
			sb.append("\"");
			
			if(type.compareTo("nm") == 0){
				sb.append(dbOutput.get(i).get("종목이름") + "(" + dbOutput.get(i).get("종목코드") + ")");
			}
			else if(type.compareTo("cd") == 0){
				sb.append(dbOutput.get(i).get("종목코드") + "(" + dbOutput.get(i).get("종목이름") + ")");
			}
			
			if(i == dbOutput.size()-1)
				sb.append("\"");
			else
				sb.append("\",");
		}
		sb.append("]}");		
		return sb.toString();
	}

	
}
