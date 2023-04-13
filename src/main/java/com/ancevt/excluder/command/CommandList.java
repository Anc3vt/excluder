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
import com.ancevt.excluder.util.PrintUtil;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.util.args.Args;

public class CommandList implements Command {
    @Override
    public void apply(Args args) {
        LocalStorage ls = ExcluderLocalStorage.localStorage();
        String prefix = args.get(String.class, 1, "");
        PrintUtil.printMapAsTextTable("Path", "Data", ls.toSortedMapGroup(prefix));
    }
}