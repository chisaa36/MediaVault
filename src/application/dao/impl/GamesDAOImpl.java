package application.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import application.dao.GameDAO;
import application.model.Game;

public class GamesDAOImpl implements GameDAO{
	
	private Connection conn;
	
	public GamesDAOImpl(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void addGame(Game game) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Game getGameById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Game getGameByTitle(String title) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Game> getGamesByUser(int userId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateGameRating(String title, int rating) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReview(String title, String review) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteGame(String title) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}