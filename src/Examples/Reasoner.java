package Examples;

/****	IMPORT	****/
/* Import Packages / Libraries */
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import Examples.AppleStore;
import Examples.Product;
import Examples.Service;
import Examples.Discount;
import Examples.Store;


import Examples.SimpleGUI;

public class Reasoner {
	

	/* Main class Object that holds the domain knowledge
	 * Will generate classes automatically 
	 * 
	 **/
	
	/*	DECLARE VARIABLES, LISTS etc.	*/

	public SimpleGUI Myface;	

	public AppleStore theAppleStore;


	/*	THE LISTS HOLDING CLASS INSTANCES OF ALL DOMAIN ENTITIES	*/
	public List theAppleStoreList,theStoreList,theProductList,theServiceList,theDiscountList,theRecentThing = new ArrayList();

	/*	VECTORS - ARRAYS OF OBJECTS	*/
	public Vector<String> appleStoreSyn = new Vector<String>(); 	
	public Vector<String> storeSyn = new Vector<String>();   
	public Vector<String> productSyn = new Vector<String>();
	public Vector<String> serviceSyn = new Vector<String>();
	public Vector<String> discountSyn = new Vector<String>();   
	public Vector<String> recentobjectsyn = new Vector<String>();

	/*	QUESTIONTYPE SELECTS METHOD TO USE IN QUERY	*/
	public String questiontype = "";         
	/*	CLASSTYPE SELECTS WHICH CLASS LIST TO QUERY	*/
	public List classtype = new ArrayList(); 
	/*	ATTRIBUTE TYPE SELECTS THE ATTRIBUTE TO CHECK FOR IN THE QUERY	*/
	public String attributetype = "";        

	/*	LAST OBJECT DEAL WITH	*/
	public Object Currentitemofinterest;
	/* LAST INDEX TO BE USED	*/
	public Integer Currentindex;         

	public String tooltipstring = "";
	
	/*	MAIN QUESTION WORDS	*/
	public String list = "list";
	public String amount = "amount";
	public String checkfor = "checkfor";
	
	
	/*	URL FOR WEBSITE */
	public String URL = "";              
//	public String URL2 = "";        
	
	/*	SET WEBSITE URL */
	public String setURL(){
		String websiteURL = "http://en.wikipedia.org/wiki/";
		return websiteURL;
	}
	
	/*	GET WEBSITE URL	*/
	public String getURL(){
		return setURL();
	}
	
	/* ACCEPTS STRING ARGUMENT TO BE DIPSPLAYED/PRINTED
	 * Each times when use this function we save 15 character for not using System.out.println().....
	 * */
	public void sop(String str){
		System.out.println(str);
	}
	
	/*
	 * Define specific Vector type i.e. productSyn
	 * Defines all specific words for products,services,stores,discounts.
	 *  
	 * */
	public void addWordsToVector(Vector<String> vector, String... args) {
		
		String[] askFor=null;
	    for (String arg : args) {
	    	/* Define all words that regards to products,services,discounts,stores */
	    	askFor = args;
	    }
	    vector.addAll(Arrays.asList(askFor));
	}
	
	/*Returns an item already wrapped up in <li> tag*/
	public String listItem(String listItem){
		return "<li>"+listItem+"</li>";
	}

	public Reasoner(SimpleGUI myface) {
		
		/*	REFERENCE TO GUI TO UPDATE TOOLTIP-TEXT*/
		Myface = myface; 
	}

	/*Load all the Apple Store knowledge from XML*/
	public void initknowledge() { 

		/*  Instance of our parser*/
		JAXB_XMLParser xmlhandler = new JAXB_XMLParser();

		/* Load AppleStore XML File */
		File xmlfiletoload = new File("AppleStore.xml");  

		/*	Vector type + specific words to be listen	*/
		/*Store*/
		addWordsToVector(storeSyn,"store","stores");
		/*Product*/
		addWordsToVector(productSyn,"products","product","items","item");
		/*Service*/
		addWordsToVector(serviceSyn,"service","services","apple care","insurance","camp","training","workshop");		
		/*Discount*/
		addWordsToVector(discountSyn,"discount","discounts","student");
		/*Recent Object - Spaces to prevent collision with "wHERe"*/
		addWordsToVector(recentobjectsyn," this"," that"," him"," her", "it");


		try {
			/* Initiate input stream */
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload);

			theAppleStore = xmlhandler.loadXML(readthatfile);
			
			theStoreList = theAppleStore.getStore();
			theProductList = theAppleStore.getProduct();  		
			theServiceList = theAppleStore.getService();  		
			theDiscountList = theAppleStore.getDiscount();  				
			theAppleStoreList.add(theAppleStore);             

			sop("List reading");
		}

