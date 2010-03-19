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
package org.apache.camel.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.processor.Pipeline;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.spi.RouteContext;

/**
 * Represents an XML &lt;intercept/&gt; element
 *
 * @version $Revision$
 */
@XmlRootElement(name = "intercept")
@XmlAccessorType(XmlAccessType.FIELD)
public class InterceptDefinition extends OutputDefinition<ProcessorDefinition> {

    // TODO: support stop later (its a bit hard as it needs to break entire processing of route)

    @XmlTransient
    protected Processor output;

    @XmlTransient
    protected final List<Processor> intercepted = new ArrayList<Processor>();

    public InterceptDefinition() {
    }

    @Override
    public String toString() {
        return "Intercept[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "intercept";
    }

    @Override
    public String getLabel() {
        return "intercept";
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public Processor createProcessor(final RouteContext routeContext) throws Exception {
        // create the output processor
        output = createOutputsProcessor(routeContext);

        // add the output as a intercept strategy to the route context so its invoked on each processing step
        routeContext.getInterceptStrategies().add(new InterceptStrategy() {
            private Processor interceptedTarget;

            public Processor wrapProcessorInInterceptors(CamelContext context, ProcessorDefinition definition,
                                                         Processor target, Processor nextTarget) throws Exception {
                // prefer next target over target as next target is the real target
                interceptedTarget = nextTarget != null ? nextTarget : target;

                // remember the target that was intercepted
                intercepted.add(interceptedTarget);

                if (interceptedTarget != null) {
                    // wrap in a pipeline so we continue routing to the next
                    List<Processor> list = new ArrayList<Processor>(2);
                    list.add(output);
                    list.add(interceptedTarget);
                    return new Pipeline(context, list);
                } else {
                    return output;
                }
            }

            @Override
            public String toString() {
                return "intercept[" + (interceptedTarget != null ? interceptedTarget : output) + "]";
            }
        });

        // remove me from the route so I am not invoked in a regular route path
        routeContext.getRoute().getOutputs().remove(this);
        // and return no processor to invoke next from me
        return null;
    }

    /**
     * Applies this interceptor only if the given predicate is true
     *
     * @param predicate the predicate
     * @return the builder
     */
    public ChoiceDefinition when(Predicate predicate) {
        return choice().when(predicate);
    }

    /**
     * This method is <b>only</b> for handling some post configuration
     * that is needed from the Spring DSL side as JAXB does not invoke the fluent
     * builders, so we need to manually handle this afterwards, and since this is
     * an interceptor it has to do a bit of magic logic to fixup to handle predicates
     * with or without proceed/stop set as well.
     */
    public void afterPropertiesSet() {
        if (getOutputs().size() == 0) {
            // no outputs
            return;
        }

        ProcessorDefinition first = getOutputs().get(0);
        if (first instanceof WhenDefinition) {
            WhenDefinition when = (WhenDefinition) first;
            // move this outputs to the when, expect the first one
            // as the first one is the interceptor itself
            for (int i = 1; i < outputs.size(); i++) {
                ProcessorDefinition out = outputs.get(i);
                when.addOutput(out);
            }
            // remove the moved from the original output, by just keeping the first one
            ProcessorDefinition keep = outputs.get(0);
            clearOutput();
            outputs.add(keep);
        }
    }

    public Processor getInterceptedProcessor(int index) {
        // avoid out of bounds
        if (index <= intercepted.size() - 1) {
            return intercepted.get(index);
        } else {
            return null;
        }
    }
}