package com.rpoc.routing;

import org.onebusaway.gtfs.model.Stop;

public class StopPoint {
	
	private Stop s ;
	private int minimum_connection_time ;
	
	private int arrival_time ;
	
	public StopPoint (Stop s, int minimum_connection_time) {
		this.s = s ;
		this.minimum_connection_time = minimum_connection_time ;
		this.arrival_time = -1 ;
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
	
	public void setArrivalTime (int time) {
		arrival_time = time ;
	}
	
}
