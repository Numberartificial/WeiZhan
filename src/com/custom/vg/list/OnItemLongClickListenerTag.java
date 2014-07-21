package com.custom.vg.list;

import android.view.View;
import android.widget.AdapterView;

public abstract interface OnItemLongClickListenerTag
{
  public abstract boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
}

