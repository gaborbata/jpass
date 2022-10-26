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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DateUtils {

    private static final Logger LOG = Logger.getLogger(DateUtils.class.getName());

    private DateUtils() {
        // utility class
    }

    public static DateTimeFormatter createFormatter(String format) {
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(format);
        } catch (IllegalArgumentException | NullPointerException e) {
            LOG.log(Level.WARNING, String.format("Could not parse date format [%s] due to [%s]", format, e.getMessage()));
            formatter = DateTimeFormatter.ISO_DATE;
        }
        return formatter;
    }

    public static String formatIsoDateTime(String dateString, DateTimeFormatter formatter) {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException | NullPointerException e) {
            try {
                // fallback to epoch timestamp
                Date date = new Date(Long.parseLong(dateString));
                dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } catch (NumberFormatException | DateTimeParseException | NullPointerException ex) {
                LOG.log(Level.WARNING, String.format("Could not parse timestamp [%s] due to [%s]", dateString, ex.getMessage()));
                dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
            }
        }
        return formatter.format(dateTime.truncatedTo(ChronoUnit.SECONDS));
    }
}
