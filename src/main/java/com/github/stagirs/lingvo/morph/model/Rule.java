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

/**
 *
 * @author Dmitriy Malakhov
 */
public class Rule{

    RuleItem[][] items;
    String pref;
    String suf;
    
    public Rule(String pref, String suf, RuleItem[][] items) {
        this.pref = pref;
        this.suf = suf;
        this.items = items;
    }

    public String getPref() {
        return pref;
    }

    public String getSuf() {
        return suf;
    }
    
    public RuleItem[][] getItems() {
        return items;
    }
    
    public static Rule parse(String line) {
        String[] parts = line.split("  ");
        String pref = parts[0];
        String suf = parts[1];
        RuleItem[][] items = new RuleItem[parts.length - 2][];
        for (int i = 0; i < items.length; i++) {
            String[] pp = parts[i + 2].split(" ");
            items[i] = new RuleItem[pp.length];
            for (int j = 0; j < pp.length; j++) {
                items[i][j] = RuleItem.parse(pp[j]);
            }
        }
        return new Rule(pref.intern(), suf.intern(), items);
    }
    
    public static String serialize(Rule rule) {
        StringBuilder sb = new StringBuilder();
        sb.append(rule.pref).append("  ").append(rule.suf);
        for (RuleItem[] items : rule.items) {
            sb.append("  ");
            sb.append(RuleItem.serialize(items[0]));
            for (int i = 1; i < items.length; i++) {
                sb.append(" ");
                sb.append(RuleItem.serialize(items[i]));
            }
        }
        return sb.toString(); 
    }

    @Override
    public String toString() {
        return serialize(this);
    }
}
