
package com.nostra13.example.universalimageloader;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.util.DensityUtil;

public class ImagePagerActivity extends BaseActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;

	ViewPager pager;
	
	List<View> views = new ArrayList<View>();
	boolean[]	isLoaded;
	private String[] imageUrls;
	
	private LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐藏标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_image_pager);
		TextView tx = (TextView)findViewById(R.id.search_item_text);
		tx = (TextView)findViewById(R.id.search_text_title_name);
		tx.setText("大图浏览");
		RelativeLayout back = (RelativeLayout)findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener(){  
            public void onClick(View source)  
            {  
                finish();  
            }  
        });  

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		imageUrls = bundle.getStringArray(Extra.IMAGES);
		int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.NONE)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

		inflater = getLayoutInflater();
		initViews();
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);

		//initViews(imageUrls.length);
	}

	
	public void initViews(){
		int size = imageUrls.length;
		isLoaded = new boolean[size];
		for(int i = 0; i < size;i++){
			isLoaded[i] = false;
		}
		for(int i = 0; i < size;i++){
			FrameLayout imageLayout = (FrameLayout)inflater.inflate(R.layout.item_pager_image, pager, false);
			assert imageLayout != null;
			TextView tx = (TextView) imageLayout.findViewById(R.id.page_num);
	        String s = (i + 1) + "/" + size; 
			tx.setText(s);
			tx.setTextSize(14);
			
			views.add(imageLayout);
		}
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;

		ImagePagerAdapter(String[] images) {
			this.images = images;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			
			if (!isLoaded[position]){
				FrameLayout imageLayout = (FrameLayout) views.get(position);
				ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
				final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

				imageLoader.displayImage(imageUrls[position], imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
						}
						Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});
				isLoaded[position] = true;
			}
			view.addView(views.get(position), 0);
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}