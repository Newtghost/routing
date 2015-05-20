package com.rpoc.routing;

import org.onebusaway.gtfs.model.StopTime;

public class Connection implements Comparable<Connection> {
	
	private String trip_id ;
	private String arrival_id ;
	private String departure_id ;
	private int departure_time ;
	private int arrival_time ;
	
	public Connection(StopTime previous_st, StopTime st) {
		trip_id = st.getTrip().getId().getId() ;
		arrival_id = st.getStop().getId().getId() ;
		departure_id = previous_st.getStop().getId().getId() ;
		arrival_time = st.getArrivalTime() ;
		departure_time = previous_st.getDepartureTime() ;
	}
	
	String getTripId () {
		return trip_id ;
	}

	String getArrivalId () {
		return arrival_id ;
	}

	String getDepartureId () {
		return departure_id ;
	}

	int getArrivalTime () {
		return arrival_time ;
	}

	int getDepartureTime () {
		return departure_time ;
	}

	public int compareTo(Connection o) {
		return departure_time - o.getDepartureTime();
	}

}
