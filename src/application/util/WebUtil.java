package application.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

//import javax.net.ssl.HttpsURLConnection;

import application.model.Host;
import application.util.properties.Settings;

public class WebUtil {

	/**
	 * downloads from the external database the hosts updated after 
	 * the lastUpdate unix time parameter.
	 * @param lastUpdate
	 * @return
	 * @throws IOException if the http connection fails
	 */
	public static List<Host> getHostsFromWeb(long lastUpdate) throws IOException {
		URL url = new URL(Settings.get("urlGetWithTime")+lastUpdate);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	con.setConnectTimeout(4000);
    	
    	List<Host> hosts = new ArrayList<Host>();
    	
    	if (200 == con.getResponseCode()) {
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			
			while ((inputLine = in.readLine()) != null) {
				hosts.add(new Host((String)inputLine.split(";")[0]
						, (String)inputLine.split(";")[1]
						, Integer.parseInt(inputLine.split(";")[2])
						, (String)inputLine.split(";")[3]
						, Long.parseLong(inputLine.split(";")[4])));
			    //content.append(inputLine);
			}
			in.close();
			con.disconnect();
			return hosts;
    	} else if (204 == con.getResponseCode())
    		return hosts;
    	
    	return null;
	}

	/**
	 * reads a standard hosts file and adds its domains to the database.
	 * The file should have 0.0.0.0 as the blocked address.
	 * @return
	 * @throws IOException
	 */
	public static List<Host> getHostsFromAlternativeWeb() throws IOException{
		URL url = new URL(Settings.get("urlAlternativeHosts"));
    	HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	con.setConnectTimeout(5000);
    	
    	List<Host> hosts = new ArrayList<Host>();
    	
    	if (200 == con.getResponseCode()) {
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
			String inputLine;
			String domain = "";
//			String[] pre;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.startsWith("0.0.0.0")) {
					domain = inputLine.split("0.0.0.0 ")[1];
//					if (pre.length==0)
//						break;
//						domain = pre[1];
					if (!" ".equals(domain) && !"".equals(domain))
						hosts.add(new Host((String)domain.split("#")[0].trim()
							, "3"
							, 3
							,"StevenBlack"
							, 0));
				}
			    //content.append(inputLine);
			}
			in.close();
			con.disconnect();
			return hosts;
    	}
    	
    	return null;
	}
	

	public static boolean uploadHostToWeb(String domain) throws IOException {
		URL url = new URL(Settings.get("urlApiHosts"));
		String postData = URLEncoder.encode("domain=" + domain + "&category=1", "UTF-8");
		byte[] postDataBytes = postData.getBytes("UTF-8");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("POST");
    	con.addRequestProperty("Content-Type" , "application/x-www-form-urlencoded");
    	con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
    	//con.setConnectTimeout(5000);
    	con.setDoOutput(true);
    	//con.getOutputStream().write(postDataBytes);
    	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    	wr.writeBytes(postData);
    	wr.flush();
    	wr.close();
    	//con.getInputStream();
    	
    	if (200 == con.getResponseCode()) {
    		con.disconnect();
    		return true;    		
    	} else {
    		con.disconnect();
    		return false;
    	}
    	
	}

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
}
