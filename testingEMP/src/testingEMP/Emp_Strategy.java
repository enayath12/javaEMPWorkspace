package testingEMP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;




@WebServlet("/Emp_Strategy")
public class Emp_Strategy extends HttpServlet {
	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "root";
	public static Connection connection = null;
	public static Statement statement = null;
	
	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/tkddata";
	
	
	 public static void main(String[] args) throws FileNotFoundException, IOException {
		 
		 /*try {
				
				String stockName = "";
				String TKDStockId ="";
				String stockDate = "";
				Double stockOpen = 0.0;
				Double stockHigh = 0.0;
				Double stockLow = 0.0;
				Double stockClose =0.0;
				Double stockVolume = 0.0;
				
				Double H_L_diff=0.0;
				Double O_C_diff=0.0;
				
			 List<String> stockDataOfaDay = TestEmp();
			 System.out.println("stockDataOfaDay  "+stockDataOfaDay.size());
			 for (ListIterator<String> iter = stockDataOfaDay.listIterator(); iter.hasNext(); ) {
				    String element = iter.next();
					
						StringBuilder stringBuilder = new StringBuilder(element);
						String[] introduction = stringBuilder.toString().split(",1_min_entry,"); // <-- Do the split here after
		                System.out.println("   introduction :  "+introduction.length);
						for (String entry_map : introduction) {

							String[] each_entry = entry_map.toString().split(",");
							// splits to give each minute data
							for (String entry_data : each_entry) {
								
								
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockName")) {
									stockName = StringUtils.substringAfterLast(entry_data, "=");

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("TKDStockId")) {
									TKDStockId=StringUtils.substringAfterLast(entry_data, "=");

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockDate")) {
									stockDate = StringUtils.substringAfterLast(entry_data, "=");

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockOpen")) {
									stockOpen = Double.parseDouble(StringUtils.substringAfterLast(entry_data, "="));

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockHigh")) {
									stockHigh = Double.parseDouble(StringUtils.substringAfterLast(entry_data, "="));

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockLow")) {
									stockLow = Double.parseDouble(StringUtils.substringAfterLast(entry_data, "="));

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockClose")) {
									stockClose = Double.parseDouble(StringUtils.substringAfterLast(entry_data, "="));

								}
								if (StringUtils.substringBeforeLast(entry_data, "=").equalsIgnoreCase("stockVolume")) {
									stockVolume = Double.parseDouble(StringUtils.substringAfterLast(entry_data, "="));

								}
								
								//System.out.println(" H_L_diff             "+(stockHigh-stockLow)+"  O_C_diff                "+(stockOpen-stockClose));
								H_L_diff=Math.floor(Math.abs(stockHigh-stockLow)* 100) / 100;
								O_C_diff=Math.floor(Math.abs(stockOpen-stockClose)* 100) / 100;
								System.out.println(" H_L_diff "+H_L_diff+"  O_C_diff "+O_C_diff);
								
							}
							
						}
					
			 }
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 

		 List<Integer> listnew = new ArrayList<>();
		 listnew.add(1);
		 listnew.add(2);
		 listnew.add(3);
		 listnew.add(4);
		 listnew.add(5);
		 listnew.add(6);
		 listnew.add(7);
		 listnew.add(8);
		 listnew.add(9);
		 listnew.add(10);
		 int first = 0;
	        int second = 0;
	        int third = 0;

	        for (int i = 0; i < listnew.size(); i++) { 
	        	    first = listnew.get(i);
	        	    second = listnew.get(i+1);;
	                third = listnew.get(i+2);;
	               
	        	System.out.println(" first "+first);
	        	System.out.println(" second "+second);
	        	System.out.println(" third "+third);
	        }
	        
	        
	        
		 
	float day1_High=100;
	float day1_Low=20;
	
	
	float day2_High=90;
	float day2_Low=30;
	
	float day3_High=80;
	float day3_Low=40;
	
	float day3_Open=80;
	float day3_Close=30;
	
	float day4_High=70;
	float day4_Low=50;
	
	
	float day1_H_L_Diff=day1_High-day1_Low;
	float day2_H_L_Diff=day2_High-day2_Low;
	float day3_H_L_Diff=day3_High-day3_Low;
	
	float day3_O_C_Diff=day3_Open-day3_Close;
	
	
	float day4_H_L_Diff=day4_High-day4_Low;
	
	
	System.out.println("day1_H_L_Diff  "+day1_H_L_Diff);
	System.out.println("day1_H_L_Diff  "+day2_H_L_Diff);
	System.out.println("day1_H_L_Diff  "+day3_H_L_Diff);
	System.out.println("day1_H_L_Diff  "+day3_O_C_Diff);
	System.out.println("day1_H_L_Diff  "+day4_H_L_Diff);
	float smallest_O_C =0;
	float smallest_H_L = Math.min(day4_H_L_Diff,Math.min(day1_H_L_Diff, Math.min(day2_H_L_Diff, day3_H_L_Diff)));
	if(smallest_H_L==day4_H_L_Diff){
		 smallest_O_C = Math.min(day3_O_C_Diff, day4_H_L_Diff);	
	}
	System.out.println("smallest_H_L  "+smallest_H_L);

	System.out.println("smallest_O_C  "+smallest_O_C);
	
	if(smallest_O_C==day4_H_L_Diff){
		System.out.println("Yes its a EMP day   ");

	}*/
		 
		 
		 
		 try {
			
			Date formatedDate =GenerateInsertStatement_One_Hour.getDateTime("20100802", "1000");
			 SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               String test = newFormat.format(formatedDate);
               System.out.println("test   :"+test);
               formatedDate = newFormat.parse(test);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			 Date formatedDate =GenerateInsertStatement_One_Hour.getDateTime("20180910", "1100");
				 SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	               String test = newFormat.format(formatedDate);
	               System.out.println("test 1     :"+test);
	               formatedDate = newFormat.parse(test);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	
	 
	 public static List<String> TestEmp(){
		 List<String> HashMap_stockDataOfaDay = new ArrayList<String>();
		 try {
			 
			 Class.forName(JDBC_DRIVER);
			 connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			 String insertQuery = "select * from abb  where stockDate between '2019-04-22 00:00:00' and '2019-07-22 00:00:00'";
					System.out.println(" insertQuery  :"+insertQuery);
					ResultSet resultSet = statement.executeQuery(insertQuery);
					StringBuilder stringBuilder_min_entry = new StringBuilder();
					StringBuilder stringBuilder_day_entry = new StringBuilder();

					if (!resultSet.isAfterLast()) {
						System.out.println(" resultSet has No data ");
					}

					resultSet.afterLast();
					while (resultSet.previous()) {
						int Id = resultSet.getInt("Id");
						String stockName = resultSet.getString("stockName");
						String TKDStockId = resultSet.getString("TKDStockId");
						Timestamp stockDate = resultSet.getTimestamp("stockDate");
						Double stockOpen = resultSet.getDouble("stockOpen");
						Double stockHigh = resultSet.getDouble("stockHigh");
						Double stockLow = resultSet.getDouble("stockLow");
						Double stockClose = resultSet.getDouble("stockClose");
						Double stockVolume = resultSet.getDouble("stockVolume");

						stringBuilder_min_entry.append("Id").append('=').append(Id).append(',');
						stringBuilder_min_entry.append("TKDStockId").append('=').append(TKDStockId).append(',');
						stringBuilder_min_entry.append("stockName").append('=').append(stockName).append(',');
						stringBuilder_min_entry.append("stockDate").append('=').append(stockDate).append(',');
						stringBuilder_min_entry.append("stockOpen").append('=').append(stockOpen).append(',');
						stringBuilder_min_entry.append("stockHigh").append('=').append(stockHigh).append(',');
						stringBuilder_min_entry.append("stockLow").append('=').append(stockLow).append(',');
						stringBuilder_min_entry.append("stockClose").append('=').append(stockClose).append(',');
						stringBuilder_min_entry.append("stockVolume").append('=').append(stockVolume).append(',');
						HashMap_stockDataOfaDay.add(stringBuilder_min_entry.toString());
						stringBuilder_day_entry.append(stringBuilder_min_entry.toString()).append(",1_min_entry,");
						stringBuilder_min_entry = new StringBuilder();
						
						
						// System.out.println("name :" + name);
						
				/*		System.out.println(" Id "+Id+"  TKDStockId  "+TKDStockId+"  name "+name+"   stockDate "+stockDate+"  stockOpen "+stockOpen+"  stockHigh "+stockHigh+"   stockLow  "+stockLow
								+"stockClose"+stockClose);*/

						
					}
					// System.out.println(" HashMap_stockDataOfaDay size :" + HashMap_stockDataOfaDay.size() + "
					// HashSet_stockDataOfaDay size :" + HashSet_stockDataOfaDay.size());

					/*
					 * long ddd = getDifferenceDays(date3, date4); System.out.println(" ddd :" + ddd);
					 */
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		return HashMap_stockDataOfaDay;	
			
	 }

	
}
