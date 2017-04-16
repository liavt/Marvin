package com.liav.bot.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Configuration {
	public static Map<String,String> properties = new HashMap<>();
	
	@SuppressWarnings("resource")
	public static void init() throws FileNotFoundException{
		Scanner s = null;
		try{
			s = new Scanner(new File("config.txt")).useDelimiter("\\Z");
			String[] content = s.next().split(System.lineSeparator());
			for(String line : content){
				String[] values = line.split("=");
				if(values.length != 2){
					throw new IllegalArgumentException("Invalid config.txt");
				}
				properties.put(values[0], values[1]);
			}
		}finally{
			if(s != null){
				s.close();
			}
		}
		
	}
}
