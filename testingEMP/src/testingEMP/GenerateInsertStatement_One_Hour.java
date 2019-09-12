package testingEMP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

@WebServlet("/GenerateInsertStatement_One_Hour")
public class GenerateInsertStatement_One_Hour extends HttpServlet {

	
	public static File sqlDumpFolder = new File("D:\\Strategy testing environment\\TKD Extracted Files\\SQL one Hour\\");
	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "root";
	public static Connection connection = null;
	public static Statement statement = null;

	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/tkddata";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	public static boolean readFileToString(File folder, String zipFileName, String fileName) throws IOException {
		/*
		 * System.out.println(" folder     ::  " + folder.toString()); System.out.println(" zipFileName     ::  " +
		 * zipFileName); System.out.println(" fileName     ::  " + FilenameUtils.removeExtension(fileName));
		 */

		String stockName = FilenameUtils.removeExtension(fileName);

		String tableName = FilenameUtils.removeExtension(fileName).replace("-F1", "");

		 tableName = StockDetails.getTableName(tableName);
		 if(tableName.isEmpty()||tableName==""||tableName==null){
			 tableName=FilenameUtils.removeExtension(fileName).replace("-F1", "");
		 }
		 int primaryKey = 1;
		 primaryKey=getTableMaxId(tableName);

		if (primaryKey == 0){
			primaryKey = 1;
		}else{
			primaryKey++;
		}
		
		int stockId=0;;
		if(GenerateInsertStatement_One_Day.getStockId(tableName)>0){
			stockId=GenerateInsertStatement_One_Day.getStockId(tableName);
		}
		
		// System.out.println(primaryKey+" tableName     "+tableName+"                       stockName        "+stockName+System.lineSeparator()+System.lineSeparator());
		 List<String> statements = new ArrayList<>();
			String headerText = "";
			String dropCreateTable = "";
			String dumpingData = "";
			String lockTable = "";
			if(!sqlDumpFolder.isDirectory()){
				sqlDumpFolder.mkdirs();
			}
			// here we get all the files in zip
			// Zip file name will be stored along with the data in each row for referenc

			
				// create table with txtFile Name
			   //
				headerText = "-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)" + System.lineSeparator() + "--" + System.lineSeparator() + "-- Host: localhost    Database: TKDDATA"
						+ System.lineSeparator() + "-- ------------------------------------------------------" + System.lineSeparator() + "-- Server version	5.7.18-log  " + System.lineSeparator()
						+ "--" + System.lineSeparator() + "-- Table structure for table `" + tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();
          if(primaryKey==1){
				dropCreateTable =  "CREATE TABLE `" + tableName + "` (" + System.lineSeparator()
						+ " `Id` int(111) NOT NULL AUTO_INCREMENT," + System.lineSeparator() + " `TKDstockId` int(111) NOT NULL," + System.lineSeparator()
						+ " `stockName` varchar(255) NOT NULL," + System.lineSeparator() + "`TimeFrame`  varchar(255) NOT NULL," + System.lineSeparator() + "`stockDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," + System.lineSeparator()
						+ "`stockOpen` double DEFAULT NULL," + System.lineSeparator() + " `stockHigh` double DEFAULT NULL," + System.lineSeparator() + "`stockLow` double DEFAULT NULL,"
						+ System.lineSeparator() + "`stockClose` double DEFAULT NULL," + System.lineSeparator() + "`stockVolume` double DEFAULT NULL," + System.lineSeparator() + " PRIMARY KEY (`Id`)"
						+ System.lineSeparator() + ")  DEFAULT CHARSET=utf8;" + System.lineSeparator();
            }
				dumpingData = "--" + System.lineSeparator() + " -- Dumping data for table `" + tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();

				lockTable = "LOCK TABLES `" + tableName + "` WRITE;" + System.lineSeparator();
				StringBuilder intoValues = new  StringBuilder(); 
				System.out.println("file  ::  " +FilenameUtils.removeExtension(fileName));
				
				if (folder.isFile() && (folder.getName().endsWith(".txt") || folder.getName().endsWith(".TXT") || folder.getName().endsWith(".csv") || folder.getName().endsWith(".CSV"))) {
					 intoValues =EMP(folder.toString(),intoValues,stockName,stockId, tableName, primaryKey);
				}

				String unlockTable = " UNLOCK TABLES;" + System.lineSeparator();
				String dumpComplete = "-- Dump completed on 2019-01-16 13:11:21" + System.lineSeparator();
				String allInOne = headerText + System.lineSeparator() + dropCreateTable + System.lineSeparator() + dumpingData + System.lineSeparator() + lockTable + 
						System.lineSeparator() + intoValues
						+ System.lineSeparator() + unlockTable + System.lineSeparator() + dumpComplete;

