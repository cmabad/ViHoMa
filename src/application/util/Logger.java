package application.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Logger {

	public static void log(String message) {
		if (null == message || "".equals(message)) {
			return;
		}
		
		String path = SystemUtil.getVihomaFolderPath() + "vihoma.log";
		BufferedWriter writer;
		message = new Timestamp(System.currentTimeMillis()) + "\t" + message + "\r\n";
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(message);
			writer.close();	
		} catch (IOException e) {
			System.out.println("Logger cannot access Vihoma folder");
		}
	}
	
	public static void err(String message) {
		if (null == message || "".equals(message)) {
			return;
		}
		
		String path = SystemUtil.getVihomaFolderPath() + "vihoma.err";
		BufferedWriter writer;
		message = new Timestamp(System.currentTimeMillis()) + "\t" + message + "\r\n";
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(message);
			writer.close();	
		} catch (IOException e) {
			System.out.println("Logger cannot access Vihoma folder");
		}
	}
}
