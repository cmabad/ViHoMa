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
import application.model.CustomHost;
import application.model.Host;
import application.util.HostsFileManager;
import application.util.Logger;
import application.util.WebUtil;
import application.util.WindowsUtil;
import application.util.properties.Messages;
import application.util.properties.Settings;
import javafx.application.Platform;
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
	private Label settingWebSourceFieldLabel;
	@FXML
	private TextField settingWebSourceField;

	@FXML
	private Label settingStevenBlackCategoryLabel;
	@FXML
	private CheckBox settingStevenBlackCategoryFakenewsCheckBox;
	@FXML
	private CheckBox settingStevenBlackCategoryGamblingCheckBox;
	@FXML
	private CheckBox settingStevenBlackCategoryPornCheckBox;
	@FXML
	private CheckBox settingStevenBlackCategorySocialCheckBox;
	
	@FXML
	private Label versionLabel;
	
	@FXML
	private Hyperlink githubLink;
	
	@FXML
	private AnchorPane statusBar;
	@FXML
	private Label statusBarLabel;

	private Main main;

	private final int STATUS_UPDATE = 1;
	public final int STATUS_OK = 0;
	private final int STATUS_ERROR = 2;
	private boolean startNotification;
	
	/**
	 * The constructor. The constructor is called before the initialize() method.
	 */
	public MainViewController() {
		startNotification = true;
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		domainColumn.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
		// categoryColumn.setCellValueFactory(
		//		cellData -> cellData.getValue().categoryProperty());
		// statusColumn.setCellValueFactory(cellData
		// -> cellData.getValue().statusProperty().asObject());
		activeColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        activeColumn.setCellFactory(param 
        		-> new CheckBoxTableCell<Host, Boolean>());
		
        customActiveColumn.setCellValueFactory(
        		cellData -> cellData.getValue().activeProperty());
        customActiveColumn.setCellFactory(param 
        		-> new CheckBoxTableCell<CustomHost, Boolean>());
        
		customDomainColumn.setCellValueFactory(
				cellData -> cellData.getValue().domainProperty());
		customIpColumn.setCellValueFactory(
				cellData -> cellData.getValue().addressProperty());

		icon = new ImageView(new Image(MainViewController.class.getClassLoader()
							.getResourceAsStream("resources/ico-big.png")));
		updateMainTab();
		setText();
		settingsLoader();
		blockedHostsActivationButton.setDisable(true);
		customHostsActivationButton.setDisable(true);
		newCustomAddressField.setVisible(false);
		newCustomHostButton.setDisable(true);
		newBlockedHostButton.setDisable(true);
		}

	/**
	 * refreshes all the i18ned text of the GUI
	 */
	private void setText() {
		vihomaLabel.setText(Messages.get("vihomaLabel"));
		vihomaLabelBelow.setText(Messages.get("vihomaLabelBelow"));
		settingTargetDomainLabel.setText(Messages.get("settingBlockAddressLabel"));
		settingTargetDomainField.setText(Factory.service.forConfiguration()
				.getBlockedAddress());
		settingHelpLabel.setText(Messages.get("help"));
		totalBlockedHostsCountLabelBelow.setText(Messages.get("blockedHosts"));
		totalCustomHostsCountLabelBelow.setText(Messages.get("customHosts"));
		settingStartupCheckBox.setText(Messages.get("settingStartupCheckBox"));
		settingDNSclientCheckBox.setText(Messages.get("settingDNSclientCheckBox"));
		settingShareBlockHostsCheckBox.setText(
				Messages.get("settingShareBlockHostsCheckBox"));
		settingVihomaStartupCheckBox.setText(
						Messages.get("settingUpdateVihomaStartupCheckBox"));
		settingWebSourceFieldLabel.setText(
				Messages.get("settingHostsFileSourceLabel"));
		settingStevenBlackCategoryLabel.setText(Messages.get("StevenBlackCategoryLabel"));
		settingStevenBlackCategoryFakenewsCheckBox.setText(Messages.get("fakenews"));
		settingStevenBlackCategoryGamblingCheckBox.setText(Messages.get("gambling"));
		settingStevenBlackCategoryPornCheckBox.setText(Messages.get("porn"));
		settingStevenBlackCategorySocialCheckBox.setText(Messages.get("social"));
		updateButton.setText(Messages.get("updateButton"));
		versionLabel.setText(Settings.get("vihomaVersion"));
	}

	/** Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainClass) {
		this.main = mainClass;

		fillBlockedHostsTable(main.getBlockedHostsData());
		fillCustomHostsTable(main.getCustomHostsData());
	}

	/*
	 * UI methods
	 */
	private void updateMainTab() {
		int hostsCount =0;
		
		try {
			hostsCount = Factory.service.forHost().getHostsCount();
			if (0>hostsCount)
				throw new IllegalStateException();
		} catch (Exception e) {
			//an anomaly has been detected with the database, exit
			errorExit();
		}
		
		totalBlockedHostsCountLabel.setText(
				String.valueOf(hostsCount));
		
		totalCustomHostsCountLabel.setText(
				String.valueOf(Factory.service.forCustomHost().getHostsCount()));
		
		totalUserBlockedHostsCountLabel.setText(
				String.valueOf((Factory.service.forHost()
						.findByCategory(Host.CATEGORY_VIHOMA).size())));
		
		if (startNotification
				&& 0 == Factory.service.forHost().getHostsCount() 
				&& 0 == Factory.service.forCustomHost().getHostsCount()) {
			lastUpdateLabel.setText("Last update: " + Messages.get("never"));
			drawStatusBar(Messages.get("pleaseUpdate"), STATUS_UPDATE);
			startNotification = false;
		} else {
			Date lastUpdate = 
					new Date(TimeUnit.SECONDS.toMillis(
							Factory.service.forConfiguration().getLastUpdateTime()));
			DateFormat df=DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			lastUpdateLabel.setText("Last update: " + df.format(lastUpdate) + " UTC");
		}
	}

	private void fillBlockedHostsTable(ObservableList<Host> list) {
		blockedHostsTable.setItems(list);
	}

	private void fillCustomHostsTable(ObservableList<CustomHost> list) {
		customHostsTable.setItems(list);
	}

	@FXML
	protected void updateDatabaseFromWeb() {
		drawStatusBar(Messages.get("updating"), STATUS_UPDATE);
		System.out.println(Messages.get("updating"));
		updateMainTab();
		List<Host> hosts = Factory.service.forHost().updateDatabaseFromWeb();

		if (null == hosts) {
			drawStatusBar(Messages.get("webConnectionError"), STATUS_ERROR);
			updateMainTab();
			return;
		}		
		main.fillBlockedHostObservableList();
		updateMainTab();
		persistHostsFile();
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
			
			if (0 == Factory.service.forHost().addHost(domain)) {
				drawStatusBar(Messages.get("errorExistingDomain"), STATUS_ERROR);
				updateMainTab();
				return;
			}

			persistHostsFile();
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
			int count = Factory.service.forCustomHost().add(domain, address);
			if (0 >= count) {
				if (count<0)
					drawStatusBar(Messages.get("errorCustomAddress"), STATUS_ERROR);
				else
					drawStatusBar(Messages.get("errorExistingDomain"), STATUS_ERROR);
				updateMainTab();
				return;
			}
			
			persistHostsFile();
			main.fillCustomHostObservableList();
			updateMainTab();
			customHostsTableFilter.setText("");
			newCustomAddressField.setVisible(false);
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
			
			persistHostsFile();
			filterBlockedHostsTable();
			updateMainTab();
			
			if (host.isActive())
				drawStatusBar(host.getDomain() + " " 
						+ Messages.get("activatedHost"), STATUS_OK);
			else
				drawStatusBar(host.getDomain() + " " 
						+ Messages.get("unactivatedHost"), STATUS_OK);
			
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
			Factory.service.forCustomHost().toggleStatus(host.getDomain(), host.getAddress());
			
			host.setActive(!host.isActive());
			
			persistHostsFile();
			filterCustomHostsTable();
			updateMainTab();
			
			if (host.isActive())
				drawStatusBar(host.getDomain() + " " 
						+ Messages.get("activatedCustomHost"), STATUS_OK);
			else
				drawStatusBar(host.getDomain() + " " 
						+ Messages.get("unactivatedCustomHost"), STATUS_OK);
			
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
			newCustomAddressField.setText("");
			newCustomAddressField.setVisible(false);
			newCustomHostButton.setDisable(true);
			main.fillCustomHostObservableList();
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
		}
		else {
			main.fillCustomHostObservableList(filter);
			newCustomAddressField.setVisible(true);
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
			boolean wasActivated = WindowsUtil.isDNSClientActivated();
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
		Factory.service.forConfiguration().toggleUpdateAtVihomaStart();
		
		if (Factory.service.forConfiguration().isUpdateAtVihomaStartupEnabled())
			drawStatusBar(Messages.get("updateAtVihomaStartupActivated"), STATUS_OK);
		else
			drawStatusBar(Messages.get("updateAtVihomaStartupDeactivated"), STATUS_OK);
	}
	
	@FXML
	protected void toggleShareHosts() {
		Factory.service.forConfiguration().toggleSharing();
		if (Factory.service.forConfiguration().isSharingAllowed()) 
			drawStatusBar(Messages.get("shareHostsEnabled"), STATUS_OK);
		else
			drawStatusBar(Messages.get("shareHostsDisabled"), STATUS_OK);
	}

	@FXML
	protected void changeTargetAddress() {
		String newAddress = settingTargetDomainField.getText();
		if (null == newAddress || "".equals(newAddress)) {
			Factory.service.forConfiguration().setBlockedAddress("");
		}
		else {
			try {
				new CustomHost("237441", newAddress);
				Factory.service.forConfiguration().setBlockedAddress(newAddress);
			}  catch (IllegalArgumentException e){
				// the address is not valid
				drawStatusBar(Messages.get("errorCustomAddress"), STATUS_ERROR);
				return;
			}
		}
		persistHostsFile();
		drawStatusBar(Messages.get("newBlockedAddress")
				+ Factory.service.forConfiguration().getBlockedAddress(), STATUS_OK);
	}
	
	@FXML
	protected void changeWebSource() {
		String newWebSource = settingWebSourceField.getText();
		Factory.service.forConfiguration().setWebSource(newWebSource);
		if ("".equals(newWebSource)) {
			settingStevenBlackCategoryFakenewsCheckBox.setDisable(false);
			settingStevenBlackCategoryGamblingCheckBox.setDisable(false);
			settingStevenBlackCategoryPornCheckBox.setDisable(false);
			settingStevenBlackCategorySocialCheckBox.setDisable(false);
		} else {
			settingStevenBlackCategoryFakenewsCheckBox.setDisable(true);
			settingStevenBlackCategoryGamblingCheckBox.setDisable(true);
			settingStevenBlackCategoryPornCheckBox.setDisable(true);
			settingStevenBlackCategorySocialCheckBox.setDisable(true);
		}
		drawStatusBar(Messages.get("newWebSource") + " "
				+ Factory.service.forConfiguration().getWebSource(), STATUS_OK);
	}
	
	@FXML
	protected void changeStevenBlackCategories() {
		int categories = 0;
		StringBuilder chosenCategories = new StringBuilder();
		chosenCategories
			.append(Messages.get("categoriesListStart")+" ")
			.append(Messages.get("unifiedHosts"));
		
		if (settingStevenBlackCategoryFakenewsCheckBox.isSelected()) {
			categories += Host.CATEGORY_STEVENBLACK_FAKENEWS;
			chosenCategories.append(", ").append(Messages.get("fakenews"));
		}
		if (settingStevenBlackCategoryGamblingCheckBox.isSelected()) {
			categories += Host.CATEGORY_STEVENBLACK_GAMBLING;
			chosenCategories.append(", ").append(Messages.get("gambling"));
		}
		if (settingStevenBlackCategoryPornCheckBox.isSelected()) {
			categories += Host.CATEGORY_STEVENBLACK_PORN;
			chosenCategories.append(", ").append(Messages.get("porn"));
		}
		if (settingStevenBlackCategorySocialCheckBox.isSelected()) {
			categories += Host.CATEGORY_STEVENBLACK_SOCIAL;
			chosenCategories.append(", ").append(Messages.get("social"));
		}
		
		Factory.service.forConfiguration().setStevenBlackCategories(categories);
		drawStatusBar(chosenCategories.toString(), STATUS_OK);
	}
	
	private void settingsLoader() {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") != 0) {
			settingDNSclientCheckBox.setDisable(true);
			settingStartupCheckBox.setDisable(true);
			return;
		} else {
			try {
				settingDNSclientCheckBox.setSelected(
						!WindowsUtil.isDNSClientActivated());
				settingStartupCheckBox.setSelected(
						WindowsUtil.isRunAtStartup());
				settingShareBlockHostsCheckBox.setSelected(
						Factory.service.forConfiguration().isSharingAllowed());
				settingVihomaStartupCheckBox.setSelected(
						Factory.service.forConfiguration().isUpdateAtVihomaStartupEnabled());
			} catch (IOException e) {
				Logger.err(e.getMessage());
			}
		}
		String webSource = Factory.service.forConfiguration().getWebSource();
		settingWebSourceField.setText("".equals(webSource)? "":webSource);
		int categories = Factory.service.forConfiguration().getStevenBlackCategories();
		settingStevenBlackCategoryFakenewsCheckBox.setSelected(
				(categories&Host.CATEGORY_STEVENBLACK_FAKENEWS) 
					== Host.CATEGORY_STEVENBLACK_FAKENEWS);
		settingStevenBlackCategoryGamblingCheckBox.setSelected(
				(categories&Host.CATEGORY_STEVENBLACK_GAMBLING) 
					== Host.CATEGORY_STEVENBLACK_GAMBLING);
		settingStevenBlackCategoryPornCheckBox.setSelected(
				(categories&Host.CATEGORY_STEVENBLACK_PORN) 
					== Host.CATEGORY_STEVENBLACK_PORN);
		settingStevenBlackCategorySocialCheckBox.setSelected(
				(categories&Host.CATEGORY_STEVENBLACK_SOCIAL) 
					== Host.CATEGORY_STEVENBLACK_SOCIAL);
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
	
	/* 
	 * COMMON
	 */
	private void persistHostsFile() {
		try {
			HostsFileManager.persistHostsFile(
					Factory.service.forHost().findAllActive()
					, Factory.service.forConfiguration().getBlockedAddress()
					, Factory.service.forCustomHost().findAllActive());
			//if windows -> ipconfig /flushdns
		} catch (IOException e) {
			errorExit();
		}
	}
	
	private void drawStatusBar(String message, int status) {
		Platform.runLater(() -> {
		if (null != message) {
			this.statusBarLabel.setText(message);
		}

		if (STATUS_OK == status)
			this.statusBar.setStyle(Settings.get("statusBarColorOk"));
		else if (STATUS_UPDATE == status)
			this.statusBar.setStyle(Settings.get("statusBarColorUpdate"));
		else if (STATUS_ERROR == status)
			this.statusBar.setStyle(Settings.get("statusBarColorError"));
		});
	}

	private void errorExit() {
		main.errorExit();
	}
}