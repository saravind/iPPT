import java.net.*;
import java.util.ArrayList;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Mendeley_Reader {
   
   public final static  int READ = 1;
   public final static int RELATED = 2;
   public final static String consumerkey = "bc25bbe40aa9f44e93c126d6096d39d304fbe3528";
   public ArrayList<String> authors = new ArrayList<String>();
   public ArrayList<String> related_titles = new ArrayList<String>();
   public ArrayList<String> related_author = new ArrayList<String>();
   public static boolean status = false;
   public String title;
   
   
   public Mendeley_Reader(String title)
   {
	   this.title = title;
	   process(this.title);
	   
   }
   
     
   public URL formURL(int args,String query) throws URISyntaxException, MalformedURLException
   {
	   URI uri = null;
	   switch(args)
	   {
	     case Mendeley_Reader.READ: uri = new URI("http","api.mendeley.com","/oapi/documents/search/title:"+query+"/","consumer_key="+consumerkey,null); 
	     break;
	     case Mendeley_Reader.RELATED:  	 
	     uri = new URI("http","api.mendeley.com","/oapi/documents/related/"+query+"/","consumer_key="+consumerkey,null); 
	     break;
	   
	   }
      return uri.toURL();

   }
   
   
   
   
   public boolean getRelated(String content) throws ParseException
   {
	   String uuid = null;
	   JSONParser parser = new JSONParser();
	   Object obj = parser.parse(content);
	   JSONObject jobj = (JSONObject)obj;
	   
	   if(jobj.get("total_results")!=null)
	   {
	   JSONArray jarray = (JSONArray)jobj.get("documents");
	   if(jarray.size() > 0)
	   {
		 for(Object obj_arr: jarray)
		 {
			JSONObject jobject =  (JSONObject)obj_arr;
			related_titles.add((String)jobject.get("title"));
			JSONArray jarr = (JSONArray)jobject.get("authors");
			StringBuffer sbBuffer = new StringBuffer();
			for(Object obj_auth:jarr)
			{
				  JSONObject auth = (JSONObject)obj_auth;
				  String forename = (String)auth.get("forename");
				  String surname = (String)auth.get("surname");
				  sbBuffer.append(forename + " "+ surname + ";");
			}
			related_author.add(sbBuffer.toString());
				
		 }
		 
		   
	   }	   
	    return true;
	  }
	  else
      {
	    return false;
      } 	   
   }
   
   
   
   public String getUUID(String str) throws ParseException
   {
	   String uuid = null;
	   JSONParser parser = new JSONParser();
	   Object obj = parser.parse(str);
	   JSONObject jobj = (JSONObject)obj;
	   
	   if(jobj.get("total_results")!=null)
	   {
	   JSONArray jarray = (JSONArray)jobj.get("documents");
	   if(jarray.size() > 0)
	   {
		  
		  JSONObject jso =  (JSONObject)jarray.get(0);
		  uuid = (String)jso.get("uuid");
		  this.title = (String)jso.get("title");
		  JSONArray jarr = (JSONArray)jso.get("authors");
		  for(Object objects:jarr)
		  { 
			  JSONObject auth = (JSONObject)objects;
			  String forename = (String)auth.get("forename");
			  String surname = (String)auth.get("surname");
			  authors.add(forename + " " + surname);
			  
		  }
		  
		  
	   }
	  }
	  
	  
	   return uuid;
   }
   
   
   public void process(String title)
   {
	   try
	   {
		   URL fetch = null;
		   fetch = formURL(Mendeley_Reader.READ,title);
		   HttpURLConnection yc = (HttpURLConnection)fetch.openConnection();
	       BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		   StringBuffer sb = new StringBuffer();
	       String inputLine = null;
	       while ((inputLine = in.readLine()) != null) 
	       {
		        sb.append(inputLine);	   
		   }
	       in.close();
		   
		   String uuid = getUUID(sb.toString());
		   
		   if(uuid !=null)
		   {
			fetch = formURL(Mendeley_Reader.RELATED,uuid);
		   
			yc = (HttpURLConnection)fetch.openConnection();
			in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			sb = new StringBuffer();
			inputLine = null;
			while ((inputLine = in.readLine()) != null) 
			{
		        sb.append(inputLine);	   
			}
			in.close();
		   
	       if(getRelated(sb.toString()))
				status = true;
	      }
         		  
	   }
	   catch(Exception ex)
	   {
		
		   
	   }
	   
   }
   
   
   
   
   
    public static void main(String[] args) throws Exception {
        
        
        Mendeley_Reader mr = new Mendeley_Reader("Automatic Summarization");
        
       if(mr.status)
       {
		System.out.println(mr.authors);
		System.out.println(mr.title);
		System.out.println(mr.related_titles);
		System.out.println(mr.related_author);
       }
       else
       {
    	   System.out.println("Error!!");
       }
		
 }
 
}