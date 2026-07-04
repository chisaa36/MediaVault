package application.model;

import java.util.List;
import java.util.ArrayList;

public class User {
	
	public String username;
	
	public ShowPlaylist completedShows;
	public ShowPlaylist currentShows;
	public ShowPlaylist upcomingShows;
	
	public SongPlaylist completedSongs;
	public SongPlaylist currentSongs;
	public SongPlaylist upcomingSongs;
	
	public GamePlaylist completedGames;
	public GamePlaylist currentGames;
	public GamePlaylist upcomingGames;
	
	
	// User-Customized Categorization
	public ShowPlaylist allShows;
	public ShowPlaylist favoriteShows;
	public List<ShowPlaylist> showPlaylists = new ArrayList<>();
	
	public SongPlaylist allSongs;
	public SongPlaylist favoriteSongs;
	public List<SongPlaylist> songPlaylists = new ArrayList<>();
	
	public GamePlaylist allGames;
	public GamePlaylist favoriteGames;
	public List<GamePlaylist> gamePlaylists = new ArrayList<>();
}
