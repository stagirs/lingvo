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
import com.github.stagirs.lingvo.morph.MorphAnalyzer;
import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.morph.MorphStateMachine;
import com.github.stagirs.lingvo.morph.model.Morph;
import com.github.stagirs.lingvo.morph.model.Rule;
import com.github.stagirs.lingvo.morph.model.RuleItem;
import com.github.stagirs.lingvo.morph.model.RuleMapping;
import com.github.stagirs.lingvo.syntax.model.SyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.AmbigSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.NumberSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.PunctMarkSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.UnknownSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.WordSyntaxItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SyntaxItemExtractor {
    public static List<SyntaxItem> extract(String text){
        int i = 0;
        List<SyntaxItem> items = new ArrayList<SyntaxItem>();
        while (i < text.length()) {
            if('0' <= text.charAt(i) && text.charAt(i) <= '9'){
                i = processNumber(items, text, i);
                continue;
            }
            if(isRussian(text.charAt(i))){
                int cur = processWord(items, text, i);
                if(cur == i){
                    i = processUnknown(items, text, i);
                }else{
                    i = cur;
                }
                continue;
            }
            switch(text.charAt(i)){
                case ' ': case '\t': case '\r': case '\n': i++; break;
                case '.': {
                    if(i + 2 < text.length() && text.charAt(i + 1) == '.' && text.charAt(i + 2) == '.'){
                        items.add(new PunctMarkSyntaxItem("...", i, i + 3)); 
                        i += 3;
                    }else if(i + 1 < text.length() && text.charAt(i + 1) == '.'){
                        items.add(new PunctMarkSyntaxItem("..", i, i + 2)); 
                        i += 2;
                    }else{
                        items.add(new PunctMarkSyntaxItem(".", i, i + 1)); 
                        i++;
                    }
                    break;
                }
                case '?': items.add(new PunctMarkSyntaxItem("?", i, i + 1)); i++; break;
                case '!': items.add(new PunctMarkSyntaxItem("!", i, i + 1)); i++; break;
                case '…': items.add(new PunctMarkSyntaxItem("...", i, i + 1)); i++; break;
                case ',': items.add(new PunctMarkSyntaxItem(",", i, i + 1)); i++; break;
                case ';': items.add(new PunctMarkSyntaxItem(";", i, i + 1)); i++; break;
                case ':': items.add(new PunctMarkSyntaxItem(":", i, i + 1)); i++; break;
                case '-': case '—': case '‐': case '−': case '–': items.add(new PunctMarkSyntaxItem("-", i, i + 1)); i++; break;
                case '"': case '\'': case '«': case '»': case '„': case '“': case '`': items.add(new PunctMarkSyntaxItem("'", i, i + 1)); i++; break;
                case '/': items.add(new PunctMarkSyntaxItem("/", i, i + 1)); i++; break;
                case '|': items.add(new PunctMarkSyntaxItem("/", i, i + 1)); i++; break;
                case '&': items.add(new PunctMarkSyntaxItem("&", i, i + 1)); i++; break;
                case '+': items.add(new PunctMarkSyntaxItem("+", i, i + 1)); i++; break;
                case '=': items.add(new PunctMarkSyntaxItem("=", i, i + 1)); i++; break;
                case '#': items.add(new PunctMarkSyntaxItem("#", i, i + 1)); i++; break;
                case '%': items.add(new PunctMarkSyntaxItem("%", i, i + 1)); i++; break;
                case '№': items.add(new PunctMarkSyntaxItem("№", i, i + 1)); i++; break;
                case '\\': items.add(new PunctMarkSyntaxItem("/", i, i + 1)); i++; break;
                case '[': items.add(new PunctMarkSyntaxItem("[", i, i + 1)); i++; break;
                case ']': items.add(new PunctMarkSyntaxItem("]", i, i + 1)); i++; break;
                case '{': items.add(new PunctMarkSyntaxItem("{", i, i + 1)); i++; break;
                case '}': items.add(new PunctMarkSyntaxItem("}", i, i + 1)); i++; break;
                case '(': items.add(new PunctMarkSyntaxItem("(", i, i + 1)); i++; break;
                case ')': items.add(new PunctMarkSyntaxItem(")", i, i + 1)); i++; break;
                case '<': items.add(new PunctMarkSyntaxItem("<", i, i + 1)); i++; break;
                case '>': items.add(new PunctMarkSyntaxItem(">", i, i + 1)); i++; break;
                default: i = processUnknown(items, text, i);
            }
        }
        return items;
    }
    
    private static int processNumber(List<SyntaxItem> items, String text, int i){
        StringBuilder sb = new StringBuilder();
        int j = i;
        for (; j < text.length(); j++) {
            if((text.charAt(j) < '0' || '9' < text.charAt(j)) && text.charAt(j) != '.' && text.charAt(j) != ','){
                break;
            }
            sb.append(text.charAt(j));
        }
        for (; j < text.length(); j++) {
            if(!isRussian(text.charAt(j)) && '-' != text.charAt(j)){
                break;
            }
            sb.append(text.charAt(j));
        }
        items.add(new NumberSyntaxItem(sb.toString(), i, j));
        return j;
    }
    
    private static int processWord(List<SyntaxItem> items, String text, int i){
        StringBuilder sb = new StringBuilder();
        int j = i;
        for (; j < text.length(); j++) {
            if(sb.length() == 1 && text.charAt(j) == '.'){
                sb.append(text.charAt(j));
                items.add(new WordSyntaxItem(sb.toString(), sb.toString(), new Form(), sb.toString(), new Form(), true, i, j + 1));
                return j + 1;
            }
            if(!isRussian(text.charAt(j)) && (text.charAt(j) != '-' || text.length() <= j + 1 || !isRussian(text.charAt(j + 1)))){
                break;
            }
            sb.append(text.charAt(j));
        }
        String word = sb.toString();
                
        Morph morpho = MorphAnalyzer.get(word.toLowerCase());
        if(morpho == null){
            RuleMapping ruleMapping = MorphStateMachine.getRuleMapping(word.toLowerCase());
            if(ruleMapping == null){
                items.add(new WordSyntaxItem(word, word.toLowerCase(), new Form(), word.toLowerCase(), new Form(), false, i, j));
            }else{
                List<WordSyntaxItem> syntaxItems = new ArrayList<WordSyntaxItem>();
                for(Rule rule : ruleMapping.getRules()){
                    if(!word.startsWith(rule.getPref()) || word.length() < rule.getPref().length() + rule.getSuf().length()){
                        continue;
                    }
                    String common = word.toLowerCase().substring(rule.getPref().length(), word.length() - rule.getSuf().length());
                    for (RuleItem[] item : rule.getItems()) {
                        for (RuleItem ruleItem : item) {
                            syntaxItems.add(new WordSyntaxItem(word, word.toLowerCase(), ruleItem.getRaw(), common + ruleItem.getSuf(), ruleItem.getNorm(), false, i, j));
                        }
                    }
                }
                items.add(new AmbigSyntaxItem(syntaxItems, false, i, j));
            }
            return j;
        }
        if(morpho.getNormCount() == 1 && morpho.getRawForms(0).length == 1){
            items.add(new WordSyntaxItem(sb.toString(), morpho.getRaw(), morpho.getRawForm(), morpho.getNorm(), morpho.getNormForm(), true, i, j));
        }else{
            items.add(new AmbigSyntaxItem(getWordSyntaxItems(sb.toString(), morpho, i, j), true, i, j));
        }
        return j;
    }
    
    private static int processUnknown(List<SyntaxItem> items, String text, int i){
        StringBuilder sb = new StringBuilder();
        int j = i;
        for (; j < text.length(); j++) {
            if(text.charAt(j) == ' ' || text.charAt(j) == '\t' || text.charAt(j) == '\r' || text.charAt(j) == '\n'){
                break;
            }
            sb.append(text.charAt(j));    
        }
        items.add(new UnknownSyntaxItem(sb.toString(), i , j));
        return j;
    }
    
    private static List<WordSyntaxItem> getWordSyntaxItems(String name, Morph morpho, int indexFrom, int indexTo){
        List<WordSyntaxItem> syntaxItems = new ArrayList<WordSyntaxItem>();
        for (int i = 0; i < morpho.getNormCount(); i++) {
            Form[] rawForm = morpho.getRawForms(i);
            Form[] normForm = morpho.getNormForms(i);
            for (int j = 0; j < normForm.length; j++) {
                syntaxItems.add(new WordSyntaxItem(name, morpho.getRaw(), rawForm[j], morpho.getNorm(i), normForm[j], true, indexFrom, indexTo));
            }
        }
        return syntaxItems;
    }
}
