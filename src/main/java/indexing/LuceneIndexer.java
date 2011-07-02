/**
 * @author Maria Mateva, Sofia Univerity, 2011
 * 
 */
package indexing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import utils.Constants;
import utils.PathMaker;

/**
 * This class communicates with the Lucene library to introduce indexing and search options.
 * Directory structure is
 * TRENDS/SearchString - folders with files to be indexed(one per search string)
 * INDEX/SearchString - index files' folders(one per search string)
 * 
 * @author mateva
 */
public class LuceneIndexer {
	public static final Version LUCENE_2_9_4 = Version.LUCENE_29;
	
	public static final String[] ENGLISH_STOP_WORDS = {
		"a", "an", "and", "are", "as", "at", "be", "but", "by",
		"for", "http", "if", "in", "into", "is", "it",
		"no", "not", "of", "on", "or", "rt", "so", "such",
		"that", "the", "their", "then", "there", "these",
		"they", "this", "to", "was", "will", "with"
	};
	// added by me: rt, http, so
	
	private static final String FIELD_NAME_PATH     = "path";
	private static final String FIELD_NAME_CONTENTS = "contents";


	private static HashSet<String> getStopWords() {
		HashSet<String> stopWords = new HashSet<String>(); 
		for (String a : ENGLISH_STOP_WORDS) {
			stopWords.add(a);
		}
		return stopWords;
	}
    
    private static void createIndex(String searctString) throws CorruptIndexException, LockObtainFailedException, IOException {
		Analyzer analyzer = new StandardAnalyzer(LUCENE_2_9_4, getStopWords());
		File toIndexDir = new File(PathMaker.path(Constants.FILES_TO_INDEX_DIRECTORY, searctString));
		
		//create index folder if it does not exist
		String root = Constants.INDEX_DIRECTORY;
		File indexRoot = new File(root);
		if (!indexRoot.exists()) {
			indexRoot.mkdir();
		}	
		
		File destination = new File(PathMaker.path(Constants.INDEX_DIRECTORY, searctString));
		Directory indexDir = FSDirectory.open(destination);

		IndexWriter indexWriter = new IndexWriter(indexDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
		File [] files = toIndexDir.listFiles();
		for (File file : files) {
			Document document = new Document();

			// Store file path in the index 
			String path = file.getCanonicalPath();
			document.add(new Field(FIELD_NAME_PATH, path, Field.Store.YES, Field.Index.NOT_ANALYZED));

			// store contents field in the index
			Reader reader = new FileReader(file);
			document.add(new Field(FIELD_NAME_CONTENTS, reader));

			indexWriter.addDocument(document);
		}
		indexWriter.optimize();
		indexWriter.close();
	}
    
    /**
     * Gets corresponding words of the trending one(most commonly met in the tweets for the trending topic).
     * @param n - number of top words we need per trending topic
     * @param searchString
     */ 
    public static String [] getTopWords(int numberOfResults, String searchString) {
    	
    	
    	File index = new File(PathMaker.path(Constants.INDEX_DIRECTORY, searchString));
    	index.mkdir();
    	
    	LinkedList<IndexPair> topResults = new LinkedList<IndexPair>();
    	try {
    		// Creating the index to search from
    	 	createIndex(searchString);
    	 	
			Directory indexDir = FSDirectory.open(index);
	    	IndexReader indexReader = IndexReader.open(indexDir);
	    	
	    	/** 
	    	 * topResults - This is a linked list to store results.
	    	 * 0 position is the smallest value
	    	 */
	    	
	    	TermEnum allWords = indexReader.terms();
	    	while (allWords.next()) {
	    		// Counting word occurrences
	    		Term term = allWords.term();
	    		if (term.text().equals(searchString.toLowerCase()) || term.text().length() == 1) {
	    			continue;
	    		}
	    		
	        	TermDocs termDocs = indexReader.termDocs(new Term(FIELD_NAME_CONTENTS, term.text()));
	        	int count = 0;
	        	while (termDocs.next()) {
	        	   count += termDocs.freq();
	        	}
   	
	        	// Finding if the word is in the most frequent elements	        	
        		Iterator<IndexPair> it = topResults.iterator();
    			int indexResults = 0;
    			
    			while (it.hasNext()) {
        			IndexPair resultPair = it.next();
        			//searching for the correct position for the string
        			if (resultPair.getCount() < count) {
        				indexResults++;
        			} 
        			else {
        				break;
        			}		        			
        		}
				if (topResults.size() < numberOfResults) {
					topResults.add(indexResults, new IndexPair(term.text(), count));
				} 
				else {    			
					if (indexResults > 0) {
						topResults.add(indexResults, new IndexPair(term.text(), count));
						topResults.removeFirst(); // the smallest element removed
					} 
				}

				
	        }
	   	}
    	catch (IOException ie) {
    		ie.printStackTrace();
    	}
    	return indexPairsToStringArr(topResults);
    }
    
    /**
     * @param ll - linked list of IndexPairs
     * @return - returns a String array of the words from the IndexPairs, turned in the opposite direction
     */
    private static String [] indexPairsToStringArr(LinkedList<IndexPair> ll) {
    	int len = ll.size();
    	String [] result = new String [len];
    	Iterator<IndexPair> it = ll.iterator();
    	int i = len - 1;
    	while (it.hasNext()) {
    		IndexPair ip = it.next();
    		// This is done in the opposite direction so greatest hits number get earlier in the array
    		result[i--] = ip.getWord();
    	}
    	return result;
    }	

}
