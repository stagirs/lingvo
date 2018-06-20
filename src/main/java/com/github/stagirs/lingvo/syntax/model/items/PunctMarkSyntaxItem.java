/*
 * Copyright 2017 Dmitriy Malakhov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.stagirs.lingvo.syntax.model.items;

import com.github.stagirs.lingvo.syntax.model.SyntaxItem;

/**
 *
 * @author Dmitriy Malakhov
 */
public class PunctMarkSyntaxItem extends SyntaxItem{
    String name;

    public PunctMarkSyntaxItem(String name, int indexFrom, int indexTo) {
        super(indexFrom, indexTo);
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "PunctMark";
    }

    @Override
    public double getScore() {
        return 0;
    }
    
}
