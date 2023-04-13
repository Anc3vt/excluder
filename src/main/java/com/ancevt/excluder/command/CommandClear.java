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
                                PrintUtil.print("deleted " + pathToDelete);
                            } else if (Files.deleteIfExists(pathToDelete)) {
                                PrintUtil.print("deleted " + pathToDelete);
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
        ExcluderLocalStorage.localStorage().save();
    }
}
