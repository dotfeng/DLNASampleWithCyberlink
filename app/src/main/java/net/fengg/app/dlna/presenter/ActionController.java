package net.fengg.app.dlna.presenter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import org.cybergarage.upnp.std.av.renderer.RenderingControl;
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
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		
		Service localService = device.getService(AVTransport.SERVICE_TYPE);
		if (localService == null)
			return false;

		Action localAction = localService.getAction(AVTransport.SEEK);
		if (localAction == null)
			return false;
		localAction.setArgumentValue(AVTransport.INSTANCEID, "0");
		// if (mUseRelTime) {
		// } else {
		// localAction.setArgumentValue("Unit", "ABS_TIME");
		// }
		// LogUtil.e(tag, "继续相对时间："+mUseRelTime);
		// 测试解决播放暂停后时间不准确
		localAction.setArgumentValue(AVTransport.UNIT, AVTransport.ABSTIME);
		localAction.setArgumentValue(AVTransport.TARGET, pausePosition);
		localAction.postControlAction();

		Action playAction = localService.getAction(AVTransport.PLAY);
		if (playAction == null) {
			return false;
		}

		playAction.setArgumentValue(AVTransport.INSTANCEID, 0);
		playAction.setArgumentValue(AVTransport.SPEED, "1");
		return playAction.postControlAction();
	}

	public String getTransportState(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return null;
		}
		
		Service service = device.getService(AVTransport.SERVICE_TYPE);
		if (service == null)
			return null;

		Action action = service.getAction(AVTransport.GETTRANSPORTINFO);
		if (action == null) {
			return null;
		}

		action.setArgumentValue(AVTransport.INSTANCEID, "0");

		if (action.postControlAction()) {
			return action.getArgumentValue(AVTransport.CURRENTTRANSPORTSTATE);
		} else {
			return null;
		}
	}

	public String getVolumeDbRange(Device device, String argument) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return null;
		}
		Service service = device.getService(RenderingControl.SERVICE_TYPE);
		if (service == null) {
			return null;
		}
		Action action = service.getAction(RenderingControl.GETVOLUMEDBRANGE);
		if (action == null) {
			return null;
		}
		action.setArgumentValue(RenderingControl.INSTANCEID, "0");
		action.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
		if (!action.postControlAction()) {
			return null;
		} else {
			return action.getArgumentValue(argument);
		}
	}

	public int getMinVolumeValue(Device device) {
		String minValue = getVolumeDbRange(device, RenderingControl.MINVALUE);
		if (TextUtils.isEmpty(minValue)) {
			return 0;
		}
		return Integer.parseInt(minValue);
	}

	public int getMaxVolumeValue(Device device) {
		String maxValue = getVolumeDbRange(device, RenderingControl.MAXVALUE);
		if (TextUtils.isEmpty(maxValue)) {
			return 100;
		}
		return Integer.parseInt(maxValue);
	}

	public boolean seek(Device device, String targetPosition) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		Service localService = device.getService(AVTransport.SERVICE_TYPE);
		if (localService == null)
			return false;

		Action action = localService.getAction(AVTransport.SEEK);
		if (action == null) {
			return false;
		}
		action.setArgumentValue(AVTransport.INSTANCEID, "0");
		action.setArgumentValue(AVTransport.UNIT, AVTransport.RELTIME);
		action.setArgumentValue(AVTransport.TARGET, targetPosition);
		
		boolean result = action.postControlAction();
		if (!result) {
			action.setArgumentValue(AVTransport.UNIT, AVTransport.ABSTIME);
			action.setArgumentValue(AVTransport.TARGET, targetPosition);
			return action.postControlAction();
		} else {
			return result;
		}

	}

	public String getPositionInfo(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return null;
		}
		Service service = device.getService(AVTransport.SERVICE_TYPE);

		if (service == null)
			return null;

		Action action = service.getAction(AVTransport.GETPOSITIONINFO);
		if (action == null) {
			return null;
		}

		action.setArgumentValue(AVTransport.INSTANCEID, "0");
		if (action.postControlAction()) {
			return action.getArgumentValue(AVTransport.ABSTIME);
		} else {
			return null;
		}
	}

	public String getMediaDuration(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return null;
		}
		
		Service service = device.getService(AVTransport.SERVICE_TYPE);
		if (service == null) {
			return null;
		}

		Action action = service.getAction(AVTransport.GETMEDIAINFO);
		if (action == null) {
			return null;
		}

		action.setArgumentValue(AVTransport.INSTANCEID, "0");
		if (action.postControlAction()) {
			return action.getArgumentValue(AVTransport.MEDIADURATION);
		} else {
			return null;
		}

	}

	public boolean setMute(Device device, String targetValue) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		
		Service service = device.getService(RenderingControl.SERVICE_TYPE);
		if (service == null) {
			return false;
		}
		Action action = service.getAction(RenderingControl.SETMUTE);
		if (action == null) {
			return false;
		}

		action.setArgumentValue(RenderingControl.INSTANCEID, "0");
		action.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
		action.setArgumentValue(RenderingControl.DESIREDMUTE, targetValue);
		return action.postControlAction();
	}

	public String getMute(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return null;
		}
		
		Service service = device.getService(RenderingControl.SERVICE_TYPE);
		if (service == null) {
			return null;
		}

		Action action = service.getAction(RenderingControl.GETMUTE);
		if (action == null) {
			return null;
		}
		action.setArgumentValue(RenderingControl.INSTANCEID, "0");
		action.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
		action.postControlAction();
		return action.getArgumentValue(RenderingControl.CURRENTMUTE);
	}

	public boolean setVoice(Device device, int value) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		
		Service service = device.getService(RenderingControl.SERVICE_TYPE);
		if (service == null) {
			return false;
		}

		Action action = service.getAction(RenderingControl.SETVOLUME);
		if (action == null) {
			return false;
		}

		action.setArgumentValue(RenderingControl.INSTANCEID, "0");
		action.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
		action.setArgumentValue(RenderingControl.DESIREDVOLUME, value);
		return action.postControlAction();

	}

	public int getVoice(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return -1;
		}
		
		Service service = device.getService(RenderingControl.SERVICE_TYPE);
		if (service == null) {
			return -1;
		}

		Action action = service.getAction(RenderingControl.GETVOLUME);
		if (action == null) {
			return -1;
		}
		action.setArgumentValue(RenderingControl.INSTANCEID, "0");
		action.setArgumentValue(RenderingControl.CHANNEL, RenderingControl.MASTER);
		if (action.postControlAction()) {
			return action.getArgumentIntegerValue(RenderingControl.CURRENTVOLUME);
		} else {
			return -1;
		}

	}

	public boolean stop(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		
		Service service = device.getService(AVTransport.SERVICE_TYPE);

		if (service == null) {
			return false;
		}
		Action action = service.getAction(AVTransport.STOP);
		if (action == null) {
			return false;
		}

		action.setArgumentValue(AVTransport.INSTANCEID, 0);
		return action.postControlAction();

	}

	public boolean pause(Device device) {
		if (device == null || !DLNAUtil.isMediaRenderer(device)) {
			return false;
		}
		
		Service service = device.getService(AVTransport.SERVICE_TYPE);
		if (service == null) {
			return false;
		}
		Action action = service.getAction(AVTransport.PAUSE);
		if (action == null) {
			return false;
		}
		action.setArgumentValue(AVTransport.INSTANCEID, 0);
		return action.postControlAction();
	}
	
	public List<ContentNode> browse(Device device, 
			String id, String startingIndex, 
			String requestedCount, String filter, String sortCriteria) {
		if (device == null) {
			return null;
		}
		
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
		argumentList.getArgument(ContentDirectory.STARTINGINDEX).setValue(
				startingIndex);
		argumentList.getArgument(ContentDirectory.REQUESTEDCOUNT).setValue(
				requestedCount);
		argumentList.getArgument(ContentDirectory.FILTER).setValue(filter);
		argumentList.getArgument(ContentDirectory.SORTCRITERIA).setValue(
				sortCriteria);
		try {
			if (action.postControlAction()) {
				ArgumentList outArgList = action.getOutputArgumentList();
				Argument result = outArgList
						.getArgument(ContentDirectory.RESULT);
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
