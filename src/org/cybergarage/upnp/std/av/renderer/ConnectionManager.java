/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003
*
*	File : ConnectionManager.java
*
*	Revision:
*
*	02/22/08
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.renderer;

import org.cybergarage.util.*;
import org.cybergarage.upnp.*;
import org.cybergarage.upnp.control.*;

public class ConnectionManager implements ActionListener, QueryListener
{
	////////////////////////////////////////////////
	// Constants
	////////////////////////////////////////////////

	public final static String SERVICE_TYPE = "urn:schemas-upnp-org:service:ConnectionManager:1";		
	
	// Browse Action	
	
	public final static String HTTP_GET = "http-get";

	public final static String SOURCEPROTOCOLINFO = "SourceProtocolInfo";
	public final static String SINKPROTOCOLINFO = "SinkProtocolInfo";
	public final static String CURRENTCONNECTIONIDS = "CurrentConnectionIDs";
	public final static String GETPROTOCOLINFO = "GetProtocolInfo";
	public final static String SOURCE = "Source";
	public final static String SINK = "Sink";
	public final static String PREPAREFORCONNECTION = "PrepareForConnection";
	public final static String REMOTEPROTOCOLINFO = "RemoteProtocolInfo";
	public final static String PEERCONNECTIONMANAGER = "PeerConnectionManager";
	public final static String PEERCONNECTIONID = "PeerConnectionID";
	public final static String DIRECTION = "Direction";
	public final static String CONNECTIONID = "ConnectionID";
	public final static String AVTRANSPORTID = "AVTransportID";
	public final static String RCSID = "RcsID";
	public final static String CONNECTIONCOMPLETE = "ConnectionComplete";
	public final static String GETCURRENTCONNECTIONIDS = "GetCurrentConnectionIDs";
	public final static String CONNECTIONIDS = "ConnectionIDs";
	public final static String GETCURRENTCONNECTIONINFO = "GetCurrentConnectionInfo";
	public final static String PROTOCOLINFO = "ProtocolInfo";
	public final static String STATUS = "Status";
	
	public final static String OK = "OK";
	public final static String CONTENTFORMATMISMATCH = "ContentFormatMismatch";
	public final static String INSUFFICIENTBANDWIDTH = "InsufficientBandwidth";
	public final static String UNRELIABLECHANNEL = "UnreliableChannel";
	public final static String UNKNOWN = "Unknown";
	public final static String INPUT = "Input";
	public final static String OUTPUT = "Output";
	
