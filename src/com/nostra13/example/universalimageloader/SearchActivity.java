package com.nostra13.example.universalimageloader;

import com.util.DensityUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_search);

		//Bundle bundle = getIntent().getExtras();
		//imageUrls = bundle.getStringArray(Extra.IMAGES);


		LinearLayout scroll = (LinearLayout) findViewById(R.id.search_scroll_add);
		for (int i = 0; i < 20; i++){
	    	RelativeLayout item = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.search_edit_item, null, false);
	    	int pxH = DensityUtil.dip2px(this, 52);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,pxH);
			lp.leftMargin = DensityUtil.dip2px(this, 16);
			lp.rightMargin = DensityUtil.dip2px(this, 16);
			item.setId(i);//设置这个View 的id 
			item.setLayoutParams(lp);
			TextView tx = (TextView) item.findViewById(R.id.search_scroll_text);
			RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams)tx.getLayoutParams();
			lp1.leftMargin = DensityUtil.dip2px(this, 4);
			lp1.addRule(RelativeLayout.CENTER_VERTICAL);
			tx.setLayoutParams(lp1);
			scroll.addView(item);
		}
	}

	private void startChooseActivity(int position) {
		/*Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);*/
	}

}