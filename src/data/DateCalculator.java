package data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class DateCalculator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String getAge(String string_date_big, String string_date_small) throws ParseException{
    	
//		System.out.println("string_date_big:"+string_date_big+" string_date_small:"+string_date_small  );
		
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date_big= simpleDateFormat.parse(string_date_big);
		Date date_small= simpleDateFormat.parse(string_date_small);
		
		LocalDate time_big= new java.sql.Date(date_big.getTime()).toLocalDate();
		LocalDate time_small=new java.sql.Date(date_small.getTime()).toLocalDate();
		Period period = Period.between(time_small, time_big);
		 
		String age="";
		if(period.getYears()<20){
			age="junior";
		}else if (period.getYears()<40){
			age="middle_age";
		}else if (period.getYears()<60){
			age="senior";
		}else if (period.getYears()<80){
			age="old";
		}else {
			age="very_old";
		}
		
		return age;
	}
	public static long differenceWithCurrent(String date) throws ParseException{
		SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");
    	Date current = new Date();
    	String current_date=dates.format(current); 
    	
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date1= simpleDateFormat.parse(current_date);
		Date date2= simpleDateFormat.parse(date);

        //Comparing dates
        long difference = Math.abs(date1.getTime() - date2.getTime());
        long differenceDates = difference / (24 * 60 * 60 * 1000);

		return differenceDates;
	}
	
	
	public static long differenceWithCurrent(String date_big, String date_small) throws ParseException{
		
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date1= simpleDateFormat.parse(date_big);
		Date date2= simpleDateFormat.parse(date_small);


        //Comparing dates
        long difference = Math.abs(date1.getTime() - date2.getTime());
        long differenceDates = difference / (24 * 60 * 60 * 1000);

		return differenceDates;
	}
	
	public static long differenceDate(String date_big, String date_small) throws ParseException{
		
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date1= simpleDateFormat.parse(date_big);
		Date date2= simpleDateFormat.parse(date_small);


        //Comparing dates
        long difference = date1.getTime() - date2.getTime();
        long differenceDates = difference / (24 * 60 * 60 * 1000);

		return differenceDates;
	}
}
