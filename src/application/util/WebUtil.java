package application.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

//import javax.net.ssl.HttpsURLConnection;

import application.model.Host;
import application.util.properties.Settings;

public class WebUtil {

	/**
	 * reads a standard hosts file and adds its domains to the database.
	 * The file should have 0.0.0.0 as the blocked address.
	 * @param source the HTTP URL from where the host file is downloaded. If null
	 * or blank (""), it takes the default value, sbc.io/hosts/hosts
	 * @return a list of Hosts. If the connection fails, null.
	 */
	public static List<Host> getHostsFromWebSource(String source) {
		URL url;
		try {
			url = new URL(source);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    	con.setRequestProperty("Accept-Encoding", "gzip");
	    	con.setRequestMethod("GET");
	    	con.setConnectTimeout(4000);
	    	
	    	List<Host> hosts = new ArrayList<Host>();
	    	
	    	if (200 == con.getResponseCode()) {
	    		BufferedReader in;
	    		if ("gzip".equals(con.getContentEncoding()))
	    			in = new BufferedReader(new InputStreamReader(
	    					new GZIPInputStream(con.getInputStream())));
	    		else
	    			in = new BufferedReader(
						  new InputStreamReader(con.getInputStream()));
				String inputLine;
				String domain = "";
				while (null != (inputLine = in.readLine())) {					
					if (inputLine.trim().startsWith("0.0.0.0")) {
						domain = inputLine.trim().split("0.0.0.0 ")[1];
						if (!" ".equals(domain) && !"".equals(domain))
							hosts.add(new Host((String)domain.split("#")[0].trim()
								, 0
								, Host.STATUS_ACTIVE
								,""
								, 0));
					}
				}
				in.close();
				con.disconnect();
				return hosts;
	    	} else {
	    		throw new IOException();
	    	}
		} catch (IOException e) {
			Logger.err(Settings.get("webSourceConnectionError") + "(" + source + ")");
			return null;
		}
    	
	}
	
	/**
	 * validates an address with the IPv4 and IPv6 specification formats.
	 * @param address the address to be validated
	 * @return true if the address follows at least one of the specifications, 
	 * false otherwise.
	 */
	public static boolean checkIpValidity(String address) {
		List<String> regex = new ArrayList<String>();
//		IPV4_REGEX
		regex.add("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
//		IPV6_HEX4DECCOMPRESSED_REGEX
		regex.add("\\\\A(25[0-5]|2[0-4]\\\\d|[0-1]?\\\\d?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|[0-1]?\\\\d?\\\\d)){3}\\\\z");
//		IPV6_6HEX4DEC_REGEX
		regex.add("\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
//		IPV6_HEXCOMPRESSED_REGEX
		regex.add("\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z");
//		IPV6_REGEX
		regex.add("\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z");
		
		for (String reg : regex)
			if (Pattern.matches(reg, address)) {
				return true;
			}
		return false;
	}

	/**
	 * creates a temporal file with the HTML user guide and opens the browser to
	 * show it to the user.
	 * @return true if the file can be opened in the browser, false otherwise
	 * (file not found, browser opening operation not supported..)
	 */
	public static boolean openHelp() {
		try {
			String base =//WebUtil.class.getProtectionDomain().getCodeSource()
					//.getLocation().getPath()
					SystemUtil.getHostsPath().split("hosts")[0] +".vihomahelp.tmp.html";
			File tempHelp = 
					new File(base);
			tempHelp.deleteOnExit();
			Files.copy(
					WebUtil.class.getResourceAsStream(
							Settings.get("helpPathLocationEN"))
					, tempHelp.toPath()
					, StandardCopyOption.REPLACE_EXISTING);
			
			Desktop.getDesktop().browse(tempHelp.toURI());
			return true;
		} catch (UnsupportedOperationException | IOException e) {
			Logger.err(e.getMessage());
			return false;
		}
	}
}
