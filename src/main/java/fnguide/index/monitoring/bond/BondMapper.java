package fnguide.index.monitoring.bond;

import java.util.HashMap;
import java.util.List;

public interface BondMapper {
	public List<HashMap<String, Object>> selectBndItemHist(String trd_dt, String idx_cd);
	public List<HashMap<String, Object>> selectBndIdxTimeSeries(String trd_dt, String idx_cd, Integer interval);
}
