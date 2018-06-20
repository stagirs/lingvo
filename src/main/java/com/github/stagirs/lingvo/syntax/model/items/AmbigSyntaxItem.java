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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Dmitriy Malakhov
 */
public class AmbigSyntaxItem extends SyntaxItem{
    private final List<WordSyntaxItem> syntaxItems;
    private final boolean exists;

    public AmbigSyntaxItem(List<WordSyntaxItem> syntaxItems, boolean exists, int indexFrom, int indexTo) {
        super(indexFrom, indexTo);
        this.syntaxItems = syntaxItems;
        this.exists = exists;
    }

    @Override
    public String getName() {
        return syntaxItems.get(0).getName();
    }

    @Override
    public String getType() {
        return syntaxItems.get(0).getType();
    }

    @Override
    public double getScore() {
        return syntaxItems.get(0).getScore();
    }

    public List<WordSyntaxItem> getSyntaxItems() {
        return syntaxItems;
    }

    @Override
    public void setIndexFrom(int indexFrom) {
        super.setIndexFrom(indexFrom);
        for (WordSyntaxItem syntaxItem : syntaxItems) {
            syntaxItem.setIndexFrom(indexFrom);
        }
    }

    @Override
    public void setIndexTo(int indexTo) {
        super.setIndexTo(indexTo);
        for (WordSyntaxItem syntaxItem : syntaxItems) {
            syntaxItem.setIndexTo(indexTo);
        }
    }

    public boolean isExists() {
        return exists;
    }
    
    public void update(){
        Collections.sort(syntaxItems, new Comparator<WordSyntaxItem>(){
            @Override
            public int compare(WordSyntaxItem o1, WordSyntaxItem o2) {
                return - Double.compare(o1.getScore(), o2.getScore());
            }
        });
    }
    
}
