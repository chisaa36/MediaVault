package application.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
	
	public void initialize(Connection conn) throws SQLException {
		try (Statement stmt = conn.createStatement()){
			stmt.execute("PRAGMA foreign_keys = ON");
			
			// create users table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS users (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				username TEXT UNIQUE,
				password TEXT NOT NULL
			)""");
			
			// create games table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS games (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL UNIQUE,
				status TEXT,
				user_rating REAL,
				developer TEXT,
				avg_playtime_mins INTEGER,
				review TEXT
			)""");
			
			// create songs table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL UNIQUE,
				status TEXT,
				user_rating REAL,
				album TEXT,
				artist TEXT,
				year_released INTEGER,
				runtime_seconds INTEGER,
				review TEXT
			)""");
			
			// create shows table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL UNIQUE,
				status TEXT,
				user_rating REAL,
				num_of_seasons INTEGER,
				num_of_episodes INTEGER,
				avg_mins_per_ep INTEGER,
				first_year_aired INTEGER,
				last_year_aired INTEGER,
				review TEXT
			)""");
			
			// create seasons table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS seasons (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				show_id INTEGER NOT NULL,
				title TEXT,
				status TEXT,
				
				FOREIGN KEY (show_id) REFERENCES shows(id)
			)""");
			
			// create episodes table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS episodes (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				season_id INTEGER NOT NULL,
				episode_number INTEGER NOT NULL,
				title TEXT,
				status TEXT,
				user_rating REAL,
							
				FOREIGN KEY (season_id) REFERENCES seasons(id)
			)""");
			
			// create games_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS games_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT NOT NULL,
				UNIQUE (user_id, title),
				
				FOREIGN KEY (user_id) REFERENCES users(id)
			)""");
			
			// create songs_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT NOT NULL,
				user_rating REAL,
				UNIQUE (user_id, title),
				
				FOREIGN KEY (user_id) REFERENCES users(id)
			)""");
			
			// create shows_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT NOT NULL,
				UNIQUE (user_id, title),
				
				FOREIGN KEY (user_id) REFERENCES users(id)
			)""");
			
			// create games_playlist_items table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS games_playlist_items (
				playlist_id INTEGER NOT NULL,
			    game_id INTEGER NOT NULL,
			    
			    PRIMARY KEY (playlist_id, game_id),
				FOREIGN KEY (playlist_id) REFERENCES games_playlists(id),
				FOREIGN KEY (game_id) REFERENCES games(id)
			)""");
			
			// create songs_playlist_items table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_playlist_items (
				playlist_id INTEGER NOT NULL,
				song_id INTEGER NOT NULL,
				
				PRIMARY KEY (playlist_id, song_id),
				FOREIGN KEY (playlist_id) REFERENCES songs_playlists(id),
				FOREIGN KEY (song_id) REFERENCES songs(id)
 			)""");
			
			// create shows_playlist_items table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_playlist_items (
				playlist_id INTEGER NOT NULL,
				show_id INTEGER NOT NULL,
				
				PRIMARY KEY (playlist_id, show_id),
				FOREIGN KEY (playlist_id) REFERENCES shows_playlists(id),
				FOREIGN KEY (show_id) REFERENCES shows(id)
			)""");
			
			// create genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS genres (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				genre TEXT NOT NULL UNIQUE
			)""");
			
			// create game_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS game_genres (
				game_id INTEGER NOT NULL,
				genre_id INTEGER NOT NULL,
				
				PRIMARY KEY (game_id, genre_id),
				FOREIGN KEY (game_id) REFERENCES games(id),
				FOREIGN KEY (genre_id) REFERENCES genres(id)
			)""");
			
			// create songs_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_genres (
				song_id INTEGER NOT NULL,
				genre_id INTEGER NOT NULL,
				
				PRIMARY KEY (song_id, genre_id),
				FOREIGN KEY (song_id) REFERENCES songs(id),
				FOREIGN KEY (genre_id) REFERENCES genres(id)
			)""");
			
			// create shows_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_genres (
				show_id INTEGER NOT NULL,
				genre_id INTEGER NOT NULL,
				
				PRIMARY KEY (show_id, genre_id),
				FOREIGN KEY (show_id) REFERENCES shows(id),
				FOREIGN KEY (genre_id) REFERENCES genres(id)
			)""");
			System.out.println("Tables initialized.");
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static int registerUser(Connection conn, String username, String password) throws SQLException {
		int userId = -1;
	
		// add user to `users` table
		String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.executeUpdate();
			ResultSet keys = pstmt.getGeneratedKeys();
	        if (keys.next()) {
	            userId = keys.getInt(1);
	        }
		} catch (SQLException e) {
			if (e.getMessage().contains("UNIQUE constraint failed")) {
		        System.out.println("Username '" + username + "' is already taken.");
		    } else {
		        System.out.println(e.getMessage());
		    }
		}
		
		// add "all" entries category if user is added
		if (userId != -1) {
			sql = "INSERT OR IGNORE INTO games_playlists (user_id, title) VALUES (?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, userId);
				pstmt.setString(2, "all_games");
				pstmt.executeUpdate();
			} catch (SQLException e) {
			    System.out.println(e.getMessage());
			}
			
			sql = "INSERT OR IGNORE INTO songs_playlists (user_id, title) VALUES (?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, userId);
				pstmt.setString(2, "all_songs");
				pstmt.executeUpdate();
			} catch (SQLException e) {
			    System.out.println(e.getMessage());
			}
			
			sql = "INSERT OR IGNORE INTO shows_playlists (user_id, title) VALUES (?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, userId);
				pstmt.setString(2, "all_shows");
				pstmt.executeUpdate();
			} catch (SQLException e) {
			    System.out.println(e.getMessage());
			}
		}
		
		System.out.println("User registered.");
		
		return userId;
	}
}
