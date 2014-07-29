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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.baixing.network.api.ApiConfiguration;
import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListenerTag;
import com.custom.vg.list.OnItemLongClickListenerTag;
import com.example.dsfwe.AdItem;
import com.example.dsfwe.BlowItem;
import com.example.dsfwe.NetworkJson;
import com.example.dsfwe.TagAdapter;
import com.example.dsfwe.TagItem;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.util.CustomGallery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.maxwin.view.Info;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;


public class ImageListActivity extends ListViewBaseActivity{

	DisplayImageOptions options;
	DisplayImageOptions g_options;
	String[] imageUrls;

	private  final static int REFRESH =1;
	private  final static int LOAD =2;
	private final static int blow_times = 1;
	private  int blow = 0;
	private BlowItem blowItem; 
	private AdItem blowAdItem;
	
	private String blowAdId;//标识哪个ad被吹上去l
	
	private InfoAdapter mAdapter;
	private List<AdItem> adlist; 

	
	private static int delay_time = 0;
	private Handler mHandler;
	
	private LinearLayout tags;
	private CustomListView tagview;
	private List<TagItem> taglist = null;
	private TagAdapter adapter;
	private String wzName;
	
	private Animation mDownA;
	private Animation mUpA;
	private RelativeLayout mRe;
	
	//tag
	private int flag;
	private final int TAG_LOAD_MORE = 0;
	private final int TAG_SHRINK_UP = 1;
	private int lastTag = -1;
	private String lastBgColor;
	
	Context activity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//隐藏标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.ac_image_list);
		//自定义标题栏

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

