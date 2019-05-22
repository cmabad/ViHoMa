package application.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
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
import application.util.WebUtil;
import application.util.WindowsUtil;
import application.util.properties.Messages;
import application.util.properties.Settings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	private SplitMenuButton newBlockedHostCategory;

	@FXML
	private TextField newCustomAddressField;
	@FXML
	private Button newBlockedHostButton;
	@FXML
	private Button newCustomHostButton;
	
	@FXML
	private ImageView icon;
	@FXML
	private Label vihomaLabel;
	@FXML
	private Label vihomaLabelBelow;
	@FXML
 	private Button updateButton; 
	@FXML
	private Label totalBlockedHostsCountLabel;
	@FXML
	private Label totalCustomHostsCountLabel;
	@FXML
	private Label totalUserBlockedHostsCountLabel;
	@FXML
	private Label totalBlockedHostsCountLabelBelow;
	@FXML
	private Label totalCustomHostsCountLabelBelow;
	@FXML
	private Label totalUserBlockedHostsCountLabelBelow;
	@FXML
	private Label lastUpdateLabel;

	
	@FXML
	private Label settingHelpLabel;
	@FXML
	private CheckBox settingStartupCheckBox;
	@FXML
	private CheckBox settingVihomaStartupCheckBox;
	@FXML
	private CheckBox settingShareBlockHostsCheckBox;
	@FXML
	private Label settingTargetDomainLabel; 
	@FXML
	private TextField settingTargetDomainField;
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
		icon = new ImageView(new Image(MainViewController.class.getClassLoader()
							.getResourceAsStream("resources/ico-big.png")));
		vihomaLabel.setText(Messages.get("vihomaLabel"));
		vihomaLabelBelow.setText(Messages.get("vihomaLabelBelow"));
		updateMainTab();
		setText();
		settingsLoader();
		blockedHostsActivationButton.setDisable(true);
		customHostsActivationButton.setDisable(true);
		newCustomAddressField.setDisable(true);
		newCustomHostButton.setDisable(true);
		newBlockedHostButton.setDisable(true);
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
		updateButton.setText(Messages.get("updateButton"));
		
		totalBlockedHostsCountLabel.setText(
				String.valueOf(Factory.service.forHost().getHostsCount()));
		
		totalCustomHostsCountLabel.setText(
				String.valueOf(Factory.service.forCustomHost().getHostsCount()));
		
		totalUserBlockedHostsCountLabel.setText(
				String.valueOf((Factory.service.forHost()
						.findByCategory(Host.CATEGORY_VIHOMA).size())));
		
		Date lastUpdate = new Date(TimeUnit.SECONDS.toMillis(
				Factory.service.forConfiguration().getLastUpdateTime()));
		DateFormat df = DateFormat.getDateTimeInstance(
				DateFormat.MEDIUM, DateFormat.SHORT);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		lastUpdateLabel.setText("Last update: " + df.format(lastUpdate) + " UTC");
	}

	private void fillBlockedHostsTable(ObservableList<Host> list) {
		blockedHostsTable.setItems(list);
	}

	private void fillCustomHostsTable(ObservableList<CustomHost> list) {
		customHostsTable.setItems(list);
	}

	@FXML
	protected void updateDatabaseFromWeb() {
		List<Host> hosts = Factory.service.forHost().updateDatabaseFromWeb();

		if (null == hosts) {
			drawStatusBar(Messages.get("webConnectionError"), STATUS_ERROR);
			return;
		}		
		main.fillBlockedHostObservableList();
		updateMainTab();
		editHostsFile();
		drawStatusBar(Messages.get("upToDate"), STATUS_OK);
	}

	@FXML
	protected void blockNewHost() {
		String errorMessage = "";
		boolean valid = true;
		String domain = blockedHostsTableFilter.getText();

		if (null == domain || 0 == domain.length()) {
			valid = false;
			errorMessage += "No valid hostname!\n";
		}

		if (valid) {
			drawStatusBar(Messages.get("blockNewHostStart") + " " + domain, STATUS_UPDATE);
			
			if (0 == Factory.service.forHost().addHost(domain, Host.CATEGORY_VIHOMA)) {
				drawStatusBar(Messages.get("errorExistingDomain"), STATUS_ERROR);
				return;
			}

			if (Factory.service.forConfiguration().isSharingAllowed())
				WebUtil.uploadHostToWeb(domain);

			editHostsFile();
			main.fillBlockedHostObservableList();
			updateMainTab();
			blockedHostsTableFilter.setText("");
			drawStatusBar(domain + " " + Messages.get("blockNewHostSuccess"), STATUS_OK);
		} else {
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
		}
	}

	@FXML
	protected void addCustomHost() {
		String errorMessage = "";
		boolean valid = true;
		String domain = customHostsTableFilter.getText();
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
				if (0 == Factory.service.forCustomHost().add(domain, address)) {
					drawStatusBar(Messages.get("errorExistingDomain"), STATUS_ERROR);
					return;
				}
			} catch (IllegalArgumentException e){
				drawStatusBar(e.getMessage(), STATUS_ERROR);
			}
			editHostsFile();
			main.fillCustomHostObservableList();
			customHostsTableFilter.setText("");
			drawStatusBar(domain + " " + Messages.get("newCustomHostSuccess"), STATUS_OK);
		} else
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
	}

	@FXML
	/**
	 * activates/deactivates the selected host in the blocked hosts table
	 */
	protected void toggleBlockedHostStatus() {
		Host host = blockedHostsTable.getSelectionModel().getSelectedItem();

		if (null == host)
			drawStatusBar(Messages.get("noHostSelected"),STATUS_ERROR);
		else {
			Factory.service.forHost().toggleStatus(host.getDomain());
			
			host.setActive(!host.isActive());
			
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
	protected void toggleCustomHostStatus() {
		CustomHost host = customHostsTable.getSelectionModel().getSelectedItem();
		
		if (null == host)
			drawStatusBar(Messages.get("noHostSelected"),STATUS_ERROR);
		else {			
			Factory.service.forCustomHost().toggleStatus(host.getDomain());
			
			host.setActive(!host.isActive());
			
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
	protected void changeBlockedHostsActivationButton(){
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
	protected void changeCustomHostsActivationButton(){
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
	protected void filterBlockedHostsTable() {
		String filter = blockedHostsTableFilter.getText();
		if (null == filter || "".equals(filter)) {
			newBlockedHostButton.setDisable(true);
			main.fillBlockedHostObservableList();
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
		}
		else {
			main.fillBlockedHostObservableList(filter);
			newBlockedHostButton.setDisable(false);
			drawStatusBar(filter + ": " + main.getBlockedHostsData().size() 
					+  " " + Messages.get("matches"), STATUS_OK);
		}
	}
	
	@FXML
	protected void filterCustomHostsTable() {
		String filter = customHostsTableFilter.getText();
		if (null == filter || "".equals(filter)) {
			main.fillCustomHostObservableList();
			newCustomHostButton.setDisable(true);
			newCustomAddressField.setDisable(true);
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
		}
		else {
			main.fillCustomHostObservableList(filter);
			newCustomAddressField.setDisable(false);
			newCustomHostButton.setDisable(false);
			drawStatusBar(filter + ": " + main.getCustomHostsData().size() 
				+  " " + Messages.get("matches"), STATUS_OK);
		}
	}	

	/*
	SETTINGS
	*/
	
	@FXML
	protected void toggleWindowsDNSClient() {
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
				String error = (wasActivated)?
						"WindowsDNSClientNotDeactivated"
						: "WindowsDNSClientNotActivated"; 
				Logger.err(Messages.get(error));
				settingDNSclientCheckBox.setSelected(!wasActivated);
				drawStatusBar(Messages.get(error), STATUS_ERROR);
			}			
		} catch (IOException e) {
			// Registry cannot be read
			Logger.err(e.getMessage());
			drawStatusBar(Messages.get("oops"), STATUS_ERROR);
		}
		
	}
	
	@FXML
	protected void toggleWindowsStartup() {
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
				String error = (wasSetUp)?
						"WindowsStartupNotDeactivated"
						: "WindowsStartupNotActivated"; 
				Logger.err(Messages.get(error));
				settingStartupCheckBox.setSelected(wasSetUp);
				drawStatusBar(Messages.get(error), STATUS_ERROR);
			}			
		} catch (IOException e) {
			// Registry cannot be read
			Logger.err(e.getMessage());
			drawStatusBar(Messages.get("oops"), STATUS_ERROR);
		}
	}
	
	@FXML
	protected void toggleVihomaStartup() {
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
	protected void toggleShareHosts() {
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

	@FXML
	protected void changeTargetAddress() {
		String newAddress = settingTargetDomainField.getText();
		if (null == newAddress || "".equals(newAddress)) {
			Factory.service.forConfiguration().set("blockedAddress", "");
		}
		else {
			try {
				new CustomHost("237441", newAddress);
				Factory.service.forConfiguration().set("blockedAddress", newAddress);
			}  catch (IllegalArgumentException e){
				// the address is not valid; do nothing
				return;
			}
		}
		editHostsFile();
		drawStatusBar(Messages.get("newBlockedAddress") + 
				Factory.service.forConfiguration().getBlockedAddress()
				, STATUS_OK);
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
	protected void openHelp() {
		if (!WebUtil.openHelp())
			drawStatusBar(Messages.get("helpFileNotFound"), STATUS_ERROR);
	}
	
	/*
	 * ABOUT 
	 */
	
	@FXML
	protected void openGithubLink() {
		try {
			Desktop.getDesktop().browse(URI.create(Settings.get("sourceCodeHttpLink")));
		} catch (IOException e) {
			Logger.err(Settings.get("sourceCodeLinkError"));
			drawStatusBar(Messages.get("oops"), STATUS_ERROR);
		}
	}
	
	 /* Common
	 */
	private void editHostsFile() {
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