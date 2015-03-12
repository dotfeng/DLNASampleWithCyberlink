package net.fengg.app.dlna.adapter;

import java.util.ArrayList;
import java.util.List;

import net.fengg.app.dlna.R;
import net.fengg.app.dlna.model.Image;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Image> data = new ArrayList<Image>();
	ImageLoader imageLoader;
	
	public ImageAdapter(Context context, List<Image> data, ImageLoader imageLoader) {
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
	public Image getItem(int position) {
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
			convertView = inflater.inflate(R.layout.item_picture, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
         
         imageLoader.displayImage("content://media/external/images/media/" + data.get(position).getId(), holder.iv_picture);
         
//         Bitmap bm = BitmapFactory.decodeFile(data.get(position)); 
//         if(iv !=null) {        	 
//        	 iv.setImageBitmap(bm); 
//         }
         return convertView;
	}
	
	public class ViewHolder {
		@InjectView(R.id.iv_picture)
		ImageView iv_picture;
		
		public ViewHolder(View view) {
		      ButterKnife.inject(this, view);
		}
	}
}
