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

@WebServlet("/GenerateInsertStatement_Five_Minute")
public class GenerateInsertStatement_Five_Minute extends HttpServlet {

	public static File sqlDumpFolder = new File("D:\\Strategy testing environment\\TKD Extracted Files\\TKD A-Z SQL FIVE MIN\\");
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

		fileName = FilenameUtils.removeExtension(fileName);
		int TKDstockId = Integer.parseInt(fileName);

		String stockName = fiveminuteStockName(TKDstockId);
		stockName = stockName.replaceAll("_F1", "-F1");
		String tableName = tableNameByStockId(TKDstockId);

		if (tableName.isEmpty() || tableName == "" || tableName == null) {
			tableName = FilenameUtils.removeExtension(fileName).replace("-F1", "");
		}
		int primaryKey = 1;
		primaryKey = getTableMaxId(tableName);

		if (primaryKey == 0) {
			primaryKey = 1;
		} else {
			primaryKey++;
		}

		/*
		 * // System.out.println(primaryKey+" tableName     "+tableName+"                       stockName        "
		 * +stockName+System.lineSeparator()+System.lineSeparator()); List<String> statements = new ArrayList<>();
		 * String headerText = ""; String dropCreateTable = ""; String dumpingData = ""; String lockTable = "";
		 * if(!sqlDumpFolder.isDirectory()){ sqlDumpFolder.mkdirs(); } // here we get all the files in zip // Zip file
		 * name will be stored along with the data in each row for referenc
		 * 
		 * 
		 * // create table with txtFile Name // headerText = "-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)" +
		 * System.lineSeparator() + "--" + System.lineSeparator() + "-- Host: localhost    Database: TKDDATA" +
		 * System.lineSeparator() + "-- ------------------------------------------------------" + System.lineSeparator()
		 * + "-- Server version	5.7.18-log  " + System.lineSeparator() + "--" + System.lineSeparator() +
		 * "-- Table structure for table `" + tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();
		 * if(primaryKey==1){ dropCreateTable = "CREATE TABLE `" + tableName + "` (" + System.lineSeparator() +
		 * " `Id` int(111) NOT NULL AUTO_INCREMENT," + System.lineSeparator() + " `TKDstockId` int(111) NOT NULL," +
		 * System.lineSeparator() + " `stockName` varchar(255) NOT NULL," + System.lineSeparator() +
		 * "`TimeFrame`  varchar(255) NOT NULL," + System.lineSeparator() +
		 * "`stockDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," + System.lineSeparator() +
		 * "`stockOpen` double DEFAULT NULL," + System.lineSeparator() + " `stockHigh` double DEFAULT NULL," +
		 * System.lineSeparator() + "`stockLow` double DEFAULT NULL," + System.lineSeparator() +
		 * "`stockClose` double DEFAULT NULL," + System.lineSeparator() + "`stockVolume` double DEFAULT NULL," +
		 * System.lineSeparator() + " PRIMARY KEY (`Id`)" + System.lineSeparator() + ")  DEFAULT CHARSET=utf8;" +
		 * System.lineSeparator(); } dumpingData = "--" + System.lineSeparator() + " -- Dumping data for table `" +
		 * tableName + "`" + System.lineSeparator() + "--" + System.lineSeparator();
		 * 
		 * lockTable = "LOCK TABLES `" + tableName + "` WRITE;" + System.lineSeparator(); StringBuilder intoValues = new
		 * StringBuilder(); System.out.println("file  ::  " +FilenameUtils.removeExtension(fileName));
		 * 
		 * if (folder.isFile() && (folder.getName().endsWith(".txt") || folder.getName().endsWith(".TXT") ||
		 * folder.getName().endsWith(".csv") || folder.getName().endsWith(".CSV"))) { intoValues
		 * =EMP(folder.toString(),intoValues,stockName,TKDstockId, tableName, primaryKey); }
		 * 
		 * String unlockTable = " UNLOCK TABLES;" + System.lineSeparator(); String dumpComplete =
		 * "-- Dump completed on 2019-01-16 13:11:21" + System.lineSeparator(); String allInOne = headerText +
		 * System.lineSeparator() + dropCreateTable + System.lineSeparator() + dumpingData + System.lineSeparator() +
		 * lockTable + System.lineSeparator() + intoValues + System.lineSeparator() + unlockTable +
		 * System.lineSeparator() + dumpComplete;
		 * 
		 * statements.add(allInOne); //System.out.println("statements  ::  " + statements.size()); //absolute file name
		 * with path if(!sqlDumpFolder.isDirectory()){ sqlDumpFolder.mkdirs(); }
		 * 
		 * String absoluteFilePath =sqlDumpFolder+File.separator+stockName+".sql";
		 * 
		 * File file = new File(absoluteFilePath); System.out.println("statements  file ::  " + file.toString());
		 * if(file.createNewFile()){ System.out.println(absoluteFilePath+" File Created"); }else {
		 * System.out.println("File "+absoluteFilePath+" already exists"); }
		 * 
		 * Files.write(Paths.get(absoluteFilePath), statements);
		 */

