package jp.lovefiat.works.opencv.view;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import jp.lovefiat.works.opencv.ImageOperation;
import jp.lovefiat.works.opencv.ImageOperation.Operator;
import jp.lovefiat.works.opencv.MainApplication;
import jp.lovefiat.works.opencv.model.FileTreeItem;
/**
 * ビューコントローラー
 * @author take
 *
 */
public class ContentViewController {
	
	private static final String ID_BUTTON_HAAR_LIKE = "button_haar_like";
	private static final String ID_BUTTON_CANNY = "button_canny";
	private static final String ID_BUTTON_HOUGH_LINES_P = "button_hough_lines_p";
	private static final String ID_BUTTON_SIMPLE_BLOB = "button_simple_blob";
	private static final String ID_CLEANUP_RESULT_IMAGES = "ID_CLEANUP_RESULT_IMAGES";

	private MainApplication mainApp;
	
	private Path rootDir;
	
	@FXML private TreeView<FileTreeItem> fileTree;
	@FXML private ContextMenu fileTreeContextMenu;
	@FXML private AnchorPane imageViewPane;
	@FXML private ImageView imagePreview;
	@FXML private TextField text_canny_threshold1;
	@FXML private TextField text_canny_threshold2;
	@FXML private TextField text_hough_threshold1;
	@FXML private TextField text_hough_threshold2;
	@FXML private TextField text_hough_rho;
	@FXML private TextField text_hough_threshold;
	@FXML private TextField text_hough_minLineLength;
	@FXML private TextField text_hough_maxLineGap;

