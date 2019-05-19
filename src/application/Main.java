package application;
	
import java.io.IOException;
import java.util.List;

import application.business.impl.ServiceFactoryImpl;
import application.conf.Factory;
import application.model.Configuration;
import application.model.CustomHost;
import application.model.Host;
import application.persistence.sqlite.SQLiteRepositoryFactory;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.HostsFileManager;
import application.util.WebUtil;
import application.util.properties.Settings;
import application.view.MainViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Host> blockedHosts = FXCollections.observableArrayList();
    private ObservableList<CustomHost> customHosts = FXCollections.observableArrayList();

    public Main() {
    	fillBlockedHostObservableList();
    	fillCustomHostObservableList();
    	
    	//TODO
//    	System.out.println(Factory.service.forConfiguration().findAll());
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
    	List<Host> userAdded = Factory.service.forHost().findByCategory(
				Host.CATEGORY_VIHOMA);
		Factory.service.forHost().deleteAll();
		Factory.service.forHost().addHosts(WebUtil.getHostsFromWeb(0));
		Factory.service.forHost().addHosts(userAdded);
		Factory.service.forConfiguration().setLastUpdateTime();
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
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ViHoMa");

        this.primaryStage.getIcons().add(
        		new Image(
        				Settings.class.getClassLoader()
        					.getResourceAsStream("resources/ico.png")));

        initRootLayout();

        showMainOverview();
        
        updateAtStartup();
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
    
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    private static void configure() {
    	Factory.service = new ServiceFactoryImpl();
    	Factory.repository = new SQLiteRepositoryFactory();
    	SQLiteJDBC.getManager(); //sets the database up
    	//Messages.setLanguage("enEN");
    }
}
