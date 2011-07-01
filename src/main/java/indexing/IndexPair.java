package indexing;

/**
 * A dummy class to hold word/count pairs
 * @author mateva
 *
 */
public class IndexPair {
	private String word;
	private int count;
	
	public IndexPair(String word, int count) {
		this.word = word;
		this.count = count;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}	
}
