package application.model;

public class Song extends Media{
	
	private int songId;
	private String album;
	private String artist;
	private int yearReleased;
	private int runtimeSeconds;

    public Song(String title, Status status, double userRating, String album, String artist, int yearReleased, int runtimeSeconds, String review) {
        super(title, status, userRating, review);
        this.album = album;
        this.artist = artist;
        this.yearReleased = yearReleased;
        this.runtimeSeconds = runtimeSeconds;
    }
    
    // getters
    public int getSongId() {return songId;}
    public String getAlbum() {return album;}
    public String getArtist() {return artist;}
    public int getYearReleased() {return yearReleased;}
    public int getRuntimeSeconds() {return runtimeSeconds;}
    public String getRuntimeString() {
    	int minutes = runtimeSeconds/60;
        int seconds = runtimeSeconds%60;

        return String.format("%d:%02d", minutes, seconds);
    }
    
    public void setSongId(int songId) {this.songId = songId;}
}
