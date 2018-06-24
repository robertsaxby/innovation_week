package com.worldcup;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



public class TwitterStreamToPubSub {

    void run(OAuthConfig oAuthConf, List<String> terms, String topic) throws Exception{

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        // terms to track
        endpoint.trackTerms(terms);

        Authentication auth = new OAuth1(
                oAuthConf.ConsumerKey,
                oAuthConf.ConsumerSecret,
                oAuthConf.AccessToken,
                oAuthConf.AccessTokenSecret
        );

        // Create a BasicClient. By default gzip is enabled.
        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();

        // This could be done on a separate thread or multiple
        try (PublishToPubSub publishToPubSub = new PublishToPubSub(topic)) {

            while (!client.isDone()) {
                String message = queue.take();
                publishToPubSub.put(message);
            }
        }
    }


    public static void main(String[] args){
        try {

            List<String> terms = Lists.newArrayList(
                    "#WorldCup",
                    "#WorldCup2018"
            );

            TwitterStreamToPubSub tstp = new TwitterStreamToPubSub();

            OAuthConfig oAuthConf = new OAuthConfig(args[0], args[1], args[2], args[3]);

            tstp.run(oAuthConf, terms, args[4]);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
