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
package com.github.stagirs.lingvo.syntax;

import static com.github.stagirs.lingvo.syntax.CharUtils.isRussian;
import com.github.stagirs.lingvo.syntax.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SentenceExtractor {
    public static List<Sentence> extract(String text){
        List<Sentence> sentences = new ArrayList<Sentence>();
        List<SyntaxItem> items = SyntaxItemExtractor.extract(text);
        if(items.isEmpty()){
            return sentences;
        }
        int sentenceBegin = 0;
        for (int i = 1; i < items.size() - 1; i++) {
            if(!items.get(i).getName().equals(".")){
                continue;
            }
            if(items.get(i - 1).getName().length() == 1 && isRussian(items.get(i - 1).getName().charAt(0))){
                //инициалы или сокращение
                continue;
            }
            if(!isRussian(items.get(i + 1).getName().charAt(0)) || !Character.isUpperCase(items.get(i + 1).getName().charAt(0))){
                //новое предложение должно идти с заглавной буквы русского алфавита
                continue;
            }
            while(items.get(i + 1).getName().equals(".")){
                i++;
            }
            sentences.add(getSentence(text, items.subList(sentenceBegin, i + 1)));
            sentenceBegin = i + 1;
        }
        sentences.add(getSentence(text, items.subList(sentenceBegin, items.size())));
        return sentences;
    }
    
    private static Sentence getSentence(String text, List<SyntaxItem> sentenceItems){
        int from = sentenceItems.get(0).getIndexFrom();
        int to = sentenceItems.get(sentenceItems.size() - 1).getIndexTo();
        for (SyntaxItem sentenceItem : sentenceItems) {
            sentenceItem.setIndexFrom(sentenceItem.getIndexFrom() - from);
            sentenceItem.setIndexTo(sentenceItem.getIndexTo() - from);
        }
        return new Sentence(from, text.substring(from, to), sentenceItems);
    }
}
