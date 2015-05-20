package com.rpoc.routing;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Router {
	
    private static final Logger LOG = LoggerFactory.getLogger(Router.class);
    
	private String departure_id ;
	private String arrival_id ;
	private int start_time ;
	
	private Map <String, Connection> connections ;
	private Map <String, ArrayList<Footpath>> footpaths ;
	private Map <String, StopPoint> stops ;
	
	public Router (Builder builder, String start_id, int start_time, String stop_id) {
		this.departure_id = start_id ;
		this.arrival_id = stop_id ;
		this.start_time = start_time ;		
		connections = builder.getConnections() ;
		footpaths = builder.getFootpaths() ;
		stops = builder.getStops() ;
	}
	
	public void run_CSA () {

		for (Footpath f : footpaths.get(departure_id)) {
			connections.get(f.getArrivalId()).setReachable();
		}

		for (Connection c : connections.values()) {

			if (c.getDepartureTime() < start_time) continue ;
			if (! c.isReachable()) continue ;

			LOG.info("Connection reachable found");

			if (c.getArrivalId().equals(arrival_id)) {
				LOG.info("Solution found !!");
				break ;
			}
			
			// TODO : construire un ou plusieurs journey
			// TODO : comment remonter ? Est ce qu'on travaille sur les segments ou sur les StopPoints ?
			
			// Update the list of Stops
			stops.get(c.getArrivalId()).setArrivalTime(c.getArrivalTime());
			LOG.info("Updating the list of stops done successfully.");
			
			// Update the list of Connection
			c.spreadReachability() ; // Same trip
			// Nearby stops
			for (Footpath f : footpaths.get(c.getArrival())) {
				connections.get(f.getArrivalId()).setReachable(); // TODO : comment faire ca simplement si plus de map ???
			}
			LOG.info("Updating the list of connection done successfully.");
		}
		
	}

}

