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
		List<HashMap<String, Object>> outItemHist = sqlSession.selectList("BondQueryMapper.selectBndItemHist" , input);
		
		
		HashMap input2 = new HashMap();		
		input2.put("trd_dt", dt);		
		if(code.equals("ktb")){
			input2.put("idx_cd", "FBI.KTB.01.1");			
		}
		else {
			input2.put("idx_cd", "FBI.KRW.01.1");
		}
		input2.put("interval", -12);
		
		List<HashMap<String, Object>> outIdxDl = sqlSession.selectList("BondQueryMapper.selectBndIdxTimeSeries" , input2);
		ArrayList<String[]> outIdxDl2 = new ArrayList<String[]>();  
		
		for(int i=0; i<outIdxDl.size(); i++){
			String[] item = new String[2];
			item[0] = (String)outIdxDl.get(i).get("TRD_DT");
			//item[1] = outIdxDl.get(i).get("CLS_PRC");
			outIdxDl2.add(item);
		}
		
		// dt 부다 일주일 내에 있으면 체크 컬럼명 '경고'
		Calendar cal = new GregorianCalendar();
		cal.setTime(Ut.str2date(dt));
		cal.add(Calendar.DATE, 7);
		Date t1_upper = cal.getTime();
		
		for(int i=0; i<outItemHist.size(); i++){
			
			if(((String)outItemHist.get(i).get("최근이자지급일")).compareTo("할인채") != 0){
				Date exp_dt = Ut.str2date((String)outItemHist.get(i).get("만기일"));
				Date int_dt = Ut.str2date((String)outItemHist.get(i).get("최근이자지급일"));
				
				if(exp_dt.before(t1_upper) || int_dt.before(t1_upper)) {
					outItemHist.get(i).put("경고", "경고");
				}
				else{
					outItemHist.get(i).put("경고", "없음");
				}
			}
			else{
				outItemHist.get(i).put("경고", "없음");
			}
		}
				
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents_bond/bonditem.jsp");
		model.addAttribute("service", "/bond/item");		
		model.addAttribute("outItemHist", outItemHist);
				
		model.addAttribute("timeseries1", GetBndTimeSeriesString(dt, "FBI.KRW.01.1"));
		model.addAttribute("timeseries2", GetBndTimeSeriesString(dt, "FBI.KRW.01.2"));
		
		if(code.equals("ktb")){
			model.addAttribute("idx_nm", "국고채 지수");
		}
		else if(code.equals("cash")){
			model.addAttribute("idx_nm", "KRW CASH");
		}
				
		return "template";
	}
	
	// 채권지수 시계열 가지고 온다.
	public String GetBndTimeSeriesString(String dt, String cd) {		
			
		HashMap input = new HashMap();		
		input.put("trd_dt", dt);
		input.put("idx_cd", cd); // 일단 처리
		input.put("interval", -12);
		List<HashMap<String, Object>> outIdxDl = sqlSession.selectList("BondQueryMapper.selectBndIdxTimeSeries" , input);
		
		// 여기에 배열을 담음..
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0; i<outIdxDl.size(); i++){
			sb.append("[");
			sb.append(Converter.GetUtc(outIdxDl.get(i).get("TRD_DT").toString()));			
			sb.append(",");			
			sb.append(outIdxDl.get(i).get("CLS_PRC").toString());
			sb.append("]");
			if(i != outIdxDl.size()-1)
				sb.append(",");
		}
		sb.append("]");		
		return sb.toString();
	}
	
	
	
}
