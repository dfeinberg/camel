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
package org.apache.camel.converter.soap;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import com.example.customerservice.GetCustomersByName;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.soap.name.ElementNameStrategy;
import org.apache.camel.converter.soap.name.TypeNameStrategy;
import org.apache.camel.test.CamelTestSupport;

public class SoapUnMarshalTest extends CamelTestSupport {
    private static final String SERVICE_PACKAGE = GetCustomersByName.class
            .getPackage().getName();

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate producer;

    public void testUnMarshalSoap() throws IOException, InterruptedException {
        resultEndpoint.expectedMessageCount(1);
        InputStream in = this.getClass().getResourceAsStream("request.xml");
        producer.sendBody(in);
        resultEndpoint.assertIsSatisfied();
        Exchange exchange = resultEndpoint.getExchanges().get(0);
        Object body = exchange.getIn().getBody();
        Assert.assertEquals(GetCustomersByName.class, body.getClass());
        GetCustomersByName request = (GetCustomersByName) body;
        Assert.assertEquals("Smith", request.getName());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                ElementNameStrategy elNameStrat = new TypeNameStrategy();
                SoapJaxbDataFormat soapDataFormat = new SoapJaxbDataFormat(
                        SERVICE_PACKAGE, elNameStrat);
                from("direct:start").unmarshal(soapDataFormat)
                        .to("mock:result");
            }
        };
    }

}