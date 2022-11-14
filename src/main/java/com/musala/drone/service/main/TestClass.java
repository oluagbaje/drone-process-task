package com.musala.drone.service.main;

import static com.musala.drone.service.utils.LogHandler.logError;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.musala.drone.service.dal.RelationalDataSource;

public class TestClass {

	
	public static void main(String[] args) {
		ReadFilesList("");
    }
	
	   private static String ReadFilesList(String dir) {
	        String jsonlists = "";
	        List<String> listfiles = new ArrayList<>();
	        try {

	            String filePathStore  = Paths.get("testingfolder").toString();
	            File file = new File(filePathStore);
	            File[] files = file.listFiles();
	            for(File f: files){

	                listfiles.add(f.getName());
	                System.out.println(f.getName());
	            }

	        } catch ( JSONException e) {
	            //LogError("Error occured"+"ReadFilesList ", e);
	        }
	        jsonlists = listfiles.toString();
	        return jsonlists;
	    }
}