	public final static String SCPD = 
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
		"<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">\n" +
		"   <specVersion>\n" +
		"      <major>1</major>\n" +
		"      <minor>0</minor>\n" +
		"	</specVersion>\n" +
		"	<actionList>\n" +
		"		<action>\n" +
		"         <name>GetCurrentConnectionInfo</name>\n" +
		"         <argumentList>\n" +
		"            <argument>\n" +
		"               <name>ConnectionID</name>\n" +
		"               <direction>in</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_ConnectionID</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>RcsID</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_RcsID</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>AVTransportID</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_AVTransportID</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>ProtocolInfo</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_ProtocolInfo</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>PeerConnectionManager</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_ConnectionManager</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>PeerConnectionID</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_ConnectionID</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>Direction</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_Direction</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>Status</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>A_ARG_TYPE_ConnectionStatus</relatedStateVariable>\n" +
		"            </argument>\n" +
		"         </argumentList>\n" +
		"      </action>\n" +
		"      <action>\n" +
		"         <name>GetProtocolInfo</name>\n" +
		"         <argumentList>\n" +
		"            <argument>\n" +
		"               <name>Source</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>SourceProtocolInfo</relatedStateVariable>\n" +
		"            </argument>\n" +
		"            <argument>\n" +
		"               <name>Sink</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>SinkProtocolInfo</relatedStateVariable>\n" +
		"            </argument>\n" +
		"         </argumentList>\n" +
		"      </action>\n" +
		"      <action>\n" +
		"         <name>GetCurrentConnectionIDs</name>\n" +
		"         <argumentList>\n" +
		"            <argument>\n" +
		"               <name>ConnectionIDs</name>\n" +
		"               <direction>out</direction>\n" +
		"               <relatedStateVariable>CurrentConnectionIDs</relatedStateVariable>\n" +
		"            </argument>\n" +
		"         </argumentList>\n" +
		"      </action>\n" +
		"   </actionList>\n" +
		"   <serviceStateTable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_ProtocolInfo</name>\n" +
		"         <dataType>string</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_ConnectionStatus</name>\n" +
		"         <dataType>string</dataType>\n" +
		"         <allowedValueList>\n" +
		"            <allowedValue>OK</allowedValue>\n" +
		"            <allowedValue>ContentFormatMismatch</allowedValue>\n" +
		"            <allowedValue>InsufficientBandwidth</allowedValue>\n" +
		"            <allowedValue>UnreliableChannel</allowedValue>\n" +
		"            <allowedValue>Unknown</allowedValue>\n" +
		"         </allowedValueList>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_AVTransportID</name>\n" +
		"         <dataType>i4</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_RcsID</name>\n" +
		"         <dataType>i4</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_ConnectionID</name>\n" +
		"         <dataType>i4</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_ConnectionManager</name>\n" +
		"         <dataType>string</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"yes\">\n" +
		"         <name>SourceProtocolInfo</name>\n" +
		"         <dataType>string</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"yes\">\n" +
		"         <name>SinkProtocolInfo</name>\n" +
		"         <dataType>string</dataType>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"no\">\n" +
		"         <name>A_ARG_TYPE_Direction</name>\n" +
		"         <dataType>string</dataType>\n" +
		"         <allowedValueList>\n" +
		"            <allowedValue>Input</allowedValue>\n" +
		"            <allowedValue>Output</allowedValue>\n" +
		"         </allowedValueList>\n" +
		"      </stateVariable>\n" +
		"      <stateVariable sendEvents=\"yes\">\n" +
		"         <name>CurrentConnectionIDs</name>\n" +
		"         <dataType>string</dataType>\n" +
		"      </stateVariable>\n" +
		"   </serviceStateTable>\n" +
		"</scpd>";	

	////////////////////////////////////////////////
	// Constructor 
	////////////////////////////////////////////////
	
	public ConnectionManager(MediaRenderer render)
	{
		maxConnectionID = 0;
		setMediaRenderer(render);
	}
	
	////////////////////////////////////////////////
	// MediaRender
	////////////////////////////////////////////////

	private MediaRenderer mediaRenderer;
	
	private void setMediaRenderer(MediaRenderer render)
	{
		mediaRenderer = render;	
	}
	
	public MediaRenderer getMediaRenderer()
	{
		return mediaRenderer;	
	}
	
	////////////////////////////////////////////////
	// Mutex
	////////////////////////////////////////////////
	
	private Mutex mutex = new Mutex();
	
	public void lock()
	{
		mutex.lock();
	}
	
	public void unlock()
	{
		mutex.unlock();
	}
	
	////////////////////////////////////////////////
	// ConnectionID
	////////////////////////////////////////////////
	
	private int maxConnectionID;
	
	public int getNextConnectionID()
	{
		lock();
		maxConnectionID++;
		unlock();
		return maxConnectionID;
	}
	
	////////////////////////////////////////////////
	// ConnectionInfoList
	////////////////////////////////////////////////
	
	// Thanks for Brian Owens (12/02/04)
	private ConnectionInfoList conInfoList = new ConnectionInfoList();;
	
	public ConnectionInfoList getConnectionInfoList()
	{
		return conInfoList;
	}
	
