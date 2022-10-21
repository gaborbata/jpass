/*
 * JPass
 *
 * Copyright (c) 2009-2022 Gabor Bata
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
package jpass.util;

import org.junit.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Test
    public void formatIsoDateTimeNullTest() {

        String dateStringCorrect = "2021-03-02T20:11:58";
        DateTimeFormatter formatter = DateUtils.createFormatter(null);

        String result = DateUtils.formatIsoDateTime(dateStringCorrect, formatter);

        String expectedResult = "2021-03-02";

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeEmptyTest() {

        String dateStringCorrect = "2021-03-02T20:11:58";
        DateTimeFormatter formatter = DateUtils.createFormatter("");

        String expectedResult = "";
        String result = DateUtils.formatIsoDateTime(dateStringCorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeIncorrectDateCorrectTest() {

        String dateStringCorrect = "2021-03-02T20:11:58";
        String invalidFormat = "yyy mmm dddd";
        DateTimeFormatter formatter = DateUtils.createFormatter(invalidFormat);

        String expectedResult = "2021-03-02";
        String result = DateUtils.formatIsoDateTime(dateStringCorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeIncorrectDateIncorrectTest() {

        String dateStringIncorrect = "2021-03-0:11:58";
        String invalidFormat = "yyy mmm dddd";
        DateTimeFormatter formatter = DateUtils.createFormatter(invalidFormat);

        String expectedResult = "1970-01-01";
        String result = DateUtils.formatIsoDateTime(dateStringIncorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeIncorrectDateEmptyTest() {

        String dateStringEmpty = "";
        String invalidFormat = "yyy mmm dddd";
        DateTimeFormatter formatter = DateUtils.createFormatter(invalidFormat);

        String expectedResult = "1970-01-01";
        String result = DateUtils.formatIsoDateTime(dateStringEmpty, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeIncorrectDateNullTest() {

        String dateStringEmpty = null;
        String invalidFormat = "yyy mmm dddd";
        DateTimeFormatter formatter = DateUtils.createFormatter(invalidFormat);

        String expectedResult = "1970-01-01";
        String result = DateUtils.formatIsoDateTime(dateStringEmpty, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeCorrectDateCorrectTest() {

        String dateStringCorrect = "2021-03-02T20:11:58";
        String validFormat = "dd.MM.yyyy";
        DateTimeFormatter formatter = DateUtils.createFormatter(validFormat);

        String expectedResult = "02.03.2021";
        String result = DateUtils.formatIsoDateTime(dateStringCorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeCorrectDateIncorrectTest() {

        String dateStringIncorrect = "2021-03-0:11:58";
        String validFormat = "dd.MM.yyyy";
        DateTimeFormatter formatter = DateUtils.createFormatter(validFormat);

        String expectedResult = "01.01.1970";
        String result = DateUtils.formatIsoDateTime(dateStringIncorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeCorrectDateEmptyTest() {

        String dateStringEmpty = "";
        String validFormat = "dd.MM.yyyy";
        DateTimeFormatter formatter = DateUtils.createFormatter(validFormat);

        String expectedResult = "01.01.1970";
        String result = DateUtils.formatIsoDateTime(dateStringEmpty, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeCorrectDateNullTest() {

        String dateStringNull = null;
        String validFormat = "dd.MM.yyyy";
        DateTimeFormatter formatter = DateUtils.createFormatter(validFormat);

        String expectedResult = "01.01.1970";
        String result = DateUtils.formatIsoDateTime(dateStringNull, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeNullDateCorrect() {

        String dateStringCorrect = "2021-03-02T20:11:58";
        String nullFormat = null;
        DateTimeFormatter formatter = DateUtils.createFormatter(nullFormat);

        String expectedResult = "2021-03-02";
        String result = DateUtils.formatIsoDateTime(dateStringCorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeNullDateIncorrect() {

        String dateStringIncorrect = "2021-03-0:11:58";
        String nullFormat = null;
        DateTimeFormatter formatter = DateUtils.createFormatter(nullFormat);

        String expectedResult = "1970-01-01";
        String result = DateUtils.formatIsoDateTime(dateStringIncorrect, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeNullDateEmpty() {

        String dateStringEmpty = "";
        String nullFormat = null;
        DateTimeFormatter formatter = DateUtils.createFormatter(nullFormat);

        String expectedResult = "1970-01-01";
        String result = DateUtils.formatIsoDateTime(dateStringEmpty, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeNullDateNull() {

        String dateStringNull = null;
        String nullFormat = null;
        DateTimeFormatter formatter = DateUtils.createFormatter(nullFormat);

        String expectedResult = "1970-01-01";
        String result = DateUtils.formatIsoDateTime(dateStringNull, formatter);

        assertEquals(result, expectedResult);
    }

    @Test
    public void formatIsoDateTimeNullDateNull2() {
        String dateStringNull = "1616697411";
        String nullFormat = null;
        DateTimeFormatter formatter = DateUtils.createFormatter(nullFormat);

        String expectedResult = "1970-01-19";
        String result = DateUtils.formatIsoDateTime(dateStringNull, formatter);

        assertEquals(result, expectedResult);
    }
}
