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
	
	public GameList completedGames;
	public GameList currentGames;
	public GameList upcomingGames;
	
	
	// User-Customized Categorization
	public ShowPlaylist allShows;
	public ShowPlaylist favoriteShows;
	public List<ShowPlaylist> showPlaylists = new ArrayList<>();
	
	public SongPlaylist allSongs;
	public SongPlaylist favoriteSongs;
	public List<SongPlaylist> songPlaylists = new ArrayList<>();
	
	public GameList allGames;
	public GameList favoriteGames;
	public List<GameList> gamePlaylists = new ArrayList<>();
}
