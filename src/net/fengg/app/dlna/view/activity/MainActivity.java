package net.fengg.app.dlna.view.activity;

import org.cybergarage.upnp.Device;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import net.fengg.app.dlna.R;
import net.fengg.app.dlna.adapter.DeviceListAdapter;
import net.fengg.app.dlna.presenter.ControlPointContainer;
import net.fengg.app.dlna.presenter.ControlPointContainer.DeviceChangeListener;
import net.fengg.app.dlna.service.DLNAService;
import net.fengg.app.dlna.util.Common;
import net.fengg.app.dlna.util.DLNAUtil;
import net.fengg.app.dlna.view.base.BaseFragmentActivity;
import net.fengg.app.dlna.view.fragment.FragmentImage;
import net.fengg.app.dlna.view.fragment.FragmentFile;
import net.fengg.app.dlna.view.fragment.FragmentVideo;
import net.fengg.app.dlna.view.fragment.FragmentAudio;
import net.fengg.lib.tabsliding.TabSlidingView;
import net.fengg.lib.tabsliding.ContentItem;
import net.fengg.lib.tabsliding.TabSlidingView.TabContentProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends BaseFragmentActivity {

	private FragmentImage fragmentImage;
	private FragmentVideo fragmentVideo;
	private FragmentAudio fragmentAudio;
	private FragmentFile fragmentAbout;

	private DisplayMetrics dm;
	private PopupWindow popupWindow;
	private MyPagerAdapter adapter;
	@InjectView(R.id.tabs)
	protected TabSlidingView tabs;
	@InjectView(R.id.pager)
	protected ViewPager pager;
	@InjectView(R.id.rl_device_toolbar)
	protected RelativeLayout rl_device_toolbar;
	@InjectView(R.id.tv_no_device)
	protected TextView tv_no_device;
	@InjectView(R.id.iv_search_device)
	protected ImageView iv_search_device;
	protected ListView lv_devices;
	DeviceListAdapter deviceAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		initView();
		startDLNAService();
		cancelBaseDialog();
	}

	private void initView() {
		dm = getResources().getDisplayMetrics();
		adapter=new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		setTabsValue();
		ControlPointContainer.getInstance().setDeviceChangeListener(
				new DeviceChangeListener() {

					@Override
					public void onDeviceChange(Device device) {
						runOnUiThread(new Runnable() {
							public void run() {
								refresh();
							}
						});
					}
				});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}
	
	@OnClick(R.id.iv_search_device)
	protected void onDevice(View view) {
		showWindow(view);
	}
	
	private void showWindow(View parent) {
		View view;
		if (popupWindow == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			view = layoutInflater.inflate(R.layout.pop_device_list,
					rl_device_toolbar, false);
			popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, Common.dp2px(this, 260));
			deviceAdapter = new DeviceListAdapter(this, ControlPointContainer.getInstance().getDevices(), Common.getImageLoader(this));
			lv_devices = (ListView) view.findViewById(R.id.lv_devices);
			lv_devices.setAdapter(deviceAdapter);
			lv_devices.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Device device = (Device)parent.getItemAtPosition(position);
					ControlPointContainer.getInstance().setSelectedDevice(device);
//					((TextView)view).setCompoundDrawables(
//							null, null, getResources().getDrawable(R.drawable.select), null);
					if(DLNAUtil.isMediaRenderer(device)) {						
						refresh();
					}else if(DLNAUtil.isMediaServer(device)) {
						openActivity(ShowDlnaActivity.class);
					}
					popupWindow.dismiss();
				}
			});
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.AnimBottom);
			popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap)null));
		}
//		popupWindow.showAsDropDown(pager, 0, 0);
		 popupWindow.showAtLocation(pager, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, Common.dp2px(this, 60));
	}
	
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
//		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 15, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(getResources().getColor(R.color.tab_green));
		tabs.setUnderlineColor(getResources().getColor(R.color.tab_green));
		tabs.setTabBackground(0);
		tabs.setIconAbove(true);
		//设置指示在上部
		tabs.setIndicatorBelow(true);

	}

	public class MyPagerAdapter extends FragmentPagerAdapter implements TabContentProvider{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getResources().getStringArray(R.array.tabs)[position];
		}

		@Override
		public int getCount() {
			return getResources().getStringArray(R.array.tabs).length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (fragmentImage == null) {
					fragmentImage = new FragmentImage();
				}
				return fragmentImage;
			case 1:
				if (fragmentVideo == null) {
					fragmentVideo = new FragmentVideo();
				}
				return fragmentVideo;
			case 2:
				if (fragmentAudio == null) {
					fragmentAudio = new FragmentAudio();
				}
				return fragmentAudio;
			case 3:
				if (fragmentAbout == null) {
					fragmentAbout = new FragmentFile();
				}
				return fragmentAbout;
			default:
				return null;
			}
		}

		@Override
		public ContentItem getTabContent(int position) {
			ContentItem item = new ContentItem();
			item.setTitle(getResources().getStringArray(R.array.tabs)[position]);
			return item;
		}
	}
	


	private void refresh() {
		if (deviceAdapter != null) {
			deviceAdapter.notifyDataSetChanged();
		}
		if(null != ControlPointContainer.getInstance().getSelectedDevice()) {			
			tv_no_device.setText(ControlPointContainer.getInstance().getSelectedDevice().getFriendlyName());
			iv_search_device.setImageResource(R.drawable.device_connected);
		}
		
	}
	
	private void startDLNAService() {
		Intent intent = new Intent(getApplicationContext(), DLNAService.class);
		startService(intent);
	}
	
	private void stopDLNAService() {
		Intent intent = new Intent(getApplicationContext(), DLNAService.class);
		stopService(intent);
	}
	
}