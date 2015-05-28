package com.rpoc.routing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

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

		/* Initialization */
		updateAccessibleConnections (request.getDepartureId(), request.getStartTime()) ;
		stops.get(request.getDepartureId()).setArrivalTime(request.getStartTime(), null);

		/* Core of the algorithm */
		for (Connection c : sorted_connections) {

			if (! c.isReachable()) continue ;
			
			/* 
			 * Consistence des temps :
			 * On ne peut pas prendre une connection si on n'est pas à temps au départ
			 */
			if (c.getDepartureTime() < stops.get(c.departure.getId().getId()).getArrivalTime()) continue ;

			if (App.DEBUG) LOG.info("Connection reachable found");
			
			if (c.getArrivalId().equals(request.getArrivalId())) {
				LOG.info("Solution found.");
				buildJourney (c) ;
				break ;
			}

			// Update the list of stops
			if (! stops.get(c.getArrivalId()).setArrivalTime(c.getArrivalTime(), c)) continue;
			if (App.DEBUG) LOG.info("Updating the list of stops done successfully.");
			
			// Update the list of connections
			updateAccessibleConnections(c.getArrivalId(), c.getArrivalTime()) ; /* connections which start from the stop and the nearby stops */
			c.spreadReachability() ; /* connections which are on the same trip */
			if (App.DEBUG) LOG.info("Updating the list of connection done successfully.");
		}
		
	}

	private void updateAccessibleConnections(String id, int time) {
		for (Connection cx : connections.get(id)) { /* Les connections accessibles depuis le stop id */
			cx.setReachable() ;
		}
		for (Footpath f : footpaths.get(id)) { /* Les stops accessibles depuis le stop id */
			for (Connection cx : connections.get(f.getArrivalId())) { /* Les connections accessibles depuis ces stops */
				cx.setReachable() ;
			}
			stops.get(f.getArrivalId()).setArrivalTime((int) (f.distance/request.getSpeed()) + time, f) ; 
		}
	}

	private void buildJourney(Segment c) {
		StopPoint dep ;
		Segment aux = c;
		while (true) {
			solution.addSegment(aux); /* On rajoute le segment dans la solution */
			dep = stops.get(aux.getDepartureId()) ;
			if (dep.marked) { 
				LOG.error("There is a loop in the journey");
				break ;
			}
			dep.marked = true ;
			if (dep.getConnection() == null) break ; /* On est revenu au départ */
			aux = dep.getConnection() ;
		}		
	}

	@SuppressWarnings("unchecked")
	public String journey2Json() throws IOException, JSONException {
		if (App.DEBUG) printJourney() ;

		JSONObject obj = new JSONObject();
		JSONObject plan = new JSONObject();
		JSONArray itineraries = new JSONArray();
		JSONObject itinerary = new JSONObject();
		JSONArray legs = new JSONArray();
		JSONObject leg = null;

		Connection c_prev = null ;
		for (Segment s : solution.getPath()) {
			if (s instanceof Footpath) {

				if (leg != null) {
					/* Add the current leg corresponding to transit path */
					JSONObject to = new JSONObject() ;
					to.put("name", c_prev.getArrivalId()) ;
					to.put("stopSequence", 0) ;
					leg.put("to", to) ;
					leg.put("endTime", c_prev.getArrivalTime()) ;
					leg.put("arrivalDelay", 0) ;
					leg.put("distance", 0) ;
					legs.add(leg) ;
				}

				/* New leg corresponding to a footpath */
				leg = new JSONObject() ;
				JSONObject from = new JSONObject() ;
				JSONObject to = new JSONObject() ;
				from.put("name", s.getDepartureId()) ;
				to.put("name", s.getArrivalId()) ;
				leg.put("from", from) ;
				leg.put("to", to) ;
				leg.put("startTime", 0) ;
				leg.put("endTime", 0) ;
				leg.put("departureDelay", 0) ;
				leg.put("arrivalDelay", 0) ;
				leg.put("distance", ((Footpath) s).distance) ;
				leg.put("mode", "WALK") ;
				legs.add(leg);
				leg = null ;

			} else {
				
				Connection c = (Connection) s ;
				
				if (c_prev == null || ! c.getTripId().equals(c_prev.getTripId())) {
					if (leg != null) {
						/* Add the current leg corresponding to transit path before to create a new one */
						JSONObject to = new JSONObject() ;
						to.put("name", c_prev.getArrivalId()) ;
						to.put("stopSequence", 0) ;
						leg.put("to", to) ;
						leg.put("endTime", c_prev.getArrivalTime()) ;
						leg.put("arrivalDelay", 0) ;
						leg.put("distance", 0) ;
						legs.add(leg) ;
					}
					
					/* Creation of a new transit path */
					leg = new JSONObject() ;
					JSONObject from = new JSONObject() ;
					from.put("name", c.getDepartureId()) ;
					from.put("stopSequence", 0) ;
					leg.put("from", from) ;
					leg.put("startTime", c.getDepartureTime()) ;
					leg.put("departureDelay", 0) ;
					leg.put("mode", "TRANSIT") ;
					leg.put("routeId", c.getRouteId()) ;
					leg.put("agencyId", "") ;
					leg.put("tripId", c.getTripId()) ;

				}
				
				c_prev = c ;
			}
		}
		
		itinerary.put("legs", legs) ;
		itineraries.add(itinerary) ;
		plan.put("itineraries", itineraries) ;
		obj.put("plan", plan) ;
		
		String json = obj.toString();
		FileWriter writer = new FileWriter("RFSPlan.json");
		writer.write(json);
		writer.close();
        LOG.info("Json created successfully !");  
        
        return json ;
	}

	private void printJourney() {
		Connection c_prev = null ;
		for (Segment s : solution.getPath()) {
			if (s instanceof Footpath) {
				System.out.println("Footpath : " + s.getDepartureId() + " --> " + s.getArrivalId());
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

