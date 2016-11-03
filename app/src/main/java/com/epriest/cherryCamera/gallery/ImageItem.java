package com.epriest.cherryCamera.gallery;


public class ImageItem {

	private Long imageId;
	private String date;
	private int num;
	private String picsize;
	private String picname;
 
	public ImageItem(int num, Long Id, String date, String picSize, String picName) {
		super();
		this.num = num;
		this.imageId = Id;
		this.date = date;
		this.picsize = picSize;
		this.picname = picName;
	}
	
	public int getNum() {
		return num;
	}
 
	public void setNum(int num) {
		this.num = num;
	}
 
	public Long getImageID() {
		return imageId;
	}
 
	public void setImageID(Long id) {
		this.imageId = id;
	}
 
	public String getDate() {
		return date;
	}
 
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getPicsize() {
		return picsize;
	}
 
	public void setPicsize(String picsize) {
		this.picsize = picsize;
	}
	
	public String getPicname() {
		return picname;
	}
 
	public void setPicname(String picname) {
		this.picname = picname;
	}
}
