package model;

import javax.persistence.Entity;

@Entity
public class Song {

	private String artist;
	private String title;
	private int year;
	private String web_url;
	private String img_url;
	
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getImage() {
		return img_url;
	}
	public void setImage(String image) {
		this.img_url = image;
	}
	public String getWeb_url() {
		return web_url;
	}
	public void setWeb_url(String web_url) {
		this.web_url = web_url;
	}
	
	
	
}
