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

package org.easysoa.message;

import javax.xml.bind.annotation.XmlRootElement;
import org.easysoa.servlet.http.CopyHttpServletResponse;

/**
 * Outgoing message
 *
 * TODO LATER extract "servlet" code in (servlet)OutMessageBuilder
 * Make it Lazy with several levels (one for headers ..., one for JSONContent or XML content)
 *
 * @author jguillemotte
 *
 */
@XmlRootElement
public class OutMessage implements Message {

	/**
	 * Response fields
	 */
	private int status = 0;
	private String statusText;
	private String protocol;
	private String protocolVersion;
	// private Cookies cookies;
	private Headers headers;
	/** null means HTTP without content ex. GET TODO OR messageContent.rawContent */
	private MessageContent messageContent;
	private String redirectURL;
	// private Long headersSize = new Long(-1);
	// private Long bodySize = new Long(-1);
	private String comment;
	private long responseTimeStamp = 0;

	public OutMessage(){
		this.statusText = "";
		this.protocol = "";
		this.protocolVersion = "";
		this.headers = new Headers();
		this.messageContent = new MessageContent();
		this.redirectURL = "";
		this.comment = "";
	}

	/**
	 * Out message constructor
	 */
	public OutMessage(int status, String statusText) {
		this();
		this.status = status;
		this.statusText = statusText;
	}

	/**
	 *
	 * @param response
	 */
	public OutMessage(CopyHttpServletResponse response) {
        this();
        // Setting status
        this.setStatus(response.getStatus());
        // Setting Message content
        this.headers = new Headers();
        // Set the headers
        // With servlet 2.5, the following method getHeaderNames is not available
        // See http://stackoverflow.com/questions/12728017/how-to-get-header-from-response-in-servlet-2-3-or-2-5
        for(String headerName : response.getHeaderNames()){
            this.headers.addHeader(new Header(headerName, response.getHeader(headerName)));
        }
        // Set the message content
        MessageContent messageContent = new MessageContent();
        messageContent.setRawContent(response.getMessageContent());
        messageContent.setEncoding(response.getCharacterEncoding());
        messageContent.setMimeType(response.getContentType());
        messageContent.setSize(response.getMessageContent().length());
        this.setMessageContent(messageContent);
    }

    public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public MessageContent getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatus(String status) throws NumberFormatException {
		this.status = Integer.parseInt(status);
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public long getResponseTimeStamp() {
		return responseTimeStamp;
	}

	public void setResponseTimeStamp(long responseTimeStamp) {
		this.responseTimeStamp = responseTimeStamp;
	}

}
