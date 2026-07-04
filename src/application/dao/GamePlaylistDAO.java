package application.dao;

import java.sql.SQLException;
import java.util.List;

import application.model.Game;
import application.model.GamePlaylist;

public interface GamePlaylistDAO {
	
	int createPlaylist(String name, int userId) throws SQLException;
	void addGameToPlaylist(int playlistId, int gameId) throws SQLException;
	void addGamesToPlaylist(int playlistId, List<Game> games) throws SQLException;
	void removeGameFromPlaylist(int playlistId, int gameId) throws SQLException;
	List<Game> getGamesInPlaylist(int playlistId) throws SQLException;
	List<GamePlaylist> getPlaylistsByUser(int userId) throws SQLException;
	void deletePlaylist(int playlistId) throws SQLException;
}