package application.model;

public class Game {
	
	private String title;
	private Status status;
	private double userRating;
	private String review;
	private String developer;
	private int avgPlaytimeMins;
	
	public Game(String title, Status status, double userRating, String review, String developer, int avgPlaytimeMins) {
		this.title = title;
		this.status = status;
		this.review = review;
		this.userRating = userRating;
		this.developer = developer;
		this.avgPlaytimeMins = avgPlaytimeMins;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public Status getStatus() {return status;}
	public double getUserRating() {return userRating;}
	public String getReview() {return review;}
	public String getDeveloper() {return developer;}
	public int getAvgPlaytimeMins() {return avgPlaytimeMins;}
	
	public void setTitle(String title) {this.title = title;}
	public void setStatus(Status status) {this.status = status;}
	public void setUserRating(double userRating) {this.userRating = userRating;}
	public void setReview(String review) {this.review = review;}
	public void setDeveloper(String developer) {this.developer = developer;}
	public void setAvgPlaytimeMins(int avgPlaytimeMins) {this.avgPlaytimeMins = avgPlaytimeMins;}
}