package com.jq.model;

public class Song {
	private String id;
	private String artist;
	private String title;
	public Song(String id, String artist, String title) {
		super();
		this.id = id;
		this.artist = artist;
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	@Override
	public String toString() {
		return "Song [id=" + id + ", artist=" + artist + ", title=" + title + "]";
	}
	
}
