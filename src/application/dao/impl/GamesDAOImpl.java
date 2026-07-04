package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.GameDAO;
import application.model.Game;

public class GamesDAOImpl implements GameDAO{
	
	private Connection conn;
	private int userId;
	
	public GamesDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}

	@Override
	public int addGame(Game game) throws SQLException {
		int gameId = -1;
		String sql = "INSERT INTO games (title, status, user_rating, developer, avg_playtime_mins) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			// add game to `games` table
			stmt.setString(1, game.getTitle());
			stmt.setString(2, game.getStatus());
			stmt.setDouble(3, game.getUserRating());
			stmt.setString(4, game.getDeveloper());
			stmt.setInt(5, game.getAvgPlaytimeMins());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
	        if (keys.next()) {
	            gameId = keys.getInt(1);
	        }
	        System.out.println("Game added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Game '" + game.getTitle() + "' is already added.");
		    } else {
		        System.out.println(e.getMessage()); // print other unexpected errors normally
		    }
		}
		
		// add game to "all_games" playlist	if game is added
		if (gameId != -1) {
			sql = "INSERT OR IGNORE INTO games_playlist_items (playlist_id, game_id)"
				+ " VALUES (?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, 1);
				stmt.setInt(2, gameId);
				stmt.executeUpdate();
			}
		}
	
		return gameId;
	}

	@Override
	public Game getGameById(int gameId) throws SQLException {
		Game game;
		int avgPlaytimeMins = 0;
		String title = null, status = null, developer = null;
		double userRating = 0;
		
		String sql = "SELECT g.id, g.title, g.status, g.user_rating, g.developer, g.avg_playtime_mins" 
				   + " FROM games_playlists gp"
				   + " INNER JOIN games_playlist_items gpi"
				   + " ON gp.id = gpi.playlist_id"
				   + " INNER JOIN games g"
				   + " ON gpi.game_id = g.id"
				   + " WHERE gp.user_id = ? AND g.id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, gameId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				title = rs.getString("title");
				status = rs.getString("status");
				userRating = rs.getDouble("user_rating");
				developer = rs.getString("developer");
				avgPlaytimeMins = rs.getInt("avg_playtime_mins");
			}
			else {
				System.out.println("Game not found");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		game = new Game(title, status, userRating, developer, avgPlaytimeMins);
		
		return game;
	}

	@Override
	public Game getGameByTitle(String title) throws SQLException {
		Game game;
		int avgPlaytimeMins = 0;
		String status = null, developer = null;
		double userRating = 0;
		
		String sql = """
		        SELECT g.id, g.title, g.status, g.user_rating, g.developer, g.avg_playtime_mins
		        FROM games_playlists gp
		        JOIN games_playlist_items gpi ON gp.id = gpi.playlist_id
		        JOIN games g ON gpi.game_id = g.id
		        WHERE g.title = ? AND gp.user_id = ?
		    """;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, title);
		    stmt.setInt(2, userId);
		    ResultSet rs = stmt.executeQuery();
		    if (rs.next()) {
		    	status = rs.getString("status");
				userRating = rs.getDouble("user_rating");
				developer = rs.getString("developer");
				avgPlaytimeMins = rs.getInt("avg_playtime_mins");
		    } else {
		    	System.out.println("Game not found.");
		    }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		game = new Game(title, status, userRating, developer, avgPlaytimeMins);
		
		return game;
	}

	@Override
	public List<Game> getGamesByUser(int userId) throws SQLException {
		List<Game> games = new ArrayList<>();

		String sql = "SELECT g.id, g.title, g.status, g.user_rating, g.developer, g.avg_playtime_mins" 
				   + " FROM games_playlists gp"
				   + " INNER JOIN games_playlist_items gpi"
				   + " ON gp.id = gpi.playlist_id"
				   + " INNER JOIN games g"
				   + " ON gpi.game_id = g.id"
				   + " WHERE gp.user_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Game game = new Game(
					rs.getString("title"),
					rs.getString("status"),
					rs.getDouble("user_rating"),
					rs.getString("developer"),
					rs.getInt("avg_playtime_mins")
				);
				
				games.add(game);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return games;
	}
	
	@Override
	public int addGenre(String string) throws SQLException {
		int genreId = -1;
		String[] genreList = string.split(" ");
		
		for (String genre : genreList) {
			String sql = "INSERT OR IGNORE INTO genres (genre) VALUES (?)";
			
		    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		        stmt.setString(1, genre);
		        stmt.executeUpdate();
		        ResultSet keys = stmt.getGeneratedKeys();
		        genreId = keys.getInt(1);
		    }
		}
		
		return genreId;
	}
	
	@Override
	public void linkGameGenre(int gameId, int genreId) throws SQLException {
	    String sql = "INSERT OR IGNORE INTO game_genres (game_id, genre_id) VALUES (?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, gameId);
	        stmt.setInt(2, genreId);
	        stmt.executeUpdate();
	    }
	}

	@Override
	public void updateGameRating(String title, double rating) throws SQLException {
		String sql = "UPDATE games "
				   +" SET user_rating = ?"
				   +" WHERE title = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setDouble(1, rating);
			stmt.setString(2, title);
			stmt.executeUpdate();
			System.out.println("Game updated: " + title);
		}
	}

	@Override
	public void updateReview(String title, String review) throws SQLException {
		String sql = "UPDATE games "
				   +" SET review = ?"
				   +" WHERE title = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, review);
			stmt.setString(2, title);
			stmt.executeUpdate();
			System.out.println("Game updated: " + title);
		}
	}

	@Override
	public void deleteGame(String title) throws SQLException {
		String sql = "DELETE FROM games"
				   +" WHERE title = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, title);
	        stmt.executeUpdate();
	        System.out.println("Game deleted: " + title);
	    }
	}
}