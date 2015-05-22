package com.rpoc.routing;

import java.util.ArrayList;

public class Journey {

	private ArrayList<Segment> path ;

	public Journey () {
		path = new ArrayList<Segment>() ;
	}
	
	public void addSegment (Segment s) {
		path.add(s) ;
	}
	
	public ArrayList<Segment> getPath () {
		return path ;
	}
	
}
