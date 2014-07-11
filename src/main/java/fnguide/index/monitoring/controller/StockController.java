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
import fnguide.index.monitoring.stock.StockConstService.PriceType;
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
	
	@RequestMapping(value = "/stock/constitution", method={RequestMethod.GET, RequestMethod.POST})	
	public String BondItem(@RequestParam(required=false) String dt, String cd, HttpServletRequest req, Model model) {
				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_stock/constitution.jsp");
		model.addAttribute("service", "/stock/constitution");
		model.addAttribute("ucdList", GetUcdList(req, model));
		return "template";
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
	
	@RequestMapping(value = "/stock/constitution/json", method=RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String GetConstitutionInfo(@RequestParam(required=false) String u_cd, String dt_univ, String dt_prc, String prc_type, HttpServletRequest req, Model model) {		
		
		String result = null;
		if(prc_type.compareTo("cls_prc") == 0){
			result = stockConstService.GetConstitutionInfo2Json(u_cd, dt_univ, dt_prc, PriceType.CLS_PRC);
		}
		else if(prc_type.compareTo("std_prc") == 0){
			result = stockConstService.GetConstitutionInfo2Json(u_cd, dt_univ, dt_prc, PriceType.STD_PRC);
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
}
