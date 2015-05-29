package com.rpoc.routing;

import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;

@Path("myservice")
public class MyService {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent
	 * to the client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt(@Context Builder builder, @Context UriInfo ui) {

		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
//    	for (String param : queryParams.keySet()) {
//    		for (String value : queryParams.get(param)) {
//    			System.out.println(param + " -> " + value) ;
//    		}
//    	}

		if (queryParams.containsKey("from") && queryParams.containsKey("to") && queryParams.containsKey("time")) {

			System.out.println( "Correct request received -- start routing" );

			builder.reinit () ;
			
			Request request = new Request (queryParams.get("from").get(0), Integer.parseInt(queryParams.get("time").get(0)), queryParams.get("to").get(0)) ;

			Router router = new Router(builder, request) ;       
			router.run_CSA();

			try {
				String json = router.journey2Json() ;
				if (App.DEBUG) {
		        	FileWriter writer = new FileWriter("DEBUG.json");
		    		writer.write(json);
		    		writer.close();
				}
				return json;
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		} 

		return "RFS request error. Usage: from to start_time";

	}
}
