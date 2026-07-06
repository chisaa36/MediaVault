package application.model;

public class Episode {

	private String title;
	private Status status;
	private double user_rating;
	private String review;
	
	// constructor
	public Episode(String title, Status status, double user_rating, String review) {
		this.title = title;
		this.status = status;
		this.user_rating = user_rating;
		this.review = review;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public Status getStatus() {return status;}
	public double getUser_rating() {return user_rating;}
	public String getReview() {return review;}
	
	public void setTitle(String title) {this.title = title;}
	public void setStatus(Status status) {this.status = status;}
	public void setUser_rating(double user_rating) {this.user_rating = user_rating;}
	public void setReview(String review) {this.review = review;}
}
