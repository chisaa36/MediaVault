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
	public int createPlaylist(String name, int userId) throws SQLException {
		int playlistId = -1;
		
		String sql = "INSERT INTO songs_playlists (user_id, title) VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, userId);
			stmt.setString(2, name);
			stmt.executeUpdate();
			
			ResultSet keys = stmt.getGeneratedKeys();
	        if (keys.next()) {
	        	playlistId = keys.getInt(1);
	        }
	        System.out.println("Playlist added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Playlist '" + name + "' is already added.");
		    } else {
		        System.out.println(e.getMessage());
		    }
		}
	
		return playlistId;
	}

	@Override
	public void addSongToPlaylist(int playlistId, int songId) throws SQLException {
		String sql = "INSERT OR IGNORE INTO songs_playlist_items (playlist_id, song_id) VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.setInt(2, songId);
			stmt.executeUpdate();
			
			System.out.println("Song added successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void addSongsToPlaylist(int playlistId, List<Song> songs) throws SQLException {
		for (Song song : songs) {
			int songId = getSongId(song.getTitle());
			
			if (songId != -1) {
				String sql = "INSERT OR IGNORE INTO songs_playlist_items (playlist_id, song_id) VALUES (?, ?)";
				
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
		String sql = "DELETE FROM songs_playlist_items WHERE playlist_id = ? AND song_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.setInt(2, songId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
		List<Song> items = new ArrayList<Song>();
		
		String sql = """
			SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds
			FROM songs_playlists sp
			JOIN songs_playlist_items spi
			ON sp.id = spi.playlist_id
			JOIN songs s
			ON spi.song_id = s.id
			WHERE sp.user_id = ? AND sp.id = ?
			""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, playlistId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Song song = new Song(rs.getString("title"),
									 Status.fromDbString(rs.getString("status")),
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
				List<Song> items = getSongsInPlaylist(rs.getInt("id"));
				
				SongPlaylist playlist = new SongPlaylist(rs.getString("title"), items);
				
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
	
	private int getSongId(String title) throws SQLException {
		String sql = """
				SELECT s.id
				FROM songs_playlists sp
				JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				JOIN songs s
				ON spi.song_id = s.id
				WHERE sp.user_id = ? AND sp.id = 1 AND s.title = ?
				""";
			
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, title);
				
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		}
		
		return -1;
	}

}
