package application.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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
		URL url = new URL(Settings.get("UrlGetWithTime")+lastUpdate);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setRequestMethod("GET");
    	con.setConnectTimeout(5000);

    	// 204 si está al día
    	if (200 == con.getResponseCode()) {
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
			String inputLine;
			List<Host> hosts = new ArrayList<Host>();
			
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
    	}
    	return null;
	}
	
	public static boolean uploadHostToWeb(String domain) throws IOException {
		URL url = new URL(Settings.get("urlApiHosts"));
		String postData = URLEncoder.encode("domain=" + domain, "UTF-8");
		byte[] postDataBytes = postData.getBytes("UTF-8");
		
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    	con.setRequestMethod("POST");
    	con.addRequestProperty("Content-Type" , "application/x-www-form-urlencoded");
    	con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
    	con.setConnectTimeout(5000);
    	con.setDoOutput(true);
    	//con.getOutputStream().write(postDataBytes);
    	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    	wr.writeBytes(postData);
    	wr.flush();
    	wr.close();
    	//con.getInputStream();
    	
    	if (200 == con.getResponseCode()) {
    		System.out.println(con.getResponseCode());
    		System.out.println("WebUtil.uploadHostToWeb(): ");
    		BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
    		return true;    		
    	} else {
    		return false;
    	}
    	
	}

}
