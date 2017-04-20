package jp.lovefiat.works.opencv;

import java.io.File;

import jp.lovefiat.works.opencv.ImageOperation.Operator;
import jp.lovefiat.works.opencv.gui.MainFrame;

public class Main {
	
	private static final String OPENCV3_HOME = "/usr/local/Cellar/opencv3/3.2.0/share/OpenCV";
	private static final String MODE_FACE = "-face";
	private static final String MODE_EDGE = "-edge";
	private static final String MODE_HOUGH_P = "-hough";

	/**
	 * エントリポイント
	 * 
	 * 使用方法: Main <イメージファイルパス>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Start.");
		
		// OpenCV ライブラリの利用準備
		Pilot.setupHomeDirectory(OPENCV3_HOME);
		Pilot.loadLibrary();

		Pilot pilot = new Pilot();
		// OpenCV チェック
		pilot.check();
		
		if (args.length < 2) {
			MainFrame frame = new MainFrame("OpenCV Pilot");
			frame.setPilot(pilot);
			frame.start();
			return;
		}
		
		ImageOperation operation = new ImageOperation(getOperator(args[0]));
		operation.sourceFile = new File(args[1]);
		pilot.flight(operation);
		
//		MainFrame frame = new MainFrame("OpenCV Pilot");
//		frame.setPilot(pilot);
//		frame.setImageSourceDir(operation.sourceFile.getParentFile().toPath());
//		frame.start();
		
		System.out.println("Finish.");
	}
	
	private static ImageOperation.Operator getOperator(String mode) {
		ImageOperation.Operator ope = Operator.NONE;
		switch (mode.toLowerCase()) {
		case MODE_EDGE:
			ope = Operator.EDGE_DETECT;
			break;
		case MODE_FACE:
			ope = Operator.FACE_DETECT;
			break;
		case MODE_HOUGH_P:
			ope = Operator.HOUGHT_LINES_P;
			break;
		}
		return ope;
	}
}