				statements.add(allInOne);
				System.out.println("statements  ::  " + statements.size());
				//absolute file name with path
				if(!sqlDumpFolder.isDirectory()){
					sqlDumpFolder.mkdirs();
				}
				
		        String absoluteFilePath =sqlDumpFolder+File.separator+stockName+".sql";
		       
		        File file = new File(absoluteFilePath);
		        System.out.println("statements  file ::  " + file.toString());
		        if(file.createNewFile()){
		            System.out.println(absoluteFilePath+" File Created");
		        }else {
		        	System.out.println("File "+absoluteFilePath+" already exists");
		        }
		        
				Files.write(Paths.get(absoluteFilePath), statements);


		return true;
	}

	public static StringBuilder EMP(String destDirnew, StringBuilder intoValues, String stockName, int stockId, String tableName, int primaryKey) throws IOException {

		// System.out.println("destDirnew :: " + destDirnew);

		String line = "";
		String cvsSplitBy = ";";
		String TimeFrame = "1 hour";
		intoValues.append("INSERT INTO `" + tableName + "` VALUES ");
		boolean addsemicolon = false;

		try (BufferedReader br = new BufferedReader(new FileReader(destDirnew))) {

			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] country = line.split(cvsSplitBy);
				try {
					String openString = country[1].trim();

					String time = "";

					
					String openstockvalue = "";
					if (openString.length() > 4) {
						time = openString.substring(0, 4);
						int total = openString.length() - 4;
						openstockvalue = openString.substring(openString.length() - total).replaceFirst("^0+(?!$)", "");
					}

					Date formatedDate = getDateTime(country[0], time);
					 SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                    String stockDate = newFormat.format(formatedDate);
	                   // System.out.println("stockDate   :"+stockDate);
	                    
	                    
					openString = openstockvalue.replace(",", ".").trim();
					String highString = country[2].replace(",", ".").trim();
					String lowString = country[3].replace(",", ".").trim();
					String closeString = country[4].replace(",", ".").trim();
					String volumeString = country[5].replace(",", ".").trim();
					// System.out.println("date ----- :: " + date + " open :: " + openString
					// + " high :: " +highString+ " low :: " + lowString + " close :: "
					// + closeString + " volume :: " + volumeString);
					float open = Float.valueOf(openString);
					float high = Float.valueOf(highString);
					float low = Float.valueOf(lowString);
					float close = Float.valueOf(closeString);
					float volume = Float.valueOf(volumeString);

					//System.out.println("stockDate      :: " + stockDate + "   open      :: " + open + "      high      :: " + high + "     low      :: " + low + "    close      :: " + close
					//		+ "     volume      :: " + volume);

					if (addsemicolon) {
						intoValues.append(",");
					}
					intoValues.append("(" + primaryKey + ",'" + stockId + "','" + stockName + "','" + TimeFrame + "','" + stockDate + "','" + open + "','" + high + "','" + low + "','" + close
							+ "','" + volume + "')");

					addsemicolon = true;
					primaryKey++;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			intoValues.append(";");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intoValues;

	}

	public static Date getDateTime(String countrydate, String time) throws Exception {

		String hour = "";
		String minute = "";
		if ((time.length() == 4)) {
			hour = time.substring(0, 2);
			int total = time.length() - 2;
			minute = time.substring(time.length() - total);
		}

		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyddMM");
		Date date = new Date();

		date = originalFormat.parse(countrydate);
		//System.out.println(" date " + date);
		SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formatedDate = newFormat.format(date);
		date = newFormat.parse(formatedDate);

		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(date); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // adds four hour
		cal.add(Calendar.MINUTE, Integer.parseInt(minute));
		date = cal.getTime(); // returns new date object

		return date;
	}

	public static boolean isMultipleof10(int n) {
		while (n > 0)
			n = n - 10;

		if (n == 0)
			return true;

		return false;
	}

	public static int getTableMaxId(String tablenme) {

		List<String> HashMap_stockDataOfaDay = new ArrayList<String>();
		int Id = 0;
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			String insertQuery = "SELECT * FROM " + tablenme + " ORDER BY id DESC LIMIT 1";
			ResultSet resultSet = statement.executeQuery(insertQuery);
			System.out.println(" insertQuery  :" + insertQuery);
			if (!resultSet.isAfterLast()) {
				System.out.println(" resultSet has No data ");
			}

			resultSet.afterLast();
			while (resultSet.previous()) {
				Id = resultSet.getInt("Id");
			}

		} catch (Exception e) {
			
		}
		return Id;

	}

	
}