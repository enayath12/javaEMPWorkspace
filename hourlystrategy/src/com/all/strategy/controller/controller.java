package com.all.strategy.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
@WebServlet(description = "My First Servlet", urlPatterns = { "/controller", "/controller.do" }, initParams = { @WebInitParam(name = "id", value = "1"),
		@WebInitParam(name = "name", value = "pankaj") })
public class controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String HTML_START = "<html><body>";
	public static final String HTML_END = "</body></html>";

	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/backtesting";

	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "1234";

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

		
		String insertQuery = "SELECT * FROM ALLSTOCKNAMES";
		hashSet_StockName = sqlstatements_09_30.getAllStockNames(insertQuery, hashSet_StockName, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		System.out.println("hashSet_StockName size :" + hashSet_StockName.size());

		for (String tableName : hashSet_StockName) {
			
			
			
			/*insertQuery = "select * from " + tableName + " where stockDate LIKE '%2017-10%'";
			HashSet<String> hashSet_tableWithIssue = sqlstatements_09_30.getStocksHasDataIssue(insertQuery, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
			for (String entryDate : hashSet_tableWithIssue) {
				System.out.println(" entryDate :: " + entryDate);
			}
			int numberOfDays = 1;*/

		}
		 test_9_30_15min_strategy("ACC", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

		// System.out.println("stockname size :-:"+StockNameAndId.size());
		
		 // hourly strategy 10:15 entry to 15:17 
		 test_10_15_hourly_strategy("ACC", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
		 
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

}
