package net.fengg.app.dlna.model;

public class Content {
	private String id;
	private String parentID;
	private String restricted;
	private String upnpClass;
	private String writeStatus;
	private String title;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getRestricted() {
		return restricted;
	}
	public void setRestricted(String restricted) {
		this.restricted = restricted;
	}
	public String getUpnpClass() {
		return upnpClass;
	}
	public void setUpnpClass(String upnpClass) {
		this.upnpClass = upnpClass;
	}
	public String getWriteStatus() {
		return writeStatus;
	}
	public void setWriteStatus(String writeStatus) {
		this.writeStatus = writeStatus;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
