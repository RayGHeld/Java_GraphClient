package com.contoso;

import java.net.MalformedURLException;
// import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.security.auth.login.Configuration.Parameters;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.IPublicClientApplication;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;


/**
 * Authentication
 */
public class Authentication {

    private static String applicationId;
    // Set authority to allow only organizational accounts
    // Device code flow only supports organizational accounts
    private static String authority;
    private static String clientSecret;

    /*
     * public static void initialize(String applicationId, String authority) {
     * Authentication.applicationId = applicationId; Authentication.authority =
     * authority; }
     */

    public static void initialize(String applicationId, String authority, String clientSecret) {
        Authentication.authority = authority;
        Authentication.applicationId = applicationId;
        Authentication.clientSecret = clientSecret;
    }

    public static String getUserAccessToken(String[] scopes) {
        if (applicationId == null) {
            System.out.println("You must initialize Authentication before calling getUserAccessToken");
            return null;
        }

        
        //Set<String> scopeSet = Set.of(scopes);
        //Set<String> = convertArrayToSet(scopes);
        Set<String> scopeSet = new HashSet<>();
        
        Collections.addAll(scopeSet, scopes);
        /*
        PublicClientApplication app;
        try {
            // Build the MSAL application object with
            // app ID and authority
            app = PublicClientApplication.builder(applicationId)
                .authority(authority)
                .build();
        } catch (MalformedURLException e) {
            return null;
        }

        
        // Create consumer to receive the DeviceCode object
        // This method gets executed during the flow and provides
        // the URL the user logs into and the device code to enter
        Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> {
            // Print the login information to the console
            System.out.println(deviceCode.message());
        };

        // Request a token, passing the requested permission scopes
        IAuthenticationResult result = app.acquireToken(
            DeviceCodeFlowParameters
                .builder(scopeSet, deviceCodeConsumer)
                .build()
        ).exceptionally(ex -> {
            System.out.println("Unable to authenticate - " + ex.getMessage());
            return null;
        }).join();
        */

        // From https://docs.microsoft.com/en-us/azure/active-directory/develop/scenario-daemon-acquire-token?tabs=java#acquiretokenforclient-api
		IClientCredential cred = ClientCredentialFactory.createFromSecret(clientSecret);
        ConfidentialClientApplication app;
        try {
            // Build the MSAL application object for a client credential flow
            app = ConfidentialClientApplication.builder(applicationId, cred ).authority(authority).build();
        } catch (MalformedURLException e) {
            System.out.println("Error creating confidential client: " + e.getMessage());
            return null;
        }
        
        IAuthenticationResult result;
        try{
            SilentParameters silentParameters = SilentParameters.builder(scopeSet).build();
            result= app.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex ){
            if (ex.getCause() instanceof MsalException) {

                ClientCredentialParameters parameters =
                        ClientCredentialParameters
                                .builder(scopeSet)
                                .build();
   
                // Try to acquire a token. If successful, you should see
                // the token information printed out to console
                result = app.acquireToken(parameters).join();
            } else {
                // Handle other exceptions accordingly
                System.out.println("Unable to authenticate = " + ex.getMessage());
                return null;
            }
        }


        if (result != null) {
            // System.out.println("Access Token - " + result.accessToken());
            return result.accessToken();
        }

        return null;
    }
}