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
import java.util.Date;
import java.util.HashSet;

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
				//hashSet_StockName = sqlstatements_09_30.getAllStockNames(insertQuery, hashSet_StockName, JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);
				//System.out.println("hashSet_StockName size :" + hashSet_StockName.size());

				for (String tableName : hashSet_StockName) {
					System.out.println("hashSet_StockName tableName :" + tableName);
				}
				
				FiveMinStockData.getFiveMinStockdata("icici_bank", JDBC_DRIVER, DB_URL, USER, PASS, connection, statement);

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
		 public static long getDifferenceDays(Timestamp timeStamp1, Timestamp timeStamp2) {
				
				//	System.out.println("   timeStamp1    ::"+timeStamp1+"    timeStamp2     "+timeStamp2);
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
					
					  if(differenceDays>=1){ 
						  System.out.println(" getDifference  DaystimeStamp1 "+timeStamp1+"   timeStamp2 :"+timeStamp2+" differenceDays :"+differenceDays); 
					  }
					 

					return differenceDays;
				}

		 
	}
