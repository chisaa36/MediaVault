package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Game;

public interface GameDAO {
	
	void addGame(Game game) throws SQLException;
	Game getGameById(int id) throws SQLException;
	Game getGameByTitle(String title) throws SQLException;
	List<Game> getGamesByUser(int userId) throws SQLException;
	void updateGameRating(String title, int rating) throws SQLException;
	void addReview(String title, String review) throws SQLException;
	void deleteGame(String title) throws SQLException;
}
