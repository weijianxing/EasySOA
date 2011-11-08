// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * 
 * --------------
 * Object kinds
 * --------------
 * 
 * Service
 * 		Business or technical service need, independently from its implementation.
 * 		Impls. may or may not be made available as web services (see Scaffolder Client). 
 * Service Implementation
 * 		Implementation of a Service. Several implementations of the same need can exist,
 * 		for instance a light (scripted) implementation, a full implementation and a mock one.
 * Service Endpoint
 * 		Service Impl. launched and made available through the web.
 * 
 * Environment
 * 		Collection of Service Endpoints used together in a certain context (development, staging...)
 * 
 * Scaffolder Client
 * 		Specific type of Service Impl. generated by EasySOA Light as a client to call
 * 		existing Service Endpoints.
 * Scaffolder Client UI
 * 		Part of a scaffolder client that displays a form for users to call a service endpoint.
 * 		Should be customizable (by editing the template for example).
 * 
 * Other possible objects:
 * 	- Development Environment (specific type of environment?)
 * 
 * --------------
 * Ressources hierarchy
 * --------------
 * /envDev/scaffolderClient/references/calledService
*/

/*
Scenarios TODO

OK scaffolder calling a service selected (in accessible environments)
OK scaffolder calling a mock
OK scaffolder, then (from called service in this environment) create mock, by replace (or LATER fork)

OK scaffolder, then in between add WS monitoring proxy
OK then record exchanges (autostart, reset(), save(name), restore(name))
OK then create mock using a named recorded session of exchanges (when a given request appears, return the response)

then create template UI impl to replace scaffolder (LATER impl rather linked or forked from other env)
then add WS proxy + js impl between template UI and mock
then record exchanges and let the user tailor a recording session that is a test suite
then setup test suite to be called on each js impl changes

*/

Object.extend(global, require('prototype'));

EASYSOA_HOST = "http://localhost";
EASYSOA_LIGHT_SERVER_URL = EASYSOA_HOST + ":9011/";
EASYSOA_PAF_SERVICES_URL = EASYSOA_HOST + ":9010/";
EASYSOA_SCAFFOLDER_UI_URL = EASYSOA_HOST + ":8090/";

SERVICE_IMPL_TYPE_SCAFFOLDER_CLIENT = "scaffolderclient";
SERVICE_IMPL_TYPE_TEMPLATING_UI = "ui";
SERVICE_IMPL_TYPE_JAVASCRIPT = "javascript";
SERVICE_IMPL_TYPE_MOCK = "mock";
SERVICE_IMPL_TYPE_EXTERNAL = "external";

exports.ENVIRONMENT_TYPE_LIGHT = "light";
exports.ENVIRONMENT_TYPE_STAGING = "staging";

toUrlPath = function(name) {
    return name.sub(' ', '_');
};

// ===================== Service Impls. =====================

var AbstractServiceImpl = Class.create({
    initialize : function(name, type, isMock /*=false*/) {
        this.name = name;
        this.type = type;
        this.isMock = (isMock == undefined) ? false : isMock;
    },
    getName : function() {
        return this.name;
    },
    edit : function() {
        if (this.type != SERVICE_IMPL_TYPE_EXTERNAL) {
            console.log("Making user edit service impl. "+name);
        }
    }
});


var ScaffolderClientImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, targetEndpoint, isMock /*=false*/) {
        $super(name, SERVICE_IMPL_TYPE_SCAFFOLDER_CLIENT, isMock);
        this.targetEndpoint = targetEndpoint;
    }
});


var JavascriptImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, isMock /*=false*/, serviceImplToMock) {
        $super(name, SERVICE_IMPL_TYPE_MOCK, isMock);
        if (serviceImplToMock != undefined) {
            console.log("Building mock using "+serviceImplToMock.name);
        }
    },
    useRecords : function(records) {
        console.log("Making mock use some request/response records");
    }
});

var TemplatingUIImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, isMock /*=false*/) {
        $super(name, SERVICE_IMPL_TYPE_TEMPLATING_UI, isMock);
    }
});


//===================== Service Endpoints =====================

var ServiceEndpoint = Class.create({
    initialize : function(impl, url, env) {
        this.impl = impl;
        this.url = url;
        this.env = env;
        this.started = false;
        this.proxyFeatures = new $H();
    },
    getName : function() {
        return this.impl.name;
    },
    getImpl : function() {
        return this.impl;
    },
    checkStarted : function() {
        console.log(" * Checking: " + this.url);
        return this.started;
    },
    start : function() {
        console.log(" * Starting: " + this.impl.name);
        this.started = true;
        return this.started;
    },
    stop : function() {
        console.log(" * Stopping: " + this.impl.name);
        this.started = false;
    },
    use : function(proxyFeature) { // TODO Use class as key instead of names
        this.proxyFeatures.set(proxyFeature.name, proxyFeature);
    },
    getProxyFeature: function(name) {
        return this.proxyFeatures.get(name);
    }
});


var ScaffolderClientEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, env) {
        $super(impl, null, env);
        this.setTargetEndpoint(impl.targetEndpoint);
    },
    setTargetEndpoint : function(targetEndpoint) {
        this.impl.targetEndpointUrl = targetEndpoint.url;
        this.url = this.env.implServerUrl
                + toUrlPath(targetEndpoint.getName())
                + "_Scaffolder_Client?endpoint=" + targetEndpoint.url;
    },
    display : function() {
        console.log("Displaying UI: "+this.url);
    }
});


//===================== Proxy features =====================

var AbstractProxyFeature = Class.create({
    initialize : function(name) {
        this.name = name;
    },
    process : function(request, response) {
        // To implement
    }
});

var MonitoringProxyFeature = Class.create(AbstractProxyFeature, {
    initialize : function($super, name) {
        $super(name);
        this.activeRun = null;
    },
    process: function(request, response) {
        console.log("Monitoring triggered");  
    },
    save: function(name) {
        console.log("Saving current run as " + name);
        activeRun = null;
    },
    restore: function(name) {
        console.log("Restoring run " + name);
        activeRun = name;
    },
    reset: function() {
        if (activeRun != null) {
            console.log("Resetting run "+activeRun);
        }
    },
    getRecords: function(name) {
        return new Array({
            "request1" : "response1",
            "request2" : "response2"
        });
    }
});


// ===================== Environments =====================

var AbstractEnvironment = Class.create({
    initialize : function(envType, name, implServerUrl) {
        this.name = name;
        this.implServerUrl = implServerUrl;
        this.serviceImpls = new Array();
        this.externalServiceEndpoints = new Array();
    },
    addExternalServiceEndpoint : function(serviceEndpoint) {
        this.externalServiceEndpoints.push(serviceEndpoint);
    },
    addServiceImpl : function(serviceImpl) {
        var newServiceEndpoint;
        switch (serviceImpl.type) {
            case SERVICE_IMPL_TYPE_SCAFFOLDER_CLIENT:
                newServiceEndpoint = new ScaffolderClientEndpoint(serviceImpl, this);
                break;
            default:
                newServiceEndpoint = new ServiceEndpoint(serviceImpl,
                        this.implServerUrl + toUrlPath(serviceImpl.name), this);
        }

        newServiceEndpoint.env = this;
        this.serviceImpls.push(newServiceEndpoint);
        
        return newServiceEndpoint;
    },
    removeServiceImpl : function(serviceImplToRemove) {
        var i = 0;
        for (var serviceImpl in this.serviceImpls) {
            if (serviceImpl == serviceImplToRemove) {
                this.serviceImpls.splice(i, i+1);
                return;
            }
            i++;
        }
    },
    start : function() {
        var allIsStarted = true;
        this.externalServiceEndpoints.each(function(impl) {
            if (!impl.checkStarted()) {
                allIsStarted = false;
            }
        });
        this.serviceImpls.each(function(impl) {
            if (!impl.start()) {
                allIsStarted = false;
            }
        });
        return allIsStarted;
    },
    stop : function() {
        for (var serviceImpl in this.serviceImpls) {
            serviceImpl.stop();
        }
    }
});

var StagingEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name, url) {
        $super(exports.ENVIRONMENT_TYPE_STAGING, name, url);
    }
});

var LightEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name, user) {
        $super(exports.ENVIRONMENT_TYPE_LIGHT, user + "_" + name, EASYSOA_LIGHT_SERVER_URL);
    }
});


// ======================== UI =========================

exports.selectServiceEndpointInUI = function(envFilter) {
    var selectedEndpoint = new ServiceEndpoint(
            new AbstractServiceImpl("PureAirFlowers", SERVICE_IMPL_TYPE_EXTERNAL),
            EASYSOA_PAF_SERVICES_URL + "PureAirFlowers",
            new StagingEnvironment(envFilter[0], EASYSOA_PAF_SERVICES_URL));
    selectedEndpoint.started = true;
    return selectedEndpoint;
};

//======================== Exports =========================

exports.ScaffolderClientImpl        = ScaffolderClientImpl;
exports.JavascriptImpl              = JavascriptImpl;
exports.TemplatingUIImpl            = TemplatingUIImpl;

exports.MonitoringProxyFeature      = MonitoringProxyFeature;

exports.LightEnvironment            = LightEnvironment;
exports.StagingEnvironment          = StagingEnvironment;