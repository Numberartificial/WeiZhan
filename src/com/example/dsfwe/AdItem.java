package com.example.dsfwe;

import java.io.Serializable;

import android.nfc.Tag;


public class AdItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	//private String []badges;
	//private Tag[] highlights;
	private String title;
	private String content;
	private String []images;

					
	public AdItem()
	{
	
		
		
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public AdItem(String title)
	{
		this.title = title;
		
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

//	public Tag[] getHighlights() {
//		return highlights;
//	}
//
//	public void setHighlights(Tag[] highlights) {
//		this.highlights = highlights;
//	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public AdItem(String id,String title)
	{
		this.id = id ;
	}
//	public String [] getBadges() {
//		return badges;
//	}
//	public void setBadges(String [] badges) {
//		this.badges = badges;
//	}

}
