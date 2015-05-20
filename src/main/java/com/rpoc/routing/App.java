package com.rpoc.routing;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
	
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws IOException {
        System.out.println( "My first app creating a list of connections -- start" );
        
		if (args.length != 1) {
			System.err.println("usage: gtfs_feed_path");
			System.exit(-1);
		}
	
        Builder builder = new Builder (args[0]);
        
        LOG.info("List created succesfully with " + builder.getConnections().size() + " connections.");
        LOG.info("List created succesfully with " + builder.getFootpaths().size() + " footpaths.");
        
        // TODO : trouver un moyen de stocker tout ça et le recharger pour ne pas le recomputer à chaque fois
        
        Router router = new Router(builder, "4818", 0, "1299") ; // 0 means earliest is the best        
        router.run_CSA();
    }
    
}