	public ConnectionInfo getConnectionInfo(int id)
	{
		int size = conInfoList.size();
		for (int n=0; n<size; n++) {
			ConnectionInfo info = conInfoList.getConnectionInfo(n);
			if (info.getID() == id)
				return info;
		}
		return null;
	}
	
	public void addConnectionInfo(ConnectionInfo info)
	{
		lock();
		conInfoList.add(info);
		unlock();
	}
	
	public void removeConnectionInfo(int id)
	{
		lock();
		int size = conInfoList.size();
		for (int n=0; n<size; n++) {
			ConnectionInfo info = conInfoList.getConnectionInfo(n);
			if (info.getID() == id) {
				conInfoList.remove(info);
				break;
			}
		}
		unlock();
	}
	
	public void removeConnectionInfo(ConnectionInfo info)
	{
		lock();
		conInfoList.remove(info);
		unlock();
	}
	
	////////////////////////////////////////////////
	// ActionListener
	////////////////////////////////////////////////

	public boolean actionControlReceived(Action action)
	{
		boolean isActionSuccess;
		
		String actionName = action.getName();

		if (actionName == null)
			return false;
		
		isActionSuccess = false;
		
		if (actionName.equals(GETPROTOCOLINFO) == true) {
			action.getArgument(SOURCE).setValue("");
			// Sink
			action.getArgument(SINK).setValue("http-get:*:video/x-ms-wmv:DLNA.ORG_PN=WMVMED_PRO," +
			"http-get:*:video/x-ms-asf:DLNA.ORG_PN=MPEG4_P2_ASF_SP_G726,http-get:*" +
			":video/x-ms-wmv:DLNA.ORG_PN=WMVMED_FULL,http-get:*:image/jpeg:DLNA.ORG_PN=" +
			"JPEG_MED,http-get:*:video/x-ms-wmv:DLNA.ORG_PN=WMVMED_BASE,http-get:*:audio" +
			"/L16;rate=44100;channels=1:DLNA.ORG_PN=LPCM,http-get:*:video/mpeg:DLNA.ORG_P" +
			"N=MPEG_PS_PAL,http-get:*:video/mpeg:DLNA.ORG_PN=MPEG_PS_NTSC,http-get:*:video/x" +
			"-ms-wmv:DLNA.ORG_PN=WMVHIGH_PRO,http-get:*:audio/L16;rate=44100;channels=2:DLNA" +
			".ORG_PN=LPCM,http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_SM,http-get:*:video/x-ms-asf" +
			":DLNA.ORG_PN=VC1_ASF_AP_L1_WMA,http-get:*:audio/x-ms-wma:DLNA.ORG_PN=WMDRM_WMABAS" +
			"E,http-get:*:video/x-ms-wmv:DLNA.ORG_PN=WMVHIGH_FULL,http-get:*:audio/x-ms-wma:DL" +
			"NA.ORG_PN=WMAFULL,http-get:*:audio/x-ms-wma:DLNA.ORG_PN=WMABASE,http-get:*:video/x-m" +
			"s-wmv:DLNA.ORG_PN=WMVSPLL_BASE,http-get:*:video/mpeg:DLNA.ORG_PN=MPEG_PS_NTSC_XAC3," +
			"http-get:*:video/x-ms-wmv:DLNA.ORG_PN=WMDRM_WMVSPLL_BASE,http-get:*:video/x-ms-wm" +
			"v:DLNA.ORG_PN=WMVSPML_BASE,http-get:*:video/x-ms-asf:DLNA.ORG_PN=MPEG4_P2_ASF_AS" +
			"P_L5_SO_G726,http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_LRG,http-get:*:audio/mpeg:DLNA" +
			".ORG_PN=MP3,http-get:*:video/mpeg:DLNA.ORG_PN=MPEG_PS_PAL_XAC3,http-get:*:audio/x-ms-wma:DLNA.ORG_PN=WMA" +
			"PRO,http-get:*:video/mpeg:DLNA.ORG_PN=MPEG1,http-get:*:image/jpeg:DLNA.ORG_PN=JPEG" +
			"_TN,http-get:*:video/x-ms-asf:DLNA.ORG_PN=MPEG4_P2_ASF_ASP_L4_SO_G726,http-get:*:audio/L16;r" +
			"ate=48000;channels=2:DLNA.ORG_PN=LPCM,http-get:*:audio/mpeg:DLNA.ORG_PN=MP3X,http-get:*:vide" +
			"o/x-ms-wmv:DLNA.ORG_PN=WMVSPML_MP3,http-get:*:video/x-ms-wmv:*");
			
			//System.out.println("+++++++++");
			isActionSuccess = true;
			return isActionSuccess;
		}

		if (actionName.equals(PREPAREFORCONNECTION) == true) {
			action.getArgument(CONNECTIONID).setValue(-1);
			action.getArgument(AVTRANSPORTID).setValue(-1);
			action.getArgument(RCSID).setValue(-1);
			isActionSuccess = true;
			return isActionSuccess;
		}
		
		if (actionName.equals(CONNECTIONCOMPLETE) == true) {
			isActionSuccess = true;
			return isActionSuccess;
		}
		
		if (actionName.equals(GETCURRENTCONNECTIONINFO) == true){
			isActionSuccess = getCurrentConnectionInfo(action);
			return isActionSuccess;
		}
		
		if (actionName.equals(GETCURRENTCONNECTIONIDS) == true){
			isActionSuccess = getCurrentConnectionIDs(action);
			return isActionSuccess;
		}

//		MediaRenderer dmr = getMediaRenderer();
//		if (dmr != null) {
//			ActionListener listener = dmr.getActionListener();
//			if (listener != null)
//				listener.actionControlReceived(action);
//		}
		
		return isActionSuccess;
	}

