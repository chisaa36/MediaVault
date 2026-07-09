package application.model;

public class Media {
	
	private String title;
	private Status status;
	private double userRating;
	private String review;
	
	// constructors
	public Media(String title, Status status, double userRating, String review) {
		this.title = title;
		this.status = status;
		this.userRating = userRating;
		this.review = review;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public Status getStatus() {return status;}
	public String getUserRatingString() { 	
	    if(status == Status.COMPLETED) {
	    	return String.valueOf(userRating);
	    }
	    	
	    return "complete to rate";
	}
	public String getReview() {
		if(status == Status.COMPLETED) {
			if(review.equals(""))
				return "no";
			else
				return "yes";
		}
	    	
	    return "complete to review";
	}
	
	public void setTitle(String title) {this.title = title;}
	public void setStatus(Status status) {this.status = status;}
	public void setUserRating(double userRating) {this.userRating = userRating;}
	public void setReview(String review) {this.review = review;}
}
