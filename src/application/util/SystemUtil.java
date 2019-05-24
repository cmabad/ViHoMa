package application.util;

import java.io.File;
import java.io.IOException;

import application.util.properties.Settings;

public class SystemUtil {

	/**
	 * gets the absolute path to the system hosts file depending on the operating
	 * system where the program is being run.
	 * @return %ROOT%\System32\drivers\etc\hosts on Windows os, /etc/hosts on 
	 * linux-based oses
	 */
	public static String getHostsPath() {
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
	
	/**
	 * gets the absolute path to the folder where all local files (configuration,
	 * logs, database) of Vihoma are stored.
	 * @return %AppData%\Vihoma\ on Windows os, ~.local/vihoma/ on linux-based oses
	 */
	public static String getVihomaFolderPath() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0)
			return System.getProperty("user.home") + Settings.get("VihomaPathWindows");
		else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
			return Settings.get("VihomaPathLinux");
		else if (os.indexOf("mac") >= 0)
            return Settings.get("VihomaPathMac");
        else if (os.indexOf("sunos") >= 0)
            return Settings.get("VihomaPathSolaris");

		throw new IllegalStateException("Cannot set os");
	}	
	
	/**
	 * tries to write a temporal file in the system's hosts folder.
	 * @return true if the action is completed, false otherwise.
	 */
	public static boolean isAdmin() {
		try {
			File f = new File(getHostsPath()+".test");
			f.createNewFile();
			f.delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
