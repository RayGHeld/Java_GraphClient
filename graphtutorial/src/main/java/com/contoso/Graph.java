package com.contoso;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;

import java.util.LinkedList;
import java.util.List;

import com.microsoft.graph.models.extensions.Calendar;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
/**
 * Graph
 */
public class Graph {

    private static IGraphServiceClient graphClient = null;
    private static SimpleAuthProvider authProvider = null;

    private static void ensureGraphClient(String accessToken) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = new SimpleAuthProvider(accessToken);

            // Create default logger to only log errors
            DefaultLogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.ERROR);

            // Build a Graph client
            graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .logger(logger)
                .buildClient();
        }
    }

    public static User getUser(String accessToken, String upn) {
        ensureGraphClient(accessToken);

        // GET /me to get authenticated user
        /*
        User me = graphClient
            .me()
            .buildRequest()
            .get();
        */
        try{

            User user = graphClient
                .users(upn)
                .buildRequest()
                .get();

            return user;       
        } catch ( Exception ex ) {
            System.out.println("Error getting user " + ex.getMessage());
            return null;
        }

    }

    public static Calendar GetCalendar(String accessToken, String upn){
        ensureGraphClient(accessToken);

        try {
            Calendar cal = graphClient
                .users(upn)
                .calendar()
                .buildRequest()
                .get();

            return cal;
        } catch (Exception ex){
            System.out.println("Error getting calendar " + ex.getMessage());
            return null;
        }


    }

    public static List<Event> getEvents(String accessToken) {
        ensureGraphClient(accessToken);
    
        // Use QueryOption to specify the $orderby query parameter
        final List<Option> options = new LinkedList<Option>();
        // Sort results by createdDateTime, get newest first
        options.add(new QueryOption("orderby", "createdDateTime DESC"));
        //options.add(new QueryOption("filter", "organizer eq 'ray@myemail.com'"));

        options.add(new HeaderOption("maxRetries","5"));
        options.add(new HeaderOption("Prefer","IdType='ImmutableId'"));
        
        
        // GET /me/events
        IEventCollectionPage eventPage = graphClient
            .me()
            .events()
            .buildRequest(options)
            .select("id,subject,organizer,start,end")
            .get();    
       
        return eventPage.getCurrentPage();
    }

    public static Event getCalenderEvent(String accessToken, String id){
        final List<Option> options = new LinkedList<Option>();
        options.add(new HeaderOption("maxRetries","5"));
        options.add(new HeaderOption("Prefer","IdType='ImmutableId'"));

        Event event = graphClient
            .me()
            .events(id)
            .buildRequest()
            .select("id,subject,organizer,start,end")
            .get();
        return event;
    }
    
}