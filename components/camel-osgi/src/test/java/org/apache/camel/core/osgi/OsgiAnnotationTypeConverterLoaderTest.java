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
package org.apache.camel.core.osgi;

import org.apache.camel.core.osgi.OsgiAnnotationTypeConverterLoader;
import org.apache.camel.osgi.test.MockTypeConverterRegistry;
import org.junit.Test;

public class OsgiAnnotationTypeConverterLoaderTest extends CamelOsgiTestSupport {
    
    @Test
    public void testLoad() throws Exception {               
        OsgiAnnotationTypeConverterLoader loader = new OsgiAnnotationTypeConverterLoader(getPackageScanClassResolver());
        MockTypeConverterRegistry registry = new MockTypeConverterRegistry();
        loader.load(registry);
        assertTrue("There should have at lest one fallback converter", registry.getFallbackTypeConverters().size() >= 1);        
        assertTrue("There should have at lest one converter", registry.getTypeConverters().size() >= 1);
    }

}