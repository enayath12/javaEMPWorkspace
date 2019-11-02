package com.tkd.EMP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Spliterator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.joda.time.DateTime;
import org.joda.time.Days;


@WebServlet("/EMPStrategy")
public class DataController extends HttpServlet {
	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/tkddata";

	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "root";

	public static Connection connection = null;
	public static Statement statement = null;

	public static void main(String[] args) throws FileNotFoundException, IOException {

		HashSet<String> hashSet_StockName = new HashSet<String>();
		HashSet<Integer> hashSet_StockId = new HashSet<Integer>();
		try {
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String insertQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'tkddata'";
		// hashSet_StockName = sqlstatements_09_30.getAllStockNames(insertQuery,
		// hashSet_StockName, JDBC_DRIVER, DB_URL, USER, PASS, connection,
		// statement);
		// System.out.println("hashSet_StockName size :" +
		// hashSet_StockName.size());

		for (String tableName : hashSet_StockName) {
			System.out.println("hashSet_StockName tableName :" + tableName);
		}

		HashMap<String, String> fivminMap = FiveMinStockData.getFiveMinStockdata("icici_bank", JDBC_DRIVER, DB_URL,
				USER, PASS, connection, statement);

		HashMap<String, String> oneHourMap = OneHourStockData.getOneHourStockdata("icici_bank", JDBC_DRIVER, DB_URL,
				USER, PASS, connection, statement);

		HashMap<String, String> oneDayMap = OneDayStockData.getOneDayStockdata("icici_bank", JDBC_DRIVER, DB_URL, USER,
				PASS, connection, statement);

		EmpStrategyTest(fivminMap, oneHourMap, oneDayMap);
		try {

		} finally {

			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		

	}

	private static void testconsiquitivenumber() {
		
		
		ArrayList<Integer> al = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)); 
		
		for (int i = 0; i <(al.size()-2); i++) {
			
			for (int j = i; j <=i+2; j++) {
				
				System.out.println("  al get value   "+al.get(j));
				
				
			}
			System.out.println(" end of set");
			
		}
		
		
		
	}

	public static long getDifferenceDays(Timestamp timeStamp1, Timestamp timeStamp2) {

		// System.out.println(" timeStamp1 ::"+timeStamp1+" timeStamp2
		// "+timeStamp2);
		long differenceDays = 0;
		try {
			Date date1 = new Date(timeStamp1.getTime());
			Date date2 = new Date(timeStamp2.getTime());

			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
			String yy = originalFormat.format(date1);
			String yy2 = originalFormat.format(date2);

			Date date3 = originalFormat.parse(yy);

			Date date4 = originalFormat.parse(yy2);

			DateTime dt1 = new DateTime(date3);
			DateTime dt2 = new DateTime(date4);

			long diff = date3.getTime() - date4.getTime();
			differenceDays = Days.daysBetween(dt1, dt2).getDays();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return differenceDays;
	}

	public static void EmpStrategyTest(HashMap<String, String> fivminMap, HashMap<String, String> oneHourMap,
			HashMap<String, String> oneDayMap) {

		HashMap<String, String> fivminMapEMP = fivminMap;
		HashMap<String, String> oneHourMapEMP = fivminMap;
		HashMap<String, String> oneDayMapEMP = fivminMap;
		try {
			for (Map.Entry<String, String> fvmin : fivminMapEMP.entrySet()) {

				// System.out.println(" fvmin key :: "+fvmin.getKey());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (Map.Entry<String, String> onehr : oneHourMapEMP.entrySet()) {

				// System.out.println(" oneHour key :: "+onehr.getKey());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			int mapSize=1;
			ArrayList<String> arrayList = new ArrayList<String>(); 
			for (Map.Entry<String, String> oneday : oneDayMapEMP.entrySet()) {
				arrayList.add(oneday.getValue());
			}
			
			EachDayData(arrayList,mapSize);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void EachDayData(ArrayList<String> arrayList,int mapSize) {
        
		for(String value:arrayList){
			
			String[] daySplitValue=value.split(",");
			
			String stockId = daySplitValue[1].split("=")[1].trim();
			String stockname = daySplitValue[2].split("=")[1].trim();
			String stockDate = daySplitValue[3].split("=")[1].trim();
			String open = daySplitValue[4].split("=")[1].trim();
			String high = daySplitValue[5].split("=")[1].trim();
			String low = daySplitValue[6].split("=")[1].trim();
			String close = daySplitValue[7].split("=")[1].trim();
			String volume = daySplitValue[8].split("=")[1].trim();
			
			
			/* System.out.println("stockId ----- :: " + stockId + " stockname :: " + stockname
			+ " stockDate :: " +stockDate+ " open :: " + open + " high :: "
			+ high + " low :: " + low+ " close :: " + close+"   volume  ::  "+volume);*/
			 
		}
		
		ArrayList<String> sixDaysDataList = new ArrayList<String>(); 
		StringBuilder sixDaysDatastrBuilder = new StringBuilder();
	  for (int i = 0; i <(arrayList.size()-5); i++) {
			for (int j = i; j <=i+5; j++) {
				
				sixDaysDataList.add(arrayList.get(j));
				//System.out.println("  al get value   "+arrayList.get(j));
			}
			sixDaysDatastrBuilder.append(sixDaysDataList.toString()).append("::sixDaysOfStocksData::");
			System.out.println(" end of set");
		}
		 
	}
	
}
