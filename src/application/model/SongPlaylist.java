package application.model;

import java.util.List;
import java.util.ArrayList;

public class SongPlaylist {
	
	private int playlistId;
	public String title;
	public List<Song> songs = new ArrayList<>();
	
	// constructor
	public SongPlaylist(String title, List<Song> songs, int playlistId) {
		super();
		this.title = title;
		this.songs = songs;
		this.playlistId = playlistId;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public List<Song> getSongs() {return songs;}
	public int getPlaylistId() {return playlistId;}
	
	public void setTitle(String title) {this.title = title;}
	public void setSongs(List<Song> songs) {this.songs = songs;}
	public void setPlaylistId(int playlistId) {this.playlistId = playlistId;}
}