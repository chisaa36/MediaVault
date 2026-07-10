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
import application.model.MediaPlaylist;
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
	
	public boolean createPlaylist(String name, Type type) throws SQLException {
		
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
	
	public List<Media> getMediasInPlaylist(int playlistId, String mediaType) throws SQLException {
		
		String table = mediaType.toLowerCase();
		List<Media> mediaItems = new ArrayList<Media>();
		
		String sql = "SELECT m.id, m.title, m.creator, m.year, mr.status, mr.user_rating, mr.review "
	            + "FROM " + table + "s_playlists mp "
	            + "JOIN " + table + "s_playlist_items mpi "
	            + "ON mp.id = mpi.playlist_id "
	            + "JOIN " + table + "s m "
	            + "ON mpi." + table + "_id = m.id "
	            + "JOIN " + table + "s_reviews mr "
	            + "ON m.id = mr." + table + "_id AND mr.user_id = mp.user_id "
	            + "WHERE mp.user_id = ? AND mp.id = ?";
			
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
				    
				    String title = rs.getString("title");
				    String creator = rs.getString("creator");
				    int year = rs.getInt("year");
				    double user_rating = rs.getDouble("user_rating");
				    
				    switch(mediaType) {
				    	case "song":
				    		sql = """
				    			SELECT album, runtime_seconds FROM songs
				    			WHERE title = ? AND creator = ?
				    			""";
				    		
				    		try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				    			pstmt.setString(1, title);
				    			pstmt.setString(2, creator);
								
								rs = pstmt.executeQuery();
								if (rs.next()) {
									Song media = new Song(title,
											  creator,
											  year,
											  status,
											  user_rating,
											  review,
											  rs.getString("album"),
											  rs.getInt("runtime_seconds"));
									
									mediaItems.add(media);
								}
								
				    		}
				    		break;
				    	case "game":
				    		sql = """
				    			SELECT avg_playtime_mins FROM games
				    			WHERE title = ? AND creator = ?
				    			""";
				    		
				    		try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				    			pstmt.setString(1, title);
				    			pstmt.setString(2, creator);
								
								rs = pstmt.executeQuery();
								
								if (rs.next()) {
									Game media = new Game(title,
											  creator,
											  year,
											  status,
											  user_rating,
											  review,
											  rs.getInt("avg_playtime_mins"));
									mediaItems.add(media);
								}
				    		}
				    		break;
			    		
				    	case "show":
				    		break;
				    }
				}
			}
		
		return mediaItems;
	}
	
	public List<MediaPlaylist> getPlaylistsByUser(int userId, String mediaType) throws SQLException {
		List<MediaPlaylist> playlists = new ArrayList<>();
		
		String sql = "SELECT id, title FROM songs_playlists WHERE user_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int playlistId = rs.getInt("id");
				List<Media> items = getMediasInPlaylist(playlistId, mediaType);
				
				MediaPlaylist playlist = new MediaPlaylist(rs.getString("title"), items, playlistId);
				
				playlists.add(playlist);
			}
		}
		
		return playlists;
	}
	
	public void deletePlaylist(int playlistId, String mediaType) throws SQLException {
		
		String sql = "DELETE FROM " + mediaType + "s_playlist_items WHERE playlist_id = ?";
			
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
			
		sql = "DELETE FROM " + mediaType + "s_playlists WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public int countStatusedMedia(int playlistId, Status status, String mediaType) throws SQLException {
	    
		String media = mediaType.toLowerCase();
		
		String sql = "SELECT COUNT(*) FROM " + media + "s_reviews mr "
				   + "JOIN " + media + "s_playlist_items mpi ON mr." + media + "_id = mpi." + media +"_id "
				   + "WHERE mpi.playlist_id = ? AND mr.user_id = ? AND LOWER(REPLACE(mr.status, '_', ' ')) = LOWER(?)";
	
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, playlistId);
			stmt.setInt(2, userId);
		    stmt.setString(3, status.toDbString());
	
		    try (ResultSet rs = stmt.executeQuery()) {
		    	return rs.next() ? rs.getInt(1) : 0;
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;

	}
	
	public double calculateAvgRating(int playlistId, String mediaType) throws SQLException {
		
		String media = mediaType.toLowerCase();
		
		String sql = "SELECT COUNT(*) FROM " + media + "s_reviews mr "
				   + "JOIN " + media + "s_playlist_items mpi ON mr." + media + "_id = mpi." + media +"_id "
				   + "WHERE mpi.playlist_id = ? AND mr.user_id = ? AND LOWER(REPLACE(mr.status, '_', ' ')) = LOWER(?)";
			
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, playlistId);
			stmt.setString(2, Status.COMPLETED.toDbString());
			    
	
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble(1);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return 0.0;
	}
	
	public void updateAllPlaylists(Media media) throws SQLException {
		
		System.out.println("@@@ UPDATINGG");
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
		
		// make use of review table
		// basically update the reviews of a media
		String sql = "UPDATE " + mediaType + "s_reviews " + 
    			"SET status = ?, user_rating = ?, review = ? " +
				"WHERE user_id = ? AND " + mediaType + "_id = " +
					"(SELECT id FROM " + mediaType + "s " +
					"WHERE title = ? AND creator = ?)";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, media.getStatus().toDbString());
	        stmt.setDouble(2, media.getUserRating());
	        stmt.setString(3, media.getReview());
	        stmt.setInt(4, userId);
	        stmt.setString(5, media.getTitle());
	        stmt.setString(6, media.getCreator());

	        stmt.executeUpdate();
	    }
	}

	public void addMediaToPlaylist(int playlistId, int mediaId, String mediaType) throws SQLException {

		String sql = "INSERT INTO " + mediaType.toLowerCase() + "s_playlist_items (playlist_id, " + mediaType.toLowerCase() + ") "
				   + "VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, playlistId);
	        stmt.setInt(2, mediaId);

	        stmt.executeUpdate();
	    }
	}
}
