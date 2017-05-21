package jp.lovefiat.works.opencv;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * OpenCV (Open Source Computer Vision Library) の実験クラス
 * 
 * @author take
 * 
 * @see http://docs.opencv.org/java/3.1.0/
 *
 */
public class Pilot {
	/** ロガー */
	private static Logger logger = null;
	/** ネイティブライブラリの読み込みステータス */
	private static boolean isLibraryLoaded = false;
	/** OpenCV ホームディレクトリ */
	private static Path sOpencvHome = null;
	
	private ImageOperation currentOperation;
	
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

		this.currentOperation = imageOperation;
		
		switch (imageOperation.operation) {
		case FACE_DETECT:
			detectFaceByHaarLike();
			break;
		case EDGE_DETECT:
			detectEdgeByCanny();
			break;
		case HOUGHT_LINES_P:
			detectLinesByHoughLinesP();
			break;
		case SIMPLE_BLOB_DETECT:
			detectSimpleBlob();
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
	 * Haar-like 特徴分類器による顔認識
	 */
	public boolean detectFaceByHaarLike() {

		final String settingFileName = OpenCVDictionary.Haarcascade.FRONTALFACE_DEFAULT;

		File source = this.currentOperation.sourceFile;
		getLogger().info("Image file: " + source);
		if (source != null && !source.exists() && !source.isFile()) {
			return false;
		}
		// イメージを読み込む
		getLogger().fine("Loading image...");
		Mat image = this.currentOperation.getImageSource();
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
		// 顔検出器
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
	 * Cannyアルゴリズムによるエッジ検出
	 */
	private boolean detectEdgeByCanny() {
		File source = this.currentOperation.sourceFile;
		getLogger().info("Image file: " + source);
		if (source != null && !source.exists() && !source.isFile()) {
			return false;
		}
		// イメージを読み込む
		getLogger().fine("Loading image...");
		Mat image = this.currentOperation.getImageSource();
		if (image == null) {
			throw new IllegalArgumentException("Illegal image file.");
		}
		// エッジ検出
		getLogger().info("detecting edge...");
		Mat gray = new Mat();
		double threshold1 = this.currentOperation.mThresHold1;	// 閾値１
		double threshold2 = this.currentOperation.mThresHold2;	// 閾値２
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);	// オリジナル画像をグレースケール化
		Imgproc.Canny(gray, gray, threshold1, threshold2); // Cannyアルゴ実行
		// 画像ファイルに出力
		String destFile = makeFileName(source, "-edge-detected");
		getLogger().info("Write to " + destFile);
		Imgcodecs.imwrite(destFile, gray);
		// 終了
		image.release();
		gray.release();

		return true;
	}
	/**
	 * 確率的ハフ変換による直線検出
	 * @return
	 */
	private boolean detectLinesByHoughLinesP() {
		File source = this.currentOperation.sourceFile;
		getLogger().info("Image file: " + source);
		if (source != null && !source.exists() && !source.isFile()) {
			return false;
		}
		// イメージを読み込む
		getLogger().fine("Loading image...");
		Mat image = this.currentOperation.getImageSource();
		if (image == null) {
			throw new IllegalArgumentException("Illegal image file.");
		}
		// エッジ検出
		getLogger().info("detecting edge...");
		Mat gray = new Mat();
		double threshold1 = this.currentOperation.mThresHold1;	// 閾値１
		double threshold2 = this.currentOperation.mThresHold2;	// 閾値２
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);	// オリジナル画像をグレースケール化
		Imgproc.Canny(gray, gray, threshold1, threshold2); // Cannyアルゴ実行

		String destFile = makeFileName(source, "-canny");
		getLogger().info("Write to " + destFile);
		Imgcodecs.imwrite(destFile, gray);

		// 確率的ハフ変換
		Mat lines = new Mat();
		Imgproc.HoughLinesP(gray,
							lines,
							this.currentOperation.mRho,
							Math.PI/180,
							this.currentOperation.mThresHold,
							this.currentOperation.mMinLineLength,
							this.currentOperation.mMaxLineGap);

		double[] matrix = new double[4];
		Point p1;
		Point p2;
		for (int j=0; j<lines.rows(); j++) {
			for (int i=0; i<lines.cols(); i++) {
				matrix = lines.get(j, i);
				if (matrix == null) {
					continue;
				}
				p1 = new Point(matrix[0], matrix[1]);
				p2 = new Point(matrix[2], matrix[3]);
				Imgproc.line(image, p1, p2, new Scalar(0, 256, 0), 5);
			}
		}
		destFile = makeFileName(source, "-edge-detected");
		getLogger().info("Write to " + destFile);
		Imgcodecs.imwrite(destFile, image);
		
		// 終了
		gray.release();
		lines.release();
		image.release();

		return true;
	}
	
	/**
	 * SimpleBlob 検出器による特徴検出
	 * @return
	 */
	private boolean detectSimpleBlob() {
		File source = this.currentOperation.sourceFile;
		getLogger().info("Image file: " + source);
		if (source != null && !source.exists() && !source.isFile()) {
			return false;
		}
		// イメージを読み込む
		getLogger().fine("Loading image...");
		Mat image = this.currentOperation.getImageSource();
		if (image == null) {
			throw new IllegalArgumentException("Illegal image file.");
		}
		getLogger().info("detecting edge...");
		Mat gray = new Mat();
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);	// オリジナル画像をグレースケール化

		MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
		detector.detect(gray, matOfKeyPoint);
		
		List<KeyPoint> keyPoints = matOfKeyPoint.toList();
		for (KeyPoint kp : keyPoints) {
			Imgproc.circle(image, kp.pt, (int)kp.size, new Scalar(0, 200, 0));
		}
		
		String destFile = makeFileName(source, "-simple-blob");
		getLogger().info("Write to " + destFile);
		Imgcodecs.imwrite(destFile, image);
		
		image.release();
		gray.release();

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
