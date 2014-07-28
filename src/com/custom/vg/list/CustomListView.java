package com.custom.vg.list;
import com.nostra13.example.universalimageloader.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
public class CustomListView extends RelativeLayout
{
	private String TAG = CustomListView.class.getSimpleName();
	private CustomAdapter myCustomAdapter;
	private static boolean addChildType;
	private static boolean  loadMoreFlag;
    public CustomListView(Context context, AttributeSet attrs) 
	{ 
		super(context, attrs); 
		//默认只显示一行。
		loadMoreFlag = true;
	}

	public static void setSLoadMoreFlag(boolean flag)
	{
					loadMoreFlag = flag;					
	}
	protected void onLayout(boolean arg0, int argLeft, int argTop, int argRight, int argBottom)
	{
		int count = getChildCount();
		int lengthX = 0;
		int lengthY = 0;
		int width=0;
		int height=0;
		View child=null;
		if(loadMoreFlag)
		{	 
	
			View more_tag = getChildAt(count-1);
			int more_width = more_tag.getMeasuredWidth();
			int more_height = more_tag.getMeasuredHeight();
			for (int i = 0; i < count; i++)
			{
				 child = getChildAt(i);
				 width = child.getMeasuredWidth();
				 height = child.getMeasuredHeight();
				 //对于第一个tag的宽度的处理。
				 if (lengthX == 0) 
				 {
					lengthX += width;
				 } 
				else//其他tag宽度要加上tag之间的间隔。
				{
					lengthX += width + getDividerWidth();
				}
				//第一行的tag的高度。
				if ((i == 0) && (lengthX <= argRight - more_width -getDividerWidth())) 
				{
					lengthY += height;
				}
						
				if (lengthX > argRight - more_width - getDividerWidth())
				{	
					break;
				}
				else 
				{
					child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
				}
			}
			more_tag.layout(argRight - more_width, argBottom - more_height, argRight, argBottom);
	    }	
		else
		{
			lengthX = 0;
			lengthY = 0;	
			for (int i = 0; i < count; i++)
			{
				 child = getChildAt(i);
				 width = child.getMeasuredWidth();
				 height = child.getMeasuredHeight();
				 //对于第一个tag的宽度的处理。
				 if (lengthX == 0) 
				 {
					lengthX += width;
				 } 
				 else
				 {
					  lengthX += width + getDividerWidth();
				 }
				 if ((i == 0) && (lengthX <= argRight)) 
				 {
					lengthY += height;
				 }
				if (lengthX > argRight)
				{		
						lengthX = width;
						lengthY += getDividerHeight() + height;
						child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
				}
				else 
				{
						child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
				}
			 }
		 }
         ViewGroup.LayoutParams lp = getLayoutParams();
         
	     if(loadMoreFlag)//loadMoreFlag为true时只显示一行的tag.
			lp.height = 50;
		 else
			lp.height = lengthY + 10;
			
   		 setLayoutParams(lp);
   			
   		if (isAddChildType()) 
   		{
   			new Thread(new RefreshCustomThread()).start();
   		}
  }
/*  63:    */  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
/*  64:    */  {
/*  65: 65 */    int width = View.MeasureSpec.getSize(widthMeasureSpec);
/*  66: 66 */    int height = View.MeasureSpec.getSize(heightMeasureSpec);
/*  67: 67 */    setMeasuredDimension(width, height);
/*  68:    */    
/*  69: 69 */    for (int i = 0; i < getChildCount(); i++) {
/*  70: 70 */     View child = getChildAt(i);
/*  71: 71 */      child.measure(0, 0);
/*  72:    */    }
/*  73: 73 */    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
/*  74:    */  }
/*  75:    */  
/*  77:    */  static final boolean isAddChildType()
/*  78:    */  {
/*  79: 79 */    return addChildType;
/*  80:    */  }
/*  81:    */  
/*  82:    */  public static void setAddChildType(boolean addChildType2) {
/*  83: 83 */    addChildType = addChildType2;
/*  84:    */  }
/*  85:    */  
/*  86: 86 */  private int dividerHeight = 0;
/*  87:    */  
/*  88:    */  final int getDividerHeight() {
/*  89: 89 */    return this.dividerHeight;
/*  90:    */  }
/*  91:    */  
/*  92:    */  public void setDividerHeight(int dividerHeight) {
/*  93: 93 */    this.dividerHeight = dividerHeight;
/*  94:    */  }
/*  95:    */  
/*  96: 96 */  private int dividerWidth = 0;
/*  97:    */  
/*  98:    */  final int getDividerWidth() {
/*  99: 99 */    return this.dividerWidth;
/* 100:    */  }
/* 101:    */  
/* 102:    */  public void setDividerWidth(int dividerWidth) {
/* 103:103 */    this.dividerWidth = dividerWidth;
/* 104:    */  }
/* 105:    */  
/* 106:    */  public void setAdapter(CustomAdapter adapter) {
/* 107:107 */    this.myCustomAdapter = adapter;
/* 108:108 */    setAddChildType(true);
/* 109:109 */    adapter.notifyCustomListView(this);
/* 110:    */  }
/* 111:    */  
/* 115:    */  public void setOnItemClickListenerTag(OnItemClickListenerTag listener)
/* 116:    */  { if(listener ==null)
	/* 117:117 */    this.myCustomAdapter.setOnItemClickListenerTag(null);
				else
/* 117:117 */    this.myCustomAdapter.setOnItemClickListenerTag(listener);
/* 118:    */  }
/* 119:    */  
/* 124:    */  public void setOnItemLongClickListenerTag(OnItemLongClickListenerTag listener)
/* 125:    */  {
/* 126:126 */    this.myCustomAdapter.setOnItemLongClickListenerTag(listener);
/* 127:    */  }
/* 128:    */  
/* 129:129 */  private final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper())
/* 130:    */  {
/* 131:    */    public void handleMessage(Message msg)
/* 132:    */    {
/* 133:133 */      super.handleMessage(msg);
/* 134:    */      try {
/* 135:135 */        if (msg.getData().containsKey("getRefreshThreadHandler")) {
/* 136:136 */          CustomListView.setAddChildType(false);
/* 137:137 */          CustomListView.this.myCustomAdapter.notifyCustomListView(CustomListView.this);
/* 138:    */        }
/* 139:    */      } catch (Exception e) {
/* 140:140 */        Log.w(CustomListView.this.TAG, e);
/* 141:    */      }
/* 142:    */    }
/* 143:    */  };
/* 144:    */  
/* 145:    */  private final class RefreshCustomThread implements Runnable
/* 146:    */  {
/* 147:    */    private RefreshCustomThread() {}
/* 148:    */    
/* 149:    */    public void run() {
/* 150:150 */      Bundle b = new Bundle();
/* 151:    */      try {
/* 152:152 */        Thread.sleep(50L);
/* 153:    */      }
/* 154:    */      catch (Exception localException) {}finally
/* 155:    */      {
/* 156:156 */        b.putBoolean("getRefreshThreadHandler", true);
/* 157:157 */        CustomListView.this.sendMsgHanlder(CustomListView.this.handler, b);
/* 158:    */      }
/* 159:    */    }
/* 160:    */  }
/* 161:    */  
/* 162:    */  private final void sendMsgHanlder(android.os.Handler handler, Bundle data) {
/* 163:163 */    Message msg = handler.obtainMessage();
/* 164:164 */    msg.setData(data);
/* 165:165 */    handler.sendMessage(msg);
/* 166:    */  }
/* 167:    */}

