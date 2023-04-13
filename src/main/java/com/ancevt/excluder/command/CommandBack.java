package com.ancevt.excluder.command;

import com.ancevt.excluder.ExcluderLocalStorage;
import com.ancevt.excluder.model.Entry;
import com.ancevt.excluder.util.DirectoryUtil;
import com.ancevt.excluder.util.PrintUtil;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.util.args.Args;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CommandBack implements Command {

    private static final LocalStorage localStorage = ExcluderLocalStorage.localStorage();

    @Override
    public void apply(Args args) {
        if (!localStorage.contains(DirectoryUtil.currentDirectory().toAbsolutePath().toString())) {
            PrintUtil.print("No excluded objects here");
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
                PrintUtil.print(object + ":");

                list.forEach(pathString -> {
                    PrintUtil.print("   " + pathString);
                });

                multiplicityFound.set(true);
            }
        });

        if (multiplicityFound.get()) {
            PrintUtil.print("\nUse: edr object <object> <part_of_path>");
        } else {
            multiplicityState.forEach((object, list) -> {
                String pathString = list.get(0);
                Path path = Path.of(pathString);
                String dateDirName = extractParentDirName(path);

                System.out.println("path: " + path);
                System.out.println("dirname: " + dateDirName);

                try {
                    String entryString = localStorage.getString(DirectoryUtil.currentDirectory().toString());
                    if (entryString != null) {

                        System.out.println("Entry string: " + entryString);

                        Entry entry = new ObjectMapper().readValue(entryString, Entry.class);
                        entry.getData().get(dateDirName).remove(object);
                        localStorage.put(DirectoryUtil.currentDirectory().toString(), new ObjectMapper().writeValueAsString(entry));

                        Files.move(path, DirectoryUtil.currentDirectory().resolve(object));

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

    private static String extractParentDirName(Path path) {
        Path parent = path.getParent();
        return parent.getFileName().toString();
    }

    private static boolean isDirectoryEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> s = Files.list(path)) {
                return s.findFirst().isEmpty();
            }
        }

        return false;
    }

    private Map<String, List<String>> multiplicityState(List<String> objectList) {
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

}
