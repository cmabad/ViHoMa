package application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WindowsUtil {

	public static boolean isDNSClientStartActivated() throws IOException {
		Process process = Runtime.
				   getRuntime().
				   exec("reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /v Start");
		
	    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));  
	    String line;
	    StringBuilder sb = new StringBuilder();
	    while ((line = br.readLine()) != null) {  
	      sb.append(line);  
	    } 
	    br.close();
	    
	    if ("0x4".indexOf(sb.toString()) == -1)
	    	return true;
	    
	    return false;
	}
}
