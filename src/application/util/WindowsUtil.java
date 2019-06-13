package application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class provides some Windows Operating System auxiliary methods.
 *
 */
public class WindowsUtil {

	/**
	 * Queries the value of the DNScache start Windows registry.
	 * @return true if the query does not return '0x4', false otherwise
	 * @throws IOException if the query cannot be done
	 */
	public static boolean isDNSClientActivated() throws IOException {
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

	/**
	 * updates the value of the DNScache start Windows registry. If the start
	 * is disabled (0x4), it is changed to automatic (0x2), and otherwise.
	 * @return true if the DNScache start Windows registry has changed
	 */
	public static boolean toggleWindowsDNSClient() {
		try {
			boolean wasActivated = isDNSClientActivated();
			if (!wasActivated) 
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /t REG_DWORD /v Start /d 2 /f");
			 else 
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /t REG_DWORD /v Start /d 4 /f");
			return wasActivated ^ isDNSClientActivated();
		} catch (IOException e) {
			//The file can't be accessed (not enough permissions)
			Logger.err(e.getMessage());
			return false;
		} 
	}
	
	/**
	 * checks if the Windows registry has an entry to run the program at the 
	 * operating system startup
	 * @return true if the entry is found, false otherwise
	 * @throws IOException
	 */
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
	
	/**
	 * toggles the start of Vihoma at the windows start. It adds or removes an 
	 * entry to the Windows registry.
	 * @return true if the Windows registry changed
	 */
	public static boolean toggleWindowsStartup() {
		try {
			boolean wasSetUp = isRunAtStartup();
			if (!wasSetUp) {
				String path = (SystemUtil.getVihomaJarPath().split("vihoma.jar")[0]).replace('/', '\\')
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
