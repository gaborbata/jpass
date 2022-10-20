package jpass.xml.bind;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * <p>
 * Java class for entry complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="entry"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="title"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;pattern value=".*\S.*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *      &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
public class Entry {

    protected String title;
    protected String url;
    protected String user;
    protected String password;
    protected String notes;
    protected String lastModification;
    protected String creationDate;

    public Entry() {
        String now = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_DATE_TIME);
        this.creationDate = now;
        this.lastModification = now;
    }

    /**
     * Gets the value of the title property.
     *
     * @return possible object is {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is {@link String}
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the url property.
     *
     * @return possible object is {@link String}
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value allowed object is {@link String}
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the user property.
     *
     * @return possible object is {@link String}
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     *
     * @param value allowed object is {@link String}
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the password property.
     *
     * @return possible object is {@link String}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value allowed object is {@link String}
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the notes property.
     *
     * @return possible object is {@link String}
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     *
     * @param value allowed object is {@link String}
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the creation date property.
     *
     * @return the creation date
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creation date property.
     *
     * @param date the creation date
     */
    public void setCreationDate(String date) {
        this.creationDate = date;
    }

    /**
     * Gets the value of the last modification property.
     *
     * @return the last modification date
     */
    public String getLastModification() {
        return lastModification;
    }

}
