package application;

import java.util.List;
import java.util.ArrayList;
import application.api.SpotifyClient;
import application.model.Media;
import application.model.MediaPlaylist;
import application.model.Song;
import application.model.Status;
import application.model.Type;
import application.dao.impl.MediaPlaylistDAOImpl;
import application.db.DatabaseConnection;
import application.db.DatabaseInitializer;
import application.dao.MediaDAO;
import application.dao.UserDAO;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Scanner;

public class Main {

	private static int loggedInUser = -1;
	private static Scanner scanner = new Scanner(System.in);
	private static Connection conn;
	private static UserDAO userDAO;
	private static MediaDAO mediaDAO;
	private static MediaPlaylistDAOImpl mediaPlaylistDAO;
	private static SpotifyClient spotifyClient = new SpotifyClient("266e17b3bb8e432d82b803598192fc5f", "f38ada98c91f4bf9bf6ed4f4490d7b12");
	
	public static void main(String[] args) throws SQLException {
		
		int checkCtr = 0;
		boolean securityCheck;
		String username, password, checkPassword, choice;
		
		try{
		    conn = DatabaseConnection.connect();

		    DatabaseInitializer initializer = new DatabaseInitializer();
		    initializer.initialize(conn);

		    userDAO = new UserDAO(conn);
		    
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
							
							loggedInUser = userDAO.getUserID(username);
							vaultMenu();
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
	
	public static void vaultMenu(){
		
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
		    	getMediaVault("Song");
		    }
		    else if(choice.equals("3"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		    	getMediaVault("Game");
		    }
		    else if(choice.equals("4"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		    	getMediaVault("Show");
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
	
	public static void getMediaVault(String mediaType){
		
		int playlistChoice = -1, triggerValue = 0, playlistId = -1;
		String choice, choice2, choice7, title;
		
		mediaDAO = new MediaDAO(conn, loggedInUser);
		mediaPlaylistDAO = new MediaPlaylistDAOImpl(conn, loggedInUser);
		
		List<Media> mediaList = new ArrayList<>();
		List<MediaPlaylist> playlists = new ArrayList<>();
		
		spotifyClient = new SpotifyClient("266e17b3bb8e432d82b803598192fc5f", "f38ada98c91f4bf9bf6ed4f4490d7b12");
		
		do
		{
			
	    	System.out.printf("\n= = = = = = = = = = = %s  VAULT = = = = = = = = = = =\n", mediaType.toUpperCase());
	    	System.out.printf("= [1] My %ss\n", mediaType);
	    	System.out.println("= [2] My Playlists");
	    	System.out.println("= [<] Back to Vault Menu");
	    	System.out.print("=\n= Enter your choice: ");
		    choice = scanner.nextLine();
		    
		    if(choice.equals("1"))
		    {
		    	do
		    	{
			    	System.out.printf("= - - - - - - - - - =  MY  %s  = - - - - - - - - - =\n", mediaType.toUpperCase());
			    	System.out.println("= [1] Completed");
			    	System.out.println("= [2] In Progress");
			    	System.out.println("= [3] Planned");
			    	System.out.printf("= [*] View All My %ss\n", mediaType);
			    	System.out.printf("= [+] Add %s\n", mediaType);
			    	System.out.printf("= [<] Back to %s Vault", mediaType);
			    	System.out.print("=\n= Enter your choice: ");
				    choice2 = scanner.nextLine();
				    
				    if(choice2.equals("1"))
				    {	
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 1;
				    	
				    	updateMedia(playlistChoice, playlists, triggerValue, mediaType);
				    }
				    else if(choice2.equals("2"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 2;
				    	
				    	updateMedia(playlistChoice, playlists, triggerValue, mediaType);
				    }
				    else if(choice2.equals("3"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 3;
				    	
				    	updateMedia(playlistChoice, playlists, triggerValue, mediaType);
				    }
				    else if(choice2.equals("*"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				    	
				    	triggerValue = 4;
				    	
				    	updateMedia(playlistChoice, playlists, triggerValue, mediaType);
				    }
				    else if(choice2.equals("+"))
				    {
				    	System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
				    	
				    	doSongSearch(playlistId, triggerValue);
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
			    			playlists = mediaPlaylistDAO.getPlaylistsByUser(loggedInUser, "Song");
			    			
			    			System.out.println();
				            System.out.println("-------------------------------------------------------------------------------------------------------------------");
				            System.out.printf("| %-3s | %-23s | %-11s | %-9s | %-11s | %-7s | %-29s |%n", "No.", "Title", "Total Songs", "Completed", "In Progress", "Planned", "Avg. Rating (Completed Songs)");
				            System.out.println("-------------------------------------------------------------------------------------------------------------------");
			    			
				            int ctr2 = 1;
				            
				            for(MediaPlaylist mp: playlists)
			    			{
			    				int completeTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.COMPLETED, mediaType),
			    					inProgressTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.IN_PROGRESS, mediaType),
			    					plannedTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.PLANNED, mediaType);
			    				
			    				String ratingTemp = String.valueOf(mediaPlaylistDAO.calculateAvgRating(mp.getPlaylistId(), mediaType));
			    				
			    				if(completeTemp == 0)
			    					ratingTemp = "/-no completed songs yet-/";
			    				
			    				if(mp.getTitle().equals("all_songs"))
			    					mp.setTitle("All Songs");
			    				
			    				System.out.printf("| %-3s | %-23s | %-11s | %-9s | %-11s | %-7s | %-29s |%n", ctr2++,
			    																	   fitToSpace(mp.getTitle(), 23),
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
					    		if(mediaPlaylistDAO.createPlaylist(title, Type.SONG))
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
									// List<Songs> 
								    MediaPlaylist mp = playlists.get(playlistChoice - 1);
								    mediaList = mediaPlaylistDAO.getMediasInPlaylist(mp.getPlaylistId(), mediaType);
						
						            updateMedia(playlistChoice, playlists, 5, mediaType);
									//updateMedia(loggedInUser, songDAO, sp, songPlaylistDAO, playlists, playlistChoice, scanner, 5, spotifyClient);
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
	
	public static void printMedia(List<Media> mediaList) {

	    System.out.println();
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-25s | %-20s | %-6s | %-10s | %-10s | %-30s |%n", "Title", "Creator", "Year", "Status", "Rating", "Review");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");

	    int ctr = 1;

	    for (Media media: mediaList)
	    	System.out.printf("| %-25s | %-20s | %-6s | %-10s | %-10s | %-30s |%n", ctr++, fitToSpace(media.getTitle(), 25), fitToSpace(media.getCreator(), 20), media.getStatus().toDbString(), media.getUserRatingString(), media.getReviewedStatus());

	    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------\n");
	}
	
	// edit a song inside a playlist
	public static void updateMedia(int playlistChoice, List<MediaPlaylist> playlists, int triggerValue, String mediaType) {

		boolean allChecker = false;
		double rating = 0.0;
		String choice7, choice8, choice5, choice4, choice6, review; 
		int songChoice = 0, songState;
		
		List<Media> mediaList = new ArrayList<>();
		
		// SELECT * FROM songs_playlists

		do
    	{
			MediaPlaylist mp = null;

		    if (triggerValue == 5)
		    	mp = playlists.get(playlistChoice - 1);

		    mediaList.clear();

		    allChecker = false;

		    if (triggerValue == 5 && (mp.getTitle().equalsIgnoreCase("All Songs") || mp.getTitle().equalsIgnoreCase("all_songs")))
		        allChecker = true;
    		
    		try {
    			
    			if(triggerValue == 1)
    			{
					// for (Media media : MediaDAO.getMediaByUser(loggedInUser))
					// getAllSongs(); pero completed
		            for (Media media : mediaDAO.getMediasByUser())
		                if (media.getStatus() == Status.COMPLETED)
		                	mediaList.add(media);
		            
		            printMedia(mediaList);
    			}
    			else if(triggerValue == 2)
    			{
					// getAllSongs(); pero in progress
    				for (Media media : mediaDAO.getMediasByUser())
		                if (media.getStatus() == Status.IN_PROGRESS)
		                	mediaList.add(media);
    				
    				printMedia(mediaList);
    			}
    			else if(triggerValue == 3)
    			{
					// getAllSongsByStatus(); pero planned
    				for (Media media : mediaDAO.getMediasByUser())
		                if (media.getStatus() == Status.PLANNED)
		                	mediaList.add(media);
    				
    				printMedia(mediaList);
    			}
    			else if(triggerValue == 4)
    			{
					// my songs
					// getAllSongsByUser(); pero completed
					// all goods
					// gawing medias
    				for (Media media : mediaDAO.getSongsByUser(loggedInUser))
    					mediaList.add(media);
    				
    				printMedia(mediaList);
    			}
    			else if(triggerValue == 5)
    			{
    				// my playlists
					
    				int completeTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.COMPLETED, mediaType),
    					inProgressTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.IN_PROGRESS, mediaType),
    					plannedTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.PLANNED, mediaType),
    					totalTemp = completeTemp + inProgressTemp + plannedTemp;
    				
					// get average
    				String ratingTemp = String.valueOf(mediaPlaylistDAO.calculateAvgRating(mp.getPlaylistId(), mediaType));
    				
    				if(completeTemp == 0)
    					ratingTemp = "N/A";
    				
    				// hmm
    				List<Media> medias = mediaPlaylistDAO.getMediasInPlaylist(mp.getPlaylistId(), mediaType);
    				
    				printMedia(medias);
    		    	
    		    	System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    		    	System.out.println("  * PLAYLIST TITLE: " + mp.getTitle());
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
	    			e.printStackTrace();
	            System.out.println("Could not load songs.");
	        }
    		
    		if(triggerValue == 1)
    		{
    			System.out.printf("= - - - - - - - - = COMPLETED %s = - - - - - - - - =\n", mediaType.toUpperCase());
    		}
    		else if(triggerValue == 2)
    		{
    			System.out.printf("= - - - - - - - = %sS  IN  PROGRESS = - - - - - - - =\n", mediaType.toUpperCase());
    		}
    		else if(triggerValue == 3)
    		{
    			System.out.printf("= - - - - - - - - =  PLANNED %sS  = - - - - - - - - =\n", mediaType.toUpperCase());
    		}
    		else if(triggerValue == 4)
    		{
    			System.out.printf("= - - - - - - - - - =  ALL %sS  = - - - - - - - - - =\n", mediaType.toUpperCase());
    		}
    		
    		if(triggerValue != 5)
    		{
	    		System.out.printf("= [#] View/Update %s Status (Input the Track No.)\n", mediaType.toUpperCase());
	    		System.out.printf("= [<] Back to My %ss\n", mediaType);
	    		System.out.println("= ");
	    		System.out.print("= Enter your choice: ");
    		}
    		else
    		{
    			System.out.printf("  * [#] View/Update %s Status (Input the Track No.)\n", mediaType);
    			if(!allChecker)
    			{
	    			System.out.printf("  * [+] Add %s to Playlist", mediaType);
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
    		else if(1 <= songChoice && songChoice <= mediaList.size())
    		{
    			if(triggerValue != 5)
    				System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
    			else
    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
				// all goods
    			Media media = mediaList.get(songChoice-1);
    			do
    			{
					// DO BY MEDIA TYPE
	    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	    			System.out.printf("  * %s %d : %s by %s\n", mediaType.toUpperCase(), songChoice, media.getTitle(), media.getCreator());
	    			System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
	    			System.out.println("  * Year Released: " + String.valueOf(media.getYear()));
	    			System.out.println("  * Status: " + media.getStatus().toDbString());
	    			
	    			if(media.getStatus() == Status.COMPLETED)
	    			{
	    				System.out.println("  * My Rating: " + media.getUserRatingString());
	    				if(media.getReview().equals(""))
		    				System.out.println("  * My Review: Unreviewed");
		    			else
		    				System.out.println("  * My Review: " + media.getReview());
	    			}
	    			else if(media.getStatus() == Status.PLANNED || media.getStatus() == Status.IN_PROGRESS)
	    			{
	    				System.out.println("  * My Rating: /-complete to rate song-/");
	    				System.out.println("  * My Review: /-complete to review song-/");
	    			}
	    			
	    			System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
	    			System.out.println("  * [1] Change Status");
	    			
	    			if(media.getStatus() == Status.COMPLETED)
	    			{
		    			System.out.println("  * [2] Change Rating");
		    			
		    			if(media.getReview().equals(""))
		    				System.out.println("  * [3] Add Review");
		    			else
		    				System.out.println("  * [3] Change Review");
	    			}
	    			
	    			System.out.println("  * [-] Remove Song");
	    			
	    			if(triggerValue == 1)
	    			{
	    				System.out.printf("  * [<] Back to Completed %ss\n", mediaType);
	    			}
	    			else if(triggerValue == 2)
	    			{
	    				System.out.printf("  * [<] Back to %ss in Progress\n", mediaType);
	    			}
	    			else if(triggerValue == 3)
	    			{
	    				System.out.printf("  * [<] Back to Planned %ss", mediaType);
	    			}
	    			else if(triggerValue == 4)
	    			{
	    				System.out.printf("  * [<] Back to All %ss", mediaType);
	    			}
	    			
	    			System.out.println("  * ");
	    			System.out.print("  * Enter your choice: ");
	    			choice8 = scanner.nextLine();
	    			
	    			if(choice8.equals("1"))
	    			{
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    				
	    				do
		            	{
			            	System.out.printf("  - {CHANGE %s STATUS}\n", mediaType.toUpperCase());
			            	
			            	if(media.getStatus() == Status.COMPLETED)
			            	{
			            		System.out.println("  - [1] In Progress");
			            		System.out.println("  - [2] Planned");
			            	}
			            	else if(media.getStatus() == Status.IN_PROGRESS)
			            	{
			            		System.out.println("  - [1] Completed");
			            		System.out.println("  - [2] Planned");
			            	}
			            	else if(media.getStatus() == Status.PLANNED)
			            	{
			            		System.out.println("  - [1] Completed");
			            		System.out.println("  - [2] In Progress");
			            	}
			            	
			            	System.out.printf("  - [<] Back to %s %d\n", mediaType.toUpperCase(), songChoice);
			            	System.out.println("  - ");
			            	System.out.print("  - Input Status: ");
			            	choice5 = scanner.nextLine();
			            	
			            	if(choice5.equals("1"))
			            	{
			            		if(media.getStatus() == Status.COMPLETED)
			            		{
				            		try {
				            			media.setStatus(Status.IN_PROGRESS);
				            			media.setUserRating(0.0);
				            			media.setReview("");

				            			mediaDAO.updateMediaStatus(media, Status.IN_PROGRESS);
				            		    
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		    System.out.println(e.getMessage());
				            		}
			            		}
			            		else if(media.getStatus() == Status.IN_PROGRESS)
			            		{
			            			do
			            			{
				            			choice4 = "";
				            			review = "";
				            			
				            			try {
				            				mediaDAO.updateMediaStatus(media, Status.COMPLETED);
					            		    
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
								            	        		System.out.println("  - " + media.getTitle() + " by " + media.getCreator() + " added successfully!");
								            	        	}
								            	        	else if(choice6.equals("2"))
								            	        	{
								            	        		review = "";
								            	        		
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - " + media.getTitle() + " by " + media.getCreator() + " added successfully!");
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
						            		
						            		media.setStatus(Status.COMPLETED);
						            		media.setUserRating(rating);
						            		media.setReview(review);

						            		mediaDAO.updateMediaStatus(media, Status.COMPLETED);
		
					            		    System.out.println("  - Status updated!");
					            		    choice5 = "<";
		
					            		}
					            		catch (SQLException e) {
					            		    System.out.println("  - Could not update status.\n");
					            		}
			            			} while(choice4.equals("WRONG"));
			            		}
			            		else if(media.getStatus() == Status.PLANNED)
			            		{
			            			do
			            			{
				            			choice4 = "";
				            			review = "";
				            			
				            			try {
				            				mediaDAO.updateMediaStatus(media, Status.COMPLETED);
					            		    
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
								            	        		System.out.println("  - " + media.getTitle() + " by " + media.getCreator() + " added successfully!");
								            	        	}
								            	        	else if(choice6.equals("2"))
								            	        	{
								            	        		review = "";
								            	        		
								            	        		System.out.println("  - ");
								            	        		System.out.println("  - " + media.getTitle() + " by " + media.getCreator() + " added successfully!");
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
						            		
					            		    media.setUserRating(rating);
					            		    media.setReview(review);
					            		    
					            		    mediaDAO.updateMediaStatus(media, Status.COMPLETED);
					            		    
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
			            		if(media.getStatus() == Status.COMPLETED)
			            		{
				            		try {
				            			media.setStatus(Status.PLANNED);
				            			media.setUserRating(0.0);
				            			media.setReview("");

				            			mediaDAO.updateMediaStatus(media, Status.PLANNED);
	
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		}
			            		}
			            		else if(media.getStatus() == Status.IN_PROGRESS)
			            		{
			            			try {
			            				mediaDAO.updateMediaStatus(media, Status.PLANNED);
	
			            				media.setStatus(Status.PLANNED);
			            				media.setUserRating(0.0);
			            				media.setReview("");
	
				            		    System.out.println("  - Status updated!");
				            		    choice5 = "<";
	
				            		}
				            		catch (SQLException e) {
				            		    System.out.println("  - Could not update status.\n");
				            		}
			            		}
			            		else if(media.getStatus() == Status.PLANNED)
			            		{
			            			try {
			            				media.setStatus(Status.IN_PROGRESS);
			            				media.setUserRating(0.0);
			            				media.setReview("");

			            				mediaDAO.updateMediaStatus(media, Status.IN_PROGRESS);
	
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
	    				
	    				if(media.getStatus() == Status.COMPLETED)
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
				            	        	media.setUserRating(rating);

				            	        	mediaDAO.updateMediaRating(media, rating);
					            		    
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
	    				
	    				if(media.getStatus() == Status.COMPLETED)
	    				{
		    				try {
			    				if(media.getReview().equals(""))
			    					System.out.println("  - {ADD SONG REVIEW}");
				    			else
				    				System.out.println("  - {CHANGE SONG REVIEW}");
			    				
				            	System.out.print("  - Input Review: ");
				            	review = scanner.nextLine();
				            	
				            	media.setReview(review);

				            	mediaDAO.updateMediaReview(media, review);
		            		    
		            		    System.out.println("  - Review updated!");
		    				}
	            		    catch (SQLException e) {
	            		    	if(media.getReview().equals(""))
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
					// DELETE SONG
	    			else if(choice8.equals("-"))
	    			{
	    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	    				
	    				try {
	    					
	    					if (triggerValue != 5) {
	    						
	    						songState = mediaDAO.deleteSong(loggedInUser, media.getTitle(), media.getCreator());
	    						
	    						if(songState == 0)
	    						{
	    							System.out.println(" - Song not found.");
	    						}
	    						else if(songState == 1)
	    						{
	    							System.out.println(" - " + media.getTitle() + " by " + media.getCreator() + " was removed from your songs.");
	    						}
	    						else if(songState == 2)
	    						{
	    							System.out.println(" - Song was not found in your songs.");
	    						}
	    					}
	    					else {
								// if in all_songs, remove song everywhere
	    					    if (allChecker){
	    					    	songState = mediaDAO.deleteSong(loggedInUser, media.getTitle(), media.getCreator());
		    						
		    						if(songState == 0)
		    						{
		    							System.out.println(" - Song not found.");
		    						}
		    						else if(songState == 1)
		    						{
		    							System.out.println(" - " + media.getTitle() + " by " + media.getCreator() + " was removed from your songs.");
		    						}
		    						else if(songState == 1)
		    						{
		    							System.out.println(" - Song was not found in your songs.");
		    						}
	    					    }
								// else, remove from playlist
	    					    else {
	    					        int mediaId = mediaDAO.findMediaId(media);

	    					        mediaPlaylistDAO.removeMediaFromPlaylist(mp.getPlaylistId(), mediaId, Type.SONG);
	    					    }
	    					}
	    					
							// why oh why
							// if it works it works
	    					media.setReview("");
	    					media.setUserRating(0.0);
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
			// add song if inside playlists kase bawal mag-add sa all_songs/all_media
    		else if(choice7.equals("+") && triggerValue == 5 && !allChecker)
    		{
    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
				// paghiwalayin yung song search sa add manually
				// doAddSong(...)
					// sa loob ng doAddSong yung SongSearch

				// basically doSongSearchAndAdd
				// ilagay yung API sa loob ng if
    			doSongSearch(mp.getPlaylistId(), triggerValue);
    		}
			// delete playlist
    		else if(choice7.equals("-") && triggerValue == 5 && !allChecker)
    		{
    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
    			try {
    			    mediaPlaylistDAO.deletePlaylist(mp.getPlaylistId(), mediaType);
    			    System.out.println("  - Playlist \"" + mp.getTitle() + "\" was deleted successfully!");
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
	
	public static void doSongSearch(int playlistId, int triggerValue) {
		
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
	            	
	                System.out.printf("| %-3d | %-23s | %-6s | %-20s |%n", ctr+1, fitToSpace(song.getTitle(), 23), song.getRuntimeString(), fitToSpace(song.getCreator(), 20));
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
				// manual adding
				// TODO: Overwrite
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
	            	
	            	Song song = new Song(title, artist, yearReleased, status, rating, review, album, runtimeSeconds);
	            	
	            	songId = mediaDAO.addMedia(song);
	            	
	            	if (songId != -1) {
	            	    System.out.println(" - " + song.getTitle() + " by " + song.getCreator() + " added successfully!");
	            	    song.setSongId(songId);
	            	}
	            	else {
	            	    System.out.println(" - " + song.getTitle() + " by " + song.getCreator() + " is already in your songs!");
	            	}
	            	
	            	choice3 = "<";
	            }
				// if nakapili ng song through search
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
	            	
					// OVERWRITE LOGIC
					// if u add same song but different review
					// can choose if u overwrite
	            	Song songTemp = results.get(songChoice-1);
	            	System.out.println("@@@ " + status.toDbString());
	            	System.out.println("@@@ " + rating);
	            	System.out.println("@@@ " + review);
	            	Song newSong = new Song(songTemp.getTitle(), songTemp.getCreator(), songTemp.getYearReleased(), status, rating, review, songTemp.getAlbum(), songTemp.getRuntimeSeconds());
	            	
	            	try {
						// add now, edit more features later e.g. review, status
	            		 songId = mediaDAO.addMedia(newSong);
						 // check if song already exists???
	            		 Song oldSong = mediaDAO.getSongOfUserById(songId);
	            		
						 // if inside a playlist, add song to that playlist and all_songs
						 // otherwise, add to all_songs only
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

									// if user wants to overwrite???
            		                if (choice2.equals("1"))
            		                {
            		                    mediaPlaylistDAO.addMediaToPlaylist(playlistId, mediaDAO.findMediaId(newSong), "Song");
            		                    mediaPlaylistDAO.updateAllPlaylists(newSong);
            		                }
									// dont overwrite
            		                else if (choice2.equals("2"))
            		                {
            		                    mediaPlaylistDAO.addMediaToPlaylist(playlistId, songId, "Song");
            		                }
            		                else
            		                {
            		                    System.out.println(" - Invalid input! Please try again.");
            		                }

            		            } while (!choice2.equals("1") && !choice2.equals("2"));
            		        }
							// without overwrite logic, add normally
            		        else
            		        {
            		        	mediaPlaylistDAO.addMediaToPlaylist(playlistId, songId, "Song");
            		        	mediaPlaylistDAO.updateAllPlaylists(newSong);
            		        }

            		        System.out.println(" - ");
            		        System.out.println(" - " + songTemp.getTitle() + " by " + songTemp.getCreator() + " added to songs successfully!");
            		        System.out.println(" - " + songTemp.getTitle() + " by " + songTemp.getCreator() + " added to playlist successfully!");
            		    }
						// add song to all_songs
            		    else
            		    {
            		        System.out.println(" - ");
            		        System.out.println(" - " + songTemp.getTitle() + " by " + songTemp.getCreator() + " added successfully!");
            		        
            		        mediaPlaylistDAO.updateAllPlaylists(newSong);
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