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
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.ancevt.excluder.command.Utils.extractParentDirName;
import static com.ancevt.excluder.command.Utils.isDirectoryEmpty;
import static com.ancevt.excluder.command.Utils.multiplicityState;

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
                    if (!LocalDateTimeUtil.isLocalDateTime(Path.of(pathString).getFileName().toString())) {
                        PrintUtil.print("   " + pathString);
                    }
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

                try {
                    String entryString = localStorage.getString(DirectoryUtil.currentDirectory().toString());
                    if (entryString != null) {
                        Entry entry = new ObjectMapper().readValue(entryString, Entry.class);
                        entry.getData().get(dateDirName).remove(object);
                        localStorage.put(DirectoryUtil.currentDirectory().toString(), new ObjectMapper().writeValueAsString(entry));

                        Path p = DirectoryUtil.currentDirectory().resolve(object);
                        Files.move(path, p);

                        PrintUtil.print(path + " -> " + object);

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
