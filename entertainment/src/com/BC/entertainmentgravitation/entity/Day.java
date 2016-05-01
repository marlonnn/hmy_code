package com.BC.entertainmentgravitation.entity;

public class Day {

	public int year;
	
	public int month;
	
	public int dayOfMonth;
	
	public int dayOfweek;
	
	public String dayOfLunar;
	
	public boolean isBlack = false;
	
	public boolean isSelect = false;
	
	public boolean isCheckout = false;
	
	/**
	 * 比较两个日期的大小
	 * @param day1 要比较的日期对象
	 * @param day2 要比较的日期对象
	 * @return  -1 无法比较，0两个日期为同一天，1为day1比较大，2为day2比较大
	 */
	public static int compare(Day day1,Day day2){
		int compare = -1;
		try {
			if(day1.year>day2.year){
				compare = 1;
			}else if(day1.year<day2.year){
				compare = 2;
			}else if(day1.month>day2.month){
				compare = 1;
			}else if(day1.month<day2.month){
				compare = 2;
			}else if(day1.dayOfMonth>day2.dayOfMonth){
				compare = 1;
			}else if(day1.dayOfMonth<day2.dayOfMonth){
				compare = 2;
			}else{
				compare = 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return compare;
	}
}
