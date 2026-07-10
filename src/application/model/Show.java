package application.model;

public class Show extends Media {
	
	private int numOfSeasons;
	private int numOfEpisodes;
	private int avgMinsPerEp;
	
	// constructor
	public Show(String title, String creator, int year, Status status, double userRating, String review, int numOfSeasons, int numOfEpisodes, int avgMinsPerEp) {
		super(title, creator, year, status, userRating, review);
		this.numOfSeasons = numOfSeasons;
		this.numOfEpisodes = numOfEpisodes;
		this.avgMinsPerEp = avgMinsPerEp;
	}
	
	// getters and setters
	public int getNumOfSeasons() {return numOfSeasons;}
	public int getNumOfEpisodes() {return numOfEpisodes;}
	public int getAvgMinsPerEp() {return avgMinsPerEp;}
	
	public void setNumOfSeasons(int numOfSeasons) {this.numOfSeasons=numOfSeasons;}
	public void setNumOfEpisodes(int numOfEpisodes) {this.numOfEpisodes=numOfEpisodes;}
	public void setAvgMinsPerEp(int avgMinsPerEp) {this.avgMinsPerEp=avgMinsPerEp;}
}