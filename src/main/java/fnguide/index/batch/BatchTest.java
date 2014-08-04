package fnguide.index.batch;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import fnguide.index.monitoring.utility.Mail;

public class BatchTest extends QuartzJobBean{

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		
		Mail.SendReportMail("spring batch test", "성공!", "reinitiate@fnguide.com", "reinitiate@gmail.com");
		System.out.println("스프링 배치 테스트");
		
	}

}
