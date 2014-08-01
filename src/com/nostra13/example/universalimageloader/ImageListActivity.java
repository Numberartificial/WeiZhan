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

import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.util.DensityUtil;
import com.baixing.network.api.ApiConfiguration;
import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListenerTag;
import com.custom.vg.list.OnItemLongClickListenerTag;
import com.example.dsfwe.AdItem;
import com.example.dsfwe.BlowItem;
import com.example.dsfwe.NetworkJson;
import com.example.dsfwe.TagAdapter;
import com.example.dsfwe.TagItem;
import com.example.yuyin.BlowDialog;
import com.example.yuyin.NetworkState;
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

import me.maxwin.view.XListView;
import me.maxwin.view.XListViewHeader;
import me.maxwin.view.XListView.IXListViewListener;


public class ImageListActivity extends ListViewBaseActivity{

	DisplayImageOptions options;
	DisplayImageOptions g_options;
	String[] imageUrls;
	
	private int TITLE_HEIGHT = 52;

	private  final static int REFRESH =1;
	private  final static int LOAD =2;
	private InfoAdapter mAdapter;
	private List<AdItem> adlist; 
	private RelativeLayout mainView;
	private LinearLayout mContentView;
	private LinearLayout mTitleView;
	
	private static int delay_time = 0;
	private Handler mHandler;
	
	LinearLayout coverView;
	
	private RelativeLayout.LayoutParams mLayoutParams;
	private int mMargin;
	private LinearLayout tags;
	private CustomListView tagview;
	private List<TagItem> taglist = null;
	private TagAdapter adapter;
	private String wzName;
	private int showTags = -1;
	private int tagShow = 1;
	
	private Animation mDownA;
	private Animation mUpA;
	private RelativeLayout ani_handle;
	private RelativeLayout mRe;
	
	private float mLastY = -1;
	private int ani_images[];
	private float ani_time = 0.25f;
	private boolean ani_show = false;
	
	//tag
	private int flag;
	private final int TAG_LOAD_MORE = 0;
	private final int TAG_SHRINK_UP = 1;
	private int lastTag = -1;
	private String lastBgColor;
	private boolean network_state_ok = false;

	Context activity = this;
	
	
	private BlowItem blowItem;
	private int blow_times = 0;
	private String blowAdId = null;
	private AdItem blowAdItem = null;
	private int BLOW_FLAG=0;

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
	
		tags = (LinearLayout)findViewById(R.id.list_tags);
		tags.setVisibility(View.VISIBLE);
		tagview = (CustomListView)tags.findViewById(R.id.list_sexangleView);	
		initTag();
		
		adlist = new ArrayList<AdItem>();
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
			
