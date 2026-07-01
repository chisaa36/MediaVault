package application.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
	
	public void initialize(Connection conn) throws SQLException {
		try (Statement stmt = conn.createStatement()){
			stmt.execute("PRAGMA foreign_keys = ON");
			
			// create users table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS users (
				id INTEGER PRIMARY KEY AUTOINCREMENT
				username TEXT
			)""");
			
			// create games table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS games (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT,
				genre TEXT,
				status TEXT,
				user_rating REAL,
				developer TEXT,
				avg_playtime_mins INTEGER
			)""");
			
			// create music table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS music (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT,
				genre TEXT,
				status TEXT,
				user_rating REAL,
				artist TEXT,
				year_released INTEGER,
				runtime_mins INTEGER
			)""");
			
			// create series table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS series (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT,
				genre TEXT,
				status TEXT,
				user_rating REAL
			)""");
			
			// create seasons table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS seasons (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				series_id INTEGER NOT NULL,
				title TEXT,
				status TEXT,
				
				FOREIGN KEY (series_id) REFERENCES series(id)
			)""");
			
			// create episodes table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS episodes (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				season_id INTEGER NOT NULL,
				episode_number INTEGER NOT NULL,
				title TEXT,
				genre TEXT,
				status TEXT,
				user_rating REAL,
							
				FOREIGN KEY (season_id) REFERENCES seasons(id)
			)""");
			
			// create games_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS games_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT,
				
				FOREIGN KEY (users_id) REFERENCES users(id)
			)""");
			
			// create music_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS music_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT,
				user_rating REAL,
				
				FOREIGN KEY (users_id) REFERENCES users(id)
			)""");
			
			// create series_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS series_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT,
				
				FOREIGN KEY (users_id) REFERENCES users(id)
			)""");
			
			// create games_lists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS games_lists (
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				game_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES games_playlists(id),
				FOREIGN KEY (game_id) REFERENCES games(id)
			)""");
			
			// create music_lists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS music_lists (
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				music_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES music_playlists(id),
				FOREIGN KEY (music_id) REFERENCES music(id)
 			)""");
			
			// create series_lists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS series_lists (
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				series_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES series_playlists(id),
				FOREIGN KEY (series_id) REFERENCES series(id)
			)""");
		}
	}
}