		catch (Exception e) {
			e.printStackTrace();
			sop("error in init");
		}
	}

	/*
	 * It accepts question type and an input + any number of arguments 
	 * and do something with it. Specified Strings will be displayed in bold.
	 *
	 *Example use: 
	 * 	acceptQuestionType(input, list, "list all","display all","show all");
	 * 
	 * */
	public String acceptQuestionType(String input, String two, String... args){
		
	    for (String arg : args) {	    	
	    	if (input.contains(arg)){questiontype = two; 
			input = input.replace(arg, "<b>"+arg+"</b>");} 		
	    }
		return input;
	}
	
	/*  Foreach each answer get answer */
	public String getAnswer(String input, Vector<String> arg1, ArrayList agr2){
		
		/*returns the String, converted to lowercase.*/
		input = input.toLowerCase(); 
		Integer subjectcounter = 0;
		
		for (int x = 0; x < arg1.size(); x++) {   
			if (input.contains((CharSequence) arg1.get(x))) {    
				classtype = agr2;             
				
				input = input.replace((CharSequence) arg1.get(x), "<b>"+arg1.get(x)+"</b>");
				
				subjectcounter = 1;
				sop("Class type" + agr2 + "recognised.");
			}
		}
		return input;
	}
	
	public  Vector<String> generateAnswer(String input) { // Generate an answer (String Vector)

		Vector<String> out = new Vector<String>();
		out.clear();                 // just to make sure this is a new and clean vector
		
		questiontype = "none";

		/*Check if answer was generated*/
		Integer Answered = 0;        

		/*Counter to keep track of # of identified subjects (classes)*/
		Integer subjectcounter = 0;  
		
		// Answer Generation Idea: content = Questiontype-method(classtype class) (+optional attribute)

		/*All in lower case because thats easier to analyse*/
		input = input.toLowerCase();
		
		// ___________________________________________________________________

		String answer = "";          // the answer we return

		// ----- Check for the kind of question (number, location, etc)------------------------------

		/*CHECK FOR A 
		 * 
		 * Below Examples are the same initial code/example as:
		 * if (input.contains("how many")){questiontype = "amount"; input = input.replace("how many", "<b>how many</b>");} 
		 * */		
		acceptQuestionType(input, list, "list","list all","display","display all","show","show all");
		acceptQuestionType(input, amount, "how many","number of","amount of","count");
		acceptQuestionType(input, checkfor, "is there a","i am searching","i am looking for","do you have","i look for","is there");
	
		if (input.contains("where") 
				|| input.contains("can't find")
				|| input.contains("can i find") 
				|| input.contains("way to"))

		{
			questiontype = "location";
			sop("Find Location");
		}
		
		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("cool thank")) 			

		{
			questiontype = "farewell";
			sop("farewell");
		}


		/*CHECKING THE SUBJECT OF THE QUESTION */
		/* Store*/
		getAnswer(input, storeSyn, (ArrayList) theStoreList);
		/* Products */
		getAnswer(input, productSyn, (ArrayList) theProductList);
		/* Service */
		getAnswer(input, serviceSyn, (ArrayList) theServiceList);
		/* Discount */
		getAnswer(input, discountSyn, (ArrayList) theDiscountList);
		
		
		
		if(subjectcounter == 0){
			for (int x = 0; x < recentobjectsyn.size(); x++) {  
				if (input.contains(recentobjectsyn.get(x))) {
					classtype = theRecentThing;
					
					input = input.replace(recentobjectsyn.get(x), "<b>"+recentobjectsyn.get(x)+"</b>");
					
					subjectcounter = 1;
					sop("Class type recognised as"+recentobjectsyn.get(x));
				}
			}
		}

		// More than one subject in question + Apple Store

		sop("subjectcounter = "+subjectcounter);

		for (int x = 0; x < appleStoreSyn.size(); x++) {  

			if (input.contains(appleStoreSyn.get(x))) {   
				
				if (subjectcounter == 0) { // AppleStore is the first subject in the question
					
					input = input.replace(appleStoreSyn.get(x), "<b>"+appleStoreSyn.get(x)+"</b>");
					
					classtype = theAppleStoreList;   

					sop("class type AppleStore recognised");		

				}
			}
		}

		// Compose Method call and generate answerVector

		/*Number of Subject*/
		if (questiontype == "amount") {

			Integer numberof = Count(classtype);

			answer=("The number of "
					+ classtype.get(0).getClass().getSimpleName() + "s is "
					+ numberof + ".");

			
			Answered = 1; 

		}

		if (questiontype == "list") { // List all Subjects of a kind

			answer=("You asked for the listing of all "
					+ classtype.get(0).getClass().getSimpleName() + "s. <br>"
					+ "We have the following "
					+ classtype.get(0).getClass().getSimpleName() + "s:"
					+ ListAll(classtype));
			
			Answered = 1; 

		}

		/*Test for a certain Subject instance*/
		if (questiontype == "checkfor") {

			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			/* An answer Given*/
			Answered = 1; 
			if (check.size() > 1) {
				Currentitemofinterest = classtype.get(Integer.valueOf(check
						.get(1)));
				sop("Classtype List = "
						+ classtype.getClass().getSimpleName());
				sop("Index in Liste = "
						+ Integer.valueOf(check.get(1)));
				Currentindex = Integer.valueOf(check.get(1));
				/*Clear it before adding (changing) the*/
				theRecentThing.clear(); 
				/*Recent thing*/
				theRecentThing.add(classtype.get(Currentindex));
			}
		}

		// Location Question in Pronomial form "Where can i find it"

		if (questiontype == "location") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Location(classtype, input));

			Answered = 1; 
		}

		if (questiontype == "farewell") {       // Reply to a farewell
			
			answer=("You are welcome.");

			Answered = 1; // An answer was given
		}
		
		
		if (Answered == 0) { // No answer was given

			answer=("Sorry I didn't understand that.");
		}

		out.add(input);
		out.add(answer);
		
		return out;
	}

	
	// Answer a question of the "How many ...." kind 
	
	public Integer Count(List thelist) { // List "thelist": List of Class Instances (e.g. theProductList)

		//URL = "http://en.wiktionary.org/wiki/"		

		URL = getURL() + classtype.get(0).getClass().getSimpleName().toLowerCase();
		sop("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL);

		return thelist.size();
	}

	// Answer a question of the "What kind of..." kind
	
	public String ListAll(List thelist) {

		String listemall = "<ul>";
		
		if (thelist == theStoreList) {                                 
			for (int i = 0; i < thelist.size(); i++) {
				Store curStore = (Store) thelist.get(i);                
				listemall = listemall + "<li>" + (curStore.getStoreName() + "</li>"); 
			}
		}
		
		if (thelist == theProductList) {                                 
			for (int i = 0; i < thelist.size(); i++) {
				Product curProduct = (Product) thelist.get(i);              
				listemall = listemall + "<li>" + (curProduct.getProductType() + "</li>"); 
			}
		}
		
		if (thelist == theServiceList) {                               
			for (int i = 0; i < thelist.size(); i++) {
				Service curService = (Service) thelist.get(i);                 
				listemall = listemall + "<li>" + (curService.getServiceName() + "</li>"); 
			}
		}
		
		if (thelist == theDiscountList) {                               
			for (int i = 0; i < thelist.size(); i++) {
				Discount curDiscount = (Discount) thelist.get(i);                 
				listemall = listemall + "<li>" + (curDiscount.getDiscountType() + "</li>");  
			}
		}

		
		listemall += "</ul>";

		URL = getURL() + classtype.get(0).getClass().getSimpleName().toLowerCase();
		sop("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL);
		
		return listemall;
	}

	// Answer a question of the "Do you have..." kind 
	
	public Vector<String> CheckFor(List thelist, String input) {

		Vector<String> yesorno = new Vector<String>();
		String answer="";
		if (classtype.isEmpty()){
			yesorno.add("Class not recognised. Please specify if you are searching for a product, stores, services, or discounts?");
		} else {
			yesorno.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (thelist == theProductList) {                   

			for (int i = 0; i < thelist.size(); i++) {

				Product curProduct = (Product) thelist.get(i);                           

				if (input.contains(curProduct.getProductName().toLowerCase())            
						|| input.contains(curProduct.getSerialNumber().toLowerCase())      
						|| input.contains(curProduct.getProductType().toLowerCase())) {  		
					
					answer+="<li><strong>Product Name:</strong>" + curProduct.getProductName() + "</li>"+
							"<li><strong>Product Type:</strong>"+curProduct.getProductType()+ "</li>"+
							"<li><strong>Serial Number:</strong>"+curProduct.getSerialNumber()+ "</li>"+
							"<li><strong>Location:</strong>"+curProduct.getLocation()+ "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Product"+answer);
					
				}
			}
		}
		
		if (thelist == theServiceList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Service curService = (Service) thelist.get(i);                           

				if (input.contains(curService.getServiceName().toLowerCase())) {  

					answer+="<li><strong>Service:</strong>" + curService.getServiceName() + "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Service"+answer);
					
				}
			}
		}
		
		if (thelist == theDiscountList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Discount curDiscount = (Discount) thelist.get(i);                           

				if (input.contains(curDiscount.getDiscountType().toLowerCase())) {  
						
					answer+="<li><strong>Discount Type:</strong>" + curDiscount.getDiscountType() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curDiscount.getDiscountPercentage() + "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Discount"+answer);
					
				}
			}
		}
		
		if (thelist == theStoreList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Store curStore = (Store) thelist.get(i);                           

				if (input.contains(curStore.getStoreName().toLowerCase())
						|| input.contains(curStore.getCity().toLowerCase())) {  

					answer+="<li><strong>Discount Type:</strong>" + curStore.getStoreName() + "</li>"+
							"<li><strong>City:</strong>" + curStore.getCity() + "</li>"+
							"<li><strong>Street:</strong>" + curStore.getStreet() + "</li>"+
							"<li><strong>House Number:</strong>" + curStore.getHouseNumber() + "</li>"+
							"<li><strong>Postcode:</strong>" + curStore.getPostcode() + "</li>"+
							"<li><strong>Phone Number:</strong>" + curStore.getPhoneNumber() + "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Check our London stores below:"+answer);
				}
			}
		}
		

		if (classtype.isEmpty()) {
			sop("Not class type given.");
		} else {
			URL = getURL() + classtype.get(0).getClass().getSimpleName().toLowerCase();			
			sop("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL);
		}
		
		sop(answer);		
		return yesorno;
	}

	//  Method to retrieve the location information from the object (Where is...) kind

	public String Location(List classtypelist, String input) {

		List thelist = classtypelist;
		String location = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("store")) {                  

				Store curStore = (Store) theRecentThing.get(0);          
				location = (curStore.getStoreName() + " "+ curStore.getCity()  + curStore.getHouseNumber() ); 

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("product")) {                 

				Product curProduct = (Product) theRecentThing.get(0);       
				location = (curProduct.getLocation() + " ");            

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("service")) {                 

				Service curService = (Service) theRecentThing.get(0);         
				location = (curService.getServiceName() + " ");             

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("discount")) {                

				Discount curDiscount = (Discount) theRecentThing.get(0);        
				location = (curDiscount.getDiscountType() + " ");            

			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("appleStore")) {                 

				location = (theAppleStore.getCity() + " " + theAppleStore.getStreet() + theAppleStore
						.getHousenumber());                                          
			}
		}

		// if a direct noun was used (product, store, etc)

		else {
			
			if (thelist == theStoreList) {                         

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Store curStore = (Store) thelist.get(i);        

					if (input.contains(curStore.getStoreName().toLowerCase())      
							|| input.contains(curStore.getCity().toLowerCase())) {

						counter = i;
						location = (curStore.getStoreName() + " ");
						Currentindex = counter;
						/*Clear it before adding (changing) theRecentThing*/
						theRecentThing.clear(); 									
						classtype = theServiceList;                                   
						theRecentThing.add(classtype.get(Currentindex));
						/*Force break*/
						i = thelist.size() + 1; 						
					}
				}
			}
		
			if (thelist == theProductList) {                        

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Product curProduct = (Product) thelist.get(i);       

					if (input.contains(curProduct.getProductName().toLowerCase())           
							|| input.contains(curProduct.getSerialNumber().toLowerCase())   
							|| input.contains(curProduct.getProductType().toLowerCase())) { 

						counter = i;
						location = (curProduct.getLocation() + " ");
						Currentindex = counter;
						/*Clear it before adding (changing) theRecentThing*/
						theRecentThing.clear(); 									
						classtype = theProductList;                                    
						theRecentThing.add(classtype.get(Currentindex));
						/*Force break*/
						i = thelist.size() + 1; 									
					}
				}
			}
			
			if (thelist == theServiceList) {                        

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Service curService = (Service) thelist.get(i);       

					if (input.contains(curService.getServiceName().toLowerCase())      
							|| input.contains(curService.getPriceRange().toLowerCase())) { 

						counter = i;
						location = (curService.getServiceName() + " ");
						Currentindex = counter;
						/*Clear it before adding (changing) theRecentThing*/
						theRecentThing.clear(); 									
						classtype = theServiceList;                                 
						theRecentThing.add(classtype.get(Currentindex));
						/*Force break*/
						i = thelist.size() + 1; 									
					}
				}
			}

			if (thelist == theAppleStoreList) {                                           

				location = (theAppleStore.getCity() + " " + theAppleStore.getStreet() + theAppleStore  
						.getHousenumber());                                                   
			}
		}

		URL = getURL() + classtype.get(0).getClass().getSimpleName().toLowerCase();
		sop("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL);

		return location;
	}

	
	/* Searches for specific thing with the class and displays information about the products. */	
	public String searchSpecificProduct(String input){
		String answer = "";		
		/*Check each product in the List */
		for (int i = 0; i < theProductList.size(); i++) { 

			/* Cast list element to Product Class */
			Product curProduct = (Product) theProductList.get(i);   												
			sop("Testing Product" + curProduct.getProductType());

			if(input.contains(curProduct.getProductType().toLowerCase())) {     

				answer = "A Product " + curProduct.getProductType() + "\n"
						+ " is for example the classic " + curProduct.getProductName()
						+ ".";
			}
		}	
		return answer;
	}
	
	/* Searches for specific thing with the class and displays information of about hte Service. */
	public String searchSpecificService(String input){
		String answer = "";		

		for (int i = 0; i < theServiceList.size(); i++) {   

			Service curService = (Service) theServiceList.get(i);   												
			sop("Testing Services" + curService.getServiceName());

			if(input.contains(curService.getServiceName().toLowerCase())) {     

				answer = "A Service" + curService.getServiceName() + "\n" 
						+ " is for example the classic " + curService.getPriceRange()      
						+ ".";
			}
		}	
		return answer;
	}

	public String readwebsite(String url) {

		String webtext = "";
		try {
			BufferedReader readit = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			String lineread = readit.readLine();

			sop("Reader okay");

			while (lineread != null) {
				webtext = webtext + lineread;
				lineread = readit.readLine();				
			}
			
			webtext = webtext.substring(webtext.indexOf("<ul>"),webtext.indexOf("</ul>"));

			webtext = "<table width=\"700\"><tr><td>" + webtext
					+ "</ul></td></tr></table>";

		} catch (Exception e) {
			webtext = "Not yet";
			sop("Error connecting to Wikipedia");
		}
		return webtext;
	}
}
