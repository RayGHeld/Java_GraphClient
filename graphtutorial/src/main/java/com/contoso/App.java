package com.contoso;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.models.extensions.Calendar;

import java.io.Console;
import java.io.IOException;
import java.util.Properties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Graph Tutorial
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Java Graph Tutorial");
        System.out.println();

        // Load OAuth settings
        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }

        final String appId = oAuthProperties.getProperty("app.id");
        final String[] appScopes = oAuthProperties.getProperty("app.scopes").split(",");
        final String authority = oAuthProperties.getProperty("app.authority");
        final String clientSecret = oAuthProperties.getProperty("app.clientSecret");

        // Get an access token
        //Authentication.initialize(appId, authority);j
        Authentication.initialize(appId, authority, clientSecret);
        final String accessToken = Authentication.getUserAccessToken(appScopes);
        System.out.println("Access token = " + accessToken);
        
        String upn = promptForUPN();
        
        // Greet the user
        User user = Graph.getUser(accessToken,upn);
        if(user!=null){
            System.out.println("You have select user " + user.displayName);
        }
                
        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println();
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. Input upn to work with");
            System.out.println("3. Get this users info");
            System.out.println("4. Get this users calender");

            try {
                choice = input.nextInt();
            } catch (InputMismatchException ex) {
                // Skip over non-integer input
                input.nextLine();
            }

            // Process user choice
            switch(choice) {
                case 0:
                    // Exit the program
                    System.out.println("Goodbye...");
                    break;
                case 1:
                    // Display access token
                    System.out.println("Access token: " + accessToken);
                    break;
                case 2:
                    upn = promptForUPN();
        
                    // Greet the user
                    user = Graph.getUser(accessToken,upn);
                    if(user!=null){
                        System.out.println("You have selected user " + user.displayName);
                    }
                    break;

                case 3:
                    if(user!=null){
                        System.out.println("User info:");
                        System.out.println("    id= " + user.id);
                        System.out.println("    mail= " + user.mail);
                    } else {
                        System.out.println("*** No user selected ***");
                    }

                    break;
                case 4:
                    if(user!=null){
                        Calendar cal = Graph.GetCalendar(accessToken,upn);
                        System.out.println("Calendar info:");
                        System.out.println("    id= " + cal.id );                        
                    } else {
                        System.out.println("*** No user selected ***");
                    }

                    break;

                /*        
                case 2:
                    // List the calendar
                    listCalendarEvents(accessToken);
                    break;
                case 3:
                    listCalendarEvents(accessToken, "AAkALgAAAAAAHYQDEapmEc2byACqAC-EWg0AE83QiuJBS02nHrd9EEXPxAABO3weWwAA");
                    break;
                */
                default:
                    System.out.println("Invalid choice");
            }
        }

        input.close();
    }

    private static String formatDateTimeTimeZone(DateTimeTimeZone date) {
        LocalDateTime dateTime = LocalDateTime.parse(date.dateTime);
    
        return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) + " (" + date.timeZone + ")";
    }

    private static String promptForUPN(){
        String upn;
        Console console = System.console();
        upn = console.readLine("Enter user upn: ");
        return upn;
    }

    private static void listCalendarEvents(String accessToken) {
        // Get the user's events
        List<Event> events = Graph.getEvents(accessToken);
    
        System.out.println("Events:");
    
        for (Event event : events) {
            System.out.println("Subject: " + event.subject);
            System.out.println("  Id: " + event.id);
            System.out.println("  Organizer: " + event.organizer.emailAddress.name);
            System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
            System.out.println("  End: " + formatDateTimeTimeZone(event.end));
        }
    
        System.out.println();
    }

    private static void listCalendarEvents(String accessToken, String id){
        Event event = Graph.getCalenderEvent(accessToken, id);
    
        System.out.println("Event:");
    
        System.out.println("Subject: " + event.subject);
        System.out.println("  Id: " + event.id);
        System.out.println("  Organizer: " + event.organizer.emailAddress.name);
        System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
        System.out.println("  End: " + formatDateTimeTimeZone(event.end));
    
        System.out.println();
    }
    
}