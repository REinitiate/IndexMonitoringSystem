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

import fnguide.index.monitoring.stock.StockConstService;
import fnguide.index.monitoring.stock.StockProfileService;
import fnguide.index.monitoring.stock.StockConstService.PriceType;
import fnguide.index.monitoring.stock.StockConstService.UnivType;
import fnguide.index.monitoring.stock.StockProfileService.IntervalType;
import fnguide.index.monitoring.stock.StockService;
import fnguide.index.monitoring.utility.Converter;
import fnguide.index.monitoring.utility.Ut;

@Controller
public class StockController {
	private static final Logger logger = LoggerFactory.getLogger(StockController.class);
	
	@Autowired
	public StockService stockService;
	
	@Autowired
	public StockConstService stockConstService;
	
	@Autowired
	public StockProfileService stockProfileService;
	
	@RequestMapping(value = "/stock/constitution", method={RequestMethod.GET, RequestMethod.POST})	
	public String GetIndexConstitution(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {
				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_stock/constitution.jsp");
		model.addAttribute("service", "/stock/constitution");		
		return "template";
	}
	
	@RequestMapping(value = "/stock/constitution/json", method=RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String GetConstitutionInfo(@RequestParam(required=false) String u_cd, String dt_univ, String dt_prc, String dt_stk, String prc_type, String univ_type, HttpServletRequest req, Model model) {		
		
		String result = null;
		if(prc_type.compareTo("cls_prc") == 0){
			if(univ_type.compareTo("FNI_STYLE_UNIV") == 0)
				result = stockConstService.GetConstitutionInfo2Json(u_cd, dt_univ, dt_prc, dt_stk, PriceType.CLS_PRC, UnivType.FNI_STYLE_UNIV);
			else if(univ_type.compareTo("FNI_MFI_U_MAP_HIST") == 0)
				result = stockConstService.GetConstitutionInfo2Json(u_cd, dt_univ, dt_prc, dt_stk, PriceType.CLS_PRC, UnivType.FNI_MFI_U_MAP_HIST);
		}
		else if(prc_type.compareTo("std_prc") == 0){
			if(univ_type.compareTo("FNI_STYLE_UNIV") == 0)
				result = stockConstService.GetConstitutionInfo2Json(u_cd, dt_univ, dt_prc, dt_stk, PriceType.STD_PRC, UnivType.FNI_STYLE_UNIV);
			else if(univ_type.compareTo("FNI_MFI_U_MAP_HIST") == 0)
				result = stockConstService.GetConstitutionInfo2Json(u_cd, dt_univ, dt_prc, dt_stk, PriceType.STD_PRC, UnivType.FNI_MFI_U_MAP_HIST);
		}
		else{
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	@RequestMapping(value = "/stock/ucdlist", method=RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String GetUcdList(HttpServletRequest req, Model model) {		
		String uCdList = stockService.GetUcdListByJson("cd");
		return uCdList;
	}
	
	@RequestMapping(value = "/stock/unmlist", method=RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String GetUnMList(HttpServletRequest req, Model model) {		
		String uCdList = stockService.GetUcdListByJson("nm");
		return uCdList;
	}
	
	@RequestMapping(value = "/stock/profile", method={RequestMethod.GET, RequestMethod.POST})	
	public String GetIndexProfile(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_stock/profile.jsp");
		model.addAttribute("service", "/stock/profile");
		return "template";
	}
	
	@RequestMapping(value = "/stock/profile/json", method={RequestMethod.GET, RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody	
	public String GetIndexProfileJson(@RequestParam(required=false) String u_cd, String bm, String t0, String t1, HttpServletRequest req, Model model) {		
		
		String result = null;
		
		String dt_w1, dt_m1, dt_m3, dt_m6, dt_y1, dt_y3, dt_y5;
		
		dt_w1 = stockProfileService.GetAvailableDt(t1, 7, IntervalType.Days);
		dt_m1 = stockProfileService.GetAvailableDt(t1, 1, IntervalType.Months);
		dt_m3 = stockProfileService.GetAvailableDt(t1, 3, IntervalType.Months);
		dt_m6 = stockProfileService.GetAvailableDt(t1, 6, IntervalType.Months);
		dt_y1 = stockProfileService.GetAvailableDt(t1, 12, IntervalType.Months);
		dt_y3 = stockProfileService.GetAvailableDt(t1, 36, IntervalType.Months);
		dt_y5 = stockProfileService.GetAvailableDt(t1, 60, IntervalType.Months);
		
		// 지수 유효 시계열 검색 start_dt, end_dt 검색
		
		return "test";
	}
	
	
}
