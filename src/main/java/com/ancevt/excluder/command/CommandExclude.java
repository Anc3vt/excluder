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
                objectList.add(args.next());
            }

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

    public static void main(String[] args) {
        Path p = DirectoryUtil.fileStorageDirectory();

        System.out.println(p.resolve("hello"));
    }
}
