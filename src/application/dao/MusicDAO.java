package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Song;

public interface MusicDAO {
	
	void addSong(Song song) throws SQLException;
	Song getSongByTitle(String title) throws SQLException;
	Song getSongById(int id) throws SQLException;
	List<Song> getSongsByUser(int userId) throws SQLException;
	void deleteSong(String title) throws SQLException;
	void updateSongRating(String title, int rating) throws SQLException;
	void addReview(String title, String review) throws SQLException;
}
