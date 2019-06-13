package application.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import application.model.CustomHost;
import application.model.Host;
import application.util.properties.Settings;

/**
 * This class manages the persistence to the hosts file. 
 *
 */
public class HostsFileManager {
	
	/**
	 * shortcut for persistHostsFile({system hosts file path}, blockedHostsList,
	 * blockedAddress, customHostsList).
	 * @param blockedHostsList
	 * @param blockedAddress 
	 * @param customHostsList
	 * @throws IOException
	 */
	public static void persistHostsFile(List<Host> blockedHostsList, String blockedAddress, List<CustomHost> customHostsList) throws IOException {
		persistHostsFile(SystemUtil.getHostsPath(),blockedHostsList, blockedAddress, customHostsList);
	}

	/**
	 * persist the provided hosts list into the system hosts file. CustomHosts are
	 * persisted before the blocked hosts. If the hosts file cannot be accessed,
	 * the hosts are persisted in the Vihoma folder.
	 * @param filePath
	 * @param blockedHostsList the list of blocks which will be prepended by the
	 * blockedAddress
	 * @param blockedAddress ip address used to block (redirect) the list of blocked
	 * hosts
	 * @param customHostsList the list of the custom host to be persisted
	 * @throws IOException if the filen cannot be found, or the user does not have
	 * enough permissions to access it.
	 */
	public static void persistHostsFile(String filePath, List<Host> blockedHostsList, String blockedAddress,
			List<CustomHost> customHostsList) throws IOException{
		
		StringBuilder sb = new StringBuilder();
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filePath));
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
			persistHostsFile(SystemUtil.getVihomaFolderPath() + "hosts"
					, blockedHostsList, blockedAddress, customHostsList);
		} catch (IOException e) {
			Logger.err(e.getMessage());
			throw e;
		}
		
	}
}
