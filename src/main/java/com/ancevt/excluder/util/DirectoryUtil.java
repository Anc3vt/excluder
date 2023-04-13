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

import com.ancevt.excluder.Excluder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.ancevt.commons.platformdepend.OsDetector.isWindows;

public class DirectoryUtil {

    public static Path fileStorageDirectory() {
        String homeDir = System.getProperty("user.home");

        Path dir;
        if (isWindows()) {
            dir = Path.of(homeDir + "\\AppData\\Roaming\\" + Excluder.class.getName());
        } else {
            dir = Path.of(homeDir + "/.local/share/" + Excluder.class.getName());
        }

        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return dir;
    }

    public static Path currentDirectory() {
        return Path.of(System.getProperty("user.dir"));
    }
}
