package com.nine_thirty.fifteen_minute.strategy;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ten_fifteen.one_hourly.strategy.sqlstatements_10_15;

public class BusinessLogic_09_30 {

	public static HashMap<String, String> get09_30_15_minute(String tableName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {

		String insertQuery = "select * from "+tableName+" WHERE stockDate >= '2018-01-01 00:00:00' AND stockDate <= '2019-12-30 00:00:00' AND TimeFrame='5_min'";
		System.out.println("   insertQuery   ::"+insertQuery);
		HashMap<String, String> min_max_value = new HashMap<String, String>();
		String stockName = "";
		/* 2017-06-01 15:25:00 */
		HashMap<String, String> stockDataOfaDay = sqlstatements_09_30.individualstockdata(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		System.out.println("  stockDataOfaDay : size :"+stockDataOfaDay.size());
		String timeOnly_InDate = "";
		String stockDate = "";
		HashSet<Integer> number_of_SL = new HashSet<Integer>();
		HashSet<Double> total_Points_Gained = new HashSet<Double>();
		HashSet<Double> risk_reward_1_16 = new HashSet<Double>();
		if (stockDataOfaDay.size() >= 1) {
			// splits to give each day data
			for (Map.Entry<String, String> map_data : stockDataOfaDay.entrySet()) {
				StringBuilder stringBuilder = new StringBuilder(map_data.getValue());
				String[] introduction = stringBuilder.toString().split(",1_min_entry,"); // <-- Do the split here after
                System.out.println("   introduction :  "+introduction.length);
				ArrayList<Double> decMax_bwn_9_30 = new ArrayList<Double>();
				ArrayList<Double> decMin_bwn_9_30 = new ArrayList<Double>();

				double max_Value_9_30 = 0.0;
				double min_Value_9_30 = 0.0;

				ArrayList<Double> decMax_bwn_10_15 = new ArrayList<Double>();
				ArrayList<Double> decMin_bwn_10_15 = new ArrayList<Double>();

				double max_Value_10_15 = 0.0;
				double min_Value_10_15 = 0.0;

				boolean isTime_bwn_9_10 = false;
				boolean isTime_bwn_10_15 = false;

				for (String entry_map : introduction) {

					String[] each_entry = entry_map.toString().split(",");
					// splits to give each minute data
					for (String entry_data : each_entry) {
						if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockDate")) {
							stockDate = StringUtils.substringAfterLast(entry_data, "=");
							timeOnly_InDate = getTimeOnlyInDate(stockDate);
							try {
								isTime_bwn_9_10 = isTimeBetweenTwoTime("09:00:00", "09:31:00", timeOnly_InDate);
								isTime_bwn_10_15 = isTimeBetweenTwoTime("09:32:00", "15:17:00", timeOnly_InDate);
							} catch (Exception e) {
								System.err.println(" e isTimeBetweenTwoTime : exception " + e.getMessage());
							}
						}

						if (isTime_bwn_9_10) {
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockName")) {
								stockName = StringUtils.substringAfterLast(entry_data, "=");

							}
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockHigh")) {
								decMax_bwn_9_30.add(Double.parseDouble(StringUtils.substringAfterLast(entry_data, "=")));

							}
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockLow")) {
								decMin_bwn_9_30.add(Double.parseDouble(StringUtils.substringAfterLast(entry_data, "=")));

							}
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockDate")) {
								stockDate = StringUtils.substringAfterLast(entry_data, "=");

							}
						} else if (isTime_bwn_10_15) {
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockName")) {
								stockName = StringUtils.substringAfterLast(entry_data, "=");

							}
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockHigh")) {
								decMax_bwn_10_15.add(Double.parseDouble(StringUtils.substringAfterLast(entry_data, "=")));

							}
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockLow")) {
								decMin_bwn_10_15.add(Double.parseDouble(StringUtils.substringAfterLast(entry_data, "=")));

							}
							if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockDate")) {
								stockDate = StringUtils.substringAfterLast(entry_data, "=");

							}
						}
					}
				}
                System.out.println("    test ");
				max_Value_9_30 = Collections.max(decMax_bwn_9_30);
				min_Value_9_30 = Collections.max(decMin_bwn_9_30);

				max_Value_10_15 = Collections.max(decMax_bwn_10_15);
				min_Value_10_15 = Collections.max(decMin_bwn_10_15);

				one_Day_Data_Result(max_Value_9_30, min_Value_9_30, max_Value_10_15, min_Value_10_15, stockName, stockDate, total_Points_Gained, number_of_SL,risk_reward_1_16);
			}
		}
