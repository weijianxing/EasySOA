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
package com.openwide.easysoa.exchangehandler;

import java.util.List;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

public class HandlerManagerImpl implements HandlerManager {

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger(HandlerManagerImpl.class.getName());        
    
    @Reference 
    private List<MessageHandler> handlers;
    
    @Override
    public void handle(InMessage inMessage, OutMessage outMessage) {
        logger.debug("Message received, calling registered handlers");
        for(MessageHandler handler : this.handlers){
            try {
                handler.handleMessage(inMessage, outMessage);
            }
            catch(Exception ex){
                logger.warn("An error occurs during the execution of the handler", ex);
            }
        }    
    }
    
    
}
