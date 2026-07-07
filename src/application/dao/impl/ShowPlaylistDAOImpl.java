package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.ShowPlaylistDAO;
import application.model.Show;
import application.model.ShowPlaylist;
import application.model.Status;

public class ShowPlaylistDAOImpl implements ShowPlaylistDAO {

	private Connection conn;
	private int userId;
	
	public ShowPlaylistDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}

	@Override
	public int createPlaylist(String name, int userId) throws SQLException {
		int playlistId = -1;
		String sql = "INSERT INTO shows_playlists (user_id, title) VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
	public void addShowToPlaylist(int playlistId, int showId) throws SQLException {
		String sql = "INSERT OR IGNORE INTO shows_playlist_items (playlist_id, show_id) VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, playlistId);
			stmt.setInt(2, showId);
			stmt.executeUpdate();
			System.out.println("Show added successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void addShowsToPlaylist(int playlistId, List<Show> shows) throws SQLException {
		String sql = """
			INSERT OR IGNORE INTO shows_playlist_items (playlist_id, show_id)
			SELECT ?, id FROM shows WHERE title = ?
			""";
			
		for (Show show : shows) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, playlistId);
				stmt.setString(2, show.getTitle());
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void removeShowFromPlaylist(int playlistId, int showId) throws SQLException {	
		String sql = "DELETE FROM shows_playlist_items WHERE playlist_id = ? AND show_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, playlistId);
			stmt.setInt(2, showId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public List<Show> getShowsInPlaylist(int playlistId) throws SQLException {
		List<Show> items = new ArrayList<>();
		
		String sql = """
			SELECT s.id, s.title, s.status, s.user_rating, s.number_of_seasons, s.num_of_episodes, s.avg_mins_per_ep, s.first_year_aired, s.last_year_aired
			FROM shows_playlists sp
			JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
			JOIN shows s ON spi.show_id = s.id
			WHERE sp.user_id = ? AND sp.id = ?
			""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, playlistId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Show show = new Show(rs.getString("title"),
			    					 Status.fromDbString(rs.getString("status")),
			    					 rs.getDouble("user_rating"),
			    					 rs.getString("review"),
			    					 rs.getInt("num_of_seasons"),
			    					 rs.getInt("num_of_episodes"),
			    					 rs.getInt("avg_mins_per_ep"),
			    					 rs.getInt("first_year_aired"),
			    					 rs.getInt("last_year_aired"));
				items.add(show);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return items;
	}

	@Override
	public List<ShowPlaylist> getPlaylistsByUser(int userId) throws SQLException {
		List<ShowPlaylist> playlists = new ArrayList<>();
		String sql = "SELECT id, title FROM shows_playlists WHERE user_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				List<Show> items = getShowsInPlaylist(rs.getInt("id"));
				ShowPlaylist playlist = new ShowPlaylist(rs.getString("title"), items);
				playlists.add(playlist);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return playlists;
	}

	@Override
	public void deletePlaylist(int playlistId) throws SQLException {
		String sql = "DELETE FROM shows_playlist_items WHERE playlist_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, playlistId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
