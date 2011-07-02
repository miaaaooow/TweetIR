package tweetIR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JTextArea;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.BasicAuthorization;
import utils.Constants;
import utils.PathMaker;

/**
 * This class implements the Runable interface and hence is used as a thread.
 * Its purpose is to collect tweet searches from equal intervals of time into a file.
 * 
 * @author Maria Mateva, Sofia University, 2011
 */

public class TweetCollector extends TimerTask {
	
	public final static int TRENDING_SLEEP_TIME = 300;
	public final static int NORMAL_SLEEP_TIME = 1000;
	public final static int DEFAULT_SEARCH_TIME = 10000;
	public final static String TREND_TWEETS_FILE_NAME = "TREND_TWEETS_FOR_";
	
	private File output; 
	private int repetitions;
	private int waitTime;
	private BasicAuthorization userProfile;
	private String searchString;
	private JTextArea resultBox;

	
	/**
	 * @param searchString
	 * @param totalTime - total time for collecting tweets, in milliseconds 
	 * @param profile - just a dump profile to perform the search from
	 * @param resultBox - a component in which to show the current results for bigger vividness of the app
	 */
	public TweetCollector (String searchString, int totalTime, int waitTime,
						   BasicAuthorization profile, JTextArea resultBox) {
		this.output = null;
		String root = Constants.FILES_TO_INDEX_DIRECTORY;
		File toIndexRoot = new File(root);
		if (!toIndexRoot.exists()) {
			toIndexRoot.mkdir();
		}		
		String path = PathMaker.path(Constants.FILES_TO_INDEX_DIRECTORY, searchString);
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdir();
		}		
		try {
			this.output = new File(path + "/" + TREND_TWEETS_FILE_NAME + searchString + "_" + System.currentTimeMillis()); 
		}
		catch (Exception ioe) {
			ioe.printStackTrace();
		}
		this.waitTime = waitTime;
		this.repetitions = (int) totalTime/waitTime;
		this.searchString = searchString;
		this.userProfile = profile;
		this.resultBox = resultBox;
	}
	
	@Override
	public void run() {
		Twitter myTwitter = new TwitterFactory().getInstance(userProfile);
		System.out.println("Tweet Collector for " + searchString + " started...");
		try {
			FileWriter fileStream = new FileWriter(output);
			BufferedWriter out = new BufferedWriter(fileStream);
			for (int i = 0; i <= repetitions; i++){
				try {		
					Query query = new twitter4j.Query(searchString);
					QueryResult result = myTwitter.search(query);   
					List<Tweet> tweets = result.getTweets();
					Iterator<Tweet> it = tweets.iterator();
					if (resultBox != null) {
						String tweet="";
						String resBox="";
						while(it.hasNext()) {
							Tweet tw = it.next();
							tweet = "\n" + tw.getText();
							resBox += tweet;
							out.write(tweet);
						}	
						resultBox.setText(resBox);
						System.out.println("resBox" + resBox);
					}
					else {
						while(it.hasNext()) {
							Tweet tw = it.next();
							out.write("\n" + tw.getText());
						}	
					}
					try {
						Thread.sleep(this.waitTime);
					} 
					catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
				catch (TwitterException te) {
					System.err.println("Couldn't connect: " + te);
				}
			}
			try {
				fileStream.close();
			}
			catch (IOException io){
				io.printStackTrace();
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		this.cancel();
		System.out.println("Tweet Collector for " + searchString + " ended.");
	}

}
