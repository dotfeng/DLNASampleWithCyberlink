/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003
*
*	File : ContentNode
*
*	Revision:
*
*	10/22/03
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.server.object.container;

import org.cybergarage.xml.*;
import org.cybergarage.upnp.std.av.server.object.*;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;

public class ContainerNode extends ContentNode
{
	////////////////////////////////////////////////
	// Constants
	////////////////////////////////////////////////

	public final static String NAME = "container";
	
	public final static String CHILD_COUNT = "childCount";
	public final static String SEARCHABLE = "searchable";

	public final static String OBJECT_CONTAINER = "object.container";

	////////////////////////////////////////////////
	// Constroctor
	////////////////////////////////////////////////
	
	public ContainerNode()
	{
		setID(-1);
		setName(NAME);
		setSearchable(0);
		setChildCount(0);
		setUPnPClass(OBJECT_CONTAINER);
		setWriteStatus(UNKNOWN);
	}

	////////////////////////////////////////////////
	//	isContainerNode
	////////////////////////////////////////////////
	
	public final static boolean isContainerNode(Node node)
	{
		String name = node.getName();
		if (name == null)
			return false;
		return name.equals(NAME);
	}
	
	////////////////////////////////////////////////
	// set
	////////////////////////////////////////////////

	public boolean set(Node node)
	{
		// Child Node -> Property;
		int nNode = node.getNNodes();
		for (int n=0; n<nNode; n++) {
			Node cnode = node.getNode(n);
			if (ContainerNode.isContainerNode(cnode) == true)
				continue;
			if (ItemNode.isItemNode(cnode) == true)
				continue;
			setProperty(cnode.getName(), cnode.getValue());
		}

		// Attribute -> Attribute;
		int nAttr = node.getNAttributes();
		for (int n=0; n<nAttr; n++) {
			Attribute attr = node.getAttribute(n);
			setAttribute(attr.getName(), attr.getValue());
		}
		
		return true;
	}
	
	////////////////////////////////////////////////
	//	Child node
	////////////////////////////////////////////////

	public void addContentNode(ContentNode node) 
	{
		addNode(node);
		node.setParentID(getID());
		setChildCount(getNContentNodes());
		node.setContentDirectory(getContentDirectory());
	}

	public boolean removeContentNode(ContentNode node) 
	{
		boolean ret = removeNode(node);
		setChildCount(getNContentNodes());
		return ret;
	}

	////////////////////////////////////////////////
	// chileCount
	////////////////////////////////////////////////
	
	public void setChildCount(int id)
	{
		setAttribute(CHILD_COUNT, id);
	}
	
	public int getChildCount()
	{
		return getAttributeIntegerValue(CHILD_COUNT);
	}

	////////////////////////////////////////////////
	// searchable
	////////////////////////////////////////////////
	
	public void setSearchable(int value)
	{
		setAttribute(SEARCHABLE, value);
	}
	
	public int getSearchable()
	{
		return getAttributeIntegerValue(SEARCHABLE);
	}
	
}

