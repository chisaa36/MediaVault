package application.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.dao.SongDAO;
import application.model.Song;
import application.model.Status;

public class SongDAOImpl implements SongDAO {
	
	private Connection conn;
	private int userId;
	
	public SongDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}
	
	@Override
	public int addSong(Song song, int userId) throws SQLException {

	    int songId = -1;
	    int playlistId = -1;

	    try {
	        conn.setAutoCommit(false);

	        // 1. Check if song already exists
	        String findSongSql = """
	            SELECT id FROM songs
	            WHERE title = ? AND artist = ?
	        """;

	        try (PreparedStatement stmt = conn.prepareStatement(findSongSql)) {
	            stmt.setString(1, song.getTitle());
	            stmt.setString(2, song.getArtist());

	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                songId = rs.getInt("id");
	            }
	        }

	        // 2. If song does not exist, insert it
	        if (songId == -1) {
	            String insertSongSql = """
	                INSERT INTO songs
	                (title, album, artist, year_released, runtime_seconds)
	                VALUES (?, ?, ?, ?, ?)
	            """;

	            try (PreparedStatement stmt = conn.prepareStatement(insertSongSql, Statement.RETURN_GENERATED_KEYS)) {
	                stmt.setString(1, song.getTitle());
	                stmt.setString(2, song.getAlbum());
	                stmt.setString(3, song.getArtist());
	                stmt.setInt(4, song.getYearReleased());
	                stmt.setInt(5, song.getRuntimeSeconds());

	                stmt.executeUpdate();

	                ResultSet keys = stmt.getGeneratedKeys();

	                if (keys.next()) {
	                    songId = keys.getInt(1);
	                }
	            }
	        }

	        // 3. Get this user's all_songs playlist
	        String findPlaylistSql = """
	            SELECT id FROM songs_playlists
	            WHERE user_id = ? AND title = 'all_songs'
	        """;

	        try (PreparedStatement stmt = conn.prepareStatement(findPlaylistSql)) {
	            stmt.setInt(1, userId);

	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                playlistId = rs.getInt("id");
	            }
	        }

	        // 4. If playlist does not exist, create it
	        if (playlistId == -1) {
	            String insertPlaylistSql = """
	                INSERT INTO songs_playlists (user_id, title)
	                VALUES (?, 'all_songs')
	            """;

	            try (PreparedStatement stmt = conn.prepareStatement(insertPlaylistSql, Statement.RETURN_GENERATED_KEYS)) {
	                stmt.setInt(1, userId);
	                stmt.executeUpdate();

	                ResultSet keys = stmt.getGeneratedKeys();

	                if (keys.next()) {
	                    playlistId = keys.getInt(1);
	                }
	            }
	        }

	        // 5. Add song to user's all_songs playlist with personal data
	        String insertItemSql = """
	            INSERT OR IGNORE INTO songs_playlist_items
	            (playlist_id, songs_id, status, user_rating, review)
	            VALUES (?, ?, ?, ?, ?)
	        """;

	        int rowsInserted = 0;

	        try (PreparedStatement stmt = conn.prepareStatement(insertItemSql)) {
	            stmt.setInt(1, playlistId);
	            stmt.setInt(2, songId);
	            stmt.setString(3, song.getStatus().toDbString());
	            stmt.setDouble(4, song.getUserRating());
	            stmt.setString(5, song.getReview());

	            rowsInserted = stmt.executeUpdate();
	        }

	        conn.commit();

	        if(rowsInserted>0)
	            System.out.println(" - " + song.getTitle() + " by " + song.getArtist() + " added successfully!");
	        else
	            System.out.println(" - " + song.getTitle() + " by " + song.getArtist() + " is already in your songs!");

	    }
	    catch (SQLException e) {
	        conn.rollback();
	        throw e;

	    } finally {
	        conn.setAutoCommit(true);
	    }

	    return songId;
	}
	
	@Override
	public Song getSongById(int songId) throws SQLException {
		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds, s.review
				FROM songs_playlists sp
				INNER JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN songs s
				ON spi.songs_id = s.id
				WHERE sp.user_id = ? AND s.id = ?
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, userId);
			stmt.setInt(2, songId);
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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	@Override
	public Song getSongByTitle(String title) throws SQLException {
		String sql = """
		        SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds, s.review
		        FROM songs_playlists sp
		        JOIN songs_playlist_items spi ON sp.id = spi.playlist_id
		        JOIN songs s ON spi.song_id = s.id
		        WHERE s.title = ? AND sp.user_id = ?
		    """;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, title);
		    stmt.setInt(2, userId);
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
		    } else {
		    	System.out.println("Song not found.");
		    }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	@Override
	public List<Song> getSongsByUser(int userId) throws SQLException {
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
	            ON spi.songs_id = s.id
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

	            // only keep this if your Song class has setReview()
	            song.setReview(rs.getString("review"));

	            songs.add(song);
	        }
	    }

	    return songs;
	}
	
	@Override
	public List<Song> getSongsByArtist(String artist, int userId) throws SQLException{
		List<Song> songs = new ArrayList<>();

		String sql = """
				SELECT s.id, s.title, s.status, s.user_rating, s.album, s.artist, s.year_released, s.runtime_seconds, s.review
				FROM songs_playlists sp
				INNER JOIN songs_playlist_items spi
				ON sp.id = spi.playlist_id
				INNER JOIN songs s
				ON spi.song_id = s.id
				WHERE sp.user_id = ? AND s.artist = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setString(2, artist);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            Song song = new Song(rs.getString("title"),
				                    Status.fromDbString(rs.getString("status")),
				                    rs.getDouble("user_rating"),
				                    rs.getString("album"),
				                    rs.getString("artist"),
				                    rs.getInt("year_released"),
				                    rs.getInt("runtime_seconds"),
				                    rs.getString("review"));

	            songs.add(song);
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return songs;
	}
	
	@Override
	public void deleteSong(int userId, String title, String artist) throws SQLException {

	    String sql = """
	        DELETE FROM songs_playlist_items
	        WHERE songs_id = (
	            SELECT id FROM songs
	            WHERE title = ? AND artist = ?
	        )
	        AND playlist_id = (
	            SELECT id FROM songs_playlists
	            WHERE user_id = ? AND title = 'all_songs'
	        )
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, title);
	        stmt.setString(2, artist);
	        stmt.setInt(3, userId);

	        int rowsDeleted = stmt.executeUpdate();

	        if (rowsDeleted > 0) {
	            System.out.println("  - " + title + " by " + artist + " was successfully removed!");
	        } else {
	            System.out.println("  - " + title + " by " + artist + " was not found in your songs.");
	        }
	    }
	}
	
	@Override
	public void updateSongRating(int userId, Song song, double rating) throws SQLException {
	    String sql = """
	        UPDATE songs_playlist_items
	        SET user_rating = ?
	        WHERE songs_id = (
	            SELECT id FROM songs
	            WHERE title = ? AND artist = ?
	        )
	        AND playlist_id = (
	            SELECT id FROM songs_playlists
	            WHERE user_id = ? AND title = 'all_songs'
	        )
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setDouble(1, rating);
	        stmt.setString(2, song.getTitle());
	        stmt.setString(3, song.getArtist());
	        stmt.setInt(4, userId);

	        stmt.executeUpdate();
	    }
	}
	
	@Override
	public void addReview(int userId, Song song, String review) throws SQLException {
	    String sql = """
	        UPDATE songs_playlist_items
	        SET review = ?
	        WHERE songs_id = (
	            SELECT id FROM songs
	            WHERE title = ? AND artist = ?
	        )
	        AND playlist_id = (
	            SELECT id FROM songs_playlists
	            WHERE user_id = ? AND title = 'all_songs'
	        )
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, review);
	        stmt.setString(2, song.getTitle());
	        stmt.setString(3, song.getArtist());
	        stmt.setInt(4, userId);

	        stmt.executeUpdate();
	    }
	}
	
	@Override
	public void updateStatus(int userId, Song song, Status newStatus) throws SQLException {
	    String sql = """
	        UPDATE songs_playlist_items
	        SET status = ?, user_rating = ?, review = ?
	        WHERE songs_id = (
	            SELECT id FROM songs
	            WHERE title = ? AND artist = ?
	        )
	        AND playlist_id = (
	            SELECT id FROM songs_playlists
	            WHERE user_id = ? AND title = 'all_songs'
	        )
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, newStatus.toDbString());
	        stmt.setDouble(2, song.getUserRating());
	        stmt.setString(3, song.getReview());
	        stmt.setString(4, song.getTitle());
	        stmt.setString(5, song.getArtist());
	        stmt.setInt(6, userId);

	        stmt.executeUpdate();
	    }
	}
}
