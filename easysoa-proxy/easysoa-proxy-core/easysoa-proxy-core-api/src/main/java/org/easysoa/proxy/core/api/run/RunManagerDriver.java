/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.run;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * An interface to start/stop runs Run is a set of recorded messages
 *
 * @author jguillemotte
 *
 */
//@Remotable
public interface RunManagerDriver {

    /**
     * Returns a help text with the available commands
     *
     * @param ui Context UriInfo
     * @return The <code>String</code> help text
     */
    // TODO in case of error, return the user informations
    @GET
    @Path("/")
    public String returnUseInformations(@Context UriInfo ui);

    /**
     * Starts a new run
     *
     * @param runName The new run name
     * @return a <code>String</code> to indicate if the command succeed
     */
    @POST
    @Path("/run/start/{runName}")
    public String startNewRun(@PathParam("runName") String runName);

    /**
     * Delete the current run
     *
     * @return a <code>String</code> to indicate if the command succeed
     */
    @POST
    @Path("/run/delete")
    public String delete();

    /**
     * Stop the current run
     *
     * @return a <code>String</code> to indicate if the command succeed
     */
    @POST
    @Path("/run/stop")
    public String stopCurrentRun();

    /**
     * Set the monitoring mode
     *
     * @param mode The monitoring mode
     * @return a <code>String</code> to indicate if the command succeed
     */
    @POST
    @Path("/setMonitoringMode/{mode}")
    public String setMonitoringMode(@PathParam("mode") String mode);

    /**
     * Save the current run
     *
     * @throws Exception
     */
    @POST
    @Path("/run/save")
    public String save() throws Exception;
}