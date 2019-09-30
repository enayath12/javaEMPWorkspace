package com.all.strategy.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nine_thirty.fifteen_minute.strategy.BusinessLogic_09_30;
import com.nine_thirty.fifteen_minute.strategy.sqlstatements_09_30;
import com.ten_fifteen.one_hourly.strategy.BusinessLogic_10_15;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet("/Controller")
public class controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String HTML_START = "<html><body>";
	public static final String HTML_END = "</body></html>";

	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/tkddata";

	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "root";

	/*
	 * public static HashSet<String> hashSet_StockName = new HashSet<String>(); public static HashSet<Integer>
	 * hashSet_StockId = new HashSet<Integer>();
	 * 
	 * public static HashMap<String, String> HashMap_name_ID = new HashMap<String, String>();
	 * 
	 * public static HashMap<String, String> min_max_HasMap_1day = new HashMap<String, String>(); public static
	 * HashMap<String, String> min_max_HasMap_1hour = new HashMap<String, String>();
	 */

	public static Connection connection = null;
	public static Statement statement = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public controller() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(" .. get ");
		PrintWriter out = response.getWriter();
		Date date = new Date();
		out.println(HTML_START + "<h2>Hi There!</h2><br/><h3>Date=" + date + "</h3>" + HTML_END);

	}

	/**
	 * Unzip it
	 * 
	 * @param zipFile
	 *            input zip file
	 * @param output
	 *            zip file output folder
	 */

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contentType = request.getContentType();
		HashSet<String> hashSet_StockName = new HashSet<String>();
		HashSet<Integer> hashSet_StockId = new HashSet<Integer>();
		try {
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter out = response.getWriter();

		
		String insertQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'tkddata'";
		hashSet_StockName = sqlstatements_09_30.getAllStockNames(insertQuery, hashSet_StockName, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		System.out.println("hashSet_StockName size :" + hashSet_StockName.size());

		for (String tableName : hashSet_StockName) {
			System.out.println("hashSet_StockName tableName :" + tableName);
			
			/*insertQuery = "select * from " + tableName + " where stockDate LIKE '%2017-10%'";
			HashSet<String> hashSet_tableWithIssue = sqlstatements_09_30.getStocksHasDataIssue(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
			for (String entryDate : hashSet_tableWithIssue) {
				System.out.println(" entryDate :: " + entryDate);
			}
			int numberOfDays = 1;*/

		}
		
		test_9_30_15min_strategy("icici_bank", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		// System.out.println("stockname size :-:"+StockNameAndId.size());
		
		 // hourly strategy 10:15 entry to 15:17 
		test_10_15_hourly_strategy("icici_bank", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		 
		// hourly strategy 09:30 entry to 15:17 
		// test_9_30_15min_strategy("ACC", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		
		
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

		RequestDispatcher req = request.getRequestDispatcher("/jsps/NewFile.jsp");
		req.forward(request, response);

		// TODO Auto-generated method stub
	}

	public void test_10_15_hourly_strategy(String tableName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {

		HashMap<String, String> hashMap_min_max_1day = new HashMap<String, String>();
		hashMap_min_max_1day = BusinessLogic_10_15.get_10_15_hourly(tableName, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

	}
	
	public void test_9_30_15min_strategy(String tableName, String JDBC_DRIVER, String DB_URL, String USER, String PASS, Connection connection, Statement statement) {
        
		System.out.println(" test_9_30_15min_strategy :  "+tableName);
		HashMap<String, String> hashMap_min_max_1day = new HashMap<String, String>();
		hashMap_min_max_1day = BusinessLogic_09_30.get09_30_15_minute(tableName, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

	}

	
	public static void EMP(ArrayList<Integer> list) {

	    double day1High =1000;
	    double day1Low =20.20;

	    double day2High =80.80;
	    double day2Low =40.20;

	    double day3High  = 80.60;
	    double day3Low  =20.70;

	    double day4High  = 60;
	    double day4Low  =40;

	    double day5High  = 60;
	    double day5Low  =20;

	    double day6High  = 80;
	    double day6Low  =60;

	    double day1Difference_H_L  =0;
	    double day2Difference_H_L   =0;
	    double day3Difference_H_L   =0;
	    double day4Difference_H_L   =0;
	    double day5Difference_H_L   =0;
	    double day6Difference_H_L   =0;

	    System.out.println("main method listt ");

	    System.out.println("main method liste "+list.size());

	    for(int i=0;i<list.size();i++) {

	        day1Difference_H_L= list.get(i);
	        day2Difference_H_L= list.get(i+1);
	        day3Difference_H_L= list.get(i+2);
	        day4Difference_H_L= list.get(i+3);
	        day5Difference_H_L= list.get(i+4);
	        day6Difference_H_L= list.get(i+5);


	        System.out.println("    day1Difference_H_L     "+day1Difference_H_L);
	        System.out.println("    day2Difference_H_L     "+day2Difference_H_L);

	        Double minValue=Math.min(Math.min(day1Difference_H_L,day2Difference_H_L),Math.min(day3Difference_H_L,day4Difference_H_L));

	        System.out.println("    minValue1     "+Math.min(day1Difference_H_L,day2Difference_H_L));
	        System.out.println("    minValue2     "+Math.min(day3Difference_H_L,day4Difference_H_L));
	        System.out.println("    minValue3     "+Math.min(Math.min(day1Difference_H_L,day2Difference_H_L),Math.min(day3Difference_H_L,day4Difference_H_L)));

	        int BothSLtriggered=0;
	        int BuySLtriggered=0;
	        int SellSLtriggered=0;

	        if(minValue.equals(day4Difference_H_L)&&(day4Difference_H_L<day3Difference_H_L)) {
	            System.out.println("its a EMP Day");
	            if(day5High>day4High) {
	                System.out.println("buy side trigerred  and catched points "+(day5High-day4High));
	                BothSLtriggered++;
	            }
	            if(day4Low>day5Low) {
	                System.out.println("sell side trigerred  and catched points "+(day4Low-day5Low));
	                BothSLtriggered++;
	            }
	            if(BothSLtriggered==2) {
	                System.out.println("both SL trigerred");
	            }
	        }else {
	            System.out.println("not a EMP day");
	        }


	    }

	}
}
