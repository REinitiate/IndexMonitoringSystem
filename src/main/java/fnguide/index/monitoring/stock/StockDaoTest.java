package fnguide.index.monitoring.stock;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fnguide.index.monitoring.stock.StockConstService.PriceType;
import fnguide.index.monitoring.stock.StockConstService.UnivType;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"file:src/main/webapp/WEB-INF/spring/root-context.xml"
		})
@Transactional
public class StockDaoTest {
	
	@Autowired
	StockConstService stockConstService;
	
	@Test
	public void test(){
		
		assertNotNull(stockConstService);
		//List<HashMap<String, Object>> result = stockConstService.GetConstitutionInfo("FI00.WLT.LVL", "20140710", "20140710", "20140710", PriceType.STD_PRC, UnivType.FNI_MFI_U_MAP_HIST);		
		//assertEquals(result.size(), 500);
	}
}
