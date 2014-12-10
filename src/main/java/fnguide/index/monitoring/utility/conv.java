package fnguide.index.monitoring.utility;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;



public class conv {
	public static Long GetUtc(String dt){		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = new GregorianCalendar();
		try {
			cal.setTime(sdf.parse(dt));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return cal.getTimeInMillis() + 1000*60*60*24;
	}
	
	public static String Date2Date(String dt, String conj){
		String result = dt.substring(0, 4) + conj + dt.substring(4, 6) + conj + dt.substring(6, 8);
		
		return result;
	}
	
	/**
	 * 기능 : BigDecimal 타입을 Double로 변경하여 반환한다.
	 * @param input 입력값
	 * @return Double로 변경된 입력값.
	 */
	public static Double BigDecimal2Double(Object input){
		if(input != null)
			return ((BigDecimal)input).doubleValue();
		else
			return 0.0;
	}
}