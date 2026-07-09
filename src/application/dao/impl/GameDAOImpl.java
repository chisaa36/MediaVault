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
		String sql = "INSERT OR IGNORE INTO games (title, developer, avg_playtime_mins) VALUES (?, ?, ?)";

	    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        stmt.setString(1, game.getTitle());
	        stmt.setString(2, game.getDeveloper());
	        stmt.setInt(3, game.getAvgPlaytimeMins());
	        stmt.executeUpdate();
	        
			ResultSet keys = stmt.getGeneratedKeys();
	        if (keys.next()) {
	            gameId = keys.getInt(1);
	        }
	        System.out.println("Game added successfully.");
		} catch (SQLException e) {
		    e.printStackTrace();
		    throw e;
		}
	    	
	    	// a duplicate game would have a gameId = 0
			if (gameId <= 0) {
		    System.out.println("Game '" + game.getTitle() + "' is already added.");
		    
		    sql = "SELECT id FROM games WHERE title = ?";
		    
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setString(1, game.getTitle());
		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                gameId = rs.getInt("id");
		            }
		        }
		    }
		}
		
		if (gameId == -1) {
		    System.out.println("Could not resolve game id for '" + game.getTitle() + "'.");
		    return -1;
		}
		
		int playlistId = -1;
		sql = "SELECT id FROM games_playlists WHERE user_id = ? AND title = 'all_games'";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		    stmt.setInt(1, userId);
		    try (ResultSet rs = stmt.executeQuery()) {
		        if (rs.next()) {
		            playlistId = rs.getInt("id");
		        }
		    }
		}
		
		if (playlistId == -1) {
		    sql = "INSERT INTO games_playlists (user_id, title) VALUES (?, 'all_games')";
		    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		        stmt.setInt(1, userId);
		        
		        stmt.executeUpdate();
		        try (ResultSet keys = stmt.getGeneratedKeys()) {
		            if (keys.next()) {
		                playlistId = keys.getInt(1);
		            }
		        }
		    }
		}
		
		sql = "INSERT OR IGNORE INTO games_reviews (user_id, game_id, status, user_rating, review) VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setInt(2, gameId);
	        stmt.setString(3, game.getStatus().toDbString());
	        stmt.setDouble(4, game.getUserRating());
	        stmt.setString(5, game.getReview());
	        stmt.executeUpdate();
	    }
		
		sql = "INSERT OR IGNORE INTO games_playlist_items (playlist_id, game_id) VALUES (?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, playlistId);
			stmt.setInt(2, gameId);
			stmt.executeUpdate();
		}
		return gameId;
	}

	/**
	 * Queries the database using a series of {@code JOIN}s from {@code games_playlists} up to 
	 * {@code games_reviews} to retrieve a game specified by its {@code title}
	 * @param title the title of the game
	 * @pre <ul> 
	 * <li>The database {@code conn} must be open, active, and valid.</li>
	 * <li>The {@code title} parameter must not be an empty or null String.</li>
	 * </ul>
	 * @post <ul> 
	 * <li>Returns the {@code Game} with its corresponding features</li>
	 * <li>If the game is not found within the database, null is returned.</li>
	 * <li>The state of the database remains unchanged.</li>
	 * </ul>
	 * @return the {@code Game} that is being retrieved by the user;
	 * returns null if the game is not found.
	 * @throws SQLException if a database access error or the connection is lost during the 
	 * method's execution
	 */
	@Override
	public Game getGameById(int gameId) throws SQLException {
		String sql = """
				SELECT g.id, g.title, r.status, r.user_rating, r.review, g.developer, g.avg_playtime_mins
				FROM games_playlists gp
				INNER JOIN games_playlist_items gpi ON gp.id = gpi.playlist_id
				INNER JOIN games g ON gpi.game_id = g.id
				LEFT JOIN games_reviews r ON g.id = r.game_id AND r.user_id = gp.user_id
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

	/**
	 * Queries the database using a series of {@code JOIN}s from {@code games_playlists} up to 
	 * {@code games_reviews} to retrieve a game specified by its {@code title}
	 * @param title the title of the game
	 * @pre <ul> 
	 * <li>The database {@code conn} must be open, active, and valid.</li>
	 * <li>The {@code title} parameter must not be an empty or null String.</li>
	 * </ul>
	 * @post <ul> 
	 * <li>Returns the {@code Game} with its corresponding features</li>
	 * <li>If the game is not found within the database, null is returned.</li>
	 * <li>The state of the database remains unchanged.</li>
	 * </ul>
	 * @return the {@code Game} that is being retrieved by the user;
	 * returns null if the game is not found.
	 * @throws SQLException if a database access error or the connection is lost during the 
	 * method's execution
	 */
	@Override
	public Game getGameByTitle(String title) throws SQLException {		
		String sql = """
		        SELECT g.id, g.title, r.status, r.user_rating, r.review, g.developer, g.avg_playtime_mins
		        FROM games g
		        LEFT JOIN games_reviews r ON g.id = r.game_id AND r.user_id = ?
		        WHERE g.title = ?
		    """;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		    stmt.setInt(1, userId);
			stmt.setString(2, title);
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
	
	/**
	 * Queries the database using a series of {@code JOIN}s from {@code games_playlists} up to 
	 * {@code games_reviews} to retrieve a user's list of {@code Game}s based on their id
	 * @param userId the user's id
	 * @pre <ul> 
	 * <li>The database {@code conn} must be open, active, and valid.</li>
	 * <li>The {@code userId} parameter must be valid, that is, id > 0 and a user exists in that
	 * id.</li>
	 * </ul>
	 * @post <ul> 
	 * <li>Returns a {@code List<Game>} containing all games of the specified {@code userId} across
	 * all playlists.</li>
	 * <li>If the user does not have any games, an empty {@code List<Game>} is returned.</li>
	 * <li>The state of the database remains unchanged.</li>
	 * </ul>
	 * @return the {@code List<Game>} containing the user's games across all playlists
	 * @throws SQLException if a database access error or the connection is lost during the 
	 * method's execution
	 */
	@Override
	public List<Game> getGamesByUser(int userId) throws SQLException {
		List<Game> games = new ArrayList<>();

		String sql = """
				SELECT g.id, g.title, r.status, r.user_rating, r.review, g.developer, g.avg_playtime_mins
		        FROM games_playlists gp
		        INNER JOIN games_playlist_items gpi ON gp.id = gpi.playlist_id
		        INNER JOIN games g ON gpi.game_id = g.id
		        LEFT JOIN games_reviews r ON g.id = r.game_id AND r.user_id = gp.user_id
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
	
	/**
	 * Processes a space-separated string of genres. New genres are inserted into the database and
	 * retrieving the id of the last processed genre.
	 * @param string a space-separated String containing the genre or list of genres to add
	 * @pre <ul> 
	 * <li>The database {@code conn} must be open, active, and valid.</li>
	 * <li>The {@code string} parameter must not be null nor empty wherein genre names are
	 * separated by a single space.</li>
	 * </ul>
	 * @post <ul> 
	 * <li>Any genre names from the input string that does not already exist will be added to the
	 * database.</li>
	 * <li>Existing genre names, however, will be ignored by the insert query because of the 
	 * {@code IGNORE} keyword. Nevertheless, the genre id will still be resolved by the 
	 * {@code SELECT} query.</li>
	 * <li>If the input string is empty, the database state remains unchanged.</li>
	 * </ul>
	 * @return the integer id of the last processed genre in the sequence;
	 * returns -1 if no genres were successfully processed.
	 * @throws SQLException if a database access error or the connection is lost during the 
	 * method's execution
	 */
	@Override
	public int addGenre(String string) throws SQLException {
		int lastGenreId = -1;
		String[] genreList = string.split(" ");
		
		for (String genre : genreList) {
			String sql = "INSERT OR IGNORE INTO genres (genre) VALUES (?)";
			
		    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		        stmt.setString(1, genre);
		        stmt.executeUpdate();

		        try (ResultSet keys = stmt.getGeneratedKeys()) {
			        if (keys.next()) {
			            lastGenreId = keys.getInt(1);
			        } else {
			            sql = "SELECT id FROM genres WHERE genre = ?";
			            
			            try (PreparedStatement selStmt = conn.prepareStatement(sql)) {
			                selStmt.setString(1, genre);
			                
			                try (ResultSet rs = selStmt.executeQuery()) {
				                if (rs.next()) {
				                    lastGenreId = rs.getInt("id");
				                }
			                }
			            }
			        }
		        }
		    }
		}
		
		return lastGenreId;
	}
	
	/**
	 * 
	 */
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
		// get the game id
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

				try (ResultSet rs = stmt.executeQuery()) {
				    if (rs.next()) {
				        reviewExists = true;
				    }
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