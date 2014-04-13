/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
public class PersonalityAnalysis_FiveFactorComputation {
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
        String strUrl="C:\\Users\\rohit_000\\Desktop\\Spring 13\\Web Info\\twitter_sample";
        
        String Key="screen_name";
        String Value="sanket_visify";
        //Agreeableness(Key,Value,strUrl);
        //Extraversion(Key,Value,strUrl);
        //Openness(Key,Value,strUrl);
        //Conscientiousness(Key,Value,strUrl);
        Neuroticism(Key,Value,strUrl);
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
       
    //REST API Twitter
    public static JSONArray GetTweets(String Key, String Value, String strUrl)throws Exception{
        String url;
        String strTweets;
        try{
            url=strUrl+"\\tweets\\getTweets.txt";
            if(FileExistenceCheck(url)){
                strTweets=ReadFile(url);
                return((JSONArray)JSONSerializer.toJSON(strTweets));
            }
            else{
                return((JSONArray)JSONSerializer.toJSON("[]"));
            }
                
                
            
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
    //<editor-fold defaultstate="collapsed" desc="float StdDeviation(List<Object> list)">
    public static float StdDeviation(List<Object> list) throws Exception{
        //List of variables used
        float mean=0;
        float sumSquare=0;
        float StdDev;
        
        //Calculate the mean
        for(int i=0; i<list.size(); i++)
        {
            mean+= Float.parseFloat(list.get(i).toString());
            sumSquare+= Math.pow(Float.parseFloat(list.get(i).toString()), 2);
        }
        mean=mean/list.size();
        
        //Calculate the Standard Deviation
        StdDev = (float) Math.sqrt( (sumSquare/list.size()) + Math.pow( mean, 2) );
        return StdDev;
    }
    //</editor-fold>
    
    //Parameter Functions--------------------------------------------------------
    public static float GetLength(String Key, String Value, String strUrl)throws Exception{
        JSONArray jsonArrayTweets;
        float flTweetsLength=0;
        try{
             // Get Tweets of User
            if(FileExistenceCheck(strUrl+"\\tweets\\getTweets.txt")==true){
                jsonArrayTweets=GetTweets(Key,Value,strUrl);
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
            else{
                return 0;
            }
                
                
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
            int intFriendSampleSize;

            JSONArray jsonArrayTweet;
            JSONObject jsonTweet;
            JSONObject jsonRetweetedStatus;
            JSONObject jsonRetweetedStatusSrc;

            List listFriends;
            List listPropogations;

            listPropogations=new ArrayList<>();

            //get friends
            listFriends=GetFriends(Key,Value,strUrl);
            //iterate all friends
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                //get tweets of friends
                if(FileExistenceCheck(strUrl+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString()+"\\tweets\\getTweets.txt")==true){
                    jsonArrayTweet=GetTweets(Key,Value,strUrl+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString());
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
            if(FileExistenceCheck(strUrl+"\\tweets\\getTweets.txt")==true){
                jsonArrayTweet=GetTweets(Key,Value,strUrl);
                //normalize the propogation count
                flNormPropogation=flMeanPropogation/jsonArrayTweet.size();
            }
            else{
                flNormPropogation=0;
            }
                
            
            //System.out.println(intPropogationCount);
            //System.out.println(flMeanPropogation);
            //System.out.println(flNormPropogation);
            
            return flNormPropogation;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //<editor-fold defaultstate="collapsed" desc="float GetStdAttention(String Key,String Value,String strUrl)">
    public static float GetStdAttention(String Key,String Value,String strUrl) throws Exception{
        try{
            
            int intIterateFriends;
            int intIterateTweet;
            float flStdAttention;
            
            
            
            JSONArray jsonArrayTweet;
            JSONObject jsonTweet;
            JSONObject jsonRetweetedStatus;
            JSONObject jsonRetweetedStatusSrc;
            
            List listFriends;
            List listPropogations;
            List listRetweets;
            List listAttention;
            
            listPropogations=new ArrayList<>();
            listRetweets= new ArrayList<>();
            listAttention = new ArrayList<>();
            
            //get friends
            listFriends=GetFriends(Key,Value,strUrl);
            //iterate all friends
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                //get tweets of friends
                if(FileExistenceCheck(strUrl+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString()+"\\tweets\\getTweets.txt")==true){
                    jsonArrayTweet=GetTweets(Key,Value,strUrl+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString());
                    for(intIterateTweet=0;intIterateTweet<jsonArrayTweet.size();intIterateTweet++){
                        if(intIterateTweet==0){
                            listPropogations.add(intIterateFriends,0);
                            listRetweets.add(intIterateFriends,0);
                        }
                        jsonTweet=jsonArrayTweet.getJSONObject(intIterateTweet);
                        
                        //identify the amount of tweets that are retweeted by the friends
                        if(jsonTweet.has("retweeted_status")){
                            listRetweets.add(intIterateFriends,Integer.parseInt(listRetweets.get(intIterateFriends).toString())+1);
                            
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
                    listRetweets.add(intIterateFriends,0);
                }
                
                
            }
            //count propogations
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                
                if(Integer.parseInt(listRetweets.get(intIterateFriends).toString())!=0){
                    listAttention.add(intIterateFriends, (float)(Integer.parseInt(listPropogations.get(intIterateFriends).toString()))/(Integer.parseInt(listRetweets.get(intIterateFriends).toString())));
                }
                else{
                    listAttention.add(intIterateFriends, 0);
                }
                    
                
            }
            flStdAttention=StdDeviation(listAttention);
            
            return flStdAttention;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="float GetMeanHashTags(String Key, String Value, String strUrl)">
    public static float GetMeanHashTags(String Key, String Value, String strUrl){
        JSONArray jsonArrayTweet;
        JSONArray jsonHashtags;
        
        JSONObject jsontemp;
        JSONObject jsonEntities;
        JSONObject jsonHashTagText;
        
        int intIteratorTweet;
        int iteratorHashtags;
        int intHashTagCount=0;
        
        try{
            if(FileExistenceCheck(strUrl+"\\tweets\\getTweets.txt")==true){
                jsonArrayTweet=GetTweets(Key,Value,strUrl);
                for(intIteratorTweet=0;intIteratorTweet<jsonArrayTweet.size();intIteratorTweet++){
                    jsontemp=jsonArrayTweet.getJSONObject(intIteratorTweet);
                    if(jsontemp.has("entities")){
                        jsonEntities=(JSONObject)JSONSerializer.toJSON(jsontemp.getString("entities"));
                        if(jsonEntities.has("hashtags")){

                            jsonHashtags=(JSONArray)JSONSerializer.toJSON(jsonEntities.getString("hashtags"));

                            for(iteratorHashtags=0;iteratorHashtags<jsonHashtags.size();iteratorHashtags++){

                                jsonHashTagText=jsonHashtags.getJSONObject(iteratorHashtags);
                                if(jsonHashTagText.has("text"))
                                {
                                    intHashTagCount++;
                                }
                            }
                        }
                    }

                }
                return ( (float)intHashTagCount/jsonArrayTweet.size());
            }
            else{
                return 0;
            }
                
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="float GetStdTime(String Key,String Value,String strUrl)">
    public static float GetStdTime(String Key,String Value,String strUrl) throws Exception{
        JSONArray jarray;
        
        int tweetIntervalCount = -1;
        String dateStart = null;
        String dateEnd ;
        String []dateComponents;
        String []months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<String> monthList = Arrays.asList(months);
        List<Long> tweetInterval = new ArrayList<>();
        Date tweetDate1;
        Date tweetDate2;
        long diff, diffSeconds, diffMinutes;
        float mean = 0;
        float squareOfInterval = 0;
        float avgSquareInterval ;
        float stdDeviation ;
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        
        try{
            if(FileExistenceCheck(strUrl+"\\tweets\\getTweets.txt")==true){
                jarray=GetTweets(Key,Value,strUrl);
                if(jarray.isEmpty()){
                    return 0;
                }
                //Loop through all the tweets to compute the tome difference between tweets
                for(int i=0; i<jarray.size(); i++)
                {
                    JSONObject j1 = jarray.getJSONObject(i);
                    String tweetId = j1.getString("id");
                    String time = j1.getString("created_at");
                    tweetIntervalCount++;

                    /*Note: The date returned in the Json is of the form: "Wed May 08 05:06:48 +0000 2013"
                     * We need to convert it into "YY/MM/dd HH:mm:ss" so that we can compute the time interval between two tweets
                     */
                    dateComponents = time.split(" ");
                    if(tweetIntervalCount == 0)
                    {
                        dateStart = dateComponents[5].substring(2)+"/"+(monthList.indexOf(dateComponents[1])+1)+"/"+dateComponents[2]+" "+dateComponents[3];
                    }
                    else
                    {
                        dateEnd = dateComponents[5].substring(2)+"/"+(monthList.indexOf(dateComponents[1])+1)+"/"+dateComponents[2]+" "+dateComponents[3];
                        tweetDate1 = format.parse(dateStart);
                        tweetDate2 = format.parse(dateEnd);
                        diff = tweetDate1.getTime() - tweetDate2.getTime();
                        diffSeconds = diff/1000;
                        diffMinutes = diff/(60 * 1000);
                        tweetInterval.add(diffMinutes);
                        dateEnd = dateStart;
                    }
                    //            System.out.println("Tweet " + tweetId + " was create at " + time + "\n");
                }

                //Loop through all the time intervals to compute the mean
                for(int i=0; i<tweetInterval.size(); i++)
                {
                    mean = mean + tweetInterval.get(i);
                    squareOfInterval = (float) (squareOfInterval + Math.pow(tweetInterval.get(i), 2));
                }

                //Caculate the standard deviation
                mean = mean/tweetInterval.size();
                avgSquareInterval = squareOfInterval/tweetInterval.size();
                stdDeviation = (float) Math.sqrt(avgSquareInterval - Math.pow(mean, 2.0));
                return stdDeviation;
           }
            else{
                return 0;
            }
                

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="float GetFFStdTime(String Key,String Value,String strUrl)">
    public static float GetFFStdTime(String Key,String Value,String strUrl) throws Exception{
        //variables needed for collecting the friends of rohit's friends, i.e Girish's friends
        int intIteratorFriend;
        List listFriends;
        
        //create an arraylist to store the StdTime values for all the user's friends
        List<Float> stdTime = new ArrayList<>();
        float timeInterval;
        float FFTime;
        try {
            
            listFriends=GetFriends(Key,Value,strUrl);
            if(listFriends.size() > 0)
            {
                for(intIteratorFriend=0; intIteratorFriend<listFriends.size(); intIteratorFriend++)
                {
                    timeInterval = GetStdTime("user_id",listFriends.get(intIteratorFriend).toString(),strUrl+"\\friends\\user_id\\"+listFriends.get(intIteratorFriend).toString());
                    if(timeInterval > 0)
                    {
                        stdTime.add(timeInterval);
                    }
                }
                timeInterval = 0;
                //Calculate the average FF-Std time
                for(int i=0; i<stdTime.size(); i++)
                {
                    timeInterval = timeInterval + stdTime.get(i);
                }
                FFTime = timeInterval / stdTime.size();
                return FFTime;
            }
            else
            {
                return 0;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="float GetStdLength(String Key, String Value, String strUrl)">
    public static float GetStdLength(String Key, String Value, String strUrl)throws Exception{
        JSONArray jsonArrayTweets;
        float flSumOfTweetsLength=0;
        float flSumOfSquareOfTweetsLength=0;
        float flStdLength;
        try{
            // Get Tweets of User
            if(FileExistenceCheck(strUrl+"\\tweets\\getTweets.txt")==true){
                jsonArrayTweets=GetTweets(Key,Value,strUrl);
                for(int i=0; i<jsonArrayTweets.size(); i++){
                    JSONObject j1 = jsonArrayTweets.getJSONObject(i);
                    String tweet = j1.getString("text");
                    flSumOfSquareOfTweetsLength+=Math.pow(tweet.length(), 2);
                    flSumOfTweetsLength += tweet.length();
                    
                }
                if(jsonArrayTweets.size() > 0){
                    flStdLength=(float) Math.sqrt(flSumOfSquareOfTweetsLength/jsonArrayTweets.size() - Math.pow(flSumOfTweetsLength/jsonArrayTweets.size(), 2));
                }
                else{
                    flStdLength = 0.0f;
                }
                return(flStdLength);
            }
            else{
                return 0;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return 0;
        
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="float GetMeanFFRetweetCount(String Key,String Value,String strUrl)">
    public static float GetMeanFFRetweetCount(String Key,String Value,String strUrl) throws Exception{
        try{
            
            int intIterateFriends;
            int intIterateTweet;
            
            JSONArray jsonArrayTweet;
            JSONObject jsonTweet;
            JSONObject jsonRetweetedStatus;
            JSONObject jsonRetweetedStatusSrc;
            
            List listFriends;
            
            List listFriendRetweets;
            List listFriendTweets;
            float flGetFractionOfFFRetweet=0;
            float flGetMeanOfFractionOfFFRetweet;
            
            listFriendRetweets= new ArrayList<>();
            listFriendTweets = new ArrayList<>();
            
            //get friends
            listFriends=GetFriends(Key,Value,strUrl);
            //iterate all friends
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                //get tweets of friends
                if(FileExistenceCheck(strUrl+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString()+"\\tweets\\getTweets.txt")==true){
                    jsonArrayTweet=GetTweets(Key,Value,strUrl+"\\friends\\user_id\\"+listFriends.get(intIterateFriends).toString());
                    listFriendTweets.add(intIterateFriends,jsonArrayTweet.size());
                    listFriendRetweets.add(intIterateFriends, 0);
                    for(intIterateTweet=0;intIterateTweet<jsonArrayTweet.size();intIterateTweet++){
                        
                        jsonTweet=jsonArrayTweet.getJSONObject(intIterateTweet);
                        
                        //identify the amount of tweets that are retweeted by the friends
                        if(jsonTweet.has("retweeted_status")){
                            
                            
                            jsonRetweetedStatus=(JSONObject)JSONSerializer.toJSON(jsonTweet.getString("retweeted_status"));
                            if(jsonRetweetedStatus.has("user")){
                                jsonRetweetedStatusSrc=(JSONObject)JSONSerializer.toJSON(jsonRetweetedStatus.getString("user"));
                                if(jsonRetweetedStatusSrc.has("screen_name")){
                                    
                                    if(Key.equals(jsonRetweetedStatusSrc.getString("screen_name"))){
                                        listFriendRetweets.add(intIterateFriends,Integer.parseInt(listFriendRetweets.get(intIterateFriends).toString())+1);
                                        System.out.println("retweeted");
                                    }
                                    else{
                                        listFriendRetweets.add(intIterateFriends,Integer.parseInt(listFriendRetweets.get(intIterateFriends).toString())+0);
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
                    listFriendTweets.add(intIterateFriends,0);
                    listFriendRetweets.add(intIterateFriends,0);
                }
                
                
            }
            //sum fraction of retweets
            for(intIterateFriends=0;intIterateFriends<listFriends.size();intIterateFriends++){
                
                if(Integer.parseInt(listFriendTweets.get(intIterateFriends).toString())!=0){
                    flGetFractionOfFFRetweet+=(float)Integer.parseInt(listFriendRetweets.get(intIterateFriends).toString())/Integer.parseInt(listFriendTweets.get(intIterateFriends).toString());
                }
                else{
                }
            }
            flGetMeanOfFractionOfFFRetweet=flGetFractionOfFFRetweet/listFriends.size();
            return flGetMeanOfFractionOfFFRetweet;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //</editor-fold>
    
    //Five Factor Function
    public static void Agreeableness(String Key,String Value,String strUrl) throws Exception{
        float flGetMeanHashTags;
        try{
            //parameter 1
            //FriendFriendStdAttention(Key,Value,strUrl);
            //parameter 2
            flGetMeanHashTags=GetMeanHashTags(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            System.out.println(flGetMeanHashTags);
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
        float flGetStdAttention;
        try{
            //parameter 1 // Length
            flGetLength=GetLength(Key, Value, strUrl+"\\users"+"\\"+Key+"\\"+Value); 
            System.out.println(flGetLength);
            //parameter 2
            flGetMeanPropogation=GetMeanPropogation(Key, Value,strUrl+"\\users\\"+Key+"\\"+Value);
            System.out.println(flGetMeanPropogation);
            //parameter 3
            flGetStdAttention=GetStdAttention(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            System.out.println(flGetStdAttention);
            
        }
        catch(Exception e){
            System.out.println(e);
        }
            
    }    
    //<editor-fold defaultstate="collapsed" desc="void Openness(String Key, String Value, String strUrl)">
    public static void Openness(String Key, String Value, String strUrl) throws Exception{
        float flGetStdTime;
        try{
            //parameter 1
            flGetStdTime=GetStdTime(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            System.out.println(flGetStdTime);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="void Conscientiousness(String Key, String Value, String strUrl)">
    public static void Conscientiousness(String Key, String Value, String strUrl){
        float flGetFFStdTime;
        
        try{
            //parameter 1
            flGetFFStdTime=GetFFStdTime(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            System.out.println(flGetFFStdTime);
            //parameter 2
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="void Neuroticism(String Key,String Value,String strUrl)">
    public static void Neuroticism(String Key,String Value,String strUrl){
        float flGetStdLength;
        float flGetMeanFFRetweetCount;
        try{
            //parameter 1
            //flGetStdLength=GetStdLength(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            //System.out.println(flGetStdLength);
            
            //parameter 2
            flGetMeanFFRetweetCount=GetMeanFFRetweetCount(Key,Value,strUrl+"\\users\\"+Key+"\\"+Value);
            System.out.println(flGetMeanFFRetweetCount);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //</editor-fold>

}
