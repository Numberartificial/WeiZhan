package com.example.dsfwe;

import java.util.HashMap;

public class TagItem {


	private String text;
	private String color;
	private String bgColor ;
	private HashMap<String,String> params;
	
	public TagItem()
	{
		 this.color ="#000000";
		 this.bgColor = "#f5f5f5";
	}
	
	public TagItem(String text){
		this.text = text;
		 this.color ="#000000";
		 this.bgColor = "#f5f5f5";
	}
	
	public TagItem(String text,String color,String bgColor){
		this.text = text;
		this.color = color;
		this.bgColor = bgColor;
		//this.params = new HashMap<String,String>();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}
	public String getBgColor() {
		return bgColor;
	}
	public HashMap<String,String> getParams() {
		return params;
	}
	public void setParams(HashMap<String,String> params) {
		this.params = params;
	}
}
