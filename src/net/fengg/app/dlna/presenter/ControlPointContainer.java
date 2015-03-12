package net.fengg.app.dlna.presenter;

import java.util.ArrayList;
import java.util.List;

import net.fengg.app.dlna.service.DLNAService;
import net.fengg.app.dlna.util.DLNAUtil;

import org.cybergarage.upnp.Device;

import com.mustafaferhan.debuglog.DebugLog;

public class ControlPointContainer {
	private List<Device> mDevices;
	private Device mSelectedDevice;
	private DeviceChangeListener mDeviceChangeListener;
	private static final ControlPointContainer mDLNAContainer = new ControlPointContainer();

	private ControlPointContainer() {
		mDevices = new ArrayList<Device>();
	}

	public static ControlPointContainer getInstance() {
		return mDLNAContainer;
	}

	public synchronized void addDevice(Device device) {
		DebugLog.d("add device" + device.getDeviceType() + device.getFriendlyName());
		if (!DLNAUtil.isMediaRenderer(device) && !DLNAUtil.isMediaServer(device)) {
			return;			
		}
		int size = mDevices.size();
		for (int i = 0; i < size; i++) {
			String udnString = mDevices.get(i).getUDN();
			if (device.getUDN().equalsIgnoreCase(udnString)) {
				return;
			}
		}
		
		mDevices.add(device);
		DebugLog.d("Devices add a device" + device.getDeviceType() + device.getFriendlyName());
		if (mDeviceChangeListener != null) {
			mDeviceChangeListener.onDeviceChange(device);
		}
	}

	public synchronized void removeDevice(Device d) {
		if (!DLNAUtil.isMediaRenderer(d) && !DLNAUtil.isMediaServer(d)) {
			return;
		}
		int size = mDevices.size();
		for (int i = 0; i < size; i++) {
			String udnString = mDevices.get(i).getUDN();
			if (d.getUDN().equalsIgnoreCase(udnString)) {
				Device device = mDevices.remove(i);
				DebugLog.d("Devices remove a device");

				if (mSelectedDevice != null && 
					mSelectedDevice.getUDN().equalsIgnoreCase(device.getUDN())) {
					mSelectedDevice = null;
				}
				if (mDeviceChangeListener != null) {
					mDeviceChangeListener.onDeviceChange(d);
				}
				break;
			}
		}
	}

	public void searchDevices() {
		DLNAService.getInstance().stopThread();
		DLNAService.getInstance().startThread();
	}
	
	public synchronized void clear() {
		if (mDevices != null) {
			mDevices.clear();
			mSelectedDevice = null;
		}
	}

	public Device getSelectedDevice() {
		return mSelectedDevice;
	}

	public void setSelectedDevice(Device mSelectedDevice) {
		this.mSelectedDevice = mSelectedDevice;
	}

	public void setDeviceChangeListener(
			DeviceChangeListener deviceChangeListener) {
		mDeviceChangeListener = deviceChangeListener;
	}

	public List<Device> getDevices() {
		return mDevices;
	}

	public interface DeviceChangeListener {
		void onDeviceChange(Device device);
	}

}
