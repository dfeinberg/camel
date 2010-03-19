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
package org.apache.camel.impl;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * @version $Revision$
 */
public class ShutdownRouteGracefulWithTimerTest extends ContextTestSupport {

    private static String foo = "";

    public void testShutdownRouteGraceful() throws Exception {
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        // should be stopped before it fires the first one
        getMockEndpoint("mock:timer").expectedMessageCount(0);

        template.sendBody("seda:foo", "A");
        template.sendBody("seda:foo", "B");
        template.sendBody("seda:foo", "C");
        template.sendBody("seda:foo", "D");
        template.sendBody("seda:foo", "E");

        assertMockEndpointsSatisfied();

        // now stop the route before its complete
        foo = foo + "stop";
        context.shutdownRoute("seda");

        // it should wait as there was 1 inflight exchange and 4 pending messages left
        assertEquals("Should graceful shutdown", "stopABCDE", foo);

        assertEquals("Timer should still be running", true, context.getRouteStatus("timer").isStarted());
        assertEquals("Seda should be stopped", true, context.getRouteStatus("seda").isStopped());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer:foo?period=500&delay=2000").routeId("timer").to("mock:timer");

                from("seda:foo").routeId("seda").to("mock:foo").delay(1000).process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        foo = foo + exchange.getIn().getBody(String.class);
                    }
                });
            }
        };
    }
}