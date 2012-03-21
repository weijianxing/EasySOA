/**
 * 
 */
package org.easysoa.records.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.records.ExchangeRecord;
import org.nuxeo.runtime.api.Framework;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.run.RunManager;

/**
 * @author jguillemotte
 *
 */
public class ExchangeRecordHandler implements ExchangeHandler {

    /* (non-Javadoc)
     * @see org.easysoa.records.handlers.ExchangeHandler#handleExchange(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) {

        ExchangeRecord record = new ExchangeRecord();
        InMessage inMessage = new InMessage((HttpServletRequest)request);
        //TODO : Complete the OutMessage with response content
        OutMessage outMessage = new OutMessage();
        
        record.setInMessage(inMessage);
        record.setOutMessage(outMessage);
        
        // Call runManager to register the exchange record
        try{
            // Get the service
            FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            RunManager runManager = (RunManager) frascati.getService("runManager", "runManagerService", RunManager.class);
            runManager.record(record);
        }
        catch(Exception ex) {
            // TODO add a better error gestion
            ex.printStackTrace();
        }
        
    }

}
