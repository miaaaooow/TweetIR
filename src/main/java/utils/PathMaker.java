package utils;
/**
 * @author mateva
 * 
 * A utility for path creation. To be improved.
 */

public class PathMaker {
	private static String separator = "/";
	
	public static String path(String path, String dir) {
		return path + separator + dir;
	}
	
	public static String userPath(String path, String dir) {
		return path + separator + "USER_" + dir;
	}
	
	
	
    /**
     * This is to normalize paths, so that they do not start with # of @.
     * Not used for now.
     * @param searchString
     * @return
     */
    public static String normalizeString(String searchString) {
    	if (searchString == null || searchString == "")
    		return searchString;
    	if (searchString.charAt(0) == '@') {
    		return "AT_@" + searchString.substring(1);
    	}
    	if (searchString.charAt(0) == '#') {
    		return "HASH_#" + searchString.substring(1);
    	}
    	return searchString;
    }
}
