package fnguide.index.monitoring.utility;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Ut {
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	
	private static DecimalFormat df1 = new DecimalFormat("#,###");
	private static DecimalFormat df2 = new DecimalFormat("0.00%");
	private static DecimalFormat df3 = new DecimalFormat("0.0000");
	
	public static String date2str(Date date){
		return sdf.format(date);
	}
	public static Date str2date(String date){
		Date result = null;
		try {
			result = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static Date addDate(Date dt, int field, int amount){
		Calendar cal = new GregorianCalendar();
		cal.setTime(dt);
		cal.add(field, amount);
		return cal.getTime();
	}
	
	public static String addDate(String strDt, int field, int amount){
		
		Date dt = str2date(strDt);		
		Calendar cal = new GregorianCalendar();
		cal.setTime(dt);
		cal.add(field, amount);
		return date2str(cal.getTime());
	}
	
	/**
	 * 기능 : '#.###' 포매팅
	 * @param data
	 * @return
	 */
	public static String df1(Object data){
		return df1.format(data);
	}
	
	/**
	 * 기능 : '0.00%' 포매팅
	 * @param data
	 * @return
	 */
	public static String df2(Object data){
		return df2.format(data);
	}
	
	/**
	 * 기능 : '0.0000' 포매팅
	 * @param data
	 * @return
	 */
	public static String df3(Object data){
		return df3.format(data);
	}
	
	
	
	
}
