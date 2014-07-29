package com.example.dsfwe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.nfc.Tag;

/*{
            "id": "a361712907",
            "title": "kaokaokao",
            "tag": "shouji",
            "content": "",
            "user": "gaohongxiang",
            "price": "",
            "images": null,
            "voice": null
        },*/

public class AdItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	//private String []badges;
	//private Tag[] highlights;	
	private String title;
	private String tag;
	private String content;
	private String userName;
	private String userImage;
	private String price;
	private List<String> images;
	//private List<String> voice;
	private String voice;

					
	public AdItem() {
	}
	
	public AdItem(String id,String title) {
		this.id = id ;
	}
	
	public AdItem(String title) {
		this.title = title;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

//	public List<String> getVoice() {
//		return voice;
//	}
//
//	public void setVoice(List<String> voice) {
//		this.voice = voice;
//	}

}
