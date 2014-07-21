/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.baixing.network.api.ApiConfiguration;
import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListenerTag;
import com.custom.vg.list.OnItemLongClickListenerTag;
import com.example.dsfwe.AdItem;
import com.example.dsfwe.NetworkJson;
import com.example.dsfwe.TagAdapter;
import com.example.dsfwe.TagItem;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.maxwin.view.Info;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;


public class ImageListActivity extends ListViewBaseActivity implements  IXListViewListener{

	DisplayImageOptions options;

	String[] imageUrls;

	private InfoAdapter mAdapter;
	private ArrayList<Info> items = new ArrayList<Info>();
	private List<AdItem> adlist; 
	
	private int start = 0;
	private static int refreshCnt = 0;
	private static int delay_time = 2000;
	private Handler mHandler;
	
	private CustomListView tagview;
	private List<TagItem> taglist = null;
	private TagAdapter adapter;
	private String wzName;
	
	private PopupWindow mPopupWindow;
	private RelativeLayout mRe;
	
	Context activity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//隐藏标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 设置标题栏的显示风格
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.ac_image_list);
		//自定义标题栏

			// 将自定义的title布局显示在标题栏中
			//	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.list_title);
		
		
		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);

		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.build();


		geneItems();
		mRe = (RelativeLayout)findViewById(R.id.list_title);
		ImageButton bt = (ImageButton)mRe.findViewById(R.id.list_bt);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		          if (mPopupWindow != null && !mPopupWindow.isShowing()) {
		                mPopupWindow.showAtLocation(mRe, Gravity.BOTTOM, 0, 0);
		            }
		          else
		               if (mPopupWindow != null && mPopupWindow.isShowing()) {
	                        mPopupWindow.dismiss();
					}
				
			}});
		listView = (XListView) findViewById(R.id.xListView);
		listView.setClickable(true);
		listView.setPullLoadEnable(true);
		mAdapter = new InfoAdapter(items);
		listView.setAdapter(mAdapter);
		listView.setXListViewListener(this);
		mHandler = new Handler();
		((ListView) listView).setAdapter(new InfoAdapter(items));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
		
		  View popupView = getLayoutInflater().inflate(R.layout.tag_pop, null);

	        mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
	        mPopupWindow.setTouchable(true);
	        mPopupWindow.setOutsideTouchable(true);
	        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

	        mPopupWindow.getContentView().setFocusableInTouchMode(true);
	        mPopupWindow.getContentView().setFocusable(true);
	        mPopupWindow.getContentView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
				}
				}});
	        

			initTag();
			
	}
	
	class DownTagTask extends AsyncTask<String,String,List<TagItem>>
	{
		 Context ctx;
		public DownTagTask(Context ctx)
		{
			this.ctx = ctx;
		}

		@Override
		protected List<TagItem> doInBackground(String... arg0) {
			try{
					if(wzName == "")
					{
						return null;
					}
					else
					{	
						taglist = NetworkJson.getWeiZhanTagList(ctx, wzName).getResult();	
						return taglist;
					}
			}catch(Exception e){
					
				e.printStackTrace();
			}
			return null;
		}
		
		  @Override  
	      protected void onPostExecute(List<TagItem> result) {  	  
			  if(taglist ==null ) taglist = new ArrayList<TagItem>();
			  if(result != null)
			    {	
			    	 taglist = result;
			    }
			    else
			    {
			    	 	taglist.add(new TagItem("您还没有自己的标签，快去创建吧","#A2B5CD","#9400D3"));
			    }
			    ShowTagListView();
	       }  
	}
	private void initTag()
	{
		ApiConfiguration.config("www.zhaoyiru.baixing.cn", null, "api_androidbaixing",
				"c6dd9d408c0bcbeda381d42955e08a3f", "dd", "dd", null);
		//从上一个Fragment获得微站名字。		
		tagview = (CustomListView) mPopupWindow.getContentView().findViewById(R.id.sexangleView);	
		//Tag和Ad必须先初始化。
		taglist = new ArrayList<TagItem>();
		
		taglist.add(new TagItem("Tag正在加载中","#A2B5CD","#9400D3"));

		tagview.setDividerHeight(3);
		tagview.setDividerWidth(30);	
		adapter = new TagAdapter(this, taglist);
		tagview.setAdapter(adapter);

		wzName = "troyside";	
		
		DownTagTask task = new DownTagTask(this);
		task.execute(wzName);
	}

	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
		super.onBackPressed();
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	private static class ViewHolder {
		TextView title;
		ImageView image;
		Button bt1;
	}
	private class InfoAdapter extends ArrayAdapter<Info>{
		public InfoAdapter(ArrayList<Info> l){
			super(activity, 0, l);
		}
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) view.findViewById(R.id.list_item_info_title);
				holder.image = (ImageView) view.findViewById(R.id.list_item_info_image);
				holder.bt1=(Button)view.findViewById(R.id.list_item_info_bt1);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.title.setText("Item " + (position + 1));

			imageLoader.displayImage(imageUrls[position], holder.image, options, animateFirstListener);
			
			holder.bt1.setText("bt1");
			//listView 中有button控件时，必须将子控件的focusable设置为false,其本身才能捕获到click
			holder.bt1.setFocusable(false);
			holder.bt1.setClickable(true);
			holder.bt1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			          if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			                mPopupWindow.showAtLocation(mRe, Gravity.BOTTOM, 0, 0);
			            }
			          else
			               if (mPopupWindow != null && mPopupWindow.isShowing()) {
		                        mPopupWindow.dismiss();
						}
					
				}});
			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	private void geneItems() {
		int num = 10;
		for (int i = 0; i != num; ++i) {
			++start;
			String s = "" + start;
			items.add(new Info("title" + s, "text" + s, "bt" + s));
		}
	}

	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("不久前");
	}
	
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start = ++refreshCnt;
				items.clear();
				geneItems();
				mAdapter = new InfoAdapter(items);
				listView.setAdapter(mAdapter);
				onLoad();
			}
		}, delay_time);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems();
				mAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, delay_time);
	}

	private void ShowTagListView(){
		Log.v("Lich","ShowTagListView");
		TagAdapter adapter2 = new TagAdapter(this, taglist);
		tagview.setDividerHeight(3);
		tagview.setDividerWidth(30);		
		tagview.setAdapter(adapter2);

		tagview.setOnItemClickListenerTag(new OnItemClickListenerTag(){

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int position, long id) {
				
				HashMap<String,String> apiParam =new HashMap<String,String>();
				
				apiParam.put("tag",taglist.get(position).getText());
				int from = 0;
				int to = 30;
				HashMap<String,String> params =  taglist.get(position).getParams();
					
				//Toast.makeText(this, "ddd",Toast.LENGTH_LONG).show();
				//获取微站的ad列表。
				
				DownAdTask task = new DownAdTask(activity,from,to,params);
				task.execute(wzName);

			}});
	}
	
	class DownAdTask extends AsyncTask<String,String,List<AdItem>>
	{
		 Context ctx;
		 int from,to;
		 HashMap<String,String>params;
		DownAdTask(Context ctx,int from, int to,HashMap<String,String>params)
		{
			this.ctx = ctx;
			this.from =from;
			this.to = to ;
			this.params = params;
		}
		@Override
		protected List<AdItem> doInBackground(String... arg0) {
			try{
				if(wzName == "")
				{
					return null;
				}
				else
				{	
					adlist = NetworkJson.getWeiZhanAdList(ctx, wzName, from, to, params).getResult();	
				    return adlist;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		  @Override  
	        protected void onPostExecute(List<AdItem> result) {  
			  	if(adlist ==null ) adlist = new ArrayList<AdItem>();
			     if(result != null)
			     {
			    	 adlist = result;
			     }
			     else
			     {
			    	 adlist.add(new AdItem("您还没有自己的广告，快去添加吧"));
			     }
	        }  
		}	
}