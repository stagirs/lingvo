/*
 * Copyright 2018 Dmitriy Malakhov.
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
package com.github.stagirs.lingvo.build.model;

import java.util.List;

/**
 *
 * @author Dmitriy Malakhov
 */
public class Annotation {
    public static class Item{
        int indexFrom;
        int indexTo;
        String id;

        public Item() {
        }

        public Item(int indexFrom, int indexTo, String id) {
            this.indexFrom = indexFrom;
            this.indexTo = indexTo;
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public int getIndexFrom() {
            return indexFrom;
        }

        public int getIndexTo() {
            return indexTo;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setIndexFrom(int indexFrom) {
            this.indexFrom = indexFrom;
        }

        public void setIndexTo(int indexTo) {
            this.indexTo = indexTo;
        }

        @Override
        public String toString() {
            return id + "/" + indexFrom + "/" + indexTo;
        }
        
    }
    
    String text;
    List<Item> items;

    public Annotation() {
    }
    

    public Annotation(String text, List<Item> items) {
        this.text = text;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
    
    public String getWord(Item item){
        return text.substring(item.indexFrom, item.indexTo);
    }

    public String getText() {
        return text;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    
}
