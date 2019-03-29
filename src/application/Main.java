package application;
	
import java.io.IOException;

import application.business.impl.ServiceFactoryImpl;
import application.conf.Factory;
import application.model.CustomHost;
import application.model.Host;
import application.persistence.sqlite.SQLiteRepositoryFactory;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.view.MainView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Host> blockedHosts = FXCollections.observableArrayList();
    private ObservableList<CustomHost> customHosts = FXCollections.observableArrayList();  
    
    public Main() {
    	configure();
    	
    	fillBlockedHostObservableList();
    	fillCustomHostObservableList();
    	
    	//TODO
//    	System.out.println(Factory.service.forConfiguration().findAll());
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
        this.primaryStage.setTitle("TFG-UO237441");

        initRootLayout();

        showMainOverview();
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
            
            MainView controller = loader.getController();
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

    public static void main(String[] args) {
        launch(args);
    }
    
    private void configure() {
    	Factory.service = new ServiceFactoryImpl();
    	Factory.repository = new SQLiteRepositoryFactory();
    	SQLiteJDBC.getManager(); //sets the database up
    	//Messages.setLanguage("enEN");
    }
}
