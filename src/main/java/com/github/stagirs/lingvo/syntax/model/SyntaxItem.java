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
package com.github.stagirs.lingvo.syntax.model;

/**
 *
 * @author Dmitriy Malakhov
 */
public abstract class SyntaxItem {
    private int indexFrom;
    private int indexTo;

    public SyntaxItem(int indexFrom, int indexTo) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
    }
    
    public abstract String getName();
    public abstract String getType();
    public abstract double getScore();

    public int getIndexFrom() {
        return indexFrom;
    }

    public int getIndexTo() {
        return indexTo;
    }

    public void setIndexFrom(int indexFrom) {
        this.indexFrom = indexFrom;
    }

    public void setIndexTo(int indexTo) {
        this.indexTo = indexTo;
    }
    
    @Override
    public String toString() {
        return getName() + ";" + getType() + ";" + getScore() + ";" + getIndexFrom() + ";" + getIndexTo(); 
    }
}
