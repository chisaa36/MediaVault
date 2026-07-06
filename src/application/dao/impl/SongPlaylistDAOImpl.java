package application.dao.impl;

import java.sql.SQLException;
import java.util.List;

import application.dao.SongPlaylistDAO;
import application.model.Song;
import application.model.SongPlaylist;

public class SongPlaylistDAOImpl implements SongPlaylistDAO {

	@Override
	public int createPlaylist(String name, int userId) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addSongToPlaylist(int playlistId, int songId) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSongsToPlaylist(int playlistId, List<Song> songs) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SongPlaylist> getPlaylistsByUser(int userId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePlaylist(int playlistId) throws SQLException {
		// TODO Auto-generated method stub

	}

}
