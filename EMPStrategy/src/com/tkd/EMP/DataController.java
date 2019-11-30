package com.tkd.EMP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Spliterator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.StringUtils;
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

		TreeMap<String, String> oneDayMap = OneDayStockData.getOneDayStockdata("icici_bank", JDBC_DRIVER, DB_URL, USER,
				PASS, connection, statement);

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
		
		
		ArrayList<Integer> al = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)); 
		
		for (int i = 0; i <(al.size()-2); i++) {
			
			for (int j = i; j <=i+2; j++) {
				
				//System.out.println("  al get value   "+al.get(j));
				
				
			}
			//System.out.println(" end of set");
			
		}
		
		
		
	}

	public static long getDifferenceDays(String timestamp_before, String timestamp_now) {

		//System.out.println(" timestamp_before ::"+timestamp_before+" timestamp_now "+timestamp_now);
		long differenceDays = 0;
		try {

			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date_before = inputFormat.parse(timestamp_before);
			Date date_now = inputFormat.parse(timestamp_now);
			
			if(date_now.after(date_before)||date_now.before(date_before)){
				//System.out.println(" timestamp_before ::"+timestamp_before+" timestamp_now "+timestamp_now);
				differenceDays=1;	
			}
			
			//6    4   6
			//4    3   4
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return differenceDays;
	}

	public static void EmpStrategyTest(
			TreeMap<String, String> oneDayMap) {

		TreeMap<String, String> oneDayMapEMP = oneDayMap;
	
		try {
			int mapSize=1;
			
			System.out.println("   oneDayMapEMP  "+oneDayMapEMP.size());
			LinkedList<String> linkedList = new LinkedList<String>(); 
			for (Map.Entry<String, String> oneday : oneDayMapEMP.entrySet()) {
				linkedList.add(oneday.getValue());
			}
			
			EachDayData(linkedList,mapSize, oneDayMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void EachDayData(LinkedList<String> linkedList,int mapSize, TreeMap<String, String> oneDayMap) {
        
		for(String value:linkedList){
			
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
		
		System.out.println("linkedList.size()   "+linkedList.size());
		
		StringBuilder completeSixDaysDatastrBldr = new StringBuilder();
		StringBuilder completeNineDaysDatastrBldr = new StringBuilder();
		
		LinkedList<String> sixDaysDataList = new LinkedList<String>();
		LinkedList<String> tempNineDays = new LinkedList<String>();
		LinkedList<String> nineDaysDataList = new LinkedList<String>();
		
		
		boolean isFourthDay=false;
	   for (int i = 0; i <(linkedList.size()-5); i++) {
		   sixDaysDataList = new LinkedList<String>();
			
		    for (int j = i; j <=i+5; j++) {
		    	
				sixDaysDataList.add(linkedList.get(j)+"::sixDaysOfStocksData::");
				if(j==i+3){
					tempNineDays.add(linkedList.get(j));
					
					//System.out.println(" j==i+3    ) " +linkedList.get(j) );
				}
			}
			
		    completeSixDaysDatastrBldr.append(sixDaysDataList.toString()).append("::completetocksData::");
			//System.out.println(" end of set sixDaysDataList.toString() "  );
		}
	   
	   
	   
	   
	   for (int i = 0; i <linkedList.size(); i++) {
		   
		   for(String test:tempNineDays){
			   
			   nineDaysDataList.add(linkedList.get(i)+"::nineDaysOfStocksData::");
			   System.out.println("   nine days data "+linkedList.get(i));
			   if(linkedList.get(i).equalsIgnoreCase(test)){
				   System.out.println("   break "+linkedList.get(i));
				   completeNineDaysDatastrBldr.append(nineDaysDataList.toString()).append("::completetocksData::");
				   nineDaysDataList = new LinkedList<String>();
			   }
		   }
			
		   
			//System.out.println(" end of set sixDaysDataList.toString() "  );
		}
	 
	  
	 // IsThisAEMPDay(completeSixDaysDatastrBldr,completeNineDaysDatastrBldr, oneDayMap);
	  
	}

	public static void IsThisAEMPDay(StringBuilder completeSixDaysDatastrBldr, StringBuilder completeNineDaysDatastrBldr, TreeMap<String, String> oneDayMap) {
		// TODO Auto-generated method stub
		
		String[] completetocksData = completeSixDaysDatastrBldr.toString().split("::completetocksData::"); // <-- Do the split here after
        System.out.println(completeSixDaysDatastrBldr.length()+"  completetocksData  length   :  "+completetocksData.length);
        
        for (String entry_map : completetocksData) {
        	
        	String[] sixDaysOfStocksData = entry_map.split("::sixDaysOfStocksData::"); // <-- Do the split here after
           // System.out.println("   sixDaysOfStocksData  length   :  "+sixDaysOfStocksData.length+"      ");
        	 
        	double  day1Difference_H_L=0;
        	double  day1High=0;
        	double  day1Low=0;
        	String day1stockDate="";
        	
        	double 	 day2Difference_H_L=0;
        	double  day2High=0;
        	double  day2Low=0;
        	String day2stockDate="";
        	
        	double  day3Difference_H_L=0;
        	double  day3High=0;
        	double  day3Low=0;
        	String day3stockDate="";
        	
        	double 	 day4Difference_H_L=0;
        	double  day4High=0;
        	double  day4Low=0;
        	String day4stockDate="";
        	
        	double 	 day5Difference_H_L=0;
        	double  day5High=0;
        	double  day5Low=0;
        	String day5stockDate="";
        	
        	double 	 day6Difference_H_L=0;
        	double  day6High=0;
        	double  day6Low=0;
        	String day6stockDate="";
        	 
        	int sixDaysHigLowCounter=1;
        	 String stockDate="";
            for (String echDyData : sixDaysOfStocksData) {
            	
            	String[] each_entry = echDyData.toString().split(",");
	       		 double high=0;
	       		 double low=0;
            	 for (String echentry : each_entry) {
            	
            		// System.out.println("   echentry value  eachDayData length   :       ");
            		 
			        	if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockHigh")) {
			        		//  System.out.println("   eachday entry stockHigh   :  "+StringUtils.substringAfterLast(echentry, "=")+"    ");
			        		  high=Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));
			
						}
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockLow")) {
							// System.out.println("   sixDaysOfStocksData stockLow   :  "+StringUtils.substringAfterLast(echentry, "="));
							 low=Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));
			
						}
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockDate")) {
							stockDate = StringUtils.substringAfterLast(echentry, "=");

						}
            	 }
            	 
            	 if(sixDaysHigLowCounter==1)
            	 {
            		 day1Difference_H_L=high-low;
            		 day1High=high;
                 	 day1Low=low;
            		 day1stockDate=stockDate;
            		 
            	 }
            	 if(sixDaysHigLowCounter==2)
            	 {
            		 day2Difference_H_L=high-low;
            		 day2High=high;
                 	 day2Low=low;
            		 day2stockDate=stockDate;
            		 
            	 }
            	 if(sixDaysHigLowCounter==3)
            	 {
            		 day3Difference_H_L=high-low;
            		 day3High=high;
                 	 day3Low=low;
            		 day3stockDate=stockDate;
            		 
            	 }
            	 if(sixDaysHigLowCounter==4)
            	 {
            		 day4Difference_H_L=high-low;
            		 day4High=high;
                 	 day4Low=low;
            		 day4stockDate=stockDate;
            		 
            	 }
            	 if(sixDaysHigLowCounter==5)
            	 {
            		 day5Difference_H_L=high-low;
            		 day5High=high;
                 	 day5Low=low;
            		 day5stockDate=stockDate;
            		 
            	 }
            	 if(sixDaysHigLowCounter==6)
            	 {
            		 day6Difference_H_L=high-low;
            		 day6High=high;
                 	 day6Low=low;
            		 day6stockDate=stockDate;
            		 
            	 }
            	 
            	 sixDaysHigLowCounter++;
            	
             }
            
          
            Double minValue=Math.min(Math.min(day1Difference_H_L,day2Difference_H_L),Math.min(day3Difference_H_L,day4Difference_H_L));

           // System.out.println("    minValue1     "+Math.min(day1Difference_H_L,day2Difference_H_L));
          //  System.out.println("    minValue2     "+Math.min(day3Difference_H_L,day4Difference_H_L));
          //  System.out.println("    minValue3     "+Math.min(Math.min(day1Difference_H_L,day2Difference_H_L),Math.min(day3Difference_H_L,day4Difference_H_L)));

            
            String day5Date ="";
            String day6Date ="";
            String day5CompleteData="";
            String day6CompleteData="";

            if((day4Difference_H_L<day3Difference_H_L)&&(day4Difference_H_L<day2Difference_H_L)&&(day4Difference_H_L<day1Difference_H_L)&&(day4High<day3High&&day4Low>day3Low)){
                System.out.println("its a EMP Day   on        "+day4stockDate   +   "  day4High                 "+day4High   +"   day4Low                "+day4Low);
                try {
					EmpProfitORLoss(day4stockDate, oneDayMap,day4High,day4Low,day5High,day5Low,day6High,day6Low);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                

            }else {

                //take the stockdate of 4th day and consider it as EMP day.
                //from the 5 min stock data map take the day stock after the emp day

              //  System.out.println("not a EMP day  on :           " +day4stockDate +    "  day4High          "+day4High   +"   day4Low                  "+day4Low);
            }
            
            }
            
        }

	private static void EmpProfitORLoss(String day4stockDate, TreeMap<String, String> oneDayMap, double day4High, double day4Low, double day5High, double day5Low, double day6High, double day6Low) throws ParseException {
		// TODO Auto-generated method stub
		
		TreeMap<String, String> fivminMap = FiveMinStockData.getFiveMinStockdata("icici_bank", JDBC_DRIVER, DB_URL,
				USER, PASS, connection, statement);

		TreeMap<String, String> oneHourMap = OneHourStockData.getOneHourStockdata("icici_bank", JDBC_DRIVER, DB_URL,
				USER, PASS, connection, statement);
		
		System.out.println("day4stockDate   :"+day4stockDate);
		// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date day4Date = inputFormat.parse(day4stockDate);
		
		LinkedList<String> twoDays5MinDate = new LinkedList<String>(); 
		int i=0;
		for (Map.Entry<String, String> fivmin : fivminMap.entrySet()) {
			//System.out.println("  fivmin.getKey()   "+fivmin.getKey()  +"  fivmin.getValue()    "+fivmin.getValue());
			Date day5and6date = inputFormat.parse(fivmin.getKey());
			if(day5and6date.after(day4Date)&&i<2){
				
				twoDays5MinDate.add(fivmin.getValue());
			    System.out.println(" day4Date      "+day4Date+"  twoDays5MinDate  "+day5and6date +fivmin.getValue() );
		     	i++;
			
			}
		}
		
		
		LinkedList<String> twoDaysOnedyDate = new LinkedList<String>(); 
		int j=0;
		for (Map.Entry<String, String> oneDy : oneDayMap.entrySet()) {
			//System.out.println("  fivmin.getKey()   "+fivmin.getKey()  +"  fivmin.getValue()    "+fivmin.getValue());
			Date day5and6date = inputFormat.parse(oneDy.getKey());
			if(day5and6date.after(day4Date)&&j<2){
				
				twoDaysOnedyDate.add(oneDy.getValue());
		    	System.out.println("   day4Date      "+day4Date+"  twoDaysOnedyDate  "+day5and6date +oneDy.getValue());
		    	j++;
			}
			
		}
		
		TwodaysOneDayData(twoDays5MinDate,twoDaysOnedyDate,day4High,day4Low,day5High,day5Low,day6High,day6Low);
		
	}

	private static void TwodaysOneDayData(LinkedList<String> twoDays5MinDate, LinkedList<String> twoDaysOnedyDate, double day4High, double day4Low, double day5High, double day5Low, double day6High, double day6Low) {
		// TODO Auto-generated method stub
		
		 int day5BothSLTrgrd=0;
		 int day6BothSLTrgrd=0;
		 
		 boolean day5hghSideEmpTrgd=false;
		 boolean day5lowSideEmpTrgd=false;
		 
		 boolean day6hghSideEmpTrgd=false;
		 boolean day6lowSideEmpTrgd=false;
		 if(day5High>day4High) {
             System.out.println("day 5 high side emp trigerred "+(day5High-day4High));
             day5hghSideEmpTrgd=true;
             
         }
         if(day4Low>day5Low) {
             System.out.println("day 5 low side emp trigerred "+(day4Low-day5Low));
             day5lowSideEmpTrgd=true;
         }
         
         if(day6High>day5High) {
             System.out.println("day 6 high side emp trigerred "+(day5High-day4High));
             day6hghSideEmpTrgd=true;
         }
         if(day5Low>day6Low) {
             System.out.println("day 6 low side emp trigerred "+(day4Low-day5Low));
             day6lowSideEmpTrgd=true;
         }
         
         if(day5BothSLTrgrd==2) {
             System.out.println("day 5 both SL trigerred");
             
         }
         if(day6BothSLTrgrd==2) {
             System.out.println("day 6 both SL trigerred");
         }
         
         
         pointsGainedinTotal(twoDays5MinDate,twoDaysOnedyDate,day5hghSideEmpTrgd,
		  day5lowSideEmpTrgd,day6hghSideEmpTrgd,day6lowSideEmpTrgd);
	}

	private static void pointsGainedinTotal(LinkedList<String> twoDays5MinDate, LinkedList<String> twoDaysOnedyDate,
			boolean day5hghSideEmpTrgd, boolean day5lowSideEmpTrgd, boolean day6hghSideEmpTrgd, boolean day6lowSideEmpTrgd) {
		
		 // TODO Auto-generated method stub
		 String[] fiveMinOfStocksData = twoDays5MinDate.toString().split(",5_min_entry,");
         
         for (String fivMinData : fiveMinOfStocksData) {
         	
         	String[] each_entry = fivMinData.toString().split(",");
	       		 double high=0;
	       		 double low=0;
	       		 String stockDate="";
         	 for (String echentry : each_entry) {
         	
         		   // System.out.println("   echentry value  eachDayData length   :       ");
			        	if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockHigh")) {
			        		//  System.out.println("   fivMinData entry stockHigh   :  "+StringUtils.substringAfterLast(echentry, "=")+"    ");
			        		  high=Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));
			
						}
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockLow")) {
							 // System.out.println("   fivMinData stockLow   :  "+StringUtils.substringAfterLast(echentry, "="));
							  low=Double.parseDouble(StringUtils.substringAfterLast(echentry, "="));
			
						}
						if (StringUtils.substringBeforeLast(echentry, "=").equalsIgnoreCase("stockDate")) {
							// System.out.println("   fivMinData stockDate   :  "+StringUtils.substringAfterLast(echentry, "="));
							  stockDate = StringUtils.substringAfterLast(echentry, "=");

						}
         	  }
         	 
          }
	}
        
	
	
	
	
}





/*
System.out.println ("  day1High                 "+day1High   +"   day1Low                "+day1Low +"    (day4Difference_H_L<day1Difference_H_L)   "+(day4Difference_H_L<day1Difference_H_L));
System.out.println ("  day2High                 "+day2High   +"   day1Low                "+day2Low  +"  (day4Difference_H_L<day2Difference_H_L)    "+(day4Difference_H_L<day2Difference_H_L));
System.out.println ("  day3High                 "+day3High   +"   day1Low                "+day3Low  +"   (day4Difference_H_L<day3Difference_H_L)   "+(day4Difference_H_L<day3Difference_H_L));
System.out.println ("  day4High                 "+day4High   +"   day1Low                "+day4Low   +"   (day4High<day4High&&day4Low>day3Low)     "+(day4High<day3High&&day4Low>day3Low));
System.out.println ("  day5High                 "+day5High   +"   day1Low                "+day5Low);
System.out.println ("  day6High                 "+day6High   +"   day1Low                "+day6Low);
*/