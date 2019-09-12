  package com.journaldev.first;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A Java MySQL PreparedStatement INSERT example.
 * Demonstrates the use of a SQL INSERT statement against a
 * MySQL database, called from a Java program, using a
 * Java PreparedStatement.
 * 
 * Created by Alvin Alexander, http://alvinalexander.com
 */
public  class createInsertdata008 
{
    
	  
	   //Create Table For all the StockNames
	   public static boolean createTableForStockNames(String JDBC_DRIVER,String DB_URL,String USER,String PASS,Connection conn,Statement stmt)
	   {
		   boolean status=true; 

		  
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		     // System.out.println("Connecting to a selected database...");
		   
		      // System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		    //  System.out.println("Creating table in given database...");
		      
		      String sql = "CREATE TABLE ALLSTOCKNAMES " +
	                   "(stockId INTEGER(111) NOT NULL AUTO_INCREMENT, " +
	                   " stockName VARCHAR(255)  NOT NULL UNIQUE, " + 
	                   " PRIMARY KEY ( stockId ))"; 
		      
		      stmt.executeUpdate(sql);
		      // System.out.println("Created table in given database...");
			   }catch(SQLSyntaxErrorException se){
		      //Handle errors for JDBC
			   //java.sql.SQLSyntaxErrorException: Table 'registration' already exists
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
		  
		 //  System.out.println("Goodbye!");
		   return status;
	   }
	   
	 //Create Table For all the StockNames
	   public static boolean createTableForIndividualStock(String NameOfStock,String JDBC_DRIVER,String DB_URL,String USER,String PASS,Connection conn,Statement stmt)
	   {
		   boolean status=true; 
		 
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to a selected database...");
		   
		    //  System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      //System.out.println("Creating table in given database..."+NameOfStock);
		      
		      String sql = "CREATE TABLE "+NameOfStock+" " +
	                   "(Id INTEGER(111) NOT NULL AUTO_INCREMENT, " +
	                   " stockName VARCHAR(255)  NOT NULL , " + 
	                   " stockDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP , " + 
	                   " stockOpen DOUBLE NULL , " + 
	                   " stockHigh DOUBLE NULL , " + 
	                   " stockLow DOUBLE NULL , " + 
	                   " stockClose DOUBLE NULL , " + 
	                   " stockVolume DOUBLE NULL , " + 
	                   " PRIMARY KEY ( Id ))"; 
		      
		      stmt.executeUpdate(sql);
		     // System.out.println("Created table in given database..."+NameOfStock);
		   }catch(SQLSyntaxErrorException se){
		      //Handle errors for JDBC
			   //java.sql.SQLSyntaxErrorException: Table 'registration' already exists
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
		  
		   return status;
	   }
	  //Create Table For all the StockNames
	  public static boolean insertAllStockNames(String NameOfStock,String JDBC_DRIVER,String DB_URL,String USER,String PASS,Connection conn,Statement stmt)
	  {
		  boolean status=true; 
		 
	    try
	    {
	    	Class.forName(JDBC_DRIVER);
	     	      
	      // the mysql insert statement
	      String insertQuery = "insert into ALLSTOCKNAMES (stockName)"+"values(?)";
	      PreparedStatement prsmt=conn.prepareStatement(insertQuery);
	      prsmt.setString(1,NameOfStock );
	      prsmt.execute();
	      
	     // System.out.println("  NameOfStock :"+NameOfStock);
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	     e.printStackTrace();
	    }
	    return status;
	  }
	  
	  //Create Table For all the StockNames
	  public static HashMap<Integer,String> getAllStockNames(String JDBC_DRIVER,String DB_URL,String USER,String PASS,Connection conn,Statement stmt)
	  {
		
		   HashMap<Integer,String> allStockNames= new HashMap<>();
	    try
	    {
	    	Class.forName(JDBC_DRIVER);
	      
	      // the mysql insert statement
	      String insertQuery = "SELECT * FROM ALLSTOCKNAMES";
	      
	      ResultSet rs = stmt.executeQuery(insertQuery);

          while (rs.next()){
        	  int stockId = rs.getInt("stockId");
        	  String stockName = rs.getString("stockName");
        	  allStockNames.put(stockId, stockName);
          }
	      
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	     e.printStackTrace();
	    }
	   // System.out.println("  allStockNames size  :"+allStockNames.size());
	    return allStockNames;
	  }
	  
