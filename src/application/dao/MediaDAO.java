package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.model.Game;
import application.model.Media;
import application.model.Show;
import application.model.Song;
import application.model.Status;
import application.model.Type;

public class MediaDAO {
	
	Connection conn;
	int userId;

	public MediaDAO(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}

	public int addMedia(Media media) throws SQLException {
	    int mediaId = -1;
	    String table = "";
	    String playlistTable = "";
	    String junctionTable = "";
	    String reviewTable = "";
	    String playlistTitle = "";
	    String junctionMediaIdColumn = "";

	    // Set up strings
	    if (media instanceof Song) {
	        table = "songs";
	        playlistTable = "songs_playlists";
	        junctionTable = "songs_playlist_items";
	        reviewTable = "songs_reviews";
	        playlistTitle = "all_songs";
	        junctionMediaIdColumn = "song_id";
	    } else if (media instanceof Game) {
	        table = "games";
	        playlistTable = "games_playlists";
	        junctionTable = "games_playlist_items";
	        reviewTable = "games_reviews";
	        playlistTitle = "all_games";
	        junctionMediaIdColumn = "game_id";
	    }
	    else if (media instanceof Show) {
	        table = "shows";
	        playlistTable = "shows_playlists";
	        junctionTable = "shows_playlist_items";
	        reviewTable = "shows_reviews";
	        playlistTitle = "all_shows";
	        junctionMediaIdColumn = "show_id";
	    }

	    // Check if media is already added
	    String findMediaSql = "SELECT id FROM " + table + " WHERE title = ? AND creator = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(findMediaSql)) {
	        stmt.setString(1, media.getTitle());
	        stmt.setString(2, media.getCreator());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) mediaId = rs.getInt("id");
	        }
	    }
	    
	    // If media does not exists, do:
	    if (mediaId <= 0) {
	    	// Add new entry
	    	String insertMedia = "INSERT INTO " + table + "(title, creator) VALUES (?, ?)";
	    	try (PreparedStatement stmt = conn.prepareStatement(insertMedia, Statement.RETURN_GENERATED_KEYS)) {
	    		stmt.setString(1, media.getTitle());
	    		stmt.setString(2, media.getCreator());
	    		stmt.executeUpdate();
	    		try (ResultSet keys = stmt.getGeneratedKeys()) {
	    	        if (keys.next())
	    	            mediaId = keys.getInt(1);
	    	    }
	    		
	    	}
	    	
	    	// Insert media specific data 
	        switch (table) {
	            case "songs":
	                Song song = (Song) media;
	                String insertSong = "UPDATE songs "
	                				  + "SET album = ?, year = ?, runtime_seconds = ? "
	                				  + "WHERE id = ?";
	                try (PreparedStatement stmt = conn.prepareStatement(insertSong)) {
	                    stmt.setString(1, song.getAlbum());
	                    stmt.setInt(2, song.getYearReleased());
	                    stmt.setInt(3, song.getRuntimeSeconds());
	                    stmt.setInt(4, mediaId);
	                    stmt.executeUpdate();
	                }
	                break;

	            case "games":
	                Game game = (Game) media;
	                String insertGame = "UPDATE games "
	                				  + "SET avg_playtime_mins = ? "
	                				  + "WHERE id = ?";
	                try (PreparedStatement stmt = conn.prepareStatement(insertGame)) {
	                    stmt.setInt(1, game.getAvgPlaytimeMins());
	                    stmt.setInt(2, mediaId);
	                    stmt.executeUpdate();
	                }
	                break;
	            case "shows":
	            	Show show = (Show) media;
	                String insertShow = "UPDATE shows "
	                				  + "SET num_of_seasons = ?, num_of_episodes = ?, "
	                				  + "avg_mins_per_ep = ? "
	                				  + "WHERE id = ?";
	                try (PreparedStatement stmt = conn.prepareStatement(insertShow)) {
	                    stmt.setInt(1, show.getNumOfSeasons());
	                    stmt.setInt(2, show.getNumOfEpisodes());
	                    stmt.setInt(3, show.getAvgMinsPerEp());
	                    stmt.setInt(6, mediaId);
	                    stmt.executeUpdate();
	                }
	                break;
	        }
	    }

	    // Generic fetch all_media table
	    int playlistId = -1;
	    String findPlaylistSql = "SELECT id FROM " + playlistTable + " WHERE user_id = ? AND title = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(findPlaylistSql)) {
	        stmt.setInt(1, userId);
	        stmt.setString(2, playlistTitle);
	        try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) playlistId = rs.getInt("id"); }
	    }

	    if (playlistId == -1) {
	        String insertPlaylistSql = "INSERT INTO " + playlistTable + " (user_id, title) VALUES (?, ?)";
	        
	        try (PreparedStatement stmt = conn.prepareStatement(insertPlaylistSql, Statement.RETURN_GENERATED_KEYS)) {
	            stmt.setInt(1, userId);
	            stmt.setString(2, playlistTitle);
	            
	            stmt.executeUpdate();
	            try (ResultSet keys = stmt.getGeneratedKeys()) { if (keys.next()) playlistId = keys.getInt(1); }
	        }
	    }
	    
	    // Add to playlist_items table
	    String insertItemSql = "INSERT OR IGNORE INTO " + junctionTable + " (playlist_id, " + junctionMediaIdColumn + ") "
	    					 + "VALUES (?, ?) ";
	    
		try (PreparedStatement stmt = conn.prepareStatement(insertItemSql)) {
			stmt.setInt(1, playlistId);
		    stmt.setInt(2, mediaId);
		    stmt.executeUpdate();
		}
		
		String insertReviewSql = "INSERT OR IGNORE INTO " + reviewTable + " (user_id, " + junctionMediaIdColumn + ", status, user_rating, review) "
				 + "VALUES (?, ?, ?, ?, ?) ";

		try (PreparedStatement stmt = conn.prepareStatement(insertReviewSql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, mediaId);
			stmt.setString(3, media.getStatus().toDbString());
			stmt.setDouble(4, media.getUserRating());
			stmt.setString(5, media.getReview());
			
			stmt.executeUpdate();
			
			System.out.println("Added reviews");
		} catch(SQLException e) {
			e.printStackTrace();
		}

	    return mediaId;
	}

	public List<Media> getMediasByUser() throws SQLException {
	    List<Media> medias = new ArrayList<>();
	    String sql = """
	        SELECT s.id, s.title, s.album, s.creator, s.year, s.runtime_seconds, sr.status, sr.user_rating, sr.review
	        FROM songs_playlists sp
	        INNER JOIN songs_playlist_items spi ON sp.id = spi.playlist_id
	        INNER JOIN songs s ON spi.song_id = s.id
	        LEFT JOIN songs_reviews sr ON s.id = sr.song_id
	        WHERE sp.user_id = ? AND sp.title = 'all_songs'
	        """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Song song = new Song(
		            				rs.getString("title"),
				                    rs.getString("creator"),
				                    rs.getInt("year"),
				                    Status.fromDbString(rs.getString("status")),
				                    rs.getDouble("user_rating"),
				                    rs.getString("review"),
				                    rs.getString("album"),
				                    rs.getInt("runtime_seconds")  
	                );
	                song.setMediaId(rs.getInt("id"));
	                medias.add(song);
	            }
	        }
	    }

	    return medias;
	}

	public List<Song> getSongsByUser(int userId) throws SQLException {
	    List<Song> songs = new ArrayList<>();
	    for (Media media : getMediasByUser()) {
	        if (media instanceof Song) {
	            songs.add((Song) media);
	        }
	    }
	    return songs;
	}

	public Media getMediaOfUserById(int mediaId) throws SQLException {
	    String sql = """
	        SELECT s.id, s.title, s.album, s.creator, s.year, s.runtime_seconds, sr.status, sr.user_rating, sr.review
	        FROM songs_playlists sp
	        INNER JOIN songs_playlist_items spi ON sp.id = spi.playlist_id
	        INNER JOIN songs s ON spi.song_id = s.id
	        LEFT JOIN songs_reviews sr ON s.id = sr.song_id
	        WHERE sp.user_id = ? AND s.id = ?
	        """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setInt(2, mediaId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                Song song = new Song(
	                				rs.getString("title"),
				                    rs.getString("creator"),
				                    rs.getInt("year"),
				                    Status.fromDbString(rs.getString("status")),
				                    rs.getDouble("user_rating"),
				                    rs.getString("review"),
				                    rs.getString("album"),
				                    rs.getInt("runtime_seconds")         
	                );
	                song.setMediaId(rs.getInt("id"));
	                return song;
	            }
	        }
	    }
	    return null;
	}

	public Song getSongOfUserById(int songId) throws SQLException {
	    Media media = getMediaOfUserById(songId);
	    return media instanceof Song ? (Song) media : null;
	}

	public void updateMediaStatus(Media media, Status newStatus) throws SQLException {
		
		String table = null;
		
		if (media instanceof Song)
			table = "song";
		else if (media instanceof Game)
			table = "game";
		else if (media instanceof Show)
			table = "show";
		
		String sql = "UPDATE " + table + "s_reviews "
				   + "SET status = ? "
				   + "WHERE user_id = ? AND " + table + "_id = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, newStatus.toDbString());
	        stmt.setInt(2, userId);
	        stmt.setInt(3, findMediaId(media));
	        stmt.executeUpdate();
	    }
	}

	public void updateMediaRating(Media media, double rating) throws SQLException {
		String table = null;
		
		if (media instanceof Song)
			table = "song";
		else if (media instanceof Game)
			table = "game";
		else if (media instanceof Show)
			table = "show";
		
		String sql = "UPDATE " + table + "s_reviews "
				   + "SET user_rating = ? "
				   + "WHERE user_id = ? AND " + table + "_id = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setDouble(1, rating);
	        stmt.setInt(2, userId);
	        stmt.setInt(3, findMediaId(media));
	        stmt.executeUpdate();
	    }
	}

	public void updateMediaReview(Media media, String review) throws SQLException {
		String table = null;
		
		if (media instanceof Song)
			table = "song";
		else if (media instanceof Game)
			table = "game";
		else if (media instanceof Show)
			table = "show";
		
		String sql = "UPDATE " + table + "s_reviews "
				   + "SET review = ? "
				   + "WHERE user_id = ? AND " + table + "_id = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, review);
	        stmt.setInt(2, userId);
	        stmt.setInt(3, findMediaId(media));
	        stmt.executeUpdate();
	    }
	}

	public int deleteMedia(String title, String creator, Type type) throws SQLException {
	    if (type != Type.SONG) {
	        return 0;
	    }

	    String findSongSql = "SELECT id FROM songs WHERE title = ? AND creator = ?";
	    int songId = -1;
	    try (PreparedStatement stmt = conn.prepareStatement(findSongSql)) {
	        stmt.setString(1, title);
	        stmt.setString(2, creator);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                songId = rs.getInt("id");
	            }
	        }
	    }

	    if (songId == -1) {
	        return 0;
	    }

	    String deleteItemSql = "DELETE FROM songs_playlist_items WHERE song_id = ? AND playlist_id IN (SELECT id FROM songs_playlists WHERE user_id = ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(deleteItemSql)) {
	        stmt.setInt(1, songId);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	    }

	    return 1;
	}

	public int deleteSong(int userId, String title, String creator) throws SQLException {
	    this.userId = userId;
	    return deleteMedia(title, creator, Type.SONG);
	}
	
	public int findMediaId(Media media) throws SQLException {
		String table = null;
		
		if (media instanceof Song)
			table = "songs";
		else if (media instanceof Game)
			table = "games";
		else if (media instanceof Show)
			table = "shows";
		
		String sql = "SELECT id FROM " + table + " WHERE title = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, media.getTitle());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            }
	        }
	    }
		
		return -1;
	}

}
