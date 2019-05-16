package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import application.Main;
import application.conf.Factory;
import application.model.Configuration;
import application.model.CustomHost;
import application.model.Host;
import application.util.HostsFileManager;
import application.util.Logger;
import application.util.WindowsUtil;
import application.util.properties.Messages;
import application.util.properties.Settings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;

public class MainViewController {

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
	private Button updateButton; 
	@FXML
	private Label totalBlockedHostsCountLabel;
	@FXML
	private Label totalCustomHostsCountLabel;
	@FXML
	private Label totalBlockedHostsCountLabelBelow;
	@FXML
	private Label totalCustomHostsCountLabelBelow;
	@FXML
	private Label lastUpdateLabel;
	@FXML
	private Chart blockedHostsChart;
	
	@FXML
	private Label settingHelpLabel;
	@FXML
	private CheckBox settingStartupCheckBox;
	@FXML
	private CheckBox settingVihomaStartupCheckBox;
	@FXML
	private CheckBox settingShareBlockHostsCheckBox;
	//@FXML
	//private TextField settingTargetDomainField;
	//@FXML
	//private CheckBox settingTargetDomainCheckBox;
	@FXML
	private CheckBox settingDNSclientCheckBox;
	
	@FXML
	private Hyperlink githubLink;
	
	@FXML
	private AnchorPane statusBar;
	@FXML
	private Label statusBarLabel;

	private Main main;

	// private int status = 0;
	private final int STATUS_UPDATE = 1;
	public final int STATUS_OK = 0;
	private final int STATUS_ERROR = 2;

	/**
	 * The constructor. The constructor is called before the initialize() method.
	 */
	public MainViewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the person table with the two columns.
		domainColumn.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
		// categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
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

		//fillBlockedHostsTable(null);
		//fillCustomHostsTable(null);
		updateMainTab();
		setText();
		settingsLoader();
		blockedHostsActivationButton.setDisable(true);
		customHostsActivationButton.setDisable(true);
		
