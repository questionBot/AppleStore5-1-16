package Examples;

/****	IMPORT	****/
/* Import Packages / Libraries */
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import Examples.Library;

import Examples.Book;

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

	public Library thelibrary;


	/*	THE LISTS HOLDING CLASS INSTANCES OF ALL DOMAIN ENTITIES	*/
	public List theLibraryList,theBookList,theStoreList,theProductList,theServiceList,theDiscountList,theMemberList,theCatalogList,theLendingList,theRecentThing = new ArrayList();

	/*	VECTORS - ARRAYS OF OBJECTS	*/
	public Vector<String> librarysyn = new Vector<String>(); 	
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
	public String URL2 = "";        
	
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
	 * Define specific Vector type i.e. booksyn
	 * Defines all specific words for book.
	 *  
	 * */
	public void addWordsToVector(Vector<String> vector, String... args) {
		
		String[] askFor=null;
	    for (String arg : args) {
	    	/* Define all words that regards to books */
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

	
	public void initknowledge() { // load all the library knowledge from XML 

		JAXB_XMLParser xmlhandler = new JAXB_XMLParser(); // we need an instance of our parser

		//This is a candidate for a name change
		File xmlfiletoload = new File("Library.xml"); // we need a (CURRENT)  file (xml) to load  

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
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload); // initiate input stream

			thelibrary = xmlhandler.loadXML(readthatfile);

			// Fill the Lists with the objects data just generated from the xml

			theBookList = thelibrary.getBook();  
			theStoreList = thelibrary.getStore();
			theProductList = thelibrary.getProduct();  		
			theServiceList = thelibrary.getService();  		
			theDiscountList = thelibrary.getDiscount();  				
			theLibraryList.add(thelibrary);             

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

		Integer Answered = 0;        // check if answer was generated

		Integer subjectcounter = 0;  // Counter to keep track of # of identified subjects (classes)
		
		// Answer Generation Idea: content = Questiontype-method(classtype class) (+optional attribute)

		// ___________________________ IMPORTANT _____________________________

		input = input.toLowerCase(); // all in lower case because thats easier to analyse
		
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
			System.out.println("Find Location");
		}
		if (input.contains("can i lend") 
				|| input.contains("can i borrow")
				|| input.contains("can i get the book")
				|| input.contains("am i able to")
				|| input.contains("could i lend") 
				|| input.contains("i want to lend")
				|| input.contains("i want to borrow"))

		{
			questiontype = "intent";
			System.out.println("Find ProductAvailability");
		}
		
		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("cool thank")) 			

		{
			questiontype = "farewell";
			System.out.println("farewell");
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
					System.out.println("Class type recognised as"+recentobjectsyn.get(x));
				}
			}
		}

		// More than one subject in question + Library ...
		// "Does the Library has .. Subject 2 ?"

		System.out.println("subjectcounter = "+subjectcounter);

		for (int x = 0; x < librarysyn.size(); x++) {  

			if (input.contains(librarysyn.get(x))) {   
				
				if (subjectcounter == 0) { // Library is the first subject in the question
					
					input = input.replace(librarysyn.get(x), "<b>"+librarysyn.get(x)+"</b>");
					
					classtype = theLibraryList;   

					System.out.println("class type Library recognised");		

				}
			}
		}

		// Compose Method call and generate answerVector

		if (questiontype == "amount") { // Number of Subject

			Integer numberof = Count(classtype);

			answer=("The number of "
					+ classtype.get(0).getClass().getSimpleName() + "s is "
					+ numberof + ".");

			Answered = 1; // An answer was given

		}

		if (questiontype == "list") { // List all Subjects of a kind

			answer=("You asked for the listing of all "
					+ classtype.get(0).getClass().getSimpleName() + "s. <br>"
					+ "We have the following "
					+ classtype.get(0).getClass().getSimpleName() + "s:"
					+ ListAll(classtype));
			Answered = 1; // An answer was given

		}

		if (questiontype == "checkfor") { // test for a certain Subject instance

			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			Answered = 1; // An answer was given
			if (check.size() > 1) {
				Currentitemofinterest = classtype.get(Integer.valueOf(check
						.get(1)));
				System.out.println("Classtype List = "
						+ classtype.getClass().getSimpleName());
				System.out.println("Index in Liste = "
						+ Integer.valueOf(check.get(1)));
				Currentindex = Integer.valueOf(check.get(1));
				theRecentThing.clear(); // Clear it before adding (changing) the
				// now recent thing
				theRecentThing.add(classtype.get(Currentindex));
			}
		}

		// Location Question in Pronomial form "Where can i find it"

		if (questiontype == "location") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Location(classtype, input));

			Answered = 1; // An answer was given
		}

