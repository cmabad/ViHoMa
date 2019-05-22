package application.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Logger {

	public static void log(String message) {
		if (null == message) {
			throw new IllegalArgumentException();
		}
		
		String path = SystemUtil.getVihomaFolderPath() + "vihoma.log";
		BufferedWriter writer;
		message = new Timestamp(System.currentTimeMillis()) + "\t" + message + "\r\n";
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(message);
			writer.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void err(String message) {
		if (null == message) {
			throw new IllegalArgumentException();
		}
		
		String path = SystemUtil.getVihomaFolderPath() + "vihoma.err";
		BufferedWriter writer;
		message = new Timestamp(System.currentTimeMillis()) + "\t" + message + "\r\n";
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(message);
			writer.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
