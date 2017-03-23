package net.fengg.app.dlna.adapter;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;

import net.fengg.app.dlna.R;
import net.fengg.app.dlna.util.DLNAUtil;
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

public class DlnaContentAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<ContentNode> data = new ArrayList<ContentNode>();
	ImageLoader imageLoader;
	
	public DlnaContentAdapter(Context context, List<ContentNode> data, ImageLoader imageLoader) {
		this.context = context;
		this.data = data;
		this.imageLoader = imageLoader;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public ContentNode getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
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
        ContentNode content = data.get(position);
        holder.tv_content_name.setText(content.getTitle());
		if (DLNAUtil.isContainer(content)) {
			holder.iv_content_icon.setImageResource(R.drawable.dlnafolder);
			holder.tv_content_date.setText("");
		} else {
			holder.tv_content_date.setText(((ItemNode)content).getDate());
			if (DLNAUtil.isAudio(content)) {
				holder.iv_content_icon.setImageResource(R.drawable.music_icon);
			} else if (DLNAUtil.isVideo(content)) {
				holder.iv_content_icon.setImageResource(R.drawable.moive_icon);
			} else if (DLNAUtil.isImage(content)) {
				imageLoader.displayImage(((ItemNode)content).getResource(), holder.iv_content_icon);
			}
		}
		
//         imageLoader.displayImage("content://media/external/images/media/" + data.get(position).getId(), holder.iv_picture);
         
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
