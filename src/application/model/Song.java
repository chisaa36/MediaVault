package application.model;

public class Song {
	
	private int songId;
	private String title;
	private Status status;
	private double userRating;
	private String album;
	private String artist;
	private int yearReleased;
	private int runtimeSeconds;
	private String review;

    public Song(String title, Status status, double userRating, String album, String artist, int yearReleased, int runtimeSeconds, String review) {
        this.title = title;
        this.status = status;
        this.userRating = userRating;
        this.album = album;
        this.artist = artist;
        this.yearReleased = yearReleased;
        this.runtimeSeconds = runtimeSeconds;
        this.review = review;
    }
    
    public int getSongId() {
    	return songId;
    }
    
    public void setSongId(int songId) {
    	this.songId = songId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Status getStatus() {
    	return status;
    }
    
    public void setStatus(Status status) {
    	this.status = status;
    }
    
    public double getUserRating() {
    	return userRating;
    }
    
    public String getUserRatingString() {
    	
    	if(status == Status.COMPLETED)
    	{
    		return String.valueOf(userRating);
    	}
    	
    	return "/-complete to rate-/";
    }
    
    public void setUserRating(double userRating) {
    	this.userRating = userRating;
    }
    
    public String getAlbum() {
    	return album;
    }

    public String getArtist() {
        return artist;
    }
    
    public int getYearReleased() {
    	return yearReleased;
    }

    public int getRuntimeSeconds() {
        return runtimeSeconds;
    }
    
    public String getRuntimeString() {
        int minutes = runtimeSeconds/60;
        int seconds = runtimeSeconds%60;

        return String.format("%d:%02d", minutes, seconds);
    }
    
    public String getReview() {
    	return review;
    }
    
    public void setReview(String review) {
    	this.review = review;
    }
    
    public String getReviewedStatus(String review) {
    	
    	if(status == Status.COMPLETED)
    	{
	    	if(review.equals(""))
	    		return "no";
	    	else
	    		return "yes";
    	}
    	
    	return "/-complete to review-/";
    }
}
