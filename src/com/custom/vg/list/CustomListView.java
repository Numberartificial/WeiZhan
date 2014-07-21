/*   1:    */package com.custom.vg.list;
/*   2:    */
/*   3:    */import android.content.Context;
/*   4:    */import android.os.Bundle;
/*   5:    */import android.os.Looper;
/*   6:    */import android.os.Message;
/*   7:    */import android.util.AttributeSet;
/*   8:    */import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
/*  12:    */
/*  13:    */public class CustomListView extends LinearLayout
/*  14:    */{
/*  15: 15 */  private String TAG = CustomListView.class.getSimpleName();
/*  16:    */  private CustomAdapter myCustomAdapter;
/*  17:    */  private static boolean addChildType;
/*  18:    */  
/*  19: 19 */  public CustomListView(Context context, AttributeSet attrs) { super(context, attrs); }
/*  20:    */  
/*  22:    */  protected void onLayout(boolean arg0, int argLeft, int argTop, int argRight, int argBottom)
/*  23:    */  {
/*  24: 24 */    Log.i(this.TAG, "L:" + argLeft + " T:" + argTop + " R:" + argRight + " B:" + argBottom);
/*  25: 25 */    int count = getChildCount();
/*  27: 27 */    int lengthX = 0;
/*  28: 28 */    int lengthY = 0;
/*  29: 29 */    for (int i = 0; i < count; i++)
/*  30:    */    {
/*  31: 31 */      android.view.View child = getChildAt(i);
/*  32: 32 */      int width = child.getMeasuredWidth();
/*  33: 33 */      int height = child.getMeasuredHeight();
/*  34:    */      
/*  35: 35 */      if (lengthX == 0) {
/*  36: 36 */        lengthX += width;
/*  37:    */      } else {
/*  38: 38 */        lengthX += width + getDividerWidth();
/*  39:    */      }
/*  40:    */      
/*  41: 41 */      if ((i == 0) && (lengthX <= argRight)) {
/*  42: 42 */        lengthY += height;
/*  43:    */      }
/*  44:    */      
/*  45: 45 */      if (lengthX > argRight) {
/*  46: 46 */        lengthX = width;
/*  47: 47 */        lengthY += getDividerHeight() + height;
/*  49: 49 */        child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
/*  50:    */      } else {
/*  51: 51 */        child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
/*  52:    */      }
/*  53:    */    }
/*  54: 54 */    ViewGroup.LayoutParams lp = getLayoutParams();
/*  55: 55 */    lp.height = lengthY;
/*  56: 56 */    setLayoutParams(lp);
/*  57: 57 */    if (isAddChildType()) {
/*  58: 58 */      new Thread(new RefreshCustomThread()).start();
/*  59:    */    }
/*  60:    */  }
/*  61:    */  
/*  63:    */  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
/*  64:    */  {
/*  65: 65 */    int width = View.MeasureSpec.getSize(widthMeasureSpec);
/*  66: 66 */    int height = View.MeasureSpec.getSize(heightMeasureSpec);
/*  67: 67 */    setMeasuredDimension(width, height);
/*  68:    */    
/*  69: 69 */    for (int i = 0; i < getChildCount(); i++) {
/*  70: 70 */      android.view.View child = getChildAt(i);
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

