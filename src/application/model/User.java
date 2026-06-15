package application.model;

import java.util.List;
import java.util.ArrayList;

public class User {
	
	public String username;
	
	public ShowList completedShows;
	public ShowList currentShows;
	public ShowList upcomingShows;
	
	public SongList completedSongs;
	public SongList currentSongs;
	public SongList upcomingSongs;
	
	public GameList completedGames;
	public GameList currentGames;
	public GameList upcomingGames;
	
	
	// User-Customized Categorization
	public ShowList allShows;
	public ShowList favoriteShows;
	public List<ShowList> showPlaylists = new ArrayList<>();
	
	public SongList allSongs;
	public SongList favoriteSongs;
	public List<SongList> songPlaylists = new ArrayList<>();
	
	public GameList allGames;
	public GameList favoriteGames;
	public List<GameList> gamePlaylists = new ArrayList<>();
}
