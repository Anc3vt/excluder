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
package com.ancevt.excluder.command;

import com.ancevt.excluder.ExcluderLocalStorage;
import com.ancevt.excluder.model.Entry;
import com.ancevt.excluder.util.DirectoryUtil;
import com.ancevt.excluder.util.PrintUtil;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.util.args.Args;
import com.ancevt.util.texttable.TextTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandHere implements Command{

    @Override
    public void apply(Args args) {
        LocalStorage ls = ExcluderLocalStorage.localStorage();

        String entryString = ls.getString(DirectoryUtil.currentDirectory().toAbsolutePath().toString());
        if(entryString != null) {
            try {
                Entry entry = new ObjectMapper().readValue(entryString, Entry.class);

                TextTable textTable = new TextTable(false, "Date", "Files");
                entry.getData().forEach(textTable::addRow);
                PrintUtil.print(textTable.render());

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
