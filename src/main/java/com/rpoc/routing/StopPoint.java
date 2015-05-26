package com.rpoc.routing;

import java.io.Serializable;

public class StopPoint implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String stop_id ;
	private int minimum_connection_time ;

	private Segment c ;	
	private int arrival_time ;
	public boolean marked = false ;
	
	public StopPoint (String stop_id, int minimum_connection_time) {
		this.stop_id = stop_id ;
		this.minimum_connection_time = minimum_connection_time ;
		this.arrival_time = -1 ;
		this.c = null ;
	}
	
	public String getStopId () {
		return stop_id ;
	}
	
	public int getMinimumConnectionTime () {
		return minimum_connection_time ;
	}
	
	public int getArrivalTime () {
		return arrival_time ;
	}

	public Segment getConnection () {
		return c ;
	}
	
	public boolean setArrivalTime (int time, Segment c) {
		if (arrival_time > 0 && time > arrival_time) {
			return false ;
		}
		arrival_time = time ;
		this.c = c ;
		return true ;
	}
	
}
