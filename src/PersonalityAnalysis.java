/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URI;
import java.util.Iterator;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
/**
 *
 * @author rohit_000
 */
public class PersonalityAnalysis {

    /**
     * @param args the command line arguments
     */
        static String AccessToken ="1368765691-CK5uI4TyQUL5pQFfIUxT52GIb88ihthkFgvADNV";
        static String AccessSecret = "OEw1Vuyi2BjHwHvxZjSwT6G1j3zy5lrD61fYcqAx4ls";
        static String ConsumerKey = "Z2MJxkErRf15afnautd2w";
        static String ConsumerSecret = "z6OvdnhTAUeio6Q3oQozdvybxtju3cdr8Cn0JwywXI";
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
        int FriendTweetRetweetMytweetCount;
        int iteratorFriendTweet;
        int FriendTweetHashTagcount;
        int iteratorHashtags;
                
        String url;
        String strFriendIds;
        String strFriendTweets;
                
        JSONObject json;
        JSONArray jsonArray;
        JSONObject jsontemp;
        JSONObject jsonRetweetedStatus;
        JSONObject jsonRetweetedStatusSrc;
        JSONObject jsonEntities;
        JSONArray jsonHashtags;
        JSONObject jsonHashTagText;
        
        List listFriendIds;
        
        Iterator<Integer> iteratorFriendId;
        try{
        //collect list of friend ids
            url="http://api.twitter.com/1.1/friends/ids.json?user_id=22180929";        
            request = new HttpGet();
            client = new DefaultHttpClient();

            request.setURI(new URI(url));
            consumer.sign(request);        
            response = client.execute(request);

            //statusCode=response.getStatusLine().getStatusCode();
            //System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());

            strFriendIds=IOUtils.toString(response.getEntity().getContent());
            json=(JSONObject)JSONSerializer.toJSON(strFriendIds);        

            System.out.print(strFriendIds);
            
            listFriendIds=(List)json.get("ids");
            iteratorFriendId = listFriendIds.iterator();

            while (iteratorFriendId.hasNext()) 
            {
                Integer temp=iteratorFriendId.next();
                System.out.print(temp);

        //collect tweets of friends
                url="http://api.twitter.com/1.1/statuses/user_timeline.json?user_id="+temp+"&count=200";

                request.setURI(new URI(url));
                consumer.sign(request);
                response=client.execute(request);

                //statusCode=response.getStatusLine().getStatusCode();
                //System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());

                strFriendTweets=IOUtils.toString(response.getEntity().getContent());
                jsonArray=(JSONArray)JSONSerializer.toJSON(strFriendTweets);

        //identify the tweets which are retweeted
        //identify the tweets retweeted by the user
                FriendTweetRetweetCount=0;
                FriendTweetRetweetMytweetCount=0;
                FriendTweetHashTagcount=0;
                for(iteratorFriendTweet=0;iteratorFriendTweet<jsonArray.size();iteratorFriendTweet++)
                    {            

                        jsontemp=jsonArray.getJSONObject(iteratorFriendTweet);
                        
                        if(jsontemp.has("retweeted_status")){
                            FriendTweetRetweetCount++;                            
                            jsonRetweetedStatus=(JSONObject)JSONSerializer.toJSON(jsontemp.getString("retweeted_status"));
                                if(jsonRetweetedStatus.has("user")){
                                    jsonRetweetedStatusSrc=(JSONObject)JSONSerializer.toJSON(jsonRetweetedStatus.getString("user"));
                                    if(jsonRetweetedStatusSrc.has("screen_name")){
                                        if("g4green_".equals(jsonRetweetedStatusSrc.getString("screen_name"))){
                                            FriendTweetRetweetMytweetCount++;
                                        }                                            
                                    }
                                }
                        }
                        if(jsontemp.has("entities")){
                            jsonEntities=(JSONObject)JSONSerializer.toJSON(jsontemp.getString("entities"));
                                if(jsonEntities.has("hashtags")){
                                   
                                    jsonHashtags=(JSONArray)JSONSerializer.toJSON(jsonEntities.getString("hashtags"));
                                    
                                    for(iteratorHashtags=0;iteratorHashtags<jsonHashtags.size();iteratorHashtags++){                                        
                                        
                                        jsonHashTagText=jsonHashtags.getJSONObject(iteratorHashtags);
                                        if(jsonHashTagText.has("text"))
                                        {
                                            FriendTweetHashTagcount++;
                                        }
                                    }
                                }
                        }

                    }
                System.out.print(" "+jsonArray.size());
                System.out.print(" "+FriendTweetRetweetCount);  
                System.out.print(" "+FriendTweetRetweetMytweetCount);  
                System.out.println(" "+FriendTweetHashTagcount);


            }
            //Attn
        //compute attn as ratio of R/RU
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
}
