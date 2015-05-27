package com.rpoc.routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Builder {
	
    private static final boolean REBUILD = false ;	

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
	private Multimap <String, Connection> connections ; 
	private Map <String, ArrayList<Footpath>> footpaths ;
	private Map <String, StopPoint> stops ;
	private ArrayList<Connection> sorted_connections ; 
		
	@SuppressWarnings("unchecked")
	public Builder (String path) throws IOException {

        // Read the GTFS
		GtfsReader reader = new GtfsReader();
		reader.setInputLocation(new File(path));

		GtfsDaoImpl store = new GtfsDaoImpl();
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
		Connection previous_c = null, c = null ;
		for (StopTime st : stop_times) {
			// Initialization
			if (previous_st == null) previous_st = st ;			
			// The same trip
			if (st.getTrip().getId().getId().equals(previous_st.getTrip().getId().getId())) {
				// The next stop
				if (st.getStopSequence() == previous_st.getStopSequence() + 1) {
					// Create a new connection
					c = new Connection(previous_st, st) ;
					connections.put(previous_st.getStop().getId().getId(), c);
					if (previous_c != null) previous_c.setNextConnection(c); ;
					previous_c = c ;						
				} else previous_c = null ;
			} else previous_c = null ;

			previous_st = st ;
		}
		
        LOG.info("List of connections created successfully.");

        if (REBUILD) {
			
	        LOG.info("Start creating all the lists.");

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
				stops.put(s1.getId().getId(), new StopPoint(s1.getId().getId(), 0));
			}

			LOG.info("List of footpaths created successfully.");
	        LOG.info("List of stops created successfully.");

			try {
				
				FileOutputStream fos = new FileOutputStream("lists.obj");
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(footpaths); /* Write footpaths */
				oos.writeObject(stops); /* Write stops */
				
				oos.close();
				fos.close();

			} catch(IOException ioe) {
				ioe.printStackTrace();
			}

	        LOG.info("All lists saved successfully.");

		} else {
			try {
				
		        LOG.info("Start loading all datas.");

				FileInputStream fis = new FileInputStream("lists.obj");
				ObjectInputStream ois = new ObjectInputStream(fis);

				/* Read footpaths */
				footpaths = (HashMap<String, ArrayList<Footpath>>) ois.readObject();
		        LOG.info("List of footpaths loaded successfully.");

				/* Read stops */
				stops = (HashMap<String, StopPoint>) ois.readObject();
		        LOG.info("List of stops loaded successfully.");

		        ois.close();
				fis.close();
			
			} catch(IOException ioe) {
				ioe.printStackTrace();
				return;
			} catch(ClassNotFoundException ce) {
				System.out.println("Class not found");
				ce.printStackTrace();
				return;
			}
		}		

		// Création d'une liste des mêmes connections mais triée
		sorted_connections = new ArrayList<Connection>() ;
		for (String k : connections.keySet()) {
			sorted_connections.addAll(connections.get(k)) ;
		}
		Collections.sort(sorted_connections);

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
