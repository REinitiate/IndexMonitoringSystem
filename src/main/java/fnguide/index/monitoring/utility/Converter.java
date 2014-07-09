package fnguide.index.monitoring.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;



public class Converter {
	public static String GetUtc(String dt){		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = new GregorianCalendar();
		try {
			cal.setTime(sdf.parse(dt));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return Long.toString(cal.getTimeInMillis() + 1000*60*60*24);
	}
	
	public static String Date2Date(String dt, String conj){
		String result = dt.substring(0, 4) + conj + dt.substring(4, 6) + conj + dt.substring(6, 8);
		
		return result;
	}	
}