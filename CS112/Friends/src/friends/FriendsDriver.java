package friends;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FriendsDriver {
	
	
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(new File("test2.txt"));
		
		Graph g = new Graph(sc);
		
		
		ArrayList<String> shortest = Friends.shortestChain(g, "milos", "ivan");
		
		if(shortest == null) {
			System.out.println("No link");
		}
		else {
			for(String person : shortest) {
				System.out.println(person);
			}
		}
		
//		
//		
//		ArrayList<ArrayList<String>> cliques = Friends.cliques(g, "rutgers");
//        if(cliques != null) {
//            for(ArrayList<String> perClique : cliques) {
//                System.out.println(perClique.toString());
//            }
//        }
//        
//		
//		ArrayList<String> connectors = Friends.connectors(g);
//		for(String connector : connectors) {
//			System.out.println(connector);
//		}
//		
	}

}