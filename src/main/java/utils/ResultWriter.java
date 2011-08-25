package utils;

/**
 * @author mateva
 * 
 * A utility for writing results file. To be improved.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class ResultWriter {
	
	private File resultsRoot;
	public ResultWriter () {
		resultsRoot = new File(Constants.RESULTS_DIRECTORY);
		if (!resultsRoot.exists()) {
			resultsRoot.mkdir();
		}	
	}
	
	// The below two methods can be unified.
	public void writeResults(String subdir, String timeStamp, String locationOrString, String trendingOrCustom, String result) {
		String path = PathMaker.path(Constants.RESULTS_DIRECTORY, subdir);
		File trendingRoot = new File(path);
		if (!trendingRoot.exists()) {
			trendingRoot.mkdir();
		}	
		File output = new File(PathMaker.path(path, trendingOrCustom + "_" + timeStamp + "_" + locationOrString));
		try {
			FileWriter fw = new FileWriter(output);
			fw.write(result);
			fw.close();
		}
		catch (IOException ieo) {
			ieo.printStackTrace();
		}
	}

}
