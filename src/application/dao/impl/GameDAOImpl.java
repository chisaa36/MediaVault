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
import application.model.Status;

public class GameDAOImpl implements GameDAO{
	
	private Connection conn;
	private int userId;
	
	public GameDAOImpl(Connection conn, int userId) {
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
			stmt.setString(2, game.getStatus().toDbString());
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
		// having a gameId == -1 means that game already exists.
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
		String sql = """
				SELECT g.id, g.title, g.status, g.user_rating, g.developer, g.avg_playtime_mins
				FROM games_playlists gp
				INNER JOIN games_playlist_items gpi
				ON gp.id = gpi.playlist_id
				INNER JOIN games g
				ON gpi.game_id = g.id
				WHERE gp.user_id = ? AND g.id = ?
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, gameId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Game(rs.getString("title"),
								Status.fromDbString(rs.getString("status")),
								rs.getDouble("user_rating"),
								rs.getString("review"),
								rs.getString("developer"),
								rs.getInt("avg_playtime_mins"));
			}
			else {
				System.out.println("Game not found");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	@Override
	public Game getGameByTitle(String title) throws SQLException {		
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
		    	return new Game(rs.getString("title"),
								Status.fromDbString(rs.getString("status")),
								rs.getDouble("user_rating"),
								rs.getString("review"),
								rs.getString("developer"),
								rs.getInt("avg_playtime_mins"));
		    } else {
		    	System.out.println("Game not found.");
		    }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	@Override
	public List<Game> getGamesByUser(int userId) throws SQLException {
		List<Game> games = new ArrayList<>();

		String sql = """
				SELECT g.id, g.title, g.status, g.user_rating, g.developer, g.avg_playtime_mins
				FROM games_playlists gp
				INNER JOIN games_playlist_items gpi
				ON gp.id = gpi.playlist_id
				INNER JOIN games g
				ON gpi.game_id = g.id
				WHERE gp.user_id = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Game game = new Game(rs.getString("title"),
									 Status.fromDbString(rs.getString("status")),
									 rs.getDouble("user_rating"),
									 rs.getString("review"),
									 rs.getString("developer"),
									 rs.getInt("avg_playtime_mins"));
				
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
		        if (keys.next()) {
		            genreId = keys.getInt(1);
		        } else {
		            sql = "SELECT id FROM genres WHERE genre = ?";
		            try (PreparedStatement selStmt = conn.prepareStatement(sql)) {
		                selStmt.setString(1, genre);
						
		                ResultSet rs = selStmt.executeQuery();
		                if (rs.next()) {
		                    genreId = rs.getInt("id");
		                }
		            }
		        }
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
	public void updateStatus(String title, Status status) throws SQLException {
		// get game id
		int gameId = getGameId(title);

		if (gameId == -1) {
			System.out.println("Game not found: " + title);
		}
		else {
			// check if review exists
			String checkSql = "SELECT user_id FROM games_reviews WHERE user_id = ? AND game_id = ?";
			boolean reviewExists = false;

			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, gameId);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}

			if (reviewExists) {
				String updateSql = "UPDATE games_reviews SET status = ? WHERE user_id = ? AND game_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setString(1, status.toDbString());
					stmt.setInt(2, userId);
					stmt.setInt(3, gameId);
					stmt.executeUpdate();
				}

				// if changing TO completed, rating/review can now be added
				// if changing AWAY from completed, clear rating and review
				if (status != Status.COMPLETED) {
					String clearSql = "UPDATE games_reviews SET user_rating = NULL, review = NULL WHERE user_id = ? AND game_id = ?";
					try (PreparedStatement stmt = conn.prepareStatement(clearSql)) {
						stmt.setInt(1, userId);
						stmt.setInt(2, gameId);
						stmt.executeUpdate();
					}
				}

			} else {
				String insertSql = "INSERT INTO games_reviews (user_id, game_id, status) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, gameId);
					stmt.setString(3, status.toDbString());
					stmt.executeUpdate();
				}
			}

			System.out.println("Status updated for '" + title + "' to: " + status.toDbString());
		}
	}

	@Override
	public void updateGameRating(String title, double rating) throws SQLException {
		int gameId = getGameId(title);

		if (gameId == -1) {
			System.out.println("Game not found: " + title);
		} else {
			String checkSql = "SELECT user_id FROM games_reviews WHERE user_id = ? AND game_id = ?";
			boolean reviewExists = false;

			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, gameId);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}

			if (reviewExists) {
				// update existing review
				String updateSql = "UPDATE games_reviews SET user_rating = ? WHERE user_id = ? AND game_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setDouble(1, rating);
					stmt.setInt(2, userId);
					stmt.setInt(3, gameId);
					stmt.executeUpdate();
				}
			} else {
				// insert new review
				String insertSql = "INSERT INTO games_reviews (user_id, game_id, user_rating) VALUES (?, ?, ?)";
				try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
					pstmt.setInt(1, userId);
					pstmt.setInt(2, gameId);
					pstmt.setDouble(3, rating);
					pstmt.executeUpdate();
				}
			}

			System.out.println("Rating updated for: " + title);
		}       
	}

	@Override
	public void updateReview(String title, String review) throws SQLException {
		// first get the game id
		int gameId = getGameId(title);

		if (gameId == -1) {
			System.out.println("Game not found: " + title);
		}
		else {
			String checkSql = "SELECT user_id FROM games_reviews WHERE user_id = ? AND game_id = ?";
			boolean reviewExists = false;

			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, gameId);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					reviewExists = true;
				}
			}

			if (reviewExists) {
				String updateSql = "UPDATE games_reviews SET review = ? WHERE user_id = ? AND game_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setString(1, review);
					stmt.setInt(2, userId);
					stmt.setInt(3, gameId);
					stmt.executeUpdate();
				}
			} else {
				String insertSql = "INSERT INTO games_reviews (user_id, game_id, review) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, gameId);
					stmt.setString(3, review);
					stmt.executeUpdate();
				}
			}

			System.out.println("Review updated for: " + title);
		}	
	}

	@Override
	public void deleteGame(String title) throws SQLException {
		String sql = "DELETE FROM games WHERE title = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, title);
	        stmt.executeUpdate();
	        System.out.println("Game '" + title + "' deleted");
	    }
	}
	
	@Override
	public int getGameId(String title) throws SQLException {
		String sql = """
			SELECT g.id
			FROM games_playlists gp
			JOIN games_playlist_items gpi
			ON gp.id = gpi.playlist_id
			JOIN games g
			ON gpi.game_id = g.id
			WHERE gp.user_id = ? AND gp.id = 1 AND g.title = ?
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