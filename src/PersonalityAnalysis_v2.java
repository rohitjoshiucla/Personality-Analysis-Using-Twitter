/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.json.*;
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
public class PersonalityAnalysis_v2 {
    static String AccessToken ="1368765691-CK5uI4TyQUL5pQFfIUxT52GIb88ihthkFgvADNV";
    static String AccessSecret = "OEw1Vuyi2BjHwHvxZjSwT6G1j3zy5lrD61fYcqAx4ls";
    static String ConsumerKey = "Z2MJxkErRf15afnautd2w";
    static String ConsumerSecret = "z6OvdnhTAUeio6Q3oQozdvybxtju3cdr8Cn0JwywXI";
    static OAuthConsumer consumer;
    
    public static void main(String[] args) throws Exception{
        // TODO code application logic here        
        
        consumer = new CommonsHttpOAuthConsumer(ConsumerKey,ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken,AccessSecret);
        
        //select set of userIds whose five factors need to be analysed
        //iterate for those user ids the five factors
        String strUrl="C:\\Users\\rohit_000\\Desktop\\Spring 13\\Web Info\\twitter";
        
        String Key="screen_name";
        String Value="bhumilharia";
        //Agreeableness(Key,Value);
        Extraversion(Key,Value,strUrl);
    }
    
    public static String ReadFile(String strUrl) throws Exception{
        String line;
        BufferedReader reader;
        StringBuilder stringBuilder;
        
        reader=new BufferedReader(new FileReader(strUrl));
        stringBuilder=new StringBuilder();
        while((line=reader.readLine())!=null){
            stringBuilder.append(line);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }    
    //<editor-fold defaultstate="collapsed" desc="Boolean FileExistenceCheck(String strUrl)">
    public static Boolean FileExistenceCheck(String strUrl)throws Exception{
        File fileExistenceCheck;
        fileExistenceCheck=new File(strUrl);
        if(fileExistenceCheck.isFile()){
            return true;
        }
        else {
            return false;
        }
    }
    //</editor-fold>
        
    //REST API Twitter
    public static JSONArray GetTweets(String Key, String Value, String strUrl)throws Exception{
        String url;
        String strTweets;
        try{
            url=strUrl+"\\tweets\\getTweets.txt";
            strTweets=ReadFile(url);
            return((JSONArray)JSONSerializer.toJSON(strTweets));
        }
        catch(Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }    
    public static List GetFriends(String Key,String Value,String strUrl) throws Exception{
        //initialization
        String url;
        String strFriendIds;
        JSONObject jsonFriendIds;
        List listFriends;
        try{
            url=strUrl+"\\friends\\getFriends.txt";        
            
            strFriendIds=ReadFile(url);
            jsonFriendIds=(JSONObject)JSONSerializer.toJSON(strFriendIds);
            
            listFriends=(List)jsonFriendIds.get("ids");
            return listFriends;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null ;
    }
    //Common Functions---------------------------------------------------------
    public static void StdAttention(String Key, String Value, String strUrl)throws Exception{
        Iterator<Integer> iteratorFriends;
        Integer intFriendId;
        Integer intTweetNum;
        Integer intRetweetCount;
        List listFriends; 
        JSONArray jsonArrayTweets;
        JSONObject jsonTweet;
        try{
            //Iterate for each friend of this user
            listFriends=GetFriends(Key,Value,strUrl);
            iteratorFriends=listFriends.iterator();
            System.out.println("Retweet Count Status");
            if(iteratorFriends.hasNext())
            {
                intFriendId=iteratorFriends.next();
                //collect tweets of friends 
                jsonArrayTweets=GetTweets("user_id",intFriendId.toString(),strUrl);
                //identify the number tweets from user which are retweeted by friends 
                intRetweetCount=0;
                for(intTweetNum=0;intTweetNum<jsonArrayTweets.size();intTweetNum++){
                    jsonTweet=jsonArrayTweets.getJSONObject(intTweetNum);
                    if(jsonTweet.has("retweeted_status")){
                        intRetweetCount++;
                    }
                }
                System.out.println(intFriendId.toString()+" "+intRetweetCount.toString());
            }   
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //Parameter Functions--------------------------------------------------------
    public static void FriendFriendStdAttention(String Key,String Value,String strUrl) throws Exception{
        Iterator<Integer> iteratorFriends;
        List listFriends;
        Integer intFriendId;
        try{
            //Get list of users friends
            listFriends=GetFriends(Key,Value,strUrl);
            //Compute attention             
            //Iterate for each friend of this user
            iteratorFriends=listFriends.iterator();
            while(iteratorFriends.hasNext())
            {                                   
                //Determine attention               
                intFriendId=iteratorFriends.next();
                System.out.println("Friend ID = "+intFriendId);
                System.out.println("Attention Details are = ");                
                StdAttention("user_id",intFriendId.toString(),strUrl);                    
            }
            //Compute Std Deviation 
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }    
    public static float GetLength(String Key, String Value, String strUrl)throws Exception{
        JSONArray jsonArrayTweets;
        float flTweetsLength=0;
        try{
             // Get Tweets of User
            jsonArrayTweets=GetTweets(Key,Value,strUrl+"\\users"+"\\"+Key+"\\"+Value);                       
            for(int i=0; i<jsonArrayTweets.size(); i++){
                JSONObject j1 = jsonArrayTweets.getJSONObject(i);
                String tweet = j1.getString("text");
                flTweetsLength += tweet.length();
            }
            if(jsonArrayTweets.size() > 0){
                flTweetsLength = flTweetsLength / jsonArrayTweets.size();
            }
            else{
                flTweetsLength = 0.0f;
            }
            return(flTweetsLength);
        }
        catch(Exception e){
            System.out.println(e);
        }
        return 0;
        
    }
    public static float GetMeanPropogation(String Key,String Value,String strUrl) throws Exception{
        try{
            
        
            int intIterateFriends;
            int intIterateTweet;
            int intPropogationCount=0;
            float flMeanPropogation; 
            float flNormPropogation;

            JSONArray jsonArrayTweet;
            JSONObject jsonTweet;
            JSONObject jsonRetweetedStatus;
            JSONObject jsonRetweetedStatusSrc;

            List listFriends;
            List listPropogations;

            listPropogations=new ArrayList<>();

            //get friends
            listFriends=GetFriends(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            //iterate all friends
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                //get tweets of friends
                if(FileExistenceCheck(strUrl+"\\users\\"+Key+"\\"+Value+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString()+"\\tweets\\getTweets.txt")){
                    jsonArrayTweet=GetTweets(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString());
                    for(intIterateTweet=0;intIterateTweet<jsonArrayTweet.size();intIterateTweet++){
                            if(intIterateTweet==0){
                                listPropogations.add(intIterateFriends,0);
                            }
                            jsonTweet=jsonArrayTweet.getJSONObject(intIterateTweet);
                            
                            //identify the amount of tweets that are retweeted by the friends
                            if(jsonTweet.has("retweeted_status")){
                                jsonRetweetedStatus=(JSONObject)JSONSerializer.toJSON(jsonTweet.getString("retweeted_status"));
                                if(jsonRetweetedStatus.has("user")){
                                    jsonRetweetedStatusSrc=(JSONObject)JSONSerializer.toJSON(jsonRetweetedStatus.getString("user"));
                                    if(jsonRetweetedStatusSrc.has("screen_name")){
                                        if(Key.equals(jsonRetweetedStatusSrc.getString("screen_name"))){
                                            listPropogations.add(intIterateFriends, Integer.parseInt(listPropogations.get(intIterateFriends).toString())+1);
                                            System.out.println("retweeted");
                                        }
                                        else{
                                            listPropogations.add(intIterateFriends, Integer.parseInt(listPropogations.get(intIterateFriends).toString())+0);
                                        }
                                    }
                                    else{

                                    }
                                }
                                else{

                                }

                            }
                            else{

                            }
                        }
                
                    }
                    else{
                            listPropogations.add(intIterateFriends,0);
                    }
                

            }
            //count propogations
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                intPropogationCount=intPropogationCount+Integer.parseInt(listPropogations.get(intIterateFriends).toString());
            
            }
            //count mean propogation
            flMeanPropogation=intPropogationCount/listFriends.size();

            //count number of tweets of user
            jsonArrayTweet=GetTweets(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);

            //normalize the propogation count
            flNormPropogation=flMeanPropogation/jsonArrayTweet.size();
            
            System.out.println(intPropogationCount);
            System.out.println(flMeanPropogation);
            System.out.println(flNormPropogation);
            
            return flNormPropogation;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //Five Factor Function
    public static void Agreeableness(String Key,String Value,String strUrl) throws Exception{
        
        try{
            //parameter 1
            FriendFriendStdAttention(Key,Value,strUrl);
            //parameter 2
            //parameter 3
            //parameter 4
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void Extraversion(String Key, String Value, String strUrl)throws Exception{
        float flGetLength;
        float flGetMeanPropogation;
        try{
            //parameter 1 // Length
            flGetLength=GetLength(Key, Value, strUrl); 
            //parameter 1
            flGetMeanPropogation=GetMeanPropogation(Key, Value,strUrl);
            //parameter 1
            System.out.println(flGetLength);
            System.out.println(flGetMeanPropogation);
        }
        catch(Exception e){
            System.out.println(e);
        }
            
    }

}
