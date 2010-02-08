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
package org.apache.camel.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.spi.ClassResolver;
import org.apache.camel.util.CastUtils;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

/* Using the bundle of CamelContext to load the class */
public class OsgiClassResolver extends DefaultClassResolver {
    private static final transient Log LOG = LogFactory.getLog(OsgiClassResolver.class);

    public BundleContext bundleContext;
    
    public OsgiClassResolver(BundleContext context) {
        this.bundleContext = context;
    }
    
    public Class<?> resolveClass(String name) {
        return loadClass(name, BundleDelegatingClassLoader.createBundleClassLoaderFor(bundleContext.getBundle()));
    }

    public <T> Class<T> resolveClass(String name, Class<T> type) {
        Class<T> answer = CastUtils.cast(loadClass(name, BundleDelegatingClassLoader.createBundleClassLoaderFor(bundleContext.getBundle())));
        return (Class<T>) answer;
    }

    public InputStream loadResourceAsStream(String uri) {
        ObjectHelper.notEmpty(uri, "uri");
        URL url = loadResourceAsURL(uri);
        InputStream answer = null;
        if (url != null) {
            try {
                answer = url.openStream();
            } catch (IOException ex) {
                LOG.error("Cannot load resource: " + uri, ex);
            }
        } 
        return answer;
    }

    public URL loadResourceAsURL(String uri) {
        ObjectHelper.notEmpty(uri, "uri");
        URL url = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            url = bundle.getEntry(uri);
            if (url != null) {
                break;
            }
        }
        return url;
    }

    protected Class<?> loadClass(String name, ClassLoader loader) {
        ObjectHelper.notEmpty(name, "name");
        Class<?> answer = null;
        // Try to use the camel context's bundle's classloader to load the class
        if (loader != null) {
            try {
                answer = loader.loadClass(name);
            } catch (ClassNotFoundException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Cannot load class: " + name + " using classloader: " + loader, e);
                }
            }
        }
        // Load the class with bundle, if there are more than one version of the class the first one will be load
        if (answer == null) {
            for (Bundle bundle : bundleContext.getBundles()) {
                try {
                    answer = bundle.loadClass(name);                    
                    if (answer != null) {
                        break;
                    }
                } catch (Exception e) {
                    // do nothing here
                }
            }
        }        
        return answer;
    }
    
}