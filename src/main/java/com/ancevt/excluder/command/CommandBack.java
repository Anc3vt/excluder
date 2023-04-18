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
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ancevt.excluder.command.Utils.extractParentDirName;
import static com.ancevt.excluder.command.Utils.isDirectoryEmpty;
import static com.ancevt.excluder.command.Utils.multiplicityState;

public class CommandBack implements Command {

    private static final LocalStorage localStorage = ExcluderLocalStorage.localStorage();

    @Override
    public void apply(Args args) {
        if (!localStorage.contains(DirectoryUtil.currentDirectory().toAbsolutePath().toString())) {
            PrintUtil.println("No excluded objects here");
            return;
        }

        args.skip();
        List<String> objectList = new ArrayList<>();
        while (args.hasNext()) {
            objectList.add(args.next());
        }

        AtomicBoolean multiplicityFound = new AtomicBoolean(false);

        Map<String, List<String>> multiplicityState = multiplicityState(objectList);
        multiplicityState.forEach((object, list) -> {
            if (list.size() > 1) {
                PrintUtil.println(object + ":");

                list.forEach(pathString -> {
                    if (!LocalDateTimeUtil.isLocalDateTime(Path.of(pathString).getFileName().toString())) {
                        PrintUtil.println("   " + pathString);
                    }
                });

                multiplicityFound.set(true);
            }
        });

        if (multiplicityFound.get()) {
            PrintUtil.println("\nUse: edr object <object> <part_of_path>");
        } else {
            multiplicityState.forEach((object, list) -> {
                String pathString = list.get(0);
                Path path = Path.of(pathString);
                String dateDirName = extractParentDirName(path);

                try {
                    String entryString = localStorage.getString(DirectoryUtil.currentDirectory().toString());
                    if (entryString != null) {
                        Entry entry = new ObjectMapper().readValue(entryString, Entry.class);
                        Map<String, List<String>> entryData = entry.getData();
                        List<String> entryDataList = entryData.get(dateDirName);
                        entryDataList.remove(object);
                        if (entryDataList.isEmpty()) {
                            entryData.remove(dateDirName);
                        }

                        if (entryData.isEmpty()) {
                            localStorage.remove(DirectoryUtil.currentDirectory().toString());
                        } else {
                            localStorage.put(DirectoryUtil.currentDirectory().toString(), new ObjectMapper().writeValueAsString(entry));
                        }

                        Path p = DirectoryUtil.currentDirectory().resolve(object);
                        Files.move(path, p);

                        PrintUtil.println(path + " -> " + object);

                        if (isDirectoryEmpty(path.getParent())) {
                            Files.deleteIfExists(path.getParent());
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


}
