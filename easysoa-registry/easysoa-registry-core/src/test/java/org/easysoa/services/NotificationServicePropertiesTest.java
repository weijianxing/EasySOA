package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests notification service
 * @author mkalam-alami, mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOACoreTestFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class NotificationServicePropertiesTest {

    static final Log log = LogFactory.getLog(NotificationServicePropertiesTest.class);
    
    @Inject NotificationService notifService;
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get notification service component", notifService);
  	  	assertNotNull("Cannot get document service component", docService);
    }
    
    /**
     * Test callcount incrementation
     * @throws Exception
     */
    @Test
    public void testCallcount() throws Exception {

    	String parentUrl ="http://www.myapp.com/api";
    	String serviceUrl ="http://www.myapp.com/api/service";
    	
    	// Create Service
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", "My Service");
    	properties.put(Service.PROP_URL, serviceUrl);
    	properties.put(Service.PROP_PARENTURL, parentUrl);
    	properties.put(Service.PROP_CALLCOUNT, "5");
    	notifService.notifyService(session, properties);
    	
    	DocumentModel doc = docService.findService(session, serviceUrl);
    	Assume.assumeNotNull(doc);
    	assertEquals(new Long(5), (Long) doc.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
    	
    	// Increment callcount
    	properties = new HashMap<String, String>();
    	properties.put(Service.PROP_URL, serviceUrl);
    	properties.put(Service.PROP_PARENTURL, parentUrl);
    	properties.put(Service.PROP_CALLCOUNT, "10");
    	notifService.notifyService(session, properties);
    	
    	doc = docService.findService(session, serviceUrl);
    	assertEquals(new Long(15), (Long) doc.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
    	
    }
    
    /**
     * Test the upload of services using the file URL property
     * (which attaches a WSDL to a Service for data extraction)
     * @throws Exception
     */
    @Test
    public void testFileUrl() throws Exception {

    	String wsdlUrl = "http://soatest.parasoft.com/calculator.wsdl",
    		serviceUrl = "http://ws1.parasoft.com/glue/calculator",
    		query = "SELECT * FROM Document WHERE serv:url = '"+serviceUrl+"'";
    	
    	// Create Service
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", "My Service");
    	properties.put(Service.PROP_URL, wsdlUrl);
    	properties.put(Service.PROP_CALLCOUNT, "5");
    	properties.put(Service.PROP_FILEURL, wsdlUrl);
    	notifService.notifyService(session, properties);
    	
    	// The URL should have been changed according to the WSDL contents
    	DocumentModel doc = docService.findService(session, serviceUrl);
    	assertNotNull("The WSDL hasn't been parsed", doc);
    	doc = docService.findService(session, wsdlUrl);
    	assertNotNull(doc);
    	DocumentModelList list = session.query(query);
    	assertEquals(1, list.size());
    	
    	// A second notification should update the same document
    	properties = new HashMap<String, String>();
    	properties.put("title", "My Updated Service");
    	properties.put(Service.PROP_URL, wsdlUrl);
    	properties.put(Service.PROP_FILEURL, wsdlUrl);
    	properties.put(Service.PROP_DESCRIPTION, "hello");
    	notifService.notifyService(session, properties);
    	
    	list = session.query(query);
    	assertEquals(1, list.size());
    	assertEquals("hello", list.get(0).getProperty(Service.SCHEMA_DUBLINCORE, Service.PROP_DESCRIPTION));
    	assertEquals("My Updated Service", list.get(0).getTitle());
    	
    }
    
}