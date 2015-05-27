package com.rpoc.routing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;

public class Router {
	
    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

	private Request request ;
	
	private Multimap <String, Connection> connections ; 
	private ArrayList<Connection> sorted_connections ; 
	private Map <String, ArrayList<Footpath>> footpaths ;
	private Map <String, StopPoint> stops ;
	
	private Journey solution ;
	
	public Router (Builder builder, Request request) {
		this.request = request ;	
		connections = builder.getConnections() ;
		sorted_connections = builder.getSortedConnections() ;
		footpaths = builder.getFootpaths() ;
		stops = builder.getStops() ;
		solution = new Journey () ;
	}
	
	public void run_CSA () {

		LOG.info("Start computing solutions.");
		
		int best_time = -1 ; /* Best time found to go from the source to the target */

		/* Initialization */
		updateAccessibleConnections (request.getDepartureId(), request.getStartTime(), best_time) ;
		stops.get(request.getDepartureId()).setArrivalTime(request.getStartTime());

		/* Core of the algorithm */
		for (Connection c : sorted_connections) {

			/* A connection has to be reachable if we want to take it */
			if (! c.isReachable()) continue ;
			
			/* We can't take a connection if we'll not be on time at the departure */
			if (c.getDepartureTime() < stops.get(c.departure.getId().getId()).getArrivalTime() + 
					stops.get(c.departure.getId().getId()).getMinimumConnectionTime()) continue ;

			if (App.DEBUG) LOG.info("Connection reachable found");
			
			// Update the list of stops since we take this connection
			if (! stops.get(c.getArrivalId()).addPaths(stops.get(c.getDepartureId()), c, best_time)) {
				/* Useless connection */
				continue;
			} else {
				if (App.DEBUG) LOG.info("Updating the list of stops done successfully.");
			}
			
			if (c.getArrivalId().equals(request.getArrivalId())) {
				LOG.info("Solution found.");
				best_time = stops.get(c.getArrivalId()).getArrivalTime() ; /* Usefull for an optimization */
			}
			
			// Update the list of connections : which connections became reachable
			updateAccessibleConnections(c.getArrivalId(), c.getArrivalTime(), best_time) ; /* connections which start from the stop and the nearby stops */
			c.spreadReachability() ; /* connections which are on the same trip */
			if (App.DEBUG) LOG.info("Updating the list of connection done successfully.");
		}

		buildJourney () ;
		
	}

	private void updateAccessibleConnections(String id, int time, int best_time) {
		for (Connection cx : connections.get(id)) { /* Les connections accessibles depuis le stop id */
			cx.setReachable() ;
		}
		for (Footpath f : footpaths.get(id)) { /* Les stops accessibles depuis le stop id */
			for (Connection cx : connections.get(f.getArrivalId())) { /* Les connections accessibles depuis ces stops */
				cx.setReachable() ;
			}
			stops.get(f.getArrivalId()).setArrivalTime(time + f.arrival_time); ; 
		}
	}

	private void buildJourney() {

		Map<Integer, ArrayList<Path>> paths = stops.get(request.getArrivalId()).getPaths() ;
		
		for (Integer i : paths.keySet()) {
			for (Path p : paths.get(i)) {
				System.out.println("Departure at " + i.intValue() + " ; travel time = " + p.getTravelTime() + " ; number of trips = " + p.getNbTrips()) ;
			}
		}	

		/* Export to a Json file */
//		try {
//			journey2Json () ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}				
	}

	private void journey2Json() throws IOException {
		if (!App.DEBUG) printJourney() ;
    	Gson gson = new Gson();
    	String json = gson.toJson(solution.toString());
		FileWriter writer = new FileWriter("Plan.json");
		writer.write(json);
		writer.close();
        LOG.info("Json created successfully !");            
	}

	private void printJourney() {
		Connection c_prev = null ;
		for (Segment s : solution.getPath()) {
			if (s instanceof Footpath) {
				System.out.println("Footpath : " + s.getDepartureId() + " --> " + s.getArrivalId());
				c_prev = null ;
			} else {
				Connection c = (Connection) s ;
				if (c_prev == null || ! c.getTripId().equals(c_prev.getTripId())) {
					System.out.println("Transit : " + c.getRouteId() + ", " + c.getTripId());
				}
				System.out.println("\t" + c.getDepartureId() + " (" + c.getDepartureTime() + "), " + c.getArrivalId() + " (" + c.getArrivalTime() + ")");
				c_prev = c ;
			}
		}		
	}

}

