package application.model;

public class Show extends Media {
	
	private int numOfSeasons;
	private int numOfEpisodes;
	private int avgMinsPerEp;
	private int firstYearAired;
	private int lastYearAired;
	
	// constructor
	public Show(String title, Status status, double userRating, String review, int numOfSeasons, int numOfEpisodes, int avgMinsPerEp, int firstYearAired, int lastYearAired) {
		super(title, status, userRating, review);
		this.numOfSeasons = numOfSeasons;
		this.numOfEpisodes = numOfEpisodes;
		this.avgMinsPerEp = avgMinsPerEp;
		this.firstYearAired = firstYearAired;
		this.lastYearAired = lastYearAired;
	}
	
	// getters and setters
	public int getNumOfSeasons() {return numOfSeasons;}
	public int getNumOfEpisodes() {return numOfEpisodes;}
	public int getAvgMinsPerEp() {return avgMinsPerEp;}
	public int getFirstYearAired() {return firstYearAired;}
	public int getLastYearAired() {return lastYearAired;}
	
	public void setNumOfSeasons(int numOfSeasons) {this.numOfSeasons=numOfSeasons;}
	public void setNumOfEpisodes(int numOfEpisodes) {this.numOfEpisodes=numOfEpisodes;}
	public void setAvgMinsPerEp(int avgMinsPerEp) {this.avgMinsPerEp=avgMinsPerEp;}
	public void setFirstYearAired(int firstYearAired) {this.firstYearAired=firstYearAired;}
	public void setLastYearAired(int lastYearAired) {this.lastYearAired=lastYearAired;}

}