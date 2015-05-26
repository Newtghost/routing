package com.rpoc.routing;

import java.io.Serializable;

import org.onebusaway.gtfs.model.Stop;

public abstract class Segment  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final double CONNECTION_THRESHOLD = 500; // in meters

	public Stop arrival ;
	public Stop departure ;
	
	Stop getArrival () {
		return arrival ;
	}

	Stop getDeparture () {
		return departure ;
	}

	String getArrivalId () {
		return arrival.getId().getId() ;
	}

	String getDepartureId () {
		return departure.getId().getId() ;
	}
		
	public static double gps2m (double lat_a, double lng_a, double lat_b, double lng_b) {
	    double pk = 180 / Math.PI;

	    double a1 = lat_a / pk;
	    double a2 = lng_a / pk;
	    double b1 = lat_b / pk;
	    double b2 = lng_b / pk;

	    double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
	    double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
	    double t3 = Math.sin(a1)*Math.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);
	   
	    return 6366000 * tt;
	}
	
	public static boolean isConnectionPossible (Stop s1, Stop s2) {
		if (s1.getId().getId().equals(s2.getId().getId())) return false ;
		double dist = gps2m(s1.getLat(), s1.getLon(), s2.getLat(), s2.getLon()) ;
		return dist < CONNECTION_THRESHOLD ;
	}
}
