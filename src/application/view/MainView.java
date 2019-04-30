package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import application.Main;
import application.conf.Factory;
import application.model.CustomHost;
import application.model.Host;
import application.util.HostsFileManager;
import application.util.Logger;
import application.util.WindowsUtil;
import application.util.properties.Messages;
import application.util.properties.Settings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;

public class MainView {

	@FXML
	private TableView<Host> blockedHostsTable;
	@FXML
	private TableColumn<Host, String> domainColumn;
	@FXML
	private TableColumn<Host, String> categoryColumn;
	@FXML
	private TableColumn<Host, Boolean> activeColumn;
	
	@FXML
	private TextField blockedHostsTableFilter;
	
	@FXML
	private TableView<CustomHost> customHostsTable;
	@FXML
	private TableColumn<CustomHost, String> customDomainColumn;
	@FXML
	private TableColumn<CustomHost, String> customIpColumn;
	@FXML
	private TableColumn<CustomHost, Boolean> customActiveColumn;
	
	@FXML
	private TextField customHostsTableFilter;
	
	@FXML
	private Button blockedHostsActivationButton; 
	@FXML
	private Button customHostsActivationButton;
	
	@FXML
	private TextField newBlockedHostDomain;

	@FXML
	private SplitMenuButton newBlockedHostCategory;

	@FXML
	private TextField newCustomDomainField;
	@FXML
	private TextField newCustomAddressField;

	@FXML
	private Label totalBlockedHostsCountLabel;

	@FXML
	private Label settingStartupLabel;
	@FXML
	private Label settingsHelpLabel;
	@FXML
	private CheckBox settingStartupCheckBox;
	@FXML
	private CheckBox settingShareBlockHostsCheckBox;
	@FXML
	private TextField settingTargetDomainField;
	@FXML
	private CheckBox settingTargetDomainCheckBox;
	@FXML
	private CheckBox settingDNSclientCheckBox;
	
	@FXML
	private AnchorPane statusBar;
	@FXML
	private Label statusBarLabel;

	private Main main;

	// private int status = 0;
	private final int STATUS_UPDATE = 1;
	private final int STATUS_OK = 0;
	private final int STATUS_ERROR = 2;

	/**
	 * The constructor. The constructor is called before the initialize() method.
	 */
	public MainView() {
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the person table with the two columns.
		domainColumn.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
		categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		// statusColumn.setCellValueFactory(cellData
		// -> cellData.getValue().statusProperty().asObject());
		activeColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        activeColumn.setCellFactory(param 
        		-> new CheckBoxTableCell<Host, Boolean>());
		
        customActiveColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        customActiveColumn.setCellFactory(param 
        		-> new CheckBoxTableCell<CustomHost, Boolean>());
        
		customDomainColumn.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
		customIpColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
		
		updateHostCountLabel();

		fillBlockedHostsTable(null);
		fillCustomHostsTable(null);
		
		if (System.getProperty("os.name").toLowerCase().indexOf("win") != 0) {
			settingDNSclientCheckBox.setDisable(true);
			return;
		} else {
			try {
				settingDNSclientCheckBox.setSelected(WindowsUtil.isDNSClientStartActivated());
			} catch (IOException e) {
//				e.printStackTrace();
				Logger.err(e.getMessage());
			}
		}
		blockedHostsActivationButton.setDisable(true);
		customHostsActivationButton.setDisable(true);
		// Listen for selection changes and show the person details when changed.
		// blockedHostsTable.getSelectionModel().selectedItemProperty().addListener(
		// (observable, oldValue, newValue) -> showPersonDetails(newValue));
	}

