package com.custom.vg.list;
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
	private boolean flag; //防止多显示一次更多按钮。
    public CustomListView(Context context, AttributeSet attrs) 
	{ 
		super(context, attrs); 
		loadMoreFlag = true;
		flag =true;
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
			if(!flag)
				flag =true;
			else
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
				flag =false;
			 }
		 }
         ViewGroup.LayoutParams lp = getLayoutParams();
	     if(loadMoreFlag)//loadMoreFlag为true时只显示一行的tag.
			lp.height = 55;
		 else
			lp.height = lengthY + 10;
			
   		 setLayoutParams(lp);
   			
   		if (isAddChildType()) 
   		{
   			new Thread(new RefreshCustomThread()).start();
   		}
  }
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
 {
 int width = View.MeasureSpec.getSize(widthMeasureSpec);
   int height = View.MeasureSpec.getSize(heightMeasureSpec);
 setMeasuredDimension(width, height);
  
 for (int i = 0; i < getChildCount(); i++) {
  View child = getChildAt(i);
     child.measure(0, 0);
 }
   super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 }

  static final boolean isAddChildType()
  {
    return addChildType;
  }
 
 public static void setAddChildType(boolean addChildType2) {
   addChildType = addChildType2;
  }
  
 private int dividerHeight = 0;
  
 final int getDividerHeight() {
  return this.dividerHeight;
 }
 
 public void setDividerHeight(int dividerHeight) {
   this.dividerHeight = dividerHeight;
 }

 private int dividerWidth = 0;
  
final int getDividerWidth() {
  return this.dividerWidth;
 }
  
 public void setDividerWidth(int dividerWidth) {
   this.dividerWidth = dividerWidth;
  }
  
 public void setAdapter(CustomAdapter adapter) {
    this.myCustomAdapter = adapter;
    setAddChildType(true);
   adapter.notifyCustomListView(this);
  }
public void setOnItemClickListenerTag(OnItemClickListenerTag listener)
{ if(listener ==null)
   this.myCustomAdapter.setOnItemClickListenerTag(null);
				else
 this.myCustomAdapter.setOnItemClickListenerTag(listener);
 }
public void setOnItemLongClickListenerTag(OnItemLongClickListenerTag listener)
 {
  this.myCustomAdapter.setOnItemLongClickListenerTag(listener);
 }

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
			private final void sendMsgHanlder(android.os.Handler handler, Bundle data) {
/* 163:163 */    Message msg = handler.obtainMessage();
/* 164:164 */    msg.setData(data);
/* 165:165 */    handler.sendMessage(msg);
/* 166:    */  }
/* 167:    */}

