package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.dao.impl.GamesDAOImpl;
import application.db.DatabaseConnection;
import application.db.DatabaseInitializer;
import application.model.Game;

public class Main {
	
	public static void main(String[] args) throws SQLException {
		// establish connection to database and create tables
		Connection conn = DatabaseConnection.connect();
		
		// registers a user and returns its id
		DatabaseInitializer.initialize(conn);
		int userId = DatabaseInitializer.registerUser(conn, "Amiel");
		
		// instantiate DAO
		GamesDAOImpl gamesDAOImpl = new GamesDAOImpl(conn, userId);
		
		// instantiate a Game to add
		Game game = new Game("Minecraft", "Completed", 9.0, "Mojang", 60);
		
		// must confirm data is added through terminal
		try {
			gamesDAOImpl.addGame(game);
			System.out.println("Game added successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		// get "Minecraft" game
		Game output = gamesDAOImpl.getGameByTitle("Minecraft");
		
		// print output
		System.out.println(output.getTitle() + "\t" + output.getStatus() + "\t" + output.getUserRating() + "\t"
						 + output.getDeveloper() + "\t" + output.getAvgPlaytimeMins());
		
		//instantiate and add another game
		game = new Game("VALORANT", "Completed", -1, "Rito", 999);
		try {
			gamesDAOImpl.addGame(game);
			System.out.println("Game added successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		// get "2nd" game
		output = gamesDAOImpl.getGameById(2);
		System.out.println(output.getTitle() + "\t" + output.getStatus() + "\t" + output.getUserRating() + "\t"
						 + output.getDeveloper() + "\t" + output.getAvgPlaytimeMins());
		
		// return all games
		System.out.println("### DISPLAY ALL GAMES ###");
		List<Game> outputs = new ArrayList<Game>();
		outputs = gamesDAOImpl.getGamesByUser(userId);
		for (Game entry : outputs) {
			System.out.println(entry.getTitle() + "\t" + entry.getStatus() + "\t" + entry.getUserRating() + "\t"
					 + entry.getDeveloper() + "\t" + entry.getAvgPlaytimeMins());
		}
	}
}