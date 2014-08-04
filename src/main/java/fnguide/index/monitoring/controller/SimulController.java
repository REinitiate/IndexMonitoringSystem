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
import fnguide.index.monitoring.stock.StockConstService;
import fnguide.index.monitoring.stock.StockProfileService;
import fnguide.index.monitoring.stock.StockConstService.PriceType;
import fnguide.index.monitoring.stock.StockConstService.UnivType;
import fnguide.index.monitoring.stock.StockProfileService.IntervalType;
import fnguide.index.monitoring.stock.StockService;
import fnguide.index.monitoring.utility.Converter;
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
	
	@RequestMapping(value = "/simul/profile", method={RequestMethod.GET, RequestMethod.POST})	
	public String GetIndexProfile(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_simul/profile.jsp");
		model.addAttribute("service", "/simul/profile");
		return "template";
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
