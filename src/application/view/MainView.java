package application.view;

import java.util.List;

import application.Main;
import application.conf.Factory;
import application.model.CustomHost;
import application.model.Host;
import application.util.HostsFileManager;
import application.util.properties.Messages;
import application.util.properties.Settings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	private TableView<CustomHost> customHostsTable;
	@FXML
	private TableColumn<CustomHost, String> customDomainColumn;
	@FXML
	private TableColumn<CustomHost, String> customIpColumn;
	@FXML
	private TableColumn<CustomHost, Boolean> customActiveColumn;
	
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
		// Listen for selection changes and show the person details when changed.
		// blockedHostsTable.getSelectionModel().selectedItemProperty().addListener(
		// (observable, oldValue, newValue) -> showPersonDetails(newValue));
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

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainClass) {
		this.main = mainClass;

		// Add observable list data to the table
		fillBlockedHostsTable(main.getBlockedHostsData());
		fillCustomHostsTable(main.getCustomHostsData());
	}

	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeletePerson() {
		int selectedIndex = blockedHostsTable.getSelectionModel().getSelectedIndex();
		blockedHostsTable.getItems().remove(selectedIndex);
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
			Factory.service.forCustomHost().add(domain, address);
			main.fillCustomHostObservableList();
		} else
			drawStatusBar("error adding new host: " + errorMessage, STATUS_ERROR);
	}

	@FXML
	private void editHostsFile() {
		// TODO Factory.service.forHosts().editHostsFile();
		HostsFileManager.editHostsFile();
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
					Messages.get("deactivateBlockedHost")
					:Messages.get("activateBlockedHost"), STATUS_UPDATE);
			
			Factory.service.forHost().toggleStatus(host.getDomain());
			
			host.setActive(!host.isActive());
			
			main.fillBlockedHostObservableList();
			updateHostCountLabel();
			
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
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
					Messages.get("deactivateBlockedHost")
					:Messages.get("activateBlockedHost"), STATUS_UPDATE);
			
			Factory.service.forCustomHost().toggleStatus(host.getDomain());
			
			host.setActive(!host.isActive());
			
			main.fillCustomHostObservableList();
			
			drawStatusBar(Messages.get("upToDate"), STATUS_OK);
		}
	}

	@FXML
	private void changeBlockedHostsActivationButton(){
		Host host = blockedHostsTable.getSelectionModel().getSelectedItem();
		if (null == host)
			return;
		else {
			if (blockedHostsTable.getSelectionModel().getSelectedItem().isActive())
				blockedHostsActivationButton.setText(
						Messages.get("hostsActivationButtonActivate"));
			else
				blockedHostsActivationButton.setText(
						Messages.get("hostsActivationButtonDeactivate"));
		}
	}
	
	@FXML
	private void changeCustomHostsActivationButton(){
		Host cHost = customHostsTable.getSelectionModel().getSelectedItem();
		if (null == cHost)
			return;
		else {
			if (cHost.isActive())
				customHostsActivationButton.setText(
						Messages.get("hostsActivationButtonActivate"));
			else
				customHostsActivationButton.setText(
						Messages.get("hostsActivationButtonDeactivate"));
		}
	}

}
