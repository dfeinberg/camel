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
package org.apache.camel.component.file;

import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.PollingConsumerPollStrategy;
import org.apache.camel.util.ObjectHelper;

/**
 * Unit test for poll strategy
 */
public class FileConsumerPollStrategyRollbackThrowExceptionTest extends ContextTestSupport {

    private static String event = "";

    private String fileUrl = "file://target/pollstrategy/?pollStrategy=#myPoll";

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("myPoll", new MyPollStrategy());
        return jndi;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteDirectory("target/pollstrategy");
        template.sendBodyAndHeader("file:target/pollstrategy/", "Hello World", Exchange.FILE_NAME, "hello.txt");
    }

    public void testRollbackThrowException() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);

        // let it run for a little while since we rethrow the exception the consumer
        // will stop scheduling and not poll anymore
        Thread.sleep(2000);

        assertMockEndpointsSatisfied();

        assertEquals("rollback", event);
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from(fileUrl).convertBodyTo(String.class).to("mock:result");
            }
        };
    }

    private class MyPollStrategy implements PollingConsumerPollStrategy {

        public boolean begin(Consumer consumer, Endpoint endpoint) {
            // start consumer as we simulate the fail in begin
            // and thus before camel lazy start it itself
            try {
                consumer.start();
            } catch (Exception e) {
                ObjectHelper.wrapRuntimeCamelException(e);
            }

            // simulate an error on first poll
            throw new IllegalArgumentException("Damn I cannot do this");
        }

        public void commit(Consumer consumer, Endpoint endpoint) {
            event += "commit";
        }

        public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception cause) throws Exception {
            event += "rollback";
            throw cause;
        }
    }

}