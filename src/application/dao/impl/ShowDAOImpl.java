package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.ShowDAO;
import application.model.Episode;
import application.model.Season;
import application.model.Show;
import application.model.Status;

public class ShowDAOImpl implements ShowDAO {
	
	private Connection conn;
	private int userId;

	public ShowDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}

	@Override
	public int addShow(Show show) throws SQLException {
		int showId = -1;
		String sql = "INSERT INTO shows (title, status, user_rating, num_of_seasons, num_of_episodes, avg_mins_per_ep, first_year_aired, last_year_aired) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		    stmt.setString(1, show.getTitle());
		    stmt.setString(2, show.getStatus().toDbString());
		    stmt.setDouble(3, show.getUserRating());
		    stmt.setInt(4, show.getNumOfSeasons());
		    stmt.setInt(5, show.getNumOfEpisodes());
		    stmt.setInt(6, show.getAvgMinsPerEp());
		    stmt.setInt(7, show.getFirstYearAired());
		    stmt.setInt(8, show.getLastYearAired());
		    stmt.executeUpdate();
		    
			ResultSet keys = stmt.getGeneratedKeys();
	        if (keys.next()) {
	            showId = keys.getInt(1);
	        }
	        System.out.println("Show added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Show '" + show.getTitle() + "' is already added.");
		    } else {
		        System.out.println(e.getMessage());
		    }
		}
		
		if (showId != -1) {
			sql = "INSERT OR IGNORE INTO shows_playlist_items (playlist_id, show_id)"
				+ " VALUES (?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, 1);
				stmt.setInt(2, showId);
				stmt.executeUpdate();
			}
		}
	
		return showId;
	}

	@Override
	public int addSeason(Season season) throws SQLException {
		int seasonId = -1;
		String sql = "INSERT INTO seasons (title, status) VALUES (?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, season.getTitle());
			stmt.setString(2, season.getStatus().toDbString());
			stmt.executeUpdate();
			
			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				seasonId = keys.getInt(1);
			}
			System.out.println("Season added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
				System.out.println("Season '" + season.getTitle() + "' is already added.");
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return seasonId;
	}

	@Override
	public int addEpisode(Episode episode) throws SQLException {
		int episodeId = -1;
		String sql = "INSERT INTO episodes (title, status, user_rating, review) VALUES (?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, episode.getTitle());
			stmt.setString(2, episode.getStatus().toDbString());
			stmt.setDouble(3, episode.getUserRating());
			stmt.setString(4, episode.getReview());
			stmt.executeUpdate();
			
			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				episodeId = keys.getInt(1);
			}
			System.out.println("Episode added successfully.");
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
				System.out.println("Episode '" + episode.getTitle() + "' is already added.");
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return episodeId;
	}

	@Override
	public Show getShowById(int id) throws SQLException {
		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.creator, s.avg_runtime_mins
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN shows s
				ON spi.show_id = s.id
				WHERE sp.user_id = ? AND s.id = ?
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Show(rs.getString("title"),
			   					Status.fromDbString(rs.getString("status")),
			   					rs.getDouble("user_rating"),
			   					rs.getString("review"),
			   					rs.getInt("num_of_seasons"),
			   					rs.getInt("num_of_episodes"),
			   					rs.getInt("avg_mins_per_ep"),
			   					rs.getInt("first_year_aired"),
			   					rs.getInt("last_year_aired"));
			}
			else {
				System.out.println("Show not found");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	@Override
	public Show getShowByTitle(String title) throws SQLException {		
		String sql = """
		        SELECT s.id, s.title, s.status, s.user_rating, s.creator, s.avg_runtime_mins
		        FROM shows_playlists sp
		        JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
		        JOIN shows s ON spi.show_id = s.id
		        WHERE s.title = ? AND sp.user_id = ?
		    """;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, title);
		    stmt.setInt(2, userId);
		    ResultSet rs = stmt.executeQuery();
		    if (rs.next()) {
		    	return new Show(rs.getString("title"),
   					 			Status.fromDbString(rs.getString("status")),
			   					rs.getDouble("user_rating"),
			   					rs.getString("review"),
			   					rs.getInt("num_of_seasons"),
			   					rs.getInt("num_of_episodes"),
			   					rs.getInt("avg_mins_per_ep"),
			   					rs.getInt("first_year_aired"),
			   					rs.getInt("last_year_aired"));
		    } else {
		    	System.out.println("Show not found.");
		    }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	@Override
	public List<Show> getShowsByUser(int userId) throws SQLException {
		List<Show> shows = new ArrayList<>();

		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.creator, s.avg_runtime_mins
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN shows s
				ON spi.show_id = s.id
				WHERE sp.user_id = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
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
				
				shows.add(show);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return shows;
	}

	@Override
	public List<Season> getSeasonsByShowId(int showId) throws SQLException {
		List<Season> seasons = new ArrayList<>();
		String sql = """
				SELECT se.id, se.title, se.status
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				INNER JOIN shows s ON spi.show_id = s.id
				INNER JOIN show_seasons ss ON s.id = ss.show_id
				INNER JOIN seasons se ON ss.season_id = se.id
				WHERE sp.user_id = ? AND s.id = ?
				""";
				
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, showId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				List<Episode> episodes = getEpisodesBySeason(showId, rs.getInt("id"));
				
				Season season = new Season(rs.getString("title"),
										   Status.fromDbString(rs.getString("status")),
										   episodes);
				
				seasons.add(season);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return seasons;
	}

	@Override
	public List<Season> getSeasonsByShowTitle(String title) throws SQLException {
		List<Season> seasons = new ArrayList<>();
		String sql = """
				SELECT s.id AS show_id, se.id, se.title, se.status
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				INNER JOIN shows s ON spi.show_id = s.id
				INNER JOIN show_seasons ss ON s.id = ss.show_id
				INNER JOIN seasons se ON ss.season_id = se.id
				WHERE sp.user_id = ? AND s.title = ?
				""";
				
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, title);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				List<Episode> episodes = getEpisodesBySeason(rs.getInt("show_id"), rs.getInt("id"));
				
				Season season = new Season(rs.getString("title"),
										   Status.fromDbString(rs.getString("status")),
										   episodes);
				seasons.add(season);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return seasons;
	}

	@Override
	public Episode getEpisodeById(int id) throws SQLException {
		String sql = """
				SELECT e.title, e.status, e.user_rating, e.review
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				INNER JOIN shows s ON spi.show_id = s.id
				INNER JOIN show_seasons ss ON s.id = ss.show_id
				INNER JOIN seasons se ON ss.season_id = se.id
				INNER JOIN season_episodes sepi ON se.id = sepi.season_id
				INNER JOIN episodes e ON sepi.episode_id = e.id
				WHERE sp.user_id = ? AND e.id = ?
				""";
				
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Episode(rs.getString("title"),
								   Status.fromDbString(rs.getString("status")),
								   rs.getDouble("user_rating"),
								   rs.getString("review"));
			} else {
				System.out.println("Episode not found");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public Episode getEpisodeByTitle(String title) throws SQLException {
		String sql = """
				SELECT e.title, e.status, e.user_rating, e.review
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				INNER JOIN shows s ON spi.show_id = s.id
				INNER JOIN show_seasons ss ON s.id = ss.show_id
				INNER JOIN seasons se ON ss.season_id = se.id
				INNER JOIN season_episodes sepi ON se.id = sepi.season_id
				INNER JOIN episodes e ON sepi.episode_id = e.id
				WHERE sp.user_id = ? AND e.title = ?
				""";
				
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, title);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Episode(rs.getString("title"),
								   Status.fromDbString(rs.getString("status")),
								   rs.getDouble("user_rating"),
								   rs.getString("review"));
			} else {
				System.out.println("Episode not found.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<Episode> getEpisodesBySeason(int showId, int seasonId) throws SQLException {
		List<Episode> items = new ArrayList<Episode>();
		
		String sql = """
			SELECT e.id, e.title, e.status, e.user_rating, e.review
			FROM shows_playlists sp
			JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
			JOIN shows s ON spi.show_id = s.id
			JOIN show_seasons ss ON s.id = ss.show_id
			JOIN seasons se ON ss.season_id = se.id
			JOIN season_episodes sepi ON se.id = sepi.season_id
			JOIN episodes e ON sepi.episode_id = e.id
			WHERE sp.user_id = ? AND s.id = ? AND se.id = ?
			""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, showId);
			stmt.setInt(3, seasonId);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Episode episode = new Episode(rs.getString("title"),
											 Status.fromDbString(rs.getString("status")),
											 rs.getDouble("user_rating"),
											 rs.getString("review"));
				
				items.add(episode);
			}
		}
		
		return items;
	}

	@Override
	public void updateShowStatus(String title, String status) throws SQLException {
		int showId = getShowId(title);
		
		if (showId == -1) {
			System.out.println("Show not found: " + title);
		} else {
			String checkSql = "SELECT user_id FROM shows_reviews WHERE user_id = ? AND show_id = ?";
			boolean reviewExists = false;
			
			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, showId);
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}
			
			if (reviewExists) {
				String updateSql = "UPDATE shows_reviews SET status = ? WHERE user_id = ? AND show_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setString(1, status);
					stmt.setInt(2, userId);
					stmt.setInt(3, showId);
					stmt.executeUpdate();
				}
				
				if (!status.equalsIgnoreCase(Status.COMPLETED.toDbString())) {
					String clearSql = "UPDATE shows_reviews SET user_rating = NULL, review = NULL WHERE user_id = ? AND show_id = ?";
					try (PreparedStatement stmt = conn.prepareStatement(clearSql)) {
						stmt.setInt(1, userId);
						stmt.setInt(2, showId);
						stmt.executeUpdate();
					}
				}
			} else {
				String insertSql = "INSERT INTO shows_reviews (user_id, show_id, status) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, showId);
					stmt.setString(3, status);
					stmt.executeUpdate();
				}
			}
			
			System.out.println("Status updated for '" + title + "' to: " + status);
		}
	}

	@Override
	public void updateShowRating(String title, double rating) throws SQLException {
		int showId = getShowId(title);
		
		if (showId == -1) {
			System.out.println("Show not found: " + title);
		} else {
			String checkSql = "SELECT user_id FROM shows_reviews WHERE user_id = ? AND show_id = ?";
			boolean reviewExists = false;
			
			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, showId);
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}
			
			if (reviewExists) {
				String updateSql = "UPDATE shows_reviews SET user_rating = ? WHERE user_id = ? AND show_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setDouble(1, rating);
					stmt.setInt(2, userId);
					stmt.setInt(3, showId);
					stmt.executeUpdate();
				}
			} else {
				String insertSql = "INSERT INTO shows_reviews (user_id, show_id, user_rating) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, showId);
					stmt.setDouble(3, rating);
					stmt.executeUpdate();
				}
			}
			
			System.out.println("Rating updated for: " + title);
		}
	}

	@Override
	public void updateEpisodeStatus(String title, String status) throws SQLException {
		int episodeId = getEpisodeId(title);
		
		if (episodeId == -1) {
			System.out.println("Episode not found: " + title);
		} else {
			String checkSql = "SELECT user_id FROM episodes_reviews WHERE user_id = ? AND episode_id = ?";
			boolean reviewExists = false;
			
			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, episodeId);
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}
			
			if (reviewExists) {
				String updateSql = "UPDATE episodes_reviews SET status = ? WHERE user_id = ? AND episode_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setString(1, status);
					stmt.setInt(2, userId);
					stmt.setInt(3, episodeId);
					stmt.executeUpdate();
				}
			} else {
				String insertSql = "INSERT INTO episodes_reviews (user_id, episode_id, status) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, episodeId);
					stmt.setString(3, status);
					stmt.executeUpdate();
				}
			}
			
			System.out.println("Episode status updated for: " + title);
		}
	}

	@Override
	public void updateEpisodeRating(String title, double rating) throws SQLException {
		int episodeId = getEpisodeId(title);
		
		if (episodeId == -1) {
			System.out.println("Episode not found: " + title);
		} else {
			String checkSql = "SELECT user_id FROM episodes_reviews WHERE user_id = ? AND episode_id = ?";
			boolean reviewExists = false;
			
			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, episodeId);
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}
			
			if (reviewExists) {
				String updateSql = "UPDATE episodes_reviews SET user_rating = ? WHERE user_id = ? AND episode_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setDouble(1, rating);
					stmt.setInt(2, userId);
					stmt.setInt(3, episodeId);
					stmt.executeUpdate();
				}
			} else {
				String insertSql = "INSERT INTO episodes_reviews (user_id, episode_id, user_rating) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, episodeId);
					stmt.setDouble(3, rating);
					stmt.executeUpdate();
				}
			}
			
			System.out.println("Episode rating updated for: " + title);
		}
	}

	@Override
	public void updateReview(String title, String review) throws SQLException {
		int showId = getShowId(title);
		
		if (showId == -1) {
			System.out.println("Show not found: " + title);
		} else {
			String checkSql = "SELECT user_id FROM shows_reviews WHERE user_id = ? AND show_id = ?";
			boolean reviewExists = false;
			
			try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
				stmt.setInt(1, userId);
				stmt.setInt(2, showId);
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						reviewExists = true;
					}
				}
			}
			
			if (reviewExists) {
				String updateSql = "UPDATE shows_reviews SET review = ? WHERE user_id = ? AND show_id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
					stmt.setString(1, review);
					stmt.setInt(2, userId);
					stmt.setInt(3, showId);
					stmt.executeUpdate();
				}
			} else {
				String insertSql = "INSERT INTO shows_reviews (user_id, show_id, review) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
					stmt.setInt(1, userId);
					stmt.setInt(2, showId);
					stmt.setString(3, review);
					stmt.executeUpdate();
				}
			}
			
			System.out.println("Review updated for: " + title);
		}
	}

	@Override
	public void deleteShow(String title) throws SQLException {
		String sql = "DELETE FROM shows WHERE title = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, title);
	        stmt.executeUpdate();
	        System.out.println("Show '" + title + "' deleted");
	    }
	}

	@Override
	public List<Show> getShowsByStatus(Status status) throws SQLException {
		List<Show> shows = new ArrayList<>();
		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.creator, s.avg_runtime_mins
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				INNER JOIN shows s ON spi.show_id = s.id
				WHERE sp.user_id = ? AND s.status = ?
				""";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, status.toDbString());
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
				
				shows.add(show);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return shows;
	}

	@Override
	public List<Show> getShowsByGenre(String genre) throws SQLException {
		List<Show> shows = new ArrayList<>();
		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.creator, s.avg_runtime_mins
				FROM shows_playlists sp
				INNER JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				INNER JOIN shows s ON spi.show_id = s.id
				INNER JOIN show_genres sg ON s.id = sg.show_id
				INNER JOIN genres g ON sg.genre_id = g.id
				WHERE sp.user_id = ? AND g.genre = ?
				""";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, genre);
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
				
				shows.add(show);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return shows;
	}

	private int getShowId(String title) throws SQLException {
		String sql = """
				SELECT s.id
				FROM shows_playlists sp
				JOIN shows_playlist_items spi ON sp.id = spi.playlist_id
				JOIN shows s ON spi.show_id = s.id
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

	private int getEpisodeId(String title) throws SQLException {
		String sql = "SELECT id FROM episodes WHERE title = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, title);
			
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		}
		
		return -1;
	}
}
