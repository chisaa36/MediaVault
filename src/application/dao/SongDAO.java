package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Song;
import application.model.Status;

public interface SongDAO {
	
	boolean isNewSong(Song song, int userId) throws SQLException;
	int addSong(Song song, int userId) throws SQLException;
	Song getSongOfUserById(int songId) throws SQLException;
	Song getSongByTitle(String title) throws SQLException;
	List<Song> getSongsByUser(int userId) throws SQLException;
	List<Song> getSongsByArtist(String artist, int userId) throws SQLException;
	int deleteSong(int userId, String title, String artist) throws SQLException;
	void updateSongRating(int userId, Song song, double rating) throws SQLException;
	void addReview(int userId, Song song, String review) throws SQLException;
	void updateStatus(int userId, Song song, Status newStatus) throws SQLException;
	int getSongId(String title, String artist) throws SQLException;
	int getNextSongId(Song song, int userId) throws SQLException;
}