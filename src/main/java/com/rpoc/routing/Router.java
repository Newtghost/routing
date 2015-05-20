package com.rpoc.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Router {
	
	public class Node {
		public String stop_id ;
		public Node previous ;
		public Connection c ;
		
		public Node (String stop_id, Node previous, Connection c) {
			this.stop_id = stop_id ;
			this.previous = previous ;
			this.c = c ;
		}

		public void setPrevious (Node previous, Connection c) {
			if (this.c == null) return ; // Loop on the departure
			if (c.getArrivalTime() < this.c.getArrivalTime()) {
				this.previous = previous ;
				this.c = c ;
			}
		}
	}

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);
    
    private ConnectionsList connections ;
	private String start_id ;
	private String stop_id ;
	private int start_time ;

	private Map<String, Node> spt ;
	private ArrayList<Node> paths ;
	
	public Router (ConnectionsList connections, String start_id, int start_time, String stop_id) {
		this.connections = connections ;
		this.start_id = start_id ;
		this.stop_id = stop_id ;
		this.start_time = start_time ;
		spt = new HashMap<String, Node> () ;
		paths = new ArrayList<Node> () ;
	}
	
	public void run_CSA () {
		// Initialize the shortest path tree
		spt.put(start_id, new Node(start_id, null, null)) ;
		
		// Run the algorithm
		Node nd, na ;
		for (Connection c : connections.getList()) {
			if (c.getDepartureTime() < start_time) continue ;
			nd = spt.get(c.getDepartureId()) ;
			if (nd == null) continue ;
			// We can take this connection
			na = spt.get(c.getArrivalId()) ;
			if (na == null) {
				Node new_node = new Node (c.getArrivalId(), nd, c) ;
				if (c.getArrivalId().equals(stop_id)) {
					paths.add(new_node) ;
				} else {
					spt.put(c.getArrivalId(), new_node) ;
				}
			} else {
				na.setPrevious(nd, c);
			}
		}
		
		// Look for solutions
		if (!paths.isEmpty()) {
			// There exists some solutions
	        LOG.info("CSA found " + paths.size() + " paths.");
	        Node step ;
	        for (Node n : paths) {
	        	step = n ;
	        	System.out.print("At time : " + step.c.getArrivalTime() + " ; ") ;
		        while (step != null) {
		        	System.out.print(step.stop_id + " ; ") ;
		        	step = step.previous ;
		        }
	        	System.out.println("") ;
	        }
		}
	}
}

