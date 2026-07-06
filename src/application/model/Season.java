package application.model;

import java.util.List;

public class Season {
	String title;
	Status status;
	List<Episode> episodes;
	
	// constructor
	public Season(String title, Status status, List<Episode> episodes) {
		this.title = title;
		this.status = status;
		this.episodes = episodes;
	}

	// getters and setters
	public String getTitle() {return title;}
	public Status getStatus() {return status;}
	public List<Episode> getEpisodes() {return episodes;}

	public void setTitle(String title) {this.title = title;}
	public void setStatus(Status status) {this.status = status;}
	public void setEpisodes(List<Episode> episodes) {this.episodes = episodes;}	
}
