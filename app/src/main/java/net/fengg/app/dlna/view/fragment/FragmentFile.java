package net.fengg.app.dlna.view.fragment;

import fi.iki.elonen.SimpleWebServer;
import butterknife.ButterKnife;
import butterknife.InjectView;
import net.fengg.app.dlna.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import net.fengg.app.dlna.view.base.BaseFragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.reflect.Method;

import static android.content.Context.STORAGE_SERVICE;

public class FragmentFile extends BaseFragment implements OnCheckedChangeListener {
	@InjectView(R.id.tb_server)
	protected ToggleButton tb_server;
	@InjectView(R.id.tv_tips)
	protected TextView tv_tips;

	private static final String MOUNT_PATH = "MOUNT_PATH";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_file, container, false);
		ButterKnife.inject(this, view);
		initView();
		init();
		return view;
	}

	public void initView(){
		tb_server.setOnCheckedChangeListener(this);
	}

	public void init() {
		detectionUSB();
	}

	@Override
	public void onStart() {
		super.onStart();
		cancelBaseDialog();
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked) {
			String ip = SimpleWebServer.getIp();
			if(TextUtils.isEmpty(ip)) {
				tv_tips.setVisibility(View.VISIBLE);
				tv_tips.setText(R.string.error);
				return;
			}
			int port = 9999;
			SimpleWebServer.startServer(ip, port, getSdPath());
			//         Environment.getExternalStorageDirectory().getPath());

			tv_tips.setVisibility(View.VISIBLE);
			tv_tips.setText(getString(R.string.input) + "\n" + "http://" + ip + ":" + port);
		}else{
			SimpleWebServer.stopServer();
			tv_tips.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取所有外置存储器的目录
	 * @return
	 */
	private String[] getSdPath(){
		String[] paths = null;
		StorageManager manager = (StorageManager) this.getContext().getSystemService(STORAGE_SERVICE);
		try {
			Method methodGetPaths = manager.getClass().getMethod("getVolumePaths");
			paths = (String[]) methodGetPaths.invoke(manager);
			Log.i("path", paths.toString());
			return paths;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void detectionUSB() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
		intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intentFilter.addDataScheme("file");
		this.getContext().registerReceiver(usbReceiver, intentFilter);
	}

	private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			mHandler.removeMessages(0);
			Message msg = new Message();
			msg.what = 0;
			if (action.equals(Intent.ACTION_MEDIA_REMOVED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				//设备卸载成功
				msg.arg1 = 0;
			} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
				//设备挂载成功
				msg.arg1 = 1;
			}
			Bundle bundle = new Bundle();
			bundle.putString(MOUNT_PATH, intent.getData().getPath());
			msg.setData(bundle);
			mHandler.sendMessageDelayed(msg, 1000);
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0){
				//卸载成功
				if (msg.arg1 == 0){
					SimpleWebServer.removeDir(msg.getData().getString(MOUNT_PATH));
				}else {//挂载成功
					SimpleWebServer.addWwwRootDir(msg.getData().getString(MOUNT_PATH));
				}
			}
			super.handleMessage(msg);
		}
	};
}
