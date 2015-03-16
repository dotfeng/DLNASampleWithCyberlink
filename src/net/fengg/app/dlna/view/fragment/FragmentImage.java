package net.fengg.app.dlna.view.fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import net.fengg.app.dlna.R;
import net.fengg.app.dlna.adapter.ImageAdapter;
import net.fengg.app.dlna.model.Image;
import net.fengg.app.dlna.presenter.ControlPointContainer;
import net.fengg.app.dlna.presenter.FragmentImagePre;
import net.fengg.app.dlna.presenter.ActionController;
import net.fengg.app.dlna.util.Common;
import net.fengg.app.dlna.util.DLNAUtil;
import net.fengg.app.dlna.view.activity.MainActivity;
import net.fengg.app.dlna.view.base.BaseFragment;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class FragmentImage extends BaseFragment implements OnItemClickListener {
	View view;
	@InjectView(R.id.gv_picture)
	protected GridView gv_picture;
	
	ImageAdapter adapter;
	
	MainActivity mainActivity;
	
	List<Image> images = new ArrayList<Image>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_image, container, false);
		ButterKnife.inject(this, view);
		initView();
		init();
		return view;
	}
	
	public void initView(){
		adapter = new ImageAdapter(getActivity(), images, Common.getImageLoader(mainActivity));
		gv_picture.setAdapter(adapter);
		gv_picture.setOnItemClickListener(this);
	}
	
	public void init() {
		new AsyncTask<String, Integer, Boolean>() {
			@Override  
	        protected void onPreExecute() { 
				images.clear();
				showBaseDialog();
	        } 
			
			@Override
			protected Boolean doInBackground(String... params) {
				//can not us images = ...
				images.addAll(FragmentImagePre.getImageFromSD(mainActivity));
				return true;
			}
			
			@Override  
	        protected void onProgressUpdate(Integer... progresses) {  
				
	        }  
			
			@Override  
	        protected void onPostExecute(Boolean result) { 
				adapter.notifyDataSetChanged();
				cancelBaseDialog();
	        }  
	          
	        @Override  
	        protected void onCancelled() {
	        	adapter.notifyDataSetChanged();
				cancelBaseDialog();
	        }  
		}.execute("");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		cancelBaseDialog();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainActivity = (MainActivity) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(null == ControlPointContainer.getInstance().getSelectedDevice()
				|| !DLNAUtil.isMediaRenderer(ControlPointContainer.getInstance().getSelectedDevice())){
			showShortToast("请连接MediaRenderer设备");
		}else {
			play(((Image) parent.getItemAtPosition(position)).getDirectory());
		}
	}
	
	private void play(String path) {
		new AsyncTask<String, Integer, Boolean>() {
			@Override  
	        protected void onPreExecute() {  
	        } 
			
			
			@Override
			protected Boolean doInBackground(String... params) {
				return ActionController.getInstance().play(
						ControlPointContainer.getInstance().getSelectedDevice(), DLNAUtil.getUrl(params[0]));
			}
			
			@Override  
	        protected void onProgressUpdate(Integer... progresses) {  
				
	        }  
			
			@Override  
	        protected void onPostExecute(Boolean result) {  
				if (result) {
					showShortToast("播放成功");
				}else {
					showShortToast("播放失败");
				}
	        }  
	          
	        @Override  
	        protected void onCancelled() {  
	        }  
		}.execute(path);
	}
}
