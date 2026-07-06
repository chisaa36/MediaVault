package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.SongDAO;
import application.model.Song;

public class SongDAOImpl {
	
	private Connection conn;
	private int userId;
	
	public SongDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}
	
	void addSong(Song song) throws SQLException {
		
		int songId = -1;
		String sql = "INSERT INTO games (title, status, user_rating, developer, avg_playtime_mins) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			// add song to 'song' table
			stmt.setString(1, song.getTitle());
			stmt.setString(2, song.getGenre());
			stmt.setString(3, song.getStatus());
			stmt.setDouble(4, song.getUserRating());
			stmt.setString(5, song.getAlbum());
			stmt.setString(6, song.getArtist());
			stmt.setInt(7, song.getYearReleased());
			stmt.setInt(8, song.getRuntimeSeconds());
			stmt.executeUpdate();
			
			ResultSet keys = stmt.getGeneratedKeys();
			
	        if (keys.next()) {
	            songId = keys.getInt(1);
	        }
	        System.out.println("Song added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Song '" + song.getTitle() + "' is already added.");
		    } else {
		        System.out.println(e.getMessage()); // print other unexpected errors normally
		    }
		}
		
		// add song to "all_songs" playlist	if game is added
		if (songId != -1) {
			sql = "INSERT OR IGNORE INTO games_playlist_items (playlist_id, game_id)"
				+ " VALUES (?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, 1);
				stmt.setInt(2, songId);
				stmt.executeUpdate();
			}
		}
	
		return songId;
	}
	
	Song getSongById(int id) throws SQLException {
		
	}
	
	Song getSongByTitle(String title) throws SQLException {
		
	}
	
	List<Song> getSongsByUser(int userId) throws SQLException{
		
	}
	
	List<Song> getSongsByArtist(String artist, int userId) throws SQLException{
		
	}
	
	void deleteSong(String title) throws SQLException{
		
	}
	
	void updateSongRating(String title, int rating) throws SQLException{
		
	}
	
	void addReview(String title, String review) throws SQLException{
		
	}
}
