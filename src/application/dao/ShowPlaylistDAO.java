package application.dao;

import java.sql.SQLException;
import java.util.List;

import application.model.Show;
import application.model.ShowPlaylist;

public interface ShowPlaylistDAO {
	
	int createPlaylist(String name, int userId) throws SQLException;
	void addShowToPlaylist(int playlistId, int showId) throws SQLException;
	void addShowsToPlaylist(int playlistId, List<Show> shows) throws SQLException;
	void removeShowFromPlaylist(int playlistId, int showId) throws SQLException;
	List<Show> getShowsInPlaylist(int playlistId) throws SQLException;
	List<ShowPlaylist> getPlaylistsByUser(int userId) throws SQLException;
	void deletePlaylist(int playlistId) throws SQLException;
}
