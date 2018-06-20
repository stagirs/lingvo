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

import com.github.stagirs.lingvo.morph.MorphStateMachine.State;
import com.github.stagirs.lingvo.morph.model.Morph;
import com.github.stagirs.lingvo.morph.model.Rule;
import com.github.stagirs.lingvo.morph.model.RuleItem;
import com.github.stagirs.lingvo.morph.model.RuleMapping;
import gnu.trove.map.hash.TObjectIntHashMap;
import static java.lang.Byte.parseByte;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.language.DaitchMokotoffSoundex;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MorphPredictor {
    private static final Map<String, String> letters = new HashMap<String, String>();
    static {
        letters.put("А", "A");
        letters.put("Б", "B");
        letters.put("В", "V");
        letters.put("Г", "G");
        letters.put("Д", "D");
        letters.put("Е", "E");
        letters.put("Ё", "E");
        letters.put("Ж", "ZH");
        letters.put("З", "Z");
        letters.put("И", "I");
        letters.put("Й", "I");
        letters.put("К", "K");
        letters.put("Л", "L");
        letters.put("М", "M");
        letters.put("Н", "N");
        letters.put("О", "O");
        letters.put("П", "P");
        letters.put("Р", "R");
        letters.put("С", "S");
        letters.put("Т", "T");
        letters.put("У", "U");
        letters.put("Ф", "F");
        letters.put("Х", "H");
        letters.put("Ц", "C");
        letters.put("Ч", "CH");
        letters.put("Ш", "SH");
        letters.put("Щ", "SH");
        letters.put("Ъ", "'");
        letters.put("Ы", "Y");
        letters.put("Ъ", "'");
        letters.put("Э", "E");
        letters.put("Ю", "U");
        letters.put("Я", "YA");
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "e");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "i");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "h");
        letters.put("ц", "c");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "sh");
        letters.put("ъ", "'");
        letters.put("ы", "y");
        letters.put("ъ", "'");
        letters.put("э", "e");
        letters.put("ю", "u");
        letters.put("я", "ya");
    }
    
    
    public static Morph get(String word){
        RuleMapping mapping = MorphStateMachine.getRuleMapping(word);
        if(mapping == null){
            return null;
        }
        TObjectIntHashMap<String> form2count = getForm2count(mapping);
        String curPrefcommon = word.substring(0, word.length() - mapping.getSuf().length());
        Morph morph = null;
        int minDistanceLev = Integer.MAX_VALUE;
        int minDistanceLength = curPrefcommon.length();
        int minDistanceSoundex = Integer.MAX_VALUE;
        DaitchMokotoffSoundex soundex = new DaitchMokotoffSoundex();
        byte[] encoded = encode(soundex, curPrefcommon);
        for(String prefcommon : mapping.getPrefcommons()){
            Rule rule = mapping.getRule(prefcommon);
            if(!curPrefcommon.startsWith(rule.getPref())){
                continue;
            }
            int distanceLev = StringUtils.getLevenshteinDistance(curPrefcommon, prefcommon);
            if(minDistanceLev < distanceLev){
                continue;
            }
            int distanceLength = Math.abs(prefcommon.length() - curPrefcommon.length());
            if(minDistanceLev == distanceLev && minDistanceLength < distanceLength){
                continue;
            }
            int distanceSoundex = getDistance(soundex, prefcommon, encoded);
            if(minDistanceLev == distanceLev && minDistanceLength == distanceLength && minDistanceSoundex < distanceSoundex){
                continue;
            }
            RuleItem maxRuleItem = getMaxRuleItem(form2count, rule);
            if(morph != null && 
                    minDistanceLev == distanceLev && 
                    minDistanceLength == distanceLength && 
                    minDistanceSoundex == distanceSoundex && 
                    form2count.get(morph.getRawForm().toString()) <= form2count.get(maxRuleItem.getRaw().toString())){
                continue;
            }
            morph = new Morph(word, new Rule(rule.getPref(), rule.getSuf(), new RuleItem[][]{new RuleItem[]{maxRuleItem}}));
            minDistanceLev = distanceLev;
            minDistanceLength = distanceLength;
            minDistanceSoundex = distanceSoundex;
        }
        if(morph == null){
            return null;
        }
        return morph;
    }
    
    
    private static TObjectIntHashMap<String> getForm2count(RuleMapping mapping){
        TObjectIntHashMap<String> form2count = new TObjectIntHashMap<String>();
        for(String prefcommon : mapping.getPrefcommons()){
            Rule rule = mapping.getRule(prefcommon);
            for (int i = 0; i < rule.getItems().length; i++) {
                for (int j = 0; j < rule.getItems()[i].length; j++) {
                    form2count.adjustOrPutValue(rule.getItems()[i][j].getRaw().toString(), 1, 1);
                }
            }
        }
        return form2count;
    }
    
    private static RuleItem getMaxRuleItem(TObjectIntHashMap<String> form2count, Rule rule){
        RuleItem maxRuleItem = rule.getItems()[0][0];
        String maxForm = maxRuleItem.getRaw().toString();
        for (int i = 0; i < rule.getItems().length; i++) {
            for (int j = 0; j < rule.getItems()[i].length; j++) {
                RuleItem ruleItem = rule.getItems()[i][j];
                String form = ruleItem.getRaw().toString();
                if(form2count.get(form) <= form2count.get(maxForm)){
                    continue;
                }
                maxRuleItem = ruleItem;
                maxForm = form;
            }
        }
        return maxRuleItem;
    }
    
    
    private static int getDistance(DaitchMokotoffSoundex soundex, String word, byte[] encoded){
        byte[] bytes = encode(soundex, word);
        int distance = 0;
        for (int i = 0; i < bytes.length && i < encoded.length; i++) {
            distance += Math.abs(encoded[i] - bytes[i]);
        }
        return distance + Math.abs(bytes.length - encoded.length);
    }
    
    private static byte[] encode(DaitchMokotoffSoundex soundex, String word){
        StringBuilder sb = new StringBuilder(word.length());
        for (int i = 0; i<word.length(); i++) {
            String l = word.substring(i, i+1);
            if (letters.containsKey(l)) {
                sb.append(letters.get(l));
            }
            else {
                sb.append(l);
            }
        }
        String encoded = soundex.encode(sb.toString());
        if(encoded.length() != 6){
            throw new RuntimeException("can't encode word " + word + ": " + sb.toString() + "; " + encoded + ";");
        }
        byte[] bytes = new byte[6];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (encoded.charAt(i) - '0');
        }
        return bytes;
    }
}
