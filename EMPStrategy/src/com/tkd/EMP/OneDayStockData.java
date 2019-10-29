package com.tkd.EMP;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class OneDayStockData {


	public static HashMap<String, String> getFiveMinStockdata(String tableName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {
				
		String insertQuery = "select * from "+tableName+" WHERE stockDate >= '2019-01-01 00:00:00' AND stockDate <= '2019-01-30 00:00:00' AND TimeFrame='1_day'";
		System.out.println("   insertQuery   ::"+insertQuery);
		HashMap<String, String> min_max_value = new HashMap<String, String>();
		String stockName = "";
		
		HashMap<String, String> stockDataOfaDay = completeDataFiveMin(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		System.out.println("  stockDataOfaDay : size :"+stockDataOfaDay.size());
		return null;
		// TODO Auto-generated method stub
		
	}

	private static HashMap<String, String> completeDataFiveMin(String insertQuery, String JDBC_DRIVER, String dB_URL,
			String uSER, String pASS, Connection connection, Statement statement) {
		
		java.sql.Timestamp timestamp_before = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		java.sql.Timestamp timestamp_now = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		HashSet<String> HashSet_stockDataOfaDay = new HashSet<String>();
		HashMap<String, String> HashMap_stockDataOfaDay = new HashMap<String, String>();
		int i = 0;
		
		// System.out.println(" insertQuery :"+insertQuery);
		
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println(" insertQuery  :"+insertQuery);
			ResultSet resultSet = statement.executeQuery(insertQuery);
			StringBuilder stringBuilder_min_entry = new StringBuilder();
			StringBuilder stringBuilder_day_entry = new StringBuilder();
			

			while (resultSet.next()) {
				int Id = resultSet.getInt("Id");
				String name = resultSet.getString("stockName");
				Timestamp stockDate = resultSet.getTimestamp("stockDate");
				Double stockOpen = resultSet.getDouble("stockOpen");
				Double stockHigh = resultSet.getDouble("stockHigh");
				Double stockLow = resultSet.getDouble("stockLow");
				Double stockClose = resultSet.getDouble("stockClose");
				Double stockVolume = resultSet.getDouble("stockVolume");

				//System.out.println( resultSet.getInt("Id")+"   name :   " + name);

				stringBuilder_min_entry.append("Id").append('=').append(Id).append(',');
				stringBuilder_min_entry.append("Id").append('=').append(Id).append(',');
				stringBuilder_min_entry.append("stockName").append('=').append(name).append(',');
				stringBuilder_min_entry.append("stockDate").append('=').append(stockDate).append(',');
				stringBuilder_min_entry.append("stockOpen").append('=').append(stockOpen).append(',');
				stringBuilder_min_entry.append("stockHigh").append('=').append(stockHigh).append(',');
				stringBuilder_min_entry.append("stockLow").append('=').append(stockLow).append(',');
				stringBuilder_min_entry.append("stockClose").append('=').append(stockClose).append(',');
				stringBuilder_min_entry.append("stockVolume").append('=').append(stockVolume).append(',');

				// 12 //13
				HashSet_stockDataOfaDay.add(stringBuilder_min_entry.toString());
				//System.out.println(" HashSet_stockDataOfaDay size :"+HashSet_stockDataOfaDay.size());
				stringBuilder_day_entry.append(stringBuilder_min_entry.toString()).append(",5_min_entry,");
				//System.out.println(" stringBuilder_day_entry size :"+stringBuilder_day_entry.toString());
				stringBuilder_min_entry = new StringBuilder();
				if (i == 0) {
					timestamp_before = stockDate;
				}
				timestamp_now = stockDate;
				long differenceDays = DataController.getDifferenceDays(timestamp_before, timestamp_now);
				timestamp_before = stockDate;

				if (differenceDays >= 1) {
					//System.out.println("timestamp_now :"+timestamp_now+" differenceDays :"+differenceDays);
					HashMap_stockDataOfaDay.put(timestamp_now.toString(), stringBuilder_day_entry.toString());
					stringBuilder_day_entry = new StringBuilder();
				}
				i++;
			}
			 System.out.println(" HashMap_stockDataOfaDay size :" + HashMap_stockDataOfaDay.size() + "  HashSet_stockDataOfaDay size :" + HashSet_stockDataOfaDay.size());

			/*
			 * long ddd = getDifferenceDays(date3, date4); System.out.println(" ddd :" + ddd);
			 */
		} catch (Exception e) {
			System.err.println(" individualstockdata Got an exception!");
			e.printStackTrace();
		}
		 System.out.println(" allStockNames size :"+HashMap_stockDataOfaDay.size());
		return HashMap_stockDataOfaDay;
	}
	

}
