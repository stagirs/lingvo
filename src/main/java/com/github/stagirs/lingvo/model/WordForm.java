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
package com.github.stagirs.lingvo.model;


/**
 *
 * @author Dmitriy Malakhov
 */
public class WordForm {
    String word;
    Form form;

    public WordForm(String word, Form form) {
        this.word = word;
        this.form = form;
    }
    

    public Form getForm() {
        return form;
    }

    public String getWord() {
        return word;
    }
    
    public static String serialize(WordForm item){
        return item.word + " " + item.form;
    }
    
    public static WordForm parse(String str){
        String[] parts = str.split(" ", -1);
        return new WordForm(parts[0], new Form(parts[1]));
    }
}
