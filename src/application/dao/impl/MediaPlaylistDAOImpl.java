package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.model.Type;
import application.model.Media;
import application.model.Song;
import application.model.Game;
import application.model.Show;
import application.model.Status;

public class MediaPlaylistDAOImpl {
	
	private Connection conn;
	private int userId;
	
	public MediaPlaylistDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}
	
	public boolean createPlaylist(String name, int userId, Type type) throws SQLException {
		
		boolean answer = true;
		
		if(type == Type.SONG)
		{
			String normalized = name.trim().toLowerCase();
	
		    if (normalized.equals("all_songs") || normalized.equals("all songs")) {
		        System.out.println(" - \"" + name + "\" is a reserved playlist name.");
		        answer = false;
		    }
			
		    if(answer)
		    {
				String sql = "INSERT INTO songs_playlists (user_id, title) VALUES (?, ?)";
				
				try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
					stmt.setInt(1, userId);
					stmt.setString(2, name);
					stmt.executeUpdate();
		
				}
				catch (SQLException e) {
					
					if (e.getMessage().contains("UNIQUE constraint failed")) {
						System.out.println(" -");
				        System.out.println(" - Playlist \"" + name + "\" already exists!");
				        answer = false;
				    }
					else
					{
				        answer = true;
				    }
				}
		    }
		}
	
		return answer;
	}
	
	public void addMediaToPlaylist(int playlistId, int mediaId, Status status, double rating, String review, Type type) throws SQLException {
		
		if(type == Type.SONG)
		{
			String sql = "INSERT OR IGNORE INTO songs_playlist_items (playlist_id, songs_id, status, user_rating, review) VALUES (?, ?, ?, ?, ?)";
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, playlistId);
				stmt.setInt(2, mediaId);
				stmt.setString(3, status.toDbString());
				stmt.setDouble(4, rating);
				stmt.setString(5, review);
				stmt.executeUpdate();
				
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void removeMediaFromPlaylist(int playlistId, int mediaId, Type type) throws SQLException {	
		
		if(type == Type.SONG)
		{
			String sql = """
			        DELETE FROM songs_playlist_items
			        WHERE playlist_id = ? AND songs_id = ?
			        """;
	
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, playlistId);
		        stmt.setInt(2, mediaId);
	
		        int rowsDeleted = stmt.executeUpdate();
	
		        if (rowsDeleted > 0) {
		            System.out.println(" - Song removed from playlist.");
		        } else {
		            System.out.println(" - Song was not found in this playlist.");
		        }
		    }
		}
	}
	
	public List<? extends Media> getMediasInPlaylist(int playlistId, Type type) throws SQLException {
		
		List<Song> songItems = new ArrayList<Song>();
		//List<Game> gameItems = new ArrayList<Game>();
		//List<Show> showItems = new ArrayList<Show>();
		
		if(type == Type.SONG)
		{
			String sql = """
				SELECT s.id, s.title, spi.status, spi.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds, spi.review
				FROM songs_playlists sp
				JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				JOIN songs s
				ON spi.songs_id = s.id
				WHERE sp.user_id = ? AND sp.id = ?
				""";
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, userId);
				stmt.setInt(2, playlistId);
				
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					
					String statusString = rs.getString("status");
	
				    Status status = statusString == null
				            ? Status.PLANNED
				            : Status.fromDbString(statusString);
	
				    String review = rs.getString("review");
				    if (review == null) {
				        review = "";
				    }
				    
					Song song = new Song(rs.getString("title"),
										 status,
										 rs.getDouble("user_rating"),
										 rs.getString("album"),
										 rs.getString("artist"),
										 rs.getInt("year_released"),
										 rs.getInt("runtime_seconds"),
										 rs.getString("review"));
					
					songItems.add(song);
				}
			}
		}
		
		return songItems;
	}
	
	public void deletePlaylist(int playlistId, Type type) throws SQLException {
		
		if(type == Type.SONG)
		{
			String sql = "DELETE FROM songs_playlist_items WHERE playlist_id = ?";
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, playlistId);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			
			sql = "DELETE FROM songs_playlists WHERE id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, playlistId);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public int countStatusedSongs(int playlistId, Status status, Type type) throws SQLException {
	    
		if(type == Type.SONG)
		{
			String sql = """
		        SELECT COUNT(*)
		        FROM songs_playlist_items
		        WHERE playlist_id = ?
		          AND LOWER(REPLACE(status, '_', ' ')) = LOWER(?)
		    """;
	
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, playlistId);
		        stmt.setString(2, status.toDbString());
	
		        try (ResultSet rs = stmt.executeQuery()) {
		            return rs.next() ? rs.getInt(1) : 0;
		        }
		    }
		}
		
		return -1;
	}
	
	public double calculateAvgRating(int playlistId, Type type) throws SQLException {
		
		if(type == Type.SONG)
		{
			String sql = """
			        SELECT AVG(user_rating)
			        FROM songs_playlist_items
			        WHERE playlist_id = ?
			          AND LOWER(REPLACE(status, '_', ' ')) = LOWER(?)
			          AND user_rating > 0
			    """;
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			    stmt.setInt(1, playlistId);
			    stmt.setString(2, Status.COMPLETED.toDbString());
			    
	
			    try (ResultSet rs = stmt.executeQuery()) {
			        if (rs.next()) {
			            return rs.getDouble(1);
			        }
			    }
			}
		}

		return 0.0;
	}
	
	public void updateAllPlaylists(Media media) throws SQLException {
		
		String mediaType = null;
		
		if (media instanceof Song) {
			mediaType = "song";
		}
		if (media instanceof Game) {
			mediaType = "game";
		}
		if (media instanceof Show) {
			mediaType = "show";
		}
		
		String sql = "UPDATE" + mediaType + "s_playlist_items " + 
    			"SET status = ?, user_rating = ? WHERE " + mediaType + "s_id IN (SELECT id FROM " + mediaType + "s " +
    			"WHERE title = ? AND artist = ?) AND playlist_id IN (SELECT id FROM " + mediaType + "s_playlists " +
    			"WHERE user_id = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, media.getStatus().toDbString());
	        stmt.setDouble(2, media.getUserRating());
	        stmt.setString(3, media.getReview());
	        stmt.setString(4, media.getTitle());
	        stmt.setString(5, media.getCreator());
	        stmt.setInt(6, userId);

	        stmt.executeUpdate();
	    }
	}
}