	@FXML
	private void initialize() {
		rootDir = new File(".").toPath();
		
	}
	/**
	 * メインアプリ／データソースを設定
	 * @param mainApp
	 */
	public void setMainApp(MainApplication mainApp) {
		this.mainApp = mainApp;
		
		initializeFileTree();
		initializeImagePreview();
		initializeTabView();
	}
	/**
	 * ツリービューノード拡張リスナー
	 * @author take
	 *
	 */
	private class TreeViewExpandedListener implements ChangeListener<Boolean> {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			BooleanProperty bb = (BooleanProperty) observable;
			if (newValue) {
				@SuppressWarnings("unchecked")
				TreeItem<FileTreeItem> t = (TreeItem<FileTreeItem>) bb.getBean();
				t.getChildren().clear();
				// ノードを更新
				updateFileTreeNode(t);
			}
		}
	}
	/**
	 * ツリービューノード選択リスナー
	 * @author take
	 *
	 */
	private class TreeViewChangedListener implements ChangeListener<TreeItem<FileTreeItem>> {

		@Override
		public void changed(ObservableValue<? extends TreeItem<FileTreeItem>> observable,
				TreeItem<FileTreeItem> oldValue, TreeItem<FileTreeItem> newValue) {

			File file = newValue.getValue().getFile();
			if (isImageFile(file)) {

				imagePreview.setFitWidth(imageViewPane.getWidth());
				imagePreview.setFitHeight(imageViewPane.getHeight());
				imagePreview.setPreserveRatio(true);

				Image image = new Image(file.toURI().toString());
				imagePreview.setImage(image);

			}
		}
	}
	
	private void initializeFileTree() {
		
		if (fileTree == null) {
			return;
		}
		File file = new File(".");
		FileTreeItem fileItem = new FileTreeItem(file);
		fileItem.setCaption(String.format(". (%s)", file.toPath().toAbsolutePath().toString()));
		TreeItem<FileTreeItem> root = new TreeItem<>(fileItem);
		fileTree.setRoot(root);
		updateFileTreeNode(root);
		root.expandedProperty().addListener(new TreeViewExpandedListener());

		fileTree.getSelectionModel().selectedItemProperty().addListener(new TreeViewChangedListener());
		
	}
	/**
	 * イメージプレビューを初期化
	 */
	private void initializeImagePreview() {
	}
	private void initializeTabView() {
		this.text_canny_threshold1.setText("150");
		this.text_canny_threshold2.setText("100");
		this.text_hough_threshold1.setText("200");
		this.text_hough_threshold2.setText("3");
		this.text_hough_rho.setText("1");
		this.text_hough_threshold.setText("80");
		this.text_hough_minLineLength.setText("30");
		this.text_hough_maxLineGap.setText("10");
	}
	/**
	 * 
	 * @param target
	 */
	private void updateFileTreeNode(TreeItem<FileTreeItem> target) {
		if (target == null) {
			return;
		}
		// ルートノードを取得
		TreeItem<FileTreeItem> root = fileTree.getRoot();
		File currentFile;
		if (target == root) {
			currentFile = rootDir.toFile();
		} else {
			currentFile = target.getValue().getFile();
		}
		// 子ノード群を再構築
		target.getChildren().clear();
		if (currentFile.isDirectory()) {
			File[] files = currentFile.listFiles();
			if (files == null) {
				return;
			}
			TreeItem<FileTreeItem> item;
			for (File file : files) {
				if (file.getName().startsWith(".")) {
					continue;
				}
				item = new TreeItem<>(new FileTreeItem(file));
				item.setValue(new FileTreeItem(file));
				if (file.isDirectory()) {
					item.expandedProperty().addListener(new TreeViewExpandedListener());
					item.getChildren().add(new TreeItem<FileTreeItem>(new FileTreeItem("リストアップしています...")));
				}
				target.getChildren().add(item);
			}
		}
	}
	/**
	 * ファイル名より拡張子を取得
	 * 
	 * @param fileName
	 * @return
	 */
	private String getExtention(String fileName) {
		String ext = "";
		int idx = fileName.lastIndexOf(".");
		if (idx != -1) {
			ext = fileName.substring(idx).toLowerCase();
		}
		return ext;
	}
	/**
	 * 画像ファイルか否か
	 * @param file
	 * @return
	 */
	private boolean isImageFile(File file) {
		// 選択中のファイルがイメージファイルの時だけ処理する
		boolean need = false;
		if (file.isFile()) {
			String ext = getExtention(file.getName());
			need = ext.equals(".jpg")
					|| ext.equals(".jpeg")
					|| ext.equals(".png");
		}
		return need;

	}
	/**
	 * 画像処理による生成結果画像ファイルだけ削除
	 */
	private void cleanupResultImages() {
		fileTree.getSelectionModel()
				.getSelectedItems()
				.stream()
				.filter(new Predicate<TreeItem<FileTreeItem>>() {

					@Override
					public boolean test(TreeItem<FileTreeItem> t) {
						FileTreeItem val = t.getValue();
						if (val == null) {
							return false;
						}
						// 選択中のファイルがイメージファイルの時だけ処理する
						File file = val.getFile();
						return isImageFile(file);
					}
				})
				.forEach(new Consumer<TreeItem<FileTreeItem>>() {

					@Override
					public void accept(TreeItem<FileTreeItem> t) {
						File selectedFile = t.getValue().getFile();
						String prefix = selectedFile.getName().split("\\.")[0].toLowerCase();
						String ext = getExtention(selectedFile.getName()).toLowerCase();
						// イメージファイル名をベースに
						File[] files = selectedFile.getParentFile().listFiles(new FilenameFilter() {
							
							@Override
							public boolean accept(File dir, String name) {
								if (name.toLowerCase().matches(String.format("%s-.+\\%s", prefix, ext))) {
									return true;
								}
								return false;
							}
						});
						// Prefix + "-*" + 拡張子 のファイルを削除する
						if (files != null) {
							for (File file : files) {
								file.delete();
							}
						}
					}
				});
		TreeItem<FileTreeItem> selectedOne = fileTree.getSelectionModel().getSelectedItems().get(0);
		if (selectedOne != null) {
			updateFileTreeNode(selectedOne.getParent());
		}

	}
	/**
	 * ツリービューで選択中のファイルを取得
	 * @return
	 */
	private File getSelectedFile() {
		TreeItem<FileTreeItem> item = getSelectedTreeItem();
		File selected = null;
		if (item != null) {
			selected = item.getValue().getFile();
		}
		return selected;
	}
	/**
	 * ツリービューで選択中のアイテムを取得
	 * @return
	 */
	private TreeItem<FileTreeItem> getSelectedTreeItem() {
		TreeItem<FileTreeItem> item = this.fileTree.getSelectionModel().getSelectedItem();
		return item;
	}
	private void setSelectEqualTreeItem(TreeItem<FileTreeItem> parentItem, File target) {
		for (TreeItem<FileTreeItem> item : parentItem.getChildren()) {
			if (item.getValue().getFile().equals(target)) {
				this.fileTree.getSelectionModel().select(item);
				break;
			}
		}
	}

	@FXML
	private void handleOnAction(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof MenuItem) {
			switch (((MenuItem) source).getId()) {
			case ID_CLEANUP_RESULT_IMAGES:
				cleanupResultImages();
				break;
			default:
				break;
			}

		} else if (source instanceof Button) {
			File selectedFile;
			TreeItem<FileTreeItem> selectedItem;
			TreeItem<FileTreeItem> parentItem;
			Path nextPath;
			ImageOperation operation;
			switch ( ((Button)source).getId() ) {
			case ID_BUTTON_HAAR_LIKE:
				operation = new ImageOperation(Operator.FACE_DETECT);
				accept(operation);
				selectedItem = getSelectedTreeItem();
				if (selectedItem == null) {
					break;
				}
				selectedFile = getSelectedFile();
				nextPath = selectedFile.toPath();
				operation.sourceFile = nextPath.toFile();
				this.mainApp.getPilot().flight(operation);
				parentItem = getSelectedTreeItem().getParent();
				updateFileTreeNode(parentItem);
				setSelectEqualTreeItem(parentItem, selectedItem.getValue().getFile());
				break;
			case ID_BUTTON_SIMPLE_BLOB:
				operation = new ImageOperation(Operator.SIMPLE_BLOB_DETECT);
				accept(operation);
				selectedItem = getSelectedTreeItem();
				if (selectedItem == null) {
					break;
				}
				selectedFile = getSelectedFile();
				nextPath = selectedFile.toPath();
				operation.sourceFile = nextPath.toFile();
				this.mainApp.getPilot().flight(operation);
				parentItem = getSelectedTreeItem().getParent();
				updateFileTreeNode(parentItem);
				setSelectEqualTreeItem(parentItem, selectedItem.getValue().getFile());
				break;
				
			case ID_BUTTON_CANNY:
				operation = new ImageOperation(Operator.EDGE_DETECT);
				accept(operation);
				selectedItem = getSelectedTreeItem();
				if (selectedItem == null) {
					break;
				}
				selectedFile = getSelectedFile();
				nextPath = selectedFile.toPath();
				operation.sourceFile = nextPath.toFile();
				this.mainApp.getPilot().flight(operation);
				parentItem = getSelectedTreeItem().getParent();
				updateFileTreeNode(parentItem);
				setSelectEqualTreeItem(parentItem, selectedItem.getValue().getFile());
				break;
				
			case ID_BUTTON_HOUGH_LINES_P:
				operation = new ImageOperation(Operator.HOUGHT_LINES_P);
				accept(operation);
				selectedItem = getSelectedTreeItem();
				if (selectedItem == null) {
					break;
				}
				selectedFile = getSelectedFile();
				nextPath = selectedFile.toPath();
				operation.sourceFile = nextPath.toFile();
				this.mainApp.getPilot().flight(operation);
				parentItem = getSelectedTreeItem().getParent();
				updateFileTreeNode(parentItem);
				setSelectEqualTreeItem(parentItem, selectedItem.getValue().getFile());
				break;

			default:
				break;
			}
		}
	}
	private void accept(ImageOperation operation) {
		switch (operation.getOperator()) {
		case EDGE_DETECT:
			operation.mThresHold1 = getDoubleValue(this.text_canny_threshold1);
			operation.mThresHold2 = getDoubleValue(this.text_canny_threshold2);
			break;
			
		case HOUGHT_LINES_P:
			operation.mThresHold1 = getDoubleValue(this.text_hough_threshold1);
			operation.mThresHold2 = getDoubleValue(this.text_hough_threshold2);
			operation.mRho = getDoubleValue(this.text_hough_rho);
			operation.mThresHold = getIntValue(this.text_hough_threshold);
			operation.mMinLineLength = getDoubleValue(this.text_hough_minLineLength);
			operation.mMaxLineGap = getDoubleValue(this.text_hough_maxLineGap);
			break;
		default:
			break;
		}
	}
	private double getDoubleValue(TextField field) {
		double val = Double.NaN;
		try {
			val = Double.valueOf(field.getText());
			
		} catch (Exception e) {
			
		}
		return val;
	}
	private int getIntValue(TextField field) {
		int val = Integer.MAX_VALUE;
		try {
			val = Integer.valueOf(field.getText());
			
		} catch (Exception e) {
			
		}
		return val;
	}

}
