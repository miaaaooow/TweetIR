/**
 * @author Maria Mateva, Sofia Univerity, 2011
 * 
 */
package tweetIR;


import indexing.LuceneIndexer;

import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.BasicAuthorization;
import utils.Constants;


public class TwitterCommunicator {
	
	public final static int CORRESPONDING_TWEETS = 10;
	
	private final static String USER = "IGenunelyRock";
	private final static String PASS = "mariamatevarocks";
	
	/** 
	 * Searches a string in current strings.
	 * @param searchString
	 * @return a StringBuffer with all tweets.
	 */
	public static StringBuffer searchInTweeter (String searchString) {
		
		BasicAuthorization auth = new BasicAuthorization(USER, PASS);
		StringBuffer sb = new StringBuffer();
		Twitter myTwitter = new TwitterFactory().getInstance(auth);
		try {		
			Query query = new Query(searchString);
			QueryResult result = myTwitter.search(query);   
			List<Tweet> tweets = result.getTweets();
			Iterator<Tweet> it = tweets.iterator();
			while(it.hasNext()) {
				Tweet tw = it.next();
				sb.append(tw.getText() + "\n");
			}			
		}
		catch (TwitterException te) {
			System.out.println("Couldn't connect: " + te);
		};
		return sb;
	}
	
	
	/**
	 * Searches a string for a certain period of time
	 * @param searchString
	 * @param filename - output filename
	 * @param time in milliseconds
	 * @return 
	 */
	public static String [] searchInTweeterForTime (String searchString, int totalSearchTime, int waitTime, JTextArea resultBox) {
		BasicAuthorization profile = new BasicAuthorization(USER, PASS);
		TweetCollector tweetsCollector = new TweetCollector(searchString, totalSearchTime, waitTime, profile, resultBox);
		tweetsCollector.run();
		String [] searchStr = new String[2];
		searchStr[0] = searchString;
		String [][] results = getCorrespondingStrings(searchStr, totalSearchTime, waitTime);	
		return results[0];
	}
	
	
	/**
	 * The method extracts the current trending topics (top 10)
	 * @param location - a location code
	 * @return a list of the trending topics strings
	 * NOTE: this method is discouraged to be used quite often as 
	 */
	public static String [] getTrendingTopics (int location) {
		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		String result [] = new String [1];
		try {
			Trends currentTrends;
			String locationName;
			if (location == Constants.DEFAULT_LOCATION){
				currentTrends = twitter.getCurrentTrends();
				locationName = "World";
			}
			else {
				currentTrends = twitter.getLocationTrends(location);
				locationName = currentTrends.getLocation().getCountryName() + " " + currentTrends.getLocation().getName();
			}
			
			Trend [] trendsContent = currentTrends.getTrends();
			result = new String[trendsContent.length + 1];
			int i;
			for (i = 0; i < trendsContent.length; i++) {
				result[i] = trendsContent[i].getName();
				System.out.println(result[i]);
			}
			result[i] = locationName;
			System.out.println("LOCATION:" + locationName);
		}
		catch (TwitterException te) {
			te.printStackTrace();
		}
		return result;
	}
	
	public static String [][] getCorrespondingStrings(String [] trends, int totalSearchTime, int waitTime) {
		BasicAuthorization auth = new BasicAuthorization(USER, PASS);
		int len = trends.length;
		String [][] result = new String[len][CORRESPONDING_TWEETS];
		for (int i = 0; i < len - 1; i++) { // the last one is the location name
			TweetCollector tc = new TweetCollector(trends[i], totalSearchTime, waitTime, auth, null);
			tc.run();
			String [] topWords = LuceneIndexer.getTopWords(CORRESPONDING_TWEETS, trends[i]);
			for (int j = 0; j < topWords.length; j++) {
				result[i][j] = topWords[j];
				System.out.println(topWords[j]);				                       
			}
		}
		return result;
	}

	
}
