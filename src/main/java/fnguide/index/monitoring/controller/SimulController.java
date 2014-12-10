package fnguide.index.monitoring.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.print.DocFlavor.INPUT_STREAM;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.junit.runner.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fnguide.index.monitoring.simul.SimulProfileService;
import fnguide.index.monitoring.simul.nps.NpsStatGenerator;
import fnguide.index.monitoring.simul.nps.NpsUtility;
import fnguide.index.monitoring.stock.StockConstService;
import fnguide.index.monitoring.stock.StockProfileService;
import fnguide.index.monitoring.stock.StockConstService.PriceType;
import fnguide.index.monitoring.stock.StockConstService.UnivType;
import fnguide.index.monitoring.stock.StockProfileService.IntervalType;
import fnguide.index.monitoring.stock.StockService;
import fnguide.index.monitoring.utility.conv;
import fnguide.index.monitoring.utility.Ut;

@Controller
public class SimulController {
	private static final Logger logger = LoggerFactory.getLogger(StockController.class);
	
	@Autowired
	public StockService stockService;
	
	@Autowired
	public StockConstService stockConstService;
	
	@Autowired
	public SimulProfileService simulProfileService;
	
	@Autowired
	public NpsStatGenerator npsStatGenerator;
	
	@Autowired
	public NpsUtility npsUtility;
	
	@RequestMapping(value = "/simul/profile", method={RequestMethod.GET, RequestMethod.POST})	
	public String GetIndexProfile(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_simul/profile.jsp");
		model.addAttribute("service", "/simul/profile");
		return "template";
	}
	
	@RequestMapping(value = "/nps/profile", method={RequestMethod.GET, RequestMethod.POST})	
	public String NpsIndexProfile(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {				
		
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_simul/nps_profile.jsp");
		model.addAttribute("service", "/nps/profile");
		
		return "template";
	}
	
	@RequestMapping(value = "nps/simul/profile/json", method={RequestMethod.GET, RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody	
	public String GetNpsProfile(
			@RequestParam(required=false, defaultValue="NPSD9") String simul_id,
			@RequestParam(required=false, defaultValue="UNIV.SCR")String u_cd,
			@RequestParam(required=false, defaultValue="NPS.BM")String u_cd_bm,
			@RequestParam(required=false, defaultValue="20010102")String t0, 
			@RequestParam(required=false, defaultValue="20141031")String t1,
			HttpServletRequest req,
			Model model) {
		
		// 날짜 처리
		t0 = npsStatGenerator.getMinDayAfter(t0);
		t1 = npsStatGenerator.getMaxDayBofore(t1);
		
		List<HashMap> dateList = npsUtility.GetDateList(t0, t1);				
		
		JSONObject result = new JSONObject();				
		JSONObject stats = npsStatGenerator.GetStats(simul_id, u_cd, u_cd_bm, t0, t1);
		JSONObject liq = npsStatGenerator.GetLiquidationAndCnt(simul_id, u_cd, u_cd_bm, t0, t1, 50000.0);
		JSONObject turnover = npsStatGenerator.GetTurnover(simul_id, u_cd, u_cd_bm, t0, t1);					
		result.put("stats", stats);
		result.put("turnover", turnover);
		result.put("liq", liq);		
		result.put("simul_id", simul_id);
		
		JSONObject header = new JSONObject();	
		header.put("t0", t0);
		header.put("t1", t1);
		header.put("u_cd", u_cd);
		header.put("simul_id", simul_id);
		result.put("header", header);
		
		return result.toString();
	}
	
	@RequestMapping(value = "/simul/profile/json", method={RequestMethod.GET, RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody	
	public String GetIndexProfileJson(@RequestParam(required=false) String simul_id, String u_cd, String u_cd_bm, String t0, String t1, HttpServletRequest req, Model model) {		
		
		JSONObject result = new JSONObject();
		
		JSONObject jsonInfo = simulProfileService.GetInfoJson(simul_id, u_cd, u_cd_bm, t0, t1);
		JSONObject jsonReturnProfile = simulProfileService.GetReturnProfileJson(simul_id, u_cd, u_cd_bm, t0, t1);
		JSONObject jsonRiskProfile = simulProfileService.GetRiskProfileJson(simul_id, u_cd, u_cd_bm, t0, t1);		
		
		result.put("리턴프로파일", jsonReturnProfile);
		result.put("리스크프로파일", jsonRiskProfile);				
		result.put("정보", jsonInfo);
		
		return result.toString();
	}
}
