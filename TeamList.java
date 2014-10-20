import java.text.DecimalFormat;

public class TeamList
{
	private Team myTeams[] = new Team[10];
	public int numTeams;
	private static DecimalFormat df = new DecimalFormat("#.###");
	
	public TeamList()
	{
		numTeams = 0;
	}
	
	public Team addTeam(String teamName)
	{
		Team newTeam = new Team(teamName);
		
		if (myTeams.length == numTeams)
			addSpace();
		
		myTeams[numTeams] = newTeam;
		numTeams++;
		
		return newTeam;
	}
	
	public Team addTeam(String teamName, String conferenceName)
	{
		Team newTeam = new Team(teamName,conferenceName);
		
		if (myTeams.length == numTeams)
			addSpace();
		
		myTeams[numTeams] = newTeam;
		numTeams++;
		
		return newTeam;
	}
	
	public void addTeam(Team team)
	{
		if (myTeams.length == numTeams)
			addSpace();
		
		myTeams[numTeams] = team;
		numTeams++;
	}
	
	public boolean doesTeamExist(String teamName)
	{
		for (int i=0; i < numTeams; i++)
			if (teamName.equals(myTeams[i].getName()))
				return true;
		
		return false;
	}
	
	public Team getTeam(String teamName)
	{
		for (int i=0; i < numTeams; i++)
			if (teamName.equals(myTeams[i].getName()))
				return myTeams[i];
		
		return null;
	}
	
	private void addSpace()
	{
		Team temp[] = new Team[numTeams*2];
		
		for (int i=0; i < numTeams; i++)
			temp[i] = myTeams[i];
			
		myTeams = temp;
	}
	
	public void nextIteration()
	{
		for (int i=0; i < numTeams; i++)
			myTeams[i].updateNewRanking();
		
		for (int i=0; i < numTeams; i++)
			myTeams[i].updatePrevRanking();
			
		sort();
	}
	
	public void sort()
	{
		Team temp;
		
		for (int i=0; i < numTeams; i++)
			for (int j=i+1; j < numTeams; j++)
			{
				if (myTeams[j].getRanking() > myTeams[i].getRanking())
				{
					temp = myTeams[i];
					myTeams[i] = myTeams[j];
					myTeams[j] = temp;
				}
			}
	}
	
	public void showRankings(int spots)
	{
		if (spots == 0)
			spots = numTeams;
		
		double thisRating;
		
		String rank;
		String team;
		String rating;
		String pLose;
		String SOSRank;
		String record;
		
		System.out.println("Rnk Team                Rating   Rec   pLoss  SOS");
		System.out.println("=================================================");
		
		for (int i=0; i < spots; i++)
		{
			if (i == 25)
				System.out.println();
			
			rank = Integer.toString(i+1);
			if (rank.length() < 2)
				rank = "  " + rank;
			else if (rank.length() < 3)
				rank = " " + rank;
			
			team = myTeams[i].getName();
			while(team.length() < 19)
				team = team + " ";
			
			thisRating = Math.log(myTeams[i].getRanking())/Math.log(2);
			rating = df.format(thisRating);
			if (rating.length() < 3)
				rating = rating + ".0";
			if (rating.length() < 4)
				rating = rating + "0";
			if (rating.length() < 5)
				rating = rating + "0";
			if (rating.length() < 6)
				if (thisRating >= 10)
					rating = rating + "0";
				else
					rating = " " + rating;
			
			record = Integer.toString(myTeams[i].getWins()) + "-" 
				+ Integer.toString(myTeams[i].getLosses());
			if (record.length() < 4)
				record = " " + record;
			
			pLose = df.format(0 - Math.log10(myTeams[i].getSOS()));
			if (pLose.length() < 3)
				pLose = pLose + ".0";
			if (pLose.length() < 4)
				pLose = pLose + "0";
			if (pLose.length() < 5)
				pLose = pLose + "0";
			if (pLose.length() < 6)
				if (0 - Math.log10(myTeams[i].getSOS()) > 10)
					pLose = pLose + "0";
				else
					pLose = " " + pLose;
			
			SOSRank = Integer.toString(myTeams[i].getSOSRank());
			if (SOSRank.length() < 2)
				SOSRank = " " + SOSRank;
			if (SOSRank.length() < 3)
				SOSRank = " " + SOSRank;
			
			System.out.println(rank + " " + team + " " + rating + "  " + record + "  " + pLose + "  " + SOSRank);
		}
	}
	
	public void resetRatings()
	{
		for (int i=0; i < numTeams; i++)
			myTeams[i].resetRating();
	}
	
	public double getListProb()
	{
		double listProb = 0.0;
		
		for (int i=0; i < numTeams; i++)
			listProb = listProb + Math.log(myTeams[i].getProb(this));
		
		return listProb;
	}
	
	public void getSOS()
	{
		double ranking = myTeams[24].getRanking();
		for (int i=0; i < numTeams; i++)
			myTeams[i].calculateSOS(ranking);
	}
	
	public void getSOSRankings()
	{
		int ranks[] = new int[numTeams];
		int teamToSwap;
		
		for (int i=0; i < numTeams; i++)
			ranks[i] = i;
		
		for (int i=0; i < numTeams; i++)
			for (int j=i+1; j < numTeams; j++)
			{
				if (myTeams[ranks[j]].getSOS() < myTeams[ranks[i]].getSOS())
				{
					teamToSwap = ranks[i];
					ranks[i] = ranks[j];
					ranks[j] = teamToSwap;
				}
			}
		
		for (int i=0; i < numTeams; i++)
			myTeams[ranks[i]].setSOSRank(i+1);
	}
}