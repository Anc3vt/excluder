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

import com.ancevt.util.texttable.TextTable;

import java.util.Map;

public class PrintUtil {

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void print(Object o) {
        System.out.print(o);
    }

    public static <T> void printMapAsTextTable(String key, String value, Map<String, T> map) {
        TextTable textTable = new TextTable(false, key, value);
        map.forEach((cells, cells2) -> textTable.addRow(cells, cells2));
        PrintUtil.println(textTable.render());
    }
}
