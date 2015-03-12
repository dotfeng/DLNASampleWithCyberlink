package net.fengg.app.dlna.model;

public class Container extends Content {
	private String searchable;
	private int childCount;
	
	public String getSearchable() {
		return searchable;
	}
	public void setSearchable(String searchable) {
		this.searchable = searchable;
	}
	public int getChildCount() {
		return childCount;
	}
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}
	
	
}
