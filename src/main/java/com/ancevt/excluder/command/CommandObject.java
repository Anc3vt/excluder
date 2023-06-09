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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.ancevt.excluder.command.Utils.isDirectoryEmpty;

public class CommandObject implements Command {

    private static final LocalStorage localStorage = ExcluderLocalStorage.localStorage();

    @Override
    public void apply(Args args) {
        args.skip();

        String object = args.next();
        String pathPart = args.next();

        Map<Path, Path> filesToMove = new HashMap<>();

        try (Stream<Path> stream = Files.walk(DirectoryUtil.fileStorageDirectory())) {
            stream.forEach(path -> {
                String pathString = path.toString();
                if (pathString.endsWith(object) && pathString.contains(pathPart)) {

                    try {
                        String entryString = localStorage.getString(DirectoryUtil.currentDirectory().toString());
                        Entry entry = new ObjectMapper().readValue(entryString, Entry.class);
                        String dateDirName = Utils.extractParentDirName(path);
                        if (entry.getData().containsKey(dateDirName)) {
                            List<String> list = entry.getData().get(dateDirName);
                            list.remove(object);

                            if (list.isEmpty()) entry.getData().remove(dateDirName);

                            if (entry.getData().isEmpty()) {
                                localStorage.remove(DirectoryUtil.currentDirectory().toString());
                            } else {
                                localStorage.put(DirectoryUtil.currentDirectory().toString(), new ObjectMapper().writeValueAsString(entry));
                            }
                        }

                        filesToMove.put(path, DirectoryUtil.currentDirectory().resolve(path.getFileName().toString()));


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            filesToMove.forEach((k, v) -> {
                try {
                    Files.move(k, v);
                    PrintUtil.println(k + " -> " + object);

                    if (isDirectoryEmpty(k.getParent())) {
                        Files.deleteIfExists(k.getParent());
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
