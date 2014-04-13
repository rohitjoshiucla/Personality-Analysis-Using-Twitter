/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.commons.lang3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import net.sf.json.*;

/**
 *
 * @author rohit_000
 */
public class testTwitterAPI {
   /**
     * @param args the command line arguments
     */
        //static String AccessToken ="1368765691-CK5uI4TyQUL5pQFfIUxT52GIb88ihthkFgvADNV";
        //static String AccessSecret = "OEw1Vuyi2BjHwHvxZjSwT6G1j3zy5lrD61fYcqAx4ls";
        //static String ConsumerKey = "Z2MJxkErRf15afnautd2w";
        //static String ConsumerSecret = "z6OvdnhTAUeio6Q3oQozdvybxtju3cdr8Cn0JwywXI";
        
        static String AccessToken ="1368765691-5psuTFZTZ4WhzOD3y46kTLwJpLqtc6ToU5oYco9";
        static String AccessSecret = "NdNubll9qQzIMZBeBGfRM2wMnxZjr1ThcqFKhzaXiAmHj";
        static String ConsumerKey = "InspcUqL1BllUxvd78Y0Q";
        static String ConsumerSecret = "NwNWjKA1RDNcNKKcwC0UtaRNcC9Edy1zOgxFmhwzd8";
        
        static OAuthConsumer consumer;
        
    public static void main(String[] args) throws Exception{
        // TODO code application logic here        
        consumer = new CommonsHttpOAuthConsumer(ConsumerKey,ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken,AccessSecret);
        FriendFriendStdAttention();                
    }
    
    public static void FriendFriendStdAttention() throws Exception
    {
    //FF
    //collect list of friend ids
    //collect tweets of friends
    //identify the tweets which are retweeted - R
    //identify the tweets which are retweeted by you - RU
       
        //initialization
        HttpGet request;
        HttpClient client;
        HttpResponse response;
        
        int statusCode;
        int FriendTweetRetweetCount;
        int iteratorFriendTweet;
                
        String url;
        String strFriendIds;
        String strFriendTweets;
                
        JSONObject json;
        JSONArray jsonArray;
        JSONObject jsontemp;
                
        List listFriendIds;
        
        Iterator<Integer> iteratorFriendId;
        try{
        //collect list of friend ids
            url="http://api.twitter.com/1.1/users/search.json?q=virat%20kohli&page=1&count=1";        
            request = new HttpGet(); 
            client = new DefaultHttpClient();

            request.setURI(new URI(url));
            consumer.sign(request);        
            response = client.execute(request);

            //statusCode=response.getStatusLine().getStatusCode();
            //System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());

            System.out.println(IOUtils.toString(response.getEntity().getContent()));
           
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
   
}
