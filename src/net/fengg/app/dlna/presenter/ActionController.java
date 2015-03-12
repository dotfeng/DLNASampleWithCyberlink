package net.fengg.app.dlna.presenter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.fengg.app.dlna.util.DLNAUtil;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.upnp.std.av.server.ContentDirectory;
import org.cybergarage.upnp.std.av.server.DC;
import org.cybergarage.upnp.std.av.server.UPnP;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.text.TextUtils;

public class ActionController {
	private static ActionController mController = new ActionController();
	
	public static ActionController getInstance() {
		return mController;
	}
	
	private static final String AVTransport1 = "urn:schemas-upnp-org:service:AVTransport:1";
	private static final String RenderingControl = "urn:schemas-upnp-org:service:RenderingControl:1";

	public boolean play(Device device, String path) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		
		Service service = device.getService(AVTransport.SERVICE_TYPE);

		if (service == null) {
			return false;
		}

		Action action = service.getAction(AVTransport.SETAVTRANSPORTURI);
		if (action == null) {
			return false;
		}

		Action playAction = service.getAction(AVTransport.PLAY);
		if (playAction == null) {
			return false;
		}

		if (TextUtils.isEmpty(path)) {
			return false;
		}

		action.setArgumentValue(AVTransport.INSTANCEID, 0);
		action.setArgumentValue(AVTransport.CURRENTURI, path);
		action.setArgumentValue(AVTransport.CURRENTURIMETADATA, DLNAUtil.getMimeType(path));
		if (!action.postControlAction()) {
			return false;
		}

