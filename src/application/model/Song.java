package application.model;

import java.util.ArrayList;
import java.util.List;

public class Song {
	
	public String title;
	public List<String> genres = new ArrayList<>();
	public double userRating;
	public String album;
	public String artist;
	public int yearReleased;
	public int runtimeMins;
}
