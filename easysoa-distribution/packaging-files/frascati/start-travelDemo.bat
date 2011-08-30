@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo Trip demo SOA
echo (Deployed on http://localhost:9000)
echo DEPENDENCY: Running services backup
echo %LINE%

set CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
"./bin/frascati-easysoa" run smart-travel-mock-services.composite -libpath ./sca-apps/trip-1.0-SNAPSHOT.jar ./sca-apps/summary-model-1.0-SNAPSHOT.jar