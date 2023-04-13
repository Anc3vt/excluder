package com.ancevt.excluder.command;

import com.ancevt.excluder.ExcluderLocalStorage;
import com.ancevt.excluder.model.Entry;
import com.ancevt.excluder.util.DirectoryUtil;
import com.ancevt.excluder.util.PrintUtil;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.util.args.Args;
import com.ancevt.util.texttable.TextTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandHere implements Command{

    @Override
    public void apply(Args args) {
        LocalStorage ls = ExcluderLocalStorage.localStorage();

        String entryString = ls.getString(DirectoryUtil.currentDirectory().toAbsolutePath().toString());
        if(entryString != null) {
            try {
                Entry entry = new ObjectMapper().readValue(entryString, Entry.class);

                TextTable textTable = new TextTable(false, "Date", "Files");
                entry.getData().forEach(textTable::addRow);
                PrintUtil.print(textTable.render());

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
