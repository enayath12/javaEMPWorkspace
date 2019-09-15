package testingEMP;
import java.io.BufferedReader;
import java.io.File;
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



	public static boolean readFileToString(File folder, String zipFileName, String fileName) throws IOException {
		
		fileName = FilenameUtils.removeExtension(fileName);
		int TKDstockId = Integer.parseInt(fileName);

		String stockName = fiveminuteStockName(TKDstockId);
		String tableName =StockDetails.getTableName(stockName.replace("-F1", "").replace("_F1", ""));
		if(tableName.isEmpty()){
		System.out.println(stockName+"                 the table name for Five Min data is empty      ::        "+tableName);
		}
		//System.out.println("     stockName                 "+stockName+"                               tableName           "+tableName+"                                   TKDstockId           "+TKDstockId);
		
		int primaryKey = 1;
		primaryKey = StockDetails.getTableMaxId(tableName);
		
		// System.out.println(primaryKey+" tableName     "+tableName+"      stockName        "+stockName+System.lineSeparator());
		 List<String> statements = new ArrayList<>();
			String headerText = "";
			String dropCreateTable = "";
			String dumpingData = "";
			String lockTable = "";
			if(!sqlDumpFolder.isDirectory()){
				sqlDumpFolder.mkdirs();
			}
			
			String absoluteFilePath =sqlDumpFolder+File.separator+stockName+".sql";
		    //  System.out.println("absoluteFilePath            ::  " + absoluteFilePath);
	        File file = new File(absoluteFilePath);
	         if(file.createNewFile()){
	            System.out.println(absoluteFilePath+" File Created  stockName                 "+stockName+"                               tableName           "+tableName);
	        }else {
	        	System.out.println("File "+absoluteFilePath+" already exists   stockName                 "+stockName+"                               tableName           "+tableName);
	        }

	        // here we get all the files in zip
	         
			// Zip file name will be stored along with the data in each row for referenc

			// System.out.println(" stockName     ::  " +stockName  +"  tableName   "+tableName+"   TKDstockId   "+TKDstockId+"    primaryKey  "+primaryKey); 
			
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
				//System.out.println("file  ::  " +FilenameUtils.removeExtension(fileName));
				
				if (folder.isFile() && (folder.getName().endsWith(".txt") || folder.getName().endsWith(".TXT") || folder.getName().endsWith(".csv") || folder.getName().endsWith(".CSV"))) {
					 intoValues =EMP(folder.toString(),intoValues,stockName,TKDstockId, tableName, primaryKey);
				}

				String unlockTable = " UNLOCK TABLES;" + System.lineSeparator();
				String dumpComplete = "-- Dump completed on 2019-01-16 13:11:21" + System.lineSeparator();
				String allInOne = headerText + System.lineSeparator() + dropCreateTable + System.lineSeparator() + dumpingData + System.lineSeparator() + lockTable + 
						System.lineSeparator() + intoValues
						+ System.lineSeparator() + unlockTable + System.lineSeparator() + dumpComplete;

				statements.add(allInOne);
				//System.out.println("statements  ::  " + statements.size());
				//absolute file name with path
				if(!sqlDumpFolder.isDirectory()){
					sqlDumpFolder.mkdirs();
				}
				
		        
				Files.write(Paths.get(absoluteFilePath), statements);

		return true;
	}


	public static StringBuilder EMP(String destDirnew, StringBuilder intoValues, String stockName, int TKDstockId, String tableName, int primaryKey) throws IOException {

		// System.out.println("destDirnew :: " + destDirnew);

		String line = "";
		String cvsSplitBy = ";";
		String TimeFrame = "5 Min";
		intoValues.append("INSERT INTO `" + tableName + "` VALUES");
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
					intoValues.append("(" + primaryKey + ",'" + TKDstockId + "','" + stockName + "','" + TimeFrame + "','" + stockDate + "','" + open + "','" + high + "','" + low + "','" + close + "','"
							+ volume + "')");

					addsemicolon = true;
					primaryKey++;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					//System.out.println(e.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//System.out.println(e.getMessage());
				}
			}
			intoValues.append(";");
		} catch (IOException e) {
			System.out.println(e.getMessage());
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


	public static String fiveminuteStockName(Integer stockCode) {

		String stockName = "";
		HashMap<Integer, String> map = new HashMap<Integer, String>();

		map.put(9069,"Unitech_Ltd");
		map.put(9068,"Global_Tele");
		map.put(9502,"Arvind_Mills_F1");
		map.put(9503,"Bank_Of_India_F1");
		map.put(9745,"REC_LTD_F1");
		map.put(9500,"ACC_F1");
		map.put(9621,"SUN_TV_NETWORK_L");
		map.put(9061,"Balrampur_Chini");
		map.put(9062,"Steel_Authority");
		map.put(9060,"India_Cem");
		map.put(9065,"Bombay_Dyeing");
		map.put(9066,"Voltas");
		map.put(9616,"HDFC_F1");
		map.put(9617,"HCLTech_F1");
		map.put(9618,"Bharat_Forg_F1");
		map.put(9619,"Bank_Nifty_F1");
		map.put(9630,"ZEE_ENTERTAINMEN");
		map.put(9510,"HDFC_Bank_F1");
		map.put(9078,"DLF");
		map.put(9750,"ASIAN_PAINTS_LIM");
		map.put(9079,"COAL_INDIA_LTD");
		map.put(9513,"ICICI_Bank_F1");
		map.put(9755,"DISH_TV_INDIA_LT");
		map.put(9514,"Infosys_F1");
		map.put(9511,"Herohonda_F1");
		map.put(9512,"HPCL_F1");
		map.put(9073,"Indiabulls_Real");
		map.put(9076,"RELIANCE_INFRAST");
		map.put(9077,"BHEL");
		map.put(9075,"CIPLA");
		map.put(9506,"Canara_Bank_F1");
		map.put(9627,"Larsen_Toubro");
		map.put(9507,"Century_Textiles");
		map.put(9504,"Bhel_F1");
		map.put(9746,"BAJAJ_AUTO_LTD_F1");
		map.put(9505,"BPCL_F1");
		map.put(9626,"Gmrinfra_F1");
		map.put(9508,"CIPLA_F1");
		map.put(9629,"Steel_Authority");
		map.put(9509,"AMBUJA_CEMENTS_L");
		map.put(9520,"Reliance_Capital");
		map.put(9521,"Reliance_F1");
		map.put(9089,"HINDUNILVR");
		map.put(9525,"TATAMOTORS_F1");
		map.put(9523,"SBI_F1");
		map.put(9083,"KOTAK_BANK");
		map.put(9084,"JSW_STEEL");
		map.put(9082,"GRASIM");
		map.put(9087,"DRREDDY");
		map.put(9088,"HINDALCO");
		map.put(9085,"AMBUJACEM");
		map.put(9086,"BAJAJ_AUTO");
		map.put(9080,"AXIS_BANK");
		map.put(9517,"M_M_F1");
		map.put(9638,"BATA_INDIA_F1");
		map.put(9518,"NTPC_F1");
		map.put(9515,"ITC_F1");
		map.put(9519,"ONGC_F1");
		map.put(9531,"MARUTI_SUZUKI_IN");
		map.put(9532,"Nifty_F1");
		map.put(9650,"LUPIN_LTD_F1");
		map.put(9651,"UNITED_SPIRITS_F1");
		map.put(9535,"Wipro_F1");
		map.put(9533,"CNXIT_F1");
		map.put(9655,"SESA_GOA_F1");
		map.put(9095,"SIEMENS");
		map.put(9092,"NTPC");
		map.put(9093,"POWERGRID");
		map.put(9098,"LICHSGFIN");
		map.put(9770,"MRF_LTD_F1");
		map.put(9097,"SUNPHARMA");
		map.put(9090,"HDFC");
		map.put(9091,"JINDALSTEL");
		map.put(9528,"TATASTEEL_F1");
		map.put(9649,"KOTAK_MAHINDRA_B");
		map.put(9529,"Union_Bank_F1");
		map.put(9526,"Tata_Power_F1");
		map.put(9527,"TCS_F1");
		map.put(9648,"JSW_STEEL_F1");
		map.put(9542,"Tata_Chem_F1");
		map.put(9663,"CNXIT_F1");
		map.put(9664,"Nifty_F1");
		map.put(9540,"Titan_Inds_F1");
		map.put(9541,"TATAGLOBAL_F1");
		map.put(9662,"Bank_Nifty_F1");
		map.put(9788,"ADANIPOWER_LTD_F1");
		map.put(9789,"APOLLO_TYRES_LTD");
		map.put(9544,"Sun_Pharma_F1");
		map.put(9665,"PFC_F1");
		map.put(9666,"IDEA_F1");
		map.put(9787,"ADANI_ENTERPRISE");
		map.put(9658,"ULTRATECH_CEMENT");
		map.put(9538,"AXIS_Bank_F1");
		map.put(9659,"VOLTAS_LTD_F1");
		map.put(9553,"Punjab_National");
		map.put(9796,"MUNDRA_PORT_SE");
		map.put(9794,"HEXAWARE_LTD_F1");
		map.put(9799,"SHRIRAM_TRANSPOR");
		map.put(9550,"RELIANCE_INFRAST");
		map.put(9548,"Siemens_F1");
		map.put(9569,"Jindal_Steel_F1");
		map.put(9566,"LIC_Housing_Fin");
		map.put(9573,"IOC_F1");
		map.put(9574,"OFSS_F1");
		map.put(9579,"Hindalco_F1");
		map.put(9577,"IDBI_F1");
		map.put(9698,"UNIPHOS_F1");
		map.put(9578,"HINDUSTAN_UNILEV");
		map.put(9690,"PETRONET_F1");
		map.put(9102,"BANKINDIA");
		map.put(9103,"FINANTECH");
		map.put(9584,"Gail_F1");
		map.put(9100,"JUBLFOOD");
		map.put(9585,"Federal_Bank_F1");
		map.put(9101,"ALBK");
		map.put(9106,"YESBANK");
		map.put(9107,"HDIL");
		map.put(9588,"Dr_Reddy_F1");
		map.put(9104,"ASIANPAINT");
		map.put(9589,"Divis_Lab_F1");
		map.put(9105,"INDUSINDBK");
		map.put(9580,"Grasim_F1");
		map.put(9597,"CESC_F1");
		map.put(9113,"IDBI");
		map.put(9114,"INDIANB");
		map.put(9111,"RECLTD");
		map.put(9112,"PFC");
		map.put(9117,"HITACHIHOM");
		map.put(9118,"HAVELLS");
		map.put(9115,"TATAGLOBAL");
		map.put(9116,"JYOTHYLAB");
		map.put(9590,"Dabur_F1");
		map.put(9593,"Colgate_F1");
		map.put(9110,"IDFC");
		map.put(9108,"KTKBANK");
		map.put(9109,"PANTALOONR");
		map.put(9003,"BPCL");
		map.put(9124,"ABAN");
		map.put(9004,"Canara_Bank");
		map.put(9125,"DABUR");
		map.put(9001,"Bank_Of_Baroda");
		map.put(9122,"DIVISLAB");
		map.put(9002,"BHARTI_AIRTEL_LI");
		map.put(9123,"AUROPHARMA");
		map.put(9007,"GAIL");
		map.put(9128,"IDEA");
		map.put(9005,"Century_Textiles");
		map.put(9126,"CESC");
		map.put(9006,"Dhampur_Sugar");
		map.put(9000,"ACC");
		map.put(9121,"LINDEINDIA");
		map.put(9014,"Infosys");
		map.put(9135,"HEXAWARE");
		map.put(9136,"APOLLOTYRE");
		map.put(9012,"Herohonda");
		map.put(9133,"UPL");
		map.put(9013,"HPCL");
		map.put(9139,"ADANIPORTS");
		map.put(9019,"Jindal_Stainless");
		map.put(9016,"ITC");
		map.put(9137,"ABB");
		map.put(9017,"ICICI_Bank");
		map.put(9138,"ADANIENT");
		map.put(9010,"Hindustan_Oil_Ex");
		map.put(9131,"BHARATFORG");
		map.put(9011,"HDFC_Bank");
		map.put(9132,"BIOCON");
		map.put(9130,"CROMPGREAV");
		map.put(9804,"INDRAPRASTHA_GAS");
		map.put(9009,"HCL_Tech");
		map.put(9802,"EXIDE_INDUSTRIES");
		map.put(9808,"NMDC_LTD_F1");
		map.put(9025,"Oriental_Bank_Of");
		map.put(9146,"DELTACORP");
		map.put(9026,"Punjab_National");
		map.put(9144,"TIMKEN");
		map.put(9024,"ONGC");
		map.put(9145,"WELCORP");
		map.put(9029,"Reliance");
		map.put(9821,"APOLLOHOSP_F1");
		map.put(9822,"GLENMARK_F1");
		map.put(9148,"BEML");
		map.put(9028,"Reliance_Capital");
		map.put(9820,"COALINDIA_F1");
		map.put(9149,"MOTHERSUMI");
		map.put(9142,"TECHM");
		map.put(9022,"M_M");
		map.put(9143,"TIMETECHNO");
		map.put(9020,"MARUTI_SUZUKI_IN");
		map.put(9141,"WOCKPHARMA");
		map.put(9812,"TATA_MOTORS_LTD");
		map.put(9816,"JUBLFOOD_LTD_F1");
		map.put(9036,"Titan");
		map.put(9157,"PVR");
		map.put(9037,"TATASTEEL");
		map.put(9158,"EROSMEDIA");
		map.put(9034,"Tata_Power");
		map.put(9155,"COLGATE");
		map.put(9035,"TCS");
		map.put(9156,"TATAELXSI");
		map.put(9712,"BIOCON_LIMITED_F1");
		map.put(9038,"TATACOMM");
		map.put(9830,"TVSMOTOR_F1");
		map.put(9039,"Wipro");
		map.put(9150,"KPIT");
		map.put(9032,"SBI");
		map.put(9153,"IOC");
		map.put(9033,"TATAMOTORS");
		map.put(9154,"ASHOKLEY");
		map.put(9151,"CRISIL");
		map.put(9152,"BIRLACORPN");
		map.put(9825,"M_MFIN_F1");
		map.put(9826,"UBL_F1");
		map.put(9823,"JUSTDIAL_F1");
		map.put(9703,"DLF_LTD_F1");
		map.put(9824,"L_TFH_F1");
		map.put(9829,"MOTHERSUMI_F1");
		map.put(9827,"EICHERMOT_F1");
		map.put(9828,"MINDTREE_F1");
		map.put(9047,"Sesa_Goa_Min");
		map.put(9722,"YESBANK_LTD_F1");
		map.put(9843,"IBULHSGFIN_F1");
		map.put(9602,"Bank_of_Baroda_F1");
		map.put(9723,"POWERGRID_F1");
		map.put(9841,"DHFL_F1");
		map.put(9600,"BHARTI_AIRTEL_LI");
		map.put(9842,"ENGINERSIN_F1");
		map.put(9040,"ZEE_ENTERTAINMEN");
		map.put(9043,"Escorts");
		map.put(9044,"Mc_Dowell");
		map.put(9042,"Bata_India");
		map.put(9836,"BEL_F1");
		map.put(9837,"BOSCHLTD_F1");
		map.put(9834,"AMARAJABAT_F1");
		map.put(9714,"HAVELLS_INDIA_LI");
		map.put(9835,"BAJFINANCE_F1");
		map.put(9719,"TECHMAHINDRA_LTD");
		map.put(9838,"BRITANNIA_F1");
		map.put(9839,"CASTROLIND_F1");
		map.put(9850,"SRF_F1");
		map.put(9059,"Larsen_Toubra");
		map.put(9730,"HINDUSTAN_ZINC_L");
		map.put(9851,"STAR_F1");
		map.put(9056,"Nagarjuna_Const");
		map.put(9613,"Indusind_Bank_F1");
		map.put(9051,"Eveready");
		map.put(9054,"Mercator_Lines_L");
		map.put(9055,"Mid_Day_Multimed");
		map.put(9052,"Flex_Ind");
		map.put(9605,"Ashok_Ley_F1");
		map.put(9847,"PIDILITe_IND_F1");
		map.put(9848,"SKSMICRO_F1");
		map.put(9845,"OIL_F1");
		map.put(9604,"Aurobindo_Pharma");
		map.put(9846,"PAGEIND_F1");
		map.put(9047,"sesa_goa");
		map.put(9159,"justdial");
		map.put(9160,"arvind");
		map.put(9161,"infratel");
		map.put(9162,"bosch_ltd");
		map.put(9163,"eichermot");
		map.put(9164,"ibulhsgfin");
		map.put(9165,"tatamtrdvr");
		map.put(9166,"ultracemco");
		map.put(9167,"lupin");
		map.put(9168,"rbl_bank");
		map.put(9169,"bharat_fin");
		map.put(9170,"enginersin");
		map.put(9171,"dmart");
		map.put(9172,"dcb_bank");
		map.put(9173,"idfc_bank");
		map.put(9175,"la_opala");
		map.put(9176,"granules");
		map.put(9177,"ujjivan");
		map.put(9178,"jk_tyre");
		map.put(9179,"webel_solar");
		map.put(9180,"fortis");
		map.put(9181,"raymond");
		map.put(9182,"amarajabat");
		map.put(9183,"pidilite_ind");
		map.put(9184,"apollo_hosp");
		map.put(9185,"balkris_ind");
		map.put(9186,"can_fin_homes");
		map.put(9187,"glenmark");
		map.put(9188,"tvs_motor");
		map.put(9189,"niit_tech");
		map.put(9190,"graphite");
		map.put(9191,"8k_miles");
		map.put(9192,"SADBHAV");
		map.put(9193,"sbi_life");
		map.put(9194,"radicokhai");
		map.put(9852,"cadilahc_F1");
		map.put(9854,"tornt_pharm_F1");
		map.put(9855,"raymond_F1");
		map.put(9857,"escorts_F1");
		map.put(9860,"bajaj_finsv_F1");
		map.put(9861,"balkris_ind_F1");
		map.put(9862,"cummins_ind_F1");
		map.put(9863,"berger_paint_F1");
		map.put(9864,"bsoft_F1");
		map.put(9865,"chola_fin_F1");
		map.put(9866,"concor_F1");
		map.put(9867,"equitas_F1");
		map.put(9868,"godrej_cp_F1");
		map.put(9869,"icici_pruli_F1");
		map.put(9870,"indigo_F1");
		map.put(9871,"infratel_F1");
		map.put(9872,"kajariacer_F1");
		map.put(9873,"manappuram_F1");
		map.put(9874,"marico_F1");
		map.put(9875,"mcx_F1");
		map.put(9876,"mfsl_F1");
		map.put(9877,"mgl_F1");
		map.put(9878,"muthoot_fin_F1");
		map.put(9879,"ncc_F1");
		map.put(9880,"nestle_ind_F1");
		map.put(9881,"niit_tech_F1");
		map.put(9882,"pel_F1");
		map.put(9883,"pvr_F1");
		map.put(9884,"ramco_cem_F1");
		map.put(9885,"rbl_bank_F1");
		map.put(9886,"shree_cem_F1");
		map.put(9887,"tata_elxsi_F1");
		map.put(9888,"tornt_power_F1");
		map.put(9889,"ujjivan_F1");

		for (Map.Entry<Integer, String> stckNm : map.entrySet()) {
			// System.out.println(stckNm.getKey() + " :: value : " +stckNm.getValue() );

			if (stockCode.equals(stckNm.getKey())) {

				stockName = stckNm.getValue();
			}
		}
		return stockName;
	}



	
}