		playAction.setArgumentValue(AVTransport.INSTANCEID, 0);
		playAction.setArgumentValue(AVTransport.SPEED, "1");
		return playAction.postControlAction();
	}

	public boolean goon(Device device, String pausePosition) {

		Service localService = device.getService(AVTransport1);
		if (localService == null)
			return false;

		final Action localAction = localService.getAction("Seek");
		if (localAction == null)
			return false;
		localAction.setArgumentValue("InstanceID", "0");
		// if (mUseRelTime) {
		// } else {
		// localAction.setArgumentValue("Unit", "ABS_TIME");
		// }
		// LogUtil.e(tag, "继续相对时间："+mUseRelTime);
		// 测试解决播放暂停后时间不准确
		localAction.setArgumentValue("Unit", "ABS_TIME");
		localAction.setArgumentValue("Target", pausePosition);
		localAction.postControlAction();

		Action playAction = localService.getAction("Play");
		if (playAction == null) {
			return false;
		}

		playAction.setArgumentValue("InstanceID", 0);
		playAction.setArgumentValue("Speed", "1");
		return playAction.postControlAction();
	}

	public String getTransportState(Device device) {
		Service localService = device.getService(AVTransport1);
		if (localService == null) {
			return null;
		}

		final Action localAction = localService.getAction("GetTransportInfo");
		if (localAction == null) {
			return null;
		}

		localAction.setArgumentValue("InstanceID", "0");

		if (localAction.postControlAction()) {
			return localAction.getArgumentValue("CurrentTransportState");
		} else {
			return null;
		}
	}

	public String getVolumeDbRange(Device device, String argument) {
		Service localService = device.getService(RenderingControl);
		if (localService == null) {
			return null;
		}
		Action localAction = localService.getAction("GetVolumeDBRange");
		if (localAction == null) {
			return null;
		}
		localAction.setArgumentValue("InstanceID", "0");
		localAction.setArgumentValue("Channel", "Master");
		if (!localAction.postControlAction()) {
			return null;
		} else {
			return localAction.getArgumentValue(argument);
		}
	}

	public int getMinVolumeValue(Device device) {
		String minValue = getVolumeDbRange(device, "MinValue");
		if (TextUtils.isEmpty(minValue)) {
			return 0;
		}
		return Integer.parseInt(minValue);
	}

	public int getMaxVolumeValue(Device device) {
		String maxValue = getVolumeDbRange(device, "MaxValue");
		if (TextUtils.isEmpty(maxValue)) {
			return 100;
		}
		return Integer.parseInt(maxValue);
	}

	public boolean seek(Device device, String targetPosition) {
		Service localService = device.getService(AVTransport.SERVICE_TYPE);
		if (localService == null)
			return false;

		Action localAction = localService.getAction(AVTransport.SEEK);
		if (localAction == null) {
			return false;
		}
		localAction.setArgumentValue(AVTransport.INSTANCEID, "0");
		localAction.setArgumentValue(AVTransport.UNIT, AVTransport.RELTIME);
		localAction.setArgumentValue(AVTransport.TARGET, targetPosition);
		
		boolean postControlAction = localAction.postControlAction();
		if (!postControlAction) {
			localAction.setArgumentValue(AVTransport.UNIT, AVTransport.ABSTIME);
			localAction.setArgumentValue(AVTransport.TARGET, targetPosition);
			return localAction.postControlAction();
		} else {
			return postControlAction;
		}

	}

	public String getPositionInfo(Device device) {
		Service localService = device.getService(AVTransport1);

		if (localService == null)
			return null;

		final Action localAction = localService.getAction("GetPositionInfo");
		if (localAction == null) {
			return null;
		}

		localAction.setArgumentValue("InstanceID", "0");
		boolean isSuccess = localAction.postControlAction();
		if (isSuccess) {
			return localAction.getArgumentValue("AbsTime");
		} else {
			return null;
		}
	}

	public String getMediaDuration(Device device) {
		Service localService = device.getService(AVTransport1);
		if (localService == null) {
			return null;
		}

		final Action localAction = localService.getAction("GetMediaInfo");
		if (localAction == null) {
			return null;
		}

		localAction.setArgumentValue("InstanceID", "0");
		if (localAction.postControlAction()) {
			return localAction.getArgumentValue("MediaDuration");
		} else {
			return null;
		}

	}

	public boolean setMute(Device mediaRenderDevice, String targetValue) {
		Service service = mediaRenderDevice.getService(RenderingControl);
		if (service == null) {
			return false;
		}
		final Action action = service.getAction("SetMute");
		if (action == null) {
			return false;
		}

		action.setArgumentValue("InstanceID", "0");
		action.setArgumentValue("Channel", "Master");
		action.setArgumentValue("DesiredMute", targetValue);
		return action.postControlAction();
	}

	public String getMute(Device device) {
		Service service = device.getService(RenderingControl);
		if (service == null) {
			return null;
		}

		final Action getAction = service.getAction("GetMute");
		if (getAction == null) {
			return null;
		}
		getAction.setArgumentValue("InstanceID", "0");
		getAction.setArgumentValue("Channel", "Master");
		getAction.postControlAction();
		return getAction.getArgumentValue("CurrentMute");
	}

	public boolean setVoice(Device device, int value) {
		Service service = device.getService(RenderingControl);
		if (service == null) {
			return false;
		}

		final Action action = service.getAction("SetVolume");
		if (action == null) {
			return false;
		}

		action.setArgumentValue("InstanceID", "0");
		action.setArgumentValue("Channel", "Master");
		action.setArgumentValue("DesiredVolume", value);
		return action.postControlAction();

	}

	public int getVoice(Device device) {
		Service service = device.getService(RenderingControl);
		if (service == null) {
			return -1;
		}

		final Action getAction = service.getAction("GetVolume");
		if (getAction == null) {
			return -1;
		}
		getAction.setArgumentValue("InstanceID", "0");
		getAction.setArgumentValue("Channel", "Master");
		if (getAction.postControlAction()) {
			return getAction.getArgumentIntegerValue("CurrentVolume");
		} else {
			return -1;
		}

	}

	public boolean stop(Device device) {
		Service service = device.getService(AVTransport.SERVICE_TYPE);

		if (service == null) {
			return false;
		}
		final Action stopAction = service.getAction(AVTransport.STOP);
		if (stopAction == null) {
			return false;
		}

		stopAction.setArgumentValue(AVTransport.INSTANCEID, 0);
		return stopAction.postControlAction();

	}

	public boolean pause(Device mediaRenderDevice) {

		Service service = mediaRenderDevice.getService(AVTransport.SERVICE_TYPE);
		if (service == null) {
			return false;
		}
		final Action pauseAction = service.getAction(AVTransport.PAUSE);
		if (pauseAction == null) {
			return false;
		}
		pauseAction.setArgumentValue(AVTransport.INSTANCEID, 0);
		return pauseAction.postControlAction();
	}
	
	public List<ContentNode> browse(Device device, 
			String id, String startingIndex, 
			String requestedCount, String filter, String sortCriteria) {
		Service service = device.getService(ContentDirectory.SERVICE_TYPE);

		if (service == null) {
			return null;
		}

		Action action = service.getAction(ContentDirectory.BROWSE);
		if (action == null) {
			return null;	
		}
			ArgumentList argumentList = action.getArgumentList();
			argumentList.getArgument(ContentDirectory.OBJECTID).setValue(id);
			argumentList.getArgument(ContentDirectory.BROWSEFLAG).setValue(
					ContentDirectory.BROWSEDIRECTCHILDREN);
			argumentList.getArgument(ContentDirectory.STARTINGINDEX)
				.setValue(startingIndex);
			argumentList.getArgument(ContentDirectory.REQUESTEDCOUNT)
				.setValue(requestedCount);
			argumentList.getArgument(ContentDirectory.FILTER).setValue(filter);
			argumentList.getArgument(ContentDirectory.SORTCRITERIA)
				.setValue(sortCriteria);
		try {
			if (action.postControlAction()) {
				ArgumentList outArgList = action.getOutputArgumentList();
				Argument result = outArgList.getArgument(ContentDirectory.RESULT);
				System.out.println("Result:" + result.getValue());
				return parseResult(result);
			} else {
				UPnPStatus err = action.getControlStatus();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static List<ContentNode> parseResult(Argument result) {

		List<ContentNode> list = new ArrayList<ContentNode>();

		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = dfactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(result.getValue()
					.getBytes("UTF-8"));

			Document doc = documentBuilder.parse(is);

			NodeList containers = doc.getElementsByTagName(ContainerNode.NAME);
			for (int j = 0; j < containers.getLength(); ++j) {
				Node container = containers.item(j);
				ContainerNode con = new ContainerNode();
				con.setID(container.getAttributes()
						.getNamedItem(ContentNode.ID).getNodeValue());
				con.setParentID(container.getAttributes()
						.getNamedItem(ContentNode.PARENT_ID).getNodeValue());
				con.setRestricted(Integer.parseInt(container.getAttributes()
						.getNamedItem(ContentNode.RESTRICTED).getNodeValue()));
				con.setChildCount(Integer.parseInt(container.getAttributes()
						.getNamedItem(ContainerNode.CHILD_COUNT).getNodeValue()));
				
				NodeList childNodes = container.getChildNodes();
				for (int l = 0; l < childNodes.getLength(); ++l) {
					Node childNode = childNodes.item(l);
					if (childNode.getNodeName().equals(UPnP.CLASS)) {
						con.setUPnPClass(childNode.getFirstChild().getNodeValue());
					} else if (childNode.getNodeName().equals(UPnP.WRITE_STATUS)) {
						con.setWriteStatus(childNode.getFirstChild().getNodeValue());
					} else if (childNode.getNodeName().equals(DC.TITLE)) {
						con.setTitle(childNode.getFirstChild().getNodeValue());
					}
				}
				list.add(con);
			}

			NodeList items = doc.getElementsByTagName(ItemNode.NAME);
			for (int j = 0; j < items.getLength(); ++j) {
				Node item = items.item(j);
				ItemNode it = new ItemNode();
				it.setID(item.getAttributes()
						.getNamedItem(ContentNode.ID).getNodeValue());
				it.setParentID(item.getAttributes()
						.getNamedItem(ContentNode.PARENT_ID).getNodeValue());
				it.setRestricted(Integer.parseInt(item.getAttributes()
						.getNamedItem(ContentNode.RESTRICTED).getNodeValue()));

				NodeList childNodes = item.getChildNodes();
				for (int l = 0; l < childNodes.getLength(); ++l) {
					Node childNode = childNodes.item(l);

					if (childNode.getNodeName().equals(UPnP.CLASS)) {
						it.setUPnPClass(childNode.getFirstChild().getNodeValue());
					} else if (childNode.getNodeName().equals(UPnP.WRITE_STATUS)) {
						it.setWriteStatus(childNode.getFirstChild().getNodeValue());
					} else if (childNode.getNodeName().equals(DC.TITLE)) {
						it.setTitle(childNode.getFirstChild().getNodeValue());
					} else if (childNode.getNodeName().equals(UPnP.STORAGE_MEDIUM)) {
						if(null != childNode.getFirstChild()) {
							it.setStorageMedium(childNode.getFirstChild().getNodeValue());
						}
					} else if (childNode.getNodeName().equals(DC.DATE)) {
						if(null != childNode.getFirstChild()) {
							it.setDate(childNode.getFirstChild().getNodeValue());
						}
					} else if (childNode.getNodeName().equals(UPnP.STORAGE_USED)) {
						if(null != childNode.getFirstChild()) {
							it.setStorageUsed(Long.parseLong(childNode.getFirstChild().getNodeValue()));
						}
					} else if (childNode.getNodeName().equals(DC.CREATOR)) {
						if(null != childNode.getFirstChild()) {
							it.setCreator(childNode.getFirstChild().getNodeValue());
						}
					} else if (childNode.getNodeName().equals(ItemNode.RES)) {
						String protocolInfo = "";
						if (childNode.getAttributes().getNamedItem(
								ItemNode.PROTOCOL_INFO) != null) {
							protocolInfo = childNode.getAttributes()
									.getNamedItem(ItemNode.PROTOCOL_INFO)
									.getNodeValue();
						}
						if(childNode.getFirstChild()!=null){
							it.setResource(childNode.getFirstChild().getNodeValue(), protocolInfo);
						}
					}

				}
				list.add(it);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
