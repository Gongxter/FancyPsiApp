package com.seemoo.pis.fancypsiapp.helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by TMZ_LToP on 25.11.2016.
 */

public class TwitterHelper {


    private static final String TWITTER_KEY = "lWm4TKu5pE936hu1YxXFGXiz7";
    private static final String TWITTER_SECRET = "lvGOnapFPdJX1VV5h9iUDYwCIYgHfw1o3AnWhhlo6i3VhGNogG";

    private static final int CHUNK_SIZE = 100;

    public static Twitter appAuth()  throws TwitterException {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        builder.setOAuthConsumerKey(TWITTER_KEY).setOAuthConsumerSecret(TWITTER_SECRET);
        OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(TWITTER_KEY);
        cb.setOAuthConsumerSecret(TWITTER_SECRET);
        cb.setOAuth2TokenType(token.getTokenType());
        cb.setOAuth2AccessToken(token.getAccessToken());

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("followers");
        RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/followers/ids");
        //System.out.println("Remaining followers ids: " + searchTweetsRateLimit.getRemaining());

        Map<String, RateLimitStatus> rateLimitStatus1 = twitter.getRateLimitStatus("friends");
        RateLimitStatus searchTweetsRateLimit1 = rateLimitStatus1.get("/friends/ids");
        //System.out.println("Remaining friends ids: " + searchTweetsRateLimit1.getRemaining());

        return twitter;
    }

    public static List<User> getFollowees(Twitter twitter, String userName) throws TwitterException {
    //TODO: add a return value and return the followees for give username also check the auth keys again
        // List of user followees
        IDs followeesIds = twitter.getFriendsIDs(userName, -1);
        long[] ids = followeesIds.getIDs();

        // Convert long[] to ArrayList<Long>

        List<Long> idsList = new ArrayList<Long>(Arrays.asList(ArrayUtils.toObject(ids)));

        Log.i("TwitterHelper",("size of idsList: " + idsList.size()));

        int listBound = 0;
        List<User> followeesList = null;

        // Get user's followees
        while(!idsList.isEmpty()) {

            // The list of followees can only be retrieved in chunks -> see method lookupUsers
            if(idsList.size() > CHUNK_SIZE) {
                listBound = CHUNK_SIZE;
            } else {
                listBound = idsList.size();
            }

            // Retrieve a list of followees from the Twitter server
            if(followeesList == null) {
                // Convert a part of ids ArrayList<Long> (size = listBound) to corresponding array of long[]
                followeesList = twitter.lookupUsers(ArrayUtils.toPrimitive(idsList.subList(0, listBound).toArray(new Long[0])));
            } else {
                followeesList.addAll(twitter.lookupUsers(ArrayUtils.toPrimitive(idsList.subList(0, listBound).toArray(new Long[0]))));
            }

            // Update the ArrayList<Long> of ids: remove already retrieved ids
            idsList.subList(0, listBound).clear();
        }

        return followeesList;
    }

}
