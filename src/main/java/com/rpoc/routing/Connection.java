package com.rpoc.routing;

import org.onebusaway.gtfs.model.StopTime;

public class Connection extends Segment implements Comparable<Connection> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String trip_id ;
	private String route_id ;
	private Connection c_next ;
	private boolean reachable ;
	
	public Connection(StopTime previous_st, StopTime st) {
		trip_id = st.getTrip().getId().getId() ;
		route_id = st.getTrip().getRoute().getId().getId() ;
		arrival = st.getStop() ;
		departure = previous_st.getStop() ;
		arrival_time = st.getArrivalTime() ;
		departure_time = previous_st.getDepartureTime() ;
		c_next = null ;
		reachable = false ;
	}
	
	String getTripId () {
		return trip_id ;
	}

	String getRouteId () {
		return route_id ;
	}

	int getArrivalTime () {
		return arrival_time ;
	}

	int getDepartureTime () {
		return departure_time ;
	}

	Connection getNextConnection () {
		return c_next ;
	}

	void setNextConnection (Connection c_next) {
		this.c_next = c_next ;
	}

	boolean isReachable () {
		return reachable ;
	}

	void setReachable () {
		reachable = true ;
	}
	
	public int compareTo(Connection o) {
		int aux = departure_time - o.getDepartureTime() ;
		if (aux == 0) {
			aux = departure.getId().getId().compareTo(o.departure.getId().getId()) ;
		}
		if (aux == 0) {
			aux = arrival.getId().getId().compareTo(o.arrival.getId().getId()) ;
		}
		if (aux == 0) {
			aux = trip_id.compareTo(o.trip_id) ;
		} 
		if (aux == 0) {
			aux = route_id.compareTo(o.route_id) ;
		} 
		return aux;
	}

	public void spreadReachability() {
		if (c_next == null) return ;
		c_next.setReachable();
		c_next.spreadReachability();
	}
	


}
