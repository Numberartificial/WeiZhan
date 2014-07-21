package com.example.dsfwe;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.custom.vg.list.CustomAdapter;
import com.nostra13.example.universalimageloader.R;

public class TagAdapter extends CustomAdapter {
	
	private List<TagItem> list;
	private Context con;
	private LayoutInflater inflater;
    
	public TagAdapter(Context context, List<TagItem> list) {
		this.con = context;
		this.list = list;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("Lich","getView");
		ViewHolder vh = null;
		if(convertView == null){
			vh = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.tag_item_layout, null);
			vh.tv = (TextView) convertView.findViewById(R.id.adapter_text);			
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		TagItem tag = list.get(position);
		vh.tv.setText(tag.getText());
		vh.tv.setBackgroundColor( Color.parseColor(tag.getBgColor()));
		vh.tv.setTextColor( Color.parseColor(tag.getColor()));

		return convertView;
	}
	
	public class ViewHolder{
		public TextView tv;
	}

}
