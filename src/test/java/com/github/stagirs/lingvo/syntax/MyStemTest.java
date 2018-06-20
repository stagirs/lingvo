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
package com.github.stagirs.lingvo.syntax;

import com.github.stagirs.lingvo.build.XmlParser;
import com.github.stagirs.lingvo.build.model.Annotation;
import com.github.stagirs.lingvo.build.model.Annotation.Item;
import com.github.stagirs.lingvo.morph.MorphAnalyzer;
import com.github.stagirs.lingvo.model.Attr;
import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.morph.model.Morph;
import com.github.stagirs.lingvo.syntax.mystem.MyStem;
import com.github.stagirs.lingvo.syntax.model.*;
import com.github.stagirs.lingvo.syntax.model.items.AmbigSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.NumberSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.WordSyntaxItem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MyStemTest {
    
    public static Map<Attr, List<String>> opencorpora2mystem = new HashMap<Attr, List<String>>();
    
    @Before
    public void before() throws IOException{
        for(String line : FileUtils.readLines(new File("mystemMapping"), "utf-8")){
            String[] parts = line.split(",");
            List<String> attrs = new ArrayList<String>();
            for (int i = 2; i < parts.length; i++) {
                if(parts[i].isEmpty()){
                    continue;
                }
                attrs.add(parts[i]);
            }
            opencorpora2mystem.put(Attr.valueOf(parts[0]), attrs);
        }
    }
    
    @Test
    public void test() throws Exception {
        Class.forName("com.github.stagirs.lingvo.morph.MorphStateMachine");
        List<String> sentences = new ArrayList<String>();
        List<Annotation> annotations = new ArrayList<Annotation>();
        for(Annotation annotation : XmlParser.getAnnotations(new File("annot.opcorpora.no_ambig.xml.zip"))){
            annotations.add(annotation);
            StringBuilder sb = new StringBuilder();
            for (SyntaxItem item : SyntaxItemExtractor.extract(annotation.getText())) {
                if(item instanceof AmbigSyntaxItem || item instanceof WordSyntaxItem || item instanceof NumberSyntaxItem){
                    sb.append(item.getName()).append(" ");
                }
            }
            sentences.add(sb.toString());
        }    
        long time = System.currentTimeMillis();
        List<SyntaxItem[][]> itemsList = MyStem.process(sentences);
        time = System.currentTimeMillis() - time;
        int good = 0;
        int bad = 0;
        int goodNorm = 0;
        for (int i = 0; i < itemsList.size(); i++) {
            Annotation annotation = annotations.get(i);
            SyntaxItem[][] items = itemsList.get(i);
            int j = 0;
            for (Item item : annotation.getItems()) {
                String word = annotation.getWord(item).toLowerCase();
                Morph morpho = MorphAnalyzer.get(word);
                if(morpho == null){
                    continue;
                }
                if(morpho.getNormCount() == 1 && morpho.getNormForms(0).length == 1){
                    continue;
                }
                while(j < items.length && items[j].length > 0 && !word.equals(items[j][0].getName().toLowerCase())){
                    j++;
                }
                if(j == items.length){
                    continue;
                }
                String type = SyntaxStatistics.getWordType(word, new Form(item.getId()));
                if(items[j].length == 1 && type.equals(getType(morpho, word, items[j][0].getType()))){
                    good++;
                }else{
                    for (SyntaxItem wordItem : items[j]) {
                        if(type.equals(getType(morpho, word, wordItem.getType()))){
                            goodNorm++;
                            break;
                        }
                    }
                    bad++;
                }
                j++;
            }
        }
        System.out.println(
                        "MyStem Время обработки: " + time + "; "
                        + "Правильно:" + good + "; "
                        + "Неправильно:" + bad + "; "
                        + "Правильная нормальная форма:" + goodNorm + "; "
                        + "Процент правильных:" + String.format("%.2f" ,good * 100.0 / (good + bad)) + "%; "
                        + "Процент правильных нормальных форм:" + String.format("%.2f", (good + goodNorm) * 100.0 / (good + bad)) + "%");
    }
    
    public static String getType(Morph morpho, String word, String form) throws IOException{
        Set<Attr> set = getAttrs(form);
        int maxCount = -1;
        Form maxForm = null;
        for (int i = 0; i < morpho.getNormCount(); i++) {
            for (Form rawForm : morpho.getRawForms(i)) {
                int count = 0;
                for (Attr attr : rawForm.getAttrs()) {
                    if(set.contains(attr)){
                        count++;
                    }
                }
                if(count > maxCount){
                    maxForm = rawForm;
                    maxCount = count;
                }
            }
        }
        if(maxCount == 0){
            maxForm.toString();
        }
        return SyntaxStatistics.getWordType(morpho.getRaw(), maxForm);
    }
    
    public static Set<Attr> getAttrs(String form){
        Set<Attr> result = new HashSet<Attr>();
        Set<String> formSet = new HashSet<String>(Arrays.asList(form.split(",|=")));
        for (Map.Entry<Attr, List<String>> entry : opencorpora2mystem.entrySet()) {
            if(formSet.containsAll(entry.getValue())){
                if(entry.getKey() == Attr.acc2){
                    continue;
                }
                result.add(entry.getKey());
            }
            if(formSet.contains("ADVPRO")){
                result.add(Attr.ADVB);
            }
        }
        return result;
    }
}
