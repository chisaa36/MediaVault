package application.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
	
	public static void initialize(Connection conn) throws SQLException {
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
				status TEXT,
				user_rating REAL,
				developer TEXT,
				avg_playtime_mins INTEGER
			)""");
			
			// create songs table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT,
				status TEXT,
				user_rating REAL,
				artist TEXT,
				year_released INTEGER,
				runtime_mins INTEGER
			)""");
			
			// create shows table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT,
				status TEXT,
				user_rating REAL
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
				title TEXT,
				
				FOREIGN KEY (users_id) REFERENCES users(id)
			)""");
			
			// create songs_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_playlists (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				user_id INTEGER NOT NULL,
				title TEXT,
				user_rating REAL,
				
				FOREIGN KEY (users_id) REFERENCES users(id)
			)""");
			
			// create shows_playlists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_playlists (
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
			
			// create songs_lists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_lists (
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				songs_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES songs_playlists(id),
				FOREIGN KEY (songs_id) REFERENCES songs(id)
 			)""");
			
			// create shows_lists table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_lists (
				playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
				shows_id INTEGER NOT NULL,
				
				FOREIGN KEY (playlist_id) REFERENCES shows_playlists(id),
				FOREIGN KEY (shows_id) REFERENCES shows(id)
			)""");
			
			// create genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS genres (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				genre TEXT NOT NULL UNIQUE,
			)""");
			
			// create game_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS game_genres (
				game_id INTEGER PRIMARY KEY NOT NULL,
				genre_id INTEGER PRIMARY KEY NOT NULL,
				
				FOREIGN KEY (game_id) REFERENCES games(id),
				FOREIGN KEY (genre_id) REFERENCES genre(id)
			)""");
			
			// create songs_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS songs_genres (
				songs_id INTEGER PRIMARY KEY NOT NULL,
				genre_id INTEGER PRIMARY KEY NOT NULL,
				
				FOREIGN KEY (songs_id) REFERENCES songs(id),
				FOREIGN KEY (genre_id) REFERENCES genre(id)
			)""");
			
			// create shows_genres table
			stmt.execute("""
				CREATE TABLE IF NOT EXISTS shows_genres (
				shows_id INTEGER PRIMARY KEY NOT NULL,
				genre_id INTEGER PRIMARY KEY NOT NULL,
				
				FOREIGN KEY (shows_id) REFERENCES shows(id),
				FOREIGN KEY (genre_id) REFERENCES genre(id)
			)""");
		}
	}
}
