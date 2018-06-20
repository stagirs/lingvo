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
package com.github.stagirs.lingvo.build;

import com.github.stagirs.lingvo.build.model.Lemma;
import com.github.stagirs.lingvo.model.WordForm;
import com.github.stagirs.lingvo.morph.model.RuleMapping;
import com.github.stagirs.lingvo.morph.model.Rule;
import com.github.stagirs.lingvo.morph.model.RuleItem;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MorphStateMachineBuilder {
    
    private static class Struct{
        TObjectIntHashMap prefcommons = new TObjectIntHashMap();
        List<Rule> rules = new ArrayList<Rule>();
        Map<String, Integer> rule2id = new HashMap<String, Integer>();
    }
    
    public static void main(String[] args) throws IOException {
        Set<String> suffixes = new HashSet();
        Map<String, List<WordForm[]>> raw2WordFormsMap = new HashMap<String, List<WordForm[]>>();
        fill(XmlParser.getLemmas(new File("dict.opcorpora.xml.zip")), suffixes, raw2WordFormsMap);
        Map<String, Struct> sufs = new HashMap<String, Struct>();
        for (Map.Entry<String, List<WordForm[]>> raw2WordForms : raw2WordFormsMap.entrySet()) {
            String raw = raw2WordForms.getKey();
            int[] index = getCommon(suffixes, raw, raw2WordForms.getValue());
            String common = index[0] < index[1] ? raw.substring(index[0], index[1]) : "";
            String rawPref = raw.substring(0, Math.min(index[0], index[1]));
            String rawSuf = raw.substring(index[1]);
            Map<String, List<RuleItem>> map = new HashMap();
            for (WordForm[] wordForm : raw2WordForms.getValue()) {
                String normSuf = wordForm[1].getWord().substring(common.length());
                if(!map.containsKey(normSuf)){
                    map.put(normSuf, new ArrayList<RuleItem>());
                }
                map.get(normSuf).add(new RuleItem(normSuf, wordForm[1].getForm(), wordForm[0].getForm()));
            }
            RuleItem[][] items = new RuleItem[map.size()][];
            int i = 0;
            for (List<RuleItem> item : map.values()) {
                items[i++] = item.toArray(new RuleItem[item.size()]);
            }
            Rule rule = new Rule(rawPref, rawSuf, items);
            String ruleId = Rule.serialize(rule);
            if(!sufs.containsKey(rawSuf)){
                sufs.put(rawSuf, new Struct());
            }
            Struct struct = sufs.get(rawSuf);
            if(!struct.rule2id.containsKey(ruleId)){
                struct.rule2id.put(ruleId, struct.rule2id.size());
                struct.rules.add(rule);
            }
            struct.prefcommons.put(rawPref + common, struct.rule2id.get(ruleId));
        }
        save(new File("src/main/resources/MorphStateMachine"), sufs);
    }
    
    
    private static void save(File file, Map<String, Struct> sufs) throws IOException {
        List<RuleMapping> list = new ArrayList<RuleMapping>();
        for (Map.Entry<String, Struct> entrySet : sufs.entrySet()) {
            list.add(new RuleMapping(entrySet.getKey(), entrySet.getValue().prefcommons, entrySet.getValue().rules.toArray(new Rule[entrySet.getValue().rules.size()])));
        }
        FileUtils.writeLines(file, "utf-8", list);
    }
    
    private static void fill(List<Lemma> lemmas, Set<String> suffixes, Map<String, List<WordForm[]>> raw2WordForms){
        for (Lemma lemma : lemmas) {
            WordForm norm = lemma.getNorm();
            for (WordForm raw : lemma.getItems()) {
                
                if(!raw2WordForms.containsKey(raw.getWord())){
                    raw2WordForms.put(raw.getWord(), new ArrayList<WordForm[]>());
                }
                raw2WordForms.get(raw.getWord()).add(new WordForm[]{raw, norm});
                
                int[] com = getCommon(norm.getWord(), raw.getWord());
                for (int j = com[1]; j < raw.getWord().length(); j++) {
                    suffixes.add(raw.getWord().substring(j));
                }
            }
        }
    }
    
    private static int[] getCommon(Set<String> suffixes, String raw, List<WordForm[]> wordForms){
        int[] index = new int[]{0, raw.length()};
        for (WordForm[] record : wordForms) {
            int[] com = getCommon(record[1].getWord(), raw);
            index[0] = Math.max(com[0], index[0]);
            index[1] = Math.min(com[1], index[1]);
        }
        for (int i = 0; i < index[1]; i++) {
            if(suffixes.contains(raw.substring(i))){
                index[1] = i;
                break;
            }
        }
        return index;
    }
    
    
    private static int[] getCommon(String norm, String form){
        for (int i = 0; i < norm.length() - 2; i++) {
            String common = norm.substring(0, norm.length() - i);
            if(form.contains(common)){
                int index = form.indexOf(common);
                return new int[]{index, index + common.length()};
            }
        }
        return new int[]{0, 0};
    }
    
}