//		if ((questiontype == "intent" && classtype == theBookList) 
//				||(questiontype == "intent" && classtype == theRecentThing)) {
//
//			// Can I lend the book or not (Can I lent "it" or not)
//			answer=("You "+ BookAvailable(classtype, input));
//			Answered = 1; // An answer was given
//		}
		
		
//		if ((questiontype == "intent" && classtype == theProductList) 
//				||(questiontype == "intent" && classtype == theRecentThing)) {
//
//			// Can I lend the book or not (Can I lent "it" or not)
//			answer=("You "+ ProductAvailable(classtype, input));
//			Answered = 1; // An answer was given
//		}

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

	// Methods to generate answers for the different kinds of Questions
	
	// Answer a question of the "Is a book or "it (meaning a book) available ?" kind

//	public String BookAvailable(List thelist, String input) {
//
//		boolean available =true;
//		String answer ="";
//		Book curbook = new Book();
//		String booktitle="";
//
//		if (thelist == theBookList) {                      //This is a candidate for a name change
//
//			int counter = 0;
//
//			//Identify which book is asked for 
//
//			for (int i = 0; i < thelist.size(); i++) {
//
//				curbook = (Book) thelist.get(i);         //This is a candidate for a name change
//
//				if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
//						|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
//						|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change
//
//					counter = i;
//
//					Currentindex = counter;
//					theRecentThing.clear(); 									//Clear it before adding (changing) the
//					classtype = theBookList;                                    //This is a candidate for a name change
//					theRecentThing.add(classtype.get(Currentindex));
//					booktitle=curbook.getTitle();
//										
//					if (input.contains(curbook.getTitle().toLowerCase())){input = input.replace(curbook.getTitle().toLowerCase(), "<b>"+curbook.getTitle().toLowerCase()+"</b>");}          
//					if (input.contains(curbook.getIsbn().toLowerCase())) {input = input.replace(curbook.getIsbn().toLowerCase(), "<b>"+curbook.getIsbn().toLowerCase()+"</b>");}     
//					if (input.contains(curbook.getAutor().toLowerCase())){input = input.replace(curbook.getAutor().toLowerCase(), "<b>"+curbook.getAutor().toLowerCase()+"</b>");}
//										
//					i = thelist.size() + 1; 									// force break
//				}
//			}
//		}
//
//		// maybe other way round or double 
//
//		if (thelist == theRecentThing && theRecentThing.get(0) != null) {
//
//			if (theRecentThing.get(0).getClass().getSimpleName()
//					.toLowerCase().equals("book")) {                  //This is a candidate for a name change
//
//				curbook = (Book) theRecentThing.get(0);               //This is a candidate for a name change		
//				booktitle=curbook.getTitle();
//			}
//		}
//
//		// check all lendings if they contain the books ISBN
//
//		for (int i = 0; i < theLendingList.size(); i++) {
//
//			Lending curlend = (Lending) theLendingList.get(i);         //This is a candidate for a name change
//
//			// If there is a lending with the books ISBN, the book is not available
//
//			if ( curbook.getIsbn().toLowerCase().equals(curlend.getIsbn().toLowerCase())) {           //This is a candidate for a name change
//
//				input = input.replace(curlend.getIsbn().toLowerCase(), "<b>"+curlend.getIsbn().toLowerCase()+"</b>");
//				
//				available=false;
//				i = thelist.size() + 1; 									// force break
//			}
//		}
//
//		if(available){
//			answer="can lend the book.";
//		}
//		else{ 
//			answer="cannot lend the book as someone else has lent it at the moment.";
//		}
//
//		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
//				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
//		URL2 = "http://en.wikipedia.org/wiki/"
//				+ booktitle;
//		System.out.println("URL = "+URL);
//		tooltipstring = readwebsite(URL);
//		String html = "<html>" + tooltipstring + "</html>";
//		Myface.setmytooltip(html);
//		Myface.setmyinfobox(URL2);
//
//		return(answer);
//
//	}
	
