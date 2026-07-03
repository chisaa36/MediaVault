package application.model;

public class Game {
	
	public String title;
	public String status;
	public double userRating;
	public String developer;
	public int avgPlaytimeMins;
	
	// getters and setters
	public String getTitle() {return title;}
	public String getStatus() {return status;}
	public double getUserRating() {return userRating;}
	public String getDeveloper() {return developer;}
	public int getAvgPlaytimeMins() {return avgPlaytimeMins;}
	
	public void setTitle(String title) {this.title = title;}
	public void setStatus(String status) {this.status = status;}
	public void setUserRating(double userRating) {this.userRating = userRating;}
	public void setDeveloper(String developer) {this.developer = developer;}
	public void setAvgPlaytimeMins(int avgPlaytimeMins) {this.avgPlaytimeMins = avgPlaytimeMins;}
}
