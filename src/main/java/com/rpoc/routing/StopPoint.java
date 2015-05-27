package com.rpoc.routing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StopPoint implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String stop_id ;
	private int minimum_connection_time ;

	/* Ensemble des chemins pareto-opt qui ont abouti à ce stop point */
	private Map <Integer, ArrayList<Path>> paths ;

	private int arrival_time ; /* Temps d'arrivée au plus tôt */
	public boolean marked = false ;
	
	public StopPoint (String stop_id, int minimum_connection_time) {
		this.stop_id = stop_id ;
		this.minimum_connection_time = minimum_connection_time ;
		arrival_time = -1 ;
		paths = new HashMap<Integer, ArrayList<Path>> () ;
	}
	
	public String getStopId () {
		return stop_id ;
	}
	
	public int getMinimumConnectionTime () {
		return minimum_connection_time ;
	}
	
	public boolean addPaths (StopPoint stop, Segment s, int best_time) {
		boolean res = false ;
		if (stop.paths.isEmpty()) { /* Initialization */
			if (best_time > 0 && s.departure_time > best_time) return res ; /* Optimization */
			addPath(s.departure_time, s.arrival_time - s.departure_time, 0, s, "") ;
			res = true ;
		} else {
			for (Integer i : stop.paths.keySet()) {
				if (best_time > 0 && i > best_time) continue ; /* Optimization */
				for (Path p : stop.paths.get(i)) {
					if (p.getSegment() instanceof Footpath && s instanceof Footpath) continue ; /* On ne peut pas enchainer deux footpaths */
					res = res || addPath(i.intValue(), p.getTravelTime() + (s.arrival_time - s.departure_time), p.getNbTrips(), s, p.getTripId()) ;
				}
			}
		}
		return res ;
	}

	@SuppressWarnings("unchecked")
	private boolean addPath (int departure_time, int travel_time, int nb_trips, Segment s, String trip_id) {
		if (!paths.isEmpty() && paths.containsKey(departure_time)) {
			Path np = new Path(travel_time, nb_trips, s, trip_id) ;
			for (Path p : (ArrayList<Path>) paths.get(departure_time).clone()) {
				if (np.dominate(p)) {
					paths.get(departure_time).remove(p) ;
				} else if (p.dominate(np)) {
					return false;
				}
				/* else pareto-opt */
			}
			paths.get(departure_time).add(np) ;
			
		} else {
			paths.put(departure_time, new ArrayList<Path> (Arrays.asList(new Path(travel_time, nb_trips, s, trip_id)))) ;
			/* Mise à jour du temps d'arrivée au plus tôt */
			if (arrival_time < 0 || s.arrival_time < arrival_time) arrival_time = s.arrival_time ; 
		}
		return true ;
	}
	
	public int getArrivalTime () {
		return arrival_time ;
	}

	public void setArrivalTime (int arrival_time) {
		if (this.arrival_time < 0 || arrival_time < this.arrival_time) this.arrival_time = arrival_time ;
	}

	public Map<Integer, ArrayList<Path>> getPaths() {
		return paths;
	}

	public Segment getSegment(int departure_time) {
		if (!paths.containsKey(departure_time)) return null ;
		return paths.get(departure_time).get(0).getSegment();
	}
}
