package fnguide.index.monitoring.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fnguide.index.monitoring.dashboard.DashBoardService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class DashBoardController {	
	private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	private DashBoardService dashBoardService;
	
	@RequestMapping(value = "/index_stk")
	public String home(@RequestParam(required=false) String dt, HttpServletRequest req, Model model) {
		
		logger.info("root 경로로 접속했을 시!"); 
		
		if(dt == null){
			dt = (String)req.getSession().getAttribute("dt");
			if(dt == null){ // 세션에 없을 시
				String today = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
				model.addAttribute("dt", today);
				req.getSession().setAttribute("dt", today);
				dt = today;
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
		
		model.addAttribute("events",dashBoardService.GetEventItem(dt));
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents/index_stk.jsp");
		
		return "template";
	}
	
	@RequestMapping(value = "/board")
	public String board(@RequestParam(required=false) String dt, HttpServletRequest req, Model model) {
		
		logger.info("root 경로로 접속했을 시!"); 
		
		if(dt == null){
			dt = (String)req.getSession().getAttribute("dt");
			if(dt == null){ // 세션에 없을 시
				String today = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
				model.addAttribute("dt", today);
				req.getSession().setAttribute("dt", today);
				dt = today;
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
		
		//model.addAttribute("events",dashBoardService.GetEventItem(dt));
		model.addAttribute("type", "url");
		model.addAttribute("contents", "contents/board.jsp");
		
		return "template";
	}
}