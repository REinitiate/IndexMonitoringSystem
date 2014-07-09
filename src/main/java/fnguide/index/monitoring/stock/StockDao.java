package fnguide.index.monitoring.stock;

import java.util.HashMap;
import java.util.List;

public interface StockDao {
	// 지수 리스트
	public List<HashMap<String, String>> selectUcdList();
}
