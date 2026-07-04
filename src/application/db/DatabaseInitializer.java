package application.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
	
	public static void initialize(Connection conn) throws SQLException {
		try (Statement stmt = conn.createStatement()){
			stmt.execute("PRAGMA foreign_keys = ON");
			
			// create users table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS users (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				username TEXT
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
				artist TEXT,
				year_released INTEGER,
				runtime_mins INTEGER,
				review TEXT
			)""");
			
			// create shows table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL UNIQUE,
				status TEXT,
				user_rating REAL,
				review TEXT
			)""");
			
			// create seasons table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS seasons (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				shows_id INTEGER NOT NULL,
				title TEXT,
				status TEXT,
				
				FOREIGN KEY (shows_id) REFERENCES shows(id)
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
				title TEXT UNIQUE,
				
				FOREIGN KEY (user_id) REFERENCES users(id)
			)""");
			
			// create songs_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT UNIQUE,
				user_rating REAL,
				
				FOREIGN KEY (user_id) REFERENCES users(id)
			)""");
			
			// create shows_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT UNIQUE,
				
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
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				songs_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES songs_playlists(id),
				FOREIGN KEY (songs_id) REFERENCES songs(id)
 			)""");
			
			// create shows_playlist_items table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_playlist_items (
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				shows_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES shows_playlists(id),
				FOREIGN KEY (shows_id) REFERENCES shows(id)
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
				FOREIGN KEY (genre_id) REFERENCES genre(id)
			)""");
			
			// create songs_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_genres (
				songs_id INTEGER NOT NULL,
				genre_id INTEGER NOT NULL,
				
				PRIMARY KEY (songs_id, genre_id),
				FOREIGN KEY (songs_id) REFERENCES songs(id),
				FOREIGN KEY (genre_id) REFERENCES genre(id)
			)""");
			
			// create shows_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_genres (
				shows_id INTEGER NOT NULL,
				genre_id INTEGER NOT NULL,
				
				PRIMARY KEY (shows_id, genre_id),
				FOREIGN KEY (shows_id) REFERENCES shows(id),
				FOREIGN KEY (genre_id) REFERENCES genre(id)
			)""");
			System.out.println("Tables initialized.");
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static int registerUser(Connection conn, String username) throws SQLException {
		int userId;
	
		// add user to `users` table
		String sql = "INSERT INTO users (username) VALUES (?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		}
		
		sql = "SELECT id FROM users WHERE username = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, username);
		    ResultSet rs = pstmt.executeQuery();
		    userId = rs.getInt("id");
		}
		
		// add "all" entries category
		sql = "INSERT INTO games_playlists (user_id, title) VALUES (?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.setString(2, "all_games");
			pstmt.executeUpdate();
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		}
		
		sql = "INSERT INTO songs_playlists (user_id, title) VALUES (?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.setString(2, "all_songs");
			pstmt.executeUpdate();
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		}
		
		sql = "INSERT INTO shows_playlists (user_id, title) VALUES (?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.setString(2, "all_shows");
			pstmt.executeUpdate();
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		}
		
		System.out.println("User registered.");
		
		return userId;
	}
}
