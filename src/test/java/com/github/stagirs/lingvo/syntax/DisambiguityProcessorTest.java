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
import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.syntax.model.*;
import com.github.stagirs.lingvo.syntax.model.items.AmbigSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.WordSyntaxItem;
import java.io.File;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Dmitriy Malakhov
 */
public class DisambiguityProcessorTest {
    @Test
    public void test() throws Exception{
        Class.forName("com.github.stagirs.lingvo.morph.MorphStateMachine");
        int good = 0;
        int bad = 0;
        int goodNorm = 0;
        long time = 0;
        for(Annotation annotation : XmlParser.getAnnotations(new File("annot.opcorpora.no_ambig.xml.zip"))){
            time -= System.currentTimeMillis();
            Sentence sentence = new Sentence(0, annotation.getText(), SyntaxItemExtractor.extract(annotation.getText()));
            DisambiguityProcessor.process(sentence);
            time += System.currentTimeMillis();
            
            for(SyntaxItem syntaxItem : sentence.getSyntaxItem()){
                if(!(syntaxItem instanceof AmbigSyntaxItem)){
                    continue;
                }
                for (Item item : annotation.getItems()) {
                    if(syntaxItem.getIndexFrom() != item.getIndexFrom()){
                        continue;
                    }
                    String word = annotation.getWord(item).toLowerCase();
                    String type = SyntaxStatistics.getWordType(word, new Form(item.getId()));
                    if(syntaxItem.getType().equals(type)){
                        good++;
                    }else{
                        AmbigSyntaxItem ambigSyntaxItem = (AmbigSyntaxItem) syntaxItem;
                        String norm = null;
                        for (WordSyntaxItem wordItem : ambigSyntaxItem.getSyntaxItems()) {
                            if(wordItem.getType().equals(type)){
                                norm = wordItem.getNormTerm();
                                break;
                            }
                        }
                        if(norm == null || norm.equals(ambigSyntaxItem.getSyntaxItems().get(0).getNormTerm())){
                            goodNorm++;
                        }
                        bad++;
                    }
                    break;
                }
            }
        }
        System.out.println(
                        "Stagirs Lingvo Время обработки: " + time + "; "
                        + "Правильно:" + good + "; "
                        + "Неправильно:" + bad + "; "
                        + "Правильная нормальная форма:" + goodNorm + "; "
                        + "Процент правильных:" + String.format("%.2f" ,good * 100.0 / (good + bad)) + "%; "
                        + "Процент правильных нормальных форм:" + String.format("%.2f", (good + goodNorm) * 100.0 / (good + bad)) + "%");
    }
    
    @Test
    public void testSentences(){
        {
            Sentence sentence = new Sentence(0, "Волков взял ружье и выстрелил в сову", SyntaxItemExtractor.extract("Волков взял ружье и выстрелил в сову"));
            DisambiguityProcessor.process(sentence);
            List<SyntaxItem> items = sentence.getSyntaxItem();
            assertEquals(items.get(0).getType(), "NOUN,masc,sing,nomn,Surn");
            assertEquals(items.get(2).getType(), "NOUN,neut,sing,accs");
            assertEquals(items.get(3).getType(), "CONJ,и");
            assertEquals(items.get(4).getType(), "VERB,masc,sing,past,indc");
        }
    }
}
