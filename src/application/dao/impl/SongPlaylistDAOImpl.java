package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.SongPlaylistDAO;
import application.model.Song;
import application.model.SongPlaylist;
import application.model.Status;

public class SongPlaylistDAOImpl implements SongPlaylistDAO {

	private Connection conn;
	private int userId;
	
	public SongPlaylistDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}
	
	@Override
	public boolean createPlaylist(String name, int userId) throws SQLException {
		
		boolean answer = true;
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
	
		return answer;
	}
	
	@Override
	public void addSongToPlaylist(int playlistId, int songId, Status status, double rating, String review) throws SQLException {
		String sql = "INSERT OR IGNORE INTO songs_playlist_items (playlist_id, songs_id, status, user_rating, review) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.setInt(2, songId);
			stmt.setString(3, status.toDbString());
			stmt.setDouble(4, rating);
			stmt.setString(5, review);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void addSongsToPlaylist(int playlistId, List<Song> songs) throws SQLException {
		
		for (Song song : songs) {
			int songId = song.getSongId();
			
			if (songId != -1) {
				String sql = "INSERT OR IGNORE INTO songs_playlist_items (playlist_id, songs_id) VALUES (?, ?)";
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)){
					stmt.setInt(1, playlistId);
					stmt.setInt(2, songId);
					stmt.executeUpdate();
					
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}
			else {
				System.out.println("Song '" + song.getTitle() + "' not found.");
			}
		}
	}

	@Override
	public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {	
		String sql = """
		        DELETE FROM songs_playlist_items
		        WHERE playlist_id = ? AND songs_id = ?
		        """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, playlistId);
	        stmt.setInt(2, songId);

	        int rowsDeleted = stmt.executeUpdate();

	        if (rowsDeleted > 0) {
	            System.out.println(" - Song removed from playlist.");
	        } else {
	            System.out.println(" - Song was not found in this playlist.");
	        }
	    }
	}

	@Override
	public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
		List<Song> items = new ArrayList<Song>();
		
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
				
				items.add(song);
			}
		}
		
		return items;
	}

	@Override
	public List<SongPlaylist> getPlaylistsByUser(int userId) throws SQLException {
		List<SongPlaylist> playlists = new ArrayList<>();
		
		String sql = "SELECT id, title FROM songs_playlists WHERE user_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int playlistId = rs.getInt("id");
				List<Song> items = getSongsInPlaylist(playlistId);
				
				SongPlaylist playlist = new SongPlaylist(rs.getString("title"), items, playlistId);
				
				playlists.add(playlist);
			}
		}
		
		return playlists;
	}

	@Override
	public void deletePlaylist(int playlistId) throws SQLException {
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
	
	public int countStatusedSongs(int playlistId, Status status) throws SQLException {
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
	
	public double calculateAvgRating(int playlistId) throws SQLException {
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

		return 0.0;
	}
	
	public void updateAllPlaylists(Song song) throws SQLException {
		String sql = """
			    UPDATE songs_playlist_items
			    SET status = ?,
			        user_rating = ?,
			        review = ?
			    WHERE songs_id IN (
			        SELECT id
			        FROM songs
			        WHERE title = ?
			          AND artist = ?
			    )
			    AND playlist_id IN (
			        SELECT id
			        FROM songs_playlists
			        WHERE user_id = ?
			    )
			    """;
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, song.getStatus().toDbString());
	        stmt.setDouble(2, song.getUserRating());
	        stmt.setString(3, song.getReview());
	        stmt.setString(4, song.getTitle());
	        stmt.setString(5, song.getArtist());
	        stmt.setInt(6, userId);

	        stmt.executeUpdate();
	    }
	}
}