	////////////////////////////////////////////////
	// GetCurrentConnectionIDs
	////////////////////////////////////////////////
	
	private boolean getCurrentConnectionIDs(Action action)
	{
		String conIDs = "";
		lock();
		int size = conInfoList.size();
		for (int n=0; n<size; n++) {
			ConnectionInfo info = conInfoList.getConnectionInfo(n);
			if (0 < n)
				conIDs += ",";
			conIDs += Integer.toString(info.getID());
		}
		action.getArgument(CONNECTIONIDS).setValue(conIDs);
		unlock();
		return true;
	}
	
	////////////////////////////////////////////////
	// GetCurrentConnectionInfo
	////////////////////////////////////////////////
	
	private boolean getCurrentConnectionInfo(Action action)
	{
		int id = action.getArgument(RCSID).getIntegerValue();
		lock();
		ConnectionInfo info = getConnectionInfo(id);
		if (info != null) { 
			action.getArgument(RCSID).setValue(info.getRcsID());
			action.getArgument(AVTRANSPORTID).setValue(info.getAVTransportID());
			action.getArgument(PEERCONNECTIONMANAGER).setValue(info.getPeerConnectionManager());
			action.getArgument(PEERCONNECTIONID).setValue(info.getPeerConnectionID());
			action.getArgument(DIRECTION).setValue(info.getDirection());
			action.getArgument(STATUS).setValue(info.getStatus());
		}
		else {
			action.getArgument(RCSID).setValue(-1);
			action.getArgument(AVTRANSPORTID).setValue(-1);
			action.getArgument(PEERCONNECTIONMANAGER).setValue("");
			action.getArgument(PEERCONNECTIONID).setValue(-1);
			action.getArgument(DIRECTION).setValue(ConnectionInfo.OUTPUT);
			action.getArgument(STATUS).setValue(ConnectionInfo.UNKNOWN);
		}
		unlock();
		return true;
	}
	
	////////////////////////////////////////////////
	// QueryListener
	////////////////////////////////////////////////

	public boolean queryControlReceived(StateVariable stateVar)
	{
		return false;
	}
}

