package net.fengg.app.dlna.adapter;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.util.StringUtil;

import net.fengg.app.dlna.R;
import net.fengg.app.dlna.model.Audio;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AudioAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Audio> data = new ArrayList<Audio>();
	ImageLoader imageLoader;
	
	public AudioAdapter(Context context, List<Audio> data, ImageLoader imageLoader) {
		this.context = context;
		this.data = data;
		this.imageLoader = imageLoader;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Audio getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.item_dlna_content, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
         
		holder.tv_content_name.setText(data.get(position).getTilte());
		holder.tv_content_date.setText(
				StringUtil.formatDuration(data.get(position).getDuration()));
		holder.iv_content_icon.setImageResource(R.drawable.music_icon);
//         imageLoader.displayImage("content://media/external/video/media/" + data.get(position).getId(), holder.iv_content_icon);
         
//         Bitmap bm = BitmapFactory.decodeFile(data.get(position)); 
//         if(iv !=null) {        	 
//        	 iv.setImageBitmap(bm); 
//         }
         return convertView;
	}
	
	public class ViewHolder {
		@InjectView(R.id.iv_content_icon)
		ImageView iv_content_icon;
		@InjectView(R.id.tv_content_name)
		TextView tv_content_name;
		@InjectView(R.id.tv_content_date)
		TextView tv_content_date;
		
		public ViewHolder(View view) {
		      ButterKnife.inject(this, view);
		}
	}
}
