package org.easysoa.services;

import java.net.MalformedURLException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface DocumentService {

    public abstract DocumentModel createAppliImpl(CoreSession session, String url) throws ClientException, MalformedURLException;

    /**
     * 
     * @param session
     * @param parentPath If null or invalid, default application is used
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public abstract DocumentModel createServiceAPI(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException;

    /**
     * Returns null if the service API doesn't exist.
     * 
     * @param session
     * @param parentPath service API
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public abstract DocumentModel createService(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException;

    /**
     * Returns null if the service API Impl doesn't exist.
     * 
     * @param session
     * @param parentPath service API Impl
     * @param archiPath
     * @return
     * @throws ClientException
     */
    public abstract DocumentModel createReference(CoreSession session, String parentPath, String title) throws ClientException;

    public abstract DocumentModel findAppliImpl(CoreSession session, String appliUrl) throws ClientException;

    public abstract DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException;

    public abstract DocumentModel findService(CoreSession session, String url) throws ClientException, MalformedURLException;

    public abstract DocumentModel findServiceReference(CoreSession session, String referenceArchiPath) throws ClientException;

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
    public abstract boolean mergeDocument(CoreSession session, DocumentModel from, DocumentModel to, boolean overwrite) throws ClientException;

    public abstract String generateDocumentID(DocumentModel doc);

    /**
     * Returns the default Appli Impl., creates it if necessary.
     * @param session
     * @return
     * @throws ClientException
     */
    public abstract DocumentModel getDefaultAppliImpl(CoreSession session) throws ClientException;

    public abstract DocumentModel getWorkspaceRoot(CoreSession session) throws ClientException;

}