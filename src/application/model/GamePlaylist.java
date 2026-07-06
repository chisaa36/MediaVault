package application.model;

import java.util.List;
import java.util.ArrayList;

public class GamePlaylist {
	
	public String title;
	public List<Game> gamePlaylists = new ArrayList<>();
	
	public GamePlaylist(String title, List<Game> gamePlaylists) {
		this.title = title;
		this.gamePlaylists = gamePlaylists;
	}
}