package com.missionsky.scp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	private DateUtil(){
		
	}
	
	/**
	 * string parse date
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static Date parse(String source,String pattern){
		if(source == null || source.trim().equals("")){
			return null;
		}
		Date date;
		try {
			if(pattern == null || pattern.trim().equals("")){
				date = new SimpleDateFormat(PATTERN).parse(source);
			}else{
				date = new SimpleDateFormat(pattern).parse(source);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return date;
	}
	
	/**
	 * date format String
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date,String pattern){
		if(date == null){
			return null;
		}
		if(pattern == null || pattern.trim().equals("")){
			return new SimpleDateFormat(PATTERN).format(date);
		}else{
			return new SimpleDateFormat(pattern).format(date);
		}
	}
}
