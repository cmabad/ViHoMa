package application.view;

import application.util.properties.Messages;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * This controller will be used to show the user a screen requiring to run the
 * program with admin privileges.
 */
public class ErrorAdminController {

	@FXML
	private Label text;
	private Stage dialogStage;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	protected void initialize() {
		text.setText(Messages.get("errorVihomaRequiresAdminRights"));
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	protected void handleOk() {
		okClicked = true;
		dialogStage.close();
	}
}
