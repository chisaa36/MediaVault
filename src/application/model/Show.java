package application.model;

public class Show {
	
	public String title;
	public Status status;
	public double userRating;
	public int numOfSeasons;
	public int numOfEpisodes;
	public int avgMinsPerEp;
	public int firstYearAired;
	public int lastYearAired;
	
	// constructor
	public Show(String title, double userRating, int numOfSeasons, int numOfEpisodes, int avgMinsPerEp,
			int firstYearAired, int lastYearAired) {
		super();
		this.title = title;
		this.userRating = userRating;
		this.numOfSeasons = numOfSeasons;
		this.numOfEpisodes = numOfEpisodes;
		this.avgMinsPerEp = avgMinsPerEp;
		this.firstYearAired = firstYearAired;
		this.lastYearAired = lastYearAired;
	}
	
	// getters and setters
	public String getTitle() {return title;}
	public Status getStatus() {return status;}
	public double getUserRating() {return userRating;}
	public int getNumOfSeasons() {return numOfSeasons;}
	public int getNumOfEpisodes() {return numOfEpisodes;}
	public int getAvgMinsPerEp() {return avgMinsPerEp;}
	public int getFirstYearAired() {return firstYearAired;}
	public int getLastYearAired() {return lastYearAired;}
	
	public void setTitle(String title) {this.title=title;}
	public void setTitle(Status status) {this.status=status;}
	public void setUserRating(double userRating) {this.userRating=userRating;}
	public void setNumOfSeasons(int numOfSeasons) {this.numOfSeasons=numOfSeasons;}
	public void setNumOfEpisodes(int numOfEpisodes) {this.numOfEpisodes=numOfEpisodes;}
	public void setAvgMinsPerEp(int avgMinsPerEp) {this.avgMinsPerEp=avgMinsPerEp;}
	public void setFirstYearAired(int firstYearAired) {this.firstYearAired=firstYearAired;}
	public void setLastYearAired(int lastYearAired) {this.lastYearAired=lastYearAired;}

}