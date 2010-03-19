/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.servlet;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;

public class ServletEndpoint extends HttpEndpoint {
    private String servletName;
    
    public ServletEndpoint() {
        super();
    }
    
    public ServletEndpoint(String endPointURI, ServletComponent component, URI httpUri, HttpParams params
                           , ClientConnectionManager httpConnectionManager, HttpClientConfigurer clientConfigurer) throws URISyntaxException {
        super(endPointURI, component, httpUri, params, httpConnectionManager, clientConfigurer);
    }
    
    public void setServletName(String name) {
        servletName = name;
    }
    
    public String getServletName() {
        return servletName;
    }
    
    @Override
    public Producer createProducer() throws Exception {
        return super.createProducer();
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new HttpConsumer(this, processor);
    }

}