	// Create Table For all the StockNames
	public static boolean insertStockData(String NameOfStock, String name, String date, String time, double open,
			double high, double low, double close, double volume,String JDBC_DRIVER,String DB_URL,String USER,String PASS,Connection conn,Statement stmt) {
		boolean status = true;
		java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		//System.out.println("  newdate "+ NameOfStock+name+date+time+" open "+ open+"  high "+ high+"  low "+ low+ " close "+close+ "volume "+volume);
		try {
			
			Class.forName(JDBC_DRIVER);

			
			 if(time.contains("12:")){
				 timestamp =createInsertdata008.getDateBasedOn12(date,time+" PM");
			 }else{
			timestamp =createInsertdata008.getDate(date,time);
			 }
			//System.out.println(" new.. post "+timestamp);
			
			String insertQuery = "insert into "+NameOfStock+"(stockName,stockDate,stockOpen,stockHigh,stockLow,stockClose,stockVolume)" + "values(?,?,?,?,?,?,?)";
			PreparedStatement prsmt = conn.prepareStatement(insertQuery);
			prsmt.setString(1, name);
			prsmt.setTimestamp(2, timestamp);
			prsmt.setDouble(3, open);
			prsmt.setDouble(4, high);
			prsmt.setDouble(5, low);
			prsmt.setDouble(6, close);
			prsmt.setDouble(7, volume);
			prsmt.execute();

			//System.out.println("  NameOfStock :" + NameOfStock);


		} catch (Exception e) {
			System.err.println("Got an exception!");
			e.printStackTrace();
		}
		return status;
	}
	public static java.sql.Timestamp getDateBasedOn12(String date,String time){
		java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		try {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd hh:mm a");
		Date newdateutil = originalFormat.parse(date+" "+time);
		//System.out.println("  newdate "+newdateutil);
		
		 timestamp = new java.sql.Timestamp(newdateutil.getTime());
		// System.out.println("  timestamp "+timestamp);
		 cal.setTimeInMillis(timestamp.getTime());
		 cal.add(Calendar.MINUTE, -1);
		 timestamp = new java.sql.Timestamp(cal.getTime().getTime());
		// System.out.println(" timestamp :  "+timestamp);
		
		
		/*originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String formatedDate = originalFormat.format(newdateutil);
		System.out.println("  formatedDate "+formatedDate+ " newDate "); // Sat Jan 02 00:00:00 GMT 2010
		System.out.println(" newDate  formatedDate "+new java.sql.Date(newdateutil.getTime())); // Sat Jan 02 00:00:00 GMT 2010
		*/
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timestamp;
	}
	public static java.sql.Timestamp getDate(String date,String time){
		java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		try {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd hh:mm");
		Date newdateutil = originalFormat.parse(date+" "+time);
		//System.out.println("  newdate "+newdateutil);
		
		 timestamp = new java.sql.Timestamp(newdateutil.getTime());
		// System.out.println("  timestamp "+timestamp);
		 cal.setTimeInMillis(timestamp.getTime());
		 cal.add(Calendar.MINUTE, -1);
		 timestamp = new java.sql.Timestamp(cal.getTime().getTime());
		 System.out.println(" timestamp :  "+timestamp);
		
		
		/*originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String formatedDate = originalFormat.format(newdateutil);
		System.out.println("  formatedDate "+formatedDate+ " newDate "); // Sat Jan 02 00:00:00 GMT 2010
		System.out.println(" newDate  formatedDate "+new java.sql.Date(newdateutil.getTime())); // Sat Jan 02 00:00:00 GMT 2010
		*/
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timestamp;
	}
}