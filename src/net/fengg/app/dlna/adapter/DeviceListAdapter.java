package net.fengg.app.dlna.adapter;

import java.util.ArrayList;
import java.util.List;

import net.fengg.app.dlna.R;
import net.fengg.app.dlna.presenter.ControlPointContainer;

import org.cybergarage.upnp.Device;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {
	private List<Device> devices = new ArrayList<Device>();
	private LayoutInflater inflater;
	private Context context;
	ImageLoader imageLoader;

	public DeviceListAdapter(Context context, List<Device> devices, ImageLoader imageLoader) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.devices = devices;
		this.imageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Device getItem(int position) {
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.item_device, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Device device = devices.get(position);
		
		Drawable select = null;
		if(null != ControlPointContainer.getInstance().getSelectedDevice() 
				&& device.getUDN().equals(
						ControlPointContainer.getInstance().getSelectedDevice().getUDN())) {
			select = context.getResources().getDrawable(R.drawable.select);
		}
		
		holder.tv_friendlyname.setText(device.getFriendlyName());
		holder.tv_friendlyname.setCompoundDrawables(null, null, select, null);
	
		return convertView;
	}
	
	public class ViewHolder {
		@InjectView(R.id.tv_friendlyname)
		TextView tv_friendlyname;
		
		public ViewHolder(View view) {
		      ButterKnife.inject(this, view);
		}
	}
}
