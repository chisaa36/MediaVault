package application.model;

import java.util.List;
import java.util.ArrayList;

public class SongPlaylist {
	
	public String title;
	public List<Song> songs = new ArrayList<>();
	
	// constructor
	public SongPlaylist(String title, List<Song> songs) {
		super();
		this.title = title;
		this.songs = songs;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public List<Song> getSongs() {return songs;}
	
	public void setTitle(String title) {this.title = title;}
	public void setSongs(List<Song> songs) {this.songs = songs;}
}