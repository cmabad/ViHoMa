package application;
	
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import application.business.impl.ServiceFactoryImpl;
import application.conf.Factory;
import application.model.Configuration;
import application.model.CustomHost;
import application.model.Host;
import application.persistence.sqlite.SQLiteRepositoryFactory;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.HostsFileManager;
import application.util.SystemUtil;
import application.util.WebUtil;
import application.util.WindowsUtil;
import application.util.properties.Messages;
import application.view.ErrorAdminController;
import application.view.ErrorDialogController;
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
    private final static boolean isAdmin = SystemUtil.isAdmin();
    
    public Main() {
    	fillBlockedHostObservableList();
    	fillCustomHostObservableList();
    }
    
    public static void main(String[] args) throws URISyntaxException {
    	configure();
    	if (0 < args.length && "-quiet".equals(args[0])) {
    		quietRun();    		
    		System.exit(0);
    	} else 
    		launch(args);
    }
  
    private static void quietRun() {
    	if (!isAdmin) {
    		System.out.println(Messages.get("errorVihomaRequiresAdminRights"));
    		SystemUtil.removeVihomaFolderPath();
    		System.exit(0);
    	}
    		
    	Factory.service.forHost().updateDatabaseFromWeb();
		try {
			HostsFileManager.persistHostsFile(
					Factory.service.forHost().findAllActive()
					, Factory.service.forConfiguration().getBlockedAddress()
					, Factory.service.forCustomHost().findAllActive());
		} catch (IOException e) {
			System.out.println(Messages.get("oops"));
		}
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
        if (isAdmin) 
	        showMainOverview();
        else {
        	showErrorAdminRightsDialog();
        	SystemUtil.removeVihomaFolderPath();
    		System.exit(0);
        }
    }
    
    public void errorExit() {
    	showErrorDialog();
    	System.exit(0);
    }

	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setMaxHeight(400);
            primaryStage.setMaxWidth(600);
            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(400);
            primaryStage.show();
        } catch (IOException e) {
            errorExit();
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
            errorExit();
        }
    }
    
    public static boolean showErrorAdminRightsDialog() {
    	 try {
    	        FXMLLoader loader = new FXMLLoader();
    	        loader.setLocation(Main.class.getResource("view/ErrorAdminDialog.fxml"));
    	        AnchorPane page = (AnchorPane) loader.load();

    	        Stage dialogStage = new Stage();
    	        dialogStage.setTitle("Vihoma");
    	        dialogStage.getIcons().add(
    	        		new Image(Main.class.getClassLoader()
    	        					.getResourceAsStream("resources/ico.png")));
    	        dialogStage.initModality(Modality.WINDOW_MODAL);
    	        dialogStage.initOwner(primaryStage);
    	        Scene scene = new Scene(page);
    	        dialogStage.setScene(scene);

    	        ErrorAdminController controller = loader.getController();
    	        controller.setDialogStage(dialogStage);

    	        dialogStage.showAndWait();

    	        return controller.isOkClicked();
    	    } catch (IOException e) {
    	        return false;
    	    }
    }
    
    public boolean showErrorDialog() {
   	 try {
   	        FXMLLoader loader = new FXMLLoader();
   	        loader.setLocation(Main.class.getResource("view/ErrorDialog.fxml"));
   	        AnchorPane page = (AnchorPane) loader.load();

   	        Stage dialogStage = new Stage();
   	        dialogStage.setTitle("Vihoma");
   	        dialogStage.getIcons().add(
   	        		new Image(Main.class.getClassLoader()
   	        					.getResourceAsStream("resources/ico.png")));
   	        dialogStage.initModality(Modality.WINDOW_MODAL);
   	        dialogStage.initOwner(primaryStage);
   	        Scene scene = new Scene(page);
   	        dialogStage.setScene(scene);

   	        ErrorDialogController controller = loader.getController();
   	        controller.setDialogStage(dialogStage);

   	        dialogStage.showAndWait();

   	        return controller.isOkClicked();
   	    } catch (IOException e) {
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
    	firstRun();
    	//Messages.setLanguage("enEN");
    }
    
   private static void firstRun() {
	   Configuration first = 
   			Factory.service.forConfiguration().findByParameter("firstRun");
		if (null == first || "yes".equals(first.getValue())) {
			try {
				if (WindowsUtil.isDNSClientActivated())
					WindowsUtil.toggleWindowsDNSClient();
				Factory.service.forConfiguration().set("firstRun", "no");
			} catch (IOException e) {
//				Logger.err(e.getMessage());
				//logging here may cause errors on unix-like systems 
			}
			WebUtil.openHelp();
		}
    }
}
