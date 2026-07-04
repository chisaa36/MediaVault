package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Song;

public interface SongDAO {
	
	void addSong(Song song) throws SQLException;
	Song getSongById(int id) throws SQLException;
	Song getSongByTitle(String title) throws SQLException;
	List<Song> getSongsByUser(int userId) throws SQLException;
	List<Song> getSongsByArtist(String artist, int userId) throws SQLException;
	void deleteSong(String title) throws SQLException;
	void updateSongRating(String title, int rating) throws SQLException;
	void addReview(String title, String review) throws SQLException;
}