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

package org.easysoa.test.helpers;

import org.json.JSONException;


/**
 * ServiceTestHelper for FullMocked mock setup
 * 
 * @author mdutoo
 *
 */
public class FullMockedServiceTestHelper extends ServiceTestHelperBase {

	@Override
	protected void waitForServices(int i) {
		// no need to sleep when mocked
	}

	@Override
	protected String cleanNuxeoRegistry(String urlPattern) throws JSONException {
		// no need to clean when mocked
		return null;
	}

}
