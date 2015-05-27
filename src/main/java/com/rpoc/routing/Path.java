package com.rpoc.routing;

public class Path {
	
	private int travel_time ;
	private int nb_trips ;
	private String trip_id ;
	private Segment s ;

	public Path (int travel_time, int nb_trips, Segment s, String trip_id) {
		this.travel_time = travel_time ;
		this.nb_trips = nb_trips ;
		this.trip_id = trip_id ;
		if (s instanceof Connection) {
			if(!((Connection) s).getTripId().equals(trip_id)) this.nb_trips ++ ;
		} 
		this.s = s ;
	}

	public int getTravelTime () {
		return travel_time ;
	}
	
	public int getNbTrips () {
		return nb_trips ;
	}	

	public Segment getSegment () {
		return s ;
	}

	public String getTripId () {
		return trip_id ;
	}

	public boolean dominate(Path p) {
		return travel_time <= p.travel_time && nb_trips <= p.nb_trips ; /* TODO : a revoir / plusieurs fois le meme ? */
	}	

}
