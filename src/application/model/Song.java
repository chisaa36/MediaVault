package application.model;

public class Song extends Media{
	
	private String album;
	private int yearReleased;
	private int runtimeSeconds;

    public Song(String title,
    			Status status,
    			double userRating,
    			String album,
    			String artist,
    			int yearReleased,
    			int runtimeSeconds,
    			String review) {
    	
        super(0,
              Type.SONG,
              title,
              artist,
              "",
              status,
              userRating,
              review,
              "");      		// info = album
    	
    	this.album = album;
        this.yearReleased = yearReleased;
        this.runtimeSeconds = runtimeSeconds;
    }
    
    // getters
    public String getAlbum() {return album;}
    public int getYearReleased() {return yearReleased;}
    public int getRuntimeSeconds() {return runtimeSeconds;}
    public String getRuntimeString() {
    	int minutes = runtimeSeconds/60;
        int seconds = runtimeSeconds%60;

        return String.format("%d:%02d", minutes, seconds);
    }
    public String setInfo() {
    	
    	return "from album\"" + album + "\" of duration " + this.getRuntimeString();
    }
    public String setYearString() {
    	
    	return String.valueOf(yearReleased);
    }
}
