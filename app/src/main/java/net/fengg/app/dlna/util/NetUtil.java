package net.fengg.app.dlna.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.cybergarage.util.StringUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * 网络连接的一些工具类
 */
public class NetUtil {

	/**
	 * 判断当前网络是否可用
	 */
	public static boolean isNetAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
	}

	/**
	 * 判断WIFI是否使用
	 */
	public static boolean isWIFIActivate(Context context) {
		return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.isWifiEnabled();
	}

	/**
	 * 修改WIFI状态
	 * 
	 * @param status
	 *            true为开启WIFI，false为关闭WIFI
	 */
	public static void changeWIFIStatus(Context context, boolean status) {
		((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.setWifiEnabled(status);
	}
	
	public static String getIp(){
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
		
		return ipAdd;
	}
}
