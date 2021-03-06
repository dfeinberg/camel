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
package org.apache.camel.management;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.CamelContext;
import org.apache.camel.TestSupport;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContextNameStrategy;

/**
 * @version $Revision$
 */
public class TwoManagedCamelContextAutoAssignedNameClashTest extends TestSupport {

    private CamelContext camel1;
    private CamelContext camel2;

    protected CamelContext createCamelContext() throws Exception {
        DefaultCamelContext context = new DefaultCamelContext();
        DefaultManagementNamingStrategy naming = (DefaultManagementNamingStrategy) context.getManagementStrategy().getManagementNamingStrategy();
        naming.setHostName("localhost");
        naming.setDomainName("org.apache.camel");
        return context;
    }

    public void testTwoManagedCamelContextClash() throws Exception {
        camel1 = createCamelContext();
        camel1.start();
        assertTrue("Should be started", camel1.getStatus().isStarted());

        MBeanServer mbeanServer = camel1.getManagementStrategy().getManagementAgent().getMBeanServer();
        ObjectName on = ObjectName.getInstance("org.apache.camel:context=localhost/camel-1,type=context,name=\"camel-1\"");
        assertTrue("Should be registered", mbeanServer.isRegistered(on));

        // now cheat and reset the counter so we can test for a clash
        DefaultCamelContextNameStrategy.setCounter(0);

        camel2 = createCamelContext();
        camel2.start();
        ObjectName on2 = ObjectName.getInstance("org.apache.camel:context=localhost/camel-2,type=context,name=\"camel-2\"");
        assertTrue("Should be registered", mbeanServer.isRegistered(on2));

        assertTrue("Should still be registered after name clash", mbeanServer.isRegistered(on));
        assertTrue("Should still be registered after name clash", mbeanServer.isRegistered(on2));
    }

    @Override
    protected void tearDown() throws Exception {
        camel1.stop();
        camel2.stop();
        super.tearDown();
    }

}
