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

import com.ancevt.excluder.util.DirectoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

class Utils {

    static Map<String, List<String>> multiplicityState(List<String> objectList) {
        Map<String, List<String>> result = new TreeMap<>();

        for (String object : objectList) {
            try (Stream<Path> stream = Files.walk(DirectoryUtil.fileStorageDirectory(), 2)) {
                stream.forEach(path -> {
                    if (path.toString().endsWith(object)) {
                        List<String> list = result.get(object);
                        if (list == null) list = new ArrayList<>();
                        list.add(path.toString());
                        result.put(object, list);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    static String extractParentDirName(Path path) {
        return path.getParent().getFileName().toString();
    }

    static boolean isDirectoryEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> s = Files.list(path)) {
                return s.findFirst().isEmpty();
            }
        }

        return false;
    }

    static void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
