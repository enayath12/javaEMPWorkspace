package com.tkd.SqlCreation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.FilenameUtils;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

@WebServlet("/GenerateInsertStatement_Five_Minute")
public class GenerateInsertStatement_Five_Minute extends HttpServlet {

	public static File sqlDumpFolder = new File("D:\\Strategy testing environment\\TKD Extracted Files\\SQL FIVE MIN\\");
	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "root";
	public static Connection connection = null;
	public static Statement statement = null;

	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/tkddata";
	public static final HashMap<String, Integer> stockDetailsMAP = new HashMap<String, Integer>();

	public static boolean readFileToString(File folder, String zipFileName, String fileName) throws IOException {

		fileName = FilenameUtils.removeExtension(fileName);
		int stockId = Integer.parseInt(fileName);
		boolean tableExists = false;
		String stockName = StockDetails.getStockName_Or_Id(stockId, "");
		String tableName = StockDetails.getTableName(stockName.replace("-F1", "").replace("_F1", ""));
		
		// System.out.println(" stockName "+stockName+" tableName "+tableName+" TKDstockId "+TKDstockId);

		int primaryKey = 1;
		primaryKey = StockDetails.getTableMaxId(tableName);

		// System.out.println(primaryKey+" tableName "+tableName+" stockName "+stockName+System.lineSeparator());
		List<String> statements = new ArrayList<>();
		String headerText = "";
		String dropCreateTable = "";
		String dumpingData = "";
		String lockTable = "";
		if (!sqlDumpFolder.isDirectory()) {
			sqlDumpFolder.mkdirs();
		}

		String absoluteFilePath = sqlDumpFolder + File.separator + tableName + ".sql";
		// System.out.println("absoluteFilePath :: " + absoluteFilePath);
		File file = new File(absoluteFilePath);
		if (file.createNewFile()) {
			// System.out.println(absoluteFilePath+" File Created stockName "+stockName+" tableName "+tableName);
		} else {
			tableExists = true;
			// System.out.println(" already exists stockName "+stockName);
		}

		StringBuilder intoValues = new StringBuilder();
		String allInOne = "";
		// here we get all the files in zip

		// Zip file name will be stored along with the data in each row for referenc

		// System.out.println(" stockName :: " +stockName +" tableName "+tableName+" TKDstockId "+TKDstockId+"
		// primaryKey "+primaryKey);

		// create table with txtFile Name
		//
		if (!tableExists) {
			headerText = "-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)" + System.lineSeparator() + "--" + System.lineSeparator() + "-- Host: localhost    Database: TKDDATA"
					+ System.lineSeparator() + "-- ------------------------------------------------------" + System.lineSeparator() + "-- Server version	5.7.18-log  " + System.lineSeparator()
					+ "--" + System.lineSeparator() + "-- Table structure for table `" + tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();
		}
		if (primaryKey == 1 && !tableExists) {
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
			// replaceSelected(" UNLOCK TABLES;", allInOne,absoluteFilePath);
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

		return true;
	}

	public static void replaceSelected(String oldChar, String newChar, String absoluteFilePath) {
		try {
			// input the file content to the StringBuffer "input"
			BufferedReader file = new BufferedReader(new FileReader(absoluteFilePath));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			file.close();
			String inputStr = inputBuffer.toString();

			// System.out.println(inputStr); // display the original file for debugging

			// logic to replace lines in the string (could use regex here to be generic)
			inputStr = inputStr.replace(oldChar, newChar);

			// display the new file for debugging
			// System.out.println("----------------------------------\n" + inputStr);

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream(absoluteFilePath);
			fileOut.write(inputStr.getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file."+e.getMessage());
		}
	}

	public static StringBuilder EMP(String destDirnew, StringBuilder intoValues, String stockName, int stockId, String tableName, int primaryKey, boolean tableExists) throws IOException {

		// System.out.println("destDirnew :: " + destDirnew);

		String line = "";
		String cvsSplitBy = ";";
		String TimeFrame = "5_MIN";
		intoValues.append("INSERT INTO `" + tableName + "` VALUES");
		boolean addsemicolon = false;
		boolean onetime = true;

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
					//System.out.println(country[0] + "   stockDate :" + stockDate);
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

					// System.out.println("stockDate :: " + stockDate + " open :: " + open + " high :: " + high + " low
					// :: " + low + " close :: " + close
					// + " volume :: " + volume);

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
