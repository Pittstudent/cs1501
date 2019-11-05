import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * The {@code AirlineAnalysis} class implements a simple information program for a 
 * fictional airline. It is designed to be accessed by employees of the company, 
 * who may pass some of the information on to customers when needed. It would
 * show a menu that lists queries that the user may look for. Airlines data should
 * be input from a file when the program begins. Output would be well formatted if
 * valid infomation has been read from the input file.
 * 
 *  <p>
 *  This implementation uses simple graph algorithms. Graphs are represented as an
 *  adjacency list. The Edge will have multiple values (distance, price) and are  
 *  implemented as a single list of edges with two values each. The DirectedEdge have
 *  implemented as two separate lists of edges, one for distance and one for cost. 
 *	The cities names are stored in an arraylist. MST is using Prim’s algorithm. Dijkstra’s
 *  algorithm is applied for searching the shortest distance and shortest price paths.
 *  The shortest hops path use breadth-first search. 
 *  <p>
 *  It supports the following operations: Show a list of direct routes with distances and
 *  prices, display a minimum spanning tree for the service routes based on distances, 
 *  find shortest path based on total miles from the source to the destination, find
 *	shortest path based on price from the source to the destination, find shortest 
 *	path based on number of individual segments from the source to the destination, search 
 *  all trips whose cost is less than or equal to an amount entered by user, add a new 
 *  route to the schedule and remove existing route from the schedule.  
 *  <p>
 * 
 * 
 * @author      Joanne Chen
 * 
 */
public class AirlineAnalysis {
	static Scanner sc = new Scanner(System.in);
	static int numCities;
	static ArrayList<String> cities = new ArrayList<String>();
	static EdgeWeightedGraph g1;
	static EdgeWeightedDigraph g2_dist;
	static EdgeWeightedDigraph g3_cost;
	static Graph g4_hop;

