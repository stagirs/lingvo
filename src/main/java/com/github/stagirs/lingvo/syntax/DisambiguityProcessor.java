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
import com.github.stagirs.lingvo.model.Attr;
import static com.github.stagirs.lingvo.model.Attr.Geox;
import static com.github.stagirs.lingvo.model.Attr.Name;
import static com.github.stagirs.lingvo.model.Attr.Orgn;
import static com.github.stagirs.lingvo.model.Attr.Patr;
import static com.github.stagirs.lingvo.model.Attr.Surn;
import static com.github.stagirs.lingvo.model.Attr.Trad;
import static com.github.stagirs.lingvo.syntax.SyntaxStatistics.count;
import com.github.stagirs.lingvo.syntax.model.*;
import com.github.stagirs.lingvo.syntax.model.items.AmbigSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.NumberSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.WordSyntaxItem;
import static java.lang.Math.E;
import static java.lang.Math.log;
import java.util.*;

/**
 *
 * @author Dmitriy Malakhov
 */
public class DisambiguityProcessor {
    private static final EnumSet UPPER_NAME_ATTRS = EnumSet.of(Geox, Orgn, Trad, Name, Surn, Patr);
    
    public static void process(Sentence sentence){   
        preProcess(sentence.getSyntaxItem());
        int i = 0;
        while (i < sentence.getSyntaxItem().size()) {
            if(!(sentence.getSyntaxItem().get(i) instanceof AmbigSyntaxItem)){
                i++;
                continue;
            }
            int posBegin = i;
            for (;i < sentence.getSyntaxItem().size(); i++){
                if(sentence.getSyntaxItem().get(i) instanceof WordSyntaxItem){
                    break;
                }
                if(sentence.getSyntaxItem().get(i) instanceof NumberSyntaxItem){
                    break;
                }
            }
            rank(sentence, posBegin, i - 1);
        }
    }
    
    private static void preProcess(List<SyntaxItem> items){
        for (int i = 0; i < items.size(); i++) {
            if(i != 0 && isRussian(items.get(i).getName().charAt(0)) && Character.isUpperCase(items.get(i).getName().charAt(0)) && items.get(i) instanceof AmbigSyntaxItem){
                AmbigSyntaxItem item = (AmbigSyntaxItem) items.get(i);
                List<WordSyntaxItem> newAmbigItems = new ArrayList<WordSyntaxItem>();
                for(WordSyntaxItem ambigItem : item.getSyntaxItems()){
                    for(Attr attr : ambigItem.getNormForm().getAttrs()){
                        if(UPPER_NAME_ATTRS.contains(attr)){
                            newAmbigItems.add(ambigItem);
                            break;
                        }
                    }
                }
                if(newAmbigItems.isEmpty()){
                    continue;
                }
                if(newAmbigItems.size() == 1){
                    items.set(i, newAmbigItems.get(0));
                }else{
                    items.set(i, new AmbigSyntaxItem(newAmbigItems, item.isExists(), item.getIndexFrom(), item.getIndexTo()));
                }
            }
        }
    }

    private static void rank(Sentence sentence, int posBegin, int posEnd) {
        SyntaxItem left = findLeft(sentence.getSyntaxItem(), posBegin - 1);
        SyntaxItem right = findRight(sentence.getSyntaxItem(), posEnd + 1);
        List<AmbigSyntaxItem> items = new ArrayList<AmbigSyntaxItem>();
        for (int i = posBegin; i <= posEnd; i++) {
            if(sentence.getSyntaxItem().get(i) instanceof AmbigSyntaxItem){
                items.add((AmbigSyntaxItem) sentence.getSyntaxItem().get(i));
            }
        }
        
        double leftSumScore = leftRank(left, items, true);
        double rightSumScore = rightRank(items, right, true);
        if(rightSumScore > leftSumScore){
            rightRank(items, right, true);
            leftRank(left, items, false);
        }else{
            leftRank(left, items, true);
            rightRank(items, right, false);
        }
        for (int i = 0; i < items.size(); i++) {
            SyntaxItem curLeft = i == 0 ? left : items.get(i - 1);
            SyntaxItem curRight = i == items.size() - 1 ? right : items.get(i + 1);
            double leftCount = count(curLeft);
            double rightCount = count(curRight);
            for (WordSyntaxItem mainWord : items.get(i).getSyntaxItems()) {
                setScore(mainWord, leftCount == 0 ? count(mainWord) : Math.min(leftCount, count(mainWord)), count(mainWord), count(curLeft, mainWord), true);
                setScore(mainWord, count(mainWord), count(mainWord), count(mainWord, curRight), false);
            }
            items.get(i).update();
        }
    }
    
    private static double leftRank(SyntaxItem left, List<AmbigSyntaxItem> items, boolean overwrite){
        double sumScore = 0;
        SyntaxItem cur = left;
        for (int i = 0; i < items.size(); i++) {
            double count = count(cur);
            for (WordSyntaxItem mainWord : items.get(i).getSyntaxItems()) {
                setScore(mainWord, count == 0 ? count(mainWord) : Math.min(count, count(mainWord)), count(mainWord), count(cur, mainWord), overwrite);
            }
            items.get(i).update();
            cur = items.get(i);
            sumScore += cur.getScore();
        }
        return sumScore;
    }
    
    private static double rightRank(List<AmbigSyntaxItem> items, SyntaxItem right, boolean overwrite){
        double sumScore = 0;
        SyntaxItem cur = right;
        for (int i = items.size() - 1; i >= 0; i--) {
            double count = count(cur);
            for (WordSyntaxItem mainWord : items.get(i).getSyntaxItems()) {
                //setScore(mainWord, count == 0 ? count(mainWord) : Math.min(count, count(mainWord)), count(mainWord), count(mainWord, cur), overwrite);
                //результат чуть лучше
                setScore(mainWord, count(mainWord), count(mainWord), count(mainWord, cur), overwrite);
            }
            items.get(i).update();
            cur = items.get(i);
            sumScore += cur.getScore();
        }
        return sumScore;
    }
    
    private static void setScore(WordSyntaxItem mainWord, double count, double wordCount, double pairCount, boolean overwrite){
        double score = wordCount == 0 ? 0 : log(E + wordCount) * pairCount / count;
        mainWord.setScore(overwrite ? score : mainWord.getScore() * score);
    }
    
    
    private static SyntaxItem findLeft(List<SyntaxItem> list, int pos){
        for (int i = pos; i >= 0; i--) {
            if(list.get(i) instanceof WordSyntaxItem){
                return list.get(i);
            }
            if(list.get(i) instanceof NumberSyntaxItem){
                return list.get(i);
            }
            if(list.get(i) instanceof AmbigSyntaxItem){
                return list.get(i);
            }
        }
        return null;
    }
    
    private static SyntaxItem findRight(List<SyntaxItem> list, int pos){
        for (int i = pos; i < list.size(); i++) {
            if(list.get(i) instanceof WordSyntaxItem){
                return list.get(i);
            }
            if(list.get(i) instanceof NumberSyntaxItem){
                return list.get(i);
            }
            if(list.get(i) instanceof AmbigSyntaxItem){
                return list.get(i);
            }
        }
        return null;
    }
}
