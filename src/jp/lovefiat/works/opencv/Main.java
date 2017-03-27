package jp.lovefiat.works.opencv;

import java.io.File;

public class Main {
	private static String OPENCV3_HOME = "/usr/local/Cellar/opencv3/3.2.0/share/OpenCV";

	public static void main(String[] args) {
		
		System.out.println("Start.");
		
		// OpenCV ライブラリの利用準備
		Pilot.setupHomeDirectory(OPENCV3_HOME);
		Pilot.loadLibrary();

		Pilot pilot = new Pilot();
		// OpenCV チェック
		pilot.check();
		
		// 静止画 - 顔認識
		ImageOperation operation = new ImageOperation(ImageOperation.Operator.FACE_DETECT);
		if (args.length > 0) {
			// 最初の引数をファイル名として取り扱う
			operation.sourceFile = new File(args[0]);			
		}
		pilot.flight(operation);
		
		System.out.println("Finish.");
	}

}

