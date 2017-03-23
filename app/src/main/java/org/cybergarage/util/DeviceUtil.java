package org.cybergarage.util;

import java.net.URLEncoder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

public class DeviceUtil {
	// //////////////////////////////////////////////
	// UUID
	// //////////////////////////////////////////////

	private static final String toUUID(int seed) {
		String id = Integer.toString((int) (seed & 0xFFFF), 16);
		int idLen = id.length();
		String uuid = "";
		for (int n = 0; n < (4 - idLen); n++)
			uuid += "0";
		uuid += id;
		return uuid;
	}

	public static final String getFriendlyName(Context context, String device){
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(device, Context.MODE_PRIVATE);
			if(sharedPreferences.getString("friendlyName", "").isEmpty()) {
				String friendlyName = Build.BRAND + " " + Build.MODEL +" " + device;
				Editor editor = sharedPreferences.edit();
		        editor.putString("friendlyName", friendlyName);
		        editor.commit();
				return friendlyName;
			} else {
				return sharedPreferences.getString("friendlyName", "");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public static final void setFriendlyName(Context context, String device, String friendlyName){
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(device, Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
		    editor.putString("friendlyName", friendlyName);
		    editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final String getUUID(Context context, String device) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(device, Context.MODE_PRIVATE);
		if(sharedPreferences.getString("uuid", "").isEmpty()) {
			String uuid = createUUID();
			Editor editor = sharedPreferences.edit();
	        editor.putString("uuid", uuid);
	        editor.commit();
			return uuid;
		} else {
			return sharedPreferences.getString("uuid", "");
		}
	}

	public static final String createUUID() {
		long time1 = System.currentTimeMillis();
		long time2 = (long) ((double) System.currentTimeMillis() * Math
				.random());
		return toUUID((int) (time1 & 0xFFFF)) + "-"
				+ toUUID((int) ((time1 >> 32) | 0xA000) & 0xFFFF) + "-"
				+ toUUID((int) (time2 & 0xFFFF)) + "-"
				+ toUUID((int) ((time2 >> 32) | 0xE000) & 0xFFFF);
	}
}