			@Override
			public boolean onTouch(MotionEvent ev){
				return ImageListActivity.this.onTouch(ev);
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
		blowItem = new BlowItem();
		network_state_ok = NetworkState.checkNetworkInfo(activity);
		
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		mainView = (RelativeLayout) this.findViewById(R.id.list_main);
		mContentView = (LinearLayout)mainView.findViewById(R.id.list_content);
		mTitleView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.ac_image_list_title, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		//lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.topMargin = 0;
		//lp.addRule(RelativeLayout.ABOVE, R.id.list_content);
		/*int h = content.getHeight() + now.getHeight() - wm.getDefaultDisplay().getHeight();
		lp.bottomMargin += h;*/
		RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
		//lp1.addRule(RelativeLayout.BELOW, R.id.list_view_title);
		mTitleView.setLayoutParams(lp);
		mRe = (RelativeLayout)mTitleView.findViewById(R.id.list_title);
		Button bt = (Button)mRe.findViewById(R.id.list_bt_notify);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popWindowDo();
			}});
		bt = (Button)mRe.findViewById(R.id.list_bt_name);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startSearchActivity();
			}});
		TextView text = (TextView)mRe.findViewById(R.id.list_text_title_name);
		text.setText("微站名");
		text.setClickable(true);
		text.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startSearchActivity();
			}});
		coverView = (LinearLayout)findViewById(R.id.ac_image_cover);
		
		//添加handle
		ani_handle = new RelativeLayout(this);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		rl.topMargin = DensityUtil.dip2px(this, 52);;
		rl.leftMargin = DensityUtil.dip2px(this, 200);
		ani_handle.setLayoutParams(rl);
		ani_handle.setBackgroundResource(R.drawable.btn_handler);
		ani_handle.setClickable(true);
		ani_handle.setFocusable(false);
		ani_images = new int[2];
		ani_images[1] = R.drawable.btn_handler_on1;
		ani_images[0] = R.drawable.btn_handler_on0;
		ani_handle.setOnTouchListener(new OnTouchListener(){     
		     
			   int lastX,lastY;  
			           
			         @Override  
			   public boolean onTouch(View v, MotionEvent event) {  
			    // TODO Auto-generated method stub  
			          int ea=event.getAction();  
			            
			          switch(ea){  
			          case MotionEvent.ACTION_DOWN:             
			             
			           lastX=(int)event.getRawX();//获取触摸事件触摸位置的原始X坐标  
			           lastY=(int)event.getRawY();             
			           break;  
			           
			          case MotionEvent.ACTION_MOVE:  
			           int dx=(int)event.getRawX()-lastX;  
			           int dy=(int)event.getRawY()-lastY;             

			           RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams)v.getLayoutParams();
			           rl.leftMargin += dx;    
			           rl.topMargin += dy;
			           mainView.requestLayout();
			           lastX=(int)event.getRawX();  
			           lastY=(int)event.getRawY();  
			           break;  
			          case MotionEvent.ACTION_UP:  
			           break;            
			          }  
			    return false;  
			   }});  
		ani_handle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tagShow == 0 && showTags == -1){
					showTags = 1;
					v.setBackgroundResource(ani_images[1]);
					ani_show = true;
					popWindowDo();
					return;
				}
				if (tagShow == 1 && showTags == -1){
					showTags = 0;
					v.setBackgroundResource(ani_images[0]);
					ani_show = true;
					popWindowDo();
					return;
				}
			}
		});
		mainView.addView(ani_handle);
		mainView.addView(mTitleView);
		
		lp1.topMargin=DensityUtil.dip2px(this, 52);
		mLayoutParams = (RelativeLayout.LayoutParams) mContentView.getLayoutParams();
		// 记录下正常显示时tagsView的BottomMargin
		mMargin = lp1.topMargin;
	}
	
	
	public class MyAnimation extends Animation {
        private int y;
        private int lastAdd;
        private int show;
        private int next;
        private float nextT;
        
        public MyAnimation(int toY, int show) {
            y = toY;
            lastAdd = 0;
            this.show = show;
            next = 1 - show;
            nextT = ani_time;
        }

        @SuppressLint("NewApi") @Override
        protected void applyTransformation(float interpolatedTime, Transformation tran) {
            if (interpolatedTime < 1.0f) {

            	if (ani_show && interpolatedTime > nextT){
            		ani_handle.setBackgroundResource(ani_images[next]);
            		next = 1 - next;
            		nextT += ani_time;
            	}
                int now = (int)(interpolatedTime * y);
            	/*mLayoutParams.setMargins(mLayoutParams.leftMargin, mLayoutParams.topMargin,
                        mLayoutParams.rightMargin, mLayoutParams.bottomMargin + (now - lastAdd));*/
                mLayoutParams.topMargin += now - lastAdd;
                lastAdd = now;
            	mainView.requestLayout();
            } else {
            	if (show == 1){
                    showTags = -1;
                    tagShow = 1;
            	}else{
                    showTags = -1;
                    tagShow = 0;
            	}

            	ani_show = false;
                ani_handle.setBackgroundResource(R.drawable.btn_handler);
            }
        }
	}
	public void popWindowDo(){
		int offset = DensityUtil.dip2px(this, 5);
		int height;
		int scale = 2;
		if (showTags == 1){
    		//tagview.setVisibility(View.VISIBLE);
			height = mMargin - mLayoutParams.topMargin; 
            mDownA = new MyAnimation(height, showTags); 
            mDownA.setDuration(height * scale);
            mainView.startAnimation(mDownA);
            showTags = -1;
            tagShow = 1;
		}
		else
		if (showTags == 0){
			    height = mMargin - mLayoutParams.topMargin;
			    height = height - coverView.getHeight() - offset;
                mUpA = new MyAnimation(height, showTags); 
                mUpA.setDuration(- height * scale);
                mainView.startAnimation(mUpA);
                showTags = -1;
                tagShow = 0;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent intent)
	{
		if(requestCode == BLOW_FLAG)
		{
			//Bundle data = intent.getExtras();
			//int 
			int data = intent.getIntExtra("lich", 0);
			if(data>0)
			{
				//Toast.makeText(activity, "您的吹力是：" + data,Toast.LENGTH_SHORT).show();
				onBlowUp();
			}
			
		}
		
		
	}
	private void startImagePagerActivity(int position, List<String> urls) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		imageUrls = (String[])urls.toArray(new String[0]);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}
	
	private void startSearchActivity() {
		Intent intent = new Intent(this, SearchActivity.class);
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
			LinearLayout image_scroll;
		}
		private class Buttons{
			RelativeLayout bt_share;
			RelativeLayout bt_collect;
			RelativeLayout bt_blow;
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
				holder.ad.image_scroll = (LinearLayout)view.findViewById(R.id.image_scroll_add);
				holder.buttons.bt_share = (RelativeLayout)view.findViewById(R.id.list_item_bt_share);
				holder.buttons.bt_collect = (RelativeLayout)view.findViewById(R.id.list_item_bt_collect);
				holder.buttons.bt_blow = (RelativeLayout)view.findViewById(R.id.list_item_bt_blow);
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
			//很重要
			holder.ad.image_scroll.removeAllViews();
			//List<String> urls = new ArrayList<String>();
			List<String> s = now.getImages();
			if (null != s)
			{
				for (int i = 0; i < s.size(); i++){
			    	ImageView iView = new ImageView(ImageListActivity.this);
					imageLoader.displayImage(s.get(i), iView, g_options);
					int pxW = DensityUtil.dip2px(ImageListActivity.this, 100);
					int pxH = DensityUtil.dip2px(ImageListActivity.this, 100);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pxW, pxH);
					lp.leftMargin = DensityUtil.dip2px(ImageListActivity.this, 6);
					lp.rightMargin = DensityUtil.dip2px(ImageListActivity.this, 6);
					iView.setId(i);//设置这个View 的id 
					iView.setTag(now.getImagesHD());
					iView.setLayoutParams(lp);
					iView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							startImagePagerActivity(v.getId(), (List<String>)v.getTag());
						}});
					holder.ad.image_scroll.addView(iView);
				}
			}else{
			}
			//listView 中有button控件时，必须将子控件的focusable设置为false,其本身才能捕获到click
			holder.buttons.bt_share.setClickable(true);
			holder.buttons.bt_blow.setFocusable(false);
			holder.buttons.bt_blow.setClickable(true);
			holder.buttons.bt_blow.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//popWindowDo();
					//popWindowDo();
					blowAdId = getItem(pos).getId();
					if(blowAdId!=null)
						blowAdId = blowAdId.substring(1,blowAdId.length());
					blowAdItem = getItem(pos);

				  if(blow_times<1){    
					  	//启动吹的功能模块。
		                Intent i = new Intent(activity, BlowDialog.class);  
		                startActivityForResult(i,BLOW_FLAG);  
					  
				  }
				  else
				  {
					  executeBlowUp();
				  }
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
	public void onBlowUp()
	{

		DownBlowFlagTask task = new DownBlowFlagTask(activity);
		task.execute(wzName);
	}
	public void executeBlowUp()
	{
		if(blowItem!=null && blowItem.getFlag())
		{
			if(blow_times < 1)
			{	
				blow_times++;
				adlist.remove(blowAdItem);
				adlist.add(0,blowAdItem);
				mAdapter.notifyDataSetChanged();
				//Toast.makeText(activity, "吹上去了，", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(activity, "气用完了，还想吹？快邀请好友下载App来吹吧", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(activity, "吹失败了，网络错误", Toast.LENGTH_SHORT).show();
		}
		
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
	
	public boolean onTouch(MotionEvent ev){
		boolean isFinished = false;
		if (mLastY == -1) {
			mLastY = ev.getY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getY() - mLastY;
			final int h = coverView.getHeight();
			if (deltaY > 0 && mLayoutParams.topMargin < mMargin){
				if (tagShow == 0 && listView.getFirstVisiblePosition() != 0){
					break;
				}
					isFinished = true;
					showTags = 1;
					mLayoutParams.topMargin += deltaY;
					if (mLayoutParams.topMargin > mMargin){
						mLayoutParams.topMargin = mMargin;
					}
					mainView.requestLayout();
					break;
			}
			if (deltaY < 0 && (mMargin - mLayoutParams.topMargin < h)){
					isFinished = true;
					showTags = 0;
					mLayoutParams.topMargin += deltaY;
					if (mMargin - mLayoutParams.topMargin >= h){
						mLayoutParams.topMargin = mMargin - h; 
					}
					mainView.requestLayout();
					break;
			}
			mLastY = ev.getY();
			break;
		case MotionEvent.ACTION_UP: 
			mLastY = -1; // reset
			popWindowDo();
			break;
		}
		if (showTags != -1){
			isFinished = true;
		}
		return isFinished;
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
					{	
					    blowItem= NetworkJson.getWeiZhanBlowFlag(ctx, wzName,blowAdId).getResult();	
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
			  if(result != null)
			    {	
			    	 blowItem = result;
			    	 executeBlowUp();
			    
			    }
			    else
			    {
			    	 	//taglist.set(0, new TagItem("您还没有自己的标签，快去创建吧."));
			    	Toast.makeText(activity, "网络连接失败啦,请检查网络", Toast.LENGTH_SHORT).show();
			    }
			    //ShowTagListView();
	       }  
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