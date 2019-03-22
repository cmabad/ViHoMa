package application.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import application.conf.Factory;
import application.model.CustomHost;
import application.model.Host;
import application.util.properties.Settings;

public class HostsFileManager {

	private static HostsFileManager instance = null;
	
	public static void editHostsFile() {
		String path = "./hosts";// = getInstance().getPath();
		String blockedAddress = Factory.service.forConfiguration().getBlockedAddress();			
		StringBuilder sb = new StringBuilder();
		//RandomAccessFile writer;
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(path));//new RandomAccessFile(path, "rw");
			sb.append(Settings.get("hostsFileHeader"));
			
			sb.append("\r\n# CUSTOM hosts go here:\r\n\r\n");
			for (CustomHost chost : Factory.service.forCustomHost().findAll()) 
				sb.append(chost.getAddress())
					.append(" ").append(chost.getDomain())
					.append("\r\n");
			
			sb.append("\r\n\r\n# BLOCKED hosts start here:\r\n\r\n");
			for (Host host : Factory.service.forHost().findAll())
				sb.append(blockedAddress)
					.append(" ").append(host.getDomain())
					.append("\r\n");
			
			writer.write(sb.toString());
			writer.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		System.out.println(os);
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
