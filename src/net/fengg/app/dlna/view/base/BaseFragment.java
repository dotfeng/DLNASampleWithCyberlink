package net.fengg.app.dlna.view.base;

import net.fengg.app.dlna.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BaseFragment extends Fragment{

	protected ProgressDialog baseDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		showBaseDialog();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onDetach() {
		super.onDetach();
	}
	/**
	 * 进入activity显示加载进度
	 */
	protected void showBaseDialog() {
		if (null == baseDialog || !baseDialog.isShowing()) {
			baseDialog = new ProgressDialog(getActivity());
			baseDialog.show();
		}
	}

	/**
	 * 消除baseDialog
	 */
	protected void cancelBaseDialog() {
		if (baseDialog.isShowing()) {
			baseDialog.dismiss();
		}
	}

	protected void openActivity(Class<?> activity) {
		openActivity(activity, null);
	}

	/**
	 * @param activity
	 * @param bundle
	 */
	protected void openActivity(Class<?> activity, Bundle bundle) {

		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		intent.setClass(getActivity(), activity);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
	
	protected void openActivityForResult(Class<?> activity, Intent intent, int requestCode) {
		intent.setClass(getActivity(), activity);
		startActivityForResult(intent, requestCode);
		getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
	
	/**
	 * 
	 * 备用，minSdkVersion 16 以上去掉注释可用
	 * 
	 * @param activity
	 * @param bundle
	 * @param requestCode
	 */
	@Deprecated
	protected void openActivityForResult(Class<?> activity, Bundle bundle,
			int requestCode) {
		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//			startActivityForResult(intent, requestCode, bundle);
//		} else {
			startActivityForResult(intent, requestCode);
//		}
			getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
	
	protected void showShortToast(String message) {
		if(!TextUtils.isEmpty(message))
			Toast.makeText(getActivity(),
				message, Toast.LENGTH_SHORT).show();
	}
	
	protected void showLongToast(String message) {
		if(!TextUtils.isEmpty(message))
			Toast.makeText(getActivity(),
				message, Toast.LENGTH_LONG).show();
	}
}