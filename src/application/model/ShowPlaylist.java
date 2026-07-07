package application.model;

import java.util.List;
import java.util.ArrayList;

public class ShowPlaylist {
	
	public String title;
	public List<Show> shows = new ArrayList<>();
	
	public ShowPlaylist(String title, List<Show> shows) {
		this.title = title;
		this.shows = shows;
	}
}