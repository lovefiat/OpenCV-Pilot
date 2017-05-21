package jp.lovefiat.works.opencv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jp.lovefiat.works.opencv.view.ContentViewController;

public class MainApplication extends Application {

	private static final String OPENCV3_HOME = "/usr/local/Cellar/opencv3/3.2.0/share/OpenCV";
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private Pilot pilot;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("OpenCV Test Driver");
		
		// OpenCV ライブラリの利用準備
		Pilot.setupHomeDirectory(OPENCV3_HOME);
		Pilot.loadLibrary();
		pilot = new Pilot();
		// OpenCV チェック
		pilot.check();

		initLayout();
		
		showLayoutView();
		
	}
	/**
	 * ルートレイアウトを初期化
	 */
	private void initLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("view/root_layout.fxml"));
			rootLayout = (BorderPane)loader.load();

			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * コンテンツレイアウトを初期化
	 */
	private void showLayoutView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("view/content_layout.fxml"));
			AnchorPane content = (AnchorPane)loader.load();
			
			this.rootLayout.setCenter(content);

			ContentViewController controller = loader.getController();
			controller.setMainApp(this);

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public Pilot getPilot() {
		return pilot;
	}

}
