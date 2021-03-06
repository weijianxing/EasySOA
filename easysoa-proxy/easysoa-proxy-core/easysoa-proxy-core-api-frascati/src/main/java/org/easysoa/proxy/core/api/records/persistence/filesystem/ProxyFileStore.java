/**
 * EasySOA Proxy Core
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.records.persistence.filesystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.easysoa.message.Header;
import org.easysoa.message.Headers;
import org.easysoa.message.InMessage;
import org.easysoa.message.MessageContent;
import org.easysoa.message.OutMessage;
import org.easysoa.message.PostData;
import org.easysoa.message.QueryParam;
import org.easysoa.message.QueryString;
import org.easysoa.persistence.StoreItf;
import org.easysoa.persistence.StoreResource;
import org.easysoa.persistence.filesystem.FileStore;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.records.assertions.AssertionSuggestions;
import org.easysoa.proxy.core.api.reports.Report;
import org.easysoa.proxy.core.api.run.Run;
import org.easysoa.proxy.core.api.simulation.SimulationStore;
import org.easysoa.proxy.core.api.template.AbstractTemplateField;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;
import org.easysoa.proxy.core.api.template.VelocityTemplate;
import org.easysoa.records.Exchange;
import org.easysoa.records.Exchange.ExchangeType;
import org.easysoa.records.ExchangeRecord;

/**
 * Take a ExchangeRecordStoreArray and store the Exchange records as files in
 * the file system
 * 
 * @author jguillemotte
 */

public class ProxyFileStore {

    // Logger
    private static Logger logger = Logger.getLogger(ProxyFileStore.class.getName());
    
    // Exchange number counter data file
    public final static String EXCHANGE_NUMBER_FILE_NAME = "exchangeNumberCounter.data";
    // File extensions
    public final static String EXCHANGE_FILE_EXTENSION = ".json";
    public final static String TEMPLATE_FILE_EXTENSION = ".vm";
    public final static String SUGGESTIONS_FILE_EXTENSION = ".fld";
    public final static String ASSERTIONS_FILE_EXTENSION = ".asr";
    // File name prefix
    public final static String EXCHANGE_FILE_PREFIX = "excRec_";
    public final static String IN_MESSAGE_FILE_PREFIX = "inMess_";
    public final static String OUT_MESSAGE_FILE_PREFIX = "outMess_";
    public final static String REQ_TEMPLATE_FILE_PREFIX = "reqTemplateRecord_";
    public final static String RES_TEMPLATE_FILE_PREFIX = "resTemplateRecord_";
    public final static String SUGGESTION_FILE_PREFIX = "fieldSuggestions_";
    public final static String ASSERTIONS_FILE_PREFIX = "assertionSuggestions_";
    
    // Path for exchange records
    protected String path;
    
    // Path for templates (fld) and and assertions (asr)
    private String templatePath;
    
    // Path for reports
    private String reportPath;
    
    // Generic interface for persistence
    private StoreItf store;
    
    /**
     * Path is set with the value of 'path.record.store' property
     */
    public ProxyFileStore() {
        store = new FileStore();
        try {
            this.path = PropertyManager.getPropertyManager().getProperty("path.record.store", "easysoa/stores/");
            this.templatePath = PropertyManager.getPropertyManager().getProperty("path.template.store", "easysoa/webContent/templates/");
            this.reportPath = PropertyManager.getPropertyManager().getProperty("path.reports", "easysoa/reports");
            logger.debug("Using property 'path.record.store' for record store path = " + this.path);
            logger.debug("Using property 'path.template.store' for template store path = " + this.templatePath);
            logger.debug("Using property 'path.reports' for reports path = " + this.reportPath);
        }
        catch(Exception ex){
            this.path = "easysoa/stores/";
            this.templatePath = "easysoa/webContent/templates/";
            this.reportPath = "easysoa/reports";
            //logger.warn("An error occurs during the ProxyFileStore initialization", ex);
            logger.warn("An error occurs during the ProxyFileStore initialization, please check the property manager.");
            logger.warn("Using default property 'path.record.store' for record store path = " + this.path);
            logger.warn("Using default property 'path.template.store' for template store path = " + this.templatePath);
            logger.warn("Using default property 'path.reports' for reports path = " + this.reportPath);
        }
    }

