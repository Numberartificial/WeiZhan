package com.example.dsfwe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.TypeReference;
import com.baixing.network.api.ApiParams;
import com.baixing.network.api.BaseApiCommand;

import android.content.Context;



public class NetworkJson {
	
	
	  
	  public static ApiResult<List<TagItem>>  getWeiZhanTagList(Context ctx, String wzName) {
		      ApiParams params = new ApiParams();
		      params.addParam("wz", wzName);
		      params.addParam("apiFormatter", "WeiZhanTag");
		  	  String json = BaseApiCommand.createCommand("weizhan.tags/", true, params).executeSync(ctx);
		       
//		  	  InputStreamReader inputReader;
//		  	  String json = null;
//			  try {
//				  inputReader = new InputStreamReader(ctx.getResources().getAssets().open("tagjson2.txt"));
//			  	  json =	new BufferedReader(inputReader).readLine();
//	
//				} catch (IOException e) {
//				e.printStackTrace();
//			 }
		      return IOUtil.json2Obj(json, new TypeReference<ApiResult<List<TagItem>>>(){});

	  }

	  public static ApiResult<List<AdItem>>  getWeiZhanAdList(Context ctx, String wzName,int from,int to,HashMap<String,String> ohterParams) {
	     	ApiParams params = new ApiParams();
	        params.addParam("wz", wzName);
		  	params.addParam("from",from);
		  	params.addParam("to",to);

		  	Iterator<Entry<String, String>> iter = ohterParams.entrySet().iterator();
		  	while(iter.hasNext())
		  	{
		  		Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
		  		params.addParam(entry.getKey(),  entry.getValue());
		  	}

		  	params.addParam("apiFormatter", "WeiZhanAd");
	
	  	   String json = BaseApiCommand.createCommand("weizhan.ads/", true, params).executeSync(ctx);

	  	   return IOUtil.json2Obj(json, new TypeReference<ApiResult<List<AdItem>>>(){});


	  }
	  public static ApiResult<List<TagItem>>  getWeiZhanList(Context ctx,String city,String lat,String lng,String keyword,int from,int to ){
	     	ApiParams params = new ApiParams();
	        params.addParam("city", city);
		  	params.addParam("lat",from);
		  	params.addParam("lng",to);
		  	//otherParams是其它各种用于查询的参数，比如，红米。
		  	params.addParam("keyword", keyword);
		  	params.addParam("from",from);
		  	params.addParam("to",to);
		  	params.addParam("ApiFormatter", "WeiZhan");
	  	    String json = BaseApiCommand.createCommand("weizhan.ads/", false, params).executeSync(ctx);

	  	    return IOUtil.json2Obj(json, new TypeReference<ApiResult<List<TagItem>>>(){});

	  }
	  
}
