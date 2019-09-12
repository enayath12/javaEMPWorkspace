
package com.journaldev.first;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet implementation class FirstServlet0015
 */
@WebServlet(description = "My First Servlet", urlPatterns = { "/FirstServlet0015", "/FirstServlet0015.do" }, initParams = {
		@WebInitParam(name = "id", value = "1"), @WebInitParam(name = "name", value = "pankaj") })
public class FirstServlet0015 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String HTML_START = "<html><body>";
	public static final String HTML_END = "</body></html>";
	List<String> fileList;
	public static final int maxFileSize = 50000 * 1024;
	public static final int maxMemSize = 50000 * 1024;
	public static final String temp_parent_zip = "E:/stock test/temp parent zip/";
	public static final String temp_child_zip = "E:\\stock test\\temp child zip\\";
	public static final String temp_Individual_file = "E:\\stock test\\temp Individual file\\";
	public static final String temp_grand_child_zip = "E:\\stock test\\temp grand child zip\\";
	public static final String temp_folder_c_drive = "C:/Users/en354899/AppData/Local/Temp/";
	
	 // JDBC driver name and database URL
	  public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	  public static final String DB_URL = "jdbc:mysql://localhost/backtesting";

	  //  Database credentials
	  public static final String USER = "root";
	  public static final String PASS = "1234";
	  public static  Connection  connection = null;
	  public static  Statement statement = null;
	 
	
	
	public static final File zipFile = new File(temp_parent_zip);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FirstServlet0015() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(" .. get ");
		PrintWriter out = response.getWriter();
		Date date = new Date();
		out.println(HTML_START + "<h2>Hi There!</h2><br/><h3>Date=" + date + "</h3>" + HTML_END);

	}

	/**
	 * Unzip it
	 * 
	 * @param zipFile
	 * input zip file
	 * @param output
	 * zip file output folder
	 */
	
	public void unZipIt(File zipFile, String outputFolder) {

		byte[] buffer = new byte[1024];

		try {
			System.out.println("outputFolder" + outputFolder);
			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();

				boolean directory = ze.isDirectory();

				if (directory) {
					outputFolder = outputFolder + "\\" + fileName;
					folder = new File(outputFolder);
					if (!folder.exists()) {
						folder.mkdir();
					}
					File newFile = new File(outputFolder + File.separator + fileName);
					// create all non exists folders
					// else you will hit FileNotFoundException for compressed
					// folder
				//	System.out.println("file getParent : " + newFile.getName());
					new File(newFile.getParent() + "\\" + newFile.getName()).mkdir();
				//	System.out.println("file getParent : " + newFile.getParent());
					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
					ze = zis.getNextEntry();
				} else {

					File newFile = new File(outputFolder + File.separator + fileName);

				//	System.out.println("file unzip : " + newFile.getAbsoluteFile());
				//	System.out.println("file newFile.getParent() : " + newFile.getParent());

					// create all non exists folders
					// else you will hit FileNotFoundException for compressed
					// folder
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					readTextFileLineByLine(newFile.getAbsoluteFile());
					fos.close();
					ze = zis.getNextEntry();
				}

			}
			zis.closeEntry();
			zis.close();

			//System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String contentType = request.getContentType();
		 try {
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  
	if ((contentType.indexOf("multipart/form-data") >= 0)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(maxMemSize);
			factory.setRepository(new File("c:\\temp"));
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(maxFileSize);
			System.out.println("   zipFile  before extractparentzip "+zipFile.getAbsolutePath() );
			extractParentZip(upload, request, response, zipFile);
		} else {
			System.out.println("<html><body><p>No file uploaded</p></body></html>");
		}
		try{
		File currentFile1 = new File(temp_parent_zip); File currentFile2 = new File(temp_child_zip);
		 File currentFile3 = new File(temp_Individual_file); File currentFile4 = new File(temp_grand_child_zip);
		  File currentFile5= new File(temp_folder_c_drive);
			
		 FileUtils.cleanDirectory( currentFile1);  FileUtils.cleanDirectory(currentFile2);
		 FileUtils.cleanDirectory( currentFile3);  FileUtils.cleanDirectory(currentFile4);FileUtils.cleanDirectory(currentFile5);}catch(Exception e){System.out.println("Exception e  :"+ e);}
		finally{
			 if(statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			 if(connection != null)
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

	public String extractParentZip(ServletFileUpload upload, HttpServletRequest request, HttpServletResponse response,
			File zipFile) {
		String status = "Success";
		
		try {
			List fileItems = upload.parseRequest(request);
			System.out.println("   fileItems  ::  " + fileItems);
			Iterator i = fileItems.iterator();
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {
					String zipFileFileName = fi.getName();
					zipFile = new File(temp_parent_zip + zipFileFileName);
					fi.write(zipFile);
					System.out.println("Uploaded Filename:  " + temp_parent_zip + zipFileFileName + "<br>");
					extractChildZip(upload,request, response, zipFile);
				}
			}
			return status;

		} catch (Exception ex) {
			System.out.println(ex);
			status = "error";
			return status;
		}
	}
	
	public Map<String,String> readTextFileLineByLine(File file) {
		Map<String,String> data= new HashMap<String, String>();
		String name="";
		String date="";
		String time="";
		double open =0;
		double high =0;
		double low =0;
		double close =0;
		double volume =0;
        boolean insertData=true;
		
		try{
			BufferedReader br = new BufferedReader(new FileReader( file));
			String sCurrentLine="";

		     String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
	          fileNameWithOutExt= fileNameWithOutExt.replace("-", "_");
	          fileNameWithOutExt= fileNameWithOutExt.replace("&", "_");
	          String fileNameWithOut_F1=fileNameWithOutExt.replace("_F1", "");
	          
	        System.out.println(" fileNameWithOutExt  :"+fileNameWithOutExt+"  file "  +file.length());
	    /*    createInsertdata.insertAllStockNames(fileNameWithOut_F1,JDBC_DRIVER,DB_URL,USER,PASS,connection,statement);
	        createInsertdata.createTableForStockNames(JDBC_DRIVER,DB_URL,USER,PASS,connection,statement);
			HashMap<Integer,String> allStockNames= createInsertdata.getAllStockNames(JDBC_DRIVER,DB_URL,USER,PASS,connection,statement);
			allStockNames.forEach((stockId,stockName)->{
			     createInsertdata.createTableForIndividualStock(fileNameWithOut_F1,JDBC_DRIVER,DB_URL,USER,PASS,connection,statement);
			});*/
	        while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(" nn " + sCurrentLine+" file name "+file.getName());
				String[] stockdetailsArray = sCurrentLine.split(",");
				int i=0;
				insertData=true;
				 for (String stockdetails: stockdetailsArray)
			        {
			          if(i==0){
			        	//stock Name
			         	  name=stockdetails.replace("-", "_");
			        	  name=name.replace("&", "_");
			        	  data.put("Name",name );
			          }else if(i==1){
			        	//stock date   
			      		 date=stockdetails;
			        	  data.put("Date",stockdetails );
			          }else if(i==2){
			        	//stock time  
			        	  time=stockdetails;
			        	  System.out.println(" insertData time :"+time+"  name "+name);
			        	  if(time.equalsIgnoreCase("09:08"))
			        	  {
			        		  System.out.println("if  insertData time :"+time+"  name "+name);
			        		  insertData=false;
			        	  }
			        	  data.put("Time",stockdetails );  
			          }else if(i==3){
			        	//stock open 
			        	  open=Double.parseDouble(stockdetails);
			        	  data.put("Open",stockdetails );
			          }else if(i==4){
			        	 //high 
			        	  high=Double.parseDouble(stockdetails);
			        	  data.put("High",stockdetails );
			          }else if(i==5){
			        	  //low
			        	  low=Double.parseDouble(stockdetails);
			        	  data.put("Low",stockdetails );
			          }else if(i==6){
			        	  //close
			        	  close=Double.parseDouble(stockdetails);
			        	  data.put("Close",stockdetails );
			          }else if(i==7){
			        	  //volume
			        	  volume=Double.parseDouble(stockdetails);
			        	  data.put("Volume",stockdetails );
			          }
			         
			          i++;
			        }
				
				 if(insertData){
					
		        	 // System.out.println(" insertData  :"+insertData+ name+" date :"+date+"  time : "+time+"   open : "+Double.valueOf(open));
		             createInsertdata0015.insertStockData(fileNameWithOut_F1,name,date,time,Double.valueOf(open),Double.valueOf(high),Double.valueOf(low),
		            		 Double.valueOf(close),Double.valueOf(volume),JDBC_DRIVER,DB_URL,USER,PASS,connection,statement);
					
					 }
			}
		return	data;
		}catch(Exception e){
			System.out.println(" Exception e :"+e);
		}
		return data;
	}
	
	public String extractChildZip(ServletFileUpload upload,HttpServletRequest request, HttpServletResponse response, File zipFile) {
		String status = "Success";
		FileInputStream fis = null;
		ZipInputStream zipIs = null;
		ZipEntry zEntry = null;
		//System.out.println("   inside extractChildZip "+zipFile.getAbsolutePath() );
		try {
			fis = new FileInputStream(zipFile);
			zipIs = new ZipInputStream(new BufferedInputStream(fis));
			while ((zEntry = zipIs.getNextEntry()) != null) {
				try {
					byte[] tmp = new byte[4 * 1024];
					FileOutputStream fos = null;
					String opFilePath = "";
					if (zEntry.isDirectory()) {
						new File(temp_child_zip + zEntry.getName()).mkdirs();
						//System.out.println("if zEntry.isDirectory() " + zEntry.isDirectory());
					} else {
						//System.out.println("else zEntry.isDirectory() " + zEntry.isDirectory()+"zEntry.getName(); "+zEntry.getName());
						
						if(zEntry.getName().contains("zip")){
							//System.out.println("contentEquals zip"+temp_child_zip  + zEntry.getName());
							opFilePath = temp_child_zip + zEntry.getName();
							
							fos = new FileOutputStream(opFilePath);
							int size = 0;
							
							while ((size = zipIs.read(tmp)) != -1) {
								fos.write(tmp, 0, size);
							}
							zipFile = new File(temp_child_zip + zEntry.getName());
							unZipIt(zipFile,temp_Individual_file);
							//SextractChildZip( upload,request, response, zipFile);
						}
					}
					
				
					fos.flush();
					fos.close();
				} catch (Exception ex) {

					System.out.println(ex);
				}
			}
			zipIs.close();
			return status;
		} catch (FileNotFoundException e) {
			System.out.println(e);
			status = "error";
			return status;
		} catch (IOException e) {
			System.out.println(e);
			status = "error";
			return status;
		}

	}

}
