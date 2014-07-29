/*   1:    */package com.custom.vg.list;
/*   4:    */import android.view.View;
/*   7:    */import android.view.ViewGroup;
/*   8:    */
/*   9:    */public class CustomAdapter
/*  10:    */{
/*  12:    */  private View myView;
/*  13:    */  private ViewGroup myViewGroup;
/*  14:    */  private CustomListView myCustomListView;
/*  15:    */  private OnItemClickListenerTag listener;
/*  16:    */  private OnItemLongClickListenerTag longListener;
/*  17:    */  
/*  18:    */  public int getCount() {
/*  19: 19 */    return 0;
/*  20:    */  }
/*  21:    */  
/*  22:    */  public Object getItem(int position) {
/*  23: 23 */    return null;
/*  24:    */  }
/*  25:    */  
/*  26:    */  public long getItemId(int position) {
/*  27: 27 */    return 0L;
/*  28:    */  }
/*  29:    */  
/*  30:    */  public View getView(int position, View convertView, ViewGroup parent) {
/*  31: 31 */    return null;
/*  32:    */  }
/*  33:    */  
/*  42:    */  private final void getAllViewAddSexangle()
/*  43:    */  {
/*  44: 44 */    this.myCustomListView.removeAllViews();
/*  45: 45 */    for (int i = 0; i < getCount(); i++) {
/*  46: 46 */      View viewItem = getView(i, this.myView, this.myViewGroup);
/*  47: 47 */      this.myCustomListView.addView(viewItem, i);
/*  48:    */    }
/*  49:    */  }
/*  50:    */  
/*  58:    */  public void notifyDataSetChanged(boolean loadMoreFlag)
/*  59:    */  {
/*  60: 60 */    CustomListView.setAddChildType(true);
				 CustomListView.setSLoadMoreFlag(loadMoreFlag);
/*  61: 61 */    notifyCustomListView(this.myCustomListView);
/*  62:    */  }
/*  63:    */  
/*  73:    */  public void notifyCustomListView(CustomListView formateList)
/*  74:    */  {
/*  75: 75 */    this.myCustomListView = formateList;
/*  76: 76 */    this.myCustomListView.removeAllViews();
/*  77: 77 */    getAllViewAddSexangle();
/*  78: 78 */    setOnItemClickListenerTag(this.listener);
/*  79: 79 */    setOnItemLongClickListenerTag(this.longListener);
/*  80:    */  }
/*  81:    */  
/*  86:    */  public void setOnItemClickListenerTag(final OnItemClickListenerTag listener)
/*  87:    */  {
				if(listener !=null)
				{	
/*  88: 88 */    this.listener = listener;
/*  89: 89 */    for (int i = 0; i < this.myCustomListView.getChildCount(); i++) {
/*  90: 90 */      final int parame = i;
/*  91: 91 */      View view = this.myCustomListView.getChildAt(i);
/*  92: 92 */      view.setOnClickListener(new View.OnClickListener()
/*  93:    */      {
/*  94:    */        public void onClick(View v)
/*  95:    */        {
/*  97: 97 */          listener.onItemClick(null, v, parame, CustomAdapter.this.getCount());
/*  98:    */        }
/*  99:    */      });
/* 100:    */    }
				}
/* 101:    */  }
/* 102:    */  
/* 107:    */  public void setOnItemLongClickListenerTag(final OnItemLongClickListenerTag listener)
/* 108:    */  {
/* 109:109 */    this.longListener = listener;
/* 110:110 */    for (int i = 0; i < this.myCustomListView.getChildCount(); i++) {
/* 111:111 */      final int parame = i;
/* 112:112 */      View view = this.myCustomListView.getChildAt(i);
/* 113:113 */      view.setOnLongClickListener(new View.OnLongClickListener()
/* 114:    */      {
/* 115:    */        public boolean onLongClick(View v)
/* 116:    */        {
/* 117:117 */          listener.onItemLongClick(null, v, parame, CustomAdapter.this.getCount());
/* 118:118 */          return true;
/* 119:    */        }
/* 120:    */      });
/* 121:    */    }
/* 122:    */  }
/* 123:    */}