	/** Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainClass) {
		this.main = mainClass;

		// Add observable list data to the table
		fillBlockedHostsTable(main.getBlockedHostsData());
		fillCustomHostsTable(main.getCustomHostsData());
	}
	
	private void updateHostCountLabel() {
		totalBlockedHostsCountLabel.setText(getBlockedHostsCount() + " blocked hosts");
	}

	private int getBlockedHostsCount() {
		return Factory.service.forHost().getHostsCount();
	}

	private void fillBlockedHostsTable(ObservableList<Host> list) {
		blockedHostsTable.setItems(list);
	}

	private void fillCustomHostsTable(ObservableList<CustomHost> list) {
		customHostsTable.setItems(list);
	}

	@FXML
	private void getHostsFromWeb() {
		drawStatusBar(Messages.get("checkingNewBlockedHosts"), STATUS_UPDATE);

		List<Host> hosts = Factory.service.forHost().downloadNewBlockedHostsFromWeb();

		if (null == hosts) {
			drawStatusBar(Messages.get("webConnectionError"), STATUS_ERROR);
			return;
		}

		drawStatusBar(Messages.get("updatingBlockedHostsList"), STATUS_UPDATE);
		Factory.service.forHost().addHosts(hosts);
		main.fillBlockedHostObservableList();

		updateHostCountLabel();

//    	Factory.service.forHost().persistOnHostsFile();
		editHostsFile();
		drawStatusBar(Messages.get("upToDate"), STATUS_OK);
	}

	@FXML
	private void blockNewHost() {
		// TODO: if the user doesn't want to add to the database...
		String errorMessage = "";
		boolean valid = true;
		String domain = newBlockedHostDomain.getText();

		if (null == domain || 0 == domain.length()) {
			valid = false;
			errorMessage += "No valid hostname!\n";
		}

		if (valid) {
			drawStatusBar(Messages.get("blockNewHostStart") + " " + domain, STATUS_UPDATE);
			Factory.service.forHost().addHost(domain, "category");

			// TODO if the user wants to share the blocked domain
			uploadNewBlockedHost(domain);

			main.fillBlockedHostObservableList();
			updateHostCountLabel();
			drawStatusBar(domain + " " + Messages.get("blockNewHostSuccess"), STATUS_OK);
		} else {
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
		}
	}

	private void uploadNewBlockedHost(String domain) {
		Boolean uploadSuccess = Factory.service.forHost().updateHost(domain);
		if (null == uploadSuccess || !uploadSuccess.booleanValue())
			System.out.println("the host wasn't uploaded");
		else {
			System.out.println("the host was uploaded");
			drawStatusBar(Messages.get("blockNewHostUpload") + " " + domain, STATUS_UPDATE);
		}
	}

	@FXML
	private void addCustomHost() {
		String errorMessage = "";
		boolean valid = true;
		String domain = newCustomDomainField.getText();
		String address = newCustomAddressField.getText();

		if (null == domain || 0 == domain.length()) {
			valid = false;
			errorMessage += "No valid hostname!\n";
		}
		if (null == address || 0 == address.length()) {
			valid = false;
			errorMessage += "No valid IP!\n";
		}

		if (valid) {
			try {
				Factory.service.forCustomHost().add(domain, address);
			} catch (IllegalArgumentException e){
				drawStatusBar(e.getMessage(), STATUS_ERROR);
			}
			main.fillCustomHostObservableList();
		} else
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
	}

	@FXML
	private void editHostsFile() {
		// TODO Factory.service.forHosts().editHostsFile();
		HostsFileManager.editHostsFile(
				Factory.service.forHost().findAll()
				, Factory.service.forConfiguration().getBlockedAddress()
				, Factory.service.forCustomHost().findAll());
	}

	@FXML
	/**
	 * activates/deactivates the selected host in the blocked hosts table
	 */
	private void toggleBlockedHostStatus() {
		Host host = blockedHostsTable.getSelectionModel().getSelectedItem();
//		toggleHostActivation(true);

		if (null == host)
			drawStatusBar(Messages.get("noHostSelected"),STATUS_ERROR);
		else {
			drawStatusBar(host.isActive()? 
					Messages.get("deactivatingDomain")
					:Messages.get("activatingDomain"), STATUS_UPDATE);
			
			Factory.service.forHost().toggleStatus(host.getDomain());
			
			host.setActive(!host.isActive());
			
			//main.fillBlockedHostObservableList();
			filterBlockedHostsTable();
			updateHostCountLabel();
			
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
			blockedHostsActivationButton.setDisable(true);
		}
	}
	
	@FXML
	/**
	 * activates/deactivates the selected host at the custom hosts table
	 */
	private void toggleCustomHostStatus() {
		CustomHost host = customHostsTable.getSelectionModel().getSelectedItem();
		
		if (null == host)
			drawStatusBar(Messages.get("noHostSelected"),STATUS_ERROR);
		else {
			drawStatusBar(host.isActive()? 
					Messages.get("deactivatingDomain")
					:Messages.get("activatingDomain"), STATUS_UPDATE);
			
			Factory.service.forCustomHost().toggleStatus(host.getDomain());
			
			host.setActive(!host.isActive());
			
			//main.fillCustomHostObservableList();
			filterCustomHostsTable();
			
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
			customHostsActivationButton.setDisable(true);
		}
	}

