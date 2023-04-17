/**
 * Copyright (C) 2023 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.excluder.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.zip.DataFormatException;

public class LocalDateTimeUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uu-MM-dd_HH-mm-ss_SSSSSS");

    public static String currentLocalDateTimeAsString() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime stringToLocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
    }

    public static boolean isLocalDateTime(String string) {
        try {
            LocalDateTime.parse(string, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}

/*

    Symbol  Meaning                     Presentation      Examples
    ------  -------                     ------------      -------
    u       year                        year              2004; 04
    y       year-of-era                 year              2004; 04
    M/L     month-of-year               number/text       7; 07; Jul; July; J
    d       day-of-month                number            10

    Symbol  Meaning                     Presentation      Examples
    ------  -------                     ------------      -------
    H       hour-of-day (0-23)          number            0
    m       minute-of-hour              number            30
    s       second-of-minute            number            55
    S       fraction-of-second          fraction          978
    n       nano-of-second              number            987654321

      @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String ldtString = jsonElement.getAsString();
        return LocalDateTime.parse(ldtString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

*/
