package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Game;
import application.model.Status;

public interface GameDAO {
	
	int addGame(Game game) throws SQLException;
	Game getGameById(int id) throws SQLException;
	Game getGameByTitle(String title) throws SQLException;
	List<Game> getGamesByUser(int userId) throws SQLException;
	int addGenre(String genre) throws SQLException;
	void linkGameGenre(int gameId, int genreId) throws SQLException;
	void updateStatus(String title, Status status) throws SQLException;
	void updateGameRating(String title, double rating) throws SQLException;
	void updateReview(String title, String review) throws SQLException;
	void deleteGame(String title) throws SQLException;
	int getGameId(String title) throws SQLException;
}
