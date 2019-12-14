package com.tkd.SqlCreation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.sun.istack.internal.logging.Logger;

@WebServlet("/Sql_Insert_Statement_Five_minute")
public class Sql_Insert_Statement_Five_Minute extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(Sql_Insert_Statement_Five_Minute.class);
	private static final Sql_Insert_Statement_Five_Minute main = new Sql_Insert_Statement_Five_Minute();

	private static final int BUFFER_SIZE = 4096;

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory (will be created if
	 * does not exists)
	 * 
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */

	  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		File srcDirectory = new File("D:\\Strategy testing environment\\TKD ZIP CSV and TXT\\TKD FIVE MIN DATA CSV semicolon\\");

		File destDirectory = new File("D:\\Strategy testing environment\\TKD Extracted Files\\TXT FIVE MINUTE\\");
		
		System.out.println("   Sql_Insert_Statement_Five_minute  ");
		
		main.getAllFilesWithCertainExtension(srcDirectory, destDirectory, "zip");

	}

	public void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	        }
	    }
	}
	
	private static void unzip(String zipFilePath, String destDir,String zipFileName) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                //System.out.println("Unzipping to     ::  "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                
                GenerateInsertStatement_Five_Minute.readFileToString(newFile, zipFileName,fileName);
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
        	System.out.println(" unzip   "+e.getMessage());
        }
        
    }
	
	public  void getAllFilesWithCertainExtension(File folder, File destDirectory ,String filterExt)
	 {
		
	  List<String> statements = new ArrayList<>();
	  
	  
	  MyExtFilter extFilter=new MyExtFilter(filterExt);
	  if(!folder.isDirectory())
	  {
	   System.out.println("Not a folder");
	  }
	  else
	  {
	   // list out all the file name and filter by the extension
	   String[] list = folder.list(extFilter);
	 
	   if (list.length == 0) {
	    System.out.println("no files end with : " + filterExt);
	    return;
	   }
	 
	   for (int i = 0; i < list.length; i++) {
	  //  System.out.println("folder "+folder+File.separator+list[i]);
	    String tempPath=destDirectory+File.separator + FilenameUtils.removeExtension(list[i]);
	    unzip(folder+File.separator+list[i] , tempPath,FilenameUtils.removeExtension(list[i]));
	    
	   }
	  }
	 }
	 
	 // inner class, generic extension filter
	  public class MyExtFilter implements FilenameFilter {
	 
	   private String ext;
	 
	   public MyExtFilter(String ext) {
	    this.ext = ext;
	   }
	 
	   public boolean accept(File dir, String name) {
	    return (name.endsWith(ext));
	   }
	  }
	  
	 
}
