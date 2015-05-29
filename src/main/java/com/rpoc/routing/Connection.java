package com.rpoc.routing;

import org.onebusaway.gtfs.model.StopTime;

public class Connection extends Segment implements Comparable<Connection> {
	
	private static final long serialVersionUID = 1L;
	
	private String trip_id ;
	private String route_id ;
	private Connection c_next ;
	private boolean reachable ;
	private int arrival_delay ;
	private int departure_delay ;
	private int departure_sequence ;
	private int arrival_sequence ;
	
	public Connection(StopTime previous_st, StopTime st) {
		trip_id = st.getTrip().getId().getId() ;
		route_id = st.getTrip().getRoute().getId().getId() ;
		arrival = st.getStop() ;
		departure = previous_st.getStop() ;
		arrival_time = st.getArrivalTime() ;
		departure_time = previous_st.getDepartureTime() ;
		departure_sequence = previous_st.getStopSequence() ;
		arrival_sequence = st.getStopSequence() ;
		arrival_delay = 0 ;
		departure_delay = 0 ;
		c_next = null ;
		reachable = false ;
	}
	
	public String getTripId () {
		return trip_id ;
	}

	public String getRouteId () {
		return route_id ;
	}

	public Connection getNextConnection () {
		return c_next ;
	}

	public void setNextConnection (Connection c_next) {
		this.c_next = c_next ;
	}

	public boolean isReachable () {
		return reachable ;
	}

	public void setReachable () {
		reachable = true ;
	}
	
	public int getDepartureDelay () {
		return departure_delay ;
	}
	
	public int getArrivalDelay () {
		return arrival_delay ;
	}

	public int getDepartureSequence () {
		return departure_sequence ;
	}
	
	public int getArrivalSequence () {
		return arrival_sequence ;
	}

	public int compareTo(Connection o) {
		int aux = (int) (departure_time - o.getDepartureTime()) ;
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
