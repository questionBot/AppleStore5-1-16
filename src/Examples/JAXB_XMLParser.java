package Examples;


/*This class uses the JAXB AppleStore to automatically read a xml file and 
generate objects containing this data from pre-compiled classes*/


// Generate the classes automatically with: Opening a command console and type:
// Path to YOUR-PROJECTROOT-IN-WORKSPACE\xjc.bat yourschemaname.xsd -d src -p yourclasspackagename


import java.io.*;
import javax.xml.bind.*;

import Examples.AppleStore;

public class JAXB_XMLParser {

	/* Generate a context to work in with JAXB*/
	private JAXBContext jaxbContext = null;	
	/* Unmarshall = genrate objects from an xml file */
	private Unmarshaller unmarshaller = null;												
	
	  /* Main object containing all data*/ 
	private AppleStore myNewAppleStore = null;          

	public JAXB_XMLParser() {

		try {
			/*Package that contains classes*/
			jaxbContext = JAXBContext.newInstance("Examples");  																													
			unmarshaller = jaxbContext.createUnmarshaller();
		}
		catch (JAXBException e) {
		}
	}
	
	// Instance objects and return a list with this objects in it
	public AppleStore loadXML(InputStream fileinputstream) {

		try {
			Object xmltoobject = unmarshaller.unmarshal(fileinputstream);

			if (myNewAppleStore == null) {

				// Generate the myNewAppleStore object that conatins all info from the xml document
				myNewAppleStore = (AppleStore) (((JAXBElement) xmltoobject).getValue());
				 
				/* Return AppleStore Objekt*/
				return myNewAppleStore;
			}
		} // try

		catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