		return true;
	}

	private static int getStockId(String tableName) {
		// TODO Auto-generated method stub
		return 0;
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
					// System.out.println("stockDate :"+stockDate);

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
		// System.out.println(" date " + date);
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
			// System.out.println(" insertQuery :" + insertQuery);
			if (!resultSet.isAfterLast()) {
				// System.out.println(" resultSet has No data ");
			}

			resultSet.afterLast();
			while (resultSet.previous()) {
				Id = resultSet.getInt("Id");
			}

		} catch (Exception e) {

		}
		return Id;

	}

	public static String tableNameByStockId(Integer stockCode) {

		String stockName = "";

		HashMap<Integer, String> map = new HashMap<Integer, String>();

		map.put(9730, "HINDUSTAN_ZINC_L");
		map.put(9745, "REC_LTD_F1");
		map.put(9746, "BAJAJ_AUTO_LTD_F1");
		map.put(9750, "ASIAN_PAINTS_LIM");
		map.put(9755, "DISH_TV_INDIA_LT");
		map.put(9770, "MRF_LTD_F1");
		map.put(9787, "ADANI_ENTERPRISE");
		map.put(9788, "ADANIPOWER_LTD_F1");
		map.put(9789, "APOLLO_TYRES_LTD");
		map.put(9794, "HEXAWARE_LTD_F1");
		map.put(9796, "MUNDRA_PORT_SE");
		map.put(9799, "SHRIRAM_TRANSPOR");
		map.put(9802, "EXIDE_INDUSTRIES");
		map.put(9804, "INDRAPRASTHA_GAS");
		map.put(9808, "NMDC_LTD_F1");
		map.put(9812, "TATA_MOTORS_LTD");
		map.put(9816, "JUBLFOOD_LTD_F1");
		map.put(9820, "COALINDIA_F1");
		map.put(9821, "APOLLOHOSP_F1");
		map.put(9822, "GLENMARK_F1");
		map.put(9823, "JUSTDIAL_F1");
		map.put(9824, "L_TFH_F1");
		map.put(9825, "M_MFIN_F1");
		map.put(9826, "UBL_F1");
		map.put(9827, "EICHERMOT_F1");
		map.put(9828, "MINDTREE_F1");
		map.put(9829, "MOTHERSUMI_F1");
		map.put(9830, "TVSMOTOR_F1");
		map.put(9834, "AMARAJABAT_F1");
		map.put(9835, "BAJFINANCE_F1");
		map.put(9836, "BEL_F1");
		map.put(9837, "BOSCHLTD_F1");
		map.put(9838, "BRITANNIA_F1");
		map.put(9839, "CASTROLIND_F1");
		map.put(9841, "DHFL_F1");
		map.put(9842, "ENGINERSIN_F1");
		map.put(9843, "IBULHSGFIN_F1");
		map.put(9845, "OIL_F1");
		map.put(9846, "PAGEIND_F1");
		map.put(9847, "PIDILITIND_F1");
		map.put(9848, "SKSMICRO_F1");
		map.put(9850, "SRF_F1");
		map.put(9851, "STAR_F1");
		map.put(9500, "ACC_F1");
		map.put(9502, "Arvind_Mills_F1");
		map.put(9503, "Bank_Of_India_Fu");
		map.put(9504, "Bhel_F1");
		map.put(9505, "BPCL_F1");
		map.put(9506, "Canara_Bank_F1");
		map.put(9507, "Century_Textiles");
		map.put(9508, "CIPLA_F1");
		map.put(9509, "AMBUJA_CEMENTS_L");
		map.put(9510, "HDFC_Bank_F1");
		map.put(9511, "Herohonda_F1");
		map.put(9000, "ACC");
		map.put(9512, "HPCL_F1");
		map.put(9001, "Bank_Of_Baroda_F1");
		map.put(9513, "ICICI_Bank_F1");
		map.put(9514, "Infosys_F1");
		map.put(9003, "BPCL");
		map.put(9515, "ITC_F1");
		map.put(9004, "Canara_Bank");
		map.put(9517, "M_M_F1");
		map.put(9006, "Dhampur_Sugar_F1");
		map.put(9518, "NTPC_F1");
		map.put(9519, "ONGC_F1");
		map.put(9007, "GAIL");
		map.put(9520, "Reliance_Capital");
		map.put(9521, "Reliance_F1");
		map.put(9009, "HCL_Tech");
		map.put(9010, "Hindustan_Oil_Ex");
		map.put(9523, "SBI_F1");
		map.put(9011, "HDFC_Bank");
		map.put(9012, "Herohonda");
		map.put(9525, "TATAMOTORS_F1");
		map.put(9013, "HPCL");
		map.put(9014, "Infosys");
		map.put(9526, "Tata_Power_F1");
		map.put(9527, "TCS_F1");
		map.put(9016, "ITC");
		map.put(9528, "TATASTEEL_F1");
		map.put(9017, "ICICI_Bank");
		map.put(9529, "Union_Bank_F1");
		map.put(9019, "Jindal_Stainless");
		map.put(9531, "MARUTI_SUZUKI_IN");
		map.put(9532, "Nifty_F1");
		map.put(9533, "CNXIT_F1");
		map.put(9022, "M_M");
		map.put(9535, "Wipro_F1");
		map.put(9024, "ONGC");
		map.put(9025, "Oriental_Bank_Of");
		map.put(9538, "AXIS_Bank_F1");
		map.put(9540, "Titan_Inds_F1");
		map.put(9541, "TATAGLOBAL_F1");
		map.put(9029, "Reliance");
		map.put(9542, "Tata_Chem_F1");
		map.put(9544, "Sun_Pharma_F1");
		map.put(9032, "SBI");
		map.put(9033, "TATAMOTORS");
		map.put(9034, "Tata_Power");
		map.put(9035, "TCS");
		map.put(9036, "Titan");
		map.put(9548, "Siemens_F1");
		map.put(9037, "TATASTEEL");
		map.put(9038, "TATACOMM");
		map.put(9039, "Wipro");
		map.put(9553, "Punjab_National");
		map.put(9042, "ata_India");
		map.put(9043, "Escorts");
		map.put(9044, "Mc_Dowell");
		map.put(9047, "esa_Goa");
		map.put(9051, "Eveready");
		map.put(9052, "Flex_Ind");
		map.put(9054, "Mercator_Lines_L");
		map.put(9566, "LIC_Housing_Fin");
		map.put(9055, "Mid_Day_Multimed");
		map.put(9056, "Nagarjuna_Const");
		map.put(9569, "Jindal_Steel_F1");
		map.put(9059, "Larsen_Toubra");
		map.put(9060, "India_Cem");
		map.put(9061, "Balrampur_Chini");
		map.put(9573, "IOC_F1");
		map.put(9574, "OFSS_F1");
		map.put(9062, "Steel_Authority");
		map.put(9065, "Bombay_Dyeing_F1");
		map.put(9577, "IDBI_F1");
		map.put(9578, "HINDUSTAN_UNILEV");
		map.put(9066, "Voltas");
		map.put(9579, "Hindalco_F1");
		map.put(9068, "Global_Tele");
		map.put(9580, "Grasim_F1");
		map.put(9069, "Unitech_Ltd");
		map.put(9584, "Gail_F1");
		map.put(9073, "Indiabulls_Real");
		map.put(9585, "Federal_Bank_F1");
		map.put(9075, "CIPLA");
		map.put(9588, "Dr_Reddy_F1");
		map.put(9076, "RELIANCE_INFRAST");
		map.put(9589, "Divis_Lab_F1");
		map.put(9077, "BHEL");
		map.put(9078, "DLF");
		map.put(9590, "Dabur_F1");
		map.put(9079, "COAL_INDIA_LTD_F1");
		map.put(9080, "AXIS_BANK");
		map.put(9593, "Colgate_F1");
		map.put(9082, "GRASIM");
		map.put(9083, "KOTAK_BANK");
		map.put(9084, "JSW_STEEL");
		map.put(9085, "AMBUJACEM");
		map.put(9597, "CESC_F1");
		map.put(9086, "BAJAJ_AUTO");
		map.put(9087, "DRREDDY");
		map.put(9600, "BHARTI_AIRTEL_LI");
		map.put(9088, "HINDALCO");
		map.put(9089, "HINDUNILVR");
		map.put(9602, "Bank_of_Baroda_F1");
		map.put(9090, "HDFC");
		map.put(9091, "JINDALSTEL");
		map.put(9092, "NTPC");
		map.put(9604, "Aurobindo_Pharma");
		map.put(9093, "POWERGRID");
		map.put(9605, "Ashok_Ley_F1");
		map.put(9095, "SIEMENS");
		map.put(9097, "SUNPHARMA");
		map.put(9098, "LICHSGFIN");
		map.put(9100, "JUBLFOOD");
		map.put(9613, "Indusind_Bank_Fu");
		map.put(9101, "ALBK");
		map.put(9102, "BANKINDIA");
		map.put(9103, "FINANTECH");
		map.put(9104, "ASIANPAINT");
		map.put(9616, "HDFC_F1");
		map.put(9617, "HCLTech_F1");
		map.put(9105, "INDUSINDBK");
		map.put(9618, "Bharat_Forg_F1");
		map.put(9106, "YESBANK");
		map.put(9619, "Bank_Nifty");
		map.put(9107, "HDIL");
		map.put(9108, "KTKBANK");
		map.put(9109, "PANTALOONR");
		map.put(9621, "SUN_TV_NETWORK_L");
		map.put(9110, "IDFC");
		map.put(9111, "RECLTD");
		map.put(9112, "PFC");
		map.put(9113, "IDBI");
		map.put(9114, "INDIANB");
		map.put(9626, "Gmrinfra_F1");
		map.put(9627, "Larsen_Toubro");
		map.put(9115, "TATAGLOBAL");
		map.put(9116, "JYOTHYLAB");
		map.put(9117, "HITACHIHOM");
		map.put(9629, "Steel_Authority");
		map.put(9630, "ZEE_ENTERTAINMEN");
		map.put(9118, "HAVELLS");
		map.put(9121, "LINDEINDIA");
		map.put(9122, "DIVISLAB");
		map.put(9123, "AUROPHARMA_F1");
		map.put(9124, "ABAN");
		map.put(9125, "DABUR");
		map.put(9126, "CESC");
		map.put(9638, "BATA_INDIA_F1");
		map.put(9128, "IDEA");
		map.put(9130, "CROMPGREAV_F1");
		map.put(9131, "BHARATFORG_F1");
		map.put(9132, "BIOCON");
		map.put(9133, "UPL");
		map.put(9135, "HEXAWARE");
		map.put(9648, "JSW_STEEL_F1");
		map.put(9136, "APOLLOTYRE_F1");
		map.put(9649, "KOTAK_MAHINDRA_B");
		map.put(9137, "ABB");
		map.put(9650, "LUPIN_LTD_F1");
		map.put(9138, "ADANIENT");
		map.put(9139, "ADANIPORTS_F1");
		map.put(9651, "UNITED_SPIRITS_F1");
		map.put(9141, "WOCKPHARMA_F1");
		map.put(9142, "TECHM");
		map.put(9655, "SESA_GOA_F1");
		map.put(9143, "TIMETECHNO_F1");
		map.put(9144, "TIMKEN");
		map.put(9145, "WELCORP");
		map.put(9658, "ULTRATECH_CEMENT");
		map.put(9146, "DELTACORP_F1");
		map.put(9659, "VOLTAS_LTD_F1");
		map.put(9148, "BEML");
		map.put(9149, "MOTHERSUMI_F1");
		map.put(9150, "KPIT");
		map.put(9663, "CNXIT_F1");
		map.put(9151, "CRISIL");
		map.put(9152, "BIRLACORPN_F1");
		map.put(9664, "Nifty");
		map.put(9665, "PFC_F1");
		map.put(9153, "IOC");
		map.put(9154, "ASHOKLEY");
		map.put(9666, "IDEA_F1");
		map.put(9155, "COLGATE");
		map.put(9156, "TATAELXSI_F1");
		map.put(9157, "PVR");
		map.put(9158, "EROSMEDIA");
		map.put(9690, "PETRONET_F1");
		map.put(9698, "UNIPHOS_F1");
		map.put(9703, "DLF_LTD_F1");
		map.put(9712, "BIOCON_LIMITED_F1");
		map.put(9714, "HAVELLS_INDIA_LI");
		map.put(9719, "TECHMAHINDRA_LTD");
		map.put(9722, "YESBANK_LTD_F1");
		map.put(9723, "POWERGRID_F1");

		for (Map.Entry<Integer, String> stckNm : map.entrySet()) {
			// System.out.println(stckNm.getKey() + " :: value : " +stckNm.getValue() );

			if (stockCode.equals(stckNm.getKey())) {
				String stockNmTemp = stckNm.getValue().replaceAll("_F1", "");
				stockName = getTableName(stockNmTemp);
				if (!stockName.isEmpty()) {
					// System.out.println(" stockName "+stockName+ " stckNm.getValue() "+stckNm.getValue());
				} else {
					System.out.println(stckNm.getKey());
				}
			}
		}
		return stockName;
	}

	public static String fiveminuteStockName(Integer stockCode) {

		String stockName = "";
		HashMap<Integer, String> map = new HashMap<Integer, String>();

		map.put(9730, "HINDUSTAN_ZINC_L");
		map.put(9745, "REC_LTD_F1");
		map.put(9746, "BAJAJ_AUTO_LTD_F1");
		map.put(9750, "ASIAN_PAINTS_LIM");
		map.put(9755, "DISH_TV_INDIA_LT");
		map.put(9770, "MRF_LTD_F1");
		map.put(9787, "ADANI_ENTERPRISE");
		map.put(9788, "ADANIPOWER_LTD_F1");
		map.put(9789, "APOLLO_TYRES_LTD");
		map.put(9794, "HEXAWARE_LTD_F1");
		map.put(9796, "MUNDRA_PORT_SE");
		map.put(9799, "SHRIRAM_TRANSPOR");
		map.put(9802, "EXIDE_INDUSTRIES");
		map.put(9804, "INDRAPRASTHA_GAS");
		map.put(9808, "NMDC_LTD_F1");
		map.put(9812, "TATA_MOTORS_LTD");
		map.put(9816, "JUBLFOOD_LTD_F1");
		map.put(9820, "COALINDIA_F1");
		map.put(9821, "APOLLOHOSP_F1");
		map.put(9822, "GLENMARK_F1");
		map.put(9823, "JUSTDIAL_F1");
		map.put(9824, "L_TFH_F1");
		map.put(9825, "M_MFIN_F1");
		map.put(9826, "UBL_F1");
		map.put(9827, "EICHERMOT_F1");
		map.put(9828, "MINDTREE_F1");
		map.put(9829, "MOTHERSUMI_F1");
		map.put(9830, "TVSMOTOR_F1");
		map.put(9834, "AMARAJABAT_F1");
		map.put(9835, "BAJFINANCE_F1");
		map.put(9836, "BEL_F1");
		map.put(9837, "BOSCHLTD_F1");
		map.put(9838, "BRITANNIA_F1");
		map.put(9839, "CASTROLIND_F1");
		map.put(9841, "DHFL_F1");
		map.put(9842, "ENGINERSIN_F1");
		map.put(9843, "IBULHSGFIN_F1");
		map.put(9845, "OIL_F1");
		map.put(9846, "PAGEIND_F1");
		map.put(9847, "PIDILITIND_F1");
		map.put(9848, "SKSMICRO_F1");
		map.put(9850, "SRF_F1");
		map.put(9851, "STAR_F1");
		map.put(9500, "ACC_F1");
		map.put(9502, "Arvind_Mills_F1");
		map.put(9503, "Bank_Of_India_Fu");
		map.put(9504, "Bhel_F1");
		map.put(9505, "BPCL_F1");
		map.put(9506, "Canara_Bank_F1");
		map.put(9507, "Century_Textiles");
		map.put(9508, "CIPLA_F1");
		map.put(9509, "AMBUJA_CEMENTS_L");
		map.put(9510, "HDFC_Bank_F1");
		map.put(9511, "Herohonda_F1");
		map.put(9000, "ACC");
		map.put(9512, "HPCL_F1");
		map.put(9001, "Bank_Of_Baroda_F1");
		map.put(9513, "ICICI_Bank_F1");
		map.put(9514, "Infosys_F1");
		map.put(9003, "BPCL");
		map.put(9515, "ITC_F1");
		map.put(9004, "Canara_Bank");
		map.put(9517, "M_M_F1");
		map.put(9006, "Dhampur_Sugar_F1");
		map.put(9518, "NTPC_F1");
		map.put(9519, "ONGC_F1");
		map.put(9007, "GAIL");
		map.put(9520, "Reliance_Capital");
		map.put(9521, "Reliance_F1");
		map.put(9009, "HCL_Tech");
		map.put(9010, "Hindustan_Oil_Ex");
		map.put(9523, "SBI_F1");
		map.put(9011, "HDFC_Bank");
		map.put(9012, "Herohonda");
		map.put(9525, "TATAMOTORS_F1");
		map.put(9013, "HPCL");
		map.put(9014, "Infosys");
		map.put(9526, "Tata_Power_F1");
		map.put(9527, "TCS_F1");
		map.put(9016, "ITC");
		map.put(9528, "TATASTEEL_F1");
		map.put(9017, "ICICI_Bank");
		map.put(9529, "Union_Bank_F1");
		map.put(9019, "Jindal_Stainless");
		map.put(9531, "MARUTI_SUZUKI_IN");
		map.put(9532, "Nifty_F1");
		map.put(9533, "CNXIT_F1");
		map.put(9022, "M_M");
		map.put(9535, "Wipro_F1");
		map.put(9024, "ONGC");
		map.put(9025, "Oriental_Bank_Of");
		map.put(9538, "AXIS_Bank_F1");
		map.put(9540, "Titan_Inds_F1");
		map.put(9541, "TATAGLOBAL_F1");
		map.put(9029, "Reliance");
		map.put(9542, "Tata_Chem_F1");
		map.put(9544, "Sun_Pharma_F1");
		map.put(9032, "SBI");
		map.put(9033, "TATAMOTORS");
		map.put(9034, "Tata_Power");
		map.put(9035, "TCS");
		map.put(9036, "Titan");
		map.put(9548, "Siemens_F1");
		map.put(9037, "TATASTEEL");
		map.put(9038, "TATACOMM");
		map.put(9039, "Wipro");
		map.put(9553, "Punjab_National");
		map.put(9042, "ata_India");
		map.put(9043, "Escorts");
		map.put(9044, "Mc_Dowell");
		map.put(9047, "esa_Goa");
		map.put(9051, "Eveready");
		map.put(9052, "Flex_Ind");
		map.put(9054, "Mercator_Lines_L");
		map.put(9566, "LIC_Housing_Fin");
		map.put(9055, "Mid_Day_Multimed");
		map.put(9056, "Nagarjuna_Const");
		map.put(9569, "Jindal_Steel_F1");
		map.put(9059, "Larsen_Toubra");
		map.put(9060, "India_Cem");
		map.put(9061, "Balrampur_Chini");
		map.put(9573, "IOC_F1");
		map.put(9574, "OFSS_F1");
		map.put(9062, "Steel_Authority");
		map.put(9065, "Bombay_Dyeing_F1");
		map.put(9577, "IDBI_F1");
		map.put(9578, "HINDUSTAN_UNILEV");
		map.put(9066, "Voltas");
		map.put(9579, "Hindalco_F1");
		map.put(9068, "Global_Tele");
		map.put(9580, "Grasim_F1");
		map.put(9069, "Unitech_Ltd");
		map.put(9584, "Gail_F1");
		map.put(9073, "Indiabulls_Real");
		map.put(9585, "Federal_Bank_F1");
		map.put(9075, "CIPLA");
		map.put(9588, "Dr_Reddy_F1");
		map.put(9076, "RELIANCE_INFRAST");
		map.put(9589, "Divis_Lab_F1");
		map.put(9077, "BHEL");
		map.put(9078, "DLF");
		map.put(9590, "Dabur_F1");
		map.put(9079, "COAL_INDIA_LTD_F1");
		map.put(9080, "AXIS_BANK");
		map.put(9593, "Colgate_F1");
		map.put(9082, "GRASIM");
		map.put(9083, "KOTAK_BANK");
		map.put(9084, "JSW_STEEL");
		map.put(9085, "AMBUJACEM");
		map.put(9597, "CESC_F1");
		map.put(9086, "BAJAJ_AUTO");
		map.put(9087, "DRREDDY");
		map.put(9600, "BHARTI_AIRTEL_LI");
		map.put(9088, "HINDALCO");
		map.put(9089, "HINDUNILVR");
		map.put(9602, "Bank_of_Baroda_F1");
		map.put(9090, "HDFC");
		map.put(9091, "JINDALSTEL");
		map.put(9092, "NTPC");
		map.put(9604, "Aurobindo_Pharma");
		map.put(9093, "POWERGRID");
		map.put(9605, "Ashok_Ley_F1");
		map.put(9095, "SIEMENS");
		map.put(9097, "SUNPHARMA");
		map.put(9098, "LICHSGFIN");
		map.put(9100, "JUBLFOOD");
		map.put(9613, "Indusind_Bank_Fu");
		map.put(9101, "ALBK");
		map.put(9102, "BANKINDIA");
		map.put(9103, "FINANTECH");
		map.put(9104, "ASIANPAINT");
		map.put(9616, "HDFC_F1");
		map.put(9617, "HCLTech_F1");
		map.put(9105, "INDUSINDBK");
		map.put(9618, "Bharat_Forg_F1");
		map.put(9106, "YESBANK");
		map.put(9619, "Bank_Nifty");
		map.put(9107, "HDIL");
		map.put(9108, "KTKBANK");
		map.put(9109, "PANTALOONR");
		map.put(9621, "SUN_TV_NETWORK_L");
		map.put(9110, "IDFC");
		map.put(9111, "RECLTD");
		map.put(9112, "PFC");
		map.put(9113, "IDBI");
		map.put(9114, "INDIANB");
		map.put(9626, "Gmrinfra_F1");
		map.put(9627, "Larsen_Toubro");
		map.put(9115, "TATAGLOBAL");
		map.put(9116, "JYOTHYLAB");
		map.put(9117, "HITACHIHOM");
		map.put(9629, "Steel_Authority");
		map.put(9630, "ZEE_ENTERTAINMEN");
		map.put(9118, "HAVELLS");
		map.put(9121, "LINDEINDIA");
		map.put(9122, "DIVISLAB");
		map.put(9123, "AUROPHARMA_F1");
		map.put(9124, "ABAN");
		map.put(9125, "DABUR");
		map.put(9126, "CESC");
		map.put(9638, "BATA_INDIA_F1");
		map.put(9128, "IDEA");
		map.put(9130, "CROMPGREAV_F1");
		map.put(9131, "BHARATFORG_F1");
		map.put(9132, "BIOCON");
		map.put(9133, "UPL");
		map.put(9135, "HEXAWARE");
		map.put(9648, "JSW_STEEL_F1");
		map.put(9136, "APOLLOTYRE_F1");
		map.put(9649, "KOTAK_MAHINDRA_B");
		map.put(9137, "ABB");
		map.put(9650, "LUPIN_LTD_F1");
		map.put(9138, "ADANIENT");
		map.put(9139, "ADANIPORTS_F1");
		map.put(9651, "UNITED_SPIRITS_F1");
		map.put(9141, "WOCKPHARMA_F1");
		map.put(9142, "TECHM");
		map.put(9655, "SESA_GOA_F1");
		map.put(9143, "TIMETECHNO_F1");
		map.put(9144, "TIMKEN");
		map.put(9145, "WELCORP");
		map.put(9658, "ULTRATECH_CEMENT");
		map.put(9146, "DELTACORP_F1");
		map.put(9659, "VOLTAS_LTD_F1");
		map.put(9148, "BEML");
		map.put(9149, "MOTHERSUMI_F1");
		map.put(9150, "KPIT");
		map.put(9663, "CNXIT_F1");
		map.put(9151, "CRISIL");
		map.put(9152, "BIRLACORPN_F1");
		map.put(9664, "Nifty");
		map.put(9665, "PFC_F1");
		map.put(9153, "IOC");
		map.put(9154, "ASHOKLEY");
		map.put(9666, "IDEA_F1");
		map.put(9155, "COLGATE");
		map.put(9156, "TATAELXSI_F1");
		map.put(9157, "PVR");
		map.put(9158, "EROSMEDIA");
		map.put(9690, "PETRONET_F1");
		map.put(9698, "UNIPHOS_F1");
		map.put(9703, "DLF_LTD_F1");
		map.put(9712, "BIOCON_LIMITED_F1");
		map.put(9714, "HAVELLS_INDIA_LI");
		map.put(9719, "TECHMAHINDRA_LTD");
		map.put(9722, "YESBANK_LTD_F1");
		map.put(9723, "POWERGRID_F1");

		for (Map.Entry<Integer, String> stckNm : map.entrySet()) {
			// System.out.println(stckNm.getKey() + " :: value : " +stckNm.getValue() );

			if (stockCode.equals(stckNm.getKey())) {

				stockName = stckNm.getValue();
			}
		}
		return stockName;
	}

	public static String getTableName(String stockNm) {
		String result = "";
		List<String> stockName = new ArrayList<String>();
		stockName.add("FDC_LTD");
		stockName.add("GUJARAT_SIDHEE_C");
		stockName.add("VISAGAR_POLYTEX");
		stockName.add("VIMAL_O_F_LTD");
		stockName.add("GARWARE_WALL_RO");
		stockName.add("USTDIAL_LTD");
		stockName.add("GM_BREWERIES");
		stockName.add("ATUL_AUTO_LIMITE");
		stockName.add("HIND_SANITARY");
		stockName.add("CEREBRA_INT_TECH");
		stockName.add("IMP_POWER");
		stockName.add("ORIENT_CEMENT_LT");
		stockName.add("BHARTI_INFRATEL");
		stockName.add("VMART_RETAIL_LTD");
		stockName.add("SHIRPUR_GOLD_REF");
		stockName.add("MOHIT_INDUSTRIES");
		stockName.add("ANKIT_MET_POW");
		stockName.add("DECCAN_CEMENTS");
		stockName.add("REPCO_HOME_FINAN");
		stockName.add("DYNAMATIC_TECHN");
		stockName.add("NRB_INDUS_BEARI");
		stockName.add("LINDE_INDIA_LIMI");
		stockName.add("ANSAL_PROPERTIES");
		stockName.add("HIL_LIMITED");
		stockName.add("SUNDARAM_CLAYTON");
		stockName.add("ZUARI_AGRO_CHEMI");
		stockName.add("BAFNA_PHARMACEUT");
		stockName.add("TARA_JEWELS_LIMI");
		stockName.add("BAJAJ_HINDUSTAN");
		stockName.add("ASF_I");
		stockName.add("VAIBHAV_GLOBAL_L");
		stockName.add("BOMBAY_BURMAH");
		stockName.add("PC_JEWELLER_LTD");
		stockName.add("CARE_LIMITED");
		stockName.add("S_E_POWER_LIMIT");
		stockName.add("PREMIER_POLYFILM");
		stockName.add("TARMAT_LIMITED");
		stockName.add("KITEX_GARMENTS_L");
		stockName.add("MORARJEE_TEXTILE");
		stockName.add("ROSSELL_INDIA_LI");
		stockName.add("MAYUR_UNIQUOTERS");
		stockName.add("RASHTRIYA_CHEM");
		stockName.add("ADVANI_HOTELS");
		stockName.add("AGRI_TECH_INDIA");
		stockName.add("SREELEATHERS_LIM");
		stockName.add("ATH_BIO_GENES");
		stockName.add("CIL_NOVA_PETROCH");
		stockName.add("8K_MILES_SOFT_SE");
		stockName.add("SKIL_INFRASTRUCT");
		stockName.add("SHANTI_GEARS");
		stockName.add("FUT_LIFESTYLE_FA");
		stockName.add("THE_RAMCO_CEMENT");
		stockName.add("PEACOCK_INDUSTRI");
		stockName.add("ORBIT_EXPORTS_LI");
		stockName.add("LYPSA_GEMS_JEW");
		stockName.add("JIND_POL_INV_F");
		stockName.add("PHILIPS_CARBON");
		stockName.add("NORTHGATE_COM_TE");
		stockName.add("MADRAS_FERT");
		stockName.add("MANGALAM_TIMBER");
		stockName.add("GREENPLY_INDS");
		stockName.add("BOSCH_LIMITED");
		stockName.add("PRESSMAN_ADVERTI");
		stockName.add("NAGREEKA_EXPORTS");
		stockName.add("NATL_STEEL");
		stockName.add("NELCO");
		stockName.add("UJAAS_ENERGY_LIM");
		stockName.add("NAVNEET_EDUCATIO");
		stockName.add("INDIABULLS_HSG_F");
		stockName.add("JAY_BHARAT_MARUT");
		stockName.add("JSW_HOLDINGS_LIM");
		stockName.add("ZEE_MEDIA_CORPOR");
		stockName.add("JIK_INDS");
		stockName.add("KANORIA_CHEM");
		stockName.add("KABRA_EXTRUSION");
		stockName.add("INTEGRA_GARMENT");
		stockName.add("CINELINE_INDIA_L");
		stockName.add("LIBERTY_SHOES");
		stockName.add("RAIN_INDUSTRIES");
		stockName.add("HONEYWELL_AUTOMA");
		stockName.add("TECHINDIA_NIRMAN");
		stockName.add("SURANA_SOLAR_LIM");
		stockName.add("DHUNSERI_PETROCH");
		stockName.add("RATTANINDIA_POWE");
		stockName.add("AHMEDNAGAR_FORGI");
		stockName.add("RATTANINDIA_INFR");
		stockName.add("KOTAKMAMC_KOTAKB");
		stockName.add("SRIKALAHASTHI_PI");
		stockName.add("INDUSIND_BANK");
		stockName.add("MONTE_CARLO_FASH");
		stockName.add("INTELLECT_DESIGN");
		stockName.add("KOTHARI_PETROCHE");
		stockName.add("GULF_OIL_LUB_IN");
		stockName.add("SNOWMAN_LOGISTIC");
		stockName.add("SPICE_MOBILITY_L");
		stockName.add("SUPERHOUSE_LIMIT");
		stockName.add("SHARDA_CROPCHEM");
		stockName.add("SQS_INDIA_BFSI_L");
		stockName.add("MARICO_LTD");
		stockName.add("HEMAROO_ENTER");
		stockName.add("AVERI_SEED_CO");
		stockName.add("GF_I");
		stockName.add("AXISCADES_ENGG_T");
		stockName.add("MAGNUM_VENTURES");
		stockName.add("PROZONE_INTU_PRO");
		stockName.add("SALORA_INTL");
		stockName.add("NAGREEKA_CAP_I");
		stockName.add("CIGNITI_TECHNOLO");
		stockName.add("CREST_VENTURES_L");
		stockName.add("PDS_MULTINATIONA");
		stockName.add("AGARWAL_INDS_COR");
		stockName.add("RELCAPAMC_RELD");
		stockName.add("NOESIS_INDUSTRIE");
		stockName.add("HEELS_I");
		stockName.add("COROMANDEL_FERT");
		stockName.add("HATSUN_AGRO_PROD");
		stockName.add("CAPLIN_POINT_LAB");
		stockName.add("FACT_LTD");
		stockName.add("HONDA_SIEL_POWER");
		stockName.add("SHREYAS_SHIPPING");
		stockName.add("GOLDMANAMC_CPS");
		stockName.add("SOM_DIST_BREW");
		stockName.add("SUBROS_LTD");
		stockName.add("SPENTEX_INDS");
		stockName.add("SURYA_ROSHNI");
		stockName.add("RELCAPAMC_RELC");
		stockName.add("TIL");
		stockName.add("BTRFLY_GANDHI_AP");
		stockName.add("E_NORA_I");
		stockName.add("THE_TINPLATE_CO");
		stockName.add("CYIENT_LIMITED");
		stockName.add("WONDERLA_HOLIDAY");
		stockName.add("CNX_AUT");
		stockName.add("JULLUNDUR_MOT_AG");
		stockName.add("CNX_Medi");
		stockName.add("CNX_Meta");
		stockName.add("CNX_20");
		stockName.add("CNX_Smallca");
		stockName.add("ORTEL_COMMUNICAT");
		stockName.add("ZEN_TECHNOLOGIES");
		stockName.add("AN_COAT_METAL");
		stockName.add("MANAK_ALUMINIUM");
		stockName.add("MANAKSIA_STEELS");
		stockName.add("ADLABS_ENTERTAIN");
		stockName.add("INDIABULLS_VENTU");
		stockName.add("CNX_NIFTY_JUN");
		stockName.add("BANCO_PRODUCTS");
		stockName.add("MENON_BEARINGS_L");
		stockName.add("CNX_Realt");
		stockName.add("india_Vi");
		stockName.add("CNXENERG");
		stockName.add("CNX_Infr");
		stockName.add("CNXMN");
		stockName.add("CNXFMC");
		stockName.add("ZODIAC_CLOTHINGS");
		stockName.add("ZODIAC_JRD_MKJ");
		stockName.add("HESTER_BIOSCIENC");
		stockName.add("E_LAND_APPAREL_L");
		stockName.add("TATA_COFFEE");
		stockName.add("GENUS_P_B_LIMITE");
		stockName.add("TATA_INVESTMENT");
		stockName.add("MOLD_TEK_PACKAGI");
		stockName.add("VISHNU_CHEMICALS");
		stockName.add("GREENLAM_INDUSTR");
		stockName.add("GANESHA_ECOSPHER");
		stockName.add("MUNJAL_SHOWA");
		stockName.add("RUCHI_INFRASTURE");
		stockName.add("LINC_PEN_PLAST");
		stockName.add("SIMPLEX_INFRASTR");
		stockName.add("CNXSERVIC");
		stockName.add("CNXPS");
		stockName.add("CNXPHARM");
		stockName.add("S_P_CNX_NIFT");
		stockName.add("CNXPSUBAN");
		stockName.add("A2Z_INFRA_ENGINE");
		stockName.add("MURUDESHWAR_CER");
		stockName.add("GOOD_LUCK_STEEL");
		stockName.add("FINEOTEX_CHEMICA");
		stockName.add("CAMLIN_FINE_SCIE");
		stockName.add("DHUNSERI_TEA_I");
		stockName.add("COUNTRY_CLUB_HOS");
		stockName.add("GULSHAN_POLYOLS");
		stockName.add("GRP_LIMITED");
		stockName.add("LAMBODHARA_TEXTI");
		stockName.add("BALKRISHNA_INDS");
		stockName.add("APOLLO_SINDOORI");
		stockName.add("LOVABLE_LINGERIE");
		stockName.add("MOST_SHARES_N100");
		stockName.add("PTC_INDIA_FIN_SE");
		stockName.add("SHIPPING_CORPN");
		stockName.add("UNITED_PHOSPHORU");
		stockName.add("SESHASAYEE_PAPER");
		stockName.add("MUTHOOT_FINANCE");
		stockName.add("TCI_DEVELOPERS_L");
		stockName.add("STEEL_AUTHORITY");
		stockName.add("SAGAR_CEMENTS");
		stockName.add("SAKTHI_SUGARS");
		stockName.add("HIMACHAL_FUT_COM");
		stockName.add("SAMTEL_COLOR");
		stockName.add("SANDESH_LTD");
		stockName.add("IND_TERRAIN_FASH");
		stockName.add("KOHINOOR_FOODS_L");
		stockName.add("SATHAVAHNA_ISPAT");
		stockName.add("DHUNSERI_INVESTM");
		stockName.add("JINDAL_SAW");
		stockName.add("SAVITA_CHEM");
		stockName.add("SENTINEL_TEA_AND");
		stockName.add("REGENCY_CERAMICS");
		stockName.add("JUBILANT_INDUSTR");
		stockName.add("OMKAR_SPL_CHEM_L");
		stockName.add("BIL_ENERGY_SYSTE");
		stockName.add("RELIANCE_INDUSTR");
		stockName.add("OLTA_I");
		stockName.add("RUCHI_SOYA_INDS");
		stockName.add("HINDUSTAN_MOTORS");
		stockName.add("ALICON_CASTALLOY");
		stockName.add("TEXMACO_RAIL_E");
		stockName.add("RAJSHREE_SUGAR");
		stockName.add("RAMA_NEWSPRINT");
		stockName.add("ALLIS_I");
		stockName.add("HEKHAWATI_POLY");
		stockName.add("RANA_SUGARS");
		stockName.add("RAMCO_INDUSTRIES");
		stockName.add("BF_INVESTMENT_LI");
		stockName.add("RANE_HOLDINGS");
		stockName.add("KALYANI_INVEST_C");
		stockName.add("RAYMOND_LTD");
		stockName.add("MOST_SHARES_M100");
		stockName.add("SUMMIT_SECURITIE");
		stockName.add("WINDSOR_MACHINES");
		stockName.add("L_T_FINANCE_HOLD");
		stockName.add("STERLING_TOOLS");
		stockName.add("Indiabulls_Integ");
		stockName.add("SUDARSHAN_CHEM");
		stockName.add("SUNDARAM_BRAKE");
		stockName.add("BODAL_CHEMICALS");
		stockName.add("SUNFLAG_IRON");
		stockName.add("WINSOME_YARNS_LI");
		stockName.add("GAYATRI_PROJECTS");
		stockName.add("SUPREME_PETROCHE");
		stockName.add("SUPER_SPG");
		stockName.add("SUPREME_INDS");
		stockName.add("SOMANY_CERAMICS");
		stockName.add("MAITHAN_ALLOYS_L");
		stockName.add("RUSHIL_DECOR_LIM");
		stockName.add("SRF_LTD");
		stockName.add("SHR_RAYALSEEMA_H");
		stockName.add("BIRLA_SL_NIFTY_E");
		stockName.add("TVS_SRICHAKRA");
		stockName.add("SHRIRAM_TRANS_FI");
		stockName.add("BHARATIYA_GLOBAL");
		stockName.add("LGI_RUBBER_CO");
		stockName.add("NVENTURE_GRO");
		stockName.add("STANDARD_INDS");
		stockName.add("STAR_PAPER_MILL");
		stockName.add("SIYARAM_SILK");
		stockName.add("DQ_ENTERTAINMENT");
		stockName.add("SKM_EGG_PRODUCTS");
		stockName.add("KF_I");
		stockName.add("VST_TILLERS_TRAC");
		stockName.add("ESTER_INDUSTRIES");
		stockName.add("PVP_VENTURES_LTD");
		stockName.add("KARMA_ENERGY_LIM");
		stockName.add("ASHIANA_HOUSING");
		stockName.add("WEIZMANN_FOREX_L");
		stockName.add("DHANUKA_AGRITECH");
		stockName.add("SHR_CEMENT");
		stockName.add("BIRLA_SL_GOLD_ET");
		stockName.add("SANGHVI_FOR_EN");
		stockName.add("PI_INDUSTRIES_LT");
		stockName.add("SHREYANS_INDS");
		stockName.add("VADILAL_INDUSTRI");
		stockName.add("SYMPHONY_LIMITED");
		stockName.add("SHYAM_TELECOM");
		stockName.add("RELAXO_FOOT_LTD");
		stockName.add("OM_METALS_INFRAP");
		stockName.add("INDO_THAI_SEC_LT");
		stockName.add("TRIVENI_TURBINE");
		stockName.add("ESSAR_SHIPPING_L");
		stockName.add("UNICHEM_LABS");
		stockName.add("IDBI_MF_IDBI_G");
		stockName.add("EVEREADY_INDS");
		stockName.add("UNITECH");
		stockName.add("JAIN_DVR_EQUITY");
		stockName.add("UNIVERSAL_CABLES");
		stockName.add("POLY_MEDICURE_LI");
		stockName.add("DYNACONS_SYS_S");
		stockName.add("RUPA_COMPANY_L");
		stockName.add("JUBILANT_LIFE_SC");
		stockName.add("UTTAM_STEEL");
		stockName.add("TORRENT_PHARMA");
		stockName.add("TOKYO_PLAST");
		stockName.add("FLEXITUFF_INTER");
		stockName.add("VASWANI_IND_LTD");
		stockName.add("TRIVENI_ENGG");
		stockName.add("TTK_PRESTIGE");
		stockName.add("HAKTI_PUMPS_I");
		stockName.add("UCAL_FUEL_SYSTE");
		stockName.add("ALEMBIC_PHARMA_L");
		stockName.add("PANAMA_PETROCHEM");
		stockName.add("PG_ELECTROPLAST");
		stockName.add("PRAKASH_CONSTROW");
		stockName.add("TCI_FINANCE");
		stockName.add("TOURISM_FINANCE");
		stockName.add("TIJARIA_POLYPIPE");
		stockName.add("THIRU_AROORAN");
		stockName.add("ONELIFE_CAP_ADVI");
		stockName.add("TN_PETRO_PRODUCT");
		stockName.add("TN_TELECOM");
		stockName.add("TN_NEWSPRINT");
		stockName.add("SURYALAKSHMI_COT");
		stockName.add("TREE_HOUSE_EDU_L");
		stockName.add("SIL_INVESTMENTS");
		stockName.add("ASAHI_SONGWON_CO");
		stockName.add("SML_ISUZU_LIMITE");
		stockName.add("SWARAJ_ENGINES");
		stockName.add("BROOKS_LAB_LIMIT");
		stockName.add("FILATEX_INDIA_LT");
		stockName.add("TD_POWER_SYSTEMS");
		stockName.add("CIMMCO_BIRLA_LIM");
		stockName.add("NFOMEDIA_I");
		stockName.add("TATA_METALIK");
		stockName.add("TIMKEN_INDIA");
		stockName.add("AGC_NETWORKS_LTD");
		stockName.add("NATIONAL_BUILDIN");
		stockName.add("MT_EDUCARE_LTD");
		stockName.add("TRIB_BHIMJI_ZAVE");
		stockName.add("HEXA_TRADEX_LIMI");
		stockName.add("DEWAN_HOUSING_FI");
		stockName.add("VARDHMAN_SPC_STE");
		stockName.add("SWAN_ENERGY_LIMI");
		stockName.add("CHROMATIC_INDIA");
		stockName.add("SPECIALITY_REST");
		stockName.add("EON_ELECTRIC_LIM");
		stockName.add("MULTI_COMMODITY");
		stockName.add("ORIENT_REFRACTOR");
		stockName.add("WIPRO_LTD");
		stockName.add("WEST_COAST_PAPER");
		stockName.add("MINDA_CORPORATIO");
		stockName.add("EXMACO_INFRA");
		stockName.add("SCHNEIDER_ELECTR");
		stockName.add("ZEE_ENTERTAINMEN");
		stockName.add("ZUARI_INDS");
		stockName.add("NANDAN_EXIM_LIMI");
		stockName.add("CAN_ROBECO_GOL");
		stockName.add("ROVOGUE_INDIA");
		stockName.add("NAGARJUNA_OIL_RE");
		stockName.add("VLS_FINANCE");
		stockName.add("TATA_COMMUNICATI");
		stockName.add("VOLTAS_LTD");
		stockName.add("VST_INDS");
		stockName.add("THOMAS_SCOTT_IN");
		stockName.add("WALCHANDNAGAR_IN");
		stockName.add("V2_RETAIL_LIMITE");
		stockName.add("WEIZMANN_LTD");
		stockName.add("ENKYS_I");
		stockName.add("DB_INT_STOCK_B");
		stockName.add("COMPUCOM_SOFTWAR");
		stockName.add("JAIHIND_PROJECTS");
		stockName.add("VARDHMAN_POLYTEX");
		stockName.add("VARDHMAN_HOLDING");
		stockName.add("ESUVIUS_I");
		stockName.add("APL_APOLLO_TUBES");
		stockName.add("KANANI_INDUSTRIE");
		stockName.add("VINYL_CHEM");
		stockName.add("VINDHYA_TELELINK");
		stockName.add("ANARASBEADSLTD");
		stockName.add("VIP_INDS");
		stockName.add("VISAKA_INDS");
		stockName.add("SYNDICATE_BANK");
		stockName.add("RANE_ENGG_VALVES");
		stockName.add("HCL_TECHNOLOGIES");
		stockName.add("GOLDSTONE_TECHNO");
		stockName.add("GLENMARK_PHARM");
		stockName.add("MUSIC_BROADCAST");
		stockName.add("ONSANTO_I");
		stockName.add("RAJESH_EXPORTS");
		stockName.add("INTENSE_TECHNOLO");
		stockName.add("AVENUE_SUPERMART");
		stockName.add("STRIDES_ARCOLAB");
		stockName.add("PRICOL_LIMITED");
		stockName.add("NEUEON_TOWERS_LI");
		stockName.add("VIDHI_SPCLTY_F_I");
		stockName.add("JITF_INFRALOGIST");
		stockName.add("ALEMBIC_LTD");
		stockName.add("IL_FS_VENTURE");
		stockName.add("JINDAL_STEEL_POW");
		stockName.add("NIPPO_BATTERIES");
		stockName.add("KPIT_CUMMINS_INF");
		stockName.add("ACCELYA_KALA_SOL");
		stockName.add("CYBERTECH_SYSTEM");
		stockName.add("SOUTH_INDIA_BANK");
		stockName.add("63_MOONS_TECHNOL");
		stockName.add("SICAL_LOGISTICS");
		stockName.add("FAIRCHEM_SPECIAL");
		stockName.add("R_S_SOFTWARE");
		stockName.add("SONATA_SOFTWARE");
		stockName.add("NATL_ALUM_NEW");
		stockName.add("J_K_BANK");
		stockName.add("ARCHIES");
		stockName.add("CITY_UNION_BANK");
		stockName.add("VIP_CLOTHING_LIM");
		stockName.add("LAURUS_LABS_LIMI");
		stockName.add("DFM_FOODS_LIMITE");
		stockName.add("NDIA_TOUR_DEV");
		stockName.add("ISAR_METAL_IND");
		stockName.add("AXIS_BANK");
		stockName.add("TIPS_INDUSTRIES");
		stockName.add("BALAJI_TELEFILMS");
		stockName.add("PODDAR_HOUSE_D");
		stockName.add("PRITISH_NANDY_CO");
		stockName.add("STERLITE_TECHNOL");
		stockName.add("CREATIVE_EYE");
		stockName.add("INDIAN_OVERSEAS");
		stockName.add("TAJ_GVK_HOTELS");
		stockName.add("QUINTEGRA_SOLUTI");
		stockName.add("VIJAYA_BANK");
		stockName.add("GUJ_AMBUJA_EXPOR");
		stockName.add("BSEL_INFRASTRUCT");
		stockName.add("CALIFORNIA_SOFTW");
		stockName.add("MUKAND_ENGINEERI");
		stockName.add("KHAITAN_ELECTRIC");
		stockName.add("AKSH_OPTIFIBRES");
		stockName.add("MUKTA_ARTS");
		stockName.add("PNB_GILTS");
		stockName.add("TATA_TELESERVICE");
		stockName.add("USHA_MARTIN");
		stockName.add("MRO_TEK_LTD");
		stockName.add("RAMCO_SYSTEMS");
		stockName.add("BHARATIYA_INTERN");
		stockName.add("CADILA_HEALTHCAR");
		stockName.add("CINEVISTAAS_LTD");
		stockName.add("CL_EDUCATE_LIMIT");
		stockName.add("MAHINDRA_LIFESPA");
		stockName.add("N_R_AGARWAL_INDS");
		stockName.add("BLB_LTD");
		stockName.add("S_P_CNX_50");
		stockName.add("AJANTA_PHARMA");
		stockName.add("VISESH_INFOTECNI");
		stockName.add("BLUE_COAST_HOTEL");
		stockName.add("IVRCL_LIMITED");
		stockName.add("NRB_BEARING");
		stockName.add("MELSTAR_INFORMAT");
		stockName.add("WOCKHARDT_LTD");
		stockName.add("SHR_RAMA_MULTITE");
		stockName.add("GANGES_SECURITIE");
		stockName.add("PALASH_SECURITIE");
		stockName.add("MAGADH_SUGAR_E");
		stockName.add("OPTIEMUS_INFRACO");
		stockName.add("FORTUNE_FIN_SERV");
		stockName.add("ORACLE_FINANCIAL");
		stockName.add("TIMES_GUARANTY");
		stockName.add("UNION_BANK");
		stockName.add("LEEL_ELECTRICALS");
		stockName.add("AUSOM_ENTERPRISE");
		stockName.add("APTECH_LTD");
		stockName.add("ERIS_LIFESCIENCE");
		stockName.add("GTPL_HATHWAY_LIM");
		stockName.add("AXISAMC_AXISNI");
		stockName.add("DYNEMIC_PRODUCTS");
		stockName.add("TRIDENT_LTD");
		stockName.add("TVS_MOTOR");
		stockName.add("ACMILLAN_I");
		stockName.add("KRBL_LTD");
		stockName.add("MARKSANS_PHARMA");
		stockName.add("NIFTY_BENCHMARK");
		stockName.add("BHARTI_AIRTEL");
		stockName.add("TRANSPORT_CORPN");
		stockName.add("PUNJAB_NATIONAL");
		stockName.add("GOLDSTONE_INFRAT");
		stockName.add("SURANA_TELECOM_A");
		stockName.add("PONNIE_SUGARS_ER");
		stockName.add("KHANDWALA_SECURI");
		stockName.add("HIKAL_LTD");
		stockName.add("HAVELLS_INDIA");
		stockName.add("DOLLAR_INDUSTRIE");
		stockName.add("ANDHRA_BANK");
		stockName.add("S_CHAND_AND_COMP");
		stockName.add("SMARTLINK_NETWOR");
		stockName.add("GODREJ_CONSUMER");
		stockName.add("NEXT_MEDIAWORKS");
		stockName.add("HSG_URBAN_DEV");
		stockName.add("PSP_PROJECTS_LIM");
		stockName.add("OSWAL_AGRO_MILLS");
		stockName.add("PRAXIS_HOME_RETA");
		stockName.add("GALAXY_SURFACTAN");
		stockName.add("GMM_PFAUDLER_LIM");
		stockName.add("DSPBRAMC_LIQUI");
		stockName.add("H_G_INFRA_ENGINE");
		stockName.add("INDIAN_BANK");
		stockName.add("HINDUSTAN_AERONA");
		stockName.add("KARDA_CONSTRUCTI");
		stockName.add("ICICI_SECURITIES");
		stockName.add("GUJARAT_RAFFIA_I");
		stockName.add("DILIGENT_MEDIA_C");
		stockName.add("FUTURE_SUPP_CHAI");
		stockName.add("MOHOTA_INDUSTRIE");
		stockName.add("RAMGOPAL_POLYTEX");
		stockName.add("PAISALO_DIGITAL");
		stockName.add("NEWGEN_SOFTWARE");
		stockName.add("GODREJ_AGROVET_L");
		stockName.add("INDIAN_ENERGY_EX");
		stockName.add("MAS_FINANCIAL_SE");
		stockName.add("GENERAL_INS_CORP");
		stockName.add("RAJDARSHAN_INDUS");
		stockName.add("RELIANCE_NIPPON");
		stockName.add("MAHINDRA_LOGISTI");
		stockName.add("IFGL_REFRACTORIE");
		stockName.add("KHADIM_INDIA_LIM");
		stockName.add("HDFC_STAND_LIFE");
		stockName.add("5PAISA_CAPITAL_L");
		stockName.add("SMS_LIFESCIENCES");
		stockName.add("ELIANCE_NAVAL");
		stockName.add("DIXON_TECHNO_IN");
		stockName.add("DISHMAN_CARBO_AM");
		stockName.add("MATRIMONY_COM_LI");
		stockName.add("RELIANCE_HOME_FI");
		stockName.add("LASA_SUPERGENERI");
		stockName.add("CAPACITE_INFRAPR");
		stockName.add("NACL_INDUSTRIES");
		stockName.add("ICICI_LOMBARD_GI");
		stockName.add("PRATAAP_SNACKS_L");
		stockName.add("UTIAMC_UTISENS");
		stockName.add("UTIAMC_UTINIFT");
		stockName.add("HRE_PUSH_CHEM");
		stockName.add("NAVKAR_CORPORATI");
		stockName.add("GUJARAT_GAS_LIMI");
		stockName.add("PENNAR_ENG_BLDG");
		stockName.add("IMCO_ELECON_I");
		stockName.add("SADBHAV_INFRA_PR");
		stockName.add("PRABHAT_DAIRY_LI");
		stockName.add("THE_P_K_TEA_PROD");
		stockName.add("HIND_RECTIFIERS");
		stockName.add("COFFEE_DAY_ENTER");
		stockName.add("SYNGENE_INTERNAT");
		stockName.add("SOMA_TEXTILE");
		stockName.add("NGINEERS_I");
		stockName.add("KAYA_LIMITED");
		stockName.add("MAJESCO_LIMITED");
		stockName.add("MUTHOOT_CAP_SERV");
		stockName.add("ORTIN_LABORATORI");
		stockName.add("POWER_MECH_PROJE");
		stockName.add("RAMA_STEEL_TUBES");
		stockName.add("SATIN_CREDIT_NET");
		stockName.add("HIND_COMPOSITES");
		stockName.add("SHARDA_MOTOR_IND");
		stockName.add("IZMO_LIMITED");
		stockName.add("EDELAMC_NIFTYEES");
		stockName.add("IND_SWIFT_LABS");
		stockName.add("TPL_PLASTECH_LIM");
		stockName.add("NILA_INFRASTRUCT");
		stockName.add("SKIPPER_LIMITED");
		stockName.add("PNC_INFRATECH_LT");
		stockName.add("ALANKIT_LIMITED");
		stockName.add("RELCAPAMC_RELN");
		stockName.add("ORICON_ENTERPRIS");
		stockName.add("MANPASAND_BEVERA");
		stockName.add("ADANI_TRANSMISSI");
		stockName.add("SHYAM_CENTURY_FE");
		stockName.add("INOX_WIND_LIMITE");
		stockName.add("THE_BYKE_HOSPITA");
		stockName.add("LUE_BLENDS_I");
		stockName.add("LAKSHMI_FIN_IND");
		stockName.add("VETO_SWITCHGEAR");
		stockName.add("VIPUL_LIMITED");
		stockName.add("EXCEL_REALTY_N_I");
		stockName.add("VRL_LOGISTICS_LI");
		stockName.add("MEP_INFRA_DEVEL");
		stockName.add("GP_PETROLEUMS_LI");
		stockName.add("LYCOS_INTERNET_L");
		stockName.add("UFO_MOVIEZ_INDIA");
		stockName.add("GOKUL_AGRO_RESOU");
		stockName.add("SEQUENT_SCIENTIF");
		stockName.add("KAMDHENU_LIMITED");
		stockName.add("SIMBHAOLI_SUGARS");
		stockName.add("KDDL_LIMITED");
		stockName.add("RICO_AUTO_INDS");
		stockName.add("EALTHCARE_GLOB");
		stockName.add("SANGHI_INDS");
		stockName.add("PUDUMJEE_PAPER_P");
		stockName.add("BHARAT_WIRE_ROPE");
		stockName.add("NKINDUSTRIES_LTD");
		stockName.add("INEOS_STYROLUTIO");
		stockName.add("EQUITAS_HOLDINGS");
		stockName.add("NFIBEAM_INCORP");
		stockName.add("NARAYANA_HRUDAYA");
		stockName.add("JINDAL_STAINLESS");
		stockName.add("RIENTAL_CARBN");
		stockName.add("AYM_SYNTEX_LIMIT");
		stockName.add("ADITYA_BIRLA_FAS");
		stockName.add("TEAMLEASE_SERVIC");
		stockName.add("PRECISION_CAMSHA");
		stockName.add("QUICK_HEAL_TECH");
		stockName.add("SAMBHAAV_MEDIA");
		stockName.add("MAX_FINANCIAL_SE");
		stockName.add("KELLTON_TECH_SOL");
		stockName.add("BHAGERIA_INDUSTR");
		stockName.add("NORTH_EAST_CARRY");
		stockName.add("UMANG_DAIRIES_LI");
		stockName.add("TTK_HEALTHCARE_L");
		stockName.add("PILANI_INV_IND");
		stockName.add("KRIDHAN_INFRA_LI");
		stockName.add("HDFCAMC_HDFCSE");
		stockName.add("HDFCAMC_HDFCNI");
		stockName.add("LINCOLN_PHARMA_L");
		stockName.add("EDELAMC_EBANK");
		stockName.add("VIKAS_ECOTECH_LI");
		stockName.add("ALKEM_LABORATORI");
		stockName.add("KOTAKMAMC_KTKN");
		stockName.add("LICNAMC_LICNMF");
		stockName.add("DR_LAL_PATH_LAB");
		stockName.add("GOCL_CORPORATION");
		stockName.add("IDFC_BANK_LIMITE");
		stockName.add("S_H_KELKAR_AND_C");
		stockName.add("INTERGLOBE_AVIAT");
		stockName.add("IVP");
		stockName.add("SBI_ETF_GOLD");
		stockName.add("SBI_ETF_NIFTY_BA");
		stockName.add("SBI_ETF_NIFTY_50");
		stockName.add("SBI_ETF_NIFTY_NE");
		stockName.add("JM_SHARE_STOCK");
		stockName.add("KEYNOTE_CORPN");
		stockName.add("LUX_INDUSTRIES_L");
		stockName.add("KHAITAN_FANS_I");
		stockName.add("SALZER_ELECTRONI");
		stockName.add("ASHAPURA_INTI_FA");
		stockName.add("DIGJAM_LIMITED");
		stockName.add("QUESS_CORP_LIMIT");
		stockName.add("MAX_INDIA_LIMITE");
		stockName.add("LLOYDS_STEELS_IN");
		stockName.add("RELCAPAMC_RRSL");
		stockName.add("L_T_INFOTECH_LIM");
		stockName.add("BLUECHIP_STOCK");
		stockName.add("UTTAM_VALUE_STEE");
		stockName.add("MCLEOD_RUSSEL");
		stockName.add("MAHANAGAR_GAS_LT");
		stockName.add("MAHAMAYA_STEEL_I");
		stockName.add("DAMODAR_INDUSTRI");
		stockName.add("TI_I");
		stockName.add("MOLD_TEK_TECHNOL");
		stockName.add("GPT_INFRAPROJECT");
		stockName.add("POKARNA_LIMITED");
		stockName.add("VIJI_FINANCE_LIM");
		stockName.add("ARMAN_FIN_SERV_L");
		stockName.add("ALBERT_DAVID_LIM");
		stockName.add("ASPINWALL_CO_L");
		stockName.add("ENDT_I");
		stockName.add("NAGARJUN_FERT_AN");
		stockName.add("WILLIAM_MAGOR");
		stockName.add("PREMIER_EXPLOSIV");
		stockName.add("SALONA_COTSPIN_L");
		stockName.add("INVESCO_INDIA_GO");
		stockName.add("MAX_VENTURES_AND");
		stockName.add("INVESCO_INDIA_NI");
		stockName.add("KESAR_ENTERPRISE");
		stockName.add("CONTROL_PRINT_LI");
		stockName.add("SBIAMC_SETF10G");
		stockName.add("THYROCARE_TECH_L");
		stockName.add("PALRED_TECHNOLOG");
		stockName.add("UJJIVAN_FIN_SER");
		stockName.add("DUCON_INFRATECHN");
		stockName.add("ARROW_GREENTECH");
		stockName.add("CROMPT_GREA_CON");
		stockName.add("SREI_INFRASTRUCT");
		stockName.add("SPML_INFRA_LTD");
		stockName.add("PARAG_MILK_FOODS");
		stockName.add("FUTURE_ENTERPRIS");
		stockName.add("LICNAMC_LICNFN");
		stockName.add("TAINWALA_CHEM");
		stockName.add("HRIRAM_PIST");
		stockName.add("EDELAMC_EQ30");
		stockName.add("PODDAR_PIGMENTS");
		stockName.add("KINGFA_SCI_TEC");
		stockName.add("ROLLATAINERS_LIM");
		stockName.add("KOTHARI_PRODUCTS");
		stockName.add("HIGH_GROUND_ENTP");
		stockName.add("KIOCL_LIMITED");
		stockName.add("B_I_IND_FIN");
		stockName.add("ASTRAZENECA_PHAR");
		stockName.add("ITD_CEMENTATION");
		stockName.add("HPL_ELECTRIC_P");
		stockName.add("IDFCAMC_IDFNIF");
		stockName.add("MASK_INVESTMENTS");
		stockName.add("FUTURE_CONSUMER");
		stockName.add("ENDURANCE_TECHNO");
		stockName.add("AUTOMOTIVE_STAMP");
		stockName.add("KREBS_BIOCHEMICA");
		stockName.add("PNB_HOUSING_FIN");
		stockName.add("MAZDA_LIMITED");
		stockName.add("VEDL");
		stockName.add("VARUN_BEVERAGES");
		stockName.add("ASAHI_I_SAFE");
		stockName.add("RUBY_MILLS");
		stockName.add("TRIGYN_TECHNOLOG");
		stockName.add("JAI_CORPORATION");
		stockName.add("JOHNSON_CONTROLS");
		stockName.add("ANGAM_I");
		stockName.add("GE_T_D_INDIA_LIM");
		stockName.add("GE_POWER_INDIA_L");
		stockName.add("ZENITH_EXPORT");
		stockName.add("CUPID_LIMITED");
		stockName.add("L_T_TECHNOLOGY_S");
		stockName.add("ELECTROTHERM");
		stockName.add("GNA_AXLES_LIMITE");
		stockName.add("HI_TECH_GEAR");
		stockName.add("ICICI_PRU_LIFE_I");
		stockName.add("MARATHON_NXTGEN");
		stockName.add("GANESH_HOUSING_F");
		stockName.add("LICNAMC_LICNFE");
		stockName.add("INDTECK_INDIA");
		stockName.add("DILIP_BUILDCON_L");
		stockName.add("NLC_INDIA_LIMITE");
		stockName.add("HIMADRI_SPECIALI");
		stockName.add("FUTURE_RETAIL_LI");
		stockName.add("NAGA_DHUNSERI_GR");
		stockName.add("MANUGRAPH_INDS");
		stockName.add("RBL_BANK_LIMITED");
		stockName.add("REMSONS_INDS");
		stockName.add("GMR_INFRASTRUCTU");
		stockName.add("TECH_MAHINDRA");
		stockName.add("ATLANTA_LTD");
		stockName.add("VOLTAMP_TRANSFOR");
		stockName.add("SELAN_EXPLORATIO");
		stockName.add("ACTION_CONSTRUCT");
		stockName.add("ANANT_RAJ_INDS");
		stockName.add("HOV_SERVICES");
		stockName.add("CSA_I");
		stockName.add("TALBROS_AUTOMOTI");
		stockName.add("K_SERA_SERA_LIMI");
		stockName.add("GEE_CEE_VENTURES");
		stockName.add("LOKESH_MACHINES");
		stockName.add("SWELECT_ENERGY_S");
		stockName.add("BAL_PHARMA");
		stockName.add("RATNAMANI_METALS");
		stockName.add("PIONEER_EMBROIDE");
		stockName.add("GTN_TEXTILES");
		stockName.add("PRIME_FOCUS");
		stockName.add("EMAMI_LTD");
		stockName.add("ALLCARGO_GLOBAL");
		stockName.add("SOLAR_EXPLOSIVES");
		stockName.add("VAKRANGEE_SOFTWA");
		stockName.add("ADHUNIK_METALIKS");
		stockName.add("GALLANTT_METAL");
		stockName.add("MALU_PAPER_MILLS");
		stockName.add("STL_GLOBAL_LTD");
		stockName.add("UTTAM_SUGAR_MILL");
		stockName.add("ROHIT_FERRO_TECH");
		stockName.add("KEWAL_KIRAN_CLOT");
		stockName.add("SUN_TV");
		stockName.add("ZICOM_ELECTRONIC");
		stockName.add("R_SYSTEMS_INTL");
		stockName.add("ODAWARI_POWER");
		stockName.add("POLYCAB_INDIA_LI");
		stockName.add("EMKAY_GLOBAL_FIN");
		stockName.add("RELIANCE_COMMUNI");
		stockName.add("SUNIL_HITECH_ENG");
		stockName.add("KEC_INTL");
		stockName.add("SAKUMA_EXPORTS");
		stockName.add("JK_CEMENT");
		stockName.add("INDO_TECH_TRANSF");
		stockName.add("M_M_FINANCIAL");
		stockName.add("BL_KASHYAP_SON");
		stockName.add("NITCO_LIMITED");
		stockName.add("VISA_STEEL");
		stockName.add("KEI_INDS");
		stockName.add("CENTURY_PLYBOARD");
		stockName.add("SANWARIA_AGRO_OI");
		stockName.add("AVT_NATURAL_PROD");
		stockName.add("TIDE_WATER_OIL_C");
		stockName.add("LUMAX_AUTO_TECHN");
		stockName.add("SARLA_PERFORMANC");
		stockName.add("ALCHEMIST");
		stockName.add("WANBURY");
		stockName.add("SANGHVI_MOVERS");
		stockName.add("PTL_ENTERPRISES");
		stockName.add("GANDHI_SPECIAL_T");
		stockName.add("AUTOLINE_INDS");
		stockName.add("XL_ENERGY_LIMITE");
		stockName.add("ESS_DEE_ALUMINIU");
		stockName.add("NATIONAL_FERTILI");
		stockName.add("LASTIBLENDS_I");
		stockName.add("PRAJAY_ENGINEERS");
		stockName.add("PARAMOUNT_COMMUN");
		stockName.add("HBL_POWER_SYSTEM");
		stockName.add("TANLA_SOLUTIONS");
		stockName.add("RAJ_RAYON_INDUST");
		stockName.add("SHIVAM_AUTOTECH");
		stockName.add("PARSVNATH_DEVELO");
		stockName.add("TORRENT_POWER_LI");
		stockName.add("SUTLEJ_TEXTILES");
		stockName.add("SOBHA_DEVELOPERS");
		stockName.add("LT_OVERSEAS");
		stockName.add("RUCHIRA_PAPERS");
		stockName.add("NEOGEN_CHEMICALS");
		stockName.add("DONEAR_INDS");
		stockName.add("VENUS_REMEDIES");
		stockName.add("ARIHANT_FOUNDATI");
		stockName.add("DOLPHIN_OFFSHORE");
		stockName.add("MADHUCON_PROJECT");
		stockName.add("GATI");
		stockName.add("MCNALLY_BHARAT_E");
		stockName.add("FIEM_INDS");
		stockName.add("DEVELOPMENT_CRED");
		stockName.add("JHS_SVENDGAARD_L");
		stockName.add("ACCEL_FRONTLINE");
		stockName.add("GLOBAL_VECTRA_HE");
		stockName.add("ARVEE_DENIMS");
		stockName.add("GTL_INFRASTRUCTU");
		stockName.add("KPIT_TECHNOLOGIE");
		stockName.add("NFO_EDGE_I");
		stockName.add("BALAJI_AMINES");
		stockName.add("IBULLSAMC_IBMF");
		stockName.add("BANSWARA_SYNTEX");
		stockName.add("ONE_POINT_ONE_SO");
		stockName.add("ICRA");
		stockName.add("DISH_TV_I");
		stockName.add("UTI_GOLD_EXCHANG");
		stockName.add("PHOENIX_MILLS");
		stockName.add("ENITH_BIRLA_I");
		stockName.add("ARO_GRANITE_INDS");
		stockName.add("BF_UTILITIES");
		stockName.add("GLOBAL_OFFSHORE");
		stockName.add("FORTIS_HEALTHCAR");
		stockName.add("KARUTURI_NETWORK");
		stockName.add("UNIPLY_INDS");
		stockName.add("LA_OPALA_RG");
		stockName.add("RAJ_TELEVISION_N");
		stockName.add("AMD_INDUSTRIES_L");
		stockName.add("PAGE_INDS");
		stockName.add("BENCHMARK_MF_G");
		stockName.add("INDIABULLS_REAL");
		stockName.add("ASTRAL_POLY_TECH");
		stockName.add("JMT_AUTO");
		stockName.add("THEMIS_MEDICARE");
		stockName.add("NCL_INDS");
		stockName.add("FIRSTSOURCE_SOLU");
		stockName.add("POWER_FINANCE_CO");
		stockName.add("TT_LTD");
		stockName.add("TRANSWARRANTY_FI");
		stockName.add("C_C_CONSTRUCTION");
		stockName.add("VIKAS_MULTICORP");
		stockName.add("SMS_PHARMACEUTIC");
		stockName.add("ORIENTAL_TRIMEX");
		stockName.add("MINDTREE_LIMITED");
		stockName.add("IDEA_CELLULAR");
		stockName.add("EURO_CERAMICS");
		stockName.add("ETWORK18_MEDIA");
		stockName.add("PITTI_LAMINATION");
		stockName.add("MINDA_INDS");
		stockName.add("HUBTOWN_LIMITED");
		stockName.add("TV18_BROADCAST_L");
		stockName.add("CAMBRIDGE_TECHNO");
		stockName.add("ANG_INDUSTRIES_L");
		stockName.add("TECHNOCRAFT_INDS");
		stockName.add("ORIENT_CERAMICS");
		stockName.add("EDINGTON_I");
		stockName.add("PEARL_GLOBAL_IND");
		stockName.add("DIGICONTENT_LIMI");
		stockName.add("INDIAMART_INTERM");
		stockName.add("INFOBEANS_TECHNO");
		stockName.add("DCM_NOUVELLE_LIM");
		stockName.add("LIBAS_DESIGNS_LI");
		stockName.add("GOA_CARBON");
		stockName.add("TIME_TECHNOPLAST");
		stockName.add("HTMT_GLOBAL_SOLU");
		stockName.add("DECOLIGHT_CERAMI");
		stockName.add("NELCAST");
		stockName.add("MEGHMANI_ORGANIC");
		stockName.add("DLF");
		stockName.add("CELESTIAL_BIOLAB");
		stockName.add("PRAKASH_PIPES_LI");
		stockName.add("EMAMI_PAPER_MILL");
		stockName.add("LPHAGEO_INDIA");
		stockName.add("BHAGWATI_BANQUET");
		stockName.add("WEBEL_SL_ENG_LT");
		stockName.add("SHR_GANESH_FORGI");
		stockName.add("HILTON_METAL_FOR");
		stockName.add("MIC_ELECTRONICS");
		stockName.add("MCDOWELL_HOLDING");
		stockName.add("NSECTICIDES_I");
		stockName.add("NAVIN_FLUORINE_I");
		stockName.add("K_M_SUGAR_MILLS");
		stockName.add("NITIN_FIRE_PROTE");
		stockName.add("MANDHANA_IND_LTD");
		stockName.add("IRCON_INTERNATIO");
		stockName.add("KSB_LIMITED");
		stockName.add("GARDEN_REACH_SHI");
		stockName.add("ADANI_GAS_LIMITE");
		stockName.add("RADAAN_MEDIAWORK");
		stockName.add("OMAX_AUTO");
		stockName.add("MIRAEAMC_MAN50");
		stockName.add("TC_I");
		stockName.add("ZZZZZZZZ_TKD_DON");
		stockName.add("MARUTI_UDYOG");
		stockName.add("LIQUID_BENCHMARK");
		stockName.add("MODI_RUBBER_LTD");
		stockName.add("GAYATRI_HIGHWAYS");
		stockName.add("RITES_LIMITED");
		stockName.add("FINE_ORGANIC_IND");
		stockName.add("HB_STOCKHOLDINGS");
		stockName.add("VARROC_ENGINEERI");
		stockName.add("HDFC_AMC_LIMITED");
		stockName.add("SIGNET_INDUSTRIE");
		stockName.add("ICICIPRAMC_ICI");
		stockName.add("MONNET_ISPAT_E");
		stockName.add("JUNIOR_BEES");
		stockName.add("GODREJ_INDS");
		stockName.add("ICICI_PRUD_SENSE");
		stockName.add("ICICI_PRUDENTIAL");
		stockName.add("ICICIPRAMC_BHA");
		stockName.add("JTEKT_INDIA_LIMI");
		stockName.add("HI_TECH_PIPES_LI");
		stockName.add("3P_LAND_HOLDINGS");
		stockName.add("ORIENT_ELECTRIC");
		stockName.add("INDOSTAR_CAPITAL");
		stockName.add("PROSEED_INDIA_LI");
		stockName.add("MAHA_RASHTRA_APE");
		stockName.add("MISHRA_DHATU_NIG");
		stockName.add("SRI_HAVISHA_HOSP");
		stockName.add("LEMON_TREE_HOTEL");
		stockName.add("ICICI_PR_NIF_LW");
		stockName.add("ICICI_PRUD_MIDCA");
		stockName.add("NOIDA_TOLL_BRIDG");
		stockName.add("ALLAHABAD_BANK");
		stockName.add("NUCLEUS_SOFTWARE");
		stockName.add("ICICI_PRUD_NIFTY");
		stockName.add("CANARA_BANK");
		stockName.add("GENESYS_INTL");
		stockName.add("DIVIS_LABS");
		stockName.add("JK_TYRE_INDS");
		stockName.add("P_G_HYGIENE_HE");
		stockName.add("APAR_INDS");
		stockName.add("CENTURY_EXTRUSIO");
		stockName.add("TCS");
		stockName.add("NIIT_TECHNOLOGIE");
		stockName.add("ULTRATECH_CEMCO");
		stockName.add("GREAVES_COTTON");
		stockName.add("GRUH_FINANCE");
		stockName.add("MAGMA_FINCORP_LI");
		stockName.add("XPRO_INDIA");
		stockName.add("RK_FORGINGS");
		stockName.add("DATAMATICS_GLOBA");
		stockName.add("NDTV_LTD");
		stockName.add("ODISHA_CEMENT_LI");
		stockName.add("CL_PRODUCTS_I");
		stockName.add("EXCEL_INDS");
		stockName.add("EXCEL_CROP_CARE");
		stockName.add("DREDGING_CORPN");
		stockName.add("INDRAPRASTHA_GAS");
		stockName.add("TV_TODAY");
		stockName.add("DHANALAKSHMI_BAN");
		stockName.add("PETRONET_LNG");
		stockName.add("TC_I");
		stockName.add("BANK_OF_MAHARASH");
		stockName.add("NILA_SPACES_LIMI");
		stockName.add("OPTO_CIRCUITS_I");
		stockName.add("CNX_IT_INDEX");
		stockName.add("SUBEX_LIMITED");
		stockName.add("MUNJAL_AUTO");
		stockName.add("UCO_BANK");
		stockName.add("CENTUM_ELECTRONI");
		stockName.add("B_A_G_FILMS_ME");
		stockName.add("VARDHAMAN_ACRYL");
		stockName.add("SUVEN_LIFE_SCIEN");
		stockName.add("WELSPUN_CORP_LTD");
		stockName.add("NALWA_SONS_INV");
		stockName.add("JAI_BALAJI_SPONG");
		stockName.add("ELSPUN_I");
		stockName.add("VS_ELEC_NEW");
		stockName.add("YBER_MEDIA_I");
		stockName.add("CNX_BANK_INDEX");
		stockName.add("JK_PAPER");
		stockName.add("SHAH_ALLOYS");
		stockName.add("RANULES_I");
		stockName.add("MERCATOR_LINES");
		stockName.add("AMTEK_INDIA");
		stockName.add("AN_INDS_I");
		stockName.add("INDIA_GLYCOLS");
		stockName.add("GEOJIT_BNP_PARIB");
		stockName.add("RELCAPAMC_RETF");
		stockName.add("Genus_Power_Infr");
		stockName.add("SHRIRAM_CITYUNI");
		stockName.add("GATEWAY_DISTRIPA");
		stockName.add("JAIPRAKASH_POWER");
		stockName.add("JINDAL_PHOTO");
		stockName.add("3I_INFOTECH");
		stockName.add("ELPMOC_DESIGN");
		stockName.add("KARNATAKA_BANK");
		stockName.add("GOKALDAS_EXPORTS");
		stockName.add("SAKSOFT_LTD");
		stockName.add("ALLSEC_TECHNOLOG");
		stockName.add("SHOPPERS_STOP");
		stockName.add("INDIA_INFOLINE");
		stockName.add("STEEL_STRIPS_WHE");
		stockName.add("ANGALAM_DRUGS");
		stockName.add("JBM_AUTO_COMPONE");
		stockName.add("DWARIKESH_SUGAR");
		stockName.add("IMPEX_FERRO_TECH");
		stockName.add("INDOCO_REMEDIES");
		stockName.add("SUPRAJIT_ENGINEE");
		stockName.add("PATEL_ENGINEERIN");
		stockName.add("JET_AIRWAYS");
		stockName.add("XCHANGING_SOLUT");
		stockName.add("BHANSALI_ENGG_PO");
		stockName.add("IND_SWIFT_LTD");
		stockName.add("GUFIC_BIOSCIENCE");
		stockName.add("ASTRA_MICROWAVE");
		stockName.add("NTPC");
		stockName.add("WELSPUN_PROJECT");
		stockName.add("KOTHARI_SUGARS");
		stockName.add("SAL_STEEL");
		stockName.add("ARTRONICS_I");
		stockName.add("CELEBRITY_FASHIO");
		stockName.add("ROYAL_ORCHID_HOT");
		stockName.add("NITIN_SPINNERS");
		stockName.add("GUJ_STATE_PETRON");
		stockName.add("ENTERTAINMENT_NE");
		stockName.add("JAGRAN_PRAKASHAN");
		stockName.add("MAHESHWARI_LOGIS");
		stockName.add("METROPOLIS_HEALT");
		stockName.add("INOX_LEISURE");
		stockName.add("GVK_POWER_INFR");
		stockName.add("SADBHAV_ENGINEER");
		stockName.add("PBA_INFRASTRUCTU");
		stockName.add("BOMBAY_RAYON_FAS");
		stockName.add("AIA_ENGINEERING");
		stockName.add("CNX_10");
		stockName.add("ORIENT_ABRASIVES");
		stockName.add("EVEREST_KANTO_CY");
		stockName.add("KERNEX_MICROSYST");
		stockName.add("VIMTA_LABS");
		stockName.add("REPRO_INDIA");
		stockName.add("PVR_LTD");
		stockName.add("PUNJ_LLOYD");
		stockName.add("FCS_SOFTWARE_SOL");
		stockName.add("RAJVIR_INDS");
		stockName.add("INDIA_MOTOR_PART");
		stockName.add("AURIONPRO_SOLUTI");
		stockName.add("SUZLON_ENERGY");
		stockName.add("MSTC_LIMITED");
		stockName.add("RAIL_VIKAS_NIGAM");
		stockName.add("SHR_RENUKA_SUGAR");
		stockName.add("BANNARI_AMMAN_SP");
		stockName.add("MSP_STEEL_POWE");
		stockName.add("YES_BANK");
		stockName.add("NECTAR_LIFESCIEN");
		stockName.add("SPL_INDS");
		stockName.add("CNX_MIDCA");
		stockName.add("REVATHI_EQUIPMEN");
		stockName.add("VIVIMED_LABS");
		stockName.add("IDFC");
		stockName.add("ANE_MADRAS");
		stockName.add("GOLDIAM_INTL");
		stockName.add("SASKEN_COMMUNICA");
		stockName.add("HT_MEDIA");
		stockName.add("MOTILAL_OSWAL_FI");
		stockName.add("PURAVANKARA_PROJ");
		stockName.add("INDOWIND_ENERGY");
		stockName.add("KOTAK_GOLD_EXC");
		stockName.add("OMAXE");
		stockName.add("VICEROY_HOTELS");
		stockName.add("SEL_MANUFACTURIN");
		stockName.add("TAKE_SOLUTIONS");
		stockName.add("ASIAN_GRANITO_I");
		stockName.add("ISMT");
		stockName.add("KPR_MILLS");
		stockName.add("MAHINDRA_FORGING");
		stockName.add("SUN_PHARMA_ADVAN");
		stockName.add("MEGASOFT");
		stockName.add("ALLIED_DIGITAL_S");
		stockName.add("HOUSING_DEV_IN");
		stockName.add("S_INDS_I");
		stockName.add("ALPA_LABORATORIE");
		stockName.add("TATA_STEEL");
		stockName.add("THERMAX_LTD");
		stockName.add("APOLLO_TYRES");
		stockName.add("ATA_I");
		stockName.add("BOMBAY_DYEING");
		stockName.add("CIPLA_LTD");
		stockName.add("ESCORTS_LTD");
		stockName.add("FEDERAL_BANK");
		stockName.add("MAHINDRA_MAHIN");
		stockName.add("LARSEN_TOUBRO");
		stockName.add("MTNL");
		stockName.add("MRPL");
		stockName.add("ORIENTAL_BANK");
		stockName.add("RELIANCE_CAPITAL");
		stockName.add("STATE_BANK");
		stockName.add("TATA_POWER_COMP");
		stockName.add("TATA_CHEMICAL_LI");
		stockName.add("TATA_MOTORS");
		stockName.add("TATA_GLOBAL_BEVE");
		stockName.add("GRASIM_INDS");
		stockName.add("GLAXOSMITHKLINE");
		stockName.add("HDFC_LTD");
		stockName.add("AMBUJA_CEMENTS");
		stockName.add("HINDALCO_INDS");
		stockName.add("HDFC_BANK");
		stockName.add("HIND_PETROLEUM");
		stockName.add("HIND_UNILEVER");
		stockName.add("IDBI");
		stockName.add("INDIAN_HOTELS");
		stockName.add("IFCI_LTD");
		stockName.add("ITC_LTD");
		stockName.add("ABB");
		stockName.add("ACC_LTD");
		stockName.add("NIFTY_MIDCAP");
		stockName.add("ASHOK_LEYLAND");
		stockName.add("ARVIND_LIMITED");
		stockName.add("RELIANCE_INDS");
		stockName.add("AJAJ_HOLDINGS");
		stockName.add("ASIAN_PAINTS");
		stockName.add("BHARAT_PETROLEUM");
		stockName.add("BHEL");
		stockName.add("BENCHMARK_BANKBE");
		stockName.add("RELIANCE_INFRAST");
		stockName.add("COLGATE_PALMOLIV");
		stockName.add("GREAT_EASTERN_SH");
		stockName.add("EIH_LTD");
		stockName.add("PSU_BANK_BEES");
		stockName.add("IL_FS_ENGINEERIN");
		stockName.add("BAJAJ_ELECT_LTD");
		stockName.add("DELTA_CORP_LTD");
		stockName.add("CERA_SANITARYWAR");
		stockName.add("ALOK_INDS");
		stockName.add("NERGY_DEVE_CO");
		stockName.add("SRI_ADHIKARI_BRO");
		stockName.add("ALPS_INDS");
		stockName.add("KOTAK_PSU_BANK");
		stockName.add("AMARA_RAJA_BATTE");
		stockName.add("RELIGARE_ENTER_L");
		stockName.add("BARAK_VALLEY_CEM");
		stockName.add("NIPHOS_ENT");
		stockName.add("TITAN_INDS");
		stockName.add("ABUR_I");
		stockName.add("SUPREME_INFRA_L");
		stockName.add("AARTI_INDS");
		stockName.add("AAARTI_DRUGS");
		stockName.add("ABAN_OFFSHORE");
		stockName.add("MAAN_ALUMINIUM_L");
		stockName.add("MANG_CHEM_FERT_L");
		stockName.add("AEGIS_LOGISTICS");
		stockName.add("ADOR_WELDING");
		stockName.add("PFIZER_LTD");
		stockName.add("ECE_INDUSTRIES_L");
		stockName.add("POWERGRID_CORPOR");
		stockName.add("SIEMENS_LTD");
		stockName.add("SPIC_LTD");
		stockName.add("SUNDARAM_FINANCE");
		stockName.add("CONS_CONST_CON");
		stockName.add("SUN_PHARMACEUTIC");
		stockName.add("SUNDARAM_FASTENE");
		stockName.add("HOMAS_COOK_I");
		stockName.add("DHANUS_TECH_LTD");
		stockName.add("HERO_HONDA");
		stockName.add("HOTEL_LEELA_VENT");
		stockName.add("INFOSYS_LIMITED");
		stockName.add("ILLETTE_I");
		stockName.add("CENTRAL_BANK_OF");
		stockName.add("TRENT_LTD");
		stockName.add("KOTAK_MAHINDRA_B");
		stockName.add("LUPIN_LABS");
		stockName.add("LIC_HOUSING_FIN");
		stockName.add("CHENNAI_PETROLEU");
		stockName.add("NIIT");
		stockName.add("CUBEX_TUBINGS_LT");
		stockName.add("PRECISION_PIPES");
		stockName.add("KAVVERI_TEL_LTD");
		stockName.add("AMBIKA_COTTON_MI");
		stockName.add("BANNARIAMAN_SUGA");
		stockName.add("BALRAMPUR_CHINI");
		stockName.add("BANK_OF_INDIA");
		stockName.add("BANK_OF_BARODA");
		stockName.add("RELIANCE_POWER_L");
		stockName.add("APOLLO_SINDHOORI");
		stockName.add("BHARAT_BIJLEE");
		stockName.add("TRANS_RECTI_LT");
		stockName.add("ASSAM_COMPANY_IN");
		stockName.add("ECLERX_SERVICES");
		stockName.add("BRIGADE_ENTER_LT");
		stockName.add("ATUL_LTD");
		stockName.add("ATLAS_CYCLES");
		stockName.add("AUTOMOTIVE_AXLES");
		stockName.add("AUROBINDO_PHARMA");
		stockName.add("BURNPUR_CEMENT_L");
		stockName.add("BGR_ENERGY_SYSTE");
		stockName.add("ARIES_AGRO_LTD");
		stockName.add("MANAKSIA_LTD");
		stockName.add("ATEL_INT_LOG");
		stockName.add("HEXAWARE_TECHNOL");
		stockName.add("EDELWEISS_CAP_LT");
		stockName.add("ATN_INTL");
		stockName.add("OLTE_PATIL_DEV");
		stockName.add("RENAISSANCE_JEWE");
		stockName.add("JYOTHY_LABS_LTD");
		stockName.add("KAUSHALYA_INFRA");
		stockName.add("ASHIMA_LTD");
		stockName.add("ASHAPURA_MINCHEM");
		stockName.add("KALYANI_FORGE_LI");
		stockName.add("HITECH_PLAST_LIM");
		stockName.add("ADHAV_MARBLE");
		stockName.add("CHOLAMANDALAM_DB");
		stockName.add("ALKYL_AMINES_CHE");
		stockName.add("EMPEE_DISTI_LTD");
		stockName.add("ANDHRA_SUGARS");
		stockName.add("MC_PROJECTS_I");
		stockName.add("ANSAL_HOUSING");
		stockName.add("APCOTEX_INDS");
		stockName.add("ANTARCTICA_LTD");
		stockName.add("AP_PAPER_MILLS");
		stockName.add("APOLLO_HOSPITALS");
		stockName.add("Adani_Ports_and");
		stockName.add("ADHA_MADHAV_CO");
		stockName.add("EIH_ASSOC_HOTELS");
		stockName.add("NESCO_LTD");
		stockName.add("CARBORUNDUM_UNIV");
		stockName.add("CEAT_LTD");
		stockName.add("DPSC_LIMITED");
		stockName.add("KIRI_INDUSTRIES");
		stockName.add("TITAGARH_WAGONS");
		stockName.add("CENTURY_TEXTILES");
		stockName.add("CENTURY_ENKA");
		stockName.add("CESC_LTD");
		stockName.add("CHAMBAL_FERT");
		stockName.add("IGARSHI_MOTORS");
		stockName.add("ANIK_INDUSTRIES");
		stockName.add("BLUE_DART_EXPRES");
		stockName.add("GSS_AMERICA_INFO");
		stockName.add("BLUE_STAR");
		stockName.add("NAHAR_CAP_FIN");
		stockName.add("BPL_LTD");
		stockName.add("V_GUARD_IND_LTD");
		stockName.add("URAL_ELEC_CORP");
		stockName.add("BSL_LTD");
		stockName.add("GAMMON_INFRA_PRO");
		stockName.add("CAN_FIN_HOMES");
		stockName.add("SITA_SHREE_FOOD");
		stockName.add("BHARAT_GEARS");
		stockName.add("BHARAT_RASAYAN");
		stockName.add("BHUSHAN_STEEL");
		stockName.add("QUANTUM_GOLD_FUN");
		stockName.add("BALLARPUR_INDS");
		stockName.add("BINANI_INDS");
		stockName.add("NMDC_LTD");
		stockName.add("3M_INDIA");
		stockName.add("OSWAL_CHEM");
		stockName.add("BIRLA_CORP");
		stockName.add("SHALIM_PAINTS_LT");
		stockName.add("CORDS_CABLE_INDU");
		stockName.add("JKUMAR_INFR_LTD");
		stockName.add("KNR_CONSTRU_LTD");
		stockName.add("SUJANA_UNI_INDS");
		stockName.add("ONMOBILE_GLOBAL");
		stockName.add("TANTIA_CONST_LTD");
		stockName.add("BEML_LTD");
		stockName.add("BERGER_PAINTS");
		stockName.add("BANG_OVERSEAS_LT");
		stockName.add("HERCULES_HOI_LT");
		stockName.add("SHRIRAM_EPC_LTD");
		stockName.add("MPHASIS");
		stockName.add("IRB_INFRA_DEV_LT");
		stockName.add("LG_BALAKRISHNAN");
		stockName.add("IL_FS_TRANS_NET");
		stockName.add("PERSISTENT_SYSTE");
		stockName.add("PRADIP_OVERSEAS");
		stockName.add("LAKSHMI_VILAS_BK");
		stockName.add("ASIAN_HOTELS_NO");
		stockName.add("NTRASOFT_TECH");
		stockName.add("LAKSHMI_MACHINE");
		stockName.add("KESORAM_INDS");
		stockName.add("ORIENT_PRESS_LIM");
		stockName.add("RSS_INFRA_PROJ");
		stockName.add("KIRLOSKAR_ELECTR");
		stockName.add("DEEP_INDUSTRIES");
		stockName.add("SUJANA_METAL_PRO");
		stockName.add("UMMINS_I");
		stockName.add("TEXMO_PIPE_PRO");
		stockName.add("MAN_INFRA_LTD");
		stockName.add("KOPRAN_LTD");
		stockName.add("UNITED_BANK_OF_I");
		stockName.add("EMMBI_POLYARNS_L");
		stockName.add("D_B_REALTY_LIMIT");
		stockName.add("KAJARIA_CERAMICS");
		stockName.add("JAIPRAKASH_ASSOC");
		stockName.add("KALPATARU_POWER");
		stockName.add("KAKATIYA_CEMENT");
		stockName.add("ATHWAY_CABLE");
		stockName.add("AMAT_HOTELS_I");
		stockName.add("KCP_LTD");
		stockName.add("KARUR_VYSYA_BANK");
		stockName.add("KCP_SUGAR_I_CO");
		stockName.add("JBF_INDS");
		stockName.add("JCT_ELEC");
		stockName.add("CONSOL_FINVEST");
		stockName.add("JINDAL_DRILLING");
		stockName.add("JINDAL_POLY_FILM");
		stockName.add("JSW_STEEL");
		stockName.add("JUBILANT_FOODWOR");
		stockName.add("KOTAK_NIFTY_ETF");
		stockName.add("JK_LAKSHMI_CEMEN");
		stockName.add("VASCON_ENGINEERS");
		stockName.add("SYNCOM_HEALTHCAR");
		stockName.add("THANGAMAYIL_JEWE");
		stockName.add("NDIAN_METALS");
		stockName.add("USHER_AGRO_LTD");
		stockName.add("EMAMI_REALTY_LIM");
		stockName.add("MOST_SHARES_M50");
		stockName.add("MOREPEN_LABS");
		stockName.add("PENINSULA_LAND");
		stockName.add("ASIAN_HOTELS_WE");
		stockName.add("MOTHERSON_SUMI");
		stockName.add("MRF_LTD");
		stockName.add("MANAPPURAM_GEN_F");
		stockName.add("MIRC_ELEC");
		stockName.add("MM_FORGINGS");
		stockName.add("MIRZA_INTL");
		stockName.add("USHA_MARTIN_EDU");
		stockName.add("PARABOLIC_DRUGS");
		stockName.add("TILAKNAGAR_INDUS");
		stockName.add("TECHNOFAB_ENG_LT");
		stockName.add("BLISS_GVS_PHARMA");
		stockName.add("HINDUSTAN_MEDIA");
		stockName.add("HANG_SENG_BENCHM");
		stockName.add("MANALI_PETROCHEM");
		stockName.add("MANGALAM_CEMENT");
		stockName.add("RAINBOW_PAPERS_L");
		stockName.add("MARAL_OVERSEAS");
		stockName.add("SUNDARAM_MULTI_P");
		stockName.add("MASTEK_LTD");
		stockName.add("PIONEER_DIST_LTD");
		stockName.add("LUMAX_INDS");
		stockName.add("KIRLOSKAR_BROTHE");
		stockName.add("NITESH_ESTATES_L");
		stockName.add("TALWALKAR_FITNES");
		stockName.add("LYKA_LABS");
		stockName.add("KALYANI_STEELS_L");
		stockName.add("TARAPUR_TRANSFOR");
		stockName.add("VARDHMAN_TEXTILE");
		stockName.add("ANDHANA_INDUS");
		stockName.add("MAHA_SCOOTERS");
		stockName.add("SJVN_LTD");
		stockName.add("JAYPEE_INFRATECH");
		stockName.add("MAHA_SEAMLESS");
		stockName.add("ORIENT_PAPER");
		stockName.add("ORIENTAL_HOTELS");
		stockName.add("ORISSA_MIN_DEV_C");
		stockName.add("KILITCH_DRUGS_IN");
		stockName.add("INFRA_BENCHMARK");
		stockName.add("PANACEA_BIOTEC");
		stockName.add("CAREER_PT_INFOSY");
		stockName.add("EROS_INTL_MEDIA");
		stockName.add("GUJARAT_PIPAVAV");
		stockName.add("NORBEN_TEA_EXP");
		stockName.add("NOCIL_LTD");
		stockName.add("WELSPUN_INV_CO");
		stockName.add("ADF_FOODS_LIMITE");
		stockName.add("DEEPAK_NITRITE_L");
		stockName.add("ONGC_CORPN");
		stockName.add("OIL_COUNTRY_TUBU");
		stockName.add("ONWARD_TECHNOLOG");
		stockName.add("INDOSOLAR_LIMITE");
		stockName.add("SKS_MICROFINANCE");
		stockName.add("BAJAJ_CORP_LIMIT");
		stockName.add("INDUSTRIAL_INV_T");
		stockName.add("PARENTERAL_DRUGS");
		stockName.add("NATCO_PHARMA");
		stockName.add("HDFC_GOLD_ETF");
		stockName.add("NAVA_BHARAT_VENT");
		stockName.add("PRAKASH_STEELAGE");
		stockName.add("THE_UGAR_SUGAR_W");
		stockName.add("PIRAMAL_HEALTHCA");
		stockName.add("NILKAMAL");
		stockName.add("MUKAND_LTD");
		stockName.add("NCC_LIMITED");
		stockName.add("HEIDELBERGCEMENT");
		stockName.add("NAHAR_INDS_ENT_L");
		stockName.add("JAYASWALS_NECO");
		stockName.add("NAHAR_POLY_FILMS");
		stockName.add("ASIAN_HOTELS_EA");
		stockName.add("NAHAR_SPG");
		stockName.add("MOIL_LIMITED");
		stockName.add("ZEE_LEARN_LIMITE");
		stockName.add("PSL_LTD");
		stockName.add("PUNJAB_CHEMICALS");
		stockName.add("KESAR_TER_INFR");
		stockName.add("KIRLOSKAR_INDUST");
		stockName.add("AVI_KUMAR_DIST");
		stockName.add("RADICO_KHAITAN");
		stockName.add("RSWM_LTD");
		stockName.add("FUTURE_MARKET_NE");
		stockName.add("PUNJAB_SIND_BA");
		stockName.add("CURA_TECHNOLOGIE");
		stockName.add("AXIS_MF_AXIS_G");
		stockName.add("PENNAR_INDUSTRIE");
		stockName.add("JINDAL_WORLDWIDE");
		stockName.add("PRAKASH_INDS");
		stockName.add("PRAJ_INDS");
		stockName.add("R_P_P_INFRA_PROJ");
		stockName.add("PRECOT_MERIDIAN");
		stockName.add("PREMIER_LTD");
		stockName.add("PRECISION_WIRES");
		stockName.add("JAMNA_AUTO_IND_L");
		stockName.add("PAE_LTD");
		stockName.add("SUMEET_IND_LIMIT");
		stockName.add("PRISM_JOHNSON_LI");
		stockName.add("PRIME_SECURTIES");
		stockName.add("SEAMEC_LIMITED");
		stockName.add("PEARL_POLYMERS");
		stockName.add("PRESTIGE_ESTATE");
		stockName.add("GYSCOAL_ALLOYS_L");
		stockName.add("RESPONSIVE_INDUS");
		stockName.add("PETRON_ENGG");
		stockName.add("COAL_INDIA_LTD");
		stockName.add("ANDHRA_CEMENTS_L");
		stockName.add("IOL_CHEM_AND_PHA");
		stockName.add("PIDILITE_INDS");
		stockName.add("POLYPLEX_CORPN");
		stockName.add("GRAVITA_INDIA_LI");
		stockName.add("RAMKY_INFRA_LTD");
		stockName.add("ORIENT_GREEN_POW");
		stockName.add("GALLANTT_ISPAT_L");
		stockName.add("PAPER_PRODUCTS");
		stockName.add("CANTABIL_RETAIL");
		stockName.add("VA_TECH_WABAG_LT");
		stockName.add("BEDMUTHA_INDUST");
		stockName.add("ASHOKA_BUILDCON");
		stockName.add("ATSPIN_I");
		stockName.add("OMMERCIAL_ENG");
		stockName.add("OBEROI_REALTY_LI");
		stockName.add("DHARANI_SUGARS");
		stockName.add("OCL_IRON_AND_STE");
		stockName.add("DR_REDDY_S_LABS");
		stockName.add("ANDMARK_PR_DEV");
		stockName.add("EASTERN_SILK_IND");
		stockName.add("SICAGEN_INDIA_LI");
		stockName.add("EASUN_REYROL");
		stockName.add("NU_TEK_INDIA_LTD");
		stockName.add("EICHER_MOTORS");
		stockName.add("ELECON_ENGG");
		stockName.add("EID_PARRY");
		stockName.add("DCM_LTD");
		stockName.add("DCM_FINANCIAL");
		stockName.add("LOTUS_EYE_HOSPIT");
		stockName.add("KSK_ENERGY_VENTU");
		stockName.add("DCM_SHRIRAM_CONS");
		stockName.add("DEEPAK_FERT");
		stockName.add("DCW");
		stockName.add("KOKUYO_CAMLIN_LT");
		stockName.add("QUANTUM_INDEX_FU");
		stockName.add("UNITED_BREWERIES");
		stockName.add("DENA_BANK");
		stockName.add("LGB_FORGE_LIMITE");
		stockName.add("DHAMPUR_SUGAR_MI");
		stockName.add("RANE_BRAKE_LININ");
		stockName.add("COSMO_FILMS");
		stockName.add("CORPORATION_BANK");
		stockName.add("CRISIL_LTD");
		stockName.add("CROMPTON_GREAVES");
		stockName.add("MVL_LIMITED");
		stockName.add("ETKORE_ALLOYS");
		stockName.add("DALMIA_BHARAT_SU");
		stockName.add("ARCHIDPLY_IND_L");
		stockName.add("VIJAY_SHANTHI_BU");
		stockName.add("BAJAJ_AUTO_LIMIT");
		stockName.add("EXIDE_INDS");
		stockName.add("BAJAJ_FINSERV_LT");
		stockName.add("OKUL_REFOILS");
		stockName.add("PIRAMAL_LIFE_SIC");
		stockName.add("IC_I");
		stockName.add("RPG_LIFE_SCIENCE");
		stockName.add("CLARIANT_CHEMICA");
		stockName.add("ALMONDZ_GLOBAL_S");
		stockName.add("GHCL_LIMITED");
		stockName.add("GIC_HOUSING_FINA");
		stockName.add("JMERA_REALTY");
		stockName.add("GUJ_INDSTL_POWER");
		stockName.add("GINNI_FILAMENTS");
		stockName.add("GUJ_LEASE_FINANC");
		stockName.add("GLOBAL_TELE_SYST");
		stockName.add("GUJ_MINERAL");
		stockName.add("GUJ_NARMADA");
		stockName.add("GODFREY_PHILLIPS");
		stockName.add("ZENSAR_TECHNOLOG");
		stockName.add("OSECO_I");
		stockName.add("MAWANA_SUGARS_LI");
		stockName.add("AS_AUTHORTY_I");
		stockName.add("ABRIEL_I");
		stockName.add("SHARIAH_BENCHMAR");
		stockName.add("HARITA_SEATING_S");
		stockName.add("HIND_NATL_GLASS");
		stockName.add("GARDEN_SILK_MILL");
		stockName.add("ARROW_TEXTILES_L");
		stockName.add("EUROTEX_INDS");
		stockName.add("EVEREST_INDUSTRI");
		stockName.add("TATA_MOTORS_DVR");
		stockName.add("UTOLITE_INDIA");
		stockName.add("ALKALI_METALS_LT");
		stockName.add("FINOLEX_INDS");
		stockName.add("FINOLEX_CABLES");
		stockName.add("UFLEX_INDS");
		stockName.add("COUNTRY_COND_S_L");
		stockName.add("GI_ENGINEERING_S");
		stockName.add("ELECTRO_STEEL_CA");
		stockName.add("ELGI_EQUIPMENTS");
		stockName.add("EMCO_LTD");
		stockName.add("WABCO_TVS_INDIA");
		stockName.add("20_MICRONS_LTD");
		stockName.add("MERCK_I");
		stockName.add("JOCIL_LIMITED");
		stockName.add("INDBANK_MERCH_BA");
		stockName.add("NEULAND_LAB_LTD");
		stockName.add("SAB_I");
		stockName.add("ESSEL_PROPACK");
		stockName.add("HIND_OIL_EXPLORA");
		stockName.add("HIND_SYNTEX");
		stockName.add("HIND_ZINC");
		stockName.add("HINDUJA_VENTURES");
		stockName.add("SUNTECK_REALTY_L");
		stockName.add("SANOFI_INDIA_LTD");
		stockName.add("ZYDUS_WELLNESS_L");
		stockName.add("HOTEL_RUGBY");
		stockName.add("AMRUTAJAN_HEALTH");
		stockName.add("HARRISON_MALAYAL");
		stockName.add("HCL_INFOSYSTEMS");
		stockName.add("HEG_LTD");
		stockName.add("HERITAGE_FOODS");
		stockName.add("HIMATSINGKA_SEID");
		stockName.add("HIND_CONSTRUCTIO");
		stockName.add("GOLDEN_TOBACCO_L");
		stockName.add("GUJ_ST_FERTILIZE");
		stockName.add("GUJ_ALKALIES");
		stockName.add("GTN_INDS");
		stockName.add("GLOBUS_SPIRITS_L");
		stockName.add("JINDAL_COTEX_LTD");
		stockName.add("GUJ_APOLLO_INDS");
		stockName.add("OIL_INDIA_LTD");
		stockName.add("GUJ_FLOUROCHEM");
		stockName.add("EURO_MULTIVISION");
		stockName.add("FEDERAL_MOGUL_GO");
		stockName.add("MAHINDRA_HOLIDAY");
		stockName.add("GKW_LIMITED");
		stockName.add("KANSAI_NEROLAC");
		stockName.add("DELTA_MAGNETS_LT");
		stockName.add("VINATI_ORGANICS");
		stockName.add("AREGAMA_I");
		stockName.add("RAPHITE_I");
		stockName.add("GRINDWELL_NORTON");
		stockName.add("ADANI_POWER_LTD");
		stockName.add("NHPC_LTD");
		stockName.add("ITI_LTD");
		stockName.add("TRF_LIMITED");
		stockName.add("UNITED_NILGIRI_T");
		stockName.add("JAIN_IRRIGATION");
		stockName.add("JAGSONPAL_PHARM");
		stockName.add("JAIN_STUDIOS");
		stockName.add("WHIRLPOOL_OF_IND");
		stockName.add("MBL_INFRASTRUCTU");
		stockName.add("JAYANT_AGRO");
		stockName.add("JAYSHREE_TEA");
		stockName.add("JB_CHEM_PHARMA");
		stockName.add("BEARDSELL_LIMITE");
		stockName.add("BAYER_CROPSCIENC");
		stockName.add("INDIAN_OIL_CORP");
		stockName.add("IPCA_LABS");
		stockName.add("HINDUSTAN_COPPER");
		stockName.add("TATA_SPONGE_IRON");
		stockName.add("KWALITY_DAIRY_I");
		stockName.add("NESTLE_INDIA_LIM");
		stockName.add("MMTC_LIMITED");
		stockName.add("AGRO_TECH_FOODS");
		stockName.add("ARSHIYA_INTERNAT");
		stockName.add("AHLUWALIA_CONT_I");
		stockName.add("INDIAN_HUME_PIPE");
		stockName.add("INDIAN_CARD_CLOT");
		stockName.add("GILLANDERS_ARBUT");
		stockName.add("INDIA_NIPPON_ELE");
		stockName.add("D_LINK_INDIA_LTD");
		stockName.add("INDO_COUNT_INDS");
		stockName.add("JSW_ENERGY_LIMIT");
		stockName.add("GODREJ_PROPERTIE");
		stockName.add("INDO_RAMA_SYNTH");
		stockName.add("D_B_CORP_LTD");
		stockName.add("INDRAPRASTHA");
		stockName.add("ABBOTT_INDIA_LIM");
		stockName.add("DEN_NETWORKS_LTD");
		stockName.add("ASTEC_LIFESCIENC");
		stockName.add("ICICI_BANK");
		stockName.add("AKZO_NOBEL_INDIA");
		stockName.add("IFB_AGRO_INDS");
		stockName.add("REFEX_REFRIGERAN");
		stockName.add("IG_PETROCHEMICAL");
		stockName.add("IFB_INDS");
		stockName.add("SHILPA_MEDICARE");
		stockName.add("SARDA_ENERGY");
		stockName.add("INDIA_CEMENTS");
		stockName.add("OX_KINGS_I");
		boolean matchFound = false;

		for (String nm : stockName) {
			if ((stockNm + ("_LTD").toLowerCase()).equalsIgnoreCase(nm.toLowerCase()) || (stockNm + ("_BANK").toLowerCase()).equalsIgnoreCase(nm.toLowerCase())
					|| (stockNm.toLowerCase()).equalsIgnoreCase(nm.toLowerCase()) || (stockNm + ("_LIMITED").toLowerCase()).equalsIgnoreCase(nm.toLowerCase())
					|| (stockNm.toLowerCase()).equalsIgnoreCase(nm.toLowerCase())) {
				result = nm;
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("NIFTY") || (stockNm.toLowerCase()).equalsIgnoreCase("NFTYMCAP50")) {
				result = "NIFTY_MIDCAP";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SUNPHARMA")||(stockNm.toLowerCase()).equalsIgnoreCase("Sun_Pharma")) {
				result = "SUN_PHARMACEUTIC";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SUNTV") || (stockNm.toLowerCase()).equalsIgnoreCase("sun_tv_network_l")) {
				result = "SUN_TV";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BANKNIFTY")) {
				result = "CNX_BANK_INDEX";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SRTRANSFIN")) {
				result = "SHRIRAM_TRANS_FI";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SBIN") || (stockNm.toLowerCase()).equalsIgnoreCase("SBI")) {
				result = "STATE_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SAIL")) {
				result = "STEEL_AUTHORITY";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("RPOWER")) {
				result = "RELIANCE_POWER_L";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("RELINFRA")) {
				result = "RELIANCE_INFRAST";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("RELIANCE")) {
				result = "RELIANCE_INDUSTR";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("RELCAPITAL")) {
				result = "RELIANCE_CAPITAL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("RCOM")) {
				result = "RELIANCE_COMMUNI";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("PTC")) {
				result = "PTC_INDIA_FIN_SE";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("POWERGRID")) {
				result = "POWERGRID_CORPOR";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("PFC")) {
				result = "POWER_FINANCE_CO";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("PFC")) {
				result = "POWER_FINANCE_CO";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("PETRONET")) {
				result = "PETRONET_LNG";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("PANTALOONR") || (stockNm.toLowerCase()).equalsIgnoreCase("ABIRLANUVO")) {
				result = "ADITYA_BIRLA_FAS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ORIENTBANK") || (stockNm.toLowerCase()).equalsIgnoreCase("Oriental_Bank_Of")) {
				result = "ORIENTAL_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("OFSS")) {
				result = "ORACLE_FINANCIAL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("MCLEODRUSS")) {
				result = "MCLEOD_RUSSEL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("MARUTI")) {
				result = "MARUTI_UDYOG";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ADANIPOWER")) {
				result = "ADANI_POWER_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ALBK")) {
				result = "ALLAHABAD_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("AMBUJACEM")) {
				result = "AMBUJA_CEMENTS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ANDHRABANK")) {
				result = "ANDHRA_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("APOLLOTYRE") || (stockNm.toLowerCase()).equalsIgnoreCase("apollo_tyres_ltd")) {
				result = "APOLLO_TYRES";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ASHOKLEY")) {
				result = "ASHOK_LEYLAND";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ASIANPAINT") || (stockNm.toLowerCase()).equalsIgnoreCase("asian_paints_lim")) {
				result = "ASIAN_PAINTS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("AUROPHARMA")) {
				result = "AUROBINDO_PHARMA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("AXISBANK")) {
				result = "AXIS_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("AXISBANK")) {
				result = "AXIS_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BAJAJ-AUTO")) {
				result = "BAJAJ_AUTO_LIMIT";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BAJAJ-AUTO")) {
				result = "BAJAJ_AUTO_LIMIT";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BANKBARODA")) {
				result = "BANK_OF_BARODA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BANKBARODA")) {
				result = "BANK_OF_BARODA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BANKINDIA") || (stockNm.toLowerCase()).equalsIgnoreCase("bank_of_india_fu")) {
				result = "BANK_OF_INDIA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BHARTIARTL") || (stockNm.toLowerCase()).equalsIgnoreCase("bharti_airtel_li")) {
				result = "BHARTI_AIRTEL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TITAN")) {
				result = "TITAN_INDS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ULTRACEMCO") || (stockNm.toLowerCase()).equalsIgnoreCase("ULTRATECH_CEMENT")) {
				result = "ULTRATECH_CEMCO";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("INDUSINDBK") || (stockNm.toLowerCase()).equalsIgnoreCase("indusind_bank_fu")) {
				result = "INDUSIND_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ONGC")) {
				result = "ONGC_CORPN";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("UNIONBANK")) {
				result = "UNION_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IDEA")) {
				result = "IDEA_CELLULAR";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HINDPETRO")) {
				result = "HIND_PETROLEUM";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BOSCHLTD")) {
				result = "BOSCH_LIMITED";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HINDZINC") || (stockNm.toLowerCase()).equalsIgnoreCase("HINDUSTAN_ZINC_L")) {
				result = "HIND_ZINC";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ICICIBANK")) {
				result = "ICICI_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATACOMM")) {
				result = "TATA_COMMUNICATI";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("YESBANK")) {
				result = "YES_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("INFY") || (stockNm.toLowerCase()).equalsIgnoreCase("Infosys")) {
				result = "INFOSYS_LIMITED";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATASTEEL")) {
				result = "TATA_STEEL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATAPOWER") || (stockNm.toLowerCase()).equalsIgnoreCase("Tata_Power") || (stockNm.toLowerCase()).equalsIgnoreCase("Tata_Power")) {
				result = "TATA_POWER_COMP";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATAMTRDVR")) {
				result = "TATA_MOTORS_DVR";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATAGLOBAL")) {
				result = "TATA_GLOBAL_BEVE";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATACHEM")||(stockNm.toLowerCase()).equalsIgnoreCase("Tata_Chem")) {
				result = "TATA_CHEMICAL_LI";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SYNDIBANK")) {
				result = "SYNDICATE_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HAVELLS") || (stockNm.toLowerCase()).equalsIgnoreCase("havells_india_li")) {
				result = "HAVELLS_INDIA";
				matchFound = true;
			}

			else if ((stockNm.toLowerCase()).equalsIgnoreCase("KOTAKBANK") || (stockNm.toLowerCase()).equalsIgnoreCase("KOTAK_BANK")) {
				result = "KOTAK_MAHINDRA_B";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JSWSTEEL")) {
				result = "JSW_HOLDINGS_LIM";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JINDALSTEL")) {
				result = "JINDAL_STEEL_POW";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("INDIACEM")) {
				result = "INDIA_CEMENTS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HINDUNILVR") || (stockNm.toLowerCase()).equalsIgnoreCase("HINDUNILVR")) {
				result = "HIND_UNILEVER";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HDFCBANK")) {
				result = "HDFC_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HCLTECH") || (stockNm.toLowerCase()).equalsIgnoreCase("HCL_Tech")) {
				result = "HCL_TECHNOLOGIES";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("GODREJIND")) {
				result = "GODREJ_INDS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("GMRINFRA")) {
				result = "GMR_INFRASTRUCTU";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("FEDERALBNK")) {
				result = "FEDERAL_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("DIVISLAB")) {
				result = "DIVIS_LABS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("CENTURYTEX") || (stockNm.toLowerCase()).equalsIgnoreCase("century_textiles")) {
				result = "CENTURY_TEXTILES";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("CANBK")) {
				result = "CANARA_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("CNXIT")) {
				result = "CNX_IT_INDEX";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("LT")) {
				result = "LARSEN_TOUBRO";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("LICHSGFIN")) {
				result = "LIC_HOUSING_FIN";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TVSMOTOR")) {
				result = "TVS_MOTOR";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("UCOBANK")) {
				result = "UCO_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IRB")) {
				result = "IRB_INFRA_DEV_LT";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("OIL")) {
				result = "OIL_INDIA_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("PNB")) {
				result = "PUNJAB_NATIONAL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("LUPIN") || (stockNm.toLowerCase()).equalsIgnoreCase("LUPIN_LTD")) {
				result = "LUPIN_LABS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HINDALCO")) {
				result = "HINDALCO_INDS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IDBI")) {
				result = "IDBI_MF_IDBI_G";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IDFC")) {
				result = "IDFC_BANK_LIMITE";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HEXAWARE")) {
				result = "HEXAWARE_TECHNOL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ZEEL")) {
				result = "ZEE_ENTERTAINMEN";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ZEEMEDIA")) {
				result = "ZEE_MEDIA_CORPOR";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ZEELEARN")) {
				result = "ZEE_LEARN_LIMITE";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TATAMOTORS") || (stockNm.toLowerCase()).equalsIgnoreCase("TATAMOTORS")
					|| (stockNm.toLowerCase()).equalsIgnoreCase("tata_motors_ltd")) {
				result = "TATA_MOTORS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("TECHM")) {
				result = "TECH_MAHINDRA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JPPOWER")) {
				result = "JAIPRAKASH_POWER";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("M&M") || (stockNm.toLowerCase()).equalsIgnoreCase("M_M")) {
				result = "MAHINDRA_MAHIN";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("COLPAL") || (stockNm.toLowerCase()).equalsIgnoreCase("COLGATE")) {
				result = "COLGATE_PALMOLIV";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("CROMPGREAV")) {
				result = "CROMPTON_GREAVES";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("DRREDDY")) {
				result = "DR_REDDY_S_LABS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("EXIDEIND")) {
				result = "EXIDE_INDS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("GRASIM")) {
				result = "GRASIM_INDS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HDIL")) {
				result = "HOUSING_DEV_IN";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IBREALEST")) {
				result = "INDIABULLS_REAL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IOB")) {
				result = "INDIAN_OVERSEAS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IOB")) {
				result = "INDIAN_OVERSEAS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IOC")) {
				result = "INDIAN_OIL_CORP";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JISLJALEQS")) {
				result = "JAIN_IRRIGATION";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JPASSOCIAT")) {
				result = "JAIPRAKASH_ASSOC";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JPASSOCIAT")) {
				result = "JAIPRAKASH_ASSOC";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IGL") || (stockNm.toLowerCase()).equalsIgnoreCase("indraprastha_gas")) {
				result = "INDRAPRASTHA_GAS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("IGL")) {
				result = "INDRAPRASTHA_GAS";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("KTKBANK")) {
				result = "KARNATAKA_BANK";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("Arvind_Mills_F1") || (stockNm.toLowerCase()).equalsIgnoreCase("Arvind_Mills")) {
				result = "ARVIND_LIMITED";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("BPCL")) {
				result = "BHARAT_PETROLEUM";
				matchFound = true;
			} /* new table added to list */else if ((stockNm.toLowerCase()).equalsIgnoreCase("GAIL")) {
				result = "GAIL";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("AMBUJA_CEMENTS_L")) {
				result = "AMBUJA_CEMENTS";
				matchFound = true;
			} /* new table added to list */ else if ((stockNm.toLowerCase()).equalsIgnoreCase("Hindustan_Oil_Ex")) {
				result = "HOEC";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("HPCL")) {
				result = "HIND_PETROLEUM";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ITC")) {
				result = "ITC_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("MARUTI_SUZUKI_IN")) {
				result = "MARUTI_UDYOG";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("SESA_GOA")) {
				result = "MARUTI_UDYOG";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("JYOTHYLAB")) {
				result = "JYOTHY_LABS_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("WOCKPHARMA")) {
				result = "WOCKHARDT_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("MOTHERSUMI")) {
				result = "MOTHERSON_SUMI";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("EROSMEDIA")) {
				result = "EROS_INTL_MEDIA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("dlf_ltd") || (stockNm.toLowerCase()).equalsIgnoreCase("DLF_LTD_F1")) {
				result = "DLF";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("dish_tv_india_lt")) {
				result = "DISH_TV_I";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("ata_india") || (stockNm.toLowerCase()).equalsIgnoreCase("BATA_INDIA_F1") || (stockNm.toLowerCase()).equalsIgnoreCase("BATA_INDIA")) {
				result = "BATA_INDIA";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("mercator_lines_l")) {
				result = "MERCATOR_LINES";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("unitech_ltd")) {
				result = "UNITECH";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("unitech_ltd")) {
				result = "UNITECH";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("WELCORP")) {
				result = "WELSPUN_CORP_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("DELTACORP")) {
				result = "DELTA_CORP_LTD";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("AMARAJABAT")) {
				result = "AMARA_RAJA_BATTE";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("Dhampur_Sugar")) {
				result = "DHAMPUR_SUGAR_MI";
				matchFound = true;
			} else if ((stockNm.toLowerCase()).equalsIgnoreCase("Herohonda")) {
				result = "HERO_HONDA";
				matchFound = true;
			}
		}
		int i = 0;

		if (!matchFound) {
			// System.out.println(i + " in else :" + stockNm);
			i++;
		}
		int j = 0;
		if (matchFound) {
			// System.out.println(j+" match found :"+result);
			j++;
		}

		return result;
	}

}