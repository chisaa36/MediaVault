package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.GamePlaylistDAO;
import application.model.Game;
import application.model.GamePlaylist;
import application.model.Status;

public class GamePlaylistDAOImpl implements GamePlaylistDAO {

	private Connection conn;
	private int userId;
	private GamesDAOImpl gamesDAOImpl;
	
	public GamePlaylistDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
		this.gamesDAOImpl = new GamesDAOImpl(conn, userId);
	}
	
	@Override
	public int createPlaylist(String name) throws SQLException {
		int playlistId = -1;
		
		// add game to `games_playlists` table
		String sql = "INSERT INTO games_playlists (user_id, title) VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, userId);
			stmt.setString(2, name);
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
	        if (keys.next()) {
	        	playlistId = keys.getInt(1);
	        }
	        System.out.println("Game added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Playlist '" + name + "' is already added.");
		    } else {
		        System.out.println(e.getMessage()); // print other unexpected errors normally
		    }
		}
	
		return playlistId;
	}

	@Override
	public void addGameToPlaylist(int playlistId, Game game) throws SQLException {
		
		// find game_id
		int gameId = gamesDAOImpl.getGameId(game.getTitle());
		
		if (gameId != -1) {
			String sql = "INSERT INTO games_playlists_items (playlist_id, game_id) VALUES (?, ?)";
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, userId);
				stmt.setInt(2, gameId);
				stmt.executeUpdate();
				
		        System.out.println("Game added successfully.");
			} catch (SQLException e) {
				if (e.getMessage().contains("UNIQUE constraint failed")) {
			        System.out.println("Game '" + game.getTitle() + "' is already added.");
			    } else {
			        System.out.println(e.getMessage()); // print other unexpected errors normally
			    }
			}
		}
		else {
			System.out.println("Game '" + game.getTitle() + "' not found.");
		}
	}

	@Override
	public void addGamesToPlaylist(int playlistId, List<Game> games) throws SQLException {
		for (Game game : games) {
			// find game_id
			int gameId = gamesDAOImpl.getGameId(game.getTitle());
			
			if (gameId != -1) {
				String sql = "INSERT INTO games_playlists_items (playlist_id, game_id) VALUES (?, ?)";
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)){
					stmt.setInt(1, userId);
					stmt.setInt(2, gameId);
					stmt.executeUpdate();
					
				} catch (SQLException e) {
					if (e.getMessage().contains("UNIQUE constraint failed")) {
				        System.out.println("Game '" + game.getTitle() + "' is already added.");
				    } else {
				        System.out.println(e.getMessage()); // print other unexpected errors normally
				    }
				}
			}
			else {
				System.out.println("Game '" + game.getTitle() + "' not found.");
			}
		}
	}

	@Override
	public void removeGameFromPlaylist(int playlistId, int gameId) throws SQLException {	
		String sql = "DELETE FROM games_playlists_items WHERE playlist_id = ?, game_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.setInt(2, gameId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public List<Game> getGamesInPlaylist(int playlistId) throws SQLException {
		List<Game> items = new ArrayList<Game>();
		
		String sql = """
				SELECT * FROM games_playlists gp
				JOIN games_playlists_items gpi
				ON gp.id = gpi.playlist_id
				JOIN games g
				ON gpi.game_id = g.id
				WHERE userId = ? AND playlistId = ?
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, playlistId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Game game = new Game(rs.getString("title"),
									 Status.fromDbString(rs.getString("status")),
									 rs.getDouble("user_rating"),
									 rs.getString("developer"),
									 rs.getInt("avg_playtime_mins"));
				
				items.add(game);
			}
		}
		
		return items;
	}

	@Override
	public List<GamePlaylist> getPlaylistsByUser(int userId) throws SQLException {
		List<GamePlaylist> playlists = new ArrayList<>();
		
		String sql = """
				SELECT * FROM games_playlists gp
				JOIN games_playlists_items gpi
				ON gp.id = gpi.playlist_id
				WHERE userId = ?
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				List<Game> items = getGamesInPlaylist(rs.getInt("playlist_id"));
				
				GamePlaylist playlist = new GamePlaylist(rs.getString("title"), items);
				
				playlists.add(playlist);
			}
		}
		
		return playlists;
	}

	@Override
	public void deletePlaylist(int playlistId) throws SQLException {
		String sql = "DELETE FROM games_playlists_items WHERE playlist_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
