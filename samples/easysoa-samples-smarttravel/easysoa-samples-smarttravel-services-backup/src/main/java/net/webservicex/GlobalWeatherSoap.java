/**
 * EasySOA Samples - Smart Travel
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
 * Contact : easysoa-dev@groups.google.com
 */

package net.webservicex;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-08T11:01:25.140+02:00
 * Generated source version: 2.3.3
 * 
 */
 
@WebService(targetNamespace = "http://www.webserviceX.NET", name = "GlobalWeatherSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface GlobalWeatherSoap {

    @WebResult(name = "GetCitiesByCountryResult", targetNamespace = "http://www.webserviceX.NET")
    @RequestWrapper(localName = "GetCitiesByCountry", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetCitiesByCountry")
    @WebMethod(operationName = "GetCitiesByCountry", action = "http://www.webserviceX.NET/GetCitiesByCountry")
    @ResponseWrapper(localName = "GetCitiesByCountryResponse", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetCitiesByCountryResponse")
    public java.lang.String getCitiesByCountry(
        @WebParam(name = "CountryName", targetNamespace = "http://www.webserviceX.NET")
        java.lang.String countryName
    );

    @WebResult(name = "GetWeatherResult", targetNamespace = "http://www.webserviceX.NET")
    @RequestWrapper(localName = "GetWeather", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetWeather")
    @WebMethod(operationName = "GetWeather", action = "http://www.webserviceX.NET/GetWeather")
    @ResponseWrapper(localName = "GetWeatherResponse", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetWeatherResponse")
    public java.lang.String getWeather(
        @WebParam(name = "CityName", targetNamespace = "http://www.webserviceX.NET")
        java.lang.String cityName,
        @WebParam(name = "CountryName", targetNamespace = "http://www.webserviceX.NET")
        java.lang.String countryName
    );
}