//	public String ProductAvailable(List thelist, String input) {
//
//		boolean available =true;
//		String answer ="";
//		Product curProduct = new Product();
//		String productName="";
//
//		if (thelist == theProductList) {                      //This is a candidate for a name change
//
//			int counter = 0;
//
//			//Identify which book is asked for 
//
//			for (int i = 0; i < thelist.size(); i++) {
//
//				curProduct = (Product) thelist.get(i);         //This is a candidate for a name change
//
//				if (input.contains(curProduct.getProductName().toLowerCase())            //This is a candidate for a name change
//						|| input.contains(curProduct.getSerialNumber().toLowerCase())      //This is a candidate for a name change
//						|| input.contains(curProduct.getProductType().toLowerCase())) {  //This is a candidate for a name change
//
//					counter = i;
//
//					Currentindex = counter;
//					theRecentThing.clear(); 									//Clear it before adding (changing) the
//					classtype = theProductList;                                    //This is a candidate for a name change
//					theRecentThing.add(classtype.get(Currentindex));
//					productName=curProduct.getProductName();
//										
//					if (input.contains(curProduct.getProductName().toLowerCase())){input = input.replace(curProduct.getProductName().toLowerCase(), "<b>"+curProduct.getProductName().toLowerCase()+"</b>");}          
//					if (input.contains(curProduct.getSerialNumber().toLowerCase())) {input = input.replace(curProduct.getSerialNumber().toLowerCase(), "<b>"+curProduct.getSerialNumber().toLowerCase()+"</b>");}     
//					if (input.contains(curProduct.getProductType().toLowerCase())){input = input.replace(curProduct.getProductType().toLowerCase(), "<b>"+curProduct.getProductType().toLowerCase()+"</b>");}
//										
//					i = thelist.size() + 1; 									// force break
//				}
//			}
//		}
//
//		// maybe other way round or double 
//
//		if (thelist == theRecentThing && theRecentThing.get(0) != null) {
//
//			if (theRecentThing.get(0).getClass().getSimpleName()
//					.toLowerCase().equals("book")) {                  //This is a candidate for a name change
//
//				curProduct = (Product) theRecentThing.get(0);               //This is a candidate for a name change		
//				productName=curProduct.getProductName();
//			}
//		}
//
//		// check all lendings if they contain the books ISBN
//
//		for (int i = 0; i < theLendingList.size(); i++) {
//
//			Lending curlend = (Lending) theLendingList.get(i);         //This is a candidate for a name change
//
//			// If there is a lending with the books ISBN, the book is not available
//
//			if (curProduct.getSerialNumber().toLowerCase().equals(curlend.getSerialNumber().toLowerCase())) {           //This is a candidate for a name change
//
//				input = input.replace(curlend.getSerialNumber().toLowerCase(), "<b>"+curlend.getSerialNumber().toLowerCase()+"</b>");
//				
//				available=false;
//				i = thelist.size() + 1; 									// force break
//			}
//		}
//
//		if(available){
//			answer="can lend the book.";
//		}
//		else{ 
//			answer="cannot lend the book as someone else has lent it at the moment.";
//		}
//
//		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
//				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
//		URL2 = "http://en.wikipedia.org/wiki/"
//				+ productName;
//		System.out.println("URL = "+URL);
//		tooltipstring = readwebsite(URL);
//		String html = "<html>" + tooltipstring + "</html>";
//		Myface.setmytooltip(html);
//		Myface.setmyinfobox(URL2);
//
//		return(answer);
//	}
	
	
	// Answer a question of the "How many ...." kind 
	
	public Integer Count(List thelist) { // List "thelist": List of Class Instances (e.g. theBookList)

		//URL = "http://en.wiktionary.org/wiki/"		

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return thelist.size();
	}

	// Answer a question of the "What kind of..." kind
	
	public String ListAll(List thelist) {

		String listemall = "<ul>";

//		if (thelist == theBookList) {                                  //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Book curbook = (Book) thelist.get(i);                  //This is a candidate for a name change
//				listemall = listemall + "<li>" + (curbook.getTitle() + "</li>");    //This is a candidate for a name change
//			}
//		}
		
		if (thelist == theStoreList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Store curStore = (Store) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curStore.getStoreName() + "</li>");    //This is a candidate for a name change
			}
		}
		
		if (thelist == theProductList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Product curProduct = (Product) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curProduct.getProductType() + "</li>");    //This is a candidate for a name change
			}
		}
		
		if (thelist == theServiceList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Service curService = (Service) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curService.getServiceName() + "</li>");    //This is a candidate for a name change
			}
		}
		
		if (thelist == theDiscountList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Discount curDiscount = (Discount) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + (curDiscount.getDiscountType() + "</li>");    //This is a candidate for a name change
			}
		}

