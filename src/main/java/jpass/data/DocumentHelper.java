/*
 * JPass
 *
 * Copyright (c) 2009-2019 Gabor Bata
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
package jpass.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jpass.crypt.io.CryptInputStream;
import jpass.crypt.io.CryptOutputStream;
import jpass.xml.bind.Entries;
import jpass.xml.converter.XmlConverter;

import static jpass.util.StringUtils.stripString;

/**
 * Helper class for reading and writing (encrypted) XML documents.
 *
 * @author Gabor_Bata
 *
 */
public final class DocumentHelper {

    /**
     * File name to read/write.
     */
    private final String fileName;

    /**
     * Key for encryption.
     */
    private final byte[] key;

    /**
     * Converter between document objects and streams representing XMLs
     */
    private static final XmlConverter<Entries> CONVERTER = new XmlConverter<Entries>(Entries.class);

    /**
     * Creates a DocumentHelper instance.
     *
     * @param fileName file name
     * @param key key for encryption
     */
    private DocumentHelper(final String fileName, final byte[] key) {
        this.fileName = fileName;
        this.key = key;
    }

    /**
     * Creates a document helper with no encryption.
     *
     * @param fileName file name
     * @return a new DocumentHelper object
     */
    public static DocumentHelper newInstance(final String fileName) {
        return new DocumentHelper(fileName, null);
    }

    /**
     * Creates a document helper with encryption.
     *
     * @param fileName file name
     * @param key key for encryption
     * @return a new DocumentHelper object
     */
    public static DocumentHelper newInstance(final String fileName, final byte[] key) {
        return new DocumentHelper(fileName, key);
    }

    /**
     * Reads and XML file to an {@link Entries} object.
     *
     * @return the document
     * @throws FileNotFoundException if file is not exists
     * @throws IOException when I/O error occurred
     * @throws DocumentProcessException when file format or password is incorrect
     */
    public Entries readDocument() throws IOException, DocumentProcessException {
        InputStream inputStream = null;
        Entries entries;
        try {
            if (this.key == null) {
                inputStream = new FileInputStream(this.fileName);
            } else {
                inputStream = new GZIPInputStream(new CryptInputStream(new BufferedInputStream(new FileInputStream(this.fileName)), this.key));
            }
            entries = CONVERTER.read(inputStream);
        } catch (Exception e) {
            throw new DocumentProcessException(stripString(e.getMessage()));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return entries;
    }

    /**
     * Writes a document into an XML file.
     *
     * @param document the document
     * @throws DocumentProcessException when document format is incorrect
     * @throws IOException when I/O error occurred
     */
    public void writeDocument(final Entries document) throws DocumentProcessException, IOException {
        OutputStream outputStream = null;
        try {
            if (this.key == null) {
                outputStream = new FileOutputStream(this.fileName);
            } else {
                outputStream = new GZIPOutputStream(new CryptOutputStream(new BufferedOutputStream(new FileOutputStream(this.fileName)), this.key));
            }
            CONVERTER.write(document, outputStream);
        } catch (Exception e) {
            throw new DocumentProcessException(stripString(e.getMessage()));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
