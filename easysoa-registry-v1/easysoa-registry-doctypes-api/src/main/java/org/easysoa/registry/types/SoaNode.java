package org.easysoa.registry.types;

import org.easysoa.registry.SoaNodeId;


public interface SoaNode extends Document {

    public static final String DOCTYPE = "SoaNode";
    
    public static final String FACET = "SoaNode";

    public static final String XPATH_SOANAME = "soan:name";
    
    public static final String XPATH_ISPLACEHOLDER = "soan:isplaceholder";

    SoaNodeId getSoaNodeId() throws Exception;

    String getSoaName() throws Exception;

    boolean isPlaceholder() throws Exception;
    
    void setIsPlaceholder(boolean isPlaceholder) throws Exception;
    
}