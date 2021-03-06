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
package org.apache.camel.component.jetty;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Unit test to verify that we can have URI options for external system (endpoint is lenient)
 */
public class JettyHttpGetWithParamAsExchangeHeaderTest extends CamelTestSupport {

    private String serverUri = "http://localhost:9080/myservice";

    @Test
    public void testHttpGetWithParamsViaURI() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedHeaderReceived("one", "einz");
        mock.expectedHeaderReceived("two", "twei");
        mock.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");

        template.requestBody(serverUri + "?one=einz&two=twei", null, Object.class);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHttpGetWithParamsViaHeader() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedHeaderReceived("one", "uno");
        mock.expectedHeaderReceived("two", "dos");
        mock.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");

        template.requestBodyAndHeader(serverUri, null, Exchange.HTTP_QUERY, "one=uno&two=dos");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHttpPost() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        mock.expectedHeaderReceived(Exchange.HTTP_METHOD, "POST");

        template.requestBody(serverUri, "Hello World");

        assertMockEndpointsSatisfied();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("jetty:" + serverUri).to("mock:result");
            }
        };
    }
}
