package jp.lovefiat.works.opencv;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageOperation {
	
	public int mThresHold;
	public double mThresHold1;
	public double mThresHold2;
	public double mRho;
	public double mMaxLineGap;
	public double mMinLineLength;
	private Mat sourceImage;
	
	/**
	 * 静止画操作
	 */
	public enum Operator {
		NONE,			/** NONE */
		FACE_DETECT,	/** 顔検出（既定） */
		EDGE_DETECT,	/** エッジ検出（既定） */
		SIMPLE_BLOB_DETECT,	/** Simple BLOB 検出器 */
		HOUGHT_LINES_P,	/** 確率的Hough変換 */
	}
	/** 操作方法 */
	Operator operation = Operator.NONE;
	/** ソースファイル */
	public File sourceFile;
	
	/**
	 * コンストラクタ
	 * @param operation
	 */
	public ImageOperation(ImageOperation.Operator operation) {
		this.operation = operation;
	}
	/**
	 * イメージソースファイルを読み込む
	 * @return イメージオブジェクト
	 */
	private Mat readImageSource() {
		return Imgcodecs.imread(sourceFile.getAbsolutePath());
	}
	/**
	 * 
	 * @return
	 */
	public Mat getImageSource() {
		if (this.sourceImage == null) {
			this.sourceImage = readImageSource();
		}
		return this.sourceImage;
	}
	
	public Operator getOperator() {
		return this.operation;
	}

}
