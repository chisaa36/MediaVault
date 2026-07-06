package application.dao.impl;

import java.sql.SQLException;
import java.util.List;

import application.dao.ShowDAO;
import application.model.Episode;
import application.model.Season;
import application.model.Show;
import application.model.Status;

public class ShowDAOImpl implements ShowDAO {
	
	private Connection conn;
	private int userId;

	public ShowDAOImpl(Connection conn, int userId) {
		this.conn = conn;
		this.userId = userId;
	}

	@Override
	public int addShow(Show show) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addSeason(Season season) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addEpisode(Episode episode) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Show getShowById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Show getShowByTitle(String title) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Show> getShowsByUser(int userId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Season> getSeasonsByShowId(int showId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Season> getSeasonsByShowTitle(String title) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Episode getEpisodeById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Episode getEpisodeByTitle(String title) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateShowStatus(String title, String status) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShowRating(String title, double rating) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEpisodeStatus(String title, String status) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEpisodeRating(String title, double rating) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addReview(String title, String review) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteShow(String title) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Show> getShowsByStatus(Status status) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Show> getShowsByGenre(String genre) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
