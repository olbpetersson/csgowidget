package se.olapetersson.csgomatches;


public class MyListItem{
	
	private String labelTeams, startTime, tournament, url;

	public String getLabelTeams() {
		return labelTeams;
	}

	public void setLabelTeams(String labelTeams) {
		this.labelTeams = labelTeams;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getTournament() {
		return tournament;
	}

	public void setTournament(String tournament) {
		this.tournament = tournament;
	}
	
	public String getURL() {
		return url;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	public MyListItem() {
	
	}
	public MyListItem(String title) {
		this.labelTeams = title;
	}
	
	public MyListItem(String title, String tournament, String startTime) {
		this.labelTeams = title;
		this.tournament = tournament;
		this.startTime = startTime;
	}
}