		g_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new RoundedBitmapDisplayer(10))
		.build();
		
		mRe = (RelativeLayout)findViewById(R.id.list_title);
		Button bt = (Button)mRe.findViewById(R.id.list_bt_notify);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popWindowDo();
			}});
		bt = (Button)mRe.findViewById(R.id.list_bt_name);
		TextView text = (TextView)mRe.findViewById(R.id.list_text_title_name);
		text.setText("微站名");
	
		tags = (LinearLayout)findViewById(R.id.list_tags);
		tags.setVisibility(View.VISIBLE);
		tagview = (CustomListView)tags.findViewById(R.id.list_sexangleView);	
		initTag();
	
		adlist = new ArrayList<AdItem>();
		blowItem = new BlowItem();

		listView = (XListView) findViewById(R.id.xListView);
		listView.setClickable(true);
		listView.setPullLoadEnable(true);
		mAdapter = new InfoAdapter(ImageListActivity.this, adlist);
		listView.setAdapter(mAdapter);
		listView.setXListViewListener(new IXListViewListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				ImageListActivity.this.onRefresh();
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				ImageListActivity.this.onLoadMore();
			}
			
		});
		mHandler = new Handler();
		geneItems(REFRESH);
		/*
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});*/
		//只显示一行tag数据。
		flag = TAG_LOAD_MORE;	
	}
	
	public class MyAnimation extends TranslateAnimation {

        private View mView;

        private LinearLayout.LayoutParams mLayoutParams;

        private int mMarginTopFromY, mMarginTopToY;

        public MyAnimation(float fromX, float toX, float fromY, float toY, View view) {
            super(fromX, toX, fromY, toY);
            mView = view;
            mLayoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            int height = mView.getHeight();
            mMarginTopFromY = (int) (height *(fromY - 1)) + mLayoutParams.topMargin;
            mMarginTopToY = (int) (height * (toY - 1)) + mLayoutParams.topMargin;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                int newMarginTop = mMarginTopFromY
                        + (int) ((mMarginTopToY - mMarginTopFromY) * interpolatedTime);
                mLayoutParams.setMargins(mLayoutParams.leftMargin, newMarginTop,
                    mLayoutParams.rightMargin, mLayoutParams.bottomMargin);
                mView.getParent().requestLayout();
            } else {
            }
        }
	}
	public void popWindowDo(){
		float height = tags.getHeight();
		if (View.VISIBLE != tags.getVisibility()){
            mDownA = new MyAnimation(0.0f, 0.0f, 1.0f, 2.0f, tags);
            mDownA.setDuration(300);
           // mDownA.setFillAfter(true);
            tags.startAnimation(mDownA);
			tags.setVisibility(View.VISIBLE);
		}
		else
			if (View.VISIBLE == tags.getVisibility()){
                mUpA = new MyAnimation(0.0f, 0.0f, 1.0f, 0.0f, tags);
                mUpA.setDuration(300);
              //  mUpA.setFillAfter(true);
                tags.startAnimation(mUpA);
				tags.setVisibility(View.INVISIBLE);
			}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	private static class ViewHolder {
	    Userinfo userinfo;
		Ad ad;
		Buttons buttons;

		ViewHolder()
		{
			this.userinfo = new Userinfo();
			this.ad = new Ad();
			this.buttons = new Buttons();
			
		}
		
		private class Userinfo{
			ImageView head;
			TextView username;
		}
		private class Ad{
			TextView price;
			TextView title;
			TextView content;
			Button voice;
			TextView voice_play;
			CustomGallery images;
		}
		private class Buttons{
			Button bt_share;
			Button bt_collect;
			Button bt_blow;
		}
	}
	
	
	
	
	private class CustomGalleryAdapter extends BaseAdapter {
		List<String> urls;
		CustomGalleryAdapter(List<String> urls){
			this.urls = urls;
		}
		@Override
		public int getCount() {
			return urls.size();
		}

		@Override
		public String getItem(int position) {
			return this.urls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_gallery_image, parent, false);
			}
			imageLoader.displayImage(getItem(position), imageView, g_options);
			return imageView;
		}
	}
	
	
	
	private class InfoAdapter extends BaseAdapter{
		private List<AdItem> list;
		private Context con;
		private LayoutInflater inflater;
		
		public InfoAdapter(Context context, List<AdItem> list) {
			this.con = context;
			this.list = list;
			inflater = LayoutInflater.from(con);
		}

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View view = convertView;
			final ViewHolder holder;
			final int pos = position;
			if (convertView == null) {
				view = inflater.inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();

				holder.ad.title = (TextView)view.findViewById(R.id.list_item_text_title);
				RelativeLayout userinfo = (RelativeLayout)view.findViewById(R.id.list_item_userinfo);
				holder.userinfo.head = (ImageView)userinfo.findViewById(R.id.list_item_image_head);
				holder.userinfo.username = (TextView)view.findViewById(R.id.list_item_text_username);
				holder.ad.price = (TextView)view.findViewById(R.id.list_item_text_price);
				holder.ad.content = (TextView)view.findViewById(R.id.list_item_text_content);
				holder.ad.voice = (Button)view.findViewById(R.id.list_item_bt_voice);
				holder.ad.voice_play = (TextView)view.findViewById(R.id.list_item_text_voice);
				
				holder.ad.images = (CustomGallery)view.findViewById(R.id.list_item_gallery_images);
				holder.buttons.bt_share = (Button)view.findViewById(R.id.list_item_bt_share);
				holder.buttons.bt_collect = (Button)view.findViewById(R.id.list_item_bt_collect);
				holder.buttons.bt_blow = (Button)view.findViewById(R.id.list_item_bt_blow);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			AdItem now = getItem(position);
			imageLoader.displayImage(now.getUserImage(), holder.userinfo.head, options, animateFirstListener);
			
			holder.userinfo.username.setText(now.getUserName());
			holder.ad.price.setText("" + now.getPrice());
			holder.ad.title.setText(now.getTitle());
			holder.ad.content.setText(now.getContent());
			holder.ad.voice.setFocusable(false);
			holder.ad.voice_play.setText("23\"");
			holder.ad.images.setFocusable(false);
			
			List<String> urls = new ArrayList<String>();
			List<String> s = now.getImages();
			if (null != s)
			{
				for (String str : s){
					urls.add(str);
				}
			}
			//为Gallery 设置图片数组。
			holder.ad.images.setAdapter(new CustomGalleryAdapter(urls){});
			
			//holder.buttons.bt_share.setText("分享");
			//holder.buttons.bt_collect.setText("收藏");
			//holder.buttons.bt_blow.setText("吹上去");
			//listView 中有button控件时，必须将子控件的focusable设置为false,其本身才能捕获到click
			holder.buttons.bt_blow.setFocusable(false);
			holder.buttons.bt_blow.setClickable(true);
			
			holder.buttons.bt_blow.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//popWindowDo();
					blowAdId = getItem(pos).getId();
					blowAdId = blowAdId.substring(1,blowAdId.length());
					blowAdItem = getItem(pos);
					//吹上去
					onBlowUp();
				
				}});
			return view;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public AdItem getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
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
	
	
	
	
	
	private void geneItems(int doWhat) {
		int num = 10;
		DownAdTask task;
		if (REFRESH == doWhat){
			task = new DownAdTask(ImageListActivity.this, 0, num - 1, null, doWhat);
		}
		else{
			int size = adlist.size();
			task = new DownAdTask(activity,size,size + num - 1,null, doWhat);
		}
	
		task.execute(wzName);
	}

	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("不久前");
	}
	
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems(REFRESH);
			}
		}, delay_time);
	}

	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems(LOAD);
			}
		}, delay_time);
	}
	
	public void onBlowUp()
	{
		//通知服务器改变顺序，接收了则改变本地的顺序，否则，不改变。
		
		DownBlowFlagTask task = new DownBlowFlagTask(this);
		task.execute(wzName);

	}
	
	public void executeBlowUp()
	{
		if(blowItem != null && blowItem.getFlag())//通知服务器改变顺序。成功的话，刷新页面。
		{	
			if(blow < blow_times)
			{	
				blow++;
				adlist.remove(blowAdItem);
				adlist.add(0,blowAdItem);
				mAdapter.notifyDataSetChanged();
			
				Toast.makeText(activity, "吹上去了", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(activity, "吹不动了,快去交朋友下载app来吹吧", Toast.LENGTH_SHORT).show();
			}
		}
		else		
		{
			Toast.makeText(activity, "吹失败啦,检查一下您的网络吧", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class DownBlowFlagTask extends AsyncTask<String,String,BlowItem>
	{
		 Context ctx;
		public DownBlowFlagTask(Context ctx)
		{
			this.ctx = ctx;
		}

		@Override
		protected BlowItem doInBackground(String... arg0) {
			try{
					if(wzName == "")
					{
						return null;
					}
					else
					{	blowItem = NetworkJson.getWeiZhanBlowFlag(ctx, wzName, blowAdId).getResult();
						return blowItem;
					}
			}catch(Exception e){
					
				e.printStackTrace();
			}
			return null;
		}
		
		  @Override  
	      protected void onPostExecute(BlowItem result) {  
			  	if(blowItem ==null ) blowItem = new BlowItem();
			  	blowItem = 	result;
			   	executeBlowUp();
	       }  
	}

	
	private void initTag()
	{
		ApiConfiguration.config("www.zhaoyiru.baixing.cn", null, "api_androidbaixing",
				"c6dd9d408c0bcbeda381d42955e08a3f", "dd", "dd", null);
		//从上一个Fragment获得微站名字。		
		//tagview = (CustomListView) mPopupWindow.getContentView().findViewById(R.id.sexangleView);	
		
		//Tag和Ad必须先初始化。
		taglist = new ArrayList<TagItem>();
		
		taglist.add(new TagItem("Tag正在加载中........"));

		tagview.setDividerHeight(3);
		tagview.setDividerWidth(20);	
		adapter = new TagAdapter(this, taglist);
		tagview.setAdapter(adapter);
		//
		wzName = "troyside";	
		
		DownTagTask task = new DownTagTask(this);
		task.execute(wzName);
	}

	private void ShowTagListView(){
		
		taglist.add(new TagItem("更多","#000000","#f5f5f5"));
		final TagAdapter adapter2 = new TagAdapter(this, taglist);
		//设置tag之间的间隔:款和高。
		tagview.setDividerHeight(10);
		tagview.setDividerWidth(30);		
		tagview.setAdapter(adapter2);
		//上次点击的tag。

		tagview.setOnItemClickListenerTag(new OnItemClickListenerTag(){

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int position, long id) {
				
				HashMap<String,String> apiParam =new HashMap<String,String>();
				
				apiParam.put("tag",taglist.get(position).getText());
				int from = 0;
				int to = 30;
				if(position == taglist.size() - 1 )
				{
					if(flag ==TAG_SHRINK_UP)
					{	
						adapter2.notifyDataSetChanged(true);
						taglist.get(position).setText("更多");
						flag =  TAG_LOAD_MORE;
					}
					else if(flag == TAG_LOAD_MORE)
					{	//为TAG_LOAD_MORE时，只显示一行tag
						adapter2.notifyDataSetChanged(false);
						taglist.get(position).setText("收起");
						flag =TAG_SHRINK_UP;
					}	
				}
				else
				{
					if(position != lastTag)
					{	
						if(lastTag != -1)
							taglist.get(lastTag).setBgColor(lastBgColor);
						lastTag = position;
						lastBgColor = taglist.get(position).getBgColor();
						//点击之后变色。
						taglist.get(position).setBgColor("#000000");
					}
					boolean flag1 = (flag== TAG_LOAD_MORE) ? true:false;
					adapter2.notifyDataSetChanged(flag1);
					HashMap<String,String> params =  taglist.get(position).getParams();
					//获取微站的ad列表。
					DownAdTask task = new DownAdTask(activity,from,to,params, 1);
					task.execute(wzName);
				}
			}});
	}
	private class DownTagTask extends AsyncTask<String,String,List<TagItem>>
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
			    	 	taglist.set(0, new TagItem("您还没有自己的标签，快去创建吧."));
			    }
			    ShowTagListView();
	       }  
	}
	private class DownAdTask extends AsyncTask<String,String,List<AdItem>>
	{
		 Context ctx;
		 int from,to;
		 HashMap<String,String>params;
		 int doWhat;
		DownAdTask(Context ctx,int from, int to,HashMap<String,String>params, int doWhat)
		{
			this.ctx = ctx;
			this.from =from;
			this.to = to ;
			this.params = params;
			this.doWhat = doWhat;
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
						return NetworkJson.getWeiZhanAdList(ctx, wzName, from, to, params).getResult();	
				
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
			    	 if (REFRESH == doWhat){ //reFresh()
			    		 adlist = result;
			    		 InfoAdapter infoAdapter = new InfoAdapter(ImageListActivity.this, adlist);
			    		 mAdapter = infoAdapter;
			    		 listView.setAdapter(infoAdapter);
			    	 }else{
			    		 for (AdItem i:result){
			    			 adlist.add(i);
			    			 mAdapter.notifyDataSetChanged();
			    		 }
			    	 }
			     }
			     else
			     {
			    	 if (REFRESH == doWhat){
			    		 adlist.clear();
			    		 adlist.add(new AdItem("您还没有自己的广告，快去添加吧"));
			    	 }
			    	 else{
			    		 
			    	 }
			     }
			     onLoad();
	        }  
		}
	

}