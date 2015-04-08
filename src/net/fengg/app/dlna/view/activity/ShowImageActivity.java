package net.fengg.app.dlna.view.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import net.fengg.app.dlna.R;
import net.fengg.app.dlna.util.Common;
import net.fengg.app.dlna.view.base.BaseActivity;

public class ShowImageActivity extends BaseActivity {
	@InjectView(R.id.iv_image)
	protected ImageView iv_image;
	@InjectView(R.id.tv_image_title)
	protected TextView tv_image_title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		ButterKnife.inject(this);
		init();
		cancelBaseDialog();
	}
	
	private void init() {
		tv_image_title.setText(getIntent().getData().toString());
		Common.getImageLoader(this).displayImage(getIntent().getData().toString(), iv_image);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
