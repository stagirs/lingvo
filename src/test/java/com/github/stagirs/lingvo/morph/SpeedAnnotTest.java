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
package com.github.stagirs.lingvo.morph;

import com.github.stagirs.lingvo.build.XmlParser;
import com.github.stagirs.lingvo.build.model.Annotation;
import com.github.stagirs.lingvo.build.model.Annotation.Item;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.tartarus.snowball.ext.RussianStemmer;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SpeedAnnotTest {
    List<String> words = new ArrayList<String>();
    @Before
    public void init() throws IOException, ClassNotFoundException{
        Class.forName("com.github.stagirs.lingvo.morph.MorphStateMachine");
        for(Annotation annotation : XmlParser.getAnnotations(new File("annot.opcorpora.no_ambig.xml.zip"))){
            for(Item item : annotation.getItems()){
                String word = annotation.getWord(item).toLowerCase();
                if(MorphAnalyzer.get(word) != null){
                    words.add(word);
                }
            }
        }   
    }
    
    @Test
    public void stagirsTest() throws IOException{
        long time = System.currentTimeMillis();
        int i = 0;
        long hash = 0;
        for (int j = 0; j < 100; j++) {
            for (String word : words) {
                i++;
                hash += MorphAnalyzer.get(word).getRaw().length();
            }
        }
        System.out.println("Stagirs: " + (System.currentTimeMillis() - time) + " ms " + i + " " + hash);
    }
    
    @Test
    public void stemmerTest() throws IOException{
        RussianStemmer rs = new RussianStemmer();
        long time = System.currentTimeMillis();
        int i = 0;
        long hash = 0;
        for (int j = 0; j < 100; j++) {
            for (String word : words) {
                i++;
                rs.setCurrent(word);
                rs.stem();
                hash += rs.getCurrent().length();
            } 
        }
        System.out.println("Stemmer: " + (System.currentTimeMillis() - time) + " ms " + i + " " + hash);
    }
    
    @Test
    public void stagirsRandomTest() throws IOException{
        long time = System.currentTimeMillis();
        long hash = 0;
        int itertions = 10000000;
        for (int j = 0; j < itertions; j++) {
            int i = (int)(Math.random() * words.size());
            hash += MorphAnalyzer.get(words.get(i)).getRaw().length();
        }
        System.out.println("Random Stagirs: " + (System.currentTimeMillis() - time) + " ms " + 10000000 + " " + hash);
    }
    
    @Test
    public void stemmerRandomTest() throws IOException{
        RussianStemmer rs = new RussianStemmer();
        long time = System.currentTimeMillis();
        long hash = 0;
        int itertions = 10000000;
        for (int j = 0; j < itertions; j++) {
            int i = (int)(Math.random() * words.size());
            rs.setCurrent(words.get(i));
            rs.stem();
            hash += rs.getCurrent().length();
        }
        System.out.println("Random Stemmer: " + (System.currentTimeMillis() - time) + " ms " + 10000000 + " " + hash);
    }
}
