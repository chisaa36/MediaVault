package application.model;

public class Episode {

	private String title;
	private Status status;
	private double userRating;
	private String review;
	
	// constructor
	public Episode(String title, Status status, double user_rating, String review) {
		this.title = title;
		this.status = status;
		this.userRating = user_rating;
		this.review = review;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public Status getStatus() {return status;}
	public double getUserRating() {return userRating;}
	public String getReview() {return review;}
	
	public void setTitle(String title) {this.title = title;}
	public void setStatus(Status status) {this.status = status;}
	public void setUserRating(double user_rating) {this.userRating = user_rating;}
	public void setReview(String review) {this.review = review;}
}
