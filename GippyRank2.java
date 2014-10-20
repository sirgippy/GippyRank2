import java.io.*;
import java.text.DecimalFormat;

public class GippyRank2
{
	private static DecimalFormat df = new DecimalFormat("#.###");
	private static final int NUM_ITERATIONS = 5000;
	
	public static void main(String[] args)
	{
		TeamList fullList = new TeamList();
		TeamList fbsList = new TeamList();
		TeamList fcsList = new TeamList();
		
		Team thisTeam;
	
		File myFile = new File("1A.txt");
		File myFile2 = new File("conferences.txt");
		String line;
		String line2;
	
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(myFile));
			BufferedReader input2 = new BufferedReader(new FileReader(myFile2));
			
			while (( line = input.readLine()) != null && (line2 = input2.readLine()) != null)
			{
				thisTeam = fullList.addTeam(line,line2);
				fbsList.addTeam(thisTeam);
				fcsList.addTeam(thisTeam);
			}
			
			input.close();
			input2.close();
		}
		catch (IOException ex){
      	ex.printStackTrace();
    	}
		
		myFile = new File("1AA.txt");
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(myFile));
			
			while (( line = input.readLine()) != null)
			{
				thisTeam = fullList.addTeam(line);
				fcsList.addTeam(thisTeam);
			}
			
			input.close();
		}
		catch (IOException ex){
      	ex.printStackTrace();
    	}
		
		myFile = new File("scores.txt");
		try
		{
			BufferedReader	input	= new	BufferedReader(new FileReader(myFile));
			
			String teamName1;
			String teamName2;
			Team team1;
			Team team2;
			int score1 = 0;
			int score2 = 0;
			boolean neutralSite = false;
			
			while	((	line = input.readLine()) != null)
			{
				teamName1	= line.substring(10,37).trim();
				teamName2	= line.substring(41,68).trim();
				if (line.charAt(38) != ' ')
					score1 =	Integer.parseInt(line.substring(38,40));
				else
					score1 = Integer.parseInt(line.substring(39,40));
					
				if (line.charAt(69) != ' ')
					score2 =	Integer.parseInt(line.substring(69,71));
				else
					score2 = Integer.parseInt(line.substring(70,71));
				
				if (line.length() > 74)
					neutralSite = true;
				else
					neutralSite = false;
				
				if (fullList.doesTeamExist(teamName1) && fullList.doesTeamExist(teamName2))
				{
					team1 = fullList.getTeam(teamName1);
					team2 = fullList.getTeam(teamName2);
					
					if (!neutralSite)
					{
						team1.addGame(team2, score1, score2,'a');
						team2.addGame(team1, score2, score1,'h');
					}
					else
					{
						team1.addGame(team2, score1, score2,'n');
						team2.addGame(team1, score2, score1,'n');
					}
				}
				else if (fullList.doesTeamExist(teamName1))
				{
					team1 = fullList.getTeam(teamName1);
					
					team2 = fullList.addTeam(teamName2);
					
					if (!neutralSite)
					{
						team1.addGame(team2, score1, score2,'a');
						team2.addGame(team1, score2, score1,'h');
					}
					else
					{
						team1.addGame(team2, score1, score2,'n');
						team2.addGame(team1, score2, score1,'n');
					}
				}
				else if (fullList.doesTeamExist(teamName2))
				{
					team2 = fullList.getTeam(teamName2);
					
					team1 = fullList.addTeam(teamName1);
					
					if (!neutralSite)
					{
						team1.addGame(team2, score1, score2,'a');
						team2.addGame(team1, score2, score1,'h');
					}
					else
					{
						team1.addGame(team2, score1, score2,'n');
						team2.addGame(team1, score2, score1,'n');
					}
				}
				else
				{
					team1 = fullList.addTeam(teamName1);
					team2 = fullList.addTeam(teamName2);
					
					if (!neutralSite)
					{
						team1.addGame(team2, score1, score2,'a');
						team2.addGame(team1, score2, score1,'h');
					}
					else
					{
						team1.addGame(team2, score1, score2,'n');
						team2.addGame(team1, score2, score1,'n');
					}
				}
			}
			
			input.close();
		}
		catch (IOException ex){
      	ex.printStackTrace();
    	}

//		boolean done = false;
//		
//		double prevProb, prevHFA, thisProb, thisHFA = 1.0;
//		double HFA_adjustment = 0.25;
		
		for (int i=0; i < NUM_ITERATIONS; i++)
			fullList.nextIteration();
//		thisProb = fbsList.getListProb();
//				
//		do
//		{
//			prevProb = thisProb;
//			prevHFA = thisHFA;
//			
//			thisHFA = prevHFA + HFA_adjustment;
//			Team.setHFA(thisHFA);
//			
//			fullList.resetRatings();
//			for (int i=0; i < NUM_ITERATIONS; i++)
//				fullList.nextIteration();
//			thisProb = fbsList.getListProb();
//			
//			if (HFA_adjustment < 0.001)
//				done = true;
//			else if (thisProb < prevProb)
//			{
//				thisHFA -= HFA_adjustment*2;
//				Team.setHFA(thisHFA);
//				
//				fullList.resetRatings();
//				for (int i=0; i < NUM_ITERATIONS; i++)
//					fullList.nextIteration();
//				thisProb = fbsList.getListProb();
//				
//				HFA_adjustment = HFA_adjustment/2;
//			}
//		}
//		while (!done);
		
		fbsList.sort();
		fbsList.getSOS();
		fbsList.getSOSRankings();
		fbsList.showRankings(0);
		
		System.out.println("HFA = " + df.format(Team.getHFA()));
	}
}