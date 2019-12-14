package com.tkd.EMP;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.HashSet;

public class OneHourStockData {

	public static TreeMap<String, String> getOneHourStockdata(String tableName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {
				
		String insertQuery = "select * from "+tableName+" WHERE stockDate >= '2019-02-27 00:00:00' AND stockDate <= '2019-06-07 00:00:00' AND TimeFrame='1_Hour'  AND stockName NOT LIKE '%_f1%' ORDER BY stockDate ASC";
		//System.out.println("   insertQuery   ::"+insertQuery);
		TreeMap<String, String> min_max_value = new TreeMap<String, String>();
		String stockName = "";
		
		TreeMap<String, String> stockDataOfaDay = completeDataOneHour(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		//System.out.println("  stockDataOfaDay : size :"+stockDataOfaDay.size());
		return stockDataOfaDay;
		// TODO Auto-generated method stub
		
	}

	private static TreeMap<String, String> completeDataOneHour(String insertQuery, String JDBC_DRIVER, String dB_URL,
			String uSER, String pASS, Connection connection, Statement statement) {
		
		String timestamp_before = "";
		String timestamp_now = "";
		HashSet<String> HashSet_stockDataOfaDay = new HashSet<String>();
		TreeMap<String, String> TreeMap_stockDataOfaDay = new TreeMap<String, String>();
		int i = 0;
		
		// System.out.println(" insertQuery :"+insertQuery);
		
		try {
			Class.forName(JDBC_DRIVER);

			//System.out.println(" insertQuery  :"+insertQuery);
			ResultSet resultSet = statement.executeQuery(insertQuery);
			StringBuilder stringBuilder_min_entry = new StringBuilder();
			StringBuilder stringBuilder_day_entry = new StringBuilder();
			

			while (resultSet.next()) {
				int Id = resultSet.getInt("Id");
				String name = resultSet.getString("stockName");
				String stockDate = resultSet.getString("stockDate");
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
				stringBuilder_day_entry.append(stringBuilder_min_entry.toString()).append("1_hour_entry,");
				//System.out.println(" stringBuilder_day_entry size :"+stringBuilder_day_entry.toString());
				stringBuilder_min_entry = new StringBuilder();
				if (i == 0) {
					timestamp_before = stockDate;
				}
				timestamp_now = stockDate;
				long differenceDays = DataController.getDifferenceDays(timestamp_before, timestamp_now);
				timestamp_before = stockDate;

				if (differenceDays >= 1||i == 0) {
					//System.out.println("timestamp_now :"+timestamp_now+" differenceDays :"+differenceDays);
					TreeMap_stockDataOfaDay.put(timestamp_now.toString(), stringBuilder_day_entry.toString());
					stringBuilder_day_entry = new StringBuilder();
				}
				i++;
			}
			// System.out.println(" TreeMap_stockDataOfaDay size :" + TreeMap_stockDataOfaDay.size() + "  HashSet_stockDataOfaDay size :" + HashSet_stockDataOfaDay.size());

			/*
			 * long ddd = getDifferenceDays(date3, date4); System.out.println(" ddd :" + ddd);
			 */
		} catch (Exception e) {
			System.err.println(" individualstockdata Got an exception!");
			e.printStackTrace();
		}
		// System.out.println(" allStockNames size :"+TreeMap_stockDataOfaDay.size());
		return TreeMap_stockDataOfaDay;
	}
	

}
