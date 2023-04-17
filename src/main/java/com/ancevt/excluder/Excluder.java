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
package com.ancevt.excluder;

import com.ancevt.excluder.command.Command;
import com.ancevt.excluder.command.CommandBack;
import com.ancevt.excluder.command.CommandClear;
import com.ancevt.excluder.command.CommandExclude;
import com.ancevt.excluder.command.CommandHere;
import com.ancevt.excluder.command.CommandList;
import com.ancevt.excluder.command.CommandObject;
import com.ancevt.excluder.util.PrintUtil;
import com.ancevt.util.args.Args;

import java.util.Map;

import static java.util.Map.entry;

public class Excluder {

    public static void main(String[] args) {
        new Excluder(Args.of(args));
    }

    private static final Map<String, Pair> commandMap = Map.ofEntries(
            entry("help",   new Pair(Excluder::help,       "list of Excluder commands")),
            entry("list",   new Pair(new CommandList(),    "list of excluded objects")),
            entry("ex",     new Pair(new CommandExclude(), "exclude object")),
            entry("back",   new Pair(new CommandBack(),    "quick back object")),
            entry("object", new Pair(new CommandObject(),  "back object exactly")),
            entry("here",   new Pair(new CommandHere(),    "list of excluded objects in current directory")),
            entry("clear",  new Pair(new CommandClear(),   "clear objects"))
    );

    public Excluder(Args args) {
        String commandString = args.get(String.class, 0, "help");
        Pair p = commandMap.get(commandString);
        if (p != null) {
            Command command = commandMap.get(commandString).command();
            command.apply(args);
        } else {
            help(args);
        }
    }

    private static void help(Args args) {
        commandMap.forEach((k, v) -> PrintUtil.print(k + "\t" + v.help()));
    }

    private record Pair(Command command, String help) {
    }
}
