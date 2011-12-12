/**
 * EasySOA Registry
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

package org.easysoa.sca.frascati;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.frascati.ParsingProcessingContext;
import org.eclipse.stp.sca.Composite;
import org.junit.runner.RunWith;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.ow2.frascati.util.FrascatiException;

@RunWith(FeaturesRunner.class)
@Features(FraSCAtiFeature.class)
public class ApiTestHelperBase {

    static final Log log = LogFactory.getLog(ApiTestHelperBase.class);
	
	/** The FraSCAti platform */
    protected static FraSCAtiServiceItf frascati;

    protected static ArrayList<Composite> componentList;    
    
	protected static void startMock() {
		log.info("Services Mock Starting");
		ParsingProcessingContext context  = null;
		try{
			context = new ParsingProcessingContext(frascati.newProcessingContext());
			frascati.processComposite("src/test/resources/RestApiMock.composite",context);
		} catch (NuxeoFraSCAtiException e){
			log.info(e.getMessage());
		}
		componentList.add(context.getRootComposite());
	}
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() {
		log.info("FraSCATI Starting");
		componentList =  new ArrayList<Composite>();
		frascati = Framework.getLocalService(FraSCAtiServiceItf.class);
	}
	
}