//		if (thelist == theMemberList) {                                //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Member curmem = (Member) thelist.get(i);               //This is a candidate for a name change
//				listemall = listemall + "<li>"                         //This is a candidate for a name change
//						+ (curmem.getSurname() + " " + curmem.getLastname() + "</li>");  //This is a candidate for a name change
//			}
//		}
//
//		if (thelist == theCatalogList) {                               //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Catalog curcat = (Catalog) thelist.get(i);             //This is a candidate for a name change
//				listemall = listemall 
//						+ "<li>" + (curcat.getName() + "</li>");      //This is a candidate for a name change
//			}
//		}
//		
//		if (thelist == theLendingList) {                               //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Lending curlend = (Lending) thelist.get(i);             //This is a candidate for a name change
//				listemall = listemall + "<li>" 
//						+ (curlend.getSerialNumber() + "</li>");                //This is a candidate for a name change
//			}
//		}
		
		listemall += "</ul>";

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);
		
		return listemall;
	}

	// Answer a question of the "Do you have..." kind 
	
	public Vector<String> CheckFor(List thelist, String input) {

		Vector<String> yesorno = new Vector<String>();
		String answer="";
		if (classtype.isEmpty()){
			yesorno.add("Class not recognised. Please specify if you are searching for a book, catalog, member, or lending?");
		} else {
			yesorno.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (thelist == theBookList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Book curbook = (Book) thelist.get(i);                           

				if (input.contains(curbook.getTitle().toLowerCase())            
						|| input.contains(curbook.getIsbn().toLowerCase())      
						|| input.contains(curbook.getAutor().toLowerCase())) {  

//					counter = i;		
					
					answer+="<li><strong>Title:</strong>" + curbook.getTitle() + "</li>"+
							"<li><strong>Author:</strong>"+curbook.getAutor()+ "</li>"+
							"<li><strong>Isbn:</strong>"+curbook.getIsbn()+ "</li>"+
							"<li><strong>Location:</strong>"+curbook.getLocation()+ "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Book"+answer);
					
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1; // force break
				}
			}
		}
		

		if (thelist == theProductList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Product curProduct = (Product) thelist.get(i);                           

				if (input.contains(curProduct.getProductName().toLowerCase())            
						|| input.contains(curProduct.getSerialNumber().toLowerCase())      
						|| input.contains(curProduct.getProductType().toLowerCase())) {  

//					counter = i;		
					
					answer+="<li><strong>Title:</strong>" + curProduct.getProductName() + "</li>"+
							"<li><strong>Author:</strong>"+curProduct.getProductType()+ "</li>"+
							"<li><strong>Isbn:</strong>"+curProduct.getSerialNumber()+ "</li>"+
							"<li><strong>Location:</strong>"+curProduct.getLocation()+ "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Product"+answer);
					
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1; // force break
				}
			}
		}
		
		if (thelist == theServiceList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Service curService = (Service) thelist.get(i);                           

				if (input.contains(curService.getServiceName().toLowerCase())) {  

//					counter = i;		
					
					answer+="<li><strong>Service:</strong>" + curService.getServiceName() + "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Service"+answer);
					
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1; // force break
				}
			}
		}
		
		if (thelist == theDiscountList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Discount curDiscount = (Discount) thelist.get(i);                           

				if (input.contains(curDiscount.getDiscountType().toLowerCase())) {  

//					counter = i;		
					
					answer+="<li><strong>Discount Type:</strong>" + curDiscount.getDiscountType() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curDiscount.getDiscountPercentage() + "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Discount"+answer);
					
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1; // force break
				}
			}
		}
		
		if (thelist == theStoreList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Store curStore = (Store) thelist.get(i);                           

				if (input.contains(curStore.getStoreName().toLowerCase())
						|| input.contains(curStore.getCity().toLowerCase())) {  

//					counter = i;		
					
					answer+="<li><strong>Discount Type:</strong>" + curStore.getStoreName() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curStore.getCity() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curStore.getStreet() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curStore.getHouseNumber() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curStore.getPostcode() + "</li>"+
							"<li><strong>disc Percentage:</strong>" + curStore.getPhoneNumber() + "</li>"+
							
							/*If there more than one occurrences, they'll be separated by below line */
							
							"---------------------------------------------------------------";
							
					yesorno.set(0, "Yes we have such a Discount"+answer);
					
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1; // force break
				}
			}
		}
		

