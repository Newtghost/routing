package com.rpoc.routing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.serialization.GtfsReader;

public class ConnectionsList {
	
	private ArrayList<Connection> connections ;
	
	public ConnectionsList (String path) throws IOException {

		// Read the GTFS
		GtfsReader reader = new GtfsReader();
		reader.setInputLocation(new File(path));

		GtfsDaoImpl store = new GtfsDaoImpl();
		reader.setEntityStore(store);
	
		reader.run();
	
		// Create the list of connections
		connections = new ArrayList<Connection> () ;
		
		// Access entities through the store to create connections
		// The stop_times.txt needs to be sorted by trip_id and stop_sequence
		StopTime previous_st = null ;
		for (StopTime st : store.getAllStopTimes()) {
			// Initialize
			if (previous_st == null) previous_st = st ;			

			if (st.getTrip().getId().getId().equals(previous_st.getTrip().getId().getId())) {
				if (st.getStopSequence() == previous_st.getStopSequence() + 1) {
					// Create a new connection
					connections.add(new Connection(previous_st, st));
				}
			}
			
			previous_st = st ;
		}
		
		// Sorting the list
		Collections.sort(connections);
	}
	
	ArrayList<Connection> getList() {
		return connections ;
	}
		
}
