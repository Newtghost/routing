package com.rpoc.routing;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

public class Router {
	
    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

	private static final double SPEED = 1; /* TODO : important à bouger autre part, dépend de l'utilisateur */
    
	private String departure_id ;
	private String arrival_id ;
	private int start_time ;
	
	private Multimap <String, Connection> connections ; 
	private ArrayList<Connection> sorted_connections ; 
	private Map <String, ArrayList<Footpath>> footpaths ;
	private Map <String, StopPoint> stops ;
	
	private Journey solution ;
	
	public Router (Builder builder, String start_id, int start_time, String stop_id) {
		this.departure_id = start_id ;
		this.arrival_id = stop_id ;
		this.start_time = start_time ;		
		connections = builder.getConnections() ;
		sorted_connections = builder.getSortedConnections() ;
		footpaths = builder.getFootpaths() ;
		stops = builder.getStops() ;
		solution = new Journey () ;
	}
	
	public void run_CSA () {

		for (Footpath f : footpaths.get(departure_id)) {
			for (Connection c : connections.get(f.getArrivalId())) {
				c.setReachable() ;
			}
		}

		for (Connection c : sorted_connections) {

			if (c.getDepartureTime() < start_time) continue ;
			if (! c.isReachable()) continue ;

			LOG.info("Connection reachable found");
			
			if (c.getArrivalId().equals(arrival_id)) {
				buildJourney (c) ; /* TODO : ca fonctionne pas il faut reconstruire le journey */ 
				LOG.info("Solution found !!");
				
				/* Print solution */
				for (Segment s : solution.getPath()) {
					System.out.println(s);
				}
				
				break ;
			}

			// Update the list of Stops
			stops.get(c.getArrivalId()).setArrivalTime(c.getArrivalTime(), c);
			LOG.info("Updating the list of stops done successfully.");
			
			// Update the list of Connection
			c.spreadReachability() ; // Same trip

			// Update connection which start from nearby stops
			for (Footpath f : footpaths.get(c.getArrivalId())) {
				for (Connection cx : connections.get(f.getArrivalId())) {
					cx.setReachable() ;
				}
				stops.get(f.getArrivalId()).setArrivalTime((int) (f.distance/SPEED), f) ; 
			}
			LOG.info("Updating the list of connection done successfully.");
		}
		
	}

	private void buildJourney(Segment c) {
		StopPoint dep = stops.get(c.getDeparture()) ;
		if (dep == null) return ;
		solution.addSegment(c);
		if (dep.getConnection() == null) return ; /* On est revenu au départ */
		if (dep.getStop().getId().getId().equals(departure_id)) return ; /* On est revenu au départ */
		buildJourney(dep.getConnection());
	}

}

