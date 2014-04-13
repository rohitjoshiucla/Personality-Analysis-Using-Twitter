/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
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
public class PersonalityAnalysis_DataDump_GetUserTimeline_Sample {
    static String AccessToken ="1368173131-f3coLmczGB62x3Ll1Yi1XSVtr66iMH3CGTmc7mY";
    static String AccessSecret = "uYeeiWoFN6AaQ203Xs98kCa0GWcHSy0xwhcWDYM";
    static String ConsumerKey = "XPUwwq1za9AQ93ABdNAig";
    static String ConsumerSecret = "DoHsnnYiqwi9bwn1s7XNc8rxggkvmi4RcopiWrpKM";
    static OAuthConsumer consumer;
    
    public static void main(String[] args) throws Exception{
        // TODO code application logic here        
        
        //<editor-fold defaultstate="collapsed" desc="Initialization">
        consumer = new CommonsHttpOAuthConsumer(ConsumerKey,ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken,AccessSecret);
        
        List listUsers;
        List listFriends;
        List listFriendofFriends;
        
        Map<String,List> mapPendingRequestsPrev ;
        Map<String,List> mapPendingRequestsCurr ;
        
        Map<String,List> mapPendingRequestsOfFriendsPrev;
        
        int intUserCount;
        int intFriendCount;
        int intHttpReqCount;
        int intHttpSuccessfulReqCount;
        int intTotalPendingRequests;
        int intFriendofFriendCount;
        int intFriendSampleSize;
        int intFriendOfFriendSampleSize;
        
        String strUrl="C:\\Users\\Rohit\\workspace\\Rohit";
        String strResponse;
        
        JSONObject jsonFriendIds;
        
        String strScreenName;
        List listPendingRequestsCurr;
        List listPendingRequestsPrev;
        List listPendingRequestsOfFriendsPrev;
        
        Iterator iteratorMapPrev;        
        Iterator iteratorMapCurr;
        Iterator iteratorMapOfFriendsPrev;
        
        Map.Entry entryPrev;
        Map.Entry entryCurr;
        
        Map.Entry entryOfFriendsPrev;
        
        long longPrevTime=0;
        java.util.Date date;
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Get and Save Users">
        //Get Users-------------------------------------------------------------
        listUsers=GetUsers(strUrl);
        //Save Users
        if(listUsers!=null) {
            for(intUserCount=0;intUserCount<listUsers.size();intUserCount++){
                SaveUsers(listUsers,intUserCount,strUrl);
            }
        }
        else{
            //System.out.println("List of Users is empty : SaveUsers not called");
        }
        //</editor-fold>
       
        //<editor-fold defaultstate="collapsed" desc="Get Pending Requests of Users">
        mapPendingRequestsPrev=GetUsersPendingRequests(strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");
        mapPendingRequestsOfFriendsPrev=GetUsersPendingRequests(strUrl+"\\users\\getPendingRequests_GetFriends.txt");
        //</editor-fold>  
        System.out.print("\n Prev: ");
        PrintMap(mapPendingRequestsPrev);
        //<editor-fold defaultstate="collapsed" desc="Get Count of Pending Requests of Users">
        intTotalPendingRequests=GetCountPendingRequests(mapPendingRequestsPrev)+GetCountPendingRequests(mapPendingRequestsOfFriendsPrev);
        //</editor-fold>
        System.out.print("\n Count: "+intTotalPendingRequests);
        
        
        top:while(intTotalPendingRequests>0 )
        {
            if((System.currentTimeMillis()-longPrevTime) > 960000){
                
                longPrevTime=System.currentTimeMillis();    
                mapPendingRequestsPrev=GetUsersPendingRequests(strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");
                mapPendingRequestsCurr=GetResetHashMap(strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");                
                mapPendingRequestsOfFriendsPrev=GetUsersPendingRequests(strUrl+"\\users\\getPendingRequests_GetFriends.txt");
                PrintMap(mapPendingRequestsOfFriendsPrev);
                System.out.print("\n Get and Save Tweets of Users");
                date= new java.util.Date();
                System.out.print("\n "+new Timestamp(date.getTime()));
                //Get Tweets of all Users
                //<editor-fold defaultstate="collapsed" desc="Get and Save Tweets of Users">
                //Get and Save Tweets of Users        
                if(listUsers!=null){
                    iteratorMapPrev=mapPendingRequestsPrev.entrySet().iterator();
                    iteratorMapCurr=mapPendingRequestsCurr.entrySet().iterator();
                    

                    for(intUserCount=0;intUserCount<listUsers.size();intUserCount++){                
                        intHttpReqCount=0;intHttpSuccessfulReqCount=0;

                        entryPrev= (Map.Entry)iteratorMapPrev.next();
                        entryCurr= (Map.Entry)iteratorMapCurr.next();
                        

                        strScreenName=(String)entryPrev.getKey();

                        listPendingRequestsPrev=(List)entryPrev.getValue();
                        listPendingRequestsCurr=(List)entryCurr.getValue();
                        

                        if(Integer.parseInt(listPendingRequestsPrev.get(0).toString())>0 ){                    
                            if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\tweets\\getTweets.txt")==false){                        
                                intHttpReqCount++;
                                strResponse=GetTweets("screen_name",listUsers.get(intUserCount).toString());
                                if(strResponse.equals("UnAuthorized Request")){
                                            intHttpReqCount--;
                                }
                                else if(strResponse.equals("Rate Limit Exceeded")){
                                    System.out.print("\n Rate Limit Exceeded");
                                    System.out.print("\n Successfully processed "+intHttpSuccessfulReqCount);

                                    listPendingRequestsCurr.set(0, 1);
                                    mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);

                                    listPendingRequestsCurr.set(2, 1);
                                    mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);

                                    listPendingRequestsCurr.set(4, 1);
                                    mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);    

                                    
                                    intTotalPendingRequests=GetCountPendingRequests(mapPendingRequestsCurr);

                                    System.out.print("\n Save Pending Requests of Users");
                                    date= new java.util.Date();
                                    System.out.print(new Timestamp(date.getTime()));    

                                    SaveUsersPendingRequests(listUsers,mapPendingRequestsCurr,strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");
                                    
                                    PrintLog(mapPendingRequestsCurr);
                                    
                                    continue top;
                                }
                                else if(!strResponse.equals("Erroneous Response")){
                                    SaveTweets(listUsers,intUserCount,strResponse,strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString());
                                    if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\tweets\\getTweets.txt")==true){
                                    intHttpSuccessfulReqCount++;
                                    }
                                    else{
                                    }
                                }
                                else{
                                }
                            }
                            else{
                                //System.out.println("Tweet of user "+listUsers.get(intUserCount).toString()+" already present");
                            }
                        }
                        System.out.print("\n Successfully processed "+intHttpSuccessfulReqCount);
                        listPendingRequestsCurr.set(0, Integer.parseInt(listPendingRequestsCurr.get(0).toString())+intHttpReqCount-intHttpSuccessfulReqCount);
                        mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                    }
                }
                else{
                    //System.out.println("List of Users is empty : SaveTweets of Users not called");
                }
                //</editor-fold>

                System.out.print("\n Get and Save Tweets of Friends of Users");
                date= new java.util.Date();
                System.out.print("\n"+new Timestamp(date.getTime()));
                //Get Tweets of All Friends of Users
                //<editor-fold defaultstate="collapsed" desc="Get and Save Tweets of Friends of Users">
                //Get and Save Tweets of Friends of Users
                if(listUsers!=null){
                    iteratorMapPrev=mapPendingRequestsPrev.entrySet().iterator();
                    iteratorMapCurr=mapPendingRequestsCurr.entrySet().iterator();
                    iteratorMapOfFriendsPrev=mapPendingRequestsOfFriendsPrev.entrySet().iterator();
                    
                    for(intUserCount=0;intUserCount<listUsers.size();intUserCount++){

                        intHttpReqCount=0;intHttpSuccessfulReqCount=0;

                        entryPrev= (Map.Entry)iteratorMapPrev.next();
                        entryCurr= (Map.Entry)iteratorMapCurr.next();
                        entryOfFriendsPrev=(Map.Entry)iteratorMapOfFriendsPrev.next();
                           
                        strScreenName=(String)entryPrev.getKey();

                        listPendingRequestsPrev=(List)entryPrev.getValue();
                        listPendingRequestsCurr=(List)entryCurr.getValue();
                        listPendingRequestsOfFriendsPrev=(List)entryOfFriendsPrev.getValue();
                        
                        if(Integer.parseInt(listPendingRequestsPrev.get(2).toString())>0 || Integer.parseInt(listPendingRequestsOfFriendsPrev.get(1).toString())>0){
                            if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\getFriends.txt")==true){
                                strResponse=readFile(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\getFriends.txt");
                                jsonFriendIds=(JSONObject)JSONSerializer.toJSON(strResponse);
                                listFriends=(List)jsonFriendIds.get("ids");
                                for(intFriendCount=0;intFriendCount<listFriends.size();intFriendCount++){
                                    if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\tweets\\getTweets.txt")==false){
                                        intHttpReqCount++;
                                        strResponse=GetTweets("user_id",listFriends.get(intFriendCount).toString());
                                        if(strResponse.equals("UnAuthorized Request")){
                                                intHttpReqCount--;
                                        }
                                        else if(strResponse.equals("Rate Limit Exceeded")){
                                            System.out.print("\n Rate Limit Exceeded");
                                            System.out.print("\n Successfully processed "+intHttpSuccessfulReqCount);

                                            listPendingRequestsCurr.set(2, 1);
                                            mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);

                                            listPendingRequestsCurr.set(4, 1);
                                            mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);    

                                            intTotalPendingRequests=GetCountPendingRequests(mapPendingRequestsCurr);

                                            System.out.print("\n Save Pending Requests of Users");
                                            date= new java.util.Date();
                                            System.out.print("\n "+new Timestamp(date.getTime()));    

                                            SaveUsersPendingRequests(listUsers,mapPendingRequestsCurr,strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");

                                            PrintLog(mapPendingRequestsCurr);

                                            continue top;
                                        }
                                        else if(!strResponse.equals("Erroneous Response")){
                                            SaveTweets(listFriends,intFriendCount,strResponse,strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString());
                                            if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\tweets\\getTweets.txt")==true){
                                                intHttpSuccessfulReqCount++;
                                            }
                                            else{
                                            }
                                        }
                                        else{
                                        }
                                    }
                                    else{
                                        //System.out.println("Tweets of Friend"+listFriends.get(intFriendCount).toString()+" of User "+listUsers.get(intUserCount).toString()+" already present");
                                    }

                                }
                            }
                            else{
                            }
                            
                        }
                        else{
                        }
                        System.out.print("\n Successfully processed "+intHttpSuccessfulReqCount);
                        
                        listPendingRequestsCurr.set(2, Integer.parseInt(listPendingRequestsCurr.get(2).toString())+intHttpReqCount-intHttpSuccessfulReqCount);
                        mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                        
                        if(Integer.parseInt(listPendingRequestsOfFriendsPrev.get(1).toString())>0){
                            listPendingRequestsCurr.set(2, 1);
                            mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                        }
                        if(Integer.parseInt(listPendingRequestsOfFriendsPrev.get(3).toString())>0){
                            listPendingRequestsCurr.set(4, 1);
                            mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                        }
                    }
                }
                else{
                    //System.out.println("List of Users is empty : Save Tweets of Friends of Users not called");
                }

                //</editor-fold>

                System.out.print("\n Get and Save Tweets of Friends of Friends of Users");
                date= new java.util.Date();
                System.out.print("\n "+new Timestamp(date.getTime()));
                //Get Tweets of first 5 Friends of Friends of first 50 Friends of Users
                //<editor-fold defaultstate="collapsed" desc="Get and Save Tweets of Friends of Friends of Users">
                if(listUsers!=null){
                    iteratorMapPrev=mapPendingRequestsPrev.entrySet().iterator();
                    iteratorMapCurr=mapPendingRequestsCurr.entrySet().iterator();
                    iteratorMapOfFriendsPrev=mapPendingRequestsOfFriendsPrev.entrySet().iterator();
                    
                    for(intUserCount=0;intUserCount<listUsers.size();intUserCount++){
                        intHttpReqCount=0;intHttpSuccessfulReqCount=0;

                        entryPrev= (Map.Entry)iteratorMapPrev.next();
                        entryCurr= (Map.Entry)iteratorMapCurr.next();
                        entryOfFriendsPrev=(Map.Entry)iteratorMapOfFriendsPrev.next();
                        
                        strScreenName=(String)entryPrev.getKey();

                        listPendingRequestsPrev=(List)entryPrev.getValue();
                        listPendingRequestsCurr=(List)entryCurr.getValue();
                        listPendingRequestsOfFriendsPrev=(List)entryOfFriendsPrev.getValue();
                        
                        if(Integer.parseInt(listPendingRequestsPrev.get(4).toString())>0|| Integer.parseInt(listPendingRequestsOfFriendsPrev.get(3).toString())>0){
                            if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\getFriends.txt")==true){
                                strResponse=readFile(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\getFriends.txt");
                                jsonFriendIds=(JSONObject)JSONSerializer.toJSON(strResponse);
                                listFriends=(List)jsonFriendIds.get("ids");
                                
                                //get first ten friends of list of friends
                                if(listFriends.size()>50){
                                    intFriendSampleSize=50;
                                }
                                else{
                                    intFriendSampleSize=listFriends.size();
                                }
                                
                                for(intFriendCount=0;intFriendCount<intFriendSampleSize;intFriendCount++){
                                    if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\friends\\getFriends.txt")==true){

                                        strResponse=readFile(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\friends\\getFriends.txt");
                                        jsonFriendIds=(JSONObject)JSONSerializer.toJSON(strResponse);
                                        listFriendofFriends=(List)jsonFriendIds.get("ids");

                                        //get first five friends of list of friends of friends
                                        if(listFriendofFriends.size()>5){
                                            intFriendOfFriendSampleSize=5;
                                        }
                                        else{
                                            intFriendOfFriendSampleSize=listFriendofFriends.size();
                                        }
                                        for(intFriendofFriendCount=0;intFriendofFriendCount<intFriendOfFriendSampleSize;intFriendofFriendCount++){
                                            if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\friends\\user_id\\"+listFriendofFriends.get(intFriendofFriendCount).toString()+"\\tweets\\getTweets.txt")==false){
                                                intHttpReqCount++;
                                                strResponse=GetTweets("user_id",listFriendofFriends.get(intFriendofFriendCount).toString());
                                                if(strResponse.equals("UnAuthorized Request")){
                                                    intHttpReqCount--;
                                                }
                                                else if(strResponse.equals("Rate Limit Exceeded")){
                                                    System.out.print("\n Rate Limit Exceeded");
                                                    System.out.print("\n Successfully processed "+intHttpSuccessfulReqCount);
                                                    
                                                    listPendingRequestsCurr.set(4, 1);
                                                    mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                                                    
                                                    intTotalPendingRequests=GetCountPendingRequests(mapPendingRequestsCurr);
                                                                                                        
                                                    System.out.print("\n Save Pending Requests of Users");
                                                    date= new java.util.Date();
                                                    System.out.print("\n "+new Timestamp(date.getTime()));    
                                                    
                                                    SaveUsersPendingRequests(listUsers,mapPendingRequestsCurr,strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");
                                                    
                                                    PrintLog(mapPendingRequestsCurr);
                                                    
                                                    continue top;
                                                }
                                                else if(!strResponse.equals("Erroneous Response")){
                                                    SaveTweets(listFriendofFriends,intFriendofFriendCount,strResponse,strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\friends\\user_id\\"+listFriendofFriends.get(intFriendofFriendCount).toString());
                                                    if(FileExistenceCheck(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount).toString()+"\\friends\\user_id\\"+listFriendofFriends.get(intFriendofFriendCount).toString()+"\\tweets\\getTweets.txt")==true){
                                                        intHttpSuccessfulReqCount++;
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
                                    }
                                }
                            }
                            else{
                            }
                        }
                        else{
                        }
                        System.out.print("\n Successfully processed "+intHttpSuccessfulReqCount);
                        
                        listPendingRequestsCurr.set(4, Integer.parseInt(listPendingRequestsCurr.get(4).toString())+intHttpReqCount-intHttpSuccessfulReqCount);
                        mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                        
                        if(Integer.parseInt(listPendingRequestsOfFriendsPrev.get(1).toString())>0){
                            listPendingRequestsCurr.set(2, 1);
                            mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                        }
                        if(Integer.parseInt(listPendingRequestsOfFriendsPrev.get(3).toString())>0){
                            listPendingRequestsCurr.set(4, 1);
                            mapPendingRequestsCurr.put(strScreenName, listPendingRequestsCurr);
                        }
                    }
                }
                else{
                    //System.out.println("List of Users is empty : Save Friends of Users not called");
                }
                //</editor-fold>

                System.out.print("\n Save Pending Requests of Users");
                date= new java.util.Date();
                System.out.print("\n "+new Timestamp(date.getTime()));    
                //<editor-fold defaultstate="collapsed" desc="Save Pending Requests of Users">
                SaveUsersPendingRequests(listUsers,mapPendingRequestsCurr,strUrl+"\\users\\getPendingRequests_GetUserTimeline.txt");
                //</editor-fold>
                
                PrintLog(mapPendingRequestsCurr);
                intTotalPendingRequests=GetCountPendingRequests(mapPendingRequestsCurr);
        
                
            }
        }
    }
    
    public static String readFile(String strUrl) throws Exception{
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
    public static String GetResponse(String url){
        //initialization
        HttpGet request;
        HttpClient client;
        HttpResponse response;
        try{
            request = new HttpGet();
            client = new DefaultHttpClient();
            
            request.setURI(new URI(url));
            consumer.sign(request);
            response = client.execute(request);
            
            if(response.getStatusLine().getStatusCode()==200)
            {
                return(IOUtils.toString(response.getEntity().getContent()));
            }
            else if(response.getStatusLine().getStatusCode()==401){
                return "UnAuthorized Request";
            }
            else if(response.getStatusLine().getStatusCode()==429){
                return "Rate Limit Exceeded";
            }
            else
            {
                return "Erroneous Response";
            }
            
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null ;
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
    
    //Get and Save Users        
    public static List GetUsers(String strUrl) {
        String strUsers;
        StringTokenizer stringTokenizer;
        List listUsers;
        try{
            strUsers=readFile(strUrl+"\\users\\getUsers.txt");
            listUsers=new ArrayList<>();
            stringTokenizer = new StringTokenizer(strUsers," ");
            while(stringTokenizer.hasMoreElements()){
                listUsers.add(stringTokenizer.nextElement());
            }
            return(listUsers);       
        }
        catch(Exception e){
             e.printStackTrace();
        }
        return null;    
    }
    public static void SaveUsers(List listUsers,int intUserCount,String strUrl){
        File fileExistenceCheck;
        Boolean boolfileCreationCheck;
        
        fileExistenceCheck=new File(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString());
        if(!fileExistenceCheck.isDirectory()){
            //System.out.println("Folder for ScreenName"+listUsers.get(intUserCount).toString()+" does not exist");
            boolfileCreationCheck=(new File(strUrl+"\\users\\screen_name\\"+listUsers.get(intUserCount).toString())).mkdirs();
            if(boolfileCreationCheck==true){
                //System.out.println("Folder for ScreenName "+listUsers.get(intUserCount).toString()+" created successfully");
            }
            else{
                //System.out.println("Folder for ScreenName "+listUsers.get(intUserCount).toString()+" creation failed");
            }
        }
        else{
            //System.out.println("Folder for ScreenName"+listUsers.get(intUserCount).toString()+" already exists");
        }
        
    }
    
    //Get and Count total pending requests of users
    
    public static Map<String,List> GetUsersPendingRequests(String strUrl)throws Exception{
        String line;
        BufferedReader reader;
        List listPendingRequests;
        Map<String,List> mapPendingRequests = new HashMap<>();
        int intPendingRequestTypeCount;
        
        
        reader=new BufferedReader(new FileReader(strUrl));
        while((line=reader.readLine())!=null){
            
            listPendingRequests = new ArrayList<>();
            String[] strCSV=line.split(",");
            
            for(intPendingRequestTypeCount=0;intPendingRequestTypeCount<strCSV.length-1;intPendingRequestTypeCount++){
                listPendingRequests.add(intPendingRequestTypeCount,Integer.parseInt(strCSV[intPendingRequestTypeCount+1]));
            }
            mapPendingRequests.put(strCSV[0], listPendingRequests);
            
        }
        return mapPendingRequests;
    }
    public static void PrintMap(Map mapPendingRequests){
        String strScreenName;
        List listPendingRequests;
        List temp;
        
        Iterator<Entry<String, List>> iteratorMap=mapPendingRequests.entrySet().iterator();
        int intPendingRequestTypeCount;
        
        while(iteratorMap.hasNext()){
            Map.Entry<String, List> entry= (Map.Entry)iteratorMap.next();
            
            strScreenName=(String)entry.getKey();
            System.out.print("\n"+strScreenName);
            
            listPendingRequests = (List) mapPendingRequests.get(strScreenName);
            for (intPendingRequestTypeCount=0;intPendingRequestTypeCount<listPendingRequests.size();intPendingRequestTypeCount++){
                System.out.print(" "+listPendingRequests.get(intPendingRequestTypeCount));
            }
       }
        
    }
    public static Map<String,List> GetResetHashMap(String strUrl)throws Exception{
        String line;
        BufferedReader reader;
        List listPendingRequests;
        Map<String,List> mapPendingRequests = new HashMap<>();
        int intPendingRequestTypeCount;
        
        
        reader=new BufferedReader(new FileReader(strUrl));
        while((line=reader.readLine())!=null){
            
            listPendingRequests = new ArrayList<>();
            String[] strCSV=line.split(",");
            
            for(intPendingRequestTypeCount=0;intPendingRequestTypeCount<strCSV.length-1;intPendingRequestTypeCount++){
                listPendingRequests.add(intPendingRequestTypeCount,0);
            }
            mapPendingRequests.put(strCSV[0], listPendingRequests);
            
        }
        return mapPendingRequests;
    }
    //<editor-fold defaultstate="collapsed" desc="int GetCountPendingRequests(Map mapPendingRequests)">
    public static int GetCountPendingRequests(Map mapPendingRequests){
        String strScreenName;
        List listPendingRequests;
        int intPendingRequestsCount=0;
        Iterator<Entry<String, List>> iteratorMap=mapPendingRequests.entrySet().iterator();
        int intPendingRequestTypeCount;
        
        while(iteratorMap.hasNext()){
            Map.Entry<String, List> entry= (Map.Entry)iteratorMap.next();
            
            strScreenName=(String)entry.getKey();
            
            listPendingRequests = (List) mapPendingRequests.get(strScreenName);
            for (intPendingRequestTypeCount=0;intPendingRequestTypeCount<listPendingRequests.size();intPendingRequestTypeCount++){
                intPendingRequestsCount+=Integer.parseInt(listPendingRequests.get(intPendingRequestTypeCount).toString());
            }
        }
        return intPendingRequestsCount;
    }
    //</editor-fold>
    
    //Get and Save Tweets 
    public static String GetTweets(String Key,String Value){
        String url;
        String strTweets;
        try{
            url="http://api.twitter.com/1.1/statuses/user_timeline.json?"+Key+"="+Value+"&count=200";
            strTweets=GetResponse(url);
            return(strTweets);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void SaveTweets(List listUsers,int intUserCount,String strResponse,String strUrl) throws Exception{
        File fileExistenceCheck;
        Boolean boolfileCreationCheck;
        
        fileExistenceCheck=new File(strUrl+"\\tweets");
        if(!fileExistenceCheck.isDirectory()){
            //System.out.println("Folder for Tweets "+listUsers.get(intUserCount).toString()+" does not exist");
            boolfileCreationCheck=(new File(strUrl+"\\tweets")).mkdirs();
            if(boolfileCreationCheck==true){
                //System.out.println("Folder for Tweets "+listUsers.get(intUserCount).toString()+" created successfully");
            }
            else{
                //System.out.println("Folder for Tweets "+listUsers.get(intUserCount).toString()+" creation failed");
            }
        }
        else{
            //System.out.println("Folder for Tweets "+listUsers.get(intUserCount).toString()+" already exists");
        }
        
        File file=new File(strUrl+"\\tweets\\getTweets.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(strResponse);
        output.close();
    }
       
    //Get and Save Friends
    public static String GetFriends(String Key,String Value) throws Exception{
        //initialization
        String url;
        String strFriendIds;
        
        try{
            url="http://api.twitter.com/1.1/friends/ids.json?"+Key+"="+Value;
            
            strFriendIds=GetResponse(url);
            
           
            return strFriendIds;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null ;
    }
    public static void SaveFriends(List listUsers,int intUserCount,List listFriends,String strUrl){
        File fileExistenceCheck;
        Boolean boolfileCreationCheck;
        int intFriendCount;
        
        for(intFriendCount=0;intFriendCount<listFriends.size();intFriendCount++){
            fileExistenceCheck=new File(strUrl+"\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount));
            if(!fileExistenceCheck.isDirectory()){
                //System.out.println(" Folder for user_id "+listFriends.get(intFriendCount).toString()+" does not exist");
                boolfileCreationCheck=(new File(strUrl+"\\"+listUsers.get(intUserCount).toString()+"\\friends\\user_id\\"+listFriends.get(intFriendCount))).mkdirs();
                if(boolfileCreationCheck==true){
                    //System.out.println(" Folder for user_id "+listFriends.get(intFriendCount).toString()+" created successfully");
                }
                else{
                    //System.out.println(" Folder for user_id "+listFriends.get(intFriendCount).toString()+" creation failed");
                }
            }
        }
        
    }
    public static void SaveFriends(List listUsers,int intUserCount,String strResponse,String strUrl) throws Exception{
        File file=new File(strUrl+"\\"+listUsers.get(intUserCount).toString()+"\\friends\\getFriends.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(strResponse);
        output.close();
    }
  
    //Save pending requests
    public static void SaveUsersPendingRequests(List listUsers,Map mapPendingRequestsCurr,String strUrl) throws Exception{
        
        File file=new File(strUrl);
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        
        String strScreenName;
        List listPendingRequestsCurr;
        Iterator iteratorMap=mapPendingRequestsCurr.entrySet().iterator();
        int intPendingRequestTypeCount;
        
        while(iteratorMap.hasNext()){
            Map.Entry entry= (Map.Entry)iteratorMap.next();
            strScreenName=(String)entry.getKey();
            listPendingRequestsCurr=(List)entry.getValue();
            output.append(strScreenName);
            for (intPendingRequestTypeCount=0;intPendingRequestTypeCount<listPendingRequestsCurr.size();intPendingRequestTypeCount++){
                output.append(","+listPendingRequestsCurr.get(intPendingRequestTypeCount).toString());
            }
            output.newLine();
        }
        
        output.close();
    }
 
    //<editor-fold defaultstate="collapsed" desc="void PrintLog(Map mapPendingRequestsCurr)">
    public static void PrintLog(Map mapPendingRequestsCurr){
        System.out.print("\n Curr: ");
        PrintMap(mapPendingRequestsCurr);
        
        int intTotalPendingRequests=GetCountPendingRequests(mapPendingRequestsCurr);
        System.out.print("\n Count: "+intTotalPendingRequests + " ");
        
        java.util.Date date= new java.util.Date();
        System.out.print(new Timestamp(date.getTime()));
        
        System.out.println("\n");
        System.out.println("-----------------");
        System.out.println("\n");
    }
    //</editor-fold>
}
