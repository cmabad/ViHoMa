package application.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import application.model.CustomHost;
import application.model.Host;
import application.util.properties.Settings;

public class HostsFileManager {

	private static String path = "";
	
	public static void persistHostsFile(List<Host> blockedHostsList, String blockedAddress, List<CustomHost> customHostsList) throws IOException {
		if ("".equals(path))
			path = SystemUtil.getHostsPath();
		
		StringBuilder sb = new StringBuilder();
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			sb.append(Settings.get("hostsFileHeader"));
			
			sb.append("\r\n# CUSTOM hosts go here:\r\n\r\n");
			for (CustomHost chost : customHostsList) 
				sb.append(chost.getAddress())
					.append(" ").append(chost.getDomain())
					.append("\r\n");
			
			sb.append("\r\n\r\n# BLOCKED hosts start here:\r\n\r\n");
			for (Host host : blockedHostsList)
				sb.append(blockedAddress)
					.append(" ").append(host.getDomain())
					.append("\r\n");
			
			writer.write(sb.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			Logger.err("HOST FILE NOT FOUND: " + e.getMessage());
			path = SystemUtil.getVihomaFolderPath() + "hosts";
			persistHostsFile(blockedHostsList, blockedAddress, customHostsList);
		} catch (IOException e) {
			Logger.err(e.getMessage());
			throw e;
		}
		
	}
}
