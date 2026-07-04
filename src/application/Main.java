package application;

import java.util.List;
import application.api.SpotifyClient;
import application.model.Song;

import application.db.DatabaseConnection;
import application.db.DatabaseInitializer;
import application.dao.UserDAO;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws SQLException {
		
		int checkCtr = 0;
		boolean securityCheck;
		String username, password, checkPassword, choice;
		Scanner scanner = new Scanner(System.in);
		
		try{
		    Connection conn = DatabaseConnection.connect();

		    DatabaseInitializer initializer = new DatabaseInitializer();
		    initializer.initialize(conn);

		    UserDAO userDAO = new UserDAO(conn);
		    
		    System.out.println();
		    
		    do
		    {
		    	System.out.println("* * * * * * * * * * * MEDIA VAULT * * * * * * * * * * *");
		    	System.out.println("* - - - - - - - - - - Login  Menu - - - - - - - - - - *");
			    System.out.println("* [1] Login as Existing User");
			    System.out.println("* [2] Register as New User");
			    System.out.println("* [X] Exit Login Menu");
			    System.out.print("*\n* Enter your choice: ");
			    choice = scanner.nextLine();
		    	
			    if(choice.equals("1"))
			    {
			    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
			    	System.out.println("\n= = = = = = = = = = = = SIGN IN = = = = = = = = = = = =");
			    	do
			    	{
				    	System.out.print("= Username: ");
						username = scanner.nextLine();		
						System.out.print("= Password: ");
						password = scanner.nextLine();
						System.out.println("= ");
						
						securityCheck = userDAO.login(username, password);
						
						if(userDAO.login(username, password))
						{
							System.out.println("= Login successful!");
							System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
							vaultMenu(username, userDAO.getUserID(username), conn, scanner);
						}
						else
						{
							System.out.println("= Invalid username or password!");
							System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
						}
						
						checkCtr++;
						
			    	} while(!securityCheck && checkCtr != 3);
			    		
			    	if(!securityCheck)
			    	{
			    		System.out.println("\n    Too many attempts. Redirecting to login menu...\n");
			    		checkCtr = 1;
			    	}
			    }
			    else if(choice.equals("2"))
			    {
			    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
			    	System.out.println("\n= = = = = = = = = = = = SIGN UP = = = = = = = = = = = =");
			    	do
			    	{
				    	System.out.print("= Enter Username: ");
				    	username = scanner.nextLine();
				    	System.out.print("= Enter Password: ");
				    	password = scanner.nextLine();
				    	System.out.print("= Confirm Password: ");
				    	checkPassword = scanner.nextLine();
				    	System.out.println("= ");
				    	
				    	securityCheck = password.equals(checkPassword) && !userDAO.usernameExists(username);
				    	
				    	if(securityCheck)
				    	{
				    		System.out.println("= Registration successful!");
							System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
				    	}
				    	else if(!password.equals(checkPassword))
				    	{
				    		System.out.println("= Password does not match. Please try again!");
				    		System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	}
				    	else
				    	{
				    		System.out.println("= Username already exists. Please try again!");
				    		System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	}
				    		
				    	checkCtr++;
				    		
			    	} while(!securityCheck && checkCtr != 3);
			    	
			    	if(!securityCheck)
			    	{
			    		System.out.println("\n    Too many attempts. Redirecting to login menu...\n");
			    		checkCtr = 1;
			    	}
			    }
			    else if(choice.equals("X"))
			    {
			    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
			    	System.out.println("           Thank you for using Media Vault!");
			    }
			    else
			    {
			    	System.out.println("*\n* Invalid Input!");
			    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
			    }
			    
		    } while(!choice.equals("X"));
		    
		    
		    scanner.close();
		}
		
		catch(SQLException e){
		    e.printStackTrace();
		}	
	}
	
	public static void vaultMenu(String username, int user_id, Connection conn, Scanner scanner){
		
		String choice;
		    
	    do
	    {
	    	System.out.println("* * * * * * * * * * * MEDIA VAULT * * * * * * * * * * *");
	    	System.out.println("* - - - - - - - - - - Vault  Menu - - - - - - - - - - *");
		    System.out.println("* [1] Settings");
		    System.out.println("* [2] Song Vault");
		    System.out.println("* [3] Game Vault");
		    System.out.println("* [4] Show Vault");
		    System.out.println("* [<] Back to Login Menu");
		    System.out.print("*\n* Enter your choice: ");
		    choice = scanner.nextLine();
	    	
		    if(choice.equals("1"))
		    {
		    	runUserSettings();
		    }
		    else if(choice.equals("2"))
		    {
		    	getSongVault(user_id, conn, scanner);
		    }
		    else if(choice.equals("3"))
		    {
		    	
		    }
		    else if(choice.equals("4"))
		    {
		    	
		    }
		    else if(choice.equals("<"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
		    }
		    else
		    {
		    	System.out.println("*\n* Invalid Input!");
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
		    }
		    
	    } while(!choice.equals("<"));
	}
	
	public static void runUserSettings(){
		
		System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    	System.out.println("\n= = = = = = = = = =  USER SETTINGS  = = = = = = = = = =");
    	System.out.println("= [1] Change Username");
    	System.out.println("= [2] Change Username");
    	System.out.println("= [3] Delete User Profile");
	}
	
	public static void getSongVault(int user_id, Connection conn, Scanner scanner){
		
		//SongDAO songDAO = new SongDAO(conn);
		String choice, choice2, choice3, search;
		
		SpotifyClient spotifyClient = new SpotifyClient("266e17b3bb8e432d82b803598192fc5f", "f38ada98c91f4bf9bf6ed4f4490d7b12");
		
		do
		{
			System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	    	System.out.println("\n= = = = = = = = = = = SONG  VAULT = = = = = = = = = = =");
	    	System.out.println("= [1] My Songs");
	    	System.out.println("= [2] My Playlists");
	    	System.out.println("= [<] Back to Vault Menu");
	    	System.out.print("=\n= Enter your choice: ");
		    choice = scanner.nextLine();
		    
		    if(choice.equals("1"))
		    {
		    	do
		    	{
			    	System.out.println("= - - - - - - - - - =  MY  SONGS  = - - - - - - - - - =");
			    	System.out.println("= [1] Completed/Reviewed");
			    	System.out.println("= [2] In Progress");
			    	System.out.println("= [3] Planned");
			    	System.out.println("= [*] View All My Songs");
			    	System.out.println("= [+] Add Song");
			    	System.out.println("= [-] Remove Song");
			    	System.out.println("= [<] Back to Song Vault");
			    	System.out.print("=\n= Enter your choice: ");
				    choice2 = scanner.nextLine();
				    
				    if(choice2.equals("1"))
				    {
				    	
				    }
				    else if(choice2.equals("2"))
				    {
				    	
				    }
				    else if(choice2.equals("3"))
				    {
				    	
				    }
				    else if(choice2.equals("*"))
				    {
				    	
				    }
				    else if(choice2.equals("+"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
				    	
				    	System.out.print(" Search Song: ");
				        search = scanner.nextLine();

				        try {
				            List<Song> results = spotifyClient.searchTracks(search);

				            System.out.println();
				            System.out.println("-----------------------------------------------------------------");
				            System.out.printf("| %-3s | %-23s | %-6s | %-20s |%n", "No.", "Title", "Length", "Artist");
				            System.out.println("-----------------------------------------------------------------");

				            int i = 1;

				            for (Song song : results) {
				                System.out.printf("| %-3d | %-23s | %-6s | %-20s |%n",
				                        i++,
				                        fitToSpace(song.getTitle(), 23),
				                        song.getRuntime(),
				                        fitToSpace(song.getArtist(), 20));
				            }

				            System.out.println("-----------------------------------------------------------------");

				        } catch (Exception e) {
				            System.out.println("Could not connect to Spotify.");
				            e.printStackTrace();
				        }
				    	
				        System.out.println();
				        
				    }
				    else if(choice2.equals("-"))
				    {
				    	
				    }
				    else if(choice2.equals("<"))
				    {
				    	
				    }
				    else
				    {
				    	
				    }
		    	} while(!choice2.equals("<"));
		    }
		    else if(choice.equals("2"))
		    {
			    System.out.println("= - - - - - - - - =  MY  PLAYLISTS  = - - - - - - - - =");
			    System.out.println("= [1] View Playlists");
		    	System.out.println("= [+] Create a Song Playlist");
		    	System.out.println("= [-] Remove a Song Playlist");
		    	System.out.println("= [<] Back to Song Vault");
		    	System.out.print("=\n= Enter your choice: ");
		    	choice3 = scanner.nextLine();
		    }
		    else if(choice.equals("<"))
		    {
		    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
		    }
		    else
		    {
		    	System.out.println("=\n= Invalid Input!");
		    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
		    }
		    
		} while(!choice.equals("<"));
	}
	
	public static void getGameVault(){
		
		// amiel this is your code from main para sa game
		
		/*
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
		*/
	}
	
	private static String fitToSpace(String text, int width) {
	    if (text == null) {
	        return "";
	    }

	    if (text.length() <= width) {
	        return text;
	    }

	    return text.substring(0, width - 3) + "...";
	}
}