		// Listen for selection changes and show the person details when changed.
		// blockedHostsTable.getSelectionModel().selectedItemProperty().addListener(
		// (observable, oldValue, newValue) -> showPersonDetails(newValue));
	}
		
	private void setText() {
		totalBlockedHostsCountLabelBelow.setText(Messages.get("blockedHosts"));
		totalCustomHostsCountLabelBelow.setText(Messages.get("customHosts"));
		settingStartupCheckBox.setText(Messages.get("settingStartupCheckBox"));
		settingDNSclientCheckBox.setText(Messages.get("settingDNSclientCheckBox"));
		settingShareBlockHostsCheckBox.setText(Messages.get("settingShareBlockHostsCheckBox"));
		settingVihomaStartupCheckBox.setText(Messages.get("settingUpdateVihomaStartupCheckBox"));
		updateButton.setText(Messages.get("updateButton"));
		
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

	/*
	 * UI methods
	 */
	private void updateMainTab() {
		totalBlockedHostsCountLabel.setText(
				String.valueOf(Factory.service.forHost().getHostsCount()));
		
		totalCustomHostsCountLabel.setText(
				String.valueOf(Factory.service.forCustomHost().getHostsCount()));
		
		Date lastUpdate = new Date(TimeUnit.SECONDS.toMillis(
				Factory.service.forConfiguration().getLastUpdateTime()));		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		lastUpdateLabel.setText("Last update: " + df.format(lastUpdate));
	}

	private void fillBlockedHostsTable(ObservableList<Host> list) {
		blockedHostsTable.setItems(list);
	}

	private void fillCustomHostsTable(ObservableList<CustomHost> list) {
		customHostsTable.setItems(list);
	}

	@FXML
	private void getHostsFromWeb() {
		boolean alternative = false;
		drawStatusBar(Messages.get("checkingNewBlockedHosts"), STATUS_UPDATE);

		List<Host> hosts = Factory.service.forHost().downloadHostsFromWeb();
		
		if (null == hosts) {
			drawStatusBar(Messages.get("webConnectionError"), STATUS_ERROR);
			
			Logger.err(Settings.get("webSourceConnectionError"));
			hosts = Factory.service.forHost().downloadHostsFromAlternativeWeb();
			if (null == hosts) {
//				e.printStackTrace();
				drawStatusBar(Messages.get("webConnectionError"), STATUS_ERROR);
				return;
			} else {
				alternative = true;
			}
		}

		drawStatusBar(Messages.get("updatingBlockedHostsList"), STATUS_UPDATE);
		Factory.service.forHost().addHosts(hosts);
		Factory.service.forConfiguration().setLastUpdateTime();
		main.fillBlockedHostObservableList();

		updateMainTab();

//    	Factory.service.forHost().persistOnHostsFile();
		editHostsFile();
		drawStatusBar(alternative? 
				Messages.get("upToDateAlternative")
				:Messages.get("upToDate"), STATUS_OK);
	}

	@FXML
	private void blockNewHost() {
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

			if (Factory.service.forConfiguration().isSharingAllowed())
				uploadNewBlockedHost(domain);

			editHostsFile();
			main.fillBlockedHostObservableList();
			updateMainTab();
			drawStatusBar(domain + " " + Messages.get("blockNewHostSuccess"), STATUS_OK);
		} else {
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
		}
	}

	private void uploadNewBlockedHost(String domain) {
		Boolean uploadSuccess = Factory.service.forHost().updateHost(domain);
		if (null == uploadSuccess || !uploadSuccess.booleanValue())
			Logger.err(domain + " " + Settings.get("blockedHostUploadError"));
		else {
			Logger.log(domain + " " + Settings.get("blockedHostUploadSuccess"));
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
			editHostsFile();
			main.fillCustomHostObservableList();
			drawStatusBar(domain + " " + Messages.get("newCustomHostSuccess"), STATUS_OK);
		} else
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
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
			editHostsFile();
			filterBlockedHostsTable();
			updateMainTab();
			
			if (host.isActive())
				drawStatusBar(host.getDomain() + " " + Messages.get("activatedHost"), STATUS_OK);
			else
				drawStatusBar(host.getDomain() + " " + Messages.get("unactivatedHost"), STATUS_OK);
			
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
			editHostsFile();
			filterCustomHostsTable();
			updateMainTab();
			
			if (host.isActive())
				drawStatusBar(host.getDomain() + " " + Messages.get("activatedCustomHost"), STATUS_OK);
			else
				drawStatusBar(host.getDomain() + " " + Messages.get("unactivatedCustomHost"), STATUS_OK);
			
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
						Messages.get("hostsActivationButtonUnblock"));
			else
				blockedHostsActivationButton.setText(
						Messages.get("hostsActivationButtonBlock"));
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

	/**
	SETTINGS
	*/
	
	@FXML
	private void toggleWindowsDNSClient() {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
			Logger.err("Trying to modify Windows registry in no-DOS system");
			return;
		}
		try {
			boolean wasActivated = WindowsUtil.isDNSClientStartActivated();
			if (WindowsUtil.toggleWindowsDNSClient()) {
				if(wasActivated) {
					drawStatusBar(Messages.get("WindowsDNSClientDeactivated"), STATUS_OK);
					Logger.log("windows DNS client deactivated");
				}else {
					drawStatusBar(Messages.get("WindowsDNSClientActivated"), STATUS_OK);
					Logger.log("windows DNS client activated");
				}
			} else {
				String error = (wasActivated==true)?
						"WindowsDNSClientNotDeactivated"
						: "WindowsDNSClientNotActivated"; 
				Logger.err(Messages.get(error));
				settingDNSclientCheckBox.setSelected(wasActivated);
				drawStatusBar(Messages.get(error), STATUS_ERROR);
			}			
		} catch (IOException e) {
			// Registry cannot be read
			Logger.err(e.getMessage());
		}
		
	}
	
	@FXML
	private void toggleWindowsStartup() {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
			Logger.err("Trying to modify Windows registry in no-DOS system");
			return;
		}
		try {
			boolean wasSetUp = WindowsUtil.isRunAtStartup();
			if (WindowsUtil.toggleWindowsStartup()) {
				if(!wasSetUp) {
					drawStatusBar(Messages.get("WindowsStartupDeactivated"), STATUS_OK);
					Logger.log("windows Startup deactivated");
				}else {
					drawStatusBar(Messages.get("WindowsStartupActivated"), STATUS_OK);
					Logger.log("windows Startup activated");
				}
			} else {
				String error = (wasSetUp==true)?
						"WindowsStartupNotDeactivated"
						: "WindowsStartupNotActivated"; 
				Logger.err(Messages.get(error));
				settingStartupCheckBox.setSelected(wasSetUp);
				drawStatusBar(Messages.get(error), STATUS_ERROR);
			}			
		} catch (IOException e) {
			// Registry cannot be read
			Logger.err(e.getMessage());
		}
	}
	
	@FXML
	private void toggleVihomaStartup() {
		String updateSetting = "updateAtVihomaStartup";
		Configuration update = Factory.service.forConfiguration()
				.findByParameter(updateSetting);
		
		if (null != update && "yes".equals(update.getValue())) {
			Factory.service.forConfiguration().set(updateSetting, "no");
			Logger.log(Settings.get("updateAtVihomaStartupDeactivated"));
			drawStatusBar(Messages.get("updateAtVihomaStartupDeactivated"), STATUS_OK);
		} else {
			Factory.service.forConfiguration().set(updateSetting, "yes");
			Logger.log(Settings.get("updateAtVihomaStartupActivated"));
			drawStatusBar(Messages.get("updateAtVihomaStartupActivated"), STATUS_OK);
		}
	}
	
	@FXML
	private void toggleShareHosts() {
		if (Factory.service.forConfiguration().isSharingAllowed()) {
			Factory.service.forConfiguration().set("shareHosts", "no");
			drawStatusBar(Messages.get("shareHostsDisabled"), STATUS_OK);
			Logger.log(Settings.get("shareHostsDisabled"));
		} else {
			Factory.service.forConfiguration().set("shareHosts", "yes");
			drawStatusBar(Messages.get("shareHostsEnabled"), STATUS_OK);
			Logger.log(Settings.get("shareHostsEnabled"));
		}
	}

	private void settingsLoader() {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") != 0) {
			settingDNSclientCheckBox.setDisable(true);
			settingStartupCheckBox.setDisable(true);
			return;
		} else {
			try {
				settingDNSclientCheckBox.setSelected(
						!WindowsUtil.isDNSClientStartActivated());
				settingStartupCheckBox.setSelected(
						WindowsUtil.isRunAtStartup());
				settingShareBlockHostsCheckBox.setSelected(
						Factory.service.forConfiguration().isSharingAllowed());
				settingVihomaStartupCheckBox.setSelected(
						Factory.service.forConfiguration().isUpdateAtVihomaStartupEnabled());
			} catch (IOException e) {
//				e.printStackTrace();
				Logger.err(e.getMessage());
			}
		}
	}
	
	@FXML
	private void openHelp() {
		try {
			File tempHelp = File.createTempFile("help", ".html");
			tempHelp.deleteOnExit();
			Files.copy(
					MainViewController.class.getResourceAsStream(Settings.get("helpPathLocationEN"))
					, tempHelp.toPath()
					, StandardCopyOption.REPLACE_EXISTING);
			Desktop.getDesktop().browse(tempHelp.toURI());
		} catch (IOException e) {
			//e.printStackTrace();
			drawStatusBar(Messages.get("helpFileNotFound"), STATUS_ERROR);
			Logger.err(e.getMessage());
		}
	}
	
	/*
	 * ABOUT 
	 */
	
	@FXML
	private void openGithubLink() {
		try {
			Desktop.getDesktop().browse(URI.create(Settings.get("sourceCodeHttpLink")));
		} catch (IOException e) {
			Logger.err(Settings.get("sourceCodeLinkError"));
		}
	}
	
	
	 /* Common
	 */
	private void editHostsFile() {
		// TODO Factory.service.forHosts().editHostsFile();
		HostsFileManager.editHostsFile(
				Factory.service.forHost().findAllActive()
				, Factory.service.forConfiguration().getBlockedAddress()
				, Factory.service.forCustomHost().findAllActive());
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
}