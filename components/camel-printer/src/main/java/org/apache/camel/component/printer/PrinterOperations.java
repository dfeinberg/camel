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
package org.apache.camel.component.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrinterOperations implements PrinterOperationsInterface {
    private static final transient Log LOG = LogFactory.getLog(PrinterOperations.class);
    private PrintService printService;
    private DocPrintJob job;
    private DocFlavor flavor;
    private PrintRequestAttributeSet printRequestAttributeSet;
    private Doc doc;

    public PrinterOperations() throws PrintException {        
        printService = PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            throw new PrintException("Printer Lookup Failure. No Default printer set up for this host");
        }
        job = printService.createPrintJob(); 
        flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new Copies(1));
        printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
        printRequestAttributeSet.add(Sides.ONE_SIDED);
    }

    public PrinterOperations(PrintService printService, DocPrintJob job, DocFlavor flavor, PrintRequestAttributeSet printRequestAttributeSet) throws PrintException {
        this();
        this.setPrintService(printService);
        this.setJob(job);  
        this.setFlavor(flavor);
        this.setPrintRequestAttributeSet(printRequestAttributeSet);
    }    
    
    public PrinterOperations(DocPrintJob job, DocFlavor flavor, PrintRequestAttributeSet printRequestAttributeSet) throws PrintException {
        this();
        this.setJob(job);
        this.setFlavor(flavor);
        this.setPrintRequestAttributeSet(printRequestAttributeSet);
    }
    
    public PrinterOperations(DocFlavor flavor, PrintRequestAttributeSet printRequestAttributeSet) throws PrintException {
        this();
        this.setFlavor(flavor);
        this.setPrintRequestAttributeSet(printRequestAttributeSet);
    } 

    public PrinterOperations(PrintRequestAttributeSet printRequestAttributeSet) throws PrintException {
        this();
        this.setPrintRequestAttributeSet(printRequestAttributeSet);
    }
    
    public void print(Doc doc, int copies, boolean sendToPrinter, String mimeType) throws PrintException {
        byte[] buffer = null;
        File file;
        
        LOG.trace("In printerOperations.print()");
        LOG.trace("Print Service = " + this.printService.getName());
        LOG.trace("About to print " + copies + " copy(s)");
        
        for (int i = 0; i < copies; i++) {
            if (!sendToPrinter) {
                LOG.debug("\tPrint Flag is set to false. This job(s) will not be printed until this setting remains in effect. Please set the flag to true or remove the setting");
                if (mimeType.equalsIgnoreCase("GIF") || mimeType.equalsIgnoreCase("RENDERABLE_IMAGE")) {
                    file = new File("./target/TestPrintJobNo" + i + "_" + UUID.randomUUID() + ".gif");
                } else if (mimeType.equalsIgnoreCase("JPEG")) {
                    file = new File("./target/TestPrintJobNo" + i + "_" + UUID.randomUUID() + ".jpeg");
                } else if (mimeType.equalsIgnoreCase("PDF")) {
                    file = new File("./target/TestPrintJobNo" + i + "_" + UUID.randomUUID() + ".pdf");
                } else {
                    file = new File("./target/TestPrintJobNo" + i + "_" + UUID.randomUUID() + ".txt");
                }
                LOG.debug("\tWriting Print Job to File: " + file.getAbsolutePath());
                try {
                    if (buffer == null) {
                        InputStream stream = doc.getStreamForBytes();
                        buffer = new byte[stream.available()];
                        int n = stream.available();
                        for (int j = 0; j < n; j++) {
                            buffer[j] = (byte)stream.read();
                        }
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(buffer);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    throw new PrintException("Error writing Document to the target file " + file.getAbsolutePath());
                }    
            } else {
                LOG.debug("\tIssuing Job " + i + " to Printer: " + this.printService.getName());
                print(doc);
            }
        }
    }
        
    public void print(Doc doc) throws PrintException {
        job.print(doc, printRequestAttributeSet);
    }

    public PrintService getPrintService() {
        return printService;
    }

    public void setPrintService(PrintService printService) {
        this.printService = printService;
    }

    public DocPrintJob getJob() {
        return job;
    }

    public void setJob(DocPrintJob job) {
        this.job = job;
    }

    public DocFlavor getFlavor() {
        return flavor;
    }

    public void setFlavor(DocFlavor flavor) {
        this.flavor = flavor;
    }

    public PrintRequestAttributeSet getPrintRequestAttributeSet() {
        return printRequestAttributeSet;
    }

    public void setPrintRequestAttributeSet(PrintRequestAttributeSet printRequestAttributeSet) {
        this.printRequestAttributeSet = printRequestAttributeSet;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

}
