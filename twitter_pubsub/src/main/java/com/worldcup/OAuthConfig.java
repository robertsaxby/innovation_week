package com.worldcup;

public final class OAuthConfig{
    public String ConsumerKey;
    public String ConsumerSecret;
    public String AccessToken;
    public String AccessTokenSecret;


    OAuthConfig(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret){
        this.ConsumerKey = consumerKey;
        this.ConsumerSecret = consumerSecret;
        this.AccessToken = accessToken;
        this.AccessTokenSecret = accessTokenSecret;
    }

}