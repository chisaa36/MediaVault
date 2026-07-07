package application.dao;

import java.sql.SQLException;
import java.util.List;

import application.model.Episode;
import application.model.Season;
import application.model.Show;
import application.model.Status;

public interface ShowDAO {
	
	int addShow(Show show) throws SQLException;
	int addSeason(Season season) throws SQLException;
	int addEpisode(Episode episode) throws SQLException;
	Show getShowById(int id) throws SQLException;
	Show getShowByTitle(String title) throws SQLException;
	List<Show> getShowsByUser(int userId) throws SQLException;
	List<Season> getSeasonsByShowId(int showId) throws SQLException;
	List<Season> getSeasonsByShowTitle(String title) throws SQLException;
	Episode getEpisodeById(int id) throws SQLException;
	Episode getEpisodeByTitle(String title) throws SQLException;
	List<Episode> getEpisodesBySeason(int showId, int seasonId) throws SQLException;
	void updateShowStatus(String title, String status) throws SQLException;
	void updateShowRating(String title, double rating) throws SQLException;
	void updateEpisodeStatus(String title, String status) throws SQLException;
	void updateEpisodeRating(String title, double rating) throws SQLException;
	void updateReview(String title, String review) throws SQLException;
	void deleteShow(String title) throws SQLException;
	List<Show> getShowsByStatus(Status status) throws SQLException;
	List<Show> getShowsByGenre(String genre) throws SQLException;
}
