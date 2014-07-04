package fnguide.index.monitoring.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SessionController {	
	private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
	
	@RequestMapping(value = "/setdate", method = RequestMethod.GET)	
	public void setdate(@RequestParam String target_dt, HttpServletRequest request) {		
		logger.info("날짜 세션 설정" + target_dt);
		request.getSession().setAttribute("targetdt", target_dt);
		
	}
}