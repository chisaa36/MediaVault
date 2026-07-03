package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Show;

public interface ShowDAO {
	
	void addGame(Show show) throws SQLException;
	Show getShowByTitle(String title) throws SQLException;
	Show getShowById(int id) throws SQLException;
	List<Show> getGamesByUser(int userId) throws SQLException;
	void deleteShow(String title) throws SQLException;
	void updateShowRating(String title, int rating) throws SQLException;
	void addReview(String title, String review) throws SQLException;
}
