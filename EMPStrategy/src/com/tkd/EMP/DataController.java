package com.tkd.EMP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.StringUtils;

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

		TreeMap<String, String> oneDayMap = OneDayStockData.getOneDayStockdata("icici_bank", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		EmpStrategyTest(oneDayMap);
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

	public static void testconsiquitivenumber() {

		ArrayList<Integer> al = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

		for (int i = 0; i < (al.size() - 2); i++) {

			for (int j = i; j <= i + 2; j++) {

				// System.out.println(" al get value "+al.get(j));

			}
			// System.out.println(" end of set");

		}

	}

	public static long getDifferenceDays(String timestamp_before, String timestamp_now) {

		// System.out.println(" timestamp_before ::"+timestamp_before+"
		// timestamp_now "+timestamp_now);
		long differenceDays = 0;
		try {

			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date_before = inputFormat.parse(timestamp_before);
			Date date_now = inputFormat.parse(timestamp_now);

			if (date_now.after(date_before) || date_now.before(date_before)) {
				// System.out.println(" timestamp_before ::"+timestamp_before+"
				// timestamp_now "+timestamp_now);
				differenceDays = 1;
			}

			// 6 4 6
			// 4 3 4

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return differenceDays;
	}

	public static void EmpStrategyTest(TreeMap<String, String> oneDayMap) {

		TreeMap<String, String> oneDayMapEMP = oneDayMap;

		try {
			int mapSize = 1;

			System.out.println("   oneDayMapEMP  " + oneDayMapEMP.size());
			LinkedList<String> linkedList = new LinkedList<String>();
			for (Map.Entry<String, String> oneday : oneDayMapEMP.entrySet()) {
				linkedList.add(oneday.getValue());
			}

			EachDayData(linkedList, mapSize, oneDayMap);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void EachDayData(LinkedList<String> linkedList, int mapSize, TreeMap<String, String> oneDayMap) {

		for (String value : linkedList) {

			String[] daySplitValue = value.split(",");

			String stockId = daySplitValue[1].split("=")[1].trim();
			String stockname = daySplitValue[2].split("=")[1].trim();
			String stockDate = daySplitValue[3].split("=")[1].trim();
			String open = daySplitValue[4].split("=")[1].trim();
			String high = daySplitValue[5].split("=")[1].trim();
			String low = daySplitValue[6].split("=")[1].trim();
			String close = daySplitValue[7].split("=")[1].trim();
			String volume = daySplitValue[8].split("=")[1].trim();

			/*
			 * System.out.println("stockId ----- :: " + stockId + " stockname :: " + stockname + " stockDate :: "
			 * +stockDate+ " open :: " + open + " high :: " + high + " low :: " + low+ " close :: " +
			 * close+"   volume  ::  "+volume);
			 */

		}

		// System.out.println("linkedList.size() " + linkedList.size());

		StringBuilder completeSixDaysDatastrBldr = new StringBuilder();
		StringBuilder completeNineDaysDatastrBldr = new StringBuilder();

		LinkedList<String> sixDaysDataList = new LinkedList<String>();
		LinkedList<String> tempNineDays = new LinkedList<String>();
		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();

		boolean isFourthDay = false;
		
		for (int i = 0; i < (linkedList.size() - 5); i++) {
			sixDaysDataList = new LinkedList<String>();

			for (int j = i; j <= i + 5; j++) {

				sixDaysDataList.add(linkedList.get(j) + "::sixDaysOfStocksData::");
				if (j == i + 3) {
					tempNineDays.add(linkedList.get(j));

					// System.out.println(" j==i+3 ) " +linkedList.get(j) );
				}
			}

			completeSixDaysDatastrBldr.append(sixDaysDataList.toString()).append("::completetocksData::");
			// System.out.println(" end of set sixDaysDataList.toString() " );
		}

		for (int i = 0; i < linkedList.size(); i++) {

			
			linkedHashSet.add(linkedList.get(i) + "::nineDaysOfStocksData::");
			if(linkedHashSet.size()>9){
				
				linkedHashSet.remove(linkedHashSet.iterator().next());
			}
			
			for (String test : tempNineDays) {

				if (linkedList.get(i).equalsIgnoreCase(test)) {
					completeNineDaysDatastrBldr.append(linkedHashSet.toString()).append("::completetocksData::");
					//nineDaysDataList = new LinkedHashSet<String>();
                    
				}
			}
			//System.out.println(" end nineDaysDataList   "+linkedHashSet.size());
		}
		
		// System.out.println(" end of set sixDaysDataList toString   "+completeNineDaysDatastrBldr.toString());
		 
	IsThisAEMPDay(completeSixDaysDatastrBldr, completeNineDaysDatastrBldr, oneDayMap);

	}

	public static void IsThisAEMPDay(StringBuilder completeSixDaysDatastrBldr, StringBuilder completeNineDaysDatastrBldr, TreeMap<String, String> oneDayMap) {
		// TODO Auto-generated method stub

		String[] completetocksData = completeSixDaysDatastrBldr.toString().split("::completetocksData::"); 
		// <-- Do the split here after
		
		//System.out.println(completeSixDaysDatastrBldr.length() + "  completetocksData  length   :  " + completetocksData.length);

		for (String entry_map : completetocksData) {

			String[] sixDaysOfStocksData = entry_map.split("::sixDaysOfStocksData::"); 
			// <-- Do the split here after
			// System.out.println(" sixDaysOfStocksData length :
			// "+sixDaysOfStocksData.length+" ");

			double day1Difference_H_L = 0;
			double day1High = 0;
			double day1Low = 0;
			String day1stockDate = "";

			double day2Difference_H_L = 0;
			double day2High = 0;
			double day2Low = 0;
			String day2stockDate = "";

			double day3Difference_H_L = 0;
			double day3High = 0;
			double day3Low = 0;
			String day3stockDate = "";

			double day4Difference_H_L = 0;
			double day4High = 0;
			double day4Low = 0;
			String day4stockDate = "";

			double day5Difference_H_L = 0;
			double day5High = 0;
			double day5Low = 0;
			String day5stockDate = "";

			double day6Difference_H_L = 0;
			double day6High = 0;
			double day6Low = 0;
			String day6stockDate = "";

			int sixDaysHigLowCounter = 1;

			for (String echDyData : sixDaysOfStocksData) {

				
				// System.out.println(" echentry value eachDayData echDyData :"+echDyData);
				 
				String[] each_entry = echDyData.toString().split(",");
				double high = 0;
				double low = 0;
				String stockDate = "";
				
				for (String echentry : each_entry) {

					// System.out.println(" echentry value eachDayData length :"+echentry);

					if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockHigh")) {
						// System.out.println(" eachday entry stockHigh :
						// "+StringUtils.substringAfterLast(echentry, "=")+" ");
						high = Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));

					}
					if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockLow")) {
						// System.out.println(" sixDaysOfStocksData stockLow :
						// "+StringUtils.substringAfterLast(echentry, "="));
						low = Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));

					}
					if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockDate")) {
						stockDate = StringUtils.substringAfterLast(echentry, "=");

					}
				}

				if (sixDaysHigLowCounter == 1) {
					day1Difference_H_L = high - low;
					day1High = high;
					day1Low = low;
					day1stockDate = stockDate;

				}
				if (sixDaysHigLowCounter == 2) {
					day2Difference_H_L = high - low;
					day2High = high;
					day2Low = low;
					day2stockDate = stockDate;

				}
				if (sixDaysHigLowCounter == 3) {
					day3Difference_H_L = high - low;
					day3High = high;
					day3Low = low;
					day3stockDate = stockDate;

				}
				if (sixDaysHigLowCounter == 4) {
					day4Difference_H_L = high - low;
					day4High = high;
					day4Low = low;
					day4stockDate = stockDate;

				}
				if (sixDaysHigLowCounter == 5) {
					day5Difference_H_L = high - low;
					day5High = high;
					day5Low = low;
					day5stockDate = stockDate;

				}
				if (sixDaysHigLowCounter == 6) {
					day6Difference_H_L = high - low;
					day6High = high;
					day6Low = low;
					day6stockDate = stockDate;

				}

				sixDaysHigLowCounter++;

			}

			Double minValue = Math.min(Math.min(day1Difference_H_L, day2Difference_H_L), Math.min(day3Difference_H_L, day4Difference_H_L));

			// System.out.println(" minValue1
			// "+Math.min(day1Difference_H_L,day2Difference_H_L));
			// System.out.println(" minValue2
			// "+Math.min(day3Difference_H_L,day4Difference_H_L));
			// System.out.println(" minValue3
			// "+Math.min(Math.min(day1Difference_H_L,day2Difference_H_L),Math.min(day3Difference_H_L,day4Difference_H_L)));

			String day5Date = "";
			String day6Date = "";
			String day5CompleteData = "";
			String day6CompleteData = "";

			if ((day4Difference_H_L < day3Difference_H_L) && (day4Difference_H_L < day2Difference_H_L) && (day4Difference_H_L < day1Difference_H_L) && (day4High < day3High && day4Low > day3Low)) {
				System.out.println("its a EMP Day   on        " + day4stockDate + "  day4High                 " + day4High + "   day4Low                " + day4Low);
				
				StringBuilder highandLowATR= getEightDaysATR(completeNineDaysDatastrBldr,day4stockDate);

				System.out.println("highandLowATR   "+highandLowATR.toString());
				String[] highATRSB = highandLowATR.toString().split("::highandLowATR::");
				
				double  highATRValue = Double.parseDouble(highATRSB[highATRSB.length-2]);
				
				double lowATRValue = Double.parseDouble(highATRSB[highATRSB.length-1]);
				
				
				System.out.println("highATRValue   "+(highATRValue-day4High));

				System.out.println("lowATRValue   "+(lowATRValue-day4Low));
				
				System.out.println("lowATRValue   "+((highATRValue-day4High)-(lowATRValue-day4Low)));
				
				System.out.println("lowATRValue   "+((day4High)-(day4Low)));
				
				try {
					EmpProfitORLoss(day4stockDate, oneDayMap, day4High, day4Low, day5High, day5Low, day6High, day6Low);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				// take the stockdate of 4th day and consider it as EMP day.
				// from the 5 min stock data map take the day stock after the
				// emp day

				// System.out.println("not a EMP day on : " +day4stockDate + "
				// day4High "+day4High +" day4Low "+day4Low);
			}

		}

	}

	
	private static StringBuilder getEightDaysATR(StringBuilder completeNineDaysDatastrBldr, String day4stockDate){
		
		StringBuilder highandLowATR=new StringBuilder();
		
		String[] completetocksDataATR = completeNineDaysDatastrBldr.toString().split("::completetocksData::");
		
		//System.out.println(" getEightDaysATR "+completetocksDataATR.length);
		for(String entry_mapATR : completetocksDataATR) {
			
			String[] nineDaysOfStocksData = entry_mapATR.split("::nineDaysOfStocksData::");
			
			String lastOne = nineDaysOfStocksData[nineDaysOfStocksData.length-2];
			
			//System.out.println(" getEightDaysATR lastOne  "+lastOne);
			
			String[]  last_entryATR= lastOne.toString().split(",");
			
			String lastEntrystockDateATR="";
			
			for (String lastEntryATR : last_entryATR) {
				
				if (StringUtils.substringBeforeLast(lastEntryATR, "=").equalsIgnoreCase("stockDate")) {
					lastEntrystockDateATR = StringUtils.substringAfterLast(lastEntryATR, "=");

				}
				
			}
			
			if(day4stockDate.equalsIgnoreCase(lastEntrystockDateATR)){
				
				System.out.println("   day4stockDate  "+day4stockDate +"   lastEntrystockDateATR  "+lastEntrystockDateATR);
				double highATR = 0;
				double lowATR = 0;
				String stockDateATR = "";
				
				for (String echDyDataATR : nineDaysOfStocksData) {

					//System.out.println("   echDyDataATR  "+echDyDataATR);
					
					String[] each_entryATR = echDyDataATR.toString().split(",");
					
					for (String echentryATR : each_entryATR) {
					
						//System.out.println("   echentryATR  "+echentryATR);
						if (StringUtils.substringBeforeLast(echentryATR, "=").equalsIgnoreCase("stockHigh")) {
							// System.out.println(" eachday entry stockHigh  echentryATR :"+
						     //        Double.parseDouble(StringUtils.substringAfterLast(echentryATR, "=")));
							highATR = highATR+Double.parseDouble(StringUtils.substringAfterLast(echentryATR, "="));

						}
						if (StringUtils.substringBeforeLast(echentryATR, "=").equalsIgnoreCase("stockLow")) {
							// System.out.println(" eachday entry stockLow  echentryATR :"+
						      //       Double.parseDouble(StringUtils.substringAfterLast(echentryATR, "=")));
							lowATR = lowATR+Double.parseDouble(StringUtils.substringAfterLast(echentryATR, "="));

						}
						if (StringUtils.substringBeforeLast(echentryATR, "=").equalsIgnoreCase("stockDate")) {
							stockDateATR = StringUtils.substringAfterLast(echentryATR, "=");

						}
						
						
						
					}

				}
				
				System.out.println("    get eigth days ATR  high   "+(highATR /8 ) +"  lowATR      "+(lowATR /8 ));
				
				highandLowATR.append(+(highATR /8 )).append("::highandLowATR::").append((lowATR /8 )).append("::highandLowATR::");
				
			}
			
		}
		
		return highandLowATR;
	}
	
	
	
	private static void EmpProfitORLoss(String day4stockDate, TreeMap<String, String> oneDayMap, double day4High, double day4Low, double day5High, double day5Low, double day6High, double day6Low)
			throws ParseException {
		// TODO Auto-generated method stub

		TreeMap<String, String> fivminMap = FiveMinStockData.getFiveMinStockdata("icici_bank", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		TreeMap<String, String> oneHourMap = OneHourStockData.getOneHourStockdata("icici_bank", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		// System.out.println("day4stockDate :" + day4stockDate);
		// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date day4Date = inputFormat.parse(day4stockDate);

		LinkedList<String> twoDays5MinDate = new LinkedList<String>();
		int i = 0;
		for (Map.Entry<String, String> fivmin : fivminMap.entrySet()) {
			// System.out.println(" fivmin.getKey() "+fivmin.getKey() +"
			// fivmin.getValue() "+fivmin.getValue());
			Date day5and6date = inputFormat.parse(fivmin.getKey());
			if (day5and6date.after(day4Date) && i < 2) {

				twoDays5MinDate.add(fivmin.getValue() + "::completetocksData::");
				// System.out.println(" day4Date " + day4Date + "
				// twoDays5MinDate " + day5and6date + fivmin.getValue());
				i++;

			}
		}

		LinkedList<String> twoDaysOnedyDate = new LinkedList<String>();
		int j = 0;
		for (Map.Entry<String, String> oneDy : oneDayMap.entrySet()) {
			// System.out.println(" fivmin.getKey() "+fivmin.getKey() +"
			// fivmin.getValue() "+fivmin.getValue());
			Date day5and6date = inputFormat.parse(oneDy.getKey());
			if (day5and6date.after(day4Date) && j < 2) {

				twoDaysOnedyDate.add(oneDy.getValue() + "::completetocksData::");
				// System.out.println(" day4Date " + day4Date + "
				// twoDaysOnedyDate " + day5and6date + oneDy.getValue());
				j++;
			}

		}

		TwodaysOneDayData(twoDays5MinDate, twoDaysOnedyDate, day4High, day4Low, day5High, day5Low, day6High, day6Low);

	}

	private static void TwodaysOneDayData(LinkedList<String> twoDays5MinDate, LinkedList<String> twoDaysOnedyDate, double day4High, double day4Low, double day5High, double day5Low, double day6High,
			double day6Low) throws ParseException {
		// TODO Auto-generated method stub

		int day5BothSLTrgrd = 0;
		int day6BothSLTrgrd = 0;

		boolean day5hghSideEmpTrgd = false;
		boolean day5lowSideEmpTrgd = false;

		boolean day6hghSideEmpTrgd = false;
		boolean day6lowSideEmpTrgd = false;
		if (day5High > day4High) {
			System.out.println("day 5 high side emp trigerred " + (day5High - day4High));
			day5hghSideEmpTrgd = true;

		}
		if (day4Low > day5Low) {
			System.out.println("day 5 low side emp trigerred " + (day4Low - day5Low));
			day5lowSideEmpTrgd = true;
		}

		if (day6High > day5High) {
			System.out.println("day 6 high side emp trigerred " + (day6High - day5High));
			day6hghSideEmpTrgd = true;
		}
		if (day5Low > day6Low) {
			System.out.println("day 6 low side emp trigerred " + (day5Low - day6Low));
			day6lowSideEmpTrgd = true;
		}

		if (day5BothSLTrgrd == 2) {
			System.out.println("day 5 both SL trigerred");

		}
		if (day6BothSLTrgrd == 2) {
			System.out.println("day 6 both SL trigerred");
		}

		pointsGainedinTotal(twoDays5MinDate, twoDaysOnedyDate, day5hghSideEmpTrgd, day5lowSideEmpTrgd, day6hghSideEmpTrgd, day6lowSideEmpTrgd, day4High, day4Low, day5High, day5Low, day6High, day6Low);
	}

	private static void pointsGainedinTotal(LinkedList<String> twoDays5MinDate, LinkedList<String> twoDaysOnedyDate, boolean day5hghSideEmpTrgd, boolean day5lowSideEmpTrgd, boolean day6hghSideEmpTrgd,
			boolean day6lowSideEmpTrgd, double day4High, double day4Low, double day5High, double day5Low, double day6High, double day6Low) throws ParseException {

		double profitHighSide = 0;
		double profitLowSide = 0;
		double totalProfit = 0;

		if (((day5hghSideEmpTrgd && day6hghSideEmpTrgd) && (!day5lowSideEmpTrgd && !day6lowSideEmpTrgd))
				|| ((!day5hghSideEmpTrgd && !day6hghSideEmpTrgd) && (day5lowSideEmpTrgd && day6lowSideEmpTrgd))) {

			if ((day5hghSideEmpTrgd && day6hghSideEmpTrgd) && (!day5lowSideEmpTrgd && !day6lowSideEmpTrgd)) {

				profitHighSide = ((day5High - day4High) + (day6High - day5High));
				System.out.println("  high side only  trigerred .." + profitHighSide);
			}
			if ((!day5hghSideEmpTrgd && !day6hghSideEmpTrgd) && (day5lowSideEmpTrgd && day6lowSideEmpTrgd)) {

				profitLowSide = ((day5Low - day6Low) + (day4Low - day5Low));
				System.out.println(" lo side only trigerred   .." + profitLowSide);

			}

		} else {
			String[] eachDayfiveMinStocksData = twoDays5MinDate.toString().split("::completetocksData::");

			// here we have complete two days 5 min data
			// System.out.println(" twoDays5MinDate.toString() " + twoDays5MinDate.toString());

			int daycount = 0;
			for (String eachday5MinData : eachDayfiveMinStocksData) {

				// here we have each day 5 min data , each 5 min entry splits by
				// ,5_min_entry,
				String[] fiveMinOfStocksData = eachday5MinData.toString().split(",5_min_entry,");

				// System.out.println(" eachday5MinData.toString() " + eachday5MinData.toString() +" daycount "
				// +daycount);
				double high = 0;
				double low = 0;

				double highAftrTrigger = 0;
				double lowAftrTrigger = 0;
				String stockDate = "";
				String dateWhenHighSideTrgrd = "";
				String dateWhenLowSideTrgrd = "";

				boolean dateWhenFirshHighEntry = true;
				boolean dateWhenFirshLowEntry = true;

				boolean day5hghSidetrgrFound = false;
				boolean day5lowSidetrgrFound = false;

				boolean day6hghSidetrgrFound = false;
				boolean day6lowSidetrgrFound = false;

				for (String fivMinData : fiveMinOfStocksData) {

					// System.out.println("
					// fivMinData.toString()"+fivMinData.toString());
					String[] each_entry = fivMinData.toString().split(",");

					for (String echentry : each_entry) {

						// System.out.println(" echentry value eachDayData length :
						// ");
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockHigh")) {
							// System.out.println(" fivMinData entry stockHigh :
							// "+StringUtils.substringAfterLast(echentry, "=")+" ");
							high = Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));

						}
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockLow")) {
							// System.out.println(" fivMinData stockLow :
							// "+StringUtils.substringAfterLast(echentry, "="));
							low = Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));

						}
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockDate")) {
							// System.out.println(" fivMinData stockDate :
							// "+StringUtils.substringAfterLast(echentry, "="));
							stockDate = StringUtils.substringAfterLast(echentry, "=");

						}

						if (daycount == 0) {
							if (day5hghSideEmpTrgd) {

								if ((high > day4High) && !day5hghSidetrgrFound && high != 0) {
									System.out.println(" day5hghSideEmpTrgd stockDate  stockDate   " + stockDate + "  high  " + high + "  day4high   " + day4High);
									dateWhenHighSideTrgrd = stockDate;
									day5hghSidetrgrFound = true;
								}
							}
							if (day5lowSideEmpTrgd) {

								if ((low < day4Low) && !day5lowSidetrgrFound && low != 0) {
									System.out.println(" day5lowSideEmpTrgd stockDate  stockDate   " + stockDate + "  low  " + low + "  day4Low   " + day4Low);
									dateWhenLowSideTrgrd = stockDate;
									day5lowSidetrgrFound = true;
								}

							}
						}

						if (daycount == 1) {
							if (day6hghSideEmpTrgd) {

								if ((high > day5High) && !day6hghSidetrgrFound && high != 0) {
									System.out.println(" high day5High  day6hghSideEmpTrgd   day5Low stockDate  " + stockDate + "  high  " + high + "  day5high   " + day5High);
									dateWhenHighSideTrgrd = stockDate;
									day6hghSidetrgrFound = true;
								}
							}
							if (day6lowSideEmpTrgd) {

								if ((low < day5Low) && !day6lowSidetrgrFound && low != 0) {
									System.out.println(" day6lowSideEmpTrgd  low < day5Low stockDate " + stockDate + "  low  " + low + "  day5Low   " + day5Low);
									dateWhenLowSideTrgrd = stockDate;
									day6lowSidetrgrFound = true;
								}

							}
							// System.out.println(" daycount "+daycount+" day6hghSideEmpTrgd "+day6hghSideEmpTrgd+"
							// day6lowSideEmpTrgd "+day6lowSideEmpTrgd);
						}

						if (!dateWhenHighSideTrgrd.isEmpty()) {

							SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date3 = newFormat.parse(dateWhenHighSideTrgrd);

							Date stockDate1 = newFormat.parse(stockDate);

							if (stockDate1.after(date3)) {

								if (dateWhenFirshHighEntry) {
									lowAftrTrigger = low;
									dateWhenFirshHighEntry = false;
								}
								if (high >= highAftrTrigger) {
									System.out.println("  dateWhenHighSideTrgrd is not empty  high>=highAftrTrigger    " + high + "   dayCount  " + daycount);
									highAftrTrigger = high;
								}
								if (low <= lowAftrTrigger) {
									System.out.println("  dateWhenHighSideTrgrd is not empty  low <= lowAftrTrigger    " + low + "   dayCount  " + daycount);
									lowAftrTrigger = low;
								}
							}

						}
						if (!dateWhenLowSideTrgrd.isEmpty()) {

							SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date4 = newFormat.parse(dateWhenLowSideTrgrd);

							Date stockDate1 = newFormat.parse(stockDate);
							// System.out.println(" dateWhenLowSideTrgrd dateWhenLowSideTrgrd " + dateWhenLowSideTrgrd +
							// " stockDate1 " + stockDate1);
							if (stockDate1.after(date4)) {

								// System.out.println(" day6lowSideEmpTrgd "+high +" highAftrTrigger "+highAftrTrigger);
								// System.out.println(" day6lowSideEmpTrgd "+low +" lowAftrTrigger "+lowAftrTrigger);
								if (dateWhenFirshLowEntry) {
									lowAftrTrigger = low;
									dateWhenFirshLowEntry = false;
								}
								if (high >= highAftrTrigger) {
									// System.out.println(" dateWhenLowSideTrgrd is not empty high>=highAftrTrigger " +
									// high + " dayCount " + daycount);
									highAftrTrigger = high;
								}
								if (low <= lowAftrTrigger) {
									// System.out.println(" dateWhenLowSideTrgrd is not empty low <= lowAftrTrigger " +
									// low + " dayCount " + daycount);
									lowAftrTrigger = low;
								}

							}
						}
					}

				}
				daycount++;
				if (!dateWhenHighSideTrgrd.isEmpty()) {

				}
				if (!dateWhenLowSideTrgrd.isEmpty()) {

				}
				totalProfit = totalProfit + (highAftrTrigger - lowAftrTrigger);
				System.out.println("   highAftrTrigger   " + highAftrTrigger + " lowAftrTrigger  " + lowAftrTrigger + "   daycount ");

				// each day 5 min data end
			}
			System.out.println("   totalProfit=(highAftrTrigger-lowAftrTrigger);   " + totalProfit);

		}
	}

}

/*
 * System.out.println ("  day1High                 "+day1High +"   day1Low                "+day1Low
 * +"    (day4Difference_H_L<day1Difference_H_L)   "+(day4Difference_H_L< day1Difference_H_L)); System.out.println
 * ("  day2High                 "+day2High +"   day1Low                "+day2Low
 * +"  (day4Difference_H_L<day2Difference_H_L)    "+(day4Difference_H_L< day2Difference_H_L)); System.out.println
 * ("  day3High                 "+day3High +"   day1Low                "+day3Low
 * +"   (day4Difference_H_L<day3Difference_H_L)   "+(day4Difference_H_L< day3Difference_H_L)); System.out.println
 * ("  day4High                 "+day4High +"   day1Low                "+day4Low
 * +"   (day4High<day4High&&day4Low>day3Low)     "+(day4High<day3High&&day4Low> day3Low)); System.out.println
 * ("  day5High                 "+day5High +"   day1Low                "+day5Low); System.out.println
 * ("  day6High                 "+day6High +"   day1Low                "+day6Low);
 */