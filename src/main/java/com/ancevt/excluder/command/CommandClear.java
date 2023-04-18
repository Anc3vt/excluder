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
import com.ancevt.util.args.Args;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.ancevt.excluder.command.Utils.deleteDirectory;

public class CommandClear implements Command {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void apply(Args args) {
        String prefix = args.get(String.class, 1, "");
        Map<String, String> map = ExcluderLocalStorage.localStorage().toSortedMapGroup(prefix);

        map.forEach((k, v) -> {
            try {
                Entry entry = objectMapper.readValue(v, Entry.class);
                entry.getData().forEach((date, objectList) -> {
                    Path dateDir = DirectoryUtil.fileStorageDirectory().resolve(date);

                    objectList.forEach(object -> {
                        try {
                            Path pathToDelete = dateDir.resolve(object);

                            if (Files.isDirectory(pathToDelete)) {
                                deleteDirectory(pathToDelete);
                                PrintUtil.println("deleted " + pathToDelete);
                            } else if (Files.deleteIfExists(pathToDelete)) {
                                PrintUtil.println("deleted " + pathToDelete);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    try {
                        Files.deleteIfExists(dateDir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        ExcluderLocalStorage.localStorage().removeGroup(prefix);
    }
}
