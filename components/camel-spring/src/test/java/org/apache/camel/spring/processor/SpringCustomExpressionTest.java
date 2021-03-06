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
package org.apache.camel.spring.processor;

import org.apache.camel.Exchange;
import org.apache.camel.impl.ExpressionAdapter;
import org.apache.camel.spring.SpringTestSupport;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version $Revision$
 */
public class SpringCustomExpressionTest extends SpringTestSupport {

    public void testTransformMyExpression() throws InterruptedException {
        getMockEndpoint("mock:result").expectedBodiesReceived("Yes Camel rocks", "Hello World");

        template.sendBody("direct:start", "Camel");
        template.sendBody("direct:start", "World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/apache/camel/spring/processor/SpringCustomExpressionTest.xml");
    }

    public static class MyExpression extends ExpressionAdapter {

        @Override
        public Object evaluate(Exchange exchange) {
            String body = exchange.getIn().getBody(String.class);
            if (body.contains("Camel")) {
                return "Yes Camel rocks";
            } else {
                return "Hello " + body;
            }
        }
    }

}