package application.model;

import java.util.ArrayList;
import java.util.List;

public class Song {
	
	public String title;
	public List<String> genres = new ArrayList<>();
	public double userRating;
	public String album;
	public String artist;
	public int yearReleased;
	public int runtimeMins;
	
	// getters and setters
	public String getTitle() {return title;}
	public List<String> getGenres() {return genres;}
	public double getUserRating() {return userRating;}
	public String getAlbum() {return album;}
	public String getArtist() {return artist;}
	public int getYearReleased() {return yearReleased;}
	public int getRuntimeMins() {return runtimeMins;}
	
	public void setTitle(String title) {this.title = title;}
	public void setGenres(List<String> genres) {this.genres = genres;}
	public void setUserRating(double userRating) {this.userRating = userRating;}
	public void setAlbum(String album) {this.album = album;}
	public void setArtist(String artist) {this.artist = artist;}
	public void setYearReleased(int yearReleased) {this.yearReleased = yearReleased;}
	public void setRuntimeMins(int runtimeMins) {this.runtimeMins = runtimeMins;}
}
