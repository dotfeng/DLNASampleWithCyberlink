package net.fengg.app.dlna.model;

public class Item extends Content{
	private String storageMedium;
	private String date;
	private String storageUsed;
	private String res;
	private String protocolInfo;
	private String creator;
	private String artist;
	private String album;
	private String duration;
	
	public String getStorageMedium() {
		return storageMedium;
	}
	public void setStorageMedium(String storageMedium) {
		this.storageMedium = storageMedium;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStorageUsed() {
		return storageUsed;
	}
	public void setStorageUsed(String storageUsed) {
		this.storageUsed = storageUsed;
	}
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public String getProtocolInfo() {
		return protocolInfo;
	}
	public void setProtocolInfo(String protocolInfo) {
		this.protocolInfo = protocolInfo;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
}
