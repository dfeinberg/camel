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
package org.apache.camel.component.mail;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @version $Revision$
 */
public class MailEndpointTest extends CamelTestSupport {

    @Test
    public void testMailEndpointCtr() throws Exception {
        MailEndpoint endpoint = new MailEndpoint();
        assertNull(endpoint.getConfiguration());
        assertNull(endpoint.getContentTypeResolver());
        assertNotNull(endpoint.getBinding());
        assertNotNull(endpoint.getHeaderFilterStrategy());

        MailConfiguration cfg = new MailConfiguration();
        cfg.setPort(21);
        cfg.setProtocol("smtp");
        cfg.setHost("myhost");
        cfg.setUsername("james");
        cfg.setPassword("secret");

        endpoint.setConfiguration(cfg);
        assertSame(cfg, endpoint.getConfiguration());
    }

    @Test
    public void testMailEndpointCtrEndpoint() throws Exception {
        MailEndpoint endpoint = new MailEndpoint("smtp://myhost");
        assertNull(endpoint.getContentTypeResolver());
        assertNotNull(endpoint.getConfiguration());
        assertNotNull(endpoint.getBinding());
        assertNotNull(endpoint.getHeaderFilterStrategy());

        MailConfiguration cfg = new MailConfiguration();
        cfg.setPort(21);
        cfg.setProtocol("smtp");
        cfg.setHost("myhost");
        cfg.setUsername("james");
        cfg.setPassword("secret");

        endpoint.setConfiguration(cfg);
        assertSame(cfg, endpoint.getConfiguration());
    }

    @Test
    public void testMailEndpointCtrEndpointConfig() throws Exception {
        MailConfiguration cfg = new MailConfiguration();
        cfg.setPort(21);
        cfg.setProtocol("smtp");
        cfg.setHost("myhost");
        cfg.setUsername("james");
        cfg.setPassword("secret");

        MailEndpoint endpoint = new MailEndpoint("smtp://myhost", cfg);
        assertSame(cfg, endpoint.getConfiguration());
        assertNull(endpoint.getContentTypeResolver());
        assertNotNull(endpoint.getBinding());
        assertNotNull(endpoint.getHeaderFilterStrategy());
        
        MyMailBinding myBnd = new MyMailBinding();
        endpoint.setBinding(myBnd);
        assertSame(myBnd, endpoint.getBinding());
    }

    private class MyMailBinding extends MailBinding {

    }

}
