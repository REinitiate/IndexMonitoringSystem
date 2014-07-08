package fnguide.index.monitoring.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.junit.runner.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fnguide.index.monitoring.bond.BondService;
import fnguide.index.monitoring.utility.Converter;
import fnguide.index.monitoring.utility.Ut;

@Controller
public class BondController {
	private static final Logger logger = LoggerFactory.getLogger(BondController.class);
	
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	private BondService bondService;
	
	@RequestMapping(value = "/bond/item", method=RequestMethod.POST)	
	public String BondItem(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {
		
		logger.info("root 경로로 접속했을 시!");
		
		String code = cd; // 국고채인지, KRW CASH 인지 구분! cash or ktb
		
		if(dt == null){
			dt = (String)req.getSession().getAttribute("dt");
			if(dt == null){ // 세션에 없을 시
				String today = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
				dt = today;
				model.addAttribute("dt", today);
				req.getSession().setAttribute("dt", today);
			}
			else{
				model.addAttribute("dt", dt);
				req.getSession().setAttribute("dt", dt);
			}
		}
		else{
			model.addAttribute("dt", dt);
			req.getSession().setAttribute("dt", dt);
		}
		
		HashMap<String, String> input = new HashMap<String, String>();		
		input.put("trd_dt", dt);		
		if(code.equals("ktb")){
			input.put("idx_cd", "FBI.KTB.01");
		}
		else {
			input.put("idx_cd", "FBI.KRW.01");
		}
		
		//List<HashMap<String, Object>> outItemHist = sqlSession.selectList("BondQueryMapper.selectBndItemHist" , input);
		List<HashMap<String, Object>> outItemHist = null;
		
		if(code.equals("ktb"))
			outItemHist = bondService.GetBndIdxItemList(BondService.IndexType.CASH, dt, 3, 3);
		else if(code.equals("cash"))
			outItemHist = bondService.GetBndIdxItemList(BondService.IndexType.KTB, dt, 3, 3);
		
		
		HashMap input2 = new HashMap();		
		input2.put("trd_dt", dt);		
		if(code.equals("ktb")){
			input2.put("idx_cd", "FBI.KTB.01.1");			
		}
		else {
			input2.put("idx_cd", "FBI.KRW.01.1");
		}
		input2.put("interval", -12);
		
		List<HashMap<String, Object>> outIdxDl = bondService.GetBndIdxTimeSeries("FBI.KTB.01.1", dt, -12);
		
				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_bond/bonditem.jsp");
		model.addAttribute("service", "/bond/item");		
		model.addAttribute("outItemHist", outItemHist);
				
		//model.addAttribute("timeseries1", GetBndTimeSeriesString(dt, "FBI.KRW.01.1"));
		//model.addAttribute("timeseries2", GetBndTimeSeriesString(dt, "FBI.KRW.01.2"));
		
		if(code.equals("ktb")){
			model.addAttribute("idx_nm", "국고채 지수");
		}
		else if(code.equals("cash")){
			model.addAttribute("idx_nm", "KRW CASH");
		}
				
		return "template";
	}
}
