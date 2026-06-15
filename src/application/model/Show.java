package application.model;

import java.util.List;
import java.util.ArrayList;

public class Show {
	
	public String title;
	public List<String> genres = new ArrayList<>();
	public double userRating;
	public int numOfSeasons;
	public int numOfEpisodes;
	public int avgMinsPerEp;
	public int firstYearAired;
	public int lastYearAired;
}
