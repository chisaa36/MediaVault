package application.model;

public class Song {
    private String title;
    private String artist;
    private String runtime;

    public Song(String title, String artist, String runtime) {
        this.title = title;
        this.artist = artist;
        this.runtime = runtime;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getRuntime() {
        return runtime;
    }
}
