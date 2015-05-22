package com.rpoc.routing;

import org.onebusaway.gtfs.model.Stop;

public class StopPoint {
	
	private Stop s ;
	private int minimum_connection_time ;

	private Segment c ;	
	private int arrival_time ;
	
	public StopPoint (Stop s, int minimum_connection_time) {
		this.s = s ;
		this.minimum_connection_time = minimum_connection_time ;
		this.arrival_time = -1 ;
		this.c = null ;
	}
	
	public Stop getStop () {
		return s ;
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
	
	public void setArrivalTime (int time, Segment c) {
		if (time > arrival_time) return ;
		arrival_time = time ;
		this.c = c ;
	}
	
}
