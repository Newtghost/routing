package com.rpoc.routing;

import org.onebusaway.gtfs.model.Stop;

public class Footpath extends Segment {

	private static final long serialVersionUID = 1L;
	
	public double distance ;

	public Footpath (Stop departure_stop, Stop arrival_stop) {
		departure = departure_stop ;
		arrival = arrival_stop ;
		
		/* Calcul de la distance */
		distance = Segment.gps2m(departure.getLat(), departure.getLon(), arrival.getLat(), arrival.getLon()) ;
		
		departure_time = 0 ;
		arrival_time = (long) (distance/Request.SPEED) ;
	}
	
	public void update (Connection previous, long start_time) {
		if (previous == null ) 
			departure_time = start_time ;
		else 
			departure_time = previous.departure_time ;
		
		arrival_time = departure_time + (long) (distance/Request.SPEED) ;
	}

}
