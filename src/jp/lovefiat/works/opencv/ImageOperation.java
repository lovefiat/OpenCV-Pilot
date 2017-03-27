package jp.lovefiat.works.opencv;

import java.io.File;

public class ImageOperation {
	
	/**
	 * 静止画操作
	 */
	public enum Operator {
		NONE,
		FACE_DETECT,
	}
	/** 操作方法 */
	Operator operation = Operator.NONE;
	File sourceFile;
	
	public ImageOperation(ImageOperation.Operator operation) {
		this.operation = operation;
	}

}