//		if (thelist == theMemberList) {                                      //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Member curmem = (Member) thelist.get(i);                      //This is a candidate for a name change
//				if (input.contains(curmem.getSurname().toLowerCase())         //This is a candidate for a name change
//						|| input.contains(curmem.getLastname().toLowerCase()) //This is a candidate for a name change
//						|| input.contains(curmem.getCity().toLowerCase())) {  //This is a candidate for a name change
//
//					counter = i;
//					yesorno.set(0, "Yes we have such a Member");               //This is a candidate for a name change
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1;
//				}
//			}
//		}
//
//		if (thelist == theCatalogList) {                                    //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Catalog curcat = (Catalog) thelist.get(i);                  //This is a candidate for a name change
//				if (input.contains(curcat.getName().toLowerCase())          //This is a candidate for a name change
//						|| input.contains(curcat.getUrl().toLowerCase())) { //This is a candidate for a name change
//
//					counter = i;
//					yesorno.set(0, "Yes we have such a Catalog");           //This is a candidate for a name change
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1;
//				}
//			}
//		}
//		
//		if (thelist == theLendingList) {                                     //This is a candidate for a name change
//			for (int i = 0; i < thelist.size(); i++) {
//				Lending curlend = (Lending) thelist.get(i);                  //This is a candidate for a name change
//				if (input.contains(curlend.getIsbn().toLowerCase())          //This is a candidate for a name change
//					|| input.contains(curlend.getMemberid().toLowerCase())){ //This is a candidate for a name change
//
//					counter = i;
//					yesorno.set(0, "Yes we have such a Lending");            //This is a candidate for a name change
//					yesorno.add(counter.toString());
//					i = thelist.size() + 1;
//				}
//			}
//		}

		if (classtype.isEmpty()) {
			System.out.println("Not class type given.");
		} else {
			URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			System.out.println("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL2);
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
					.toLowerCase().equals("book")) {                  //This is a candidate for a name change

				Book curbook = (Book) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curbook.getLocation() + " ");             //This is a candidate for a name change

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("store")) {                  //This is a candidate for a name change

				Store curStore = (Store) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curStore.getStoreName() + " "+ curStore.getCity()  + curStore.getHouseNumber() );             //This is a candidate for a name change

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("product")) {                  //This is a candidate for a name change

				Product curProduct = (Product) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curProduct.getLocation() + " ");             //This is a candidate for a name change

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("service")) {                  //This is a candidate for a name change

				Service curService = (Service) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curService.getServiceName() + " ");             //This is a candidate for a name change

			}
			
			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("discount")) {                  //This is a candidate for a name change

				Discount curDiscount = (Discount) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curDiscount.getDiscountType() + " ");             //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("member")) {               //This is a candidate for a name change

				Member curmem = (Member) theRecentThing.get(0);      //This is a candidate for a name change
				location = (curmem.getCity() + " " + curmem.getStreet() + " " + curmem  //This is a candidate for a name change
						.getHousenumber());                                    //This is a candidate for a name change
			}

			if (theRecentThing.get(0).getClass().getSimpleName()  
					.toLowerCase().equals("catalog")) {                 //This is a candidate for a name change

				Catalog curcat = (Catalog) theRecentThing.get(0);       //This is a candidate for a name change
				location = (curcat.getLocation() + " ");                //This is a candidate for a name change
			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("library")) {                  //This is a candidate for a name change

				location = (thelibrary.getCity() + " " + thelibrary.getStreet() + thelibrary   //This is a candidate for a name change
						.getHousenumber());                                           //This is a candidate for a name change
			}
		}

		// if a direct noun was used (book, member, etc)

		else {
			
			if (thelist == theStoreList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Store curStore = (Store) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curStore.getStoreName().toLowerCase())      
							|| input.contains(curStore.getCity().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curStore.getStoreName() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theServiceList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

			if (thelist == theBookList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Book curbook = (Book) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curbook.getLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theBookList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theProductList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Product curProduct = (Product) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curProduct.getProductName().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curProduct.getSerialNumber().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curProduct.getProductType().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curProduct.getLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theProductList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}
			
			if (thelist == theServiceList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Service curService = (Service) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curService.getServiceName().toLowerCase())      
							|| input.contains(curService.getPriceRange().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curService.getServiceName() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theServiceList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

//			if (thelist == theMemberList) {                                         //This is a candidate for a name change
//
//				int counter = 0;
//
//				for (int i = 0; i < thelist.size(); i++) {
//
//					Member curmember = (Member) thelist.get(i);         				  //This is a candidate for a name change
//
//					if (input.contains(curmember.getSurname().toLowerCase())              //This is a candidate for a name change
//							|| input.contains(curmember.getLastname().toLowerCase())      //This is a candidate for a name change
//							|| input.contains(curmember.getMemberid().toLowerCase())) {   //This is a candidate for a name change
//
//						counter = i;
//						location = (curmember.getCity() + " ");
//						Currentindex = counter;
//						theRecentThing.clear(); 										// Clear it before adding (changing) the
//						classtype = theMemberList;            	 						//This is a candidate for a name change
//						theRecentThing.add(classtype.get(Currentindex));
//						i = thelist.size() + 1; 				             	        // force break
//					}
//				}
//			}

//			if (thelist == theCatalogList) {                                       	 //This is a candidate for a name change
//
//				int counter = 0;
//
//				for (int i = 0; i < thelist.size(); i++) {
//
//					Catalog curcatalog = (Catalog) thelist.get(i);                    //This is a candidate for a name change
//
//					if (input.contains(curcatalog.getName().toLowerCase())            //This is a candidate for a name change						     
//							|| input.contains(curcatalog.getUrl().toLowerCase())) {   //This is a candidate for a name change
//
//						counter = i;
//						location = (curcatalog.getLocation() + " ");
//						Currentindex = counter;
//						theRecentThing.clear();                                      // Clear it before adding (changing) the	
//						classtype = theCatalogList;                                  //This is a candidate for a name change
//						theRecentThing.add(classtype.get(Currentindex));
//						i = thelist.size() + 1;                                      // force break
//					}
//				}
//			}

			if (thelist == theLibraryList) {                                                  //This is a candidate for a name change

				location = (thelibrary.getCity() + " " + thelibrary.getStreet() + thelibrary  //This is a candidate for a name change
						.getHousenumber());                                                   //This is a candidate for a name change
			}
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return location;
	}

	/* Searches for specific thing with the class and displays information of it's availability. */
	
	public String searchSpecificBook(String input){
		String answer = "";		

		for (int i = 0; i < theBookList.size(); i++) {   // check each book in the List, //This is a candidate for a name change

			Book curbook = (Book) theBookList.get(i);    // cast list element to Book Class //This is a candidate for a name change												
			System.out.println("Testing Book" + curbook.getAutor());

			if(input.contains(curbook.getAutor().toLowerCase())) {     // check for the author //This is a candidate for a name change

				answer = "A book written by " + curbook.getAutor() + "\n"  //This is a candidate for a name change
						+ " is for example the classic " + curbook.getTitle()      //This is a candidate for a name change
						+ ".";
			}
		}	
		return answer;
	}
	
	/* Searches for specific thing with the class and displays information of it's availability. */
	
	public String searchSpecificProduct(String input){
		String answer = "";		

		for (int i = 0; i < theProductList.size(); i++) {   // check each book in the List, //This is a candidate for a name change

			Product curProduct = (Product) theProductList.get(i);    // cast list element to Book Class //This is a candidate for a name change												
			System.out.println("Testing Product" + curProduct.getProductType());

			if(input.contains(curProduct.getProductType().toLowerCase())) {     // check for the author //This is a candidate for a name change

				answer = "A book written by " + curProduct.getProductType() + "\n"  //This is a candidate for a name change
						+ " is for example the classic " + curProduct.getProductName()      //This is a candidate for a name change
						+ ".";
			}
		}	
		return answer;
	}
	
	/* Searches for specific thing with the class and displays information of it's availability. */
	
	public String searchSpecificService(String input){
		String answer = "";		

		for (int i = 0; i < theServiceList.size(); i++) {   // check each book in the List, //This is a candidate for a name change

			Service curService = (Service) theServiceList.get(i);    // cast list element to Book Class //This is a candidate for a name change												
			System.out.println("Testing Services" + curService.getServiceName());

			if(input.contains(curService.getServiceName().toLowerCase())) {     // check for the author //This is a candidate for a name change

				answer = "A book written by " + curService.getServiceName() + "\n"  //This is a candidate for a name change
						+ " is for example the classic " + curService.getPriceRange()      //This is a candidate for a name change
						+ ".";
			}
		}	
		return answer;
	}
	
	
//	public String testit() {   // test the loaded knowledge by querying for books written by dostoyjewski
//
//		String answer = "";
//
//		System.out.println("Book List = " + theBookList.size());  //This is a candidate for a name change
//
//		for (int i = 0; i < theBookList.size(); i++) {   // check each book in the List, //This is a candidate for a name change
//
//			Book curbook = (Book) theBookList.get(i);    // cast list element to Book Class //This is a candidate for a name change												
//			System.out.println("Testing Book" + curbook.getAutor());
//
//			if (curbook.getAutor().equalsIgnoreCase("dostoyjewski")) {     // check for the author //This is a candidate for a name change
//
//				answer = "A book written by " + curbook.getAutor() + "\n"  //This is a candidate for a name change
//						+ " is for example the classic " + curbook.getTitle()      //This is a candidate for a name change
//						+ ".";
//			}
//		}
//		return answer;
//	}

	public String readwebsite(String url) {

		String webtext = "";
		try {
			BufferedReader readit = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			String lineread = readit.readLine();

			System.out.println("Reader okay");

			while (lineread != null) {
				webtext = webtext + lineread;
				lineread = readit.readLine();				
			}

			// Hard coded cut out from "wordnet website source text": 
			//Check if website still has this structure   vvvv ...definitions...  vvvv 		
			
			webtext = webtext.substring(webtext.indexOf("<ul>"),webtext.indexOf("</ul>"));                                 //               ^^^^^^^^^^^^^^^^^              

			webtext = "<table width=\"700\"><tr><td>" + webtext
					+ "</ul></td></tr></table>";

		} catch (Exception e) {
			webtext = "Not yet";
			System.out.println("Error connecting to wordnet");
		}
		return webtext;
	}
}
