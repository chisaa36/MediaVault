package application.model;

public class Game extends Media{
	
	private String developer;
	private int avgPlaytimeMins;
	
	public Game(String title, Status status, double userRating, String review, String developer, int avgPlaytimeMins) {
		super(title, status, userRating, review);
		this.developer = developer;
		this.avgPlaytimeMins = avgPlaytimeMins;
	}
	
	// getters and setters
	public String getDeveloper() {return developer;}
	public int getAvgPlaytimeMins() {return avgPlaytimeMins;}
	
	public void setDeveloper(String developer) {this.developer = developer;}
	public void setAvgPlaytimeMins(int avgPlaytimeMins) {this.avgPlaytimeMins = avgPlaytimeMins;}
}