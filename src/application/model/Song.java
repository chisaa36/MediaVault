package application.model;

public class Song {
	
	private String title;
	private Status status;
	private double userRating;
	private String album;
	private String artist;
	private int yearReleased;
	private int runtimeSeconds;

    public Song(String title, Status status, double userRating, String album, String artist, int yearReleased, int runtimeSeconds) {
        this.title = title;
        this.status = status;
        this.userRating = userRating;
        this.album = album;
        this.artist = artist;
        this.yearReleased = yearReleased;
        this.runtimeSeconds = runtimeSeconds;
    }

    public String getTitle() {
        return title;
    }
    
    public Status getStatus() {
    	return status;
    }
    
    public double getUserRating() {
    	return userRating;
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
}
