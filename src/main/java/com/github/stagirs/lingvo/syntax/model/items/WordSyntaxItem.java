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

import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.syntax.SyntaxStatistics;
import com.github.stagirs.lingvo.syntax.model.SyntaxItem;

/**
 *
 * @author Dmitriy Malakhov
 */
public class WordSyntaxItem extends SyntaxItem{
    

    String name;
    String rawTerm;
    Form rawForm;
    
    String normTerm;
    Form normForm;
    
    String type;
    double score;
    boolean exists;

    
    public WordSyntaxItem(String name, String rawTerm, Form rawForm, String normTerm, Form normForm, boolean exists, int indexFrom, int indexTo) {
        super(indexFrom, indexTo);
        this.name = name;
        this.rawTerm = rawTerm;
        this.rawForm = rawForm;
        this.normTerm = normTerm;
        this.normForm = normForm;
        this.exists = exists;
        this.type = SyntaxStatistics.getWordType(rawTerm, rawForm);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
    

    public Form getNormForm() {
        return normForm;
    }

    public String getNormTerm() {
        return normTerm;
    }

    public Form getRawForm() {
        return rawForm;
    }

    public String getRawTerm() {
        return rawTerm;
    }

    public boolean isExists() {
        return exists;
    }
}
