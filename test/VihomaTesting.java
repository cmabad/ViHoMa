
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import application.Main;
import application.conf.Factory;
import application.model.CustomHost;
import application.model.Host;
import application.util.HostsFileManager;
import application.util.SystemUtil;
import application.util.WindowsUtil;

/**
 * This test cases will check the functionalities of the program.
 * It is required to run them with administrator privileges.
 * THESE TESTS OVERRIDE YOUR VIHOMA DATABASE AND HOSTS FILE!
 * @author chris
 *
 */
public class VihomaTesting {

	private Host testBlockedHost;
//	private CustomHost testCustomHost;
	
	@BeforeClass
	public static void initialSetup() {
		Main.configure();
	}
	@Before
	public void dbSetup() {
		Factory.service.forHost().deleteAll();
		Factory.service.forCustomHost().deleteAll();
		Factory.service.forHost().addHost("VihomaBlockedHost");
		testBlockedHost = Factory.service.forHost().findByDomain("VihomaBlockedHost").get(0);
		Factory.service.forCustomHost().add("VihomaCustomHost", "1.2.3.4");
//		testCustomHost = Factory.service.forCustomHost().findByDomainOrIp("VihomaCustomHost").get(0);
		Factory.service.forConfiguration().setBlockedAddress("");
		persistHostsFile();
	}
	
	/**
	 * looks for a term or expression in the local hosts file
	 * @param text the string to search
	 */
	private boolean existsInHostsFile(String text) {
		File hosts = new File(SystemUtil.getHostsPath());
		try {
			Scanner scanner = new Scanner(hosts);
			while (scanner.hasNextLine()) {
				scanner.nextLine();
				if (scanner.findInLine(text) != null) {
					scanner.close();
					return true;
				}
			}
			scanner.close();
			return false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
	}
	
	private void persistHostsFile() {
		try {
			HostsFileManager.persistHostsFile(
					Factory.service.forHost().findAllActive()
					, Factory.service.forConfiguration().getBlockedAddress()
					, Factory.service.forCustomHost().findAllActive());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Use case 1. An internet connection is required.
	 */
	public void updateDatabaseFromWeb() {
		int totalBlocked = (Factory.service.forHost().findAll()).size();
		// download
		Factory.service.forHost().updateDatabaseFromWeb();
		assert(totalBlocked < (Factory.service.forHost().findAll()).size());
		persistHostsFile();
		assert(existsInHostsFile(testBlockedHost.getDomain()));
		
		// unblock a host
		Factory.service.forHost().toggleStatus(testBlockedHost.getDomain());
		persistHostsFile();
		assert(!existsInHostsFile(testBlockedHost.getDomain()));
		
		// download again
		Factory.service.forHost().updateDatabaseFromWeb();
		persistHostsFile();
		assert(!existsInHostsFile(testBlockedHost.getDomain()));
		assert(!
				((Factory.service.forHost().findByDomain(testBlockedHost.getDomain()))
						.get(0)).isActive().booleanValue());
	}
	
	@Test
	/**
	 * Use case 2
	 */
	public void blockHost() {
		Host newHost = new Host("newBlockedHost");
		
		// block correctly
		assert(1==
			Factory.service.forHost().addHost(newHost.getDomain(),Host.CATEGORY_VIHOMA));
		
		// block an existing blocked host
		assert(0==
			Factory.service.forHost().addHost(newHost.getDomain(),Host.CATEGORY_VIHOMA));
	}
	
	@Test
	/**
	 * Use case 3
	 */
	public void addCustomHost() {
		CustomHost cHost = new CustomHost("newCustomHost", "4.3.2.1");
		
		// add correctly
		assert(1==
			Factory.service.forCustomHost().add(cHost.getDomain(),cHost.getAddress()));
		
		// add existing custom host
		assert(0==
			Factory.service.forCustomHost().add(cHost.getDomain(),cHost.getAddress()));
		
		// add with wrong ip
		assert(0 > Factory.service.forCustomHost().add("wrong", "wrongAddress"));
	}
	
	@Test
	/**
	 * Use cases 4, 8, 10
	 */
	public void configurationManagement() {
		// change blocking address
		String newBlockingAddress = "111.111.111.112";
		assert(!existsInHostsFile(newBlockingAddress));
		Factory.service.forConfiguration().setBlockedAddress(newBlockingAddress);
		persistHostsFile();
		assert(existsInHostsFile(newBlockingAddress));

		// change sharing
		boolean sharingOptionValue = 
				Factory.service.forConfiguration().isSharingAllowed();
		Factory.service.forConfiguration().toggleSharing();
		assert(sharingOptionValue
				^Factory.service.forConfiguration().isSharingAllowed());
		Factory.service.forConfiguration().toggleSharing();
		
		// update at start
		boolean updateAtStart = 
				Factory.service.forConfiguration().isUpdateAtVihomaStartupEnabled();
		Factory.service.forConfiguration().toggleUpdateAtVihomaStart();
		assert(updateAtStart
				^Factory.service.forConfiguration().isUpdateAtVihomaStartupEnabled());
		Factory.service.forConfiguration().toggleUpdateAtVihomaStart();
	}
	
	@Test
	/**
	 * Use case 7
	 */
	public void changeHostActivation() {
		Host newHost = new Host("newBlockedHost");
		CustomHost cHost = new CustomHost("newCustomHost", "4.3.2.1");
		persistHostsFile();
		assert(!existsInHostsFile(newHost.getDomain()));
		assert(!existsInHostsFile(cHost.getDomain()));
		
		// add hosts
		Factory.service.forHost().addHost(newHost.getDomain(), Host.CATEGORY_VIHOMA);
		Factory.service.forHost().addHost("activatedDomain");
		Factory.service.forCustomHost().add(cHost.getDomain(), cHost.getAddress());
		persistHostsFile();
		assert(existsInHostsFile(newHost.getDomain()));
		assert(existsInHostsFile(cHost.getDomain()));
		assert(existsInHostsFile("activatedDomain"));
		
		// deactivate
		Factory.service.forHost().toggleStatus(newHost.getDomain());
		Factory.service.forCustomHost().toggleStatus(cHost.getDomain(),cHost.getAddress());
		persistHostsFile();
		assert(!existsInHostsFile(newHost.getDomain()));
		assert(!existsInHostsFile(cHost.getDomain()));
		assert(existsInHostsFile("activatedDomain"));
		
		// reactivate
		Factory.service.forHost().toggleStatus(newHost.getDomain());
		Factory.service.forCustomHost().toggleStatus(cHost.getDomain(),cHost.getAddress());
		persistHostsFile();
		assert(existsInHostsFile(newHost.getDomain()));
		assert(existsInHostsFile(cHost.getDomain()));
		assert(existsInHostsFile("activatedDomain"));
	}
	
	@Test
	/**
	 * Use case 9.
	 * ONLY WINDOWS OPERATING SYSTEMS
	 */
	public void WindowsDNSClient() {
		try {
			boolean isActivated = WindowsUtil.isDNSClientActivated();
			WindowsUtil.toggleWindowsDNSClient();
			assert(isActivated ^ WindowsUtil.isDNSClientActivated());
			WindowsUtil.toggleWindowsDNSClient();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
