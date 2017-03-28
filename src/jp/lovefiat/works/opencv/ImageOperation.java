package jp.lovefiat.works.opencv;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageOperation {
	
	/**
	 * 静止画操作
	 */
	public enum Operator {
		NONE,			/** NONE */
		FACE_DETECT,	/** 顔検出（既定） */
		EDGE_DETECT,	/** エッジ検出（既定） */
	}
	/** 操作方法 */
	Operator operation = Operator.NONE;
	/** ソースファイル */
	File sourceFile;
	
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
	public Mat readImageSource() {
		return Imgcodecs.imread(sourceFile.getAbsolutePath());
	}

}
