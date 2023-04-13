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
            try (Stream<Path> stream = Files.walk(DirectoryUtil.fileStorageDirectory())) {
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
