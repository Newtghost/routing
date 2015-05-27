package com.rpoc.routing;

public class Request {
	
	public static final double SPEED = 0.3; /* 0.5 meter per second */
	
	private String departure_id ;
	private String arrival_id ;
	private int start_time ;
	
	public Request(String start_id, int start_time, String stop_id) {
		this.departure_id = start_id ;
		this.arrival_id = stop_id ;
		this.start_time = start_time ;	
	}
	
	public String getDepartureId () {
		return departure_id ;
	}

	public String getArrivalId () {
		return arrival_id ;
	}

	public int getStartTime () {
		return start_time ;
	}
	
}