/*		double totalPoints = 0.0;
		for (Double total_Points : total_Points_Gained) {
			double advalue = totalPoints + total_Points;
			totalPoints = advalue;
		}
		
		double numberOfSL = 0.0;
		for (Integer total_Points : number_of_SL) {
			double advalue = numberOfSL + total_Points;
			numberOfSL = advalue;
		}

		System.out.println( "  stockName : "+stockName+" totalPoints :  " + totalPoints+" stockData : size : " + stockDataOfaDay.size()+" numberOfSL : "+numberOfSL+" number of  risk_reward_1_16 :"+risk_reward_1_16.size());
	*/	return min_max_value;

	}

	public static void one_Day_Data_Result(double max_Value_9_30, double min_Value_9_30, double max_Value_10_15, double min_Value_10_15, String stockName, String stockDate,
			HashSet<Double> total_Points_Gained, HashSet<Integer> number_of_SL,HashSet<Double> risk_reward_1_16) {
		boolean short_Side_Entry = false;
		boolean buy_Side_Entry = false;
		boolean SL_Triggered = false;

		double points_gained = 0.0;

		double stop_loss = 0.0;

		stop_loss = max_Value_9_30 - min_Value_9_30;
		if (min_Value_10_15 <= min_Value_9_30) {
			short_Side_Entry = true;
		}
		if (max_Value_10_15 >= max_Value_9_30) {
			buy_Side_Entry = true;
		}

		if (short_Side_Entry) {
			points_gained = min_Value_9_30 - min_Value_10_15;
		}
		if (buy_Side_Entry) {
			points_gained = max_Value_10_15 - max_Value_9_30;
		}

		if (short_Side_Entry && buy_Side_Entry) {
			SL_Triggered = true;
			number_of_SL.add(1);
		}
		double percentage=120;
		double value=stop_loss;
		double risk_reward = (double)(value*(percentage/100.0d));
		total_Points_Gained.add(points_gained);
		
		if(points_gained>=risk_reward){
			//System.out.println(" risk_reward>=points_gained  "+points_gained);
			risk_reward_1_16.add(risk_reward);
		}
//		System.out.println(" points_gained  "+points_gained +"  risk_reward :"+risk_reward+"  stop_loss : "+stop_loss);
		/*
		 * System.out.println("    points_gained " + points_gained + " SL_Triggered   " + SL_Triggered +
		 * "  number_of_SL   " + number_of_SL + " stockDate :" +
		 * stockDate+"   total_Points_Gained "+total_Points_Gained);
		 */

	}

	public static boolean isTimeBetweenTwoTime(String argStartTime, String argEndTime, String argCurrentTime) throws Exception {

		String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
		boolean isTimeValid = false;
		if (argStartTime.matches(reg) && argEndTime.matches(reg) && argCurrentTime.matches(reg)) {
			try {

				Date time1 = new SimpleDateFormat("HH:mm:ss").parse(argStartTime);
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(time1);

				Date time2 = new SimpleDateFormat("HH:mm:ss").parse(argEndTime);
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTime(time2);
				calendar2.add(Calendar.DATE, 1);

				Date d = new SimpleDateFormat("HH:mm:ss").parse(argCurrentTime);
				Calendar calendar3 = Calendar.getInstance();
				calendar3.setTime(d);
				calendar3.add(Calendar.DATE, 1);

				Date x = calendar3.getTime();
				if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
					// checkes whether the current time is between 14:49:00 and 20:11:13.
					isTimeValid = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return isTimeValid;

		} else {
			throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
		}

	}

	public static String getTimeOnlyInDate(String stockDate) {
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
		timestamp = timestamp.valueOf(stockDate);
		Date date1 = new Date(timestamp.getTime());
		SimpleDateFormat originalFormat009 = new SimpleDateFormat("HH:mm:ss");
		String timeOnlyInDate = originalFormat009.format(date1);
		return timeOnlyInDate;

	}

	public static HashMap<String, String> getByTime_9_to_10_15(String tableName, int numberOfDays, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection,
			Statement statement) {

		HashMap<String, String> min_max_value = new HashMap<String, String>();

		String insertQuery = "select * from  " + tableName + "  where stockDate between '2017/01/01' and '2017/01/03' and TIME(stockDate) between '09:00:00' AND '10:15:00'";

		min_max_value = getStockByTime(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		return min_max_value;

	}

	public static HashMap<String, String> getByTime_10_15_TO_3_17(String tableName, int numberOfDays, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection,
			Statement statement) {
		System.out.println("business login getByTime_10_15_TO_3_1");
		HashMap<String, String> min_max_value = new HashMap<String, String>();
		String insertQuery = "select * from  " + tableName + "  where stockDate like  '%2017/01/01%' and '2017/01/03'  and TIME(stockDate) between '10:15:00' AND '15:17:00'";
		
		min_max_value = getStockByTime(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		return min_max_value;

	}

	public static HashMap<String, String> getStockByTime(String insertQuery, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {
		HashMap<String, String> min_max_value = new HashMap<String, String>();
		String stockName = "";
		/* 2017-06-01 15:25:00 */
		HashMap<String, String> stockDataOfaDay = sqlstatements_10_15.individualstockdata(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		ArrayList<Double> decMax = new ArrayList<Double>();
		ArrayList<Double> decMin = new ArrayList<Double>();
		String stockDate = "";
		// System.out.println(" getStockDataOfaDay stockDataOfaDay :" +stockDataOfaDay.size());
		/*
		 * if (stockDataOfaDay.size() >= 1) { for (String data : stockDataOfaDay) { StringBuilder stringBuilder = new
		 * StringBuilder(data); String[] introduction = stringBuilder.toString().split(","); // <-- Do the split here
		 * after // replacements. for (String entry : introduction) { if (StringUtils.substringBeforeLast(entry,
		 * "=").equalsIgnoreCase("stockName")) { stockName = StringUtils.substringAfterLast(entry, "=");
		 * 
		 * } if (StringUtils.substringBeforeLast(entry, "=").equalsIgnoreCase("stockHigh")) {
		 * decMax.add(Double.parseDouble(StringUtils.substringAfterLast(entry, "=")));
		 * 
		 * } if (StringUtils.substringBeforeLast(entry, "=").equalsIgnoreCase("stockLow")) {
		 * decMin.add(Double.parseDouble(StringUtils.substringAfterLast(entry, "=")));
		 * 
		 * } if (StringUtils.substringBeforeLast(entry, "=").equalsIgnoreCase("stockDate")) { stockDate =
		 * StringUtils.substringAfterLast(entry, "=");
		 * 
		 * }
		 * 
		 * } } System.out.println(" stockDate :" + stockDate); double maxValue = Collections.max(decMax); double
		 * minValue = Collections.min(decMin); min_max_value.put("maxValue", String.valueOf(maxValue));
		 * min_max_value.put("minValue", String.valueOf(minValue)); min_max_value.put("stockDate", stockDate);
		 * min_max_value.put("stockDataOfaDay", String.valueOf(stockDataOfaDay)); min_max_value.put("stockName",
		 * stockName); }
		 */
		// System.out.println("minValue "+minValue+" maxValue " + maxValue + " stockDataOfaDay :" +
		// stockDataOfaDay.size() + " decMax size ::" + decMax.size() );

		return min_max_value;

	}

	public static double getMaxValue(ArrayList<Double> decMax) {

		double maxx = 0.0;
		System.out.println(" decMax  :" + decMax.indexOf(Collections.max(decMax)));
		System.out.println(" Collections.max (decMax) " + Collections.max(decMax));
		for (int i = 0; i < decMax.size(); i++) {
			// System.out.println(" decMax[i] :"+decMax[i]);
			if (maxx < decMax.get(i)) {
				maxx = decMax.get(i);
			}
		}
		System.out.println(maxx);
		return maxx;

	}

	public static double getMinValue(ArrayList<Double> decMin) {

		double minn = 0.0;
		System.out.println(" minn :" + decMin.indexOf(Collections.min(decMin)));
		System.out.println(" Collections.min(decMin) " + Collections.min(decMin));

		for (int i = 0; i < decMin.size(); i++) {
			// System.out.println(" decMin[i] :"+decMin.get(i));
			if (minn > decMin.get(i)) {
				minn = decMin.get(i);
			}
			// System.out.println("minn :"+minn);
		}

		System.out.println(minn);
		return minn;

	}

}
