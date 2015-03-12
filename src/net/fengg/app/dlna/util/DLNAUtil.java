package net.fengg.app.dlna.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.UPnP;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.util.StringUtil;

public class DLNAUtil {

	/**
	 * Check if the device is a media render device
	 * 
	 * @param device
	 * @return
	 */
	public static boolean isMediaRenderer(Device device) {
		if (device != null
				&& MediaRenderer.DEVICE_TYPE.equalsIgnoreCase(device.getDeviceType())) {
			return true;
		}

		return false;
	}
	
	public static boolean isMediaServer(Device device) {
		if (device != null
				&& MediaServer.DEVICE_TYPE.equalsIgnoreCase(device.getDeviceType())) {
			return true;
		}

		return false;
	}
	
	public static String getMimeType(String url){
		String mime = null;
		int dot = url.lastIndexOf( '.' );
		if ( dot >= 0 )
			mime = (String)theMimeTypes.get( url.substring( dot + 1 ).toLowerCase());
		if ( mime == null )
			mime = "application/octet-stream";
		return mime;
	}
		
	public static Hashtable<String, String> theMimeTypes = new Hashtable<String, String>();
	static {
		StringTokenizer st = new StringTokenizer("css		text/css "
				+ "htm		text/html " + "html		text/html " + "xml		text/xml "
				+ "txt		text/plain " + "asc		text/plain " + "gif		image/gif "
				+ "jpg		image/jpeg " + "jpeg		image/jpeg " + "png		image/png "
				+ "mp3		audio/mpeg " + "m3u		audio/mpeg-url "
				+ "mp4		video/mp4 " + "ogv		video/ogg " + "flv		video/x-flv "
				+ "mov		video/quicktime "
				+ "swf		application/x-shockwave-flash "
				+ "js			application/javascript " + "pdf		application/pdf "
				+ "doc		application/msword " + "ogg		application/x-ogg "
				+ "zip		application/octet-stream "
				+ "exe		application/octet-stream "
				+ "class		application/octet-stream ");
		while (st.hasMoreTokens())
			theMimeTypes.put(st.nextToken(), st.nextToken());
	}
	
	public static String getUrl(String dir){
		String url = null;
		InetAddress inet=null;
		if(dir.toLowerCase().startsWith("http://") || dir.toLowerCase().startsWith("https://"))
			return dir;
		
		try {    
			inet = InetAddress.getLocalHost();
		      
		} catch (Exception e) {    
		    e.printStackTrace();    
		}  
		
		boolean get = false;
		String ipAdd = null;
		Enumeration<NetworkInterface> netInterfaces = null;    
		try {    
		    netInterfaces = NetworkInterface.getNetworkInterfaces();    
		    while (netInterfaces.hasMoreElements()) {    
		        NetworkInterface ni = netInterfaces.nextElement();   
		        Enumeration<InetAddress> ips = ni.getInetAddresses();    
		        while (ips.hasMoreElements()) {    
		            String  strtemp = ips.nextElement().getHostAddress();
		        	
		        	if(StringUtil.isIp(strtemp) && !strtemp.equals("127.0.0.1")){
		        		ipAdd = strtemp;
		            	get = true;
		            	break;
		        	}
		        }  
		        if(get)break;
		    }    
		} catch (Exception e) {    
		    e.printStackTrace();    
		}  
		
		try {
			url = "http://"+ipAdd+":"+ MediaServer.DEFAULT_HTTP_PORT + "/local"+URLEncoder.encode(dir, "UTF-8")+"";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	
	public static boolean isContainer(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(ContainerNode.OBJECT_CONTAINER);
	}
	
	public static boolean isImage(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(UPnP.OBJECT_ITEM_IMAGEITEM);
	}
	
	public static boolean isVideo(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(UPnP.OBJECT_ITEM_VIDEOITEM);
	}
	
	public static boolean isAudio(ContentNode content) {
		if(null == content.getUPnPClass()) {
			return false;
		}
		return content.getUPnPClass().startsWith(UPnP.OBJECT_ITEM_AUDIOITEM);
	}
}
