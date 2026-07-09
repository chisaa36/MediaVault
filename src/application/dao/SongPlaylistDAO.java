package application.dao;

import java.sql.SQLException;
import java.util.List;

import application.model.Song;
import application.model.SongPlaylist;
import application.model.Status;

public interface SongPlaylistDAO {
	
	boolean createPlaylist(String name, int userId) throws SQLException;
	void addSongToPlaylist(int playlistId, int songId, Status status, double rating, String review) throws SQLException;
	void addSongsToPlaylist(int playlistId, List<Song> songs) throws SQLException;
	void removeSongFromPlaylist(int playlistId, int songId) throws SQLException;
	List<Song> getSongsInPlaylist(int playlistId) throws SQLException;
	List<SongPlaylist> getPlaylistsByUser(int userId) throws SQLException;
	void deletePlaylist(int playlistId) throws SQLException;
	void updateAllPlaylists(Song song) throws SQLException;
}
