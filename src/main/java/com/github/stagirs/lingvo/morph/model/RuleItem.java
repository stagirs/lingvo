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
public class RuleItem {
    private String suf;
    private Form norm;
    private Form raw;

    public RuleItem(String suf, Form norm, Form raw) {
        this.suf = suf.intern();
        this.norm = norm;
        this.raw = raw;
    }

    public String getSuf() {
        return suf;
    }

    public Form getNorm() {
        return norm;
    }

    public Form getRaw() {
        return raw;
    }
    
    public static RuleItem parse(String line) {
        String[] parts = line.split("/", -1);
        return new RuleItem(parts[0], new Form(parts[1]), new Form(parts[2]));
    }
    
    public static String serialize(RuleItem ruleItem) {
        StringBuilder sb = new StringBuilder();
        sb.append(ruleItem.suf).append("/")
                .append(ruleItem.norm).append("/")
                .append(ruleItem.raw);
        return sb.toString(); 
    }

    @Override
    public String toString() {
        return serialize(this);
    }
}
