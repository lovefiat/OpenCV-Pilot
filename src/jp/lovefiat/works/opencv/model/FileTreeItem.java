package jp.lovefiat.works.opencv.model;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileTreeItem {
	private File file;
	private StringProperty name;
	private String caption;
	
	public FileTreeItem(String path) {
		this(new File(path));
	}
	public FileTreeItem(File file) {
		this.file = file;
		
		this.name = new SimpleStringProperty();
		
		this.name.set(this.file.getName());
	}
	
	@Override
	public String toString() {
		if (this.caption != null) {
			return this.caption;
			
		} else {
			return getName();
			
		}
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public final StringProperty nameProperty() {
		return this.name;
	}
	
	public final String getName() {
		return this.nameProperty().get();
	}
	
	public final void setName(final String name) {
		this.nameProperty().set(name);
	}
	public File getFile() {
		return file;
	}
	

}
