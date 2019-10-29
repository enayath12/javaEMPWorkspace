package com.tkd.SqlCreation;

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
import com.tkd.SqlCreation.StockDetails;

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
	public static final HashMap<String, Integer> stockDetailsMAP = new HashMap<String, Integer>();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	public static boolean readFileToString(File folder, String zipFileName, String fileName) throws IOException {
		
		/* System.out.println(" GenerateInsertStatement_One_Hour    folder     ::  " + folder.toString()); System.out.println(" zipFileName     ::  " +
		 zipFileName); System.out.println(" fileName     ::  " + FilenameUtils.removeExtension(fileName));*/
		 
		try{
		String stockName = FilenameUtils.removeExtension(fileName);
		boolean tableExists = false;
		int primaryKey = 1;
		
		FilenameUtils.removeExtension(fileName).replace("-F1", "");
		
		String tableName  = StockDetails.getTableName(FilenameUtils.removeExtension(fileName).replace("-F1", "").replace("_F1", ""));
		//System.out.println("   the table name    ::        "+tableName);

		
		primaryKey=StockDetails.getTableMaxId(tableName);
		System.out.println("   the primaryKey      ::        "+primaryKey);
		int stockId=0;
			
			String stockId_String=StockDetails.getStockName_Or_Id(0,tableName);
			System.out.println("   stockId_String    ::        "+stockId_String);
			try{
			stockId=Integer.parseInt(stockId_String);
			}catch(Exception e){
				System.out.println("   the getStockName_Or_Id stockId   "+e);
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
			String absoluteFilePath = sqlDumpFolder + File.separator + tableName + ".sql";
			// System.out.println("absoluteFilePath :: " + absoluteFilePath);
			File file = new File(absoluteFilePath);
			if (file.createNewFile()) {
				//System.out.println(absoluteFilePath+" File Created stockName "+stockName+" tableName "+tableName);
			} else {
				tableExists = true;
				// System.out.println(" already exists stockName "+stockName);
			}

			StringBuilder intoValues = new StringBuilder();
			String allInOne = "";

			if(!tableName.isEmpty()&&tableName!=null&&tableName!=""){
				//System.out.println(" not empty  tableName.isEmpty() stockName  tableName   "+tableName);
			if (!tableExists) {
				headerText = "-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)" + System.lineSeparator() + "--" + System.lineSeparator() + "-- Host: localhost    Database: TKDDATA"
						+ System.lineSeparator() + "-- ------------------------------------------------------" + System.lineSeparator() + "-- Server version	5.7.18-log  " + System.lineSeparator()
						+ "--" + System.lineSeparator() + "-- Table structure for table `" + tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();
			}
			if (primaryKey == 1 && !tableExists) {
			//	System.out.println(" primaryKey == 1 && !tableExist me ");
				dropCreateTable = "CREATE TABLE `" + tableName + "` (" + System.lineSeparator() + " `Id` int(111) NOT NULL AUTO_INCREMENT," + System.lineSeparator() + " `TKDstockId` int(111) NOT NULL,"
						+ System.lineSeparator() + " `stockName` varchar(255) NOT NULL," + System.lineSeparator() + "`TimeFrame`  varchar(255) NOT NULL," + System.lineSeparator()
						+ "`stockDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," + System.lineSeparator() + "`stockOpen` double DEFAULT NULL," + System.lineSeparator()
						+ " `stockHigh` double DEFAULT NULL," + System.lineSeparator() + "`stockLow` double DEFAULT NULL," + System.lineSeparator() + "`stockClose` double DEFAULT NULL,"
						+ System.lineSeparator() + "`stockVolume` double DEFAULT NULL," + System.lineSeparator() + " PRIMARY KEY (`Id`)" + System.lineSeparator() + ")  DEFAULT CHARSET=utf8;"
						+ System.lineSeparator();
			}
			if (!tableExists) {
				dumpingData = "--" + System.lineSeparator() + " -- Dumping data for table `" + tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();

				lockTable = "LOCK TABLES `" + tableName + "` WRITE;" + System.lineSeparator();

				// System.out.println("file :: " +FilenameUtils.removeExtension(fileName));
			}
			if (folder.isFile() && (folder.getName().endsWith(".txt") || folder.getName().endsWith(".TXT") || folder.getName().endsWith(".csv") || folder.getName().endsWith(".CSV"))) {
				intoValues = EMP(folder.toString(), intoValues, stockName, stockId, tableName, primaryKey, tableExists);
			}
			if (!tableExists) {
				String unlockTable = " UNLOCK TABLES;" + System.lineSeparator();
				String dumpComplete = "-- Dump completed on 2019-01-16 13:11:21" + System.lineSeparator();
				allInOne = headerText + System.lineSeparator() + dropCreateTable + System.lineSeparator() + dumpingData + System.lineSeparator() + lockTable + System.lineSeparator() + intoValues
						+ System.lineSeparator() + unlockTable + System.lineSeparator() + dumpComplete;
			}
			if (tableExists) {
				allInOne = System.lineSeparator() + intoValues + System.lineSeparator() + System.lineSeparator() + " UNLOCK TABLES;";
				StockDetails.replaceSelected(" UNLOCK TABLES;", allInOne,absoluteFilePath);
			}

			// System.out.println("statements :: " + statements.size());
			// absolute file name with path
			if (!sqlDumpFolder.isDirectory()) {
				sqlDumpFolder.mkdirs();
			}

			if (!tableExists) {
				statements.add(allInOne);
			    Files.write(Paths.get(absoluteFilePath), statements);
			}
			}else{
				System.out.println("  tableName               "+tableName+"                stockId              "+stockId+"                         stockName           "+stockName);
			}
			
			}catch(Exception e){
				System.out.println(" Exception  error :-:"+e);
			}
			return true;
	}

	public static StringBuilder EMP(String destDirnew, StringBuilder intoValues, String stockName, int stockId, String tableName, int primaryKey, boolean tableExists) throws IOException {

		// System.out.println("destDirnew :: " + destDirnew);

		String line = "";
		String cvsSplitBy = ";";
		String TimeFrame = "1_HOUR";
		intoValues.append("INSERT INTO `" + tableName + "` VALUES ");
		boolean addsemicolon = false;
		boolean onetime = true;
		
		for(Map.Entry<String, Integer> checktableName:stockDetailsMAP.entrySet()){
			if(checktableName.getKey().trim().equalsIgnoreCase(tableName.trim())){
				primaryKey=checktableName.getValue()+1;
			}
		}
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

					String stockDate = StockDetails.getDateTime(country[0], time);
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
					intoValues.append("(" + primaryKey + ",'" + stockId + "','" + stockName + "','" + TimeFrame + "','" + stockDate + "','" + open + "','" + high + "','" + low + "','" + close + "','"
							+ volume + "')");

					addsemicolon = true;
					
					stockDetailsMAP.put(tableName, primaryKey);
					onetime = false;
					primaryKey++;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());	
				}
			}
			intoValues.append(";");
		} catch (IOException e) {
			System.out.println(e.getMessage());	
		}
		return intoValues;

	}

	public static boolean isMultipleof10(int n) {
		while (n > 0)
			n = n - 10;

		if (n == 0)
			return true;

		return false;
	}



}