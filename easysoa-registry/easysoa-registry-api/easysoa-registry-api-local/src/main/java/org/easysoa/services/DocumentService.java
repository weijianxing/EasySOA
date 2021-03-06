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

package org.easysoa.services;

import java.net.MalformedURLException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface DocumentService {

    /**
     * Creates an Appli Impl. in the Master workspace.
     * @param session
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException
     */
    DocumentModel createAppliImpl(CoreSession session, String url) throws ClientException, MalformedURLException;

    /**
     * Creates an Appli Impl. in the given workspace.
     * If no such workspace exists, it is created.
     * @param session
     * @param url
     * @param workspace
     * @return
     * @throws ClientException
     * @throws MalformedURLException
     */
    DocumentModel createAppliImpl(CoreSession session, String url, String workspace) throws ClientException, MalformedURLException;

    /**
     * 
     * @param session
     * @param parentPath Valid Service API or Appli Impl.
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    DocumentModel createServiceAPI(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException;

    /**
     * Returns null if the service API doesn't exist.
     * 
     * @param session
     * @param parentPath Valid Service API
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    DocumentModel createService(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException;

    /**
     * Returns null if the service API Impl doesn't exist.
     * 
     * @param session
     * @param parentPath service API Impl
     * @param archiPath
     * @return
     * @throws ClientException
     */
    DocumentModel createReference(CoreSession session, String parentPath, String title) throws ClientException;

    DocumentModel findWorkspace(CoreSession session, String name) throws ClientException;
    
    DocumentModel findEnvironment(CoreSession session, String name) throws ClientException;
    
    DocumentModel findAppliImpl(CoreSession session, String appliUrl) throws ClientException;

    DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException;

    DocumentModel findService(CoreSession session, String url) throws ClientException, MalformedURLException;
    
    /**
     * Find all child documents from the given model.
     * @param session
     * @param model
     * @param doctype A specific doctype to look for, or null for everything
     * @return All child services, or the document itself if the document is a service
     * @throws ClientException
     */
    public DocumentModelList findChildren(CoreSession session, DocumentModel model, String doctype) throws ClientException;

    DocumentModel findServiceReference(CoreSession session, String referenceArchiPath) throws ClientException;

    /**
     * Merges properties from a document to another,
     * i.e. copies properties from a source model to the destination.
     * The source document is deleted, and the destination saved.
     * @param from
     * @param to
     * @param overwrite If destination properties have to be overwritten
     * @return
     * @throws ClientException
     */
    boolean mergeDocument(CoreSession session, DocumentModel from, DocumentModel to, boolean overwrite) throws ClientException;

    String generateDocumentID(DocumentModel doc);

    DocumentModel getDefaultWorkspace(CoreSession session) throws ClientException;
        
    /**
     * Returns the default Appli Impl., creates it if necessary.
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModel getDefaultAppliImpl(CoreSession session) throws ClientException;

    /**
     * Returns the default Appli Impl. in the desired workspace, creates it if necessary.
     * @param session
     * @param workspace
     * @return Never returns null
     * @throws ClientException
     */
    DocumentModel getDefaultAppliImpl(CoreSession session, String workspace) throws ClientException;
    
    /**
     * Returns the workspace in which the current document is.
     * @param session
     * @param model
     * @return The workspace or null
     * @throws Exception
     */
    DocumentModel getWorkspace(CoreSession session, DocumentModel model) throws ClientException;
    
    DocumentRef getWorkspaceRoot(CoreSession session) throws ClientException;

}