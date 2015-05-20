package com.rpoc.routing;

import org.onebusaway.gtfs.model.Stop;

public class Footpath extends Segment {

	public double distance ;

	public Footpath (Stop s1, Stop s2) {
		departure = s1 ;
		arrival = s2 ;
		distance = Segment.gps2m(s1.getLat(), s1.getLon(), s2.getLat(), s2.getLon()) ;
	}

}
