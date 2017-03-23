package net.fengg.app.dlna.model;

import java.io.Serializable;

public class Image implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title;
	private String type;
	private String name;
	private int size;
	private String directory;
	private int id;
	private String dateAdded;
	private boolean isContaier;
	
	public int getId() {
		return id;
	}
	public String getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public boolean isContaier() {
		return isContaier;
	}
	public void setContaier(boolean isContaier) {
		this.isContaier = isContaier;
	}
}
