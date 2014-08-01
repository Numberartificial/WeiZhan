package com.example.yuyin;

import android.content.Context;
import android.net.ConnectivityManager;
import   android.net.NetworkInfo.State;
import android.net.ConnectivityManager;
public class NetworkState {

	public static boolean checkNetworkInfo(Context con)
    {
        ConnectivityManager conMan = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);

        //mobile 3G Data Network
        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        //wifi
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if((mobile==State.CONNECTED)||(wifi==State.CONNECTED))
        	return true;
        else
        	return false;
    }
}