    /**
     * Replace the original method. In this method 2 stores are created at the
     * same time : Record store for original exchange records and template store
     * for template files, assertions files and customized exchange records
     * @throws Exception 
     */
    public void createStore(String storeName) throws Exception {
        logger.debug("Creating record store folder : " + path + storeName);
        store.createStore(path + storeName);
        logger.debug("Creating template store folder : " + templatePath + storeName);
        store.createStore(templatePath + storeName);
    }

    /**
     * Save the run
     * @param run The run to save
     * @throws Exception If a problem occurs during the run save
     */
    public void save(Run run) throws Exception {
        // Create the run folder
        logger.debug("Path to store run = " + path + run.getName());
       
        // Create the store folder
        this.createStore(run.getName());
        
        // lock the store
        store.createRecordingLock(path + run.getName());        
        
        // Create in the same time the template folder
        for (ExchangeRecord record : run.getExchangeRecordList()) {
            this.save(record, path + run.getName() + "/");
        }
        
        // unlock the store
        store.removeRecordingLock(path + run.getName());
    }

    /**
     * Write an exchange record on the file system. Three files are generated : one for the exchange, one for the input message and one for the output message 
     * @param exchangeRecord The exchange record to save
     * @param path The path where the exchange record must be saved
     * @return The exchange record ID
     * @throws IOException If a problem occurs
     */
    protected String save(ExchangeRecord exchangeRecord, String recordPath) throws Exception{
        // StoreResource
        StoreResource resource;
        // Saving the three parts of the exchange record
        JSONObject inJSON = JSONObject.fromObject(exchangeRecord.getInMessage());
        resource = new StoreResource(IN_MESSAGE_FILE_PREFIX + exchangeRecord.getExchange().getExchangeID() + EXCHANGE_FILE_EXTENSION, recordPath, inJSON.toString());
        store.save(resource);
        JSONObject outJSON = JSONObject.fromObject(exchangeRecord.getOutMessage());        
        resource = new StoreResource(OUT_MESSAGE_FILE_PREFIX + exchangeRecord.getExchange().getExchangeID() + EXCHANGE_FILE_EXTENSION, recordPath, outJSON.toString());
        store.save(resource);
        JSONObject excJSON = JSONObject.fromObject(exchangeRecord.getExchange());
        resource = new StoreResource(EXCHANGE_FILE_PREFIX + exchangeRecord.getExchange().getExchangeID() + EXCHANGE_FILE_EXTENSION, recordPath, excJSON.toString());
        store.save(resource);
        return exchangeRecord.getExchange().getExchangeID();        
    }    
    
    /**
     * Save field suggestions in a fld file
     * 
     * @param templateFieldSuggestions
     * @param recordID
     * @throws IOException
     */
    public void saveFieldSuggestions(TemplateFieldSuggestions templateFieldSuggestions, String storeName, String recordID) throws Exception {
        // lock the store
        store.createRecordingLock(path + storeName);        
        String fieldSuggestFileName = SUGGESTION_FILE_PREFIX + recordID + SUGGESTIONS_FILE_EXTENSION;
        JSONObject templateJSON = JSONObject.fromObject(templateFieldSuggestions);
        StoreResource resource = new StoreResource(fieldSuggestFileName, this.templatePath + storeName, templateJSON.toString()); 
        store.save(resource);
        // unlock the recording lock
        store.removeRecordingLock(path + storeName);
    }

    /**
     * Save a customized ExchangeRecord in the template store
     * @param record
     * @param storeName
     * @throws Exception
     */
    public void saveCustomRecord(ExchangeRecord record, String storeName) throws Exception {
        this.save(record, templatePath + storeName + "/");
    }

