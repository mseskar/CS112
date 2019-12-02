package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		

		boolean[] visited = new boolean[g.members.length]; //following Sesh implementation of BFS
		Queue<String> queue = new Queue<String>(); 
		 
		
		ArrayList<ArrayList<ArrayList<String>>> nodePaths = new ArrayList<ArrayList<ArrayList<String>>>(g.members.length); //index of the array is the number of the node, each entry is the list of paths to that node from the source
		//populate 
		for(int i =0; i < g.members.length; i++) {
	    	nodePaths.add(i, new ArrayList<ArrayList<String>>() );
		}
		//start from the source, first name given
		int index = g.map.get(p1); 
		System.out.println("First visiting " + p1);
		//visited[index] = true;
		queue.enqueue(p1);
		visited[index] = true;
		ArrayList<String> path = new ArrayList<String>(); //actual path to the node
		ArrayList<ArrayList<String>> routes = nodePaths.get(index); //list of all possible paths to this node
		path.add(p1);
		routes.add(path);		
		//printNodePaths(nodePaths, index);
		 
		while(!queue.isEmpty()) {
			//examine the next node that needs to be evaluated
			index = g.map.get((queue.dequeue()));
			
			for(Friend ptr = g.members[index].first; ptr != null; ptr = ptr.next) {
			    int nextIndex = ptr.fnum;
			    //cover all the friends / neighbors of the latest node visited
			    if(!visited[nextIndex]) {
			    	System.out.println("Visited " + g.members[nextIndex].name);
			    	visited[nextIndex] = true; //mark the node complete only after we've checked all it's connected nodes
			    	routes = nodePaths.get(nextIndex);  
			    	for(ArrayList<String> list : nodePaths.get(index)) {
			    		path = (ArrayList<String>)list.clone();
			    		path.add(g.members[nextIndex].name);
			    		System.out.println(path.toString());
			    		routes.add(path);
			    	}
			    	queue.enqueue(g.members[nextIndex].name);
			    	//printNodePaths(nodePaths, nextIndex);
			    }		    
				
			}
			
		}

		
		//printAllPaths(nodePaths, g, p1);
		return shortestPath(nodePaths, g.map.get(p2));
	}
//	
//	private static void printNodePaths(ArrayList<ArrayList<ArrayList<String>>> nodePaths, int index) {
//		System.out.println("All paths for " + index );
//		ArrayList<ArrayList<String>> routes = nodePaths.get(index);
//		for(ArrayList<String> paths : routes) {
//			System.out.println(paths.toString());
//		}		
//	}
//	
//	private static void printAllPaths(ArrayList<ArrayList<ArrayList<String>>> nodePaths, Graph g, String p1) {
//		for(int i = 0; i < nodePaths.size(); i++) {
//			printNodePaths(nodePaths, i);
//		}
//		
//	}
	
	private static ArrayList<String> shortestPath(ArrayList<ArrayList<ArrayList<String>>> nodePaths, int index){
		ArrayList<String> shortest = new ArrayList<String>();
		ArrayList<ArrayList<String>> paths = nodePaths.get(index);
		shortest = paths.get(0);
		for(int i = 0; i < paths.size(); i ++) {
			if(paths.get(i).size() < shortest.size()) shortest = paths.get(i); 
		}
		return shortest;
	}
    
    
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
	    boolean[] visited = new boolean[g.members.length];
	    
	    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
	    
	    for(int index = 0; index < g.members.length; index++) {
	        
	        if(visited[index]) // check if we've been there before
	            continue;
	        
	        ArrayList<String> clique = new ArrayList<String>();
	        
	        DFS(g, school, visited, index, clique); //run DFS from each node, this way even if we encounter islands we can continue
	        
	        if(!clique.isEmpty()) {
	            result.add(clique);
	        }
	        
	    }
		if(result.isEmpty()) {
		    return null;
		}
		return result;
		
		
	}
	
	private static void DFS(Graph g, String school, boolean[] visited, int index, ArrayList<String> clique) {
        visited[index] = true;
        if(g.members[index].student && g.members[index].school.equals(school))
            clique.add(g.members[index].name);
      
        for(Friend ptr = g.members[index].first; ptr != null; ptr = ptr.next) {
            int nextIndex = ptr.fnum;
            
            if(!(visited[nextIndex]) && g.members[nextIndex].student && g.members[nextIndex].school.equals(school)) {
                DFS(g, school, visited, nextIndex, clique);
            }
        }
        
        
    }
	
	
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		return null;
		
	}
	
	

}

