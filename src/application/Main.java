package application;

import java.util.List;
import java.util.ArrayList;
import application.api.SpotifyClient;
import application.model.Media;
import application.model.MediaPlaylist;
import application.model.Song;
import application.model.Game;
import application.model.Show;
import application.model.Status;
import application.model.Type;
import application.dao.impl.MediaPlaylistDAOImpl;
import application.db.DatabaseConnection;
import application.db.DatabaseInitializer;
import application.dao.impl.MediaDAOImpl;
import application.dao.UserDAO;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Scanner;


/**
 * Main driver class for the Media Vault application.
 *
 * <p>This class controls the application's command-line interface, including
 * user login and registration, media vault navigation, playlist management,
 * media searching, and media updates.</p>
 */
public class Main {

	private static int loggedInUser = -1;
	private static Scanner scanner = new Scanner(System.in);
	private static Connection conn;
	private static UserDAO userDAO;
	private static MediaDAOImpl mediaDAO;
	private static MediaPlaylistDAOImpl mediaPlaylistDAO;
	private static SpotifyClient spotifyClient = new SpotifyClient("266e17b3bb8e432d82b803598192fc5f", "f38ada98c91f4bf9bf6ed4f4490d7b12");
	
	
	/**
     * Starts the Media Vault application.
     *
     * <p>The method connects to the database, initializes the required tables,
     * and repeatedly displays the login menu. Users may log in, register a new
     * account, or exit the application.</p>
     *
     * @param args command-line arguments supplied to the application
     * @throws SQLException if a database operation fails
     */
	public static void main(String[] args) throws SQLException {
		
		int checkCtr = 0;
		boolean securityCheck;
		String username, password, checkPassword, choice;
		
		try{
			// Establish the connection to the SQLite database.
		    conn = DatabaseConnection.connect();
		    
		    // Create the required database tables if they do not yet exist.
		    DatabaseInitializer initializer = new DatabaseInitializer();
		    initializer.initialize(conn);
		    
		    // Initialize the DAO responsible for user operations.
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
	
	/**
	 * Displays the main vault menu for the currently logged-in user.
	 *
	 * <p>Users may access the Song, Game, or Show vaults, view all media in
	 * their library, or return to the login menu.</p>
	 */
	public static void vaultMenu(){
		
		List<Media> combined = new ArrayList<>();
		
		List<Song> songList = new ArrayList<>();
		List<Game> gameList = new ArrayList<>();
		List<Show> showList = new ArrayList<>();
		mediaDAO = new MediaDAOImpl(conn, loggedInUser);
		boolean isAllMedias = true;
				
		String choice;
		    
	    do
	    {
	    	System.out.println("* * * * * * * * * * * MEDIA VAULT * * * * * * * * * * *");
	    	System.out.println("* - - - - - - - - - - Vault  Menu - - - - - - - - - - *");
		    System.out.println("* [1] Song Vault");
		    System.out.println("* [2] Game Vault");
		    System.out.println("* [3] Show Vault");
		    System.out.println("* [*] View All Media");
		    System.out.println("* [<] Back to Login Menu");
		    System.out.print("*\n* Enter your choice: ");
		    choice = scanner.nextLine();
	    	
		    /*
		    if(choice.equals("1"))
		    {
		    	runUserSettings();
		    }
		    else
		    */
		    
		    if(choice.equals("1"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		    	getMediaVault("Song");
		    }
		    else if(choice.equals("2"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		    	getMediaVault("Game");
		    }
		    else if(choice.equals("3"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		    	getMediaVault("Show");
		    }
		    else if(choice.equals("<"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
		    }
		    else if(choice.equals("*"))
		    {
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		    	
		    	try {
			    	songList = mediaDAO.getSongsByUser();
			    	gameList = mediaDAO.getGamesByUser();
			    	showList = mediaDAO.getShowsByUser();
			    	
			    	combined.addAll(songList);
			    	combined.addAll(gameList);
			    	combined.addAll(showList);
			    	
			    	printMedia(combined, isAllMedias);
		    	}
		    	catch(SQLException e) {
		    		System.out.println(" Error in loading medias.");
		    	}
		    }
		    else
		    {
		    	System.out.println("*\n* Invalid input! Please try again.");
		    	System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
		    }
		    
	    } while(!choice.equals("<"));
	}
	
	/*
	public static void runUserSettings(){
		
		System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    	System.out.println("\n= = = = = = = = = =  USER SETTINGS  = = = = = = = = = =");
    	System.out.println("= [1] Change Username");
    	System.out.println("= [2] Change Username");
    	System.out.println("= [3] Delete User Profile");
	}
	*/
	
	/**
	 * Displays and manages the vault for a specific media type.
	 *
	 * <p>This method allows the user to browse their media collection,
	 * manage playlists, search for new media, and perform operations
	 * such as viewing, adding, updating, or removing media entries.</p>
	 *
	 * @param mediaType the type of media to manage (Song, Game, or Show)
	 */
	public static void getMediaVault(String mediaType){
		
		int playlistChoice = -1, triggerValue = 0, playlistId = -1;
		String choice, choice2, choice7, title;
		
		mediaDAO = new MediaDAOImpl(conn, loggedInUser);
		mediaPlaylistDAO = new MediaPlaylistDAOImpl(conn, loggedInUser);
		
		//List<Media> mediaList = new ArrayList<>();
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
			    	System.out.printf("= - - - - - - - - - =  MY  %sS  = - - - - - - - - - =\n", mediaType.toUpperCase());
			    	System.out.println("= [1] Completed");
			    	System.out.println("= [2] In Progress");
			    	System.out.println("= [3] Planned");
			    	System.out.printf("= [*] View All My %ss\n", mediaType);
			    	System.out.printf("= [+] Add %s\n", mediaType);
			    	System.out.printf("= [<] Back to %s Vault\n", mediaType);
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
				    	
				    	doMediaSearch(playlistId, triggerValue, mediaType);
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
			    			if(mediaType.equalsIgnoreCase("song"))
			    			{
			    				playlists = mediaPlaylistDAO.getPlaylistsByUser(loggedInUser, "Song");
			    			}
			    			else if(mediaType.equalsIgnoreCase("game"))
			    			{
			    				playlists = mediaPlaylistDAO.getPlaylistsByUser(loggedInUser, "Game");
			    			}
			    			else if(mediaType.equalsIgnoreCase("show"))
			    			{
			    				playlists = mediaPlaylistDAO.getPlaylistsByUser(loggedInUser, "Show");
			    			}
			    			
			    			System.out.println();
				            System.out.println("-------------------------------------------------------------------------------------------------------------------");
				            System.out.printf("| %-3s | %-23s | %-11s | %-9s | %-11s | %-7s | %-29s |%n", "No.", "Title", "Total " + mediaType + "s", "Completed", "In Progress", "Planned", "Avg. Rating (Completed " + mediaType + "s)");
				            System.out.println("-------------------------------------------------------------------------------------------------------------------");
			    			
				            int ctr2 = 1;
				            
				            for(MediaPlaylist mp: playlists)
			    			{
			    				int completeTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.COMPLETED, mediaType),
			    					inProgressTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.IN_PROGRESS, mediaType),
			    					plannedTemp = mediaPlaylistDAO.countStatusedMedia(mp.getPlaylistId(), Status.PLANNED, mediaType);
			    				
			    				String ratingTemp = String.valueOf(mediaPlaylistDAO.calculateAvgRating(mp.getPlaylistId(), mediaType));
			    				
			    				if(completeTemp == 0)
			    					ratingTemp = "/-no completed " + mediaType.toLowerCase() + " yet-/";
			    				
			    				if(mediaType.equalsIgnoreCase("song"))
			    				{
			    					if(mp.getTitle().equals("all_songs"))
			    						mp.setTitle("All Songs");
			    				}
			    				else if(mediaType.equalsIgnoreCase("game"))
			    				{
			    					if(mp.getTitle().equals("all_games"))
			    						mp.setTitle("All Games");
			    				}
			    				else if(mediaType.equalsIgnoreCase("show"))
			    				{
			    					if(mp.getTitle().equals("all_shows"))
			    						mp.setTitle("All Shows");
			    				}
			    				
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
					    System.out.println("= [+] Add a " + mediaType + " Playlist");
				    	System.out.println("= [<] Back to " + mediaType + " Vault");
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
					    		if(mediaPlaylistDAO.createPlaylist(title, mediaType))
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
								    //MediaPlaylist mp = playlists.get(playlistChoice - 1);
								    //mediaList = mediaPlaylistDAO.getMediasInPlaylist(mp.getPlaylistId(), mediaType);
						
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
	
	/**
	 * Shortens a string so that it fits within a specified width.
	 *
	 * <p>If the text exceeds the specified width, it is truncated and an
	 * ellipsis (...) is appended.</p>
	 *
	 * @param text the text to format
	 * @param width the maximum allowed width
	 * @return the formatted string
	 */
	private static String fitToSpace(String text, int width) {
	    if (text == null) {
	        return "";
	    }

	    if (text.length() <= width) {
	        return text;
	    }

	    return text.substring(0, width - 3) + "...";
	}
	
	/**
	 * Displays a formatted table containing a list of media entries.
	 *
	 * <p>The table includes general media information such as title,
	 * creator, release year, status, rating, review status, and
	 * media-specific details. When displaying all media types, an
	 * additional column indicating the media type is shown.</p>
	 *
	 * @param mediaList the media entries to display
	 * @param isAllMedias whether the list contains multiple media types
	 */
	public static void printMedia(List<? extends Media> mediaList, boolean isAllMedias) {
		
		String mediaType = "";
		
	    System.out.println();
	    if(isAllMedias)
	    	System.out.print("-------");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        if(isAllMedias)
	    	System.out.printf("| %-4s ", "Type");
        System.out.printf("| %-3s | %-25s | %-20s | %-11s | %-11s | %-20s | %-22s | %-50s |%n", "No.", "Title", "Creator", "Year", "Status", "Rating", "Reviewed By User", "Info");
        if(isAllMedias)
	    	System.out.print("-------");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	    int ctr = 1;

	    for (Media media: mediaList)
	    {
	    	
	    	if (media instanceof Song)
				mediaType = "SONG";
			else if (media instanceof Game)
				mediaType = "GAME";
			else if (media instanceof Show)
				mediaType = "SHOW";
	    	
	    	if(isAllMedias)
	    		System.out.printf("| %-4s ", mediaType);
	    	System.out.printf("| %-3s | %-25s | %-20s | %-11s | %-11s | %-20s | %-22s | %-50s |%n", ctr++, fitToSpace(media.getTitle(), 25), fitToSpace(media.getCreator(), 20), media.getYearString(), media.getStatus().toDbString(), media.getUserRatingString(), media.getReviewedStatus(), fitToSpace(media.getMediaInfo(), 50));	    
	    }
	    
	    if(isAllMedias)
	    	System.out.print("-------");
	    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
	}
	
	/**
	 * Displays and manages media entries based on the selected view or playlist.
	 *
	 * <p>This method allows users to browse media by status or playlist,
	 * view detailed information, update status, rating, and review,
	 * remove media, or manage playlist contents.</p>
	 *
	 * @param playlistChoice the selected playlist number
	 * @param playlists the list of available playlists
	 * @param triggerValue determines which view or operation to perform
	 * @param mediaType the type of media being managed
	 */
	public static void updateMedia(int playlistChoice, List<MediaPlaylist> playlists, int triggerValue, String mediaType) {

		boolean allChecker = false;
		double rating = 0.0;
		String choice7, choice8, choice5, choice4, choice6, review; 
		int songChoice = 0, mediaState = 0;
		
		Media media = null;
		List<? extends Media> medias = new ArrayList<>();
		List<Media> mediaList = new ArrayList<>();
		boolean isAllMedias = false;
		
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
    				if(mediaType.equalsIgnoreCase("song"))
    				{	
			            for (Song song : mediaDAO.getSongsByUser())
			                if (song.getStatus() == Status.COMPLETED)
			                	mediaList.add(song);
    				}
    				else if(mediaType.equalsIgnoreCase("game"))
    				{
    					for (Game game : mediaDAO.getGamesByUser())
			                if (game.getStatus() == Status.COMPLETED)
			                	mediaList.add(game);
    				}
    				else if(mediaType.equalsIgnoreCase("show"))
    				{
    					for (Show show : mediaDAO.getShowsByUser())
			                if (show.getStatus() == Status.COMPLETED)
			                	mediaList.add(show);
    				}
		            
		            printMedia(mediaList, isAllMedias);
    			}
    			else if(triggerValue == 2)
    			{
    				if(mediaType.equalsIgnoreCase("song"))
    				{	
			            for (Song song : mediaDAO.getSongsByUser())
			                if (song.getStatus() == Status.IN_PROGRESS)
			                	mediaList.add(song);
    				}
    				else if(mediaType.equalsIgnoreCase("game"))
    				{
    					for (Game game : mediaDAO.getGamesByUser())
			                if (game.getStatus() == Status.IN_PROGRESS)
			                	mediaList.add(game);
    				}
    				else if(mediaType.equalsIgnoreCase("show"))
    				{
    					for (Show show : mediaDAO.getShowsByUser())
			                if (show.getStatus() == Status.IN_PROGRESS)
			                	mediaList.add(show);
    				}
    				
    				printMedia(mediaList, isAllMedias);
    			}
    			else if(triggerValue == 3)
    			{
    				if(mediaType.equalsIgnoreCase("song"))
    				{	
			            for (Song song : mediaDAO.getSongsByUser())
			                if (song.getStatus() == Status.PLANNED)
			                	mediaList.add(song);
    				}
    				else if(mediaType.equalsIgnoreCase("game"))
    				{
    					for (Game game : mediaDAO.getGamesByUser())
			                if (game.getStatus() == Status.PLANNED)
			                	mediaList.add(game);
    				}
    				else if(mediaType.equalsIgnoreCase("show"))
    				{
    					for (Show show : mediaDAO.getShowsByUser())
			                if (show.getStatus() == Status.PLANNED)
			                	mediaList.add(show);
    				}
    				
    				printMedia(mediaList, isAllMedias);
    			}
    			else if(triggerValue == 4)
    			{
					// my songs
					// getAllSongsByUser(); pero completed
					// all goods
					// gawing medias
    				if(mediaType.equalsIgnoreCase("song"))
    				{	
			            for (Song song : mediaDAO.getSongsByUser())
			                mediaList.add(song);
    				}
    				else if(mediaType.equalsIgnoreCase("game"))
    				{
    					for (Game game : mediaDAO.getGamesByUser())
			                mediaList.add(game);
    				}
    				else if(mediaType.equalsIgnoreCase("show"))
    				{
    					for (Show show : mediaDAO.getShowsByUser())
			                mediaList.add(show);
    				}
    				
    				printMedia(mediaList, isAllMedias);
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
    				
    				if(mediaType.equalsIgnoreCase("song"))
    				{
    					medias = mediaPlaylistDAO.getSongsInPlaylist(mp.getPlaylistId());
    				}
    				else if(mediaType.equalsIgnoreCase("game"))
    				{
    					medias = mediaPlaylistDAO.getGamesInPlaylist(mp.getPlaylistId());
    				}
    				else if(mediaType.equalsIgnoreCase("show"))
    				{
    					medias = mediaPlaylistDAO.getShowsInPlaylist(mp.getPlaylistId());
    				}
    				
    				printMedia(medias, isAllMedias);
    		    	
    		    	System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    		    	System.out.println("  * PLAYLIST TITLE: " + mp.getTitle());
    		    	System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
    		    	System.out.println("  * Total " + mediaType + "s: " + totalTemp);
    		    	System.out.println("  * # of Completed " + mediaType + "s: " + completeTemp);
    		    	System.out.println("  * # of " + mediaType + "s In Progress: " + inProgressTemp);
    		    	System.out.println("  * # of Planned " + mediaType + "s: " + plannedTemp);
    		    	System.out.println("  * Average Rating across Completed Entries: " + ratingTemp);
    		    	System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
    			}

	        }
	    	catch (SQLException e) {
	    			e.printStackTrace();
	            System.out.println("Could not load " + mediaType + "s.");
	        }
    		
    		if(triggerValue == 1)
    		{
    			System.out.printf("= - - - - - - - - = COMPLETED %sS = - - - - - - - - =\n", mediaType.toUpperCase());
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
	    			System.out.printf("  * [+] Add %s to Playlist\n", mediaType);
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
    		else if((triggerValue != 5 && 1 <= songChoice && songChoice <= mediaList.size()) || (triggerValue == 5 && 1 <= songChoice && songChoice <= medias.size()))
    		{
    			if(triggerValue != 5)
    				System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
    			else
    				System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
    			
				// all goods
    			if(triggerValue != 5)
    			{
    				media = mediaList.get(songChoice-1);
    			}
    			else
    			{
    				media = medias.get(songChoice-1);
    			}
    			
    			do
    			{
					// DO BY MEDIA TYPE
	    			System.out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	    			System.out.printf("  * %s %d : %s by %s\n", mediaType.toUpperCase(), songChoice, media.getTitle(), media.getCreator());
	    			System.out.println("  * - - - - - - - - - - - - - - - - - - - - - - - - - *");
	    			
	    			if(mediaType.equalsIgnoreCase("song"))
	    			{
	    				Song song = (Song) media;
	    				
	    				System.out.println("  * Year Released: " + song.getYearReleased());
	    				System.out.println("  * Album: " + song.getAlbum());
	    				System.out.println("  * Runtime: " + song.getRuntimeString());
	    				
	    			}
	    			else if (mediaType.equalsIgnoreCase("game"))
	    			{
	    			    Game game = (Game) media;
	    			    
	    			    System.out.println("  * Year Released: " + game.getYearReleased());
	    			    System.out.println("  * Genre: " + game.getGenre());
	    			    System.out.println("  * Avg. Playtime in Minutes: " + game.getAvgPlaytimeMins());
	    			}
	    			else if (mediaType.equalsIgnoreCase("show")) {
	    			    Show show = (Show) media;
	    			    
	    			    if(show.isAiring())
	    			    {
	    			    	System.out.println("  * Airing Status: Currently Airing");
	    			    	System.out.println("  * Year Started: " + show.getYearStart());
	    			    }
	    			    else
	    			    {
	    			    	System.out.println("  * Airing Status: Finished Airing");
	    			    	System.out.println("  * Year Started: " + show.getYearStart());
	    			    	System.out.println("  * Year Ended: " + show.getYearEnd());
	    			    }

	    			    System.out.println("  * Genre: " + show.getGenre());
	    			    System.out.println("  * Number of Seasons: " + show.getNumOfSeasons());
	    			}
	    			
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
	    				System.out.println("  * My Rating: /-complete to rate " + mediaType.toLowerCase() + "-/");
	    				System.out.println("  * My Review: /-complete to review " + mediaType.toLowerCase() +"-/");
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
	    			
	    			System.out.println("  * [-] Remove " + mediaType.toLowerCase());
	    			
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
	    				System.out.printf("  * [<] Back to Planned %ss\n", mediaType);
	    			}
	    			else if(triggerValue == 4)
	    			{
	    				System.out.printf("  * [<] Back to All %ss\n", mediaType);
	    			}
	    			else if(triggerValue == 5)
	    			{
	    				System.out.println("  * [<] Back to Playlists");
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
								            	        	System.out.println("  - {REVIEW " + mediaType.toUpperCase() + "?}");
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
					            		    mediaDAO.updateMediaRating(media, rating);
					            		    mediaDAO.updateMediaReview(media, review);
		
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
								            	        	System.out.println("  - {REVIEW " + mediaType.toUpperCase() + "?}");
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
					            		    mediaDAO.updateMediaRating(media, rating);
					            		    mediaDAO.updateMediaReview(media, review);
					            		    
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
		    					
			    				System.out.println("  - {CHANGE " + mediaType.toUpperCase() + " RATING}");
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
			    					System.out.println("  - {ADD "+ mediaType.toUpperCase() + " REVIEW}");
				    			else
				    				System.out.println("  - {CHANGE " + mediaType.toUpperCase() + " REVIEW}");
			    				
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
	    						
	    						if(mediaType.equalsIgnoreCase("song"))
	    						{
	    							mediaState = mediaDAO.deleteSong(media.getTitle(), media.getCreator());
	    						}
	    						else if(mediaType.equalsIgnoreCase("game"))
	    						{
	    							mediaState = mediaDAO.deleteGame(media.getTitle(), media.getCreator());
	    						}
	    						else if(mediaType.equalsIgnoreCase("show"))
	    						{
	    							mediaState = mediaDAO.deleteShow(media.getTitle(), media.getCreator());
	    						}
	    						
	    						if(mediaState == 0)
	    						{
	    							System.out.println(" - " + mediaType + " not found.");
	    						}
	    						else if(mediaState == 1)
	    						{
	    							System.out.println(" - " + media.getTitle() + " by " + media.getCreator() + " was removed from your " + mediaType + "s.");
	    						}
	    						else if(mediaState == 2)
	    						{
	    							System.out.println(" - " + mediaType + " was not found in your " + mediaType + "s.");
	    						}
	    					}
	    					else {
								// if in all_songs, remove song everywhere
	    					    if (allChecker){
	    					    	
	    					    	if(mediaType.equalsIgnoreCase("song"))
		    						{
		    							mediaState = mediaDAO.deleteSong(media.getTitle(), media.getCreator());
		    						}
		    						else if(mediaType.equalsIgnoreCase("game"))
		    						{
		    							mediaState = mediaDAO.deleteGame(media.getTitle(), media.getCreator());
		    						}
		    						else if(mediaType.equalsIgnoreCase("show"))
		    						{
		    							mediaState = mediaDAO.deleteShow(media.getTitle(), media.getCreator());
		    						}
		    						
	    					    	if(mediaState == 0)
		    						{
		    							System.out.println(" - " + mediaType + " not found.");
		    						}
		    						else if(mediaState == 1)
		    						{
		    							System.out.println(" - " + media.getTitle() + " by " + media.getCreator() + " was removed from your " + mediaType + "s.");
		    						}
		    						else if(mediaState == 2)
		    						{
		    							System.out.println(" - " + mediaType + " was not found in your " + mediaType + "s.");
		    						}
	    					    }
								// else, remove from playlist
	    					    else {
	    					        int mediaId = mediaDAO.findMediaId(media);
	    					        
	    					        if(mediaType.equalsIgnoreCase("song"))
	    					        {
	    					        	mediaPlaylistDAO.removeMediaFromPlaylist(mp.getPlaylistId(), mediaId, Type.SONG);
	    					        }
	    					        else if(mediaType.equalsIgnoreCase("game"))
	    					        {
	    					        	mediaPlaylistDAO.removeMediaFromPlaylist(mp.getPlaylistId(), mediaId, Type.GAME);
	    					        }
	    					        else if(mediaType.equalsIgnoreCase("show"))
	    					        {
	    					        	mediaPlaylistDAO.removeMediaFromPlaylist(mp.getPlaylistId(), mediaId, Type.SHOW);
	    					        }
	    					    }
	    					}
	    					
							// why oh why
							// if it works it works
	    					media.setReview("");
	    					media.setUserRating(0.0);
	    				}
	    				catch (SQLException e) {
	    					e.printStackTrace();
			    			System.out.println("  - Could not remove " + mediaType.toLowerCase() + ".");
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
    			doMediaSearch(mp.getPlaylistId(), triggerValue, mediaType);
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
	
	/**
	 * Searches for media and allows the user to add it to their library
	 * or a playlist.
	 *
	 * <p>Song searches are performed using the Spotify Web API, while
	 * other media types are added manually. Users may also choose to
	 * manually add songs if they are not available through Spotify.</p>
	 *
	 * @param playlistId the destination playlist ID
	 * @param triggerValue identifies the operation that initiated the search
	 * @param mediaType the type of media to search for
	 */
	public static void doMediaSearch(int playlistId, int triggerValue, String mediaType) {
		
		Status status = Status.PLANNED;
		int resultSize = 0, songChoice = 0, ctr;
		String choice3 = "", choice5, choice6, search, review = "";
		double rating = 0.0;
		boolean isSong = true;
		List<Song> results = null;
		
		if(!mediaType.equalsIgnoreCase("song"))
			isSong = false;
		
		try {
	    	do
	    	{
	    		if(isSong)
	    		{
		    		System.out.print(" Search Song: ");
			        search = scanner.nextLine();
			        
			        results = spotifyClient.searchTracks(search);
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
	    		}
	            
	            try {
	            	songChoice = Integer.parseInt(choice3);
	            }
	            catch (NumberFormatException e) {
	            }
	            
	            if(choice3.equals("<") && isSong)
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =");
	            }
	            else if(choice3.equals("?") && isSong)
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            }
				// manual adding
	            else if(choice3.equals("+") || !isSong)
	            {
	            	if(isSong)
	            		System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            	
	            	manuallyAddMedia(mediaType, playlistId, triggerValue);
	            	
	            	choice3 = "<";
	            }
				// if nakapili ng song through search
	            else if(1 <= songChoice && songChoice <= resultSize && isSong)
	            {
	            	System.out.println("= - - - - - - - - - - - - - - - - - - - - - - - - - - =\n");
	            	
	            	do
	            	{
		            	System.out.println(" - {" + mediaType.toUpperCase() + " STATUS}");
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
			            	        	System.out.println(" - {REVIEW " + mediaType.toUpperCase() + "?}");
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
	            	Song newSong = new Song(songTemp.getTitle(), status, rating, songTemp.getAlbum(), songTemp.getCreator(), songTemp.getYearReleased(), songTemp.getRuntimeSeconds(), review);
	            	
					doMediaOverwrite(playlistId, newSong, scanner, triggerValue, mediaType);
	            	
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
	
	/**
	 * Handles the overwrite process for an existing media entry.
	 *
	 * <p>If the selected media already exists in the user's library, this
	 * method prompts the user to confirm whether the existing status,
	 * personal rating, and review should be replaced with new values.
	 * When confirmed, the corresponding review information is updated
	 * in the database.</p>
	 *
	 * @param media the existing media item to update
	 * @param mediaType the type of media being overwritten
	 * @throws SQLException if a database error occurs while updating the media
	 */
	public static void doMediaOverwrite(int playlistId, Media newMedia, Scanner scanner, int triggerValue, String mediaType) {
		
		int mediaId;
		String choice2;
		boolean alreadyExists;
		Media oldMedia = null;
		
		// OVERWRITE LOGIC
		// if u add same song but different review
		// can choose if u overwrite
		
    	try {
    		mediaId = mediaDAO.findMediaId(newMedia);
    		
    		if(mediaId != -1)
    		{
	    		if(mediaType.equalsIgnoreCase("song"))
	    			oldMedia = mediaDAO.getSongOfUserById(mediaId);
	    		else if(mediaType.equalsIgnoreCase("game"))
	    			oldMedia = mediaDAO.getGameOfUserById(mediaId);
	    		else if(mediaType.equalsIgnoreCase("show"))
	    			oldMedia = mediaDAO.getShowOfUserById(mediaId);
    		}
    		
    		alreadyExists = (oldMedia != null);
    		
    		if(!alreadyExists)
    		{
    			mediaId = mediaDAO.addMedia(newMedia);
    		}
    		
    		if ( alreadyExists && (!oldMedia.getReview().equals(newMedia.getReview()) || oldMedia.getUserRating() != newMedia.getUserRating() || !oldMedia.getStatus().toDbString().equals(newMedia.getStatus().toDbString())) )
	        {
	            do
	            {
	                System.out.println(" - ");
	                System.out.println(" - Would you like to overwrite your previous status/rating/review?");
	                System.out.println(" - {STATUS}");
	                System.out.println(" - Previous: " + oldMedia.getStatus().toDbString());
	                System.out.println(" - New: " + newMedia.getStatus().toDbString());
	                System.out.println(" - {RATING}");
	                
	                if(oldMedia.getUserRating() == 0.0)
	                	System.out.println(" - Previous: /-complete to rate-/");
	                else
	                	System.out.println(" - Previous: " + oldMedia.getUserRating());
	                
	                if(newMedia.getUserRating() == 0.0)
	                	System.out.println(" - New: /-complete to rate-/");
	                else
	                	System.out.println(" - New: " + newMedia.getUserRating());
	                
	                System.out.println(" - {REVIEW}");
	                
	                if(oldMedia.getReview().equals(""))
	                {
	                	if(!oldMedia.getStatus().toDbString().equals("completed"))
	                		System.out.println(" - Previous: /-complete to review-/");
	                	else
	                		System.out.println(" - Previous: /-unreviewed-/");
	                }
	                else
	                	System.out.println(" - Previous: \"" + oldMedia.getReview() + "\"");
	                
	                if(newMedia.getReview().equals(""))
	                {
	                	if(!oldMedia.getStatus().toDbString().equals("completed"))
	                		System.out.println(" - New: /-complete to review-/");
	                	else
	                		System.out.println(" - New: /-unreviewed-/");
	                }
	                else
	                	System.out.println(" - New: \"" + newMedia.getReview() + "\"");
	                
	                System.out.println(" - ");
	                System.out.println(" - [1] Yes");
	                System.out.println(" - [2] No");
	                System.out.print(" - Enter your choice: ");

	                choice2 = scanner.nextLine();

	                if (choice2.equals("1"))
	                {
	                	if(triggerValue == 5)
	                	{
	                		
		                    mediaPlaylistDAO.addMediaToPlaylist(
		                        playlistId,
		                        mediaId,
		                        newMedia.getStatus(),
		                        newMedia.getUserRating(),
		                        newMedia.getReview(),
		                        mediaType
		                    );
	                	}
	                    
	                    mediaPlaylistDAO.updateAllPlaylists(newMedia);
	                }
	                else if (choice2.equals("2"))
	                {
	                    mediaPlaylistDAO.addMediaToPlaylist(
	                        playlistId,
	                        mediaId,
	                        oldMedia.getStatus(),
	                        oldMedia.getUserRating(),
	                        oldMedia.getReview(),
	                        mediaType
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
	        	if(triggerValue == 5)
	        	{
		            mediaPlaylistDAO.addMediaToPlaylist(
		                playlistId,
		                mediaId,
		                newMedia.getStatus(),
                        newMedia.getUserRating(),
                        newMedia.getReview(),
                        mediaType
		            );
	        	}
	        }

	        System.out.println(" - ");
	        System.out.println(" - " + newMedia.getTitle() + " by " + newMedia.getCreator() + " added to " + mediaType.toLowerCase() + "s successfully!");
	        if(triggerValue == 5)
	        	System.out.println(" - " + newMedia.getTitle() + " by " + newMedia.getCreator() + " added to playlist successfully!");
    	}	 
    	catch (SQLException e) {
    		e.printStackTrace();
    		System.out.println(" - SQL ERROR: " + e.getMessage());
    	}
	}
	
	/**
	 * Allows the user to manually add a media item to their library or
	 * a selected playlist.
	 *
	 * <p>The user is prompted to enter the media's information. The media
	 * is then inserted into the database and optionally added to the
	 * selected playlist.</p>
	 *
	 * @param mediaType the type of media to add
	 * @param playlistId the destination playlist ID
	 * @param triggerValue identifies the operation that initiated the add
	 */
	public static void manuallyAddMedia(String mediaType, int playlistId, int triggerValue) {
		
		Status status = Status.PLANNED;
		double rating = 0.0;
		String title = "", album = "", artist = "", director = "", developer = "", review = "", genre = "", airing = "", choice4, choice6;
		int yearReleased = 0, yearStart = 0, yearEnd = 0, runtimeSeconds = 0, numOfSeasons = 0, avgPlaytimeMins = 0;
		boolean integerLoop = false, isAiring = true;
		Media newMedia = null;
		
		do
    	{
			do
			{
				integerLoop = false;
				
				try {
					if(mediaType.equalsIgnoreCase("song"))
					{
			        	System.out.print(" Enter Song Title: ");
			        	title = scanner.nextLine();
			        	System.out.print(" Enter Artist's Name: ");
			        	artist = scanner.nextLine();
			        	System.out.print(" Enter Year Released: ");
			        	yearReleased = Integer.parseInt(scanner.nextLine());
			        	
			        	if(yearReleased <= 0)
			        		integerLoop = true;
			        	
			        	System.out.print(" Enter Album Title: ");
			        	album = scanner.nextLine();
			        	System.out.print(" Enter Runtime in Seconds: ");
			        	runtimeSeconds = Integer.parseInt(scanner.nextLine());
			        	
			        	if(runtimeSeconds <= 0)
			        		integerLoop = true;
					}
					else if(mediaType.equalsIgnoreCase("game"))
					{
						System.out.print(" Enter Game Title: ");
			        	title = scanner.nextLine();
			        	System.out.print(" Enter Developer's Name: ");
			        	developer = scanner.nextLine();
			        	System.out.print(" Enter Year Released: ");
			        	yearReleased = Integer.parseInt(scanner.nextLine());
			        	
			        	if(yearReleased <= 0)
			        		integerLoop = true;
			        	
			        	System.out.print(" Enter Genre: ");
			        	genre = scanner.nextLine();
			        	System.out.print(" Enter Average Playtime in Minutes: ");
			        	avgPlaytimeMins = Integer.parseInt(scanner.nextLine());
			        	
					}
					else if(mediaType.equalsIgnoreCase("show"))
					{
						System.out.print(" Enter Show Title: ");
			        	title = scanner.nextLine();
			        	System.out.print(" Enter Director's Name: ");
			        	director = scanner.nextLine();
			        	do
			        	{
				        	System.out.print(" Still Airing? [Y/N]: ");
				        	airing = scanner.nextLine();
				        	
				        	if(airing.equals("Y"))
				        	{
				        		isAiring = true;
				        	}
				        	else if(airing.equals("N"))
				        	{
				        		isAiring = false;
				        	}
				        	else
				        	{
				        		System.out.println(" ");
				        		System.out.println(" Invalid input! Please try again.");
				        	}
				        	
			        	} while(!airing.equals("Y") && !airing.equals("N"));
			        	
			        	System.out.print(" Enter Year Started: ");
			        	yearStart = Integer.parseInt(scanner.nextLine());
			        	
			        	if(yearStart <= 0)
			        		integerLoop = true;
			        	
			        	if(airing.equals("N"))
			        	{
				        	System.out.print(" Enter Year Ended: ");
				        	yearEnd = Integer.parseInt(scanner.nextLine());
				        	
				        	if(yearEnd <= 0)
				        		integerLoop = true;
			        	}
			        	
			        	System.out.print(" Enter Genre: ");
			        	genre = scanner.nextLine();
			        	System.out.print(" Enter Number of Seasons: ");
			        	numOfSeasons = Integer.parseInt(scanner.nextLine());
			        	
			        	if(numOfSeasons <= 0)
			        		integerLoop = true;
					}
		        	
		        	System.out.println();
				}
				catch(NumberFormatException e) {
					integerLoop = true;
					System.out.println(" Invalid input/s in integer-type fields.\n");
				}
			} while(integerLoop);
        	
        	do
        	{
            	System.out.println(" - {" + mediaType.toUpperCase() + " STATUS}");
            	System.out.println(" - [1] Completed");
            	System.out.println(" - [2] In Progress");
            	System.out.println(" - [3] Planned");
            	System.out.println(" - [X] Redo Manual Add");
            	System.out.println(" - ");
            	System.out.print(" - Input Status: ");
            	choice4 = scanner.nextLine();
            	
            	if(choice4.equals("1"))
            	{
            		System.out.print(" - Input Personal Rating: ");
            		
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
	            	        	System.out.println(" - {REVIEW " + mediaType.toUpperCase()+ "?}");
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
            	        System.out.println(" Please enter a valid number.\n");
            	        choice4 = "WRONG";
            	    }
            	}
            	else if(choice4.equals("2"))
            	{
            		status = Status.IN_PROGRESS;
            	}
            	else if(choice4.equals("3"))
            	{
            		status = Status.PLANNED;
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
    	
		if(mediaType.equalsIgnoreCase("song"))
    		newMedia = new Song(title, status, rating, album, artist, yearReleased, runtimeSeconds, review);
		if(mediaType.equalsIgnoreCase("game"))
    		newMedia = new Game(title, developer, yearReleased, status, rating, review, genre, avgPlaytimeMins);
		if(mediaType.equalsIgnoreCase("show"))
    		newMedia = new Show(title, director, yearStart, yearEnd, status, rating, review, genre, numOfSeasons, isAiring);
    	
		doMediaOverwrite(playlistId, newMedia, scanner, triggerValue, mediaType);
	}
}