	@FXML
	private void changeBlockedHostsActivationButton(){
		blockedHostsActivationButton.setDisable(false);
		Host host = blockedHostsTable.getSelectionModel().getSelectedItem();
		if (null == host)
			return;
		else {
			if (blockedHostsTable.getSelectionModel().getSelectedItem().isActive())
				blockedHostsActivationButton.setText(
						Messages.get("hostsActivationButtonDeactivate"));
			else
				blockedHostsActivationButton.setText(
						Messages.get("hostsActivationButtonActivate"));
		}
	}
	
	@FXML
	private void changeCustomHostsActivationButton(){
		customHostsActivationButton.setDisable(false);
		Host cHost = customHostsTable.getSelectionModel().getSelectedItem();
		if (null == cHost)
			return;
		else {
			if (cHost.isActive())
				customHostsActivationButton.setText(
						Messages.get("hostsActivationButtonDeactivate"));
			else
				customHostsActivationButton.setText(
						Messages.get("hostsActivationButtonActivate"));
		}
		
	}

	@FXML
	private void filterBlockedHostsTable() {
		String filter = blockedHostsTableFilter.getText();
		if (null == filter || "".equals(filter)) {
			main.fillBlockedHostObservableList();
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
		}
		else {
			main.fillBlockedHostObservableList(filter);
			drawStatusBar(filter + ": " + main.getBlockedHostsData().size() 
					+  " " + Messages.get("matches"), STATUS_OK);
		}
	}
	
	@FXML
	private void filterCustomHostsTable() {
		String filter = customHostsTableFilter.getText();
		if (null == filter || "".equals(filter)) {
			main.fillCustomHostObservableList();
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
		}
		else {
			main.fillCustomHostObservableList(filter);
			drawStatusBar(filter + ": " + main.getCustomHostsData().size() 
				+  " " + Messages.get("matches"), STATUS_OK);
		}
	}

	private void drawStatusBar(String message, int status) {
		// System.out.println(message);
		if (null != message) {
			this.statusBarLabel.setText(message);
		}

		if (STATUS_OK == status)
			this.statusBar.setStyle(Settings.get("statusBarColorOk"));
		else if (STATUS_UPDATE == status)
			this.statusBar.setStyle(Settings.get("statusBarColorUpdate"));
		else if (STATUS_ERROR == status)
			this.statusBar.setStyle(Settings.get("statusBarColorError"));

	}

	@FXML
	private void toggleWindowsDNSClient() {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
			Logger.err("Trying to modify Windows registry in no-DOS system");
			return;
		}
		try {
			boolean oldValue = WindowsUtil.isDNSClientStartActivated();
			if (settingDNSclientCheckBox.isSelected()) {
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /t REG_DWORD /v Start /d 2 /f");
				Logger.log("windows DNS client activated");
				drawStatusBar(Messages.get("WindowsDNSClientActivated"), STATUS_OK);
			} else {
				Runtime.
				   getRuntime().
				   exec("reg add HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\Dnscache /t REG_DWORD /v Start /d 4 /f");
				Logger.log("windows DNS client deactivated");
				drawStatusBar(Messages.get("WindowsDNSClientDeactivated"), STATUS_OK);
			}
			
			if (oldValue == WindowsUtil.isDNSClientStartActivated()) {
				String error = oldValue==true?
						"WindowsDNSClientNotDeactivated"
						: "WindowsDNSClientNotActivated"; 
				Logger.err(Messages.get(error));
				drawStatusBar(Messages.get(error), STATUS_ERROR);
			}
		} catch (IOException e) {
			e.printStackTrace();
			//Validate the case the file can't be accesed (not enought permissions)
			Logger.err(e.getMessage());
		} 
	}

	@FXML
	private void openHelp() {
		try {
			File tempHelp = File.createTempFile("help", ".html");
			tempHelp.deleteOnExit();
			Files.copy(
					MainView.class.getResourceAsStream(Settings.get("helpPathLocationEN"))
					, tempHelp.toPath()
					, StandardCopyOption.REPLACE_EXISTING);
			Desktop.getDesktop().browse(tempHelp.toURI());
		} catch (IOException e) {
			//e.printStackTrace();
			drawStatusBar(Messages.get("helpFileNotFound"), STATUS_ERROR);
			Logger.err(e.getMessage());
		}
	}
}
