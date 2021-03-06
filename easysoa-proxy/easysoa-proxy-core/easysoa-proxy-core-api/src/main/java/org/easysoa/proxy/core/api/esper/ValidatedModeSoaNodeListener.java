/**
 * EasySOA Proxy
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

package org.easysoa.proxy.core.api.esper;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.monitoring.soa.Api;
import org.easysoa.proxy.core.api.monitoring.soa.Appli;
import org.easysoa.proxy.core.api.monitoring.soa.Node;
import org.easysoa.proxy.core.api.monitoring.soa.Service;
import org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Esper listener for Node objects
 * 
 * Node objects are used in the monitoring service to build the service tree.
 * 
 * What it does : only increases call count of Service.
 * 
 * @author jguillemotte
 *
 */
public class ValidatedModeSoaNodeListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(ValidatedModeSoaNodeListener.class.getName());
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if(newEvents != null){
			for (EventBean newEvent : newEvents) {
				update(newEvent);
			}
		}
	}

	/**
	 * Update 
	 * @param newData New event data
	 */
	public void update(EventBean newEvent) {
		logger.debug("[NodeListener] --- Event received: " + newEvent.getUnderlying());
		logger.debug("[NodeListener] --- " + newEvent.getUnderlying().getClass().getName());
		Node soaNode = (Node)(newEvent.getUnderlying());		
		if(soaNode instanceof Service){
			Service service = (Service) soaNode;
			service.setCallCount(service.getCallCount() + 1);
            try {
                new NuxeoRegistrationService().registerRestService(service);
            } catch (Exception e) {
                logger.error("Failed to register REST service", e);
            }
		} else if(soaNode instanceof Api){
			// Nothing to do, no counter to increase for API in Nuxeo model
		} else if(soaNode instanceof Appli){
			// Nothing to do, no counter to increase for Appli in Nuxeo model
		}
	}	
	
}
