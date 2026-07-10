package application.model;

public class Game extends Media{
	
	private int avgPlaytimeMins;
	
	public Game(String title, String creator, int year, Status status, double userRating, String review, int avgPlaytimeMins) {
		super(title, creator, year, status, userRating, review);
		this.avgPlaytimeMins = avgPlaytimeMins;
	}
	
	// getters and setters
	public int getAvgPlaytimeMins() {return avgPlaytimeMins;}
	
	public void setAvgPlaytimeMins(int avgPlaytimeMins) {this.avgPlaytimeMins = avgPlaytimeMins;}
}