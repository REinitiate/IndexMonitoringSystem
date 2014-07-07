package fnguide.index.monitoring.bond;

import java.util.HashMap;
import java.util.List;

public interface BondDao {
	
	// 채권 구성정보 로딩
	public List<HashMap<String, Object>> getBndIdxItem(String trd_dt, String idx_cd);
	
	// 채권 지수 시계열 로딩
	public List<HashMap<String, Object>> getBndIdxTimeSeries(String trd_dt, Integer interval, String idx_cd);
	
}
