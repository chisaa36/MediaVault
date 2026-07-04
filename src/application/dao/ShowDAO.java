package application.dao;
import java.sql.SQLException;
import java.util.List;

import application.model.Show;

public interface ShowDAO {
	
	void addShow(Show show) throws SQLException;
	Show getShowById(int id) throws SQLException;
	Show getShowByTitle(String title) throws SQLException;
	List<Show> getShowsByUser(int userId) throws SQLException;
	void updateShowRating(String title, int rating) throws SQLException;
	void addReview(String title, String review) throws SQLException;
	void deleteShow(String title) throws SQLException;
}
