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
import application.model.Status;

public class SongDAOImpl implements SongDAO {
	
	private Connection conn;
	private int userId;
	
	public SongDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}
	
	public int addSong(Song song) throws SQLException {
		
		int songId = -1;
		String sql = "INSERT INTO games (title, status, user_rating, developer, avg_playtime_mins) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			// add song to 'song' table
			stmt.setString(1, song.getTitle());
			stmt.setString(2, song.getStatus().toDbString());
			stmt.setDouble(3, song.getUserRating());
			stmt.setString(4, song.getAlbum());
			stmt.setString(5, song.getArtist());
			stmt.setInt(6, song.getYearReleased());
			stmt.setInt(7, song.getRuntimeSeconds());
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
	
	public Song getSongById(int songId) throws SQLException {
		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds
				FROM songs_playlists sp
				INNER JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN songs s
				ON spi.song_id = s.id
				WHERE sp.user_id = ? AND s.id = ?
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, songId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Song(rs.getString("title"),
								Status.fromDbString(rs.getString("status")),
								rs.getDouble("user_rating"),
								rs.getString("album"),
								rs.getString("artist"),
								rs.getInt("year_released"),
								rs.getInt("runtime_seconds"));
			}
			else {
				System.out.println("Song not found");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	public Song getSongByTitle(String title) throws SQLException {
		String sql = """
		        SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds
		        FROM songs_playlists sp
		        JOIN songs_playlist_items spi ON sp.id = spi.playlist_id
		        JOIN songs s ON spi.game_id = s.id
		        WHERE g.title = ? AND gp.user_id = ?
		    """;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, title);
		    stmt.setInt(2, userId);
		    ResultSet rs = stmt.executeQuery();
		    if (rs.next()) {
		    	return new Song(rs.getString("title"),
								Status.fromDbString(rs.getString("status")),
								rs.getDouble("user_rating"),
								rs.getString("album"),
								rs.getString("artist"),
								rs.getInt("year_released"),
								rs.getInt("runtime_seconds"));
		    } else {
		    	System.out.println("Game not found.");
		    }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	public List<Song> getSongsByUser(int userId) throws SQLException{
		
		List<Song> songs = new ArrayList<>();

		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds
				FROM songs_playlists sp
				INNER JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN games s
				ON spi.song_id = s.id
				WHERE sp.user_id = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Song song = new Song(rs.getString("title"),
									Status.fromDbString(rs.getString("status")),
									rs.getDouble("user_rating"),
									rs.getString("album"),
									rs.getString("artist"),
									rs.getInt("year_released"),
									rs.getInt("runtime_seconds"));
				
				songs.add(song);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return songs;
	}
	
	public List<Song> getSongsByArtist(String artist, int userId) throws SQLException{
		List<Song> songs = new ArrayList<>();

		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds
				FROM songs_playlists sp
				INNER JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN games s
				ON spi.game_id = s.id
				WHERE sp.user_id = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setString(2, artist);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            Song song = new Song(rs.getString("title"),
				                    Status.fromDbString(rs.getString("status")),
				                    rs.getDouble("user_rating"),
				                    rs.getString("album"),
				                    rs.getString("artist"),
				                    rs.getInt("year_released"),
				                    rs.getInt("runtime_seconds")
	            );

	            songs.add(song);
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return songs;
	}
	
	public void deleteSong(String title) throws SQLException{
		String sql = "DELETE FROM songs WHERE title = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, title);
	        stmt.executeUpdate();
	        System.out.println("Song '" + title + "' deleted");
	    }
	}
	
	public void updateSongRating(String title, int rating) throws SQLException{
		String sql = """
				UPDATE songs
				SET user_rating = ? 
				WHERE title = ?
				""";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setDouble(1, rating);
			stmt.setString(2, title);
			stmt.executeUpdate();
			System.out.println("Song updated: " + title);
		}
	}
	
	public void addReview(String title, String review) throws SQLException{
		String sql = """
				UPDATE games 
				SET review = ?
				WHERE title = ?
				""";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, review);
			stmt.setString(2, title);
			stmt.executeUpdate();
			System.out.println("Song updated: " + title);
		}
	}
}
