package me.maxwin.view;

public class Info {
	private String title;
	private String text;
	private String bt1;
	private String imageURL;
	public Info(String title, String text, String bt1){
		this.title = title;
		this.text = text;
		this.bt1 = bt1;
	}
	public String getTitle(){
		return title;
	}
	public String getText(){
		return text;
	}
	public String getBt1(){
		return bt1;
	}
	public String getImageURL(){
		return imageURL;
	}
}
