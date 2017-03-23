/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2004
*
*	File : FileDirectory
*
*	Revision:
*
*	02/10/04
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.server.directory.file;

import java.io.*;
import java.util.List;

import org.cybergarage.util.*;
import org.cybergarage.xml.*;
import org.cybergarage.upnp.std.av.server.*;
import org.cybergarage.upnp.std.av.server.object.*;
import org.cybergarage.upnp.std.av.server.object.item.file.*;

public class FileDirectory extends Directory
{
	////////////////////////////////////////////////
	// Constructor
	////////////////////////////////////////////////
	
	public FileDirectory(String name, String path)
	{
		super(name);
		setPath(path);
	}
	//edit feng begin
	public FileDirectory(String name, List<String> pathList) {
		super(name);
		setPathList(pathList);
	}
	
	////////////////////////////////////////////////
	// PathList
	////////////////////////////////////////////////
	
	private List<String> pathList;

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}
	//edit feng end
	////////////////////////////////////////////////
	// Path
	////////////////////////////////////////////////

	private String path;
	
	public void setPath(String value)
	{
		path = value;		
	}
	
	public String getPath()
	{
		return path;
	}

	////////////////////////////////////////////////
	// create/updateItemNode
	////////////////////////////////////////////////
	
	private boolean updateItemNode(FileItemNode itemNode, File file)
	{
		Format format = getContentDirectory().getFormat(file);
		if (format == null)
			return false;
		FormatObject formatObj = format.createObject(file);
		
		// File/TimeStamp
		itemNode.setFile(file);
		
		// Title
		String title = formatObj.getTitle();
		if (0 < title.length())
			itemNode.setTitle(title.replaceAll("&", "&amp;"));
			
		// Creator
		String creator = formatObj.getCreator();
		if (0 < creator.length())
			itemNode.setCreator(creator);

		// Media Class
		String mediaClass = format.getMediaClass();
		if (0 < mediaClass.length())
			itemNode.setUPnPClass(mediaClass);

		// Date
		long lastModTime = file.lastModified();
		itemNode.setDate(lastModTime);
		
		// Storatge Used
		try {
			long fileSize = file.length();
			itemNode.setStorageUsed(fileSize);	
		}
		catch (Exception e) {
			Debug.warning(e);
		}
		
		// ProtocolInfo
		String mimeType = format.getMimeType();
		String protocol = ConnectionManager.HTTP_GET + ":*:" + mimeType + ":*";
		String id = itemNode.getID();
		String url = getContentDirectory().getContentExportURL(id);
		AttributeList objAttrList = formatObj.getAttributeList();
		itemNode.setResource(url, protocol, objAttrList);
		
		// Update SystemUpdateID
		getContentDirectory().updateSystemUpdateID();
		
		return true;
	}
	
	private FileItemNode createCompareItemNode(File file)
	{
		Format format = getContentDirectory().getFormat(file);
		if (format == null)
			return null;
		FileItemNode itemNode = new FileItemNode();
		itemNode.setFile(file);
		return itemNode;
	}
	
	////////////////////////////////////////////////
	// FileList
	////////////////////////////////////////////////
	
	private int getDirectoryItemNodeList(File dirFile, FileItemNodeList itemNodeList)
	{
		//edit feng begin
		if(dirFile.isFile()) {
			FileItemNode itemNode = createCompareItemNode(dirFile);
			if (itemNode != null) {
				itemNodeList.add(itemNode);				
			}
		}else {
			//edit feng end
			File childFile[] = dirFile.listFiles();
			int fileCnt = childFile.length;
			for (int n=0; n<fileCnt; n++) {
				File file = childFile[n];
				if (file.isDirectory() == true) {
					getDirectoryItemNodeList(file, itemNodeList);
					continue;
				}
				if (file.isFile() == true) {
					FileItemNode itemNode = createCompareItemNode(file);
					if (itemNode == null)
						continue;						
					itemNodeList.add(itemNode);
				}
			}
		}
		return itemNodeList.size();
	}
	
	private FileItemNodeList getCurrentDirectoryItemNodeList()
	{
		FileItemNodeList itemNodeList = new FileItemNodeList();
		//edit feng begin
		if(null != getPath()) {
			String path = getPath();
			File pathFile = new File(path);
			getDirectoryItemNodeList(pathFile, itemNodeList);			
		}else if(null != getPathList()) {
			List<String> pathList = getPathList();
			for(String path : pathList) {
				File pathFile = new File(path);
				getDirectoryItemNodeList(pathFile, itemNodeList);
			}
		}
		//edit feng end
		return itemNodeList;
	}
	
	////////////////////////////////////////////////
	// updateItemNodeList
	////////////////////////////////////////////////
	
	private FileItemNode getItemNode(File file)
	{
		int nContents = getNContentNodes();
		for (int n=0; n<nContents; n++) {
			ContentNode cnode = getContentNode(n);
			if ((cnode instanceof FileItemNode) == false)
				continue;
			FileItemNode itemNode = (FileItemNode)cnode;
			if (itemNode.equals(file) == true)
				return itemNode;
		}
		return null;
	}
	
	private void addItemNode(FileItemNode itemNode)
	{
		addContentNode(itemNode);
	}
	
	private boolean updateItemNodeList(FileItemNode newItemNode)
	{
		File newItemNodeFile = newItemNode.getFile();
		FileItemNode currItemNode = getItemNode(newItemNodeFile);
		if (currItemNode == null) {
			int newItemID = getContentDirectory().getNextItemID();
			newItemNode.setID(newItemID);
			updateItemNode(newItemNode, newItemNodeFile);
			addItemNode(newItemNode);
			return true;
		}
		
		long currTimeStamp = currItemNode.getFileTimeStamp();
		long newTimeStamp = newItemNode.getFileTimeStamp();
		if (currTimeStamp == newTimeStamp)
			return false;
			
		updateItemNode(currItemNode, newItemNodeFile);
		
		return true;
	}
	
	private boolean updateItemNodeList()
	{
		boolean updateFlag = false;
		
		// Checking Deleted Items
		int nContents = getNContentNodes();
		ContentNode cnode[] = new ContentNode[nContents];
		for (int n=0; n<nContents; n++)
			cnode[n] = getContentNode(n);
		for (int n=0; n<nContents; n++) {
			if ((cnode[n] instanceof FileItemNode) == false)
				continue;
			FileItemNode itemNode = (FileItemNode)cnode[n];
			File itemFile = itemNode.getFile();
			if (itemFile == null)
				continue;
			if (itemFile.exists() == false) {
				removeContentNode(cnode[n]);
				updateFlag = true;
			}
		}
		
		// Checking Added or Updated Items
		FileItemNodeList itemNodeList = getCurrentDirectoryItemNodeList();
		int itemNodeCnt = itemNodeList.size();
		for (int n=0; n<itemNodeCnt; n++) {
			FileItemNode itemNode = itemNodeList.getFileItemNode(n);
			if (updateItemNodeList(itemNode) == true)
				updateFlag = true;
		}
		
		return updateFlag;
	}

	////////////////////////////////////////////////
	// update
	////////////////////////////////////////////////
	
	public boolean update()
	{
		return updateItemNodeList();
	}
}

