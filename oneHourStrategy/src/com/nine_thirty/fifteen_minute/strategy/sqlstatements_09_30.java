package com.nine_thirty.fifteen_minute.strategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.List;


/**
 * A Java MySQL PreparedStatement INSERT example. Demonstrates the use of a SQL INSERT statement against a MySQL
 * database, called from a Java program, using a Java PreparedStatement.
 * 
 * Created by Alvin Alexander, http://alvinalexander.com
 */

public class sqlstatements_09_30 {
	// Create Table For all the StockNames
	public static HashSet<String> getAllStockNames(String insertQuery, HashSet<String> hashSet_StockName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection,
			Statement statement) {

		try {
			Class.forName(JDBC_DRIVER);
			// the mysql insert statement
            System.out.println(" insertQuery  "+insertQuery);
			ResultSet rs = statement.executeQuery(insertQuery);

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				hashSet_StockName.add(tableName);
			}

		} catch (Exception e) {
			System.err.println("Got an exception!");
			e.printStackTrace();
		}
		// System.out.println(" allStockNames size :"+allStockNames.size());
		return hashSet_StockName;
	}

	// Create Table For all the StockNames
	public static HashSet<String> getStocksHasDataIssue(String insertQuery, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {
		java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		
		HashSet<String> hashSetName = new HashSet<String>();
		// System.out.println(" stockName :"+stockName);
		try {
			Class.forName(JDBC_DRIVER);
			ResultSet resultSet = statement.executeQuery(insertQuery);
			// System.out.println(" stockName :"+stockName);
			resultSet.afterLast();
			while (resultSet.previous()) {
				int Id = resultSet.getInt("Id");
				String Name = resultSet.getString("stockName");
				String stockDate = (String) resultSet.getString("stockDate");

				// System.out.println(" I009d :"+Id+" Name :"+Name+" stockDate "+stockDate);

				// System.out.println(" Name : :"+Name);
				hashSetName.add(Name);

			}

		} catch (Exception e) {
			System.err.println("Got an exception!");
			e.printStackTrace();
		}
		// System.out.println(" allStockNames size :"+allStockNames.size());
		return hashSetName;
	}

	// Create Table For all the StockNames
	public static HashMap<String, String> individualstockdata(String insertQuery, String JDBC_DRIVER, String DB_URL, String USER, String PASS,
			Connection connection, Statement statement) {

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
				long differenceDays = getDifferenceDays(timestamp_before, timestamp_now);
				timestamp_before = stockDate;

				if (differenceDays >= 1) {
					System.out.println("timestamp_now :"+timestamp_now+" differenceDays :"+differenceDays);
					HashMap_stockDataOfaDay.put(timestamp_now.toString(), stringBuilder_day_entry.toString());
					stringBuilder_day_entry = new StringBuilder();
				}
				i++;
			}
			// System.out.println(" HashMap_stockDataOfaDay size :" + HashMap_stockDataOfaDay.size() + "
			// HashSet_stockDataOfaDay size :" + HashSet_stockDataOfaDay.size());

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

	public static long getDifferenceDays(Timestamp timeStamp1, Timestamp timeStamp2) {
		
		System.out.println("   timeStamp1    ::"+timeStamp1+"    timeStamp2     "+timeStamp2);
		long differenceDays = 0;
		try {
			Date date1 = new Date(timeStamp1.getTime());
			Date date2 = new Date(timeStamp2.getTime());

			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
			String yy = originalFormat.format(date1);
			String yy2 = originalFormat.format(date2);
			Date date3 = originalFormat.parse(yy);

			Date date4 = originalFormat.parse(yy2);

			long diff = date3.getTime() - date4.getTime();
			differenceDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		  if(differenceDays>=1){ 
			  System.out.println(" getDifference  DaystimeStamp1 "+timeStamp1+"   timeStamp2 :"+timeStamp2+" differenceDays :"+differenceDays); 
		  }
		 

		return differenceDays;
	}

	public static java.sql.Timestamp getDate(String date, String time) {
		java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd hh:mm");
			Date newdateutil = originalFormat.parse(date + " " + time);
			// System.out.println(" newdate "+newdateutil);

			timestamp = new java.sql.Timestamp(newdateutil.getTime());
			// System.out.println(" timestamp "+timestamp);
			cal.setTimeInMillis(timestamp.getTime());
			cal.add(Calendar.MINUTE, -1);
			timestamp = new java.sql.Timestamp(cal.getTime().getTime());
			System.out.println(" timestamp :  " + timestamp);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timestamp;
	}

}