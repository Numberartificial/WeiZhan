package com.example.dsfwe;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.custom.vg.list.CustomAdapter;
import com.nostra13.example.universalimageloader.R;

public class TagAdapter extends CustomAdapter {
	
	private List<TagItem> list;
	private Context con;
	private LayoutInflater inflater;
    private int flag;
	public TagAdapter(Context context, List<TagItem> list) {
		this.con = context;
		this.list = list;
		inflater = LayoutInflater.from(con);
		flag = 1;
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
//		if(position == list.size() -1 && (tag.getText() == "收起"))
//		{	
//			SpannableStringBuilder sb = new SpannableStringBuilder();
//			ImageSpan imgspan = new ImageSpan(con,R.drawable.btn_downarrow);
//			SpannableString ss = new SpannableString("More..");
//			ss.setSpan(imgspan, ss.length() -1, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//			sb.append(ss);
//			vh.tv.setText(sb);
//		}
//		else
//		{	
			vh.tv.setText(tag.getText());
			vh.tv.setBackgroundColor( Color.parseColor(tag.getBgColor()));
			if(tag.getText() != "收起"&&tag.getText() != "更多")
				vh.tv.setTextColor( Color.parseColor(tag.getColor()));
//		}
		return convertView;
	}
	
	public class ViewHolder{
		public TextView tv;
	}

}
