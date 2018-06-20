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
package com.github.stagirs.lingvo.morph.model;

import com.github.stagirs.lingvo.model.Form;

/**
 *
 * @author Dmitriy Malakhov
 */
public class Morph {
    String word;
    String common;
    Rule rule;

    public Morph(String word, Rule rule) {
        this.word = word;
        this.common = word.substring(rule.pref.length(), word.length() - rule.suf.length());
        this.rule = rule;
    }

    public Form getRawForm() {
        return rule.items[0][0].getRaw();
    }
    
    public Form getNormForm() {
        return rule.items[0][0].getNorm();
    }
    
    public int getNormCount(){
        return rule.items.length;
    }
    
    public Form[] getRawForms(int normId) {
        Form[] forms = new Form[rule.items[normId].length];
        for (int i = 0; i < rule.items[normId].length; i++) {
            forms[i] = rule.items[normId][i].getRaw();
        }
        return forms;
    }
    
    public Form[] getNormForms(int normId) {
        Form[] forms = new Form[rule.items[normId].length];
        for (int i = 0; i < rule.items[normId].length; i++) {
            forms[i] = rule.items[normId][i].getNorm();
        }
        return forms;
    }
    
    public String getNorm() {
        return common + rule.items[0][0].getSuf();
    }
    
    public String getNorm(int normId) {
        return common + rule.items[normId][0].getSuf();
    }

    public String getRaw() {
        return word;
    }
    
    public boolean isStop(){
        for (int i = 0; i < rule.items.length; i++) {
            for (int j = 0; j < rule.items[i].length; j++) {
                if(rule.items[i][j].getRaw().isStop()){
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getSuf(){
        return rule.getSuf();
    }
    
    public String getPref(){
        return rule.getPref();
    }
}
