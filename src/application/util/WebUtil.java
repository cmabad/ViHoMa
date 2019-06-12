package application.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


//import javax.net.ssl.HttpsURLConnection;

import application.model.Host;
import application.util.properties.Settings;

public class WebUtil {

	public static List<Host> getHostsFromWeb(long lastUpdate){
		List<Host> hosts = null;
		
		try {
			hosts = getHostsFromPrimarySource(lastUpdate);
		} catch (IOException e) {
			Logger.err(Settings.get("primarySourceConnectionError"));
			try {
				hosts = getHostsFromAlternativeSource();
			} catch (IOException e1) {
				Logger.err(Settings.get("alternativeSourceConnectionError"));
			}
		}
		
		return hosts;
	}
	
	/**
	 * downloads from the external database the hosts updated after 
	 * the lastUpdate unix time parameter.
	 * @param lastUpdate unix seconds
	 * @return null if the connection returns a http code different than 200,
	 * or a list of hosts (could be empty) from the Vihoma central database 
	 * @throws IOException if the http connection fails
	 */
	public static List<Host> getHostsFromPrimarySource(long lastUpdate) throws IOException {
		URL url = new URL(Settings.get("urlGetWithTime")+lastUpdate);
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
			
			while ((inputLine = in.readLine()) != null) {
				hosts.add(new Host((String)inputLine.split(";")[0]
						, Integer.parseInt(inputLine.split(";")[1])
						, Integer.parseInt(inputLine.split(";")[2])
						, (String)inputLine.split(";")[3]
						, Long.parseLong(inputLine.split(";")[4])));
			}
			in.close();
			con.disconnect();
			return hosts;
    	} else if (204 == con.getResponseCode()) {
    		return hosts;
    	} else {
    		Logger.err(Settings.get("webSourceConnectionError"));
    		return null;
    	}
	}

	/**
	 * reads a standard hosts file and adds its domains to the database.
	 * The file should have 0.0.0.0 as the blocked address.
	 * @return null if the connection returns a http code different than 200,
	 * or a list of hosts (could be empty) got from the alternative hosts source 
	 * @throws IOException
	 */
	public static List<Host> getHostsFromAlternativeSource() throws IOException{
		URL url = new URL(Settings.get("urlAlternativeHosts"));
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
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.startsWith("0.0.0.0")) {
					domain = inputLine.split("0.0.0.0 ")[1];
					if (!" ".equals(domain) && !"".equals(domain))
						hosts.add(new Host((String)domain.split("#")[0].trim()
							, 3
							, 3
							,"StevenBlack"
							, 0));
				}
			}
			in.close();
			con.disconnect();
			return hosts;
    	} else {
    		Logger.err(Settings.get("webSourceConnectionError"));
    		return null;
    	}
	}
	
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
	 * uploads a domain name to the Vihoma central database
	 * @param domain 
	 * @return true if the domain was successfully uploaded, false otherwise
	 */
	public static boolean uploadHostToWeb(String domain) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(Settings.get("urlApiHosts"));

		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("domain", domain));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
			    try (InputStream instream = entity.getContent()) {
			       if(response.toString().indexOf("200 OK")==-1)
			    	   throw new IOException();
			    }
			}
			Logger.log(domain + " " + Settings.get("blockedHostUploadSuccess"));
			return true;
	 
		} catch (IOException e) {
			Logger.err(domain + " " + Settings.get("blockedHostUploadError"));
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

	public static boolean openHelp() {
		try {
			String base =//WebUtil.class.getProtectionDomain().getCodeSource()
					//.getLocation().getPath()
					SystemUtil.getHostsPath().split("hosts")[0] +".vihomahelp.html";
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
		} catch (IOException e) {
			Logger.err(e.getMessage());
			return false;
		}
	}
}
