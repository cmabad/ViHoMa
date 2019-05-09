package application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class WindowsUtil {

	public static boolean isDNSClientStartActivated() throws IOException {
		Process process = Runtime.
				   getRuntime().
				   exec("reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /v Start");
		
	    BufferedReader br = new BufferedReader(
	    		new InputStreamReader(process.getInputStream()));  
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
	
	public static boolean isRunAtStartup() throws IOException {
		Process process = Runtime.
				   getRuntime().
				   exec("reg query HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run");
		
	    BufferedReader br = new BufferedReader(
	    		new InputStreamReader(process.getInputStream()));  
	    String line;
	    StringBuilder sb = new StringBuilder();
	    while ((line = br.readLine()) != null) {  
	      sb.append(line);  
	    } 
	    br.close();
	    
	    if (sb.toString().indexOf("Vihoma") == -1)
	    	return false;
	    
	    return true;
	}
	
	public static String getPath() {
		try {
			return URLDecoder.decode(WindowsUtil.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath(), "UTF-8")
					.substring(1);
		} catch (URISyntaxException | UnsupportedEncodingException e) {
//			e.printStackTrace();
			Logger.err(e.getMessage());
			System.exit(1);
			return "";
		}
	}
	
	public static boolean toggleWindowsStartup() {
		try {
			boolean wasSetUp = isRunAtStartup();
			if (!wasSetUp) {
				String path = getPath().split("vihoma.jar")[0]
						+"vihomaAdmin.bat quiet";
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /t REG_SZ /v Vihoma /d \"" + path + "\" /f");
			}
			 else 
				Runtime.
				   getRuntime().
				   exec("reg delete HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v Vihoma /f");
			return wasSetUp ^ isRunAtStartup();
		} catch (IOException e) {
			Logger.err(e.getMessage());
			return false;
		} 
	}
}
