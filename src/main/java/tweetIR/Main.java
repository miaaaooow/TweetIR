/**
 * @author Maria Mateva, Sofia Univerity, 2011
 * 
 */
package tweetIR;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utils.Constants;

public class Main extends JFrame {
	
	/** CONSTANTS **/
	private static final long serialVersionUID = 1L;
	
	private final static int BOARDX 	= 1000;
	private final static int BOARDY 	= 400;
	private final static int BUTTONY 	= 60;	
	
	private final static String SEARCH  = "SEARCH";
	private final static String SEARCH_LONG = "SEARCH_LONG";
	private final static String SEARCH_TRENDING = "SEARCH_TRENDING";
	private final static String CLEAR   = "CLEAR";


	/** private fields **/
	private JTextField searchBox;
	private JTextField totalTimeBox;
	private JTextField waitBox;
	private JTextField locationBox;
	private JTextArea resultBox;
	
	private boolean startMode = true;
	
	
	public Main() {
		super("TweetIR - retrieve Tweeter's info :)");
		setLocation(150, 100);

	    addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	          System.exit(0);
	        }
	      });
		setSize(BOARDX, BOARDY + BUTTONY);	
		setVisible(true);
		setFocusable(true);
		requestFocus(); 
	}
	
	/**
	 * Search once for a string, real time results
	 */
	public void search() {
		String text = getStringFromBox(searchBox, Constants.DEFAULT_SEARCH_STRING);
		resultBox.setText(TwitterCommunicator.searchInTweeter(text).toString());
	}
	
	/**
	 * Search for a string for a long time and collect info into a file
	 */	
	public void searchForLongTime() {
		String text = getStringFromBox(searchBox, Constants.DEFAULT_SEARCH_STRING);
		int totalSearchTime = getIntFromBox(totalTimeBox, Constants.DEFAULT_SEARCH_TIME);
		int waitTime = getIntFromBox(waitBox, Constants.NORMAL_SLEEP_TIME);
		String [] result = 
			TwitterCommunicator.searchInTweeterForTime(text, totalSearchTime, waitTime, resultBox);
		screenCustomSearch(text, result);
	}
	
	/**
	 * Search for the trending topics
	 */
	public void searchTrendingTopics() {
		int location  = getIntFromBox(locationBox, Constants.DEFAULT_LOCATION);
		int totalTime = getIntFromBox(totalTimeBox, Constants.DEFAULT_SEARCH_TIME);
		int waitTime  = getIntFromBox(waitBox, Constants.TRENDING_SLEEP_TIME);
		String[]   trends        = TwitterCommunicator.getTrendingTopics(location);
		String[][] corresponding = TwitterCommunicator.getCorrespondingStrings(trends, totalTime, waitTime);
		screenLocatedTrends(trends, corresponding);
	}
	
	/**
	 * Safe taking a value from a box
	 * 
	 * @param box - a box to take a string from
	 * @param defaultValue - a value to be returned if the value from the box is not a positive number
	 * @return
	 */
	private int getIntFromBox(JTextField box, int defaultValue) {
		try {
			int valueFromBox = Integer.parseInt(box.getText());
			if (valueFromBox <= 0) {
				System.err.println("Please enter valid positive numbers!\n Default values will be used!");
			} 
			else { 
				return valueFromBox;
			}
		}
		catch (NumberFormatException nfe) {
		}		
		return defaultValue;
	}

	/**
	 * Taking a value from a text box
	 * @param box
	 * @param defaultValue
	 * @return
	 */
	private String getStringFromBox(JTextField box, String defaultValue) {
		String text = searchBox.getText();
		if (text == "") {
			System.out.println("Default search string will be used, " + defaultValue);
			return defaultValue;
		}
		else {
			return text;
		}
	}

	/**
	 * Screens out results in the result box
	 * @param trends - a list of string trending topics
	 * @param corresopdings - results from the search
	 */
	public void screenLocatedTrends (String [] trends, String [][] corresopdings) {
		int len = trends.length;
		String result = "Trending topics for location: " + trends[len - 1];
		for (int i = 0; i < len - 1; i++) {
			result += "\n Topic " + (i+1) + ": " + trends[i] + " --> "; 
			for (String cor: corresopdings[i]) {
				result += cor + ", ";
			}
		}
		resultBox.setText(result);
	}
	
	/**
	 *  Screens out results in the result box for a single searchString
	 *  
	 * @param searchString
	 * @param results
	 */
	public void screenCustomSearch (String searchString, String[] results) {
		String result = "Search string: " + searchString + "\nResults:";
		for (int i = 0; i < results.length; i++) {
			result += "\n --> " + results[i]; 
		}
		resultBox.setText(result);
	}
	
	

	public void clearFields () {
		System.out.println("CLEAR!");	
		searchBox.setText("");
		totalTimeBox.setText("");
		resultBox.setText("");
		locationBox.setText("");
	}

	public void paint(Graphics g) {
	    if (startMode)
	    	initGraphics(this.getContentPane());	   
	}
	
	private void initGraphics (Container pane) {
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));				
	    JPanel buttons = getButtonPanel();	  
	    JPanel search = getSearchPanel();
	    JPanel result = getResultBox();
		pane.add(search);
		pane.add(buttons);
    	pane.add(result);
		startMode = false;
	}
	
	private JPanel getSearchPanel() {
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		searchPanel.setSize(BOARDX, BUTTONY);
		searchBox = createTextBox();
		totalTimeBox = createTextBox();
		waitBox = createTextBox();
		locationBox = createTextBox();
		
		JLabel jl1 = new JLabel("Search criteria: "); 
		JLabel jl2 = new JLabel("Search time[ms]: "); 
		JLabel jl3 = new JLabel("Wait Time[ms]: ");
		JLabel jl4 = new JLabel("Trending location: ");
		searchPanel.add(jl1); 
		searchPanel.add(searchBox);
		searchPanel.add(jl2);
		searchPanel.add(totalTimeBox);
		searchPanel.add(jl3);
		searchPanel.add(waitBox);
		searchPanel.add(jl4);
		searchPanel.add(locationBox);
		return searchPanel;
	}
	
	private JPanel getResultBox() {
		JPanel resultPanel = new JPanel();
		resultBox = new JTextArea();
		resultBox.setSize(BOARDX - 10, BOARDY - BUTTONY * 2);
		resultBox.setColumns(90);
		resultBox.setRows(31);
		resultPanel.add(resultBox);
		return resultPanel;
	}
	
	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setSize(BOARDX, BUTTONY);
		
		/** definition of mouse listeners **/
		ActionListener listener = new ActionListener() {						
			@Override
			public void actionPerformed(ActionEvent e) {
				String action = e.getActionCommand();
				if (action.equals(SEARCH) ) {
					search();
				} 
				else if (action.equals(SEARCH_LONG)) {
					searchForLongTime();
	            }
				else if (action.equals(SEARCH_TRENDING)) {
					searchTrendingTopics();
				}
				else if (action.equals(CLEAR)) {
	            	clearFields();;
	            }
			}
		};		
		
		/** UI components arangement **/
		
		addButton("Search!", SEARCH, listener,  buttonPanel);
		addButton("Long Time Search!", SEARCH_LONG, listener, buttonPanel);
		addButton("Search Trending!", SEARCH_TRENDING, listener, buttonPanel);
		addButton("Clear !", CLEAR, listener, buttonPanel);
		
		return  buttonPanel;
	}
	
	
	private void addButton(String name, String caughtAction, ActionListener listener, JPanel container) {
		JButton button = new JButton(name);
		button.setActionCommand(caughtAction);
		button.addActionListener(listener);
		container.add(button);		
	}
	
	private JTextField createTextBox() {
		JTextField res = new JTextField();
		res.setColumns(10);
		res.setSize(new Dimension(80, 20));
		return res;
	}
	
	public static void main(String[] args) {
		new Main();	
	}
}