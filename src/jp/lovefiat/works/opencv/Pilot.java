package jp.lovefiat.works.opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Pilot {
	
	public void flight() {
		
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        println("m = " + m.dump());
        println(System.getProperty("java.library.path"));

	}
	
	private static void println(String msg) {
		System.out.println(msg);
	}


}