    /**
     * Save a template record
     * @param templateRecord The template record to save
     * @throws IOException If a problem occurs
     */
    public Map<String, String> saveTemplate(VelocityTemplate templateRecord, String storeName) throws Exception {
        // lock the store
        store.createRecordingLock(path + storeName);        
        HashMap<String, String> templateFileMap = new HashMap<String, String>();
        StoreResource resource; 
        String reqTemplateFileName = REQ_TEMPLATE_FILE_PREFIX + templateRecord.getrecordID() + TEMPLATE_FILE_EXTENSION;
        String resTemplateFileName = RES_TEMPLATE_FILE_PREFIX + templateRecord.getrecordID() + TEMPLATE_FILE_EXTENSION;
        resource = new StoreResource(reqTemplateFileName, templatePath + "/" + storeName, templateRecord.getRequestTemplate());
        store.save(resource);
        templateFileMap.put("reqTemplate", reqTemplateFileName);
        resource = new StoreResource(resTemplateFileName, templatePath + "/" + storeName, templateRecord.getResponsetemplate());
        store.save(resource);
        templateFileMap.put("resTemplate", resTemplateFileName);
        // unlock the store
        store.removeRecordingLock(path + storeName);        
        return templateFileMap;
    }

    /**
     * 
     * @param storeName
     * @param recordID
     * @return
     * @throws Exception
     */
    public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String recordID) throws Exception {
        checkForLock(path + storeName);
        logger.debug("loading field suggestions for record ID : " + recordID);
        // Getting the resource
        StoreResource resource = store.load(SUGGESTION_FILE_PREFIX + recordID + SUGGESTIONS_FILE_EXTENSION, templatePath + storeName);
        // Transform the resource in TemplateFieldSuggestions
        @SuppressWarnings("rawtypes")
        HashMap<String, Class> classMap = new HashMap<String, Class>();
        classMap.put("templateFields", AbstractTemplateField.class);
        return (TemplateFieldSuggestions) convertJSONToObject(resource.getContent(), TemplateFieldSuggestions.class, classMap); 
    }
    
    /**
     * Load assertion suggestions from JSON ASR file
     * 
     * @param storeName Name of the store where the assertion suggestions are stored
     * @return The Assertion suggestions
     * @throws Exception If a problem occurs
     */
    public AssertionSuggestions getAssertionSuggestions(String storeName, String recordID) throws Exception {
        checkForLock(path + storeName);
        logger.debug("loading assertion suggestions :" + ASSERTIONS_FILE_PREFIX + recordID + ASSERTIONS_FILE_EXTENSION);
        StoreResource resource = store.load(ASSERTIONS_FILE_PREFIX + recordID + ASSERTIONS_FILE_EXTENSION, templatePath + storeName);
        @SuppressWarnings("rawtypes")
        HashMap<String, Class> classMap = new HashMap<String, Class>();
        return (AssertionSuggestions) convertJSONToObject(resource.getContent(), AssertionSuggestions.class, classMap);
    }    

    /**
     * Returns the name list of the recorded templates
     * 
     * @param storeName Name of the store where the template are stored
     * @return A <code>List</code> containing the name of the template files
     * @throws Exception 
     */
    public List<String> getTemplateList(String storeName) throws Exception {
        checkForLock(path + storeName);
        ArrayList<String> templateFileList = new ArrayList<String>();
        List<StoreResource> resourceList = store.getResourceList(templatePath + "/" + storeName);
        for(StoreResource resource : resourceList){
            if(resource.getResourceName().endsWith(TEMPLATE_FILE_EXTENSION)){
                templateFileList.add(resource.getResourceName());
            }  
        }
        return templateFileList;
    }

    /**
     * Save assertion suggestions
     * @param assertionSuggestions The assertion suggestion to save
     * @param recordID The record ID
     * @param storeName The store name where the asr file will be saved
     * @throws Exception If a problem occurs
     */
    public void saveAssertionSuggestions(AssertionSuggestions assertionSuggestions, String recordID, String storeName) throws Exception {
        // lock the store
        store.createRecordingLock(path + storeName);
        String assertionSuggestFileName = ASSERTIONS_FILE_PREFIX + recordID + ASSERTIONS_FILE_EXTENSION;
        JSONObject fieldSuggestJSON = JSONObject.fromObject(assertionSuggestions);
        StoreResource resource = new StoreResource(assertionSuggestFileName, templatePath + storeName, fieldSuggestJSON.toString());
        store.save(resource);
        // unlock the store
        store.removeRecordingLock(path + storeName);        
    }

    /**
     * Save an assertion report, using the report name to generate two files
     * corresponding to xml and txt versions
     * @param assertionReport The assertion report to save
     * @throws Exception If a problem occurs
     */
    public void saveReport(Report report) throws Exception {
        StoreResource resource = new StoreResource(report.getReportName() + ".xml", this.reportPath, report.generateXMLReport());
        store.save(resource);
        resource = new StoreResource(report.getReportName() + ".txt", this.reportPath, report.generateTXTReport());
        store.save(resource);
    }
    
    /**
     * 
     * @param exchangeRecordStoreName
     * @return
     * @throws Exception
     */
    public List<ExchangeRecord> getExchangeRecordlist(String exchangeRecordStoreName) throws Exception {
        // Check if there is a recording lock, if true is returned, waits for lock deletion
        checkForLock(path + exchangeRecordStoreName);
        
        // loads all files in path with extension & lists them
        logger.debug("exchangeRecordStoreName  = " + exchangeRecordStoreName);
        List<StoreResource> resourceList = store.getResourceList(path + "/" + exchangeRecordStoreName);
        ArrayList<ExchangeRecord> recordList = new ArrayList<ExchangeRecord>();
        for(StoreResource resource : resourceList){
            if(resource.getResourceName().startsWith(EXCHANGE_FILE_PREFIX) && resource.getResourceName().endsWith(EXCHANGE_FILE_EXTENSION)){
                String id = resource.getResourceName().substring(resource.getResourceName().lastIndexOf("_")+1, resource.getResourceName().lastIndexOf("."));
                recordList.add(this.loadExchangeRecord(exchangeRecordStoreName, id, false));                
            }
        }
        return recordList;
    }

    /**
     * Returns the list of the exchange record stores
     * @return A list of ExchangeRecord Stores
     */
    public List<String> getExchangeRecordStorelist() {
        return store.getStoreList(this.path);
    }

    /**
     * 
     * @param exchangeRecord
     * @return
     * @throws Exception
     */
    public String save(ExchangeRecord exchangeRecord) throws Exception {
        return save(exchangeRecord, path);
    }
    
    /**
     * 
     * @param exchangeStoreName
     * @param recordID
     * @return
     * @throws Exception
     */
    public ExchangeRecord loadExchangeRecord(String exchangeStoreName, String recordID, boolean customizedRecord) throws Exception {
        checkForLock(path + exchangeStoreName);
        String workPath = "";
        if(customizedRecord){
            workPath = this.templatePath;
        } else {
            workPath = this.path;
        }
        ExchangeRecord record = new ExchangeRecord();
        @SuppressWarnings("rawtypes")
        HashMap<String, Class> classMap = new HashMap<String, Class>();
        classMap.put("exchangeType", ExchangeType.class);
        classMap.put("queryString", QueryString.class);
        classMap.put("queryParams", QueryParam.class);
        classMap.put("postData", PostData.class);
        classMap.put("headers", Headers.class);
        classMap.put("headerList", Header.class);
        //classMap.put("customFields", CustomFields.class);
        //classMap.put("customFieldList", CustomField.class);
        classMap.put("messageContent", MessageContent.class);
        // Warning : With json-lib version 2.4, the conversion of a JSON structure containing an other JSON structure doesn't work
        // Use a previous version
        StoreResource resource = store.load(EXCHANGE_FILE_PREFIX + recordID + EXCHANGE_FILE_EXTENSION, workPath + exchangeStoreName);
        record.setExchange((Exchange) convertJSONToObject(resource.getContent(), Exchange.class, classMap));
        resource = store.load(IN_MESSAGE_FILE_PREFIX + recordID + EXCHANGE_FILE_EXTENSION, workPath + exchangeStoreName);
        record.setInMessage((InMessage) convertJSONToObject(resource.getContent(), InMessage.class, classMap));
        resource = store.load(OUT_MESSAGE_FILE_PREFIX + recordID + EXCHANGE_FILE_EXTENSION, workPath + exchangeStoreName);
        record.setOutMessage((OutMessage) convertJSONToObject(resource.getContent(), OutMessage.class, classMap));
        return record;
    }

    /**
     * Save a simulation store
     * @param simulationStore The simulation store to save
     * @throws Exception  If a problem occurs
     */
    public void saveSimulationStore(SimulationStore simulationStore) throws Exception {
        // lock the store
        store.createRecordingLock(path + simulationStore);
        Iterator<ExchangeRecord> recordIterator = simulationStore.getRecordList().keySet().iterator() ;
        String recordID;
        while(recordIterator.hasNext()){
            ExchangeRecord record = recordIterator.next();
            TemplateFieldSuggestions suggestions = simulationStore.getRecordList().get(record);
            // Saving the records contained in the store
            recordID = this.save(record, path + "/" + simulationStore.getStoreName());
            this.saveFieldSuggestions(suggestions, simulationStore.getStoreName(), recordID);
        }
        // unlock the store
        store.removeRecordingLock(path + simulationStore);        
    }
    
    /**
     * Load a SimulationStore
     * @param storeName The simulation store to load
     * @return A SimulationStore
     * @throws Exception If a problem occurs during the loading
     */
    public SimulationStore loadSimulationStore(String storeName) throws Exception {
        checkForLock(path + storeName);
        logger.debug("loading simulation store :" + storeName);
        List<StoreResource> resourceList = store.getResourceList(path + "/" + storeName);
        SimulationStore simulStore = new SimulationStore(storeName);
        for(StoreResource resource : resourceList){
            if(resource.getResourceName().endsWith(EXCHANGE_FILE_EXTENSION)){
                String recordID = resource.getResourceName().substring(resource.getResourceName().lastIndexOf("_")+1, resource.getResourceName().lastIndexOf("."));
                ExchangeRecord record = this.loadExchangeRecord(storeName, recordID, false);
                TemplateFieldSuggestions suggestions = this.getTemplateFieldSuggestions(storeName, recordID);
                simulStore.addRecordSuggestions(record, suggestions);                
            }
        }
        return simulStore;
    }
    
    /**
     * Convert a JSON string in the corresponding Java object
     * @param jsonString The JSON String to convert
     * @param objectClass The object type to return
     * @param classMap A map containing all the sub-objects included in the main object 
     * @return An object
     */
    private Object convertJSONToObject(String jsonString, @SuppressWarnings("rawtypes") Class objectClass, @SuppressWarnings("rawtypes") HashMap<String, Class> classMap) throws Exception {
        return JSONObject.toBean((JSONObject) JSONSerializer.toJSON(jsonString), objectClass, classMap);
    }
 
    /**
     * Check if there is a recording lock, if true is returned, waits for lock deletion
     * @param storeName The store name to check
     * @throws Exception if the lock is not release before timeout is elapsed
     */
    private void checkForLock(String storeName) throws Exception {
        if(store.checkRecordingLock(storeName)){ 
            store.waitForRecordingLock(storeName, 5000);
        }        
    }

    /**
     * Save the current exchange number
     */
    public void saveExchangeNumber(long exchangeNumberValue) throws Exception {
        StoreResource resource = new StoreResource(this.path + "/" + EXCHANGE_NUMBER_FILE_NAME);
        String content = "{\"exchangeNumberCounterValue\" : \"" + exchangeNumberValue + "\"}";
        resource.setContent(content);
        store.save(resource);
    }
    
    /**
     * Load the current exchange number file 
     * @return
     * @throws Exception 
     */
    public long getExchangeNumber() throws Exception {
        StoreResource resource = store.load(this.path + "/" + EXCHANGE_NUMBER_FILE_NAME, "");
        String jsonContent = resource.getContent();
        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonContent);
        long counterValue = jsonObject.getLong("exchangeNumberCounterValue");
        return counterValue;
    }
    
}
