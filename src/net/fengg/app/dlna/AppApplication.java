package net.fengg.app.dlna;

import org.cybergarage.upnp.ControlPoint;

import android.app.Application;

public class AppApplication extends Application {

	public ControlPoint mControlPoint;

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
