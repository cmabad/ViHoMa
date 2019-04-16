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

	private static HostsFileManager instance = null;
	private static String path = "";
	
	public static void editHostsFile(List<Host> blockedHostsList, String blockedAddress, List<CustomHost> customHostsList) {
		if ("".equals(path))
			path = getInstance().getPath();
		
		//String blockedAddress = Factory.service.forConfiguration().getBlockedAddress();			
		StringBuilder sb = new StringBuilder();
		//RandomAccessFile writer;
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(path));//new RandomAccessFile(path, "rw");
			sb.append(Settings.get("hostsFileHeader"));
			
			sb.append("\r\n# CUSTOM hosts go here:\r\n\r\n");
			for (CustomHost chost : customHostsList)//Factory.service.forCustomHost().findAllActive()) 
				sb.append(chost.getAddress())
					.append(" ").append(chost.getDomain())
					.append("\r\n");
			
			sb.append("\r\n\r\n# BLOCKED hosts start here:\r\n\r\n");
			for (Host host : blockedHostsList)//Factory.service.forHost().findAllActive())
				sb.append(blockedAddress)
					.append(" ").append(host.getDomain())
					.append("\r\n");
			
			writer.write(sb.toString());
			writer.close();			
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Logger.err("HOST FILE NOT FOUND: " + e.getMessage());
			path = System.getProperty("user.home") + "/hosts.ViHoMa";
			editHostsFile(blockedHostsList, blockedAddress, customHostsList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static HostsFileManager getInstance() {
		if (null == instance)
			instance = new HostsFileManager();
		
		return instance;
	}

	private String getPath() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0)
			return System.getenv("SystemRoot") + Settings.get("hostsFilePathWindows");
		else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
			return Settings.get("hostsFilePathLinux");
		else if (os.indexOf("mac") >= 0)
            return Settings.get("hostsFilePathMac");
        else if (os.indexOf("sunos") >= 0)
            return Settings.get("hostsFilePathSolaris");

		throw new IllegalStateException("Cannot set os");
	}
}
