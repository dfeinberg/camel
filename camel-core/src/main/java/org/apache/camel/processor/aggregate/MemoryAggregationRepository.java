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
package org.apache.camel.processor.aggregate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.ServiceSupport;
import org.apache.camel.spi.AggregationRepository;

/**
 * A memory based {@link org.apache.camel.spi.AggregationRepository} which stores in memory only.
 *
 * @version $Revision$
 */
public class MemoryAggregationRepository extends ServiceSupport implements AggregationRepository<Object> {

    private final Map<Object, Exchange> cache = new ConcurrentHashMap<Object, Exchange>();

    public Exchange add(CamelContext camelContext, Object key, Exchange exchange) {
        return cache.put(key, exchange);
    }

    public Exchange get(CamelContext camelContext, Object key) {
        return cache.get(key);
    }

    public void remove(CamelContext camelContext, Object key) {
        cache.remove(key);
    }

    @Override
    protected void doStart() throws Exception {
    }

    @Override
    protected void doStop() throws Exception {
        cache.clear();
    }

}