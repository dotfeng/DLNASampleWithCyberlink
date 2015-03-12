package net.fengg.app.dlna.service;

import net.fengg.app.dlna.presenter.FragmentAudioPre;
import net.fengg.app.dlna.presenter.FragmentImagePre;
import net.fengg.app.dlna.presenter.FragmentVideoPre;
import net.fengg.app.dlna.presenter.SearchThread;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.std.av.renderer.MediaRenderer;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.directory.file.FileDirectory;
import org.cybergarage.upnp.std.av.server.object.format.GIFFormat;
import org.cybergarage.upnp.std.av.server.object.format.ID3Format;
import org.cybergarage.upnp.std.av.server.object.format.JPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.MP3Format;
import org.cybergarage.upnp.std.av.server.object.format.MPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.PNGFormat;

import com.mustafaferhan.debuglog.DebugLog;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * The service to search the DLNA Device in background all the time.
 * 
 * @author CharonChui
 * 
 */
public class DLNAService extends Service {
	private MediaRenderer mediaRenderer;
	private MediaServer mediaServer;
	private ControlPoint mControlPoint;
	private SearchThread mSearchThread;
	private WifiStateReceiver mWifiStateReceiver;
	private static DLNAService dlnaService;

	public static DLNAService getInstance() {
		return dlnaService;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unInit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startThread();
		startMediaRenderer();
		startMediaServer();
		return super.onStartCommand(intent, flags, startId);
	}

	private void init() {
		dlnaService = this;
		mControlPoint = new ControlPoint();
		mSearchThread = new SearchThread(mControlPoint);
		registerWifiStateReceiver();
	}

	private void unInit() {
		stopThread();
		stopMediaRenderer();
		stopMediaServer();
		unregisterWifiStateReceiver();
	}

	/**
	 * Make the thread start to search devices.
	 */
	public void startThread() {
		if (mSearchThread != null) {
			DebugLog.d("thread is not null");
			mSearchThread.setSearcTimes(0);
		} else {
			DebugLog.d("thread is null, create a new thread");
			mSearchThread = new SearchThread(mControlPoint);
		}

		if (mSearchThread.isAlive()) {
			DebugLog.d("thread is alive");
			mSearchThread.awake();
		} else {
			DebugLog.d( "start the thread");
			mSearchThread.start();
		}
	}

	public void stopThread() {
		if (mSearchThread != null) {
			mSearchThread.stopThread();
			mControlPoint.stop();
			mSearchThread = null;
			mControlPoint = null;
			DebugLog.w("stop dlna service");
		}
	}

	private void registerWifiStateReceiver() {
		if (mWifiStateReceiver == null) {
			mWifiStateReceiver = new WifiStateReceiver();
			registerReceiver(mWifiStateReceiver, new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}

	private void unregisterWifiStateReceiver() {
		if (mWifiStateReceiver != null) {
			unregisterReceiver(mWifiStateReceiver);
			mWifiStateReceiver = null;
		}
	}

	private class WifiStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context c, Intent intent) {
			Bundle bundle = intent.getExtras();
			int statusInt = bundle.getInt("wifi_state");
			switch (statusInt) {
			case WifiManager.WIFI_STATE_UNKNOWN:
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				DebugLog.e("wifi enable");
				startThread();
//				startMediaRenderer();
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				DebugLog.e("wifi disabled");
				stopThread();
//				stopMediaRenderer();
				break;
			default:
				break;
			}
//			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
//					.getAction())) {
//				Parcelable parcelableExtra = intent
//						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//				if (null != parcelableExtra) {
//					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//					boolean isConnected = networkInfo.isAvailable();
//					if (!isConnected) {
//						DebugLog.e("wifi disabled");
//						stopThread();
//						stopMediaRenderer();
//					} else {
//						DebugLog.e("wifi enable");
//						startThread();
//						startMediaRenderer();
//					}
//				}
//			}
		}
	}
	
	public void startMediaServer() {
		new Thread() {
			public void run() {
				try {
					mediaServer = new MediaServer(getApplicationContext());
					
					mediaServer.addPlugIn(new JPEGFormat());
					mediaServer.addPlugIn(new PNGFormat());
					mediaServer.addPlugIn(new GIFFormat());
					mediaServer.addPlugIn(new MPEGFormat());
					mediaServer.addPlugIn(new ID3Format());
					mediaServer.addPlugIn(new MP3Format());
					
					mediaServer.addContentDirectory(
							new FileDirectory("Image", 
									FragmentImagePre.getImagePathList(getApplication())));
					mediaServer.addContentDirectory(
							new FileDirectory("Video", 
									FragmentVideoPre.getVideoPathList(getApplication())));
					mediaServer.addContentDirectory(
							new FileDirectory("Audio", 
									FragmentAudioPre.getAudioPathList(getApplication())));
					
					mediaServer.start();
				} catch (InvalidDescriptionException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void stopMediaServer() {
		new Thread() {
			public void run() {
				if(null != mediaServer) {
					mediaServer.stop();
				}
			}
		}.start();
	}
	
	public void startMediaRenderer() {
		new Thread() {
			public void run() {
				try {
					mediaRenderer = new MediaRenderer(getApplicationContext());
					mediaRenderer.start();
				} catch (InvalidDescriptionException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void stopMediaRenderer() {
		new Thread() {
			public void run() {
				if(null != mediaRenderer) {
					mediaRenderer.stop();
				}
			}
		}.start();
	}
	
	public ControlPoint getControlPoint() {
		return mControlPoint;
	}
	
	public MediaRenderer getMediaRenderer() {
		return mediaRenderer;
	}
	
	public void play(String url, String metadata) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.setDataAndType(uri, metadata);
		startActivity(intent);
//		if(metadata.contains("image")) {
//			Bundle bundle = new Bundle();
//			bundle.putString("url", url);
//			intent.setClass(getApplicationContext(),ShowImageActivity.class);
//			intent.putExtras(bundle);
//		}else if(metadata.contains("audio")) {
//			
//		}else if(metadata.contains("video")) {
//			
//		}
	}

}