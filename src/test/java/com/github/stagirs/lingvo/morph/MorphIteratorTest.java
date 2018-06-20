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
import com.github.stagirs.lingvo.build.model.Lemma;
import com.github.stagirs.lingvo.model.WordForm;
import com.github.stagirs.lingvo.morph.model.Morph;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Проверяем, правильно ли работает на словах из словаря
 * @author Dmitriy Malakhov
 */
public class MorphIteratorTest {
    Map<String, Set<String>> word2norms = new HashMap<String, Set<String>>();
    
    @Before
    public void before() throws IOException{
        for(Lemma lemma : XmlParser.getLemmas(new File("dict.opcorpora.xml.zip"))){
            for (WordForm wordForm : lemma.getItems()) {
                if(!word2norms.containsKey(wordForm.getWord())){
                    word2norms.put(wordForm.getWord(), new HashSet<String>());
                }
                word2norms.get(wordForm.getWord()).add(lemma.getNorm().getWord());
            }
        }    
    }
    
    @Test
    public void findTest() throws IOException{
        MorphIterator iterator = new MorphIterator();
        int count = 0;
        while(iterator.hasNext()){
            Morph m = iterator.next();  
            Morph morpho = MorphAnalyzer.get(m.getRaw());
            if(morpho == null){
                throw new RuntimeException(m.getRaw());
            }
            if(morpho.getNormCount() != m.getNormCount() || word2norms.get(m.getRaw()).size() != m.getNormCount()){
                throw new RuntimeException(m.getRaw());
            }
            count++;
        }
        for (String word : word2norms.keySet()) {
            Morph morpho = MorphAnalyzer.get(word);
            if(morpho == null){
                throw new RuntimeException(word);
            }
            if(morpho.getNormCount() != word2norms.get(word).size()){
                throw new RuntimeException(word);
            }
        }
        assertEquals(count, word2norms.size());
    }
}
