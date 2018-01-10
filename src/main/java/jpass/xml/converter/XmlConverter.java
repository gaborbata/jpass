/*
 * JPass
 *
 * Copyright (c) 2009-2018 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jpass.xml.converter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for conversion between objects and streams representing XMLs.
 *
 * @author Gabor_Bata
 *
 * @param <T> the type of object to map
 */
public class XmlConverter<T> {

    private final Class<T> documentClass;
    private final XmlMapper mapper;

    public XmlConverter(Class<T> documentClass) {
        this.documentClass = documentClass;
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        this.mapper = new XmlMapper(module);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
    }

    /**
     * Maps the given object to the given output stream.
     *
     * @param document the document object which represents the XML document
     * @param outputStream the output stream
     * @throws IOException if any error occurred
     */
    public void write(T document, OutputStream outputStream) throws IOException {
        mapper.writeValue(outputStream, document);
    }

    /**
     * Maps the given input stream to a document object.
     *
     * @param inputStream the input stream
     * @return the document object
     * @throws IOException if any error occurred
     */
    public T read(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, documentClass);
    }
}
