package application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

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

	/**
	 * downloads from the external database the hosts updated after 
	 * the lastUpdate unix time parameter.
	 * @param lastUpdate unix seconds
	 * @return null if the connection returns a http code different than 200,
	 * or a list of hosts (could be empty) from the Vihoma central database 
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
						, Integer.parseInt(inputLine.split(";")[1])
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
	 * @return null if the connection returns a http code different than 200,
	 * or a list of hosts (could be empty) got from the alternative hosts source 
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
							, 3
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
	

	/**
	 * uploads a domain name to the Vihoma central database
	 * @param domain 
	 * @return true if the domain was successfully uploaded, false otherwise
	 * @throws IOException if the connection to the central database fails
	 */
	public static boolean uploadHostToWeb(String domain) throws IOException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(Settings.get("urlApiHosts"));

		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("domain", domain));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
		    try (InputStream instream = entity.getContent()) {
		       if(response.toString().indexOf("200 OK")==-1)
		        return false;
		    }
		}
		return true;
    	
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
