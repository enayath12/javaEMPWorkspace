package com.tkd.EMP;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;

public class FiveMinStockData {

	public static TreeMap<String, String> getFiveMinStockdata(String tableName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {
				
		String insertQuery = "select * from "+tableName+" WHERE stockDate >= '2019-02-27 00:00:00' AND stockDate <= '2019-06-07 00:00:00' AND TimeFrame='5_min' AND stockName NOT LIKE '%_f1%' ORDER BY stockDate ASC";
		//System.out.println("   insertQuery   ::"+insertQuery);
		TreeMap<String, String> min_max_value = new TreeMap<String, String>();
		String stockName = "";
		
		TreeMap<String, String> stockDataOfaDay = completeDataFiveMin(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		//System.out.println("  stockDataOfaDay : size :"+stockDataOfaDay.size());
		return stockDataOfaDay;
		// TODO Auto-generated method stub
		
	}

	private static TreeMap<String, String> completeDataFiveMin(String insertQuery, String JDBC_DRIVER, String dB_URL,
			String uSER, String pASS, Connection connection, Statement statement) {
		
		String timestamp_before = "";
		String timestamp_now = "";
		
		TreeMap<String, String> TreeMap_stockDataOfaDay = new TreeMap<String, String>();
		int i = 0;
		
		// System.out.println(" insertQuery :"+insertQuery);
		
		try {
			Class.forName(JDBC_DRIVER);

			//System.out.println(" insertQuery  :"+insertQuery);
			ResultSet resultSet = statement.executeQuery(insertQuery);
			StringBuilder stringBuilder_min_entry = new StringBuilder();
			StringBuilder stringBuilder_day_entry = new StringBuilder();
			
// count == 1000
			
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

				
				if (i == 0) {
					timestamp_before = stockDate;
				}
				timestamp_now = stockDate;
				long differenceDays = 0;
				if (i!= 0) {
					 differenceDays = DataController.getDifferenceDays(timestamp_before, timestamp_now);
				}
				
				DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				if (differenceDays >= 1||resultSet.isLast()) {
					
					Date day4Date = inputFormat.parse(timestamp_before);
					TreeMap_stockDataOfaDay.put(timestamp_before, stringBuilder_day_entry.toString());
					stringBuilder_day_entry = new StringBuilder();
					//System.out.println(" in if stringBuilder_day_entry stockDate :  "+stockDate    +"   timestamp_before   "+timestamp_before +"    stringBuilder_day_entry.toString()     "+timestamp_before );

				}
				
				stringBuilder_day_entry.append(stringBuilder_min_entry.toString()).append("5_min_entry,");

				stringBuilder_min_entry = new StringBuilder();
				
				timestamp_before = stockDate;
				i++;
			}
			 //System.out.println(" TreeMap_stockDataOf5Min size :" + TreeMap_stockDataOfaDay.size() );

			/*
			 * long ddd = getDifferenceDays(date3, date4); System.out.println(" ddd :" + ddd);
			 */
		} catch (Exception e) {
			
			System.err.println(" individualstockdata Got an exception!");
			e.printStackTrace();
			
		}
		
		for (Map.Entry<String, String> fivmin : TreeMap_stockDataOfaDay.entrySet()) {
		
		// System.out.println(" TreeMap_stockDataOfaDay key   :"+fivmin.getKey()   );
			 
		}
		
		return TreeMap_stockDataOfaDay;
	}
	
}
