package com.rpoc.routing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Builder {
	
	private Multimap <String, Connection> connections ; 
	private ArrayList<Connection> sorted_connections ; 
	private Map <String, ArrayList<Footpath>> footpaths ;
	private Map <String, StopPoint> stops ;
	
	private GtfsDaoImpl store ;
	
	public Builder (String path) throws IOException {

		// Read the GTFS
		GtfsReader reader = new GtfsReader();
		reader.setInputLocation(new File(path));

		store = new GtfsDaoImpl();
		reader.setEntityStore(store);
	
		reader.run();
	
		// Create the lists of connections
		connections = ArrayListMultimap.create () ;
		
		// Get and sort the list of stop times to make easier the creation of all connections
		ArrayList<StopTime> stop_times = new ArrayList<StopTime> (store.getAllStopTimes()) ;
		Collections.sort(stop_times, new Comparator<StopTime>( ){
			public int compare(StopTime o1, StopTime o2) {
				if (o1.getTrip().getId().getId().equals(o2.getTrip().getId().getId())) {
					return o1.getStopSequence() - o2.getStopSequence() ;
				}
				else {
					return o1.getTrip().getId().getId().compareTo(o2.getTrip().getId().getId()) ;
				}
			}			
		} );
		
		// Access entities through the store to create connections
		StopTime previous_st = null ;
		for (StopTime st : stop_times) {
			// Initialization
			if (previous_st == null) previous_st = st ;			
			// The same trip
			if (st.getTrip().getId().getId().equals(previous_st.getTrip().getId().getId())) {
				// The next stop
				if (st.getStopSequence() == previous_st.getStopSequence() + 1) {
					// Create a new connection
					connections.put(previous_st.getStop().getId().getId(), new Connection(previous_st, st));
					// TODO : initialiser les c_next ici 
				}
			}
			
			previous_st = st ;
		}
		
		// Création d'une liste des mêmes connections mais triée
		sorted_connections = new ArrayList<Connection>() ;
		for (String k : connections.keySet()) {
			sorted_connections.addAll(connections.get(k)) ;
		}
		Collections.sort(sorted_connections);

		// Create footpaths and stops lists
		footpaths = new HashMap<String, ArrayList<Footpath>> () ;
		stops = new HashMap<String, StopPoint> () ;

		for (Stop s1 : store.getAllStops()) {
			ArrayList<Footpath> neighbours = new ArrayList<Footpath> () ;
			for (Stop s2 : store.getAllStops()) {
				if (Segment.isConnectionPossible (s1, s2)) {
					neighbours.add(new Footpath(s1, s2)) ;
				}
			}
			footpaths.put(s1.getId().getId(), neighbours) ;
			stops.put(s1.getId().getId(), new StopPoint(s1, 0));
		}

		
	}
	
	/* Renvoie la liste des connections triée : plus simple pour le déroulement de l'algo */
	ArrayList<Connection> getSortedConnections() {
		return sorted_connections ;
	}

	/* Renvoie la table de hâchage : plus simple pour mettre à jour les connections */
	Multimap <String, Connection> getConnections() {
		return connections ;
	}
	
	Map<String, ArrayList<Footpath>> getFootpaths() {
		return footpaths ;
	}

	Map<String, StopPoint> getStops() {
		return stops ;
	}

}
