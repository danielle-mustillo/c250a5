// STUDENT_NAME: Danielle Mustillo
// STUDENT_ID: 260533476

import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
	searchEngine() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String>>();
		internet = new directedGraph();
	} // end of constructor2013
    
    
	// Returns a String description of a searchEngine
	public String toString() {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	}
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
	/* WRITE SOME CODE HERE */
    	//set visited and page rank
    	internet.setVisited(url, true);
    	internet.setPageRank(url, 1);
    	
    	//add the words to the wordIndex
    	LinkedList<String> words = htmlParsing.getContent(url);
    	
    	//add words to wordIndex, also add the website to that words list of URL's. 
    	for(String word : words)
    		if(!wordIndex.containsKey(word)) {
    			LinkedList<String> newWord = new LinkedList<String>();
    			newWord.addLast(url);
    			wordIndex.put(word, newWord);
    		}
    		else {
    			LinkedList<String> theURLs = wordIndex.get(word);
    			theURLs.addLast(url);
    		}
    	
    	/* Go to each URL in the current webpage, 
    	 * Add it to our internet directedGraph.
    	 */
    	LinkedList<String> outgoingURL = htmlParsing.getLinks(url);
    	Iterator<String> iterator = outgoingURL.iterator();
    	while(iterator.hasNext()) {
    		String next = iterator.next();
    		internet.addEdge(url, next);
    		if(!internet.getVisited(next)) {
    			traverseInternet(next);
    		}
    	}
	
	/* Hints
	   0) This should take about 50-70 lines of code (or less)
	   1) To parse the content of the url, call
	   htmlParsing.getContent(url), which returns a LinkedList of Strings 
	   containing all the words at the given url. Also call htmlParsing.getLinks(url).
	   and assign their results to a LinkedList of Strings.
	   2) To iterate over all elements of a LinkedList, use an Iterator,
	   as described in the text of the assignment
	   3) Refer to the description of the LinkedList methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods contains(String s), 
	   addLast(String s), iterator()
	   4) Refer to the description of the HashMap methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods containsKey(String s), 
	   get(String s), put(String s, LinkedList l).  
	*/
	
	
	
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       This method will probably fit in about 30 lines.
    */
    void computePageRanks() {
	/* WRITE YOUR CODE HERE */
    	
    	
    	for(int resolution = 0; resolution < 10; resolution++) {
    		Iterator<String> urls = internet.vertices.keySet().iterator();
    		
    		//store the page ranks temporarily to add at end of iteration
    		HashMap<String, double[]> newMap = new HashMap<String, double[]>();
    		
        	while(urls.hasNext()) {
    			String url = urls.next();
    			double pageRank[] = {0.5};
    			LinkedList<String> references = internet.getEdgesInto(url);
    			for(String reference : references) {
    				double rank = internet.getPageRank(reference);
    				int outDegree = internet.getOutDegree(reference);
    				pageRank[0] += rank / outDegree * 0.5;
    			}
    			newMap.put(url, pageRank);
    		}
        	
        	Iterator<String> url2 = newMap.keySet().iterator();
        	while(url2.hasNext()) {
        		String url = url2.next();
        		internet.setPageRank(url, newMap.get(url)[0]);
        	}
    	}
    } // end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
	/* WRITE YOUR CODE HERE */
    	LinkedList<String> sites = new LinkedList<String>();
    	do {
    		int space = query.indexOf(' ') > -1 ? query.indexOf(' ') : query.length();
    		String word = query.substring(0, space);
    		
    		if(space != query.length())
    			query = query.substring(space + 1);
    		else
    			query = "";
    		
    		if(wordIndex.containsKey(word)) {
    			LinkedList<String> urls = wordIndex.get(word); 
    			for(String url : urls) {
    				sites.addLast(url);
    			}
    		}
    		query = query.substring(query.indexOf(' ') + 1);
    	}while(query.length() > 0);

		String bestSiteSofar = "";
		for(String url : sites)
			if(internet.getPageRank(url) > internet.getPageRank(bestSiteSofar))
				bestSiteSofar = url;
  
		return bestSiteSofar; // remove this
    } // end of getBestURL
    
	
    public static void main(String args[]) throws Exception{		
		searchEngine mySearchEngine = new searchEngine();
		// to debug your program, start with.
//		 mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");

		// When your program is working on the small example, move on to
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");

		// TODO this is just for debugging purposes. REMOVE THIS BEFORE
		// SUBMITTING
		mySearchEngine.computePageRanks();
		
//		System.out.println(mySearchEngine);

		BufferedReader stndin = new BufferedReader(new InputStreamReader(
				System.in));
		String query;
		do {
			System.out.print("Enter query: ");
			query = stndin.readLine();
			if (query != null && query.length() > 0) {
				System.out.println("Best site = "
						+ mySearchEngine.getBestURL(query) + " " + mySearchEngine.internet.getPageRank(mySearchEngine.getBestURL(query)));
			}
		} while (query != null && query.length() > 0);
	} // end of main
}