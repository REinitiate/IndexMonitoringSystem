package fnguide.index.monitoring.stock;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
		//List<HashMap<String, Object>> result = stockConstService.GetConstitutionInfo("FI00.WLT.LVL", "20140710", "20140710", StockConstService.PriceType.STD_PRC);		
		//assertEquals(result.size(), 500);
	}
}
