package application.dao;

import java.sql.SQLException;
import java.util.List;

import application.model.Song;
import application.model.SongList;

public interface SongPlaylistDAO {
	
	int createPlaylist(String name, int userId) throws SQLException;
	void addSongToPlaylist(int playlistId, int songId) throws SQLException;
	void addSongsToPlaylist(int playlistId, List<Song> songs) throws SQLException;
	void removeSongFromPlaylist(int playlistId, int songId) throws SQLException;
	List<Song> getSongsInPlaylist(int playlistId) throws SQLException;
	List<SongList> getPlaylistsByUser(int userId) throws SQLException;
	void deletePlaylist(int playlistId) throws SQLException;
}
