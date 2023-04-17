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
import com.ancevt.excluder.util.LocalDateTimeUtil;
import com.ancevt.excluder.util.PrintUtil;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.util.args.Args;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandExclude implements Command {

    private static final LocalStorage localStorage = ExcluderLocalStorage.localStorage();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void apply(Args args) {
        String dateTimeString = LocalDateTimeUtil.currentLocalDateTimeAsString();
        Path currentDir = DirectoryUtil.currentDirectory().toAbsolutePath();
        Path storageTargetDir = DirectoryUtil.fileStorageDirectory().resolve(dateTimeString).toAbsolutePath();

        try {
            Files.createDirectories(storageTargetDir);
            args.skip(1);

            List<String> objectList = new ArrayList<>();
            while (args.hasNext()) {
                String object = args.next();
                Path sourcePath = Path.of(object);
                if (Files.exists(sourcePath)) {
                    objectList.add(object);
                } else {
                    PrintUtil.print("Object " + sourcePath + " does not exist");
                }
            }

            if(objectList.isEmpty()) return;

            String key = currentDir.toString();

            Entry entry = localStorage.contains(key) ?
                    objectMapper.readValue(localStorage.getString(key), Entry.class) : new Entry();

            entry.getData().put(dateTimeString, List.copyOf(objectList));

            localStorage.put(key, objectMapper.writeValueAsString(entry));

            objectList.forEach(object -> {
                try {
                    Path sourcePath = Path.of(object);
                    Path targetPath = storageTargetDir.resolve(object);
                    if (Files.exists(sourcePath)) {
                        Files.move(sourcePath, targetPath);
                        PrintUtil.print(object + " -> " + targetPath);
                    } else {
                        PrintUtil.print("Object " + sourcePath + " does not exist");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
