package application.model;

public class Song extends Media{
	
	private String album;
	private int yearReleased;
	private int runtimeSeconds;

    public Song(String title,
    			String artist,
    			int yearReleased,
    			Status status,
    			double userRating,
    			String review,
    			String album,    			
    			int runtimeSeconds)
    			 {
    	
        super(0,
              Type.SONG,
              title,
              artist,
              status,
              userRating,
              review,
              yearReleased,
              "");      		// info = album
    	
    	this.album = album;
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

	public void setSongId(int songId) {
		this.mediaId = songId;
		
	}
}
