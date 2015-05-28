package com.rpoc.routing;

import java.io.IOException;
import java.net.URI;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class App {
	
    public static final boolean DEBUG = false ;
    
    // Builder
    /* TODO : ce path pourrait être en paramètre de l'app */
    public static final String GTFS_PATH = "C:\\Users\\david.leydier\\otp\\graphs\\gtfs";
	Builder builder = null;

	// Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8079/myapp/";

	public App () {
    	try {
			builder = new Builder (GTFS_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public void startServer() {
		// create a resource config that scans for JAX-RS resources and providers
        // in com.example.simple_service package
        final ResourceConfig rc = new ResourceConfig().packages("com.rpoc.routing");
        rc.register(builder.makeBinder()) ;

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
    	App myapp = new App () ;
    	myapp.startServer();
    }

    
}
