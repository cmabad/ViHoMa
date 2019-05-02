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
	    
	    if (sb.toString().indexOf("0x4") == -1)
	    	return true;
	    
	    return false;
	}
	
	public static boolean toggleWindowsDNSClient() {
		try {
			boolean wasActivated = isDNSClientStartActivated();
			if (!wasActivated) 
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /t REG_DWORD /v Start /d 2 /f");
			 else 
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /t REG_DWORD /v Start /d 4 /f");
			return wasActivated ^ isDNSClientStartActivated();
		} catch (IOException e) {
			//Validate the case the file can't be accessed (not enough permissions)
			Logger.err(e.getMessage());
			return false;
		} 
	}
}
