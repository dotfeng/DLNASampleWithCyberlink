package net.fengg.app.dlna.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import net.fengg.app.dlna.R;
import net.fengg.app.dlna.adapter.DlnaContentAdapter;
import net.fengg.app.dlna.presenter.ActionController;
import net.fengg.app.dlna.presenter.ControlPointContainer;
import net.fengg.app.dlna.util.Common;
import net.fengg.app.dlna.util.DLNAUtil;
import net.fengg.app.dlna.view.base.BaseActivity;

public class ShowDlnaActivity extends BaseActivity implements OnItemClickListener, OnRefreshListener2<ListView> {
	
	@InjectView(R.id.prl_file_list)
	protected PullToRefreshListView prl_file_list;
	private ListView actualListView;
	DlnaContentAdapter adapter;
	@InjectView(R.id.tv_title)
	protected TextView tv_title;
	
	List<ContentNode> contents = new ArrayList<ContentNode>();
	private int startingIndex = 0;
	private final int REQUESTED_COUNT = 10;
	
	private Stack<String> stack = new Stack<String>();
	
	private Map<String, String> titles = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_dlna);
		ButterKnife.inject(this);
		init();
		cancelBaseDialog();
	}
	
	private void init() {
		titles.put("0", ControlPointContainer.getInstance().getSelectedDevice().getFriendlyName());
		tv_title.setText(titles.get("0"));
		prl_file_list.setMode(Mode.BOTH);
		prl_file_list.setOnRefreshListener(this);
		actualListView = prl_file_list.getRefreshableView();
		adapter = new DlnaContentAdapter(this, contents, Common.getImageLoader(this));
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(this);
		browse("0");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void browse(String id) {
		new AsyncTask<String, Integer, List<ContentNode>>() {
			@Override  
	        protected void onPreExecute() {
	        } 
			
			@Override
			protected List<ContentNode> doInBackground(String... params) {
				List<ContentNode> data = 
						browse(ControlPointContainer.getInstance().getSelectedDevice(), 
								params[0], startingIndex, REQUESTED_COUNT);
				return data;
			}
			
			@Override  
	        protected void onProgressUpdate(Integer... progresses) {  
				
	        }  
			
			@Override  
	        protected void onPostExecute(List<ContentNode> result) {  
				if(null == result) {
					return;
				}
				
				contents.addAll(result);
				
				actualListView.setSelection(startingIndex);
				adapter.notifyDataSetChanged();
				prl_file_list.onRefreshComplete();
				if(contents.size() % REQUESTED_COUNT != 0) {
					prl_file_list.setMode(Mode.PULL_FROM_START);
				}
	        }  
	          
	        @Override  
	        protected void onCancelled() {
	        }  
		}.execute(id);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ContentNode content = (ContentNode) parent.getItemAtPosition(position);
		
		if(DLNAUtil.isContainer(content)) {
			titles.put(content.getID(), content.getTitle());
			tv_title.setText(content.getTitle());
			contents.clear();
			startingIndex = 0;
			adapter.notifyDataSetChanged();
			browse(content.getID());
		}else {
			Intent intent = new Intent();
			Uri uri = Uri.parse(((ItemNode)content).getResource());
			
			if(DLNAUtil.isImage(content)) {
				intent.setClass(getApplicationContext(), ShowImageActivity.class);			
				intent.setData(uri);
			}else if(DLNAUtil.isVideo(content)) {
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "video/*");
			}else if(DLNAUtil.isAudio(content)) {
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "audio/*");
			}
			
			startActivity(intent);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (!stack.isEmpty()
				&& stack.size() > 1) {
			String currentLevel = stack.peek();
			if (!currentLevel.equals("0")) {
				stack.pop();
				String newLevel = stack.peek();
				contents.clear();
				startingIndex = 0;
				adapter.notifyDataSetChanged();
				tv_title.setText(titles.get(newLevel));
				browse(newLevel);
				return;
			}
		}
		super.onBackPressed();
	}

	@OnClick(R.id.tv_title)
	protected void onTitle() {
		onBackPressed();
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(
				getApplicationContext(),
				System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(label);
		
		contents.clear();
		startingIndex = 0;
		adapter.notifyDataSetChanged();
		browse(stack.peek());
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(
				getApplicationContext(),
				System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(label);
		startingIndex += REQUESTED_COUNT;
		browse(stack.peek());
	}
	
	public List<ContentNode> browse(Device device, 
			String id, int startingIndex, 
			int requestedCount) {
		
		List<ContentNode> list = new ArrayList<ContentNode>();
		
		list = ActionController.getInstance().browse(device, id, startingIndex + "", requestedCount + "", "*", "");
		
		if (stack.size() == 0 || !stack.peek().equals(id)) {
			stack.push(id);
		}
		
		return list;
	}
}
