package application.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * saves system messages into log files inside the Vihoma system folder.
 *
 */
public class Logger {

	/**
	 * saves the results of some of the processes of the program into the vihoma.log
	 * file
	 * @param message the message to save into the log file
	 */
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
	
	/**
	 * saves error messages into the vihoma.err log file
	 * @param message the error to save into the log file
	 */
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
