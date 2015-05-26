package com.rpoc.routing;

import java.io.IOException;

public class App {
	
    public static void main( String[] args ) throws IOException {
        System.out.println( "My first app creating a list of connections -- start" );
        
		if (args.length != 1) {
			System.err.println("usage: gtfs_feed_path");
			System.exit(-1);
		}
	
        Builder builder = new Builder (args[0]);
        
        Request request = new Request ("3932", 0, "2391") ;
        
        Router router = new Router(builder, request) ; // 0 means earliest is the best        
        router.run_CSA();
    }
    
}
