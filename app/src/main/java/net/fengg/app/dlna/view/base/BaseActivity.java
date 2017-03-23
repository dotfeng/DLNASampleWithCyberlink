/**
 * 
 */
package net.fengg.app.dlna.view.base;


import net.fengg.app.dlna.AppManager;
import net.fengg.app.dlna.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


/**
 * activity基类
 * 
 * @author Feng
 * 
 */
public class BaseActivity extends Activity  implements OnClickListener {

	protected ProgressDialog baseDialog;

	private long exitTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		showBaseDialog();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelBaseDialog();
		AppManager.getAppManager().finishActivity(this);

	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data){  
		super.onActivityResult(requestCode, resultCode, data);
    }
	
	@Override
	public void onClick(View v) {
	}
	
	/**
	 * 进入activity显示加载进度
	 */
	protected void showBaseDialog() {
		if (null == baseDialog || !baseDialog.isShowing()) {
			baseDialog = new ProgressDialog(this);
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

	protected void finishAndOpenActivity(Class<?> activity) {
		super.finish();
		openActivity(activity);
	}
	
	protected void finishAndOpenActivity(Class<?> activity, Bundle bundle) {
		super.finish();
		openActivity(activity, bundle);
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
		intent.setClass(getApplicationContext(), activity);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

	}

	protected void openActivityForResult(Class<?> activity, Intent intent, int requestCode) {
		intent.setClass(getApplicationContext(), activity);
		startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
	
	/**
	 * 
	 * 备用，minSdkVersion 16 以上去掉注释可用
	 * 
	 * @param activity
	 * @param bundle
	 * @param requestCode
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void openActivityForResult(Class<?> activity, Bundle bundle,
			int requestCode) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), activity);
		
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//			startActivityForResult(intent, requestCode, bundle);
//		} else {
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			startActivityForResult(intent, requestCode);
//		}
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
	
	protected void showShortToast(String message) {
		if(!TextUtils.isEmpty(message))
			Toast.makeText(this,
				message, Toast.LENGTH_SHORT).show();
	}
	
	protected void showLongToast(String message) {
		if(!TextUtils.isEmpty(message))
			Toast.makeText(this,
				message, Toast.LENGTH_LONG).show();
	}
	
	protected void onBackExit() {
		setResult(RESULT_OK);
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			showShortToast("再按一次退出");
			exitTime = System.currentTimeMillis();
		} else {
			exitTime = 0;
			AppManager.getAppManager().finishAllActivities();
		}
	}
}