	/**
 	 *  This method would load from a file of a list of all of the service routes that the 
 	 *  Airline company runs. The file name was input by the user. These routes include the
 	 *  cities served and the various non-stop destinations from each city. Then it would
 	 *  interpreting these routes as a graph with the cities being the vertices and the non-stop
 	 *  trips being the edges. Here we assume that all routes are bidirectional, as airlines 
 	 *  almost fly non-stop routes in both directions. Then create both undirected graph and 
 	 *  directed graph, with a link in each direction for each trip, according to the flight 
 	 *  infomation. Both typs of graph would be used to find out solution of different searches 
 	 *  later. Each edge has 2 different weights: one weight based on the distance between the
 	 *  cities and the other based on the price of a ticket between the cities. 
 	 * <p>
 	 *  If there is any exception countered while reading the file, this method would catch 
 	 *  that exception and print out the exception to the command.
 	 *
 	 * @param s    input data file name   
	 *
     */
	public static void init(String s){
		try{
			String filename = s;
			File file = new File(filename);

			while(!file.exists()){
				System.out.print("File does not exist. Please try another file...");
	            filename = sc.nextLine();
	            file = new File(filename);
			}

			Scanner scanner = new Scanner(file);
			String tmp = scanner.nextLine();
			numCities = Integer.parseInt(tmp);
	
			g1 = new EdgeWeightedGraph(numCities);
			g2_dist = new EdgeWeightedDigraph(numCities);
			g3_cost = new EdgeWeightedDigraph(numCities);
			g4_hop = new Graph(numCities);
			

			int i = 0;
			while (scanner.hasNext() && i < numCities){
				tmp = scanner.nextLine();
				cities.add(tmp);
				i++;
			}

			while (scanner.hasNext()){
				tmp = scanner.nextLine();
				String[] temps = tmp.split(" ");
				int v1 = Integer.parseInt(temps[0])-1;
				int v2 = Integer.parseInt(temps[1])-1;
				double distance = Double.parseDouble(temps[2]);
				double cost = Double.parseDouble(temps[3]);
			
				g4_hop.addEdge(v1,v2);
				Edge e = new Edge(v1,v2,distance,cost);
				g1.addEdge(e);
				
				DirectedEdge dist_edge = new DirectedEdge(v1,v2,distance);
				DirectedEdge cost_edge = new DirectedEdge(v1,v2,cost);
				g2_dist.addEdge(dist_edge);
				g3_cost.addEdge(cost_edge);
				
				dist_edge = new DirectedEdge(v2,v1,distance);
				cost_edge = new DirectedEdge(v2,v1,cost);
				g2_dist.addEdge(dist_edge);
				g3_cost.addEdge(cost_edge);				
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}

	/** 
     *  Display by points and edges of a minimum spanning tree for the service routes based
     *  on distances. This could be useful for maintenance routes and/or shipping supplies 
     *  from a central supply location to all of the airports. It provides infomation of the 
     *  smallest distance that allow us to travel all the cities that the airlines cover.  
     * <p>
     *	If the route graph is not connected, this query would identify and show each of the
     *  connected subtrees of the graph and show the cities that are not connected on the 
     *  graph (not appear in the minimum spanning trees of each connected component). 
     * <p>
     *	It computes a minimum spanning tree (or forest) of an edge-weighted graph, where each
 	 *  undirected edge is of type {@link Edge} and has two real-valued weights--distance and
 	 *  price.
     *
     */
	public static void showMinTree(){
		ArrayList<String> include = new ArrayList<String>();
		PrimMST mst = new PrimMST(g1);
		System.out.println("\nMINIMUM SPANNING TREE");
		System.out.println("-------------------------------");
		System.out.println("The edges in the MST based on distance follow:");
		for(Edge e : mst.edges()){
			System.out.println(cities.get(e.getw())+" , "+cities.get(e.getv())+" : "+(int)(e.weight()));
			if (!include.contains(cities.get(e.getw()))){
				include.add(cities.get(e.getw()));
			}
			if (!include.contains(cities.get(e.getv()))){
				include.add(cities.get(e.getv()));
			}
		}

		if(notConnectedGraph(include)){
			StdOut.println(" : not connected");
		}		
	}

	/** 
     *  This is a private method that check if there are cities that are not included in the
     *  minimum spanning tree. It is achieved by comparing all the cities that we have read
     *  from input file and cities in the MST. This is a helper method of showMInTree() and 
     *  only the AirlineAnalysis class can call it.  
     * <p>
     *	If there are cities that are not connected in the graph, this method would print out 
     *  these cities' names in a well formatted way and return true. 
     * <p>
     *	If there is not any cities that is excluded by the minimum spanning tree, this method
     *	would simply return false.
     *
 	 * @param include    an arraylist of cities that are in the MST
 	 * @return          <code>true</code> if there is city that is not connected in MST; 
     *                  <code>false</code> otherwise.  
     *
     */
	private static boolean notConnectedGraph(ArrayList<String> include){
		StdOut.println(" ");
		boolean is = false;
		int num = 0;
		for(String city : cities){
			if(!include.contains(city)){
				if(num == 0){
					StdOut.print(city);
				}else{
					StdOut.print(", "+city);
				}
				is = true;
				num++;
			}
		}
		return is;
	}

	/** 
	 *	This method would allow search for shortest path based on total miles (one way) from the
	 *  source to the destination. Assuming distance and time are directly related, this could be
	 *  useful to passengers who are in a hurry. It would also appeal to passengers like environmentalists
	 *  who want to limit their carbon footprints. To search, user would enter the source and
	 *  destination cities' names, and the output would has the cities in the path (starting at the 
	 *  source and ending at the destination). The output would include every sub-airline's distance 
	 *  from the source and destination with stop cities.
	 * <p>
	 *  If user input a city name that has a typo or is not a city that covered by the Airline
	 *  company, this method will keep asking user for the city name until the name is valid.
	 * <p>
     *  If multiple paths have the same distance from the source to the destination, this method 
     *  would only print one out. 
  	 * <p>    
     *  If there is no path between the source and destination, this method would indicate this 
     *  fact on the command.
     * <p>
     *  This method computes the shortest path tree on an edge-weighted digraph by Dijkstra's 
     *  algorithm. 
     *
     */
	public static void distantShortestPath(){
		String c1;
		do{
			System.out.println("Please enter the departure city: ");
			c1 = StdIn.readString();
		}while (!cities.contains(c1));
		int v1 = cities.indexOf(c1);

		String c2;
		do{
			System.out.println("Please enter the destination city: ");
			c2 = StdIn.readString();
		}while(!cities.contains(c2));
		int v2 = cities.indexOf(c2);

		System.out.println("\nSHORTEST DISTANCE PATH from "+ c1 +" to "+ c2);
		System.out.println("----------------------------------------------");
		DijkstraSP sp = new DijkstraSP(g2_dist, v1);
		if (sp.hasPathTo(v2)) {
            System.out.printf("Shortest distance from " + c1 + " to " + c2 + " is " + sp.distTo(v2) +" \n");
            System.out.println("Path with edges (in order):");
            for (DirectedEdge e : sp.pathTo(v2)) {
                System.out.print(cities.get(e.from()) + " " + (int)(e.weight()) + " ");
            }
            System.out.println(c2);
        }else {
            System.out.print(c1 +" to "+ c2 +" no path\n");
        }
	}

	/** 
	 *	This method would allow search for shortest path based on price from the source to the 
	 *  destination. Since distance and price do not always correspond, this could be useful for
	 *  passengers who want to save money. To search, user would enter the source and
	 *  destination cities' names, and the output would has the stop cities in the path (starting  
	 *  at the source and ending at the destination) with each 'segment' airline's price.
	 * <p>
	 *  If user input a city name that has a typo or is not a city that covered by the Airline 
	 *  company, this method will keep asking user for the city name until the name is valid.
	 * <p>
     *  If multiple paths have the same cost from the source to the destination, this method 
     *  would only output one. 
  	 * <p>    
     *  If there is no path between the source and destination at all, this method would indicate 
     *  this fact on the command. 
     * <p>
     *  This method computes the shortest path tree on an edge-weighted digraph by Dijkstra's 
     *  algorithm. 
     *
     */
	public static void priceShortestPath(){
		
		String c1;
		do{
			System.out.println("Please enter the departure city: ");
			c1 = StdIn.readString();
		}while (!cities.contains(c1));
		int v1 = cities.indexOf(c1);

		String c2;
		do{
			System.out.println("Please enter the destination city: ");
			c2 = StdIn.readString();
		}while(!cities.contains(c2));
		int v2 = cities.indexOf(c2);

		System.out.println("\nSHORTEST COST PATH from "+ c1 +" to "+ c2);
		System.out.println("----------------------------------------------");
		DijkstraSP sp = new DijkstraSP(g3_cost, v1);
		if (sp.hasPathTo(v2)) {
            System.out.printf("Shortest cost from " + c1 + " to " + c2 + " is " + sp.distTo(v2) +" \n");
            System.out.println("Path with edges (in order):");
            for (DirectedEdge e : sp.pathTo(v2)) {
                System.out.print(cities.get(e.from()) + " " + e.weight() + " ");
            }
            System.out.println(c2);
        }else {
            System.out.print(c1 +" to "+ c2 +" no path\n");
        }
	}

	/** 
	 *  This method would allow search for shortest path based on number of stops (individual segments) 
	 *  from the source to the destination. This option could be useful to passengers who prefer fewer 
	 *  segments for traveling with small children or in a hurry. To search, user would enter the source
	 *  and destination cities' names, and the output would include the every city in the route (starting  
	 *  at the source and ending at the destination).
	 * <p>
	 *  If user input a city name that has a typo or is not a city that covered by the Airline 
	 *  company, this method will keep asking user for the city name until the input name is valid.
	 * <p>
     *  If multiple paths have the same number of stops from the source to the destination, this method 
     *  would only print one out. 
  	 * <p>    
     *  If there is no path between the source and destination, this method would indicate this 
     *  fact on the command.
     * <p>
     *  This method computes the shortest path tree on an undirected graph using breadth-first search.
	 *
     */	
	public static void hopShortestPath(){
		String c1;
		do{
			System.out.println("Please enter the departure city: ");
			c1 = StdIn.readString();
		}while (!cities.contains(c1));
		int v1 = cities.indexOf(c1);

		String c2;
		do{
			System.out.println("Please enter the destination city: ");
			c2 = StdIn.readString();
		}while(!cities.contains(c2));
		int v2 = cities.indexOf(c2);

		System.out.println("\nFEWEST HOPS from "+ c1 +" to "+ c2);
		System.out.println("----------------------------------------------");
		BreadthFirstPaths bfs = new BreadthFirstPaths(g4_hop, v1);
		if (bfs.hasPathTo(v2)) {
			System.out.printf("Fewest hops from " + c1 + " to " + c2 + " is " + (int)(bfs.distTo(v2)) +" \n");
            System.out.println("Path (in order):");
            for (int x : bfs.pathTo(v2)) {
                System.out.print(cities.get(x) + " ");
            }
            System.out.println("");
        }else{
            System.out.print(c1 +" to "+ c2 +" no path\n");
        }
	}

	/** 
	 *  This method would find out all trips whose cost is less than or equal to a given dollar amount  
	 *  entered by the user. In this case, a trip can contain an arbitrary number of stops as long as
	 *  it would not repeat any cities (it is not a cycle or cannot contain a cycle).  This feature 
	 *  would be useful for the airline to print out weekly "super saver" fare advertisements or to help
	 *  travelers who are flexible in their destinations but not flexible in their overall costs. It  
	 *  would be very useful especially near a vacation. 
	 * <p>
	 *  Note that some paths are duplicated since it would output once from each end city's point of view.
	 * <p>
	 *  the output would has every stop city name in the path (starting at the source and ending at the
	 *  destination) with each 'segment' airline's price.
	 * <p>
     *  This method computes the shortest path tree on an edge-weighted digraph by Dijkstra's algorithm. 
	 *
     */	
	public static void costLessThan(){
		System.out.println("Please enter a dollar amount that a trip's cost is less than or equal to: ");
		int max_cost = StdIn.readInt();
       
        System.out.println("\nALL PATHS OF COST "+max_cost+" OR LESS");
		System.out.println("Note that paths are duplicated, once from each end city's point of view");
		System.out.println("------------------------------------------------------------------------");
		for(int s = 0; s < numCities; s++){
        	DijkstraSP sp = new DijkstraSP(g3_cost, s);
	        for (int v = 0; v < g3_cost.V(); v++) {
	            if (sp.hasPathTo(v) && sp.distTo(v) <= max_cost) {
	            	if(sp.distTo(v) != 0.0){
		            	System.out.print("Cost: "+sp.distTo(v)+" Path (in order): ");
		            	for (DirectedEdge e : sp.pathTo(v)) {
		            		System.out.print(cities.get(e.from()) + " "+ e.weight() + " ");
		            	} 
		            	System.out.println(cities.get(v)+"\n");      
			        } 
	            }
	        }
	    }
	}

	/** 
	 *  This method would add a new route to the schedule. User would enter the source and destination
	 *  cities' names of the new route. Both input cities should already exist in the data. Otherwise 
	 *  the method would keep asking user to enter a city's name. After that, user would enter the 
	 *  distance and price for the new route. Adding a new route to the schedule may affect the result 
	 *  of other option's query. This feature would be useful for the workers in the airline company 
	 *  to update new airline route to let travelers get the latest route infomation.
	 * <p>
	 *  If user input a city name that has a typo or is not a city that covered by the Airline 
	 *  company, this method will keep asking user for the city name until the input name is valid.
	 * <p>
	 *  If a path between the source and the destination is already existed, the method would print
	 *  out a notification to the command.
	 * <p>
	 *  If a path between the source and the destination does not existed, the method would ask user
	 *  to input the distance and price of the new route and then add this new route to each of the 
	 *  graphs. Once the new route has been added to the graph, a notification would indicate this 
	 *  fact in the command line.
	 *
     */	
	public static void addRoute(){
		String c1;
		do{
			System.out.println("Please enter one of the cities name of the new route: ");
			c1 = StdIn.readString();
		}while (!cities.contains(c1));
		int v1 = cities.indexOf(c1);

		String c2;
		do{
			System.out.println("Please enter another city's name of the new route: ");
			c2 = StdIn.readString();
		}while(!cities.contains(c2));
		int v2 = cities.indexOf(c2);

		if (!g1.hasEdge(v1,v2)){
			System.out.println("Please enter the distance of the new route: ");
			double distance = StdIn.readDouble();
			System.out.println("Please enter the price of the new route: ");
			double cost = StdIn.readDouble();

			Edge e = new Edge(v1,v2,distance,cost);
			g1.addEdge(e);
			g4_hop.addEdge(v1,v2);

			DirectedEdge dist_edge = new DirectedEdge(v1,v2,distance);
			DirectedEdge cost_edge = new DirectedEdge(v1,v2,cost);
			g2_dist.addEdge(dist_edge);
			g3_cost.addEdge(cost_edge);
					
			dist_edge = new DirectedEdge(v2,v1,distance);
			cost_edge = new DirectedEdge(v2,v1,cost);
			g2_dist.addEdge(dist_edge);
			g3_cost.addEdge(cost_edge);
			System.out.println("A new route has been successfully added...");
		}else{
			System.out.println("A route between the two cities is already existed...");
		}
	}

	/** 
	 *  This method would remove a route from the schedule. User would enter the source and 
	 *  destination cities' names of the route that is going to deleted. Both input cities should  
	 *  already exist in the input data. Otherwise the method would keep asking user to enter a 
	 *  valid city's name. Removing existing route from the schedule may affect the result 
	 *  of other option's query. This feature would be useful for customer to get up-to-dated 
	 *  infomation about airlines. Customers could get the latest canceled airline infomation so  
	 *  that they can make arrangment on their schedule for the change.
	 * <p>
	 *  If user input a city name that has a typo or is not a city that covered by the Airline 
	 *  company, this method will keep asking user for the city name until the input name is valid.
	 * <p>
	 *  If a route between the source and the destination exists, the method would remove this path
	 *  from each of the graphs and print out a notification about this action to the command.
	 * <p>
	 *  If the route between the source and the destination does not existed, the method would print
	 *  out a notification to the command.
	 *
     */	
	public static void removeRoute(){
		String c1;
		do{
			System.out.println("Please enter one of the cities name of the route that would be removed: ");
			c1 = StdIn.readString();
		}while (!cities.contains(c1));
		int v1 = cities.indexOf(c1);

		String c2;
		do{
			System.out.println("Please enter another city's name of the route that would be removed: ");
			c2 = StdIn.readString();
		}while(!cities.contains(c2));
		int v2 = cities.indexOf(c2);

		if (g1.hasEdge(v1,v2)){
			g1.removeEdge(g1.find(v1,v2));
			g4_hop.removeEdge(v1,v2);

			DirectedEdge dist_edge = g2_dist.find(v1, v2);
			DirectedEdge cost_edge = g3_cost.find(v1, v2);
			g2_dist.removeEdge(dist_edge);
			g3_cost.removeEdge(cost_edge);
					
			dist_edge = g2_dist.find(v2, v1);
			cost_edge = g3_cost.find(v2, v1);;
			g2_dist.removeEdge(dist_edge);
			g3_cost.removeEdge(cost_edge);
			System.out.println("This route has been successfully deleted...");
		}else{
			System.out.println("A route between the two cities does not exist...");
		}
	}

 	/**
 	 *  This method would indicate the cheapest route from a source city to a destination city 
 	 *  through a third city. In other words, it tries to solve "What is the shortest path from
 	 *  A to B given that I want to stop at C for a while?" All three cities would be input by the
 	 *  user. This feature provides more convenience for travellers who plan to visit a third city 
 	 *  shortly or people who cannot endure long flights. First, it would show the total cost of
 	 *  the route that the method found. Then show the route by printing out each of the stop cities 
 	 *  in the path (starting at the source and ending at the destination with the given stop)
 	 *  plus each of the 'segment' airline's price.
 	 * <p>
 	 *	If user input a city name that has a typo or is not a city that covered by the Airline 
	 *  company, this method will keep asking user for the city name until the input city name 
	 *  is valid.
 	 * <p>
 	 *  If there is no path between the source and destination given a certain stop, this method  
     *  would indicate this fact on the command.
 	 * <p> 
 	 *  If multiple paths have the same price from the source to the destination with the given 
 	 *  stop, this method would only print one out.
	 *
     */	
	public static void threeCheapestPath(){
		String c1;
		do{
			System.out.println("Please enter the departure city: ");
			c1 = StdIn.readString();
		}while (!cities.contains(c1));
		int v1 = cities.indexOf(c1);

		String c2;
		do{
			System.out.println("Please enter the destination city: ");
			c2 = StdIn.readString();
		}while(!cities.contains(c2));
		int v2 = cities.indexOf(c2);

		String c3;
		do{
			System.out.println("Please enter the city that you would like to have a stop: ");
			c3 = StdIn.readString();
		}while(!cities.contains(c3));
		int v3 = cities.indexOf(c3);

		System.out.println("\nSHORTEST COST PATH from "+ c1 +" to "+ c2 +" given that stop at "+ c3 +" for a while");
		System.out.println("---------------------------------------------------------------------------------------");
		DijkstraSP sp1 = new DijkstraSP(g3_cost, v1);
		DijkstraSP sp2 = new DijkstraSP(g3_cost, v3);
		if (isIncludeStop(v1, v2, v3) && sp1.hasPathTo(v3) && sp2.hasPathTo(v2)) {
			double totalCost = sp1.distTo(v3)+sp2.distTo(v2);
			if(totalCost >= sp1.distTo(v2)){
				printCheapestOneRoute(v1, v2, v3, sp1);
	        }else{
	        	printCheapestStopRoute(v1, v2, v3, sp1, sp2);
	        }
	    }else{
	    	if(isIncludeStop(v1, v2, v3)){
	    		printCheapestOneRoute(v1, v2, v3, sp1);
	        }else if(sp1.hasPathTo(v3) && sp2.hasPathTo(v2)){
	        	printCheapestStopRoute(v1, v2, v3, sp1, sp2);
	        }else {
	            System.out.print(c1 +" to "+ c2 +" given that stop at "+ c3 +" has no path\n");
	        }
	    }
	}

	/** 
     *  This is a private method that check if the shortest path based on price from the source  
	 *  to the destination contains the third city that we would like to have a stop. This is simply 
	 *  a helper method of threeCheapestPath() and only the AirlineAnalysis class can call it. 
	 *  Since the cheapest route from a source city to a destination city through a third city
	 *  could be the shortest path based on price from source to destination that contains the 
	 *  third city or the sum of the shortest route based on price from source to the third city
	 *  and thr shortest route based on price from the third party to the destination, we have this
	 *  helper method to verify that the third city's position is in the cheapest route from source 
	 *  to destination so that we can go on further cost evaluation. 
     * <p>
     *	If the thrid city is on the shortest path based on price from the source city to the 
     *  destination city, this helper method would return true. Otherwise, it would return 
     *  false.
     *
     * @param v1        the index number of the source
     * @param v2        the index number of destination
     * @param v3        the index number of the third city
     * @return          <code>true</code> if the thrid city is on the shortest cost path from  
     *  				the source city to the destination city; 
     *                  <code>false</code> otherwise.
     */
	private static boolean isIncludeStop(int v1, int v2, int v3){
		boolean include = false;
		DijkstraSP sp = new DijkstraSP(g3_cost, v1);
		if (sp.hasPathTo(v2)) {
            for (DirectedEdge e : sp.pathTo(v2)) {
            	if(e.from() == v3){
            		include = true;
            	}
            }
        }
        return include;
	}

	/** 
     *  This is a private method that would print the shortest path based on price from the 
	 *  source to the destination that contains the third city on command. This is simply a   
	 *  helper method of threeCheapestPath() and only the AirlineAnalysis class can call it.
	 *  The output would has all the cities in the path (starting at the source and ending at
	 *  the destination). And the output would include the total cost it eventually need  and 
	 *  every sub-airline's cost from the source and destination with stop cities. 
     *
     * @param v1        the index number of the source
     * @param v2        the index number of destination
     * @param v3        the index number of the third city
     * @param sp1		the shortest path tree running by Dijksra's algorithm from vertex v1
     */
	private static void printCheapestOneRoute(int v1, int v2, int v3, DijkstraSP sp1){
		System.out.printf("Shortest cost from " + cities.get(v1) + " to " + cities.get(v2) + " with stop at "+ cities.get(v3) + " is " + sp1.distTo(v2) +" \n");
	    System.out.println("Path with edges (in order):");
	    for (DirectedEdge e : sp1.pathTo(v2)) {
	        System.out.print(cities.get(e.from()) + " " + e.weight() + " ");
	    }
	    System.out.println(cities.get(v2));
	}

	/** 
     *  This is a private method that would simply print the shortest route based on price
	 *  from source to the third city and the shortest route based on price from the third
	 *  party to the destination. This is simply a helper method of threeCheapestPath()  
	 *  and only the AirlineAnalysis class can call it. The output would has all the cities 
	 *  in the path. And the output would include the total cost it eventually need and 
	 *  every sub-airline's cost from the source and destination with stop cities. 
     *
     * @param v1        the index number of the source
     * @param v2        the index number of destination
     * @param v3        the index number of the third city
     * @param sp1		the shortest path tree running by Dijksra's algorithm from vertex v1
     * @param sp1		the shortest path tree running by Dijksra's algorithm from vertex v3
     */
	private static void printCheapestStopRoute(int v1, int v2, int v3, DijkstraSP sp1, DijkstraSP sp2){
		double totalCost = sp1.distTo(v3)+sp2.distTo(v2);
       	System.out.printf("Shortest cost from " + cities.get(v1) + " to " + cities.get(v2) + " with stop at "+ cities.get(v3) + " is " + totalCost +" \n");
        System.out.println("Path with edges (in order):");
            
        for (DirectedEdge e : sp1.pathTo(v3)) {
            System.out.print(cities.get(e.from()) + " " + e.weight() + " ");
        }
        for (DirectedEdge e : sp2.pathTo(v2)) {
            System.out.print(cities.get(e.from()) + " " + e.weight() + " ");
        }
        System.out.println(cities.get(v2));
	}

	/** 
	 *  This method would show the entire list of direct routes, and their corresponding distances
	 *  and prices. This amounts to outputting the entire graph in a well-formatted way. This 
	 *  operation does not require any sophisticated algorithms to implement. This feature would
	 *  allow customer quickly access all direct fly routes that the airline company provides with 
	 *  their distance and price infomation. It is also useful for employees of the airline company 
	 *  to check a certain airline route has been added to and removed from the schedule.  
	 * <p>
	 *  Note that some paths are duplicated since it would output once from each end city's point 
	 *  of view.
	 *
     */	
	public static void showRoute(){
		System.out.println("\nDIRECT ROUTES WITH DISTANCES AND PRICES");
		System.out.println("Note that paths are not duplicated, once from each end city's point of view");
		System.out.println("-------------------------------------------------------------------------");
		for (int v = 0; v < numCities; v++) {
            for (Edge e : g1.adj(v)) {
            	StdOut.println(cities.get(v) + " to " + cities.get(e.other(v)) + " : " + (int)(e.weight()) + " miles, "+ e.cost() + " dollars");
            }
        }
	}

   /** 
	*  This method would save all the routes back to the same data file and format that they 
	*  were read in from, but containing the possibly modified route information. All the routes 
	*  are bidirectional so there is no duplicated route written back to the file. It would not 
	*  overwrite the existed routes in the data file. Every city in the direct path is represented
	*  as number.
	*
 	* @param s    input data file name
	*/
	public static void saveRoutes(String s){
		ArrayList<Edge> include = new ArrayList<Edge>();
		try{
			FileWriter fw = new FileWriter(s,true); //true will append the new data
			fw.write("\nSAVING ALL THE ROUTES...\n---------------------------------\n"); 
			for (int v = 0; v < numCities; v++) {
	            for (Edge e : g1.adj(v)) {
	            	Edge e_flip = g1.find(e.other(v), v);
	            	if((!include.contains(e) && !include.contains(e_flip))){
	            		fw.write((v+1) + " " + (e.other(v)+1) + " " + (int)(e.weight()) + " "+ e.cost() + "\n");
	            		include.add(e);
	            		include.add(e_flip);
	            	}
	           	}
	        }
	        fw.close();
		}catch(IOException ioe){
    		System.err.println("IOException: " + ioe.getMessage());
		}		
	}
	/**
	 *  This is the main program that contains a menu-driven loop that asks the user for any of the
	 *  queries. It would first pass the arguement of input data file name to process. Once all the  
	 *  data in the input file has been processed, it would output a menu that has queries labeled 
	 *  from 0 to 9. Each query would meet different requirements of the project. User would have 
	 *  to input the label number to start each query option. The output would be shown on the 
	 *  command line in a clear and well-formatted manner.
	 * <p>
	 *  Query labeled 8 would output the cheapest route from a source city to a destination city 
	 *  through a third city. This meets one of extra credit options. 
	 * <p>
	 *  Query labeled 0 would quit the program. Before quitting, all the routes would be saved back to
	 *  the same file and format that they were read in from, but containing the possibly modified
	 *  route information.  
	 *
	 * @param args    command line arguments as an array of String objects   
	 *
     */ 
	public static void main(String[] args) {
		boolean stop = false;
		init(args[0]);

		while(!stop){
			System.out.println("\n********************************************************************************\n");
			System.out.println("What are you looking for? (enter number 0-9)\n");
			System.out.println("1.Find the minimum spanning tree for the service routes based on distances\n");
			System.out.println("2.Shortest path based on total miles (one way) from the source to the destination \n");
			System.out.println("3.Shortest path based on price from the source to the destination \n");
			System.out.println("4.Shortest path based on number of hops (individual segments) from the source to the destination \n");
			System.out.println("5.Find all trips whose cost is less than or equal to an amount\n");
			System.out.println("6.Add a new route to the schedule \n");
			System.out.println("7.Remove a route from the schedule\n");
			System.out.println("8.The cheapest route from a source city to a destination city through a third city.\n");
			System.out.println("9.Show the entire list of direct routes, distances and prices.\n");
			System.out.println("0.Exit the program");
			System.out.println("********************************************************************************\n");
			int input = StdIn.readInt();
		
			switch (input) {
				case 1: showMinTree();
						break;
				case 2: distantShortestPath();
						break;
				case 3: priceShortestPath();
						break;
				case 4: hopShortestPath();
						break;
				case 5: costLessThan();
						break;
				case 6: addRoute();
						break;
				case 7 :removeRoute();
						break;
				case 8 :threeCheapestPath();
						break;
				case 9 :showRoute();
						break;
				case 0: stop = true;
						saveRoutes(args[0]);
						break;
			}
		}

		System.out.println("Exiting the main menu...");
		System.exit(0);
	}
}