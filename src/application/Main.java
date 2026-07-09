package application;

import java.util.List;
import java.util.ArrayList;
import application.api.SpotifyClient;
import application.model.Song;
import application.model.SongPlaylist;
import application.model.Status;
import application.dao.impl.SongDAOImpl;
import application.dao.impl.SongPlaylistDAOImpl;
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
						
						if(securityCheck)
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
			    		checkCtr = 0;
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
				    		userDAO.addUser(username, password);
				    		// DatabaseInitializer.registerUser(conn, username);
				    		
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
			    		checkCtr = 0;
			    	}
			    }
			    else if(choice.equals("X"))
			    {
			    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
			    	System.out.println("           Thank you for using Media Vault!");
			    }
			    else
			    {
			    	System.out.println("*\n* Invalid input! Please try again.");
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
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
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
		    	System.out.println("*\n* Invalid input! Please try again.");
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
		
		int playlistChoice = -1, triggerValue = 0, playlistId = -1;
		SongDAOImpl songDAO = new SongDAOImpl(conn, user_id);
		SongPlaylistDAOImpl songPlaylistDAO = new SongPlaylistDAOImpl(conn, user_id);
		String choice, choice2, choice7, title;
		List<Song> songs = new ArrayList<>();
		List<SongPlaylist> playlists = new ArrayList<>();
		
		SpotifyClient spotifyClient = new SpotifyClient("266e17b3bb8e432d82b803598192fc5f", "f38ada98c91f4bf9bf6ed4f4490d7b12");
		
		do
		{
			
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
			    	System.out.println("= [1] Completed");
			    	System.out.println("= [2] In Progress");
			    	System.out.println("= [3] Planned");
			    	System.out.println("= [*] View All My Songs");
			    	System.out.println("= [+] Add Song");
			    	System.out.println("= [<] Back to Song Vault");
			    	System.out.print("=\n= Enter your choice: ");
				    choice2 = scanner.nextLine();
				    
				    if(choice2.equals("1"))
				    {	
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 1;
				    	
				    	doSongEdits(user_id, songDAO, songs, songPlaylistDAO, playlists, playlistChoice, scanner, triggerValue, spotifyClient);
				    }
				    else if(choice2.equals("2"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 2;
				    	
				    	doSongEdits(user_id, songDAO, songs, songPlaylistDAO, playlists, playlistChoice, scanner, triggerValue, spotifyClient);
				    }
				    else if(choice2.equals("3"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 3;
				    	
				    	doSongEdits(user_id, songDAO, songs, songPlaylistDAO, playlists, playlistChoice, scanner, triggerValue, spotifyClient);
				    }
				    else if(choice2.equals("*"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 4;
				    	
				    	doSongEdits(user_id, songDAO, songs, songPlaylistDAO, playlists, playlistChoice, scanner, triggerValue, spotifyClient);
				    }
				    else if(choice2.equals("+"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
				    	
				    	doSongSearch(spotifyClient, scanner, songDAO, songPlaylistDAO, user_id, playlistId, triggerValue);
				    }
				    else if(choice2.equals("<"))
				    {
				    	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
				    }
				    else
				    {
				    	System.out.println("=\n= Invalid input! Please try again.");
				    }
		    	} while(!choice2.equals("<"));
		    }
		    else if(choice.equals("2"))
		    {
		    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
		    	
		    	try {
		    		
			    	do
			    	{	
			    			playlists = songPlaylistDAO.getPlaylistsByUser(user_id);
			    			
			    			System.out.println();
				            System.out.println("-------------------------------------------------------------------------------------------------------------------");
				            System.out.printf("| %-3s | %-23s | %-11s | %-9s | %-11s | %-7s | %-29s |%n", "No.", "Title", "Total Songs", "Completed", "In Progress", "Planned", "Avg. Rating (Completed Songs)");
				            System.out.println("-------------------------------------------------------------------------------------------------------------------");
			    			
				            int ctr2 = 1;
				            
			    			for(SongPlaylist sp: playlists)
			    			{
			    				int completeTemp = songPlaylistDAO.countStatusedSongs(sp.getPlaylistId(), Status.COMPLETED),
			    					inProgressTemp = songPlaylistDAO.countStatusedSongs(sp.getPlaylistId(), Status.IN_PROGRESS),
			    					plannedTemp = songPlaylistDAO.countStatusedSongs(sp.getPlaylistId(), Status.PLANNED);
			    				
			    				String ratingTemp = String.valueOf(songPlaylistDAO.calculateAvgRating(sp.getPlaylistId()));
			    				
			    				if(completeTemp == 0)
			    					ratingTemp = "/-no completed songs yet-/";
			    				
			    				if(sp.getTitle().equals("all_songs"))
			    					sp.setTitle("All Songs");
			    				
			    				System.out.printf("| %-3s | %-23s | %-11s | %-9s | %-11s | %-7s | %-29s |%n", ctr2++,
			    																	   fitToSpace(sp.getTitle(), 23),
			    																	   String.valueOf(completeTemp + inProgressTemp + plannedTemp),
			    																	   String.valueOf(completeTemp),
			    																	   String.valueOf(inProgressTemp),
			    																	   String.valueOf(plannedTemp),
			    																	   ratingTemp ); 
			    			}
			    			
			    			System.out.println("-------------------------------------------------------------------------------------------------------------------\n");
			    		
					    System.out.println("= - - - - - - - - =  MY  PLAYLISTS  = - - - - - - - - =");
					    System.out.println("= [#] View/Update Playlist (Input the Playlist No.)");
					    System.out.println("= [+] Add a Song Playlist");
				    	System.out.println("= [<] Back to Song Vault");
				    	System.out.print("=\n= Enter your choice: ");
				    	choice7 = scanner.nextLine();
				    	

						if (choice7.equals("<")) {
						    System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
						}
						else if (choice7.equals("+")) {
							System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
				    		
				    		System.out.println(" - {CREATE PLAYLIST}");
				    		System.out.print(" - Enter Title: ");
				    		title = scanner.nextLine();
				    		
				    		try {
					    		if(songPlaylistDAO.createPlaylist(title, user_id))
					    		{
						    		System.out.println(" - ");
						    		System.out.println(" - " + title + " successfully added!");
					    		}
				    		}
				    		catch(SQLException e){
				    			
				    			System.out.println(" - ");
					    		System.out.println(" - " + title + "was not added successfully.");
				    		}
						}
						else {
							System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
							
						    try {
						        playlistChoice = Integer.parseInt(choice7);
						
						        if (1 <= playlistChoice && playlistChoice <= playlists.size()) {
						            SongPlaylist sp = playlists.get(playlistChoice - 1);
						            songs = songPlaylistDAO.getSongsInPlaylist(sp.getPlaylistId());
						
						            doSongEdits(user_id, songDAO, songs, songPlaylistDAO, playlists, playlistChoice, scanner, 5, spotifyClient);
						        }
						        else {
						            System.out.println("=\n= Invalid input! Please try again.");
						            System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
						        }
						    }
						    catch (NumberFormatException e) {
						    }
						}
					    	
			    	} while(!choice7.equals("<"));
		    	}
		    	
	    		catch (SQLException e)
	    		{
	    			System.out.println("\n 0 playlists loaded.\n");
	    			System.out.println(e.getMessage());
	    		}
		    }
		    else if(choice.equals("<"))
		    {
		    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
		    }
		    else
		    {
		    	System.out.println("=\n= Invalid input! Please try again.");
		    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
		    }
		    
		} while(!choice.equals("<"));
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
	
	public static void printSongs(List<Song> songs) {

	    System.out.println();
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-3s | %-23s | %-6s | %-4s | %-17s | %-13s | %-20s | %-22s |%n", "No.", "Title", "Length", "Year", "Artist", "Status", "My Rating", "Reviewed");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");

	    int ctr = 1;

	    for (Song song : songs)
	    	System.out.printf("| %-3d | %-23s | %-6s | %-4s | %-17s | %-13s | %-20s | %-22s |%n", ctr++, fitToSpace(song.getTitle(), 23), song.getRuntimeString(), String.valueOf(song.getYearReleased()), fitToSpace(song.getArtist(), 17), song.getStatus().toDbString(), song.getUserRatingString(), song.getReviewedStatus());

	    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------\n");
	}
	
	public static void doSongEdits(int user_id, SongDAOImpl songDAO, List<Song> songs, SongPlaylistDAOImpl songPlaylistDAO, List<SongPlaylist> playlists, int playlistChoice, Scanner scanner, int triggerValue, SpotifyClient spotifyClient) {
		
		boolean allChecker = false;
		double rating = 0.0;
		String choice7, choice8, choice5, choice4, choice6, review; 
		int songChoice = 0, songState;
		
		do
    	{
			SongPlaylist sp = null;

		    if (triggerValue == 5)
		        sp = playlists.get(playlistChoice - 1);

		    songs.clear();

		    allChecker = false;

		    if (triggerValue == 5 && (sp.getTitle().equalsIgnoreCase("All Songs") || sp.getTitle().equalsIgnoreCase("all_songs")))
		        allChecker = true;
    		
    		try {
    			
    			if(triggerValue == 1)
    			{
		            for (Song song : songDAO.getSongsByUser(user_id))
		                if (song.getStatus() == Status.COMPLETED)
		                    songs.add(song);
		            
		            printSongs(songs);
    			}
    			else if(triggerValue == 2)
    			{
    				for (Song song : songDAO.getSongsByUser(user_id))
		                if (song.getStatus() == Status.IN_PROGRESS)
		                    songs.add(song);
    				
    				printSongs(songs);
    			}
    			else if(triggerValue == 3)
    			{
    				for (Song song : songDAO.getSongsByUser(user_id))
		                if (song.getStatus() == Status.PLANNED)
		                    songs.add(song);
    				
    				printSongs(songs);
    			}
    			else if(triggerValue == 4)
    			{
    				for (Song song : songDAO.getSongsByUser(user_id))
    					songs.add(song);
    				
    				printSongs(songs);
    			}
    			else if(triggerValue == 5)
    			{
    				
    				int completeTemp = songPlaylistDAO.countStatusedSongs(sp.getPlaylistId(), Status.COMPLETED),
    					inProgressTemp = songPlaylistDAO.countStatusedSongs(sp.getPlaylistId(), Status.IN_PROGRESS),
    					plannedTemp = songPlaylistDAO.countStatusedSongs(sp.getPlaylistId(), Status.PLANNED),
    					totalTemp = completeTemp + inProgressTemp + plannedTemp;
    				
    				String ratingTemp = String.valueOf(songPlaylistDAO.calculateAvgRating(sp.getPlaylistId()));
    				
    				if(completeTemp == 0)
    					ratingTemp = "N/A";
    				
    				songs = songPlaylistDAO.getSongsInPlaylist(sp.getPlaylistId());
    				
    				printSongs(songs);
    		    	
    		    	System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    		    	System.out.println("  * PLAYLIST TITLE: " + sp.getTitle());
    		    	System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
    		    	System.out.println("  * Total Songs: " + totalTemp);
    		    	System.out.println("  * # of Completed Songs: " + completeTemp);
    		    	System.out.println("  * # of Songs In Progress: " + inProgressTemp);
    		    	System.out.println("  * # of Planned Songs: " + plannedTemp);
    		    	System.out.println("  * Average Rating across Completed Entries: " + ratingTemp);
    		    	System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
    			}

	        }
	    	catch (SQLException e) {
	            System.out.println("Could not load songs.");
	        }
    		
    		if(triggerValue == 1)
    		{
    			System.out.println("= - - - - - - - - = COMPLETED SONGS = - - - - - - - - =");
    		}
    		else if(triggerValue == 2)
    		{
    			System.out.println("= - - - - - - - = SONGS  IN  PROGRESS = - - - - - - - =");
    		}
    		else if(triggerValue == 3)
    		{
    			System.out.println("= - - - - - - - - =  PLANNED SONGS  = - - - - - - - - =");
    		}
    		else if(triggerValue == 4)
    		{
    			System.out.println("= - - - - - - - - - =  ALL SONGS  = - - - - - - - - - =");
    		}
    		
    		if(triggerValue != 5)
    		{
	    		System.out.println("= [#] View/Update Song Status (Input the Track No.)");
	    		System.out.println("= [<] Back to My Songs");
	    		System.out.println("= ");
	    		System.out.print("= Enter your choice: ");
    		}
    		else
    		{
    			System.out.println("  * [#] View/Update Song Status (Input the Track No.)");
    			if(!allChecker)
    			{
	    			System.out.println("  * [+] Add Song to Playlist");
	    			System.out.println("  * [-] Delete Playlist");
    			}
    			System.out.println("  * [<] Back to My Playlists");
    			System.out.println("  * ");
    			System.out.print("  * Enter your choice: ");
    		}
    		
    		choice7 = scanner.nextLine();
    		
    		try {
            	songChoice = Integer.parseInt(choice7);
            }
            catch (NumberFormatException e) {
            	songChoice = 0;
            }
    		
    		if(choice7.equals("<"))
    		{
    			if(triggerValue != 5)
    				System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
    			else
    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    		}
    		else if(1 <= songChoice && songChoice <= songs.size())
    		{
    			if(triggerValue != 5)
    				System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
    			else
    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
    			Song song = songs.get(songChoice-1);  
    			
    			do
    			{
	    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	    			System.out.println("  * SONG " + songChoice + ": " + song.getTitle() + " by " + song.getArtist());
	    			System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
	    			System.out.println("  * Year Released: " + String.valueOf(song.getYearReleased()));
	    			System.out.println("  * Status: " + song.getStatus().toDbString());
	    			
	    			if(song.getStatus() == Status.COMPLETED)
	    			{
	    				System.out.println("  * My Rating: " + song.getUserRatingString());
	    				if(song.getReview().equals(""))
		    				System.out.println("  * My Review: Unreviewed");
		    			else
		    				System.out.println("  * My Review: " + song.getReview());
	    			}
	    			else if(song.getStatus() == Status.PLANNED || song.getStatus() == Status.IN_PROGRESS)
	    			{
	    				System.out.println("  * My Rating: /-complete to rate song-/");
	    				System.out.println("  * My Review: /-complete to review song-/");
	    			}
	    			
	    			System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
	    			System.out.println("  * [1] Change Status");
	    			
	    			if(song.getStatus() == Status.COMPLETED)
	    			{
		    			System.out.println("  * [2] Change Rating");
		    			
		    			if(song.getReview().equals(""))
		    				System.out.println("  * [3] Add Review");
		    			else
		    				System.out.println("  * [3] Change Review");
	    			}
	    			
	    			System.out.println("  * [-] Remove Song");
	    			
	    			if(triggerValue == 1)
	    			{
	    				System.out.println("  * [<] Back to Completed Songs");
	    			}
	    			else if(triggerValue == 2)
	    			{
	    				System.out.println("  * [<] Back to Songs in Progress");
	    			}
	    			else if(triggerValue == 3)
	    			{
	    				System.out.println("  * [<] Back to Planned Songs");
	    			}
	    			else if(triggerValue == 4)
	    			{
	    				System.out.println("  * [<] Back to All Songs");
	    			}
	    			
	    			System.out.println("  * ");
	    			System.out.print("  * Enter your choice: ");
	    			choice8 = scanner.nextLine();
	    			
	    			if(choice8.equals("1"))
	    			{
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    				
	    				do
		            	{
			            	System.out.println("  - {CHANGE SONG STATUS}");
			            	
			            	if(song.getStatus() == Status.COMPLETED)
			            	{
			            		System.out.println("  - [1] In Progress");
			            		System.out.println("  - [2] Planned");
			            	}
			            	else if(song.getStatus() == Status.IN_PROGRESS)
			            	{
			            		System.out.println("  - [1] Completed");
			            		System.out.println("  - [2] Planned");
			            	}
			            	else if(song.getStatus() == Status.PLANNED)
			            	{
			            		System.out.println("  - [1] Completed");
			            		System.out.println("  - [2] In Progress");
			            	}
			            	
			            	System.out.println("  - [<] Back to Song " + songChoice);
			            	System.out.println("  - ");
			            	System.out.print("  - Input Status: ");
			            	choice5 = scanner.nextLine();
			            	
			            	if(choice5.equals("1"))
			            	{
			            		if(song.getStatus() == Status.COMPLETED)
			            		{
				            		try {
				            			song.setStatus(Status.IN_PROGRESS);
				            			song.setUserRating(0.0);
				            			song.setReview("");

				            			songDAO.updateStatus(user_id, song, Status.IN_PROGRESS);
				            			songPlaylistDAO.updateAllPlaylists(song);
				            		    
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		    System.out.println(e.getMessage());
				            		}
			            		}
			            		else if(song.getStatus() == Status.IN_PROGRESS)
			            		{
			            			do
			            			{
				            			choice4 = "";
				            			review = "";
				            			
				            			try {
					            		    songDAO.updateStatus(user_id, song, Status.COMPLETED);
					            		    
					            		    System.out.print("  - Input Personal Rating: ");
						            		
						            		try {
						            	        rating = Double.parseDouble(scanner.nextLine());
	
						            	        if (rating < 1 || rating > 10) {
						            	        	System.out.println("  - Rating must be between 1 and 10.\n  -");
						            	        	choice4 = "WRONG";
						            	        }
						            	        else
						            	        {
						            	        	if(choice4 != "WRONG")
						            	        	{
							            	        	do
							            	        	{
							            	        		review = "";
							            	        		
								            	        	System.out.println("  - ");
								            	        	System.out.println("  - {REVIEW SONG?}");
								            	        	System.out.println("  - [1] Yes");
								            	        	System.out.println("  - [2] No");
								            	        	System.out.print("  - Enter your choice: ");
								            	        	choice6 = scanner.nextLine();
								            	        	
								            	        	if(choice6.equals("1"))
								            	        	{
								            	        		System.out.println("  - ");
								            	        		System.out.print("  - Enter Review: ");
								            	        		review = scanner.nextLine();
								            	        		
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - " + song.getTitle() + " by " + song.getArtist() + " added successfully!");
								            	        	}
								            	        	else if(choice6.equals("2"))
								            	        	{
								            	        		review = "";
								            	        		
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - " + song.getTitle() + " by " + song.getArtist() + " added successfully!");
								            	        	}
								            	        	else
								            	        	{
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - Invalid input. Please try again.\n  -");
								            	        		choice4 = "WRONG";
								            	        	}
								            	        	
							            	        	} while(!choice6.equals("1") && !choice6.equals("2"));
						            	        	}
						            	        }
						            	    }
						            		catch (NumberFormatException e) {
						            	        System.out.println("  - Please enter a valid number.\n  -");
						            	    }
						            		
						            		song.setStatus(Status.COMPLETED);
						            		song.setUserRating(rating);
						            		song.setReview(review);

						            		songDAO.updateStatus(user_id, song, Status.COMPLETED);
						            		songPlaylistDAO.updateAllPlaylists(song);
		
					            		    System.out.println("  - Status updated!");
					            		    choice5 = "<";
		
					            		}
					            		catch (SQLException e) {
					            		    System.out.println("  - Could not update status.\n");
					            		}
			            			} while(choice4.equals("WRONG"));
			            		}
			            		else if(song.getStatus() == Status.PLANNED)
			            		{
			            			do
			            			{
				            			choice4 = "";
				            			review = "";
				            			
				            			try {
					            		    songDAO.updateStatus(user_id, song, Status.COMPLETED);
					            		    
					            		    System.out.print("  - Input Personal Rating: ");
						            		
						            		try {
						            	        rating = Double.parseDouble(scanner.nextLine());
	
						            	        if (rating < 1 || rating > 10) {
						            	        	System.out.println("  - Rating must be between 1 and 10.\n  -");
						            	        	choice4 = "WRONG";
						            	        }
						            	        else
						            	        {
						            	        	if(choice4 != "WRONG")
						            	        	{
							            	        	do
							            	        	{
							            	        		review = "";
							            	        		
								            	        	System.out.println("  - ");
								            	        	System.out.println("  - {REVIEW SONG?}");
								            	        	System.out.println("  - [1] Yes");
								            	        	System.out.println("  - [2] No");
								            	        	System.out.print("  - Enter your choice: ");
								            	        	choice6 = scanner.nextLine();
								            	        	
								            	        	if(choice6.equals("1"))
								            	        	{
								            	        		System.out.println("  - ");
								            	        		System.out.print("  - Enter Review: ");
								            	        		review = scanner.nextLine();
								            	        		
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - " + song.getTitle() + " by " + song.getArtist() + " added successfully!");
								            	        	}
								            	        	else if(choice6.equals("2"))
								            	        	{
								            	        		review = "";
								            	        		
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - " + song.getTitle() + " by " + song.getArtist() + " added successfully!");
								            	        	}
								            	        	else
								            	        	{
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - Invalid input. Please try again.\n  -");
								            	        		choice4 = "WRONG";
								            	        	}
								            	        	
							            	        	} while(!choice6.equals("1") && !choice6.equals("2"));
						            	        	}
						            	        }
						            	    }
						            		catch (NumberFormatException e) {
						            	        System.out.println("  - Please enter a valid number.\n  -");
						            	        choice4 = "WRONG";
						            	    }
						            		
					            		    song.setUserRating(rating);
					            		    song.setReview(review);
					            		    
					            		    songDAO.updateStatus(user_id, song, Status.COMPLETED);
					            		    
					            		    if(!choice4.equals("WRONG"))
					            		    	System.out.println("  - Status updated!");
					            		    
					            		    choice5 = "<";
		
					            		}
					            		catch (SQLException e) {
					            		    System.out.println("  - Could not update status.\n");
					            		    System.out.println(e.getMessage());
					            		}
			            			} while(choice4.equals("WRONG"));
			            		}
			            	}
			            	else if(choice5.equals("2"))
			            	{
			            		if(song.getStatus() == Status.COMPLETED)
			            		{
				            		try {
				            			song.setStatus(Status.PLANNED);
				            			song.setUserRating(0.0);
				            			song.setReview("");

				            			songDAO.updateStatus(user_id, song, Status.PLANNED);
				            			songPlaylistDAO.updateAllPlaylists(song);
	
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		}
			            		}
			            		else if(song.getStatus() == Status.IN_PROGRESS)
			            		{
			            			try {
				            		    songDAO.updateStatus(user_id, song, Status.PLANNED);
	
				            		    song.setStatus(Status.PLANNED);
				            		    song.setUserRating(0.0);
				            		    song.setReview("");
	
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		}
			            		}
			            		else if(song.getStatus() == Status.PLANNED)
			            		{
			            			try {
			            				song.setStatus(Status.IN_PROGRESS);
			            				song.setUserRating(0.0);
			            				song.setReview("");

			            				songDAO.updateStatus(user_id, song, Status.IN_PROGRESS);
			            				songPlaylistDAO.updateAllPlaylists(song);
	
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		    System.out.println(e.getMessage());
				            		}
			            		}
			            	}
			            	else
			            	{
			            		System.out.println("  - Invalid input! Please try again.\n  -");
			            	}
		            	} while(!choice5.equals("<"));
	    				
	    				choice8 = "<";
	    			}
	    			else if(choice8.equals("2"))
	    			{
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    				
	    				if(song.getStatus() == Status.COMPLETED)
	    				{
		    				do
		    				{
		    					choice5 = "";
		    					
			    				System.out.println("  - {CHANGE SONG RATING}");
				            	System.out.print("  - Input New Rating: ");
				            	try {
				            		
				            		try {
				            			rating = Double.parseDouble(scanner.nextLine());
				            		}
				            		catch (NumberFormatException e) {
				            			System.out.println("  - Please enter a valid number.\n  -");
				            			choice5 = "WRONG";
				            		}
				            		
			            	        if(!choice5.equals("WRONG"))
			            	        {
				            	        if (rating < 1 || rating > 10)
				            	        {
				            	        	System.out.println("  - Rating must be between 1 and 10.\n  -");
				            	        	choice5 = "WRONG";
				            	        }
				            	        else
				            	        {
				            	        	song.setUserRating(rating);

				            	        	songDAO.updateSongRating(user_id, song, rating);
				            	        	songPlaylistDAO.updateAllPlaylists(song);
					            		    
					            		    System.out.println("  - Rating updated!");
					            		    choice5 = "<";
				            	        }
			            	        }
				            	}
				            	catch (SQLException e) {
				            		System.out.println("  - Could not update rating.\n  -");
			            		    choice5 = "WRONG";
				            	}
				            	
		    				} while(choice5.equals("WRONG"));
		    				
		    				choice8 = "<";
	    				}
	    				else
	    				{
	    					System.out.println("  * Invalid input! Please try again.");
		    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	    				}
	    			}
	    			else if(choice8.equals("3"))
	    			{
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    				
	    				if(song.getStatus() == Status.COMPLETED)
	    				{
		    				try {
			    				if(song.getReview().equals(""))
			    					System.out.println("  - {ADD SONG REVIEW}");
				    			else
				    				System.out.println("  - {CHANGE SONG REVIEW}");
			    				
				            	System.out.print("  - Input Review: ");
				            	review = scanner.nextLine();
				            	
				            	song.setReview(review);

				            	songDAO.addReview(user_id, song, review);
				            	songPlaylistDAO.updateAllPlaylists(song);
		            		    
		            		    System.out.println("  - Review updated!");
		    				}
	            		    catch (SQLException e) {
	            		    	if(song.getReview().equals(""))
	            		    		System.out.println("  - Could not add review.\n  -");
				    			else
				    				System.out.println("  - Could not change review.\n  -");
		            		    choice5 = "WRONG";
			            	}
		    				
		    				choice8 = "<";
	    				}
	    				else
	    				{
	    					System.out.println("  * Invalid input! Please try again.");
		    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	    				}
	    			}
	    			else if(choice8.equals("-"))
	    			{
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    				
	    				try {
	    					
	    					if (triggerValue != 5) {
	    						
	    						songState = songDAO.deleteSong(user_id, song.getTitle(), song.getArtist());
	    						
	    						if(songState == 0)
	    						{
	    							System.out.println(" - Song not found.");
	    						}
	    						else if(songState == 1)
	    						{
	    							System.out.println(" - " + song.getTitle() + " by " + song.getArtist() + " was removed from your songs.");
	    						}
	    						else if(songState == 1)
	    						{
	    							System.out.println(" - Song was not found in your songs.");
	    						}
	    					}
	    					else {
	    					    if (allChecker){
	    					    	songState = songDAO.deleteSong(user_id, song.getTitle(), song.getArtist());
		    						
		    						if(songState == 0)
		    						{
		    							System.out.println(" - Song not found.");
		    						}
		    						else if(songState == 1)
		    						{
		    							System.out.println(" - " + song.getTitle() + " by " + song.getArtist() + " was removed from your songs.");
		    						}
		    						else if(songState == 1)
		    						{
		    							System.out.println(" - Song was not found in your songs.");
		    						}
	    					    }
	    					    else {
	    					        int songId = songDAO.getSongId(song.getTitle(), song.getArtist());

	    					        songPlaylistDAO.removeSongFromPlaylist(sp.getPlaylistId(), songId);
	    					    }
	    					}
	    					
	    					song.setReview("");
	    					song.setUserRating(0.0);
	    				}
	    				catch (SQLException e) {
			    			System.out.println("  - Could not remove song.");
		            	}
	    				
	    				choice8 = "<";
	    			}
	    			else if(choice8.equals("<"))
	    			{
	    				System.out.print("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    			}
	    			else
	    			{
	    				System.out.println("  * Invalid input! Please try again.");
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    			}
	    			
    			} while(!choice8.equals("<"));
    		}
    		else if(choice7.equals("+") && triggerValue == 5 && !allChecker)
    		{
    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
    			doSongSearch(spotifyClient, scanner, songDAO, songPlaylistDAO, user_id, sp.getPlaylistId(), triggerValue);
    		}
    		else if(choice7.equals("-") && triggerValue == 5 && !allChecker)
    		{
    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
    			try {
    			    songPlaylistDAO.deletePlaylist(sp.getPlaylistId());
    			    System.out.println("  - Playlist \"" + sp.getTitle() + "\" was deleted successfully!");
    			}
    			catch (SQLException e) {
    			    System.out.println("  - Playlist was not deleted.");
    			    System.out.println(e.getMessage());
    			}
    			
    			choice7 = "<";
    		}
    		else
    		{
    			if(triggerValue != 5)
    			{
	    			System.out.println("= ");
	    			System.out.println("= Invalid input! Please try again.");
	    			System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - =");
    			}
    			else
    			{
    				System.out.println("  * ");
    				System.out.println("  * Invalid input! Please try again.");
    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * *");
    			}
    		}
    		
    	} while(!choice7.equals("<"));
	}
	
	public static void doSongSearch(SpotifyClient spotifyClient, Scanner scanner, SongDAOImpl songDAO, SongPlaylistDAOImpl songPlaylistDAO, int user_id, int playlistId, int triggerValue) {
		
		Status status = Status.PLANNED;
		int songId, resultSize, songChoice = 0, yearReleased, runtimeSeconds, ctr;
		String title, album, artist, choice2, choice3, choice4, choice5, choice6, search, review = "";
		double rating = 0.0;
		
		try {
	    	do
	    	{
	    		System.out.print(" Search Song: ");
		        search = scanner.nextLine();
		        
		        List<Song> results = spotifyClient.searchTracks(search);
	            System.out.println(" Songs found: " + results.size());
		        
	            System.out.println();
	            System.out.println("-----------------------------------------------------------------");
	            System.out.printf("| %-3s | %-23s | %-6s | %-20s |%n", "No.", "Title", "Length", "Artist");
	            System.out.println("-----------------------------------------------------------------");
	            
	            resultSize = Math.min(10, results.size());
	            
	            for(ctr = 0; ctr < resultSize; ctr++)
	            {
	            	Song song = results.get(ctr);
	            	
	                System.out.printf("| %-3d | %-23s | %-6s | %-20s |%n", ctr+1, fitToSpace(song.getTitle(), 23), song.getRuntimeString(), fitToSpace(song.getArtist(), 20));
	            }
	            
	            System.out.println("-----------------------------------------------------------------");
	            System.out.println();
	            System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
	            System.out.println("= [#] Choose Song (Input the Track No.)");
	            System.out.println("= [?] Change Search");
	            System.out.println("= [+] Add Song Manually");
	            System.out.println("= [<] Back to My Songs");
	            System.out.print("=\n= Enter your choice: ");
	    		
	            choice3 = scanner.nextLine();
	            
	            try {
	            	songChoice = Integer.parseInt(choice3);
	            }
	            catch (NumberFormatException e) {
	            }
	            
	            if(choice3.equals("<"))
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
	            }
	            else if(choice3.equals("?"))
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            }
	            else if(choice3.equals("+"))
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            	
	            	do
	            	{
		            	System.out.print(" Enter Song Title: ");
		            	title = scanner.nextLine();
		            	System.out.print(" Enter Album Title: ");
		            	album = scanner.nextLine();
		            	System.out.print(" Enter Artist's Name: ");
		            	artist = scanner.nextLine();
		            	System.out.print(" Enter Year Released: ");
		            	yearReleased = Integer.parseInt(scanner.nextLine());
		            	System.out.print(" Enter Runtime in Seconds: ");
		            	runtimeSeconds = Integer.parseInt(scanner.nextLine());
		            	
		            	System.out.println();
		            	
		            	do
		            	{
			            	System.out.println(" - {SONG STATUS}");
			            	System.out.println(" - [1] Completed");
			            	System.out.println(" - [2] In Progress");
			            	System.out.println(" - [3] Planned");
			            	System.out.println(" - [X] Redo Manual Add");
			            	System.out.print(" Input Status: ");
			            	choice4 = scanner.nextLine();
			            	
			            	if(choice4.equals("1"))
			            	{
			            		System.out.print(" Input Personal Rating: ");
			            		
			            		try {
			            	        rating = Double.parseDouble(scanner.nextLine());
	
			            	        if (rating < 1 || rating > 10) {
			            	        	System.out.println(" Rating must be between 1 and 10.\n");
			            	        	choice4 = "WRONG";
			            	        }
			            	        else
			            	        {
			            	        	status = Status.COMPLETED;
			            	        	
			            	        	do
			            	        	{
			            	        		review = "";
			            	        		
				            	        	System.out.println(" - ");
				            	        	System.out.println(" - {REVIEW SONG?}");
				            	        	System.out.println(" - [1] Yes");
				            	        	System.out.println(" - [2] No");
				            	        	System.out.print(" - Enter your choice: ");
				            	        	choice6 = scanner.nextLine();
				            	        	
				            	        	if(choice6.equals("1"))
				            	        	{
				            	        		System.out.println(" - ");
				            	        		System.out.print(" - Enter Review: ");
				            	        		review = scanner.nextLine();
				            	        		
				            	        		System.out.println(" - ");
				            	        		System.out.println(" - " + title + " by " + artist + " added successfully!");
				            	        	}
				            	        	else if(choice6.equals("2"))
				            	        	{
				            	        		review = "";
				            	        		
				            	        		System.out.println(" - ");
				            	        		System.out.println(" - " + title + " by " + artist + " added successfully!");
				            	        	}
				            	        	else
				            	        	{
				            	        		System.out.println(" - ");
				            	        		System.out.println(" - Invalid input. Please try again.");
				            	        	}
				            	        	
			            	        	} while(!choice6.equals("1") && !choice6.equals("2"));
			            	        }
			            	    }
			            		catch (NumberFormatException e) {
			            	        System.out.println(" Please enter a valid number.\n");
			            	        choice4 = "WRONG";
			            	    }
			            	}
			            	else if(choice4.equals("2"))
			            	{
			            		status = Status.IN_PROGRESS;
			            		
			            		System.out.println(" - ");
            	        		System.out.println(" - " + title + " by " + artist + " added successfully!");
			            	}
			            	else if(choice4.equals("3"))
			            	{
			            		status = Status.PLANNED;
			            		
			            		System.out.println(" - ");
            	        		System.out.println(" - " + title + " by " + artist + " added successfully!");
			            	}
			            	else if(choice4.equals("X"))
			            	{
			            		System.out.println();
			            	}
			            	else
			            	{
			            		System.out.println(" Invalid input! Please try again.\n");
			            	}
		            	} while(!choice4.equals("1") && !choice4.equals("2") && !choice4.equals("3"));
		            	
	            	} while(choice4.equals("X") && !choice4.equals("1") && !choice4.equals("2") && !choice4.equals("3"));
	            	
	            	Song song = new Song(title, status, rating, album, artist, yearReleased, runtimeSeconds, review);
	            	
	            	songId = songDAO.addSong(song, user_id);
	            	
	            	if (songId != -1) {
	            	    System.out.println(" - " + song.getTitle() + " by " + song.getArtist() + " added successfully!");
	            	    song.setSongId(songId);
	            	}
	            	else {
	            	    System.out.println(" - " + song.getTitle() + " by " + song.getArtist() + " is already in your songs!");
	            	}
	            	
	            	choice3 = "<";
	            }
	            else if(1 <= songChoice && songChoice <= resultSize)
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            	
	            	do
	            	{
		            	System.out.println(" - {SONG STATUS}");
		            	System.out.println(" - [1] Completed");
		            	System.out.println(" - [2] In Progress");
		            	System.out.println(" - [3] Planned");
		            	System.out.println(" - ");
		            	System.out.print(" - Input Status: ");
		            	choice5 = scanner.nextLine();
		            	
		            	if(choice5.equals("1"))
		            	{
		            		System.out.print(" - Input Personal Rating: ");
		            		
		            		try {
		            	        rating = Double.parseDouble(scanner.nextLine());
	
		            	        if (rating < 1 || rating > 10)
		            	        {
		            	        	System.out.println(" - Rating must be between 1 and 10.\n");
		            	        	choice5 = "WRONG";
		            	        }
		            	        else
		            	        {
		            	        	status = Status.COMPLETED;
		            	        	
		            	        	do
		            	        	{
		            	        		review = "";
		            	        		
			            	        	System.out.println(" - ");
			            	        	System.out.println(" - {REVIEW SONG?}");
			            	        	System.out.println(" - [1] Yes");
			            	        	System.out.println(" - [2] No");
			            	        	System.out.print(" - Enter your choice: ");
			            	        	choice6 = scanner.nextLine();
			            	        	
			            	        	if(choice6.equals("1"))
			            	        	{
			            	        		System.out.println(" - ");
			            	        		System.out.print(" - Enter Review: ");
			            	        		review = scanner.nextLine();
			            	        	}
			            	        	else if(choice6.equals("2"))
			            	        	{
			            	        		review = "";
			            	        	}
			            	        	else
			            	        	{
			            	        		System.out.println(" - ");
			            	        		System.out.println(" - Invalid input. Please try again.");
			            	        	}
			            	        	
		            	        	} while(!choice6.equals("1") && !choice6.equals("2"));
		            	        }
		            	    }
		            		catch (NumberFormatException e) {
		            	        System.out.println(" - Please enter a valid number.\n");
		            	        choice5 = "WRONG";
		            	    }
		            	}
		            	else if(choice5.equals("2"))
		            	{
		            		status = Status.IN_PROGRESS;
		            	}
		            	else if(choice5.equals("3"))
		            	{
		            		status = Status.PLANNED;
		            	}
		            	else
		            	{
		            		System.out.println(" - Invalid input! Please try again.\n");
		            	}
	            	} while(!choice5.equals("1") && !choice5.equals("2") && !choice5.equals("3"));
	            	
	            	Song songTemp = results.get(songChoice-1);
	            	Song newSong = new Song(songTemp.getTitle(), status, rating, songTemp.getAlbum(), songTemp.getArtist(), songTemp.getYearReleased(), songTemp.getRuntimeSeconds(), review);
	            	
	            	try {
	            		 songId = songDAO.addSong(newSong, user_id);
	            		 Song oldSong = songDAO.getSongOfUserById(songId);
	            		
	            		 if (triggerValue == 5)
	            		 {
            		        if ( oldSong != null && oldSong.getStatus() != null && (!oldSong.getReview().equals(review) || oldSong.getUserRating() != rating || !oldSong.getStatus().toDbString().equals(status.toDbString())) )
            		        {
            		            do
            		            {
            		                System.out.println(" - ");
            		                System.out.println(" - Would you like to overwrite your previous status/rating/review?");
            		                System.out.println(" - {STATUS}");
            		                System.out.println(" - Previous: " + oldSong.getStatus().toDbString());
            		                System.out.println(" - New: " + status.toDbString());
            		                System.out.println(" - {RATING}");
            		                
            		                if(oldSong.getUserRating() == 0.0)
            		                	System.out.println(" - Previous: /-complete to rate-/");
            		                else
            		                	System.out.println(" - Previous: " + oldSong.getUserRating());
            		                
            		                if(rating == 0.0)
            		                	System.out.println(" - New: /-complete to rate-/");
            		                else
            		                	System.out.println(" - New: " + rating);
            		                
            		                System.out.println(" - {REVIEW}");
            		                
            		                if(oldSong.getReview().equals(""))
            		                {
            		                	if(!oldSong.getStatus().toDbString().equals("completed"))
            		                		System.out.println(" - Previous: /-complete to review-/");
            		                	else
            		                		System.out.println(" - Previous: /-unreviewed-/");
            		                }
            		                else
            		                	System.out.println(" - Previous: \"" + oldSong.getReview() + "\"");
            		                
            		                if(review.equals(""))
            		                {
            		                	if(!status.toDbString().equals("completed"))
            		                		System.out.println(" - New: /-complete to review-/");
            		                	else
            		                		System.out.println(" - New: /-unreviewed-/");
            		                }
            		                else
            		                	System.out.println(" - New: \"" + review + "\"");
            		                
            		                System.out.println(" - ");
            		                System.out.println(" - [1] Yes");
            		                System.out.println(" - [2] No");
            		                System.out.print(" - Enter your choice: ");

            		                choice2 = scanner.nextLine();

            		                if (choice2.equals("1"))
            		                {
            		                    songPlaylistDAO.addSongToPlaylist(
            		                        playlistId,
            		                        songId,
            		                        status,
            		                        rating,
            		                        review
            		                    );
            		                    
            		                    songPlaylistDAO.updateAllPlaylists(newSong);
            		                }
            		                else if (choice2.equals("2"))
            		                {
            		                    songPlaylistDAO.addSongToPlaylist(
            		                        playlistId,
            		                        songId,
            		                        oldSong.getStatus(),
            		                        oldSong.getUserRating(),
            		                        oldSong.getReview()
            		                    );
            		                }
            		                else
            		                {
            		                    System.out.println(" - Invalid input! Please try again.");
            		                }

            		            } while (!choice2.equals("1") && !choice2.equals("2"));
            		        }
            		        else
            		        {
            		            songPlaylistDAO.addSongToPlaylist(
            		                playlistId,
            		                songId,
            		                status,
            		                rating,
            		                review
            		            );
            		        }

            		        System.out.println(" - ");
            		        System.out.println(" - " + songTemp.getTitle() + " by " + songTemp.getArtist() + " added to songs successfully!");
            		        System.out.println(" - " + songTemp.getTitle() + " by " + songTemp.getArtist() + " added to playlist successfully!");
            		    }
            		    else
            		    {
            		        System.out.println(" - ");
            		        System.out.println(" - " + songTemp.getTitle() + " by " + songTemp.getArtist() + " added successfully!");
            		        
            		        songPlaylistDAO.updateAllPlaylists(newSong);
            		    }
	            	}
	            	catch (SQLException e) {
	            	}
	            	
	            	choice3 = "<";
	            }
	            else
	            {
	            	System.out.println("=\n= Invalid input! Please try again.");
			    	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            }
	        
	    	} while(!choice3.equals("<"));
		}
		catch(Exception e) {
			System.out.println("An error has occured.");
            e.printStackTrace();
		}
    	
        System.out.println();
        
	}
}