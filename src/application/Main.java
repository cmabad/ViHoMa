package application;
	
import java.io.File;
import java.io.IOException;

import application.business.impl.ServiceFactoryImpl;
import application.conf.Factory;
import application.model.Configuration;
import application.model.CustomHost;
import application.model.Host;
import application.persistence.sqlite.SQLiteRepositoryFactory;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.HostsFileManager;
import application.util.SystemUtil;
import application.view.ErrorAdminController;
import application.view.MainViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Host> blockedHosts = FXCollections.observableArrayList();
    private ObservableList<CustomHost> customHosts = FXCollections.observableArrayList();
    
    public Main() {
    	fillBlockedHostObservableList();
    	fillCustomHostObservableList();
    }
    
    public static void main(String[] args) { 
    	configure();
    	if (0 < args.length && "quiet".equals(args[0])) {
    		quietRun();    		
    		System.exit(0);
    	} else 
    		launch(args);
    }
  
    private static void quietRun() {
    	Factory.service.forHost().updateDatabaseFromWeb();
		HostsFileManager.editHostsFile(
				Factory.service.forHost().findAllActive()
				, Factory.service.forConfiguration().getBlockedAddress()
				, Factory.service.forCustomHost().findAllActive());		
	}

	public ObservableList<Host> getBlockedHostsData() {
        return blockedHosts;
    }
    
    public ObservableList<CustomHost> getCustomHostsData() {
        return customHosts;
    }
    
    public void fillBlockedHostObservableList() {
    	blockedHosts.clear();
    	for (Host host : Factory.service.forHost().findAll())
			blockedHosts.add(host);
    }
    
    public void fillCustomHostObservableList() {
    	customHosts.clear();
    	for (CustomHost chost : Factory.service.forCustomHost().findAll())
			customHosts.add(chost);
    }
    
    public void fillBlockedHostObservableList(String filter) {
		blockedHosts.clear();
		for (Host host : Factory.service.forHost().findByDomain(filter))
			blockedHosts.add(host);
	}
    
    public void fillCustomHostObservableList(String filter) {
    	customHosts.clear();
    	for (CustomHost chost : Factory.service.forCustomHost().findByDomainOrIp(filter))
			customHosts.add(chost);
    }
    
    @Override
    public void start(Stage primaryStagee) {
        primaryStage = primaryStagee;
        primaryStage.setTitle("ViHoMa");

        primaryStage.getIcons().add(
        		new Image(Main.class.getClassLoader()
        					.getResourceAsStream("resources/ico.png")));

        initRootLayout();
        if (SystemUtil.isAdmin()) {
	        showMainOverview();
	        updateAtStartup();
        } else {
        	showErrorAdminRightsDialog();
    		System.exit(0);
        }
    }
    
    private void updateAtStartup() {
    	Configuration update = Factory.service.forConfiguration()
				.findByParameter("updateAtVihomaStartup");
		if (null != update && "yes".equals(update.getValue()))
				quietRun();
	}

	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setMaxHeight(400);
            primaryStage.setMaxWidth(600);
            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(400);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the main view inside the root layout.
     */
    public void showMainOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/MainView.fxml"));
            AnchorPane mainView = (AnchorPane) loader.load();
            
            rootLayout.setCenter(mainView);
            
            MainViewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean showErrorAdminRightsDialog() {
    	 try {
    	        // Load the fxml file and create a new stage for the popup dialog.
    	        FXMLLoader loader = new FXMLLoader();
    	        loader.setLocation(Main.class.getResource("view/ErrorAdminDialog.fxml"));
    	        AnchorPane page = (AnchorPane) loader.load();

    	        // Create the dialog Stage.
    	        Stage dialogStage = new Stage();
    	        dialogStage.setTitle("Vihoma");
    	        dialogStage.getIcons().add(
    	        		new Image(Main.class.getClassLoader()
    	        					.getResourceAsStream("resources/ico.png")));
    	        dialogStage.initModality(Modality.WINDOW_MODAL);
    	        dialogStage.initOwner(primaryStage);
    	        Scene scene = new Scene(page);
    	        dialogStage.setScene(scene);

    	        // Set the person into the controller.
    	        ErrorAdminController controller = loader.getController();
    	        controller.setDialogStage(dialogStage);

    	        // Show the dialog and wait until the user closes it
    	        dialogStage.showAndWait();

    	        return controller.isOkClicked();
    	    } catch (IOException e) {
    	        //e.printStackTrace();
    	        return false;
    	    }
    }
    
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    private static void configure() {
    	File file = new File(SystemUtil.getVihomaFolderPath());
    	file.mkdirs();
    	Factory.service = new ServiceFactoryImpl();
    	Factory.repository = new SQLiteRepositoryFactory();
    	SQLiteJDBC.getManager(); //sets the database up
    	//Messages.setLanguage("enEN");
    }
}
