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
package org.apache.camel.builder.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class DefaultTransformErrorHandler implements ErrorHandler, ErrorListener {
    private static final transient Log LOG = LogFactory.getLog(DefaultTransformErrorHandler.class);

    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void warning(SAXParseException exception) throws SAXException {
        LOG.warn("parser warning", exception);
        
    }

    public void error(TransformerException exception) throws TransformerException {
        throw exception;
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        throw exception;
    }

    public void warning(TransformerException exception) throws TransformerException {
        LOG.warn("parser warning", exception);
        
    }

}