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
package com.github.stagirs.lingvo.morph.model;

import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Set;
import static org.apache.commons.lang.math.NumberUtils.toFloat;
import static org.apache.commons.lang.math.NumberUtils.toInt;

/**
 *
 * @author Dmitriy Malakhov
 */
public class RuleMapping {
    /**
     * Измеряется от 0 до 1
     * Чем больше, тем меньше памяти, минимально 110мб.
     * Чем меньше тем больше скорость работы.
     */
    private static float loadFactor = 0.5f;
    static {
        if(System.getProperty("stagirs.lingvo.morph.mapping.loadfactor") != null){
            loadFactor = toFloat(System.getProperty("stagirs.lingvo.morph.mapping.loadfactor"));
        }
    }
    String suf;
    TObjectIntHashMap<String> prefcommons;
    Rule[] rules;

    
    public RuleMapping(String suf, TObjectIntHashMap<String> prefcommons, Rule[] rules) {
        this.prefcommons = prefcommons;
        this.rules = rules;
        this.suf = suf;
    }
    
    public static RuleMapping parse(String str){
        String[] parts = str.split("\t", -1);
        String suf = parts[0];
        Rule[] rules = new Rule[toInt(parts[1])];
        for (int i = 0; i < rules.length; i++) {
            rules[i] = Rule.parse(parts[2 + i]);
        }
        TObjectIntHashMap<String> prefcommons = new TObjectIntHashMap<String>((parts.length - 2 + rules.length) / 2, loadFactor, -1);
        for (int i = 2 + rules.length; i < parts.length; i+=2) {
            prefcommons.put(parts[i].intern(), toInt(parts[i + 1]));
        }
        return new RuleMapping(suf, prefcommons, rules);
    }

    public Set<String> getPrefcommons() {
        return prefcommons.keySet();
    }

    public Rule[] getRules() {
        return rules;
    }
    
    public String getSuf() {
        return suf;
    }

    public Rule getRule(String key) {
        int index = prefcommons.get(key);
        if(index < 0){
            return null;
        }
        return rules[index];
    }
    
    public static String serialize(RuleMapping mapping){
        StringBuilder sb = new StringBuilder();
        sb.append(mapping.suf).append("\t").append(mapping.rules.length);
        for (Rule rule : mapping.rules) {
            sb.append("\t").append(Rule.serialize(rule));
        }
        for (String prefcommon : mapping.prefcommons.keySet()) {
            sb.append("\t").append(prefcommon);
            sb.append("\t").append(mapping.prefcommons.get(prefcommon));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return serialize(this);
    }
    
    
}
