package jp.lovefiat.works.opencv;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * OpenCV (Open Source Computer Vision Library) の実験クラス
 * 
 * @author take
 *
 */
public class Pilot {
	/** ロガー */
	private static Logger logger = null;
	/** ネイティブライブラリの読み込みステータス */
	private static boolean isLibraryLoaded = false;
	/** OpenCV ホームディレクトリ */
	private static Path sOpencvHome = null;
	/**
	 * ロガーを取得します
	 * 
	 * @return
	 */
	private static Logger getLogger() {
		if (logger == null) {
			// ロガーをセットアップ
			logger = Logger.getLogger(Pilot.class.getName());
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.FINER);
			// ハンドラを追加
			ConsoleHandler handler = new ConsoleHandler();
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);
		}
		return logger;
	}
	
	/**
	 * ライブラリを読み込む
	 */
	public static void loadLibrary() {
		if (!isLibraryLoaded) {
			getLogger().config("loading native liblary...");
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			getLogger().config("done.");
			isLibraryLoaded = true;
		}
	}
	/**
	 * OpenCV のホームディレクトリを設定します
	 * @param abspath
	 */
	public static void setupHomeDirectory(String abspath) {
		File file = new File(abspath);
		if (file.exists() && file.isDirectory()) {
			sOpencvHome = file.toPath();
			getLogger().config("OpenCV HOME: " + file.toString());
			
		} else {
			getLogger().warning("invalid OpenCV HOME directory.");
			
		}
	}

	/**
	 * ライブラリの疎通チェック
	 */
	public void check() {
		if (!isLibraryLoaded) {
			throw new IllegalStateException("Native library is not loaded.");
		}
//		Mat m = Mat.eye(3, 3, CvType.CV_8UC1);
//		println("m = " + m.dump());
		getLogger().finest("java.library.path=" + System.getProperty("java.library.path"));
	}

	/**
	 * 静止画像処理を呼び出し
	 * 
	 * @param imageOperation
	 */
	public void flight(ImageOperation imageOperation) {

		switch (imageOperation.operation) {
		case FACE_DETECT:
			detectFace(imageOperation.sourceFile);
			break;
		default:
			getLogger().warning("Unknown operation: " + imageOperation.operation.toString());
			break;
		}

	}
	@SuppressWarnings("unused")
	private static void println(String msg) {
		getLogger().finest(msg);
	}

	/**
	 * 顔認識
	 * 
	 * @param source ソースのイメージファイル
	 */
	public boolean detectFace(File source) {

		final String settingFileName = OpenCVDictionary.Haarcascade.FRONTALFACE_DEFAULT;

		getLogger().info("Image file: " + source);
		if (source != null && !source.exists() && !source.isFile()) {
			return false;
		}
		// イメージを読み込む
		getLogger().fine("Loading image...");
		Mat image = Imgcodecs.imread(source.getAbsolutePath());
		if (image == null) {
			throw new IllegalArgumentException("Illegal image file.");
		}
		// 顔検出のための設定を読み込む
		getLogger().fine("Loading setting...");
		File setting = resolveOpenCVFile(settingFileName).toFile();
		if (!setting.exists()) {
			throw new RuntimeException(settingFileName + " is not found.");
		}
		getLogger().info("detecting faces...");
		MatOfRect faces = new MatOfRect();
		CascadeClassifier faceDetector = new CascadeClassifier(
				setting.getAbsolutePath());
		faceDetector.detectMultiScale(image, faces);
		getLogger().fine(String.format("Detected %d faces.", faces.toArray().length));
		// 検出した部分をマーク
		for (Rect rect : faces.toArray()) {
			Imgproc.rectangle(
					image,
					new Point(rect.x, rect.y),
					new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 255, 0));
		}
		// 画像ファイルに出力
		String destFile = makeFileName(source, "-face-detected");
		getLogger().info("Write to " + destFile);
		Imgcodecs.imwrite(destFile, image);
		
		image.release();

		return true;
	}
	/**
	 * 新しいファイル名を作成
	 * 
	 * @param source ベースファイルパス
	 * @param suffix サフィックス
	 * @return
	 */
	private String makeFileName(File source, String suffix) {
		String[] parts = source.getName().split("\\.");
		StringBuilder newName = new StringBuilder(source.getParent() + File.separator + parts[0]);
		newName.append(suffix);
		for (int i=1; i<parts.length; i++) {
			newName.append(".");
			newName.append(parts[1]);
		}
		
		return newName.toString();
	}
	/**
	 * OpenCV ホームディレクトリよりファイル名を解決
	 * 
	 * @param filename 相対ファイル名
	 * @return
	 */
	private Path resolveOpenCVFile(String filename) {
		Path abs = sOpencvHome;
		if (sOpencvHome != null) {
			abs = sOpencvHome.resolve(filename);
		} else {
			abs = new File(filename).toPath();
		}
		
		return abs;
	}

}
