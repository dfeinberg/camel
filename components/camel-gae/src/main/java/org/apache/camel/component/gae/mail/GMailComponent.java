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
package org.apache.camel.component.gae.mail;

import java.util.Map;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

import org.apache.camel.Endpoint;
import org.apache.camel.component.gae.bind.OutboundBinding;
import org.apache.camel.impl.DefaultComponent;

/**
 * The <a href="http://camel.apache.org/gmail.html">Google App Engine Mail
 * Component</a> supports outbound mail communication. It makes use of the mail
 * service provided by Google App Engine.
 */
public class GMailComponent extends DefaultComponent {

    @Override
    @SuppressWarnings("unchecked")
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        OutboundBinding binding = resolveAndRemoveReferenceParameter(
                parameters, "outboundBindingRef", OutboundBinding.class, new GMailBinding());
        MailService service = resolveAndRemoveReferenceParameter(
                parameters, "mailServiceRef", MailService.class, MailServiceFactory.getMailService());
        GMailEndpoint endpoint = new GMailEndpoint(uri, remaining);
        endpoint.setOutboundBinding(binding);
        endpoint.setMailService(service);
        return endpoint;
    }

}