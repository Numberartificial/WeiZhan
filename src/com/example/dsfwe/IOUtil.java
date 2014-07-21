package com.example.dsfwe;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import android.text.TextUtils;



public class IOUtil {

	public static String obj2Json(Object obj) {
		try {
			return JSON.toJSONString(obj);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T json2Obj(String json, Class<T> clz) {
		try {
			if(TextUtils.isEmpty(json)) {
				return null;
			}
			return JSON.parseObject(json, clz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T json2Obj(String json, TypeReference<T> typeRef) {
		try {
			if(TextUtils.isEmpty(json)) {
				return null;
			}
			return JSON.parseObject(json, typeRef);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
