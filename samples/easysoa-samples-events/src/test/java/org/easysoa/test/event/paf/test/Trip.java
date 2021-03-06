
/*
 * 
 */


package org.easysoa.test.event.paf.test;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.0
 * 2012-06-15T11:28:32.965+02:00
 * Generated source version: 2.4.0
 * 
 */

public class Trip extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://scenario1.j1.galaxy.inria.fr/", "Trip");
    public final static QName TripPort = new QName("http://scenario1.j1.galaxy.inria.fr/", "TripPort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:9000/GalaxyTrip?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(Trip.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:9000/GalaxyTrip?wsdl");
        }
        WSDL_LOCATION = url;
    }

    /**
     * Constructor
     * @param wsdlLocation
     */
    public Trip(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }
    
    /**
     * Constructor
     * @param wsdlLocation
     * @param serviceName
     */
    public Trip(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    /**
     * Default constructor
     */
    public Trip() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     * getter
     * @return
     */
    //  @WebEndpoint(name = "TripPort")
    public TripPortType getTripPort() {
        return super.getPort(TripPort, TripPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TripPortType
     */
   // @WebEndpoint(name = "TripPort")
    public TripPortType getTripPort(WebServiceFeature... features) {
        return super.getPort(TripPort, TripPortType.class, features);
    }
}
