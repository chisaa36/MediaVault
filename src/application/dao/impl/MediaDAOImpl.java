package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.model.Media;
import application.model.Song;
import application.model.Game;
import application.model.Show;
import application.model.Status;
import application.model.Type;

public abstract class MediaDAOImpl {
	
	private Connection conn;
	private int userId;
	
	public MediaDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}
	
	public int addMedia(Media media, int userId) throws SQLException {
	    int mediaId = -1;

	    String table = "";
	    String playlistTable = "";
	    String junctionTable = "";
	    String playlistTitle = "";
	    String junctionMediaIdColumn = "";

	    // Set up strings
	    if (media instanceof Song) {
	        table = "songs";
	        playlistTable = "songs_playlists";
	        junctionTable = "songs_playlist_items";
	        playlistTitle = "all_songs";
	        junctionMediaIdColumn = "songs_id";
	    }
	    else if (media instanceof Game) {
	        table = "games";
	        playlistTable = "games_playlists";
	        junctionTable = "games_playlist_items";
	        playlistTitle = "all_games";
	        junctionMediaIdColumn = "game_id";
	    }
	    else if (media instanceof Show) {
	        table = "shows";
	        playlistTable = "shows_playlists";
	        junctionTable = "shows_playlist_items";
	        playlistTitle = "all_shows";
	        junctionMediaIdColumn = "show_id";
	    }

	    // Check if media is already added
	    String findMediaSql =
	            "SELECT id FROM " + table + " WHERE title = ? AND creator = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(findMediaSql)) {
	        stmt.setString(1, media.getTitle());
	        stmt.setString(2, media.getCreator());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next())
	                mediaId = rs.getInt("id");
	        }
	    }

	    // If media does not exist
	    if (mediaId == -1) {

	        // Insert base media row
	        String insertMedia =
	                "INSERT INTO " + table + " (title, creator) VALUES (?, ?)";

	        try (PreparedStatement stmt =
	                     conn.prepareStatement(insertMedia, Statement.RETURN_GENERATED_KEYS)) {

	            stmt.setString(1, media.getTitle());
	            stmt.setString(2, media.getCreator());
	            stmt.executeUpdate();

	            try (ResultSet keys = stmt.getGeneratedKeys()) {
	                if (keys.next())
	                    mediaId = keys.getInt(1);
	            }
	        }

	        // Insert media-specific data
	        switch (table) {

	            case "songs":
	                Song song = (Song) media;

	                String updateSong =
	                        "UPDATE songs " +
	                        "SET album = ?, year_released = ?, runtime_seconds = ? " +
	                        "WHERE id = ?";

	                try (PreparedStatement stmt = conn.prepareStatement(updateSong)) {
	                    stmt.setString(1, song.getAlbum());
	                    stmt.setInt(2, song.getYearReleased());
	                    stmt.setInt(3, song.getRuntimeSeconds());
	                    stmt.setInt(4, mediaId);
	                    stmt.executeUpdate();
	                }
	                break;

	            case "games":
	                Game game = (Game) media;

	                String updateGame =
	                        "UPDATE games " +
	                        "SET avg_playtime_mins = ? " +
	                        "WHERE id = ?";

	                try (PreparedStatement stmt = conn.prepareStatement(updateGame)) {
	                    stmt.setInt(1, game.getAvgPlaytimeMins());
	                    stmt.setInt(2, mediaId);
	                    stmt.executeUpdate();
	                }
	                break;
	        }
	    }

	    // Generic fetch all_media playlist
	    int playlistId = -1;

	    String findPlaylistSql =
	            "SELECT id FROM " + playlistTable +
	            " WHERE user_id = ? AND title = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(findPlaylistSql)) {
	        stmt.setInt(1, userId);
	        stmt.setString(2, playlistTitle);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next())
	                playlistId = rs.getInt("id");
	        }
	    }

	    if (playlistId == -1) {

	        String insertPlaylistSql =
	                "INSERT INTO " + playlistTable +
	                " (user_id, title) VALUES (?, ?)";

	        try (PreparedStatement stmt =
	                     conn.prepareStatement(insertPlaylistSql,
	                             Statement.RETURN_GENERATED_KEYS)) {

	            stmt.setInt(1, userId);
	            stmt.setString(2, playlistTitle);
	            stmt.executeUpdate();

	            try (ResultSet keys = stmt.getGeneratedKeys()) {
	                if (keys.next())
	                    playlistId = keys.getInt(1);
	            }
	        }
	    }

	    // Add to reviews table
	    String insertItemSql =
	            "INSERT OR IGNORE INTO " + junctionTable +
	            " (playlist_id, " + junctionMediaIdColumn +
	            ", status, user_rating, review) VALUES (?, ?, ?, ?, ?)";

	    try (PreparedStatement stmt = conn.prepareStatement(insertItemSql)) {
	        stmt.setInt(1, playlistId);
	        stmt.setInt(2, mediaId);
	        stmt.setString(3, media.getStatus().toDbString());
	        stmt.setDouble(4, media.getUserRating());
	        stmt.setString(5, media.getReview());
	        stmt.executeUpdate();
	    }

	    return mediaId;
	}
	
	public Media getMediaOfUserById(int mediaId, Type type) throws SQLException {
		
		if(type == Type.SONG)
		{
			String sql = """
					SELECT s.id, s.title, spi.status, spi.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds, spi.review
					FROM songs_playlists sp
					INNER JOIN songs_playlist_items spi
					ON sp.id = spi.playlist_id
					INNER JOIN songs s
					ON spi.song_id = s.id
					WHERE sp.user_id = ? AND s.id = ?
					""";
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, userId);
				stmt.setInt(2, mediaId);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return new Song(rs.getString("title"),
									Status.fromDbString(rs.getString("status")),
									rs.getDouble("user_rating"),
									rs.getString("album"),
									rs.getString("artist"),
									rs.getInt("year_released"),
									rs.getInt("runtime_seconds"),
									rs.getString("review"));
				}
				else {
					System.out.println("Song not found");
				}
			}
			catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		else if(type == Type.GAME)
		{
			
		}
		else if(type == Type.SHOW)
		{
			
		}
		
		return null;
	}
	
	public List<? extends Media> getMediasByUser(Type type) throws SQLException {
		
		if(type == Type.SONG)
		{
		    List<Song> songs = new ArrayList<>();
		
		    String sql = """
		        SELECT 
		            s.title,
		            s.album,
		            s.artist,
		            s.year_released,
		            s.runtime_seconds,
		            spi.status,
		            spi.user_rating,
		            spi.review
		        FROM songs_playlists sp
		        INNER JOIN songs_playlist_items spi
		            ON sp.id = spi.playlist_id
		        INNER JOIN songs s
		            ON spi.song_id = s.id
		        WHERE sp.user_id = ?
		          AND sp.title = 'all_songs'
		    """;
		
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		
		        stmt.setInt(1, userId);
		
		        ResultSet rs = stmt.executeQuery();
		
		        while (rs.next()) {
		
		            Song song = new Song(
		                rs.getString("title"),
		                Status.fromDbString(rs.getString("status")),
		                rs.getDouble("user_rating"),
		                rs.getString("album"),
		                rs.getString("artist"),
		                rs.getInt("year_released"),
		                rs.getInt("runtime_seconds"),
		                rs.getString("review")
		            );
		
		            song.setReview(rs.getString("review"));
		
		            songs.add(song);
		        }
		    }
		
		    return songs;
		}
		else if(type == Type.GAME)
		{
			
		}
		else if(type == Type.SHOW)
		{
			
		}
		
		return null;
	}
	
	public int deleteMedia(String title, String artist, Type type) throws SQLException {
		
	    int mediaId = getMediaId(title, artist, type), result = 1;

	    if (mediaId == -1) {
	        result = 0;
	    }
	    
	    if(result != 0)
	    {
		    String sql = """
		        DELETE FROM medias_playlist_items
		        WHERE media_id = ? AND type = ?
		        AND playlist_id IN (
		            SELECT id
		            FROM mediass_playlists
		            WHERE user_id = ?
		        )
		        """;
	
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, mediaId);
		        stmt.setString(2, type.toDbString());
		        stmt.setInt(3, userId);
	
		        int rowsDeleted = stmt.executeUpdate();
	
		        if (rowsDeleted > 0)
		        {
		        	result = 1;
		        }
		        else
		        {
		        	result = 2;
		        }
		    }
	    }
	    
	    return result;
	}
	
	public void updateMediaRating(Media media, double rating, Type type) throws SQLException {

		String mediaType = null;
		
		if (media instanceof Song) {
			mediaType = "song";
		}
		if (media instanceof Game) {
			mediaType = "game";
		}
		if (media instanceof Show) {
			mediaType = "show";
		}
		
	    String sql = "UPDATE" + mediaType + "s_playlist_items " + 
	    			"SET user_rating = ? WHERE " + mediaType + "_id = (SELECT id FROM " + mediaType + "s " +
	    			"WHERE title = ? AND artist = ?) AND playlist_id = (SELECT id FROM " + mediaType + "s_playlists " +
	    			"WHERE user_id = ? AND title = 'all_" + mediaType + "s'";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setDouble(1, rating);
	        stmt.setString(2, media.getTitle());
	        stmt.setString(3, media.getCreator());
	        stmt.setInt(4, userId);
	        stmt.setString(5, type.toDbString());

	        stmt.executeUpdate();
	    }
	}
	
	public void addReview(Media media, String review, Type type) throws SQLException {
		
		String mediaType = null;
		
		if (media instanceof Song) {
			mediaType = "song";
		}
		if (media instanceof Game) {
			mediaType = "game";
		}
		if (media instanceof Show) {
			mediaType = "show";
		}
		
	    String sql = "UPDATE" + mediaType + "s_playlist_items " + 
	    			"SET user_rating = ? WHERE media_id = (SELECT id FROM " + mediaType + "s " +
	    			"WHERE title = ? AND artist = ?) AND playlist_id = (SELECT id FROM " + mediaType + "s_playlists " +
	    			"WHERE user_id = ? AND title = 'all_" + mediaType + "s'";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, review);
	        stmt.setString(2, media.getTitle());
	        stmt.setString(3, media.getCreator());
	        stmt.setInt(4, userId);
	        stmt.setString(5, type.toDbString());

	        stmt.executeUpdate();
	    }
	}
	
	public void updateStatus(Media media, Status newStatus, Type type) throws SQLException {
		
		String mediaType = null;
		
		if (media instanceof Song) {
			mediaType = "song";
		}
		if (media instanceof Game) {
			mediaType = "game";
		}
		if (media instanceof Show) {
			mediaType = "show";
		}
		
	    String sql = "UPDATE" + mediaType + "s_playlist_items " + 
	    			"SET user_rating = ? WHERE media_id = (SELECT id FROM " + mediaType + "s " +
	    			"WHERE title = ? AND artist = ?) AND playlist_id = (SELECT id FROM " + mediaType + "s_playlists " +
	    			"WHERE user_id = ? AND title = 'all_" + mediaType + "s'";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, newStatus.toDbString());
	        stmt.setDouble(2, media.getUserRating());
	        stmt.setString(3, media.getReview());
	        stmt.setString(4, media.getTitle());
	        stmt.setString(5, media.getCreator());
	        stmt.setInt(6, userId);
	        stmt.setString(7, type.toDbString());

	        stmt.executeUpdate();
	    }
	}
	
	public int getMediaId(String title, String artist, Type type) throws SQLException {

	    String sql = "SELECT id FROM medias WHERE title = ? AND artist = ? AND type = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, title);
	        stmt.setString(2, artist);
	        stmt.setString(3, type.toDbString());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            }
	        }
	    }

	    return -1;
	}
}
