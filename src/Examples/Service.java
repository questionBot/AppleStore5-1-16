package Examples;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Services complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Service">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nameOfService" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="priceRange" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="available" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service", propOrder = {
    "nameOfService",
    "priceRange",
    "available"
})
public class Service {

    @XmlElement(required = true)
    protected String nameOfService;
    @XmlElement(required = true)
    protected String priceRange;
    protected boolean available;

    /**
     * Gets the value of the nameOfService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return nameOfService;
    }

    /**
     * Sets the value of the nameOfService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.nameOfService = value;
    }

    /**
     * Gets the value of the available property.
     * 
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the value of the available property.
     * 
     */
    public void setAvailable(boolean value) {
        this.available = value;
    }

    /**
     * Gets the value of the priceRange property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriceRange() {
        return priceRange;
    }

    /**
     * Sets the value of the priceRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriceRange(String value) {
        this.priceRange = value;
    }

}
