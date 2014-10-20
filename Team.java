public class Team
{
	private String myTeamName;
	private String myConference;
	private int gamesPlayed;
	private Team opponents[] = new Team[220];
	private int myScore[] = new int[220];
	private int oppScore[] = new int[220];
	private char location[] = new char[220];
	private double prevRanking;
	private double newRanking;
	private double probWin;
	private int mySOSRank;
	
	private double GRANULARITY = 2.0;
	
	private static double HFA = 1.35;
	private static final double QWF = 0.1;
	private static final double SOSF = 0.2;
	private static final int UM = 24;
	private static final int LM = 7;
	
	private static final int NUM_GOD_GAMES = 0;
	
	private static final double highRating = Math.pow(2, 15);
	private static final double initialRating = 1.0;
	private static final double lowRating = Math.pow(2, -15);
	
	private static Team highTeam = new Team(highRating);
	private static Team lowTeam = new Team(lowRating);
	
	public Team(double rating)
	{
		prevRanking = rating;
	}
	
	public Team(String teamName)
	{
		myTeamName = teamName;
		myConference = "";
		gamesPlayed = 0;
		prevRanking = initialRating;
		for (int i=0; i<NUM_GOD_GAMES; i++)
		{	
			addGame(lowTeam,UM,0,'n');
			addGame(highTeam,0,UM,'n');
		}
	}
	
	public Team(String teamName, String conferenceName)
	{
		myTeamName = teamName;
		myConference = conferenceName;
		gamesPlayed = 0;
		prevRanking = initialRating;
		for (int i=0; i<NUM_GOD_GAMES; i++)
		{	
			addGame(lowTeam,UM,0,'n');
			addGame(highTeam,0,UM,'n');
		}
	}
	
	public void addGame(Team opp, int hScore, int oScore, char thisLocation)
	{
		opponents[gamesPlayed] = opp;
		myScore[gamesPlayed] = hScore;
		oppScore[gamesPlayed] = oScore;
		location[gamesPlayed] = thisLocation;
		gamesPlayed++;
	}
	
	public void updateNewRanking()
	{
		double downprob = 1.0, prob = 1.0, upprob = 1.0;
		double upranking = prevRanking*GRANULARITY;
		double downranking = prevRanking/GRANULARITY;
		double thisQWF, oppRanking;
		double thisUpRanking, thisDownRanking, thisRanking;
		int mov;
		
		for(int i=0; i < gamesPlayed; i++)
		{
			thisRanking = prevRanking;
			thisUpRanking = upranking;
			thisDownRanking = downranking;
			oppRanking = opponents[i].getRanking();
			
			if (location[i] == 'h') {
				thisRanking = thisRanking*HFA;
				thisUpRanking = thisUpRanking*HFA;
				thisDownRanking = thisDownRanking*HFA;
			}
			else if (location[i] == 'a') {
				oppRanking = oppRanking*HFA;
			}
			
			mov = myScore[i] - oppScore[i];
			thisQWF = getQWF(mov);
			
			if (mov > 0) {
				upprob = upprob * ( Math.pow(thisUpRanking / (thisUpRanking + oppRanking),(1-thisQWF-SOSF))
						* Math.pow(oppRanking / (thisUpRanking + oppRanking), thisQWF+SOSF) );
				prob = prob * ( Math.pow(thisRanking / (thisRanking + oppRanking),(1-thisQWF-SOSF))
						* Math.pow(oppRanking / (thisRanking + oppRanking), thisQWF+SOSF) );
				downprob = downprob * ( Math.pow(thisDownRanking / (thisDownRanking + oppRanking),(1-thisQWF-SOSF))
						* Math.pow(oppRanking / (thisDownRanking + oppRanking), thisQWF+SOSF) );
			}
			else {
				upprob = upprob * ( Math.pow(oppRanking / (thisUpRanking + oppRanking),(1-thisQWF-SOSF))
						* Math.pow(thisUpRanking / (thisUpRanking + oppRanking), thisQWF+SOSF) );
				prob = prob * ( Math.pow(oppRanking / (thisRanking + oppRanking),(1-thisQWF-SOSF))
						* Math.pow(thisRanking / (thisRanking + oppRanking), thisQWF+SOSF) );
				downprob = downprob * ( Math.pow(oppRanking / (thisDownRanking + oppRanking),(1-thisQWF-SOSF))
						* Math.pow(thisDownRanking / (thisDownRanking + oppRanking), thisQWF+SOSF) );
			}
		}
		
		if (upprob > prob)
		{
			newRanking = upranking;
		}
		else if (downprob > prob)
		{
			newRanking = downranking;
		}
		else
		{
			newRanking = prevRanking;
			GRANULARITY = Math.sqrt(GRANULARITY);
		}
	}
	
	public void updatePrevRanking()
	{
		prevRanking = newRanking;
	}
	
	public double getRanking()
	{
		return prevRanking;
	}
	
	public String getName()
	{
		return myTeamName;
	}
	
	public String getConference()
	{
		return myConference;
	}
	
	private double getQWF(int mov) {
		double thisQWF;
		
		if (mov >= UM)
			thisQWF = 0;
		else if (mov >= LM)
			thisQWF = QWF * (UM - mov) / (UM - LM);
		else if (mov >= -LM)
			thisQWF = QWF;
		else if (mov >= -UM)
			thisQWF = QWF * (UM + mov) / (UM - LM);
		else
			thisQWF = 0;
		
		return thisQWF;
	}
	
	public double getProb(TeamList list)
	{
		double prob = 1.0;
		double thisQWF;
		
		for(int i=2; i < gamesPlayed; i++)
		{
			if (myConference.equals(opponents[i].getConference()))
			{
				if (location[i] == 'h')
				{
					if (list.getTeam(opponents[i].getName()) == null)
						continue;
					
					if (myScore[i] > oppScore[i] + LM)
					{
						if (myScore[i] >= oppScore[i] + UM)
							thisQWF = 0;
						else
							thisQWF = QWF * (UM - (myScore[i] - oppScore[i])) / (UM - LM);
						prob = prob * ( (1-thisQWF-SOSF) * HFA*prevRanking / (HFA*prevRanking + opponents[i].getRanking())
								+ (thisQWF+SOSF) * opponents[i].getRanking() / (HFA*prevRanking + opponents[i].getRanking()));
					}
					else if (myScore[i] > oppScore[i])
						prob = prob * ( (1-QWF-SOSF) * HFA*prevRanking / (HFA*prevRanking + opponents[i].getRanking())
								+ (QWF+SOSF) * opponents[i].getRanking() / (HFA*prevRanking + opponents[i].getRanking()));
					else if (myScore[i] + LM >= oppScore[i])
						prob = prob * ( (QWF+SOSF) * HFA*prevRanking / (HFA*prevRanking + opponents[i].getRanking())
								+ (1-QWF-SOSF) * opponents[i].getRanking() / (HFA*prevRanking + opponents[i].getRanking()));
					else
					{
						if (oppScore[i] >= myScore[i] + UM)
							thisQWF = 0;
						else
							thisQWF = QWF * (UM - (oppScore[i] - myScore[i])) / (UM - LM);
						prob = prob * ( (thisQWF+SOSF) * HFA*prevRanking / (HFA*prevRanking + opponents[i].getRanking())
								+ (1-thisQWF-SOSF) * opponents[i].getRanking() / (HFA*prevRanking + opponents[i].getRanking()));
					}
				}
				else if (location[i] == 'a')
				{
					if (myScore[i] > oppScore[i] + LM)
					{
						if (myScore[i] >= oppScore[i] + UM)
							thisQWF = 0;
						else
							thisQWF = QWF * (UM - (myScore[i] - oppScore[i])) / (UM - LM);
						prob = prob * ( (1-thisQWF-SOSF) * prevRanking / (prevRanking + HFA*opponents[i].getRanking())
								+ (thisQWF+SOSF) * HFA*opponents[i].getRanking() / (prevRanking + HFA*opponents[i].getRanking()));
					}
					else if (myScore[i] > oppScore[i])
						prob = prob * ( (1-QWF-SOSF) * prevRanking / (prevRanking + HFA*opponents[i].getRanking())
								+ (QWF+SOSF) * HFA*opponents[i].getRanking() / (prevRanking + HFA*opponents[i].getRanking()));
					else if (myScore[i] + LM >= oppScore[i])
						prob = prob * ( (QWF+SOSF) * prevRanking / (prevRanking + HFA*opponents[i].getRanking())
								+ (1-QWF-SOSF) * HFA*opponents[i].getRanking() / (prevRanking + HFA*opponents[i].getRanking()));
					else
					{
						if (oppScore[i] >= myScore[i] + UM)
							thisQWF = 0;
						else
							thisQWF = QWF * (UM - (oppScore[i] - myScore[i])) / (UM - LM);
						prob = prob * ( (thisQWF+SOSF) * prevRanking / (prevRanking + opponents[i].getRanking())
								+ (1-thisQWF-SOSF) * HFA*opponents[i].getRanking() / (prevRanking + HFA*opponents[i].getRanking()));
					}
				}
				else
				{
					if (myScore[i] > oppScore[i] + LM)
					{
						if (myScore[i] >= oppScore[i] + UM)
							thisQWF = 0;
						else
							thisQWF = QWF * (UM - (myScore[i] - oppScore[i])) / (UM - LM);
						prob = prob * ( (1-thisQWF-SOSF) * prevRanking / (prevRanking + opponents[i].getRanking())
								+ (thisQWF+SOSF) * opponents[i].getRanking() / (prevRanking + opponents[i].getRanking()));
					}
					else if (myScore[i] > oppScore[i])
						prob = prob * ( (1-QWF-SOSF) * prevRanking / (prevRanking + opponents[i].getRanking())
								+ (QWF+SOSF) * opponents[i].getRanking() / (prevRanking + opponents[i].getRanking()));
					else if (myScore[i] + LM >= oppScore[i])
						prob = prob * ( (QWF+SOSF) * prevRanking / (prevRanking + opponents[i].getRanking())
								+ (1-QWF-SOSF) * opponents[i].getRanking() / (prevRanking + opponents[i].getRanking()));
					else
					{
						if (oppScore[i] >= myScore[i] + UM)
							thisQWF = 0;
						else
							thisQWF = QWF * (UM - (oppScore[i] - myScore[i])) / (UM - LM);
						prob = prob * ( (thisQWF+SOSF) * prevRanking / (prevRanking + opponents[i].getRanking())
								+ (1-thisQWF-SOSF) * opponents[i].getRanking() / (prevRanking + opponents[i].getRanking()));
					}
				}
			}
		}
		
		return Math.sqrt(prob);
	}
	
	public void resetRating()
	{
		prevRanking = initialRating;
		GRANULARITY = 10.0;
	}
	
	public void calculateSOS(double ranking)
	{
		probWin = 1.0;
		
		for(int i=0; i < gamesPlayed; i++)
		{
			if (location[i] == 'h')
				probWin = probWin * HFA*ranking / (HFA*ranking + opponents[i].getRanking());
			else if (location[i] == 'a')
				probWin = probWin * ranking / (ranking + HFA*opponents[i].getRanking());
			else
				probWin = probWin * ranking / (ranking + opponents[i].getRanking());
		}
	}
	
	public double getSOS()
	{
		return probWin;
	}
	
	public void setSOSRank(int rank)
	{
		mySOSRank = rank;
	}
	
	public int getSOSRank()
	{
		return mySOSRank;
	}
	
	public int getWins()
	{
		int wins = 0;
		
		for (int i = 0; i < gamesPlayed; i++)
			if (myScore[i] > oppScore[i])
				wins++;
		
		return wins;
	}
	
	public int getLosses()
	{
		int losses = 0;
		
		for (int i = 0; i < gamesPlayed; i++)
			if (myScore[i] < oppScore[i])
				losses++;
		
		return losses;
	}
	
	public static double getHFA()
	{
		return HFA;
	}
	
	public static void setHFA(double newHFA)
	{
		HFA = newHFA;
	}
}