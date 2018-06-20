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

import com.github.stagirs.lingvo.morph.model.RuleMapping;
import com.github.stagirs.lingvo.morph.model.Rule;
import com.github.stagirs.lingvo.morph.model.RuleItem;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MorphStateMachine {
    public static class State{
        RuleMapping mapping;
        State[] states = new State[34];
    }
    
    private static final State state = new State();
    static {
        try {
            URL url = MorphStateMachine.class.getClassLoader().getResource("MorphStateMachine");
            InputStream in = url.openStream();
            try{
                for (String line : IOUtils.readLines(in, "utf-8")){
                    RuleMapping mapping = RuleMapping.parse(line);
                    for (Rule rule : mapping.getRules()) {
                        for (RuleItem[] ris : rule.getItems()) {
                            /*
                             * Если RuleItem относится к стоп-слову, то вероятность его использования выше, и его стоит выдвинуть вперед
                             * TODO можно также использовать в сортировке встречаемость слов 
                             * (это позволит правильно угадывать нормальную форму в большинстве случаев, без особого снятия омонимии с помощью DisambiguityProcessor)
                             */
                            Arrays.sort(ris, new Comparator<RuleItem>() {
                                @Override
                                public int compare(RuleItem o1, RuleItem o2) {
                                    if(o1.getNorm().isStop() == o2.getNorm().isStop()){
                                        return 0;
                                    }else{
                                        return o1.getNorm().isStop() ? -1 : 1;
                                    }
                                }
                            });
                        }
                    }
                    add(state, mapping.getSuf(), mapping);
                }
            }finally{
                in.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static State begin() {
        return state;
    }
    
    public static State getState(State state, char c){
        return state.states[char2byte(c)];
    }

    public static State[] getStates(State state) {
        return state.states;
    }
    
    private static void add(State state, String suf, RuleMapping mapping){
        if(suf.isEmpty()){
            state.mapping = mapping;
            return;
        }
        int charCode = char2byte(suf.charAt(suf.length() - 1));
        if(state.states[charCode] == null){
            state.states[charCode] = new State();
        }
        add(state.states[charCode], suf.substring(0, suf.length() - 1), mapping);
    }
    
    public static RuleMapping getRuleMapping(String word){
        State state = MorphStateMachine.begin();
        int finish = word.length() - 1;
        for (; finish >= 0; finish--) {
            State curState = MorphStateMachine.getState(state, word.charAt(finish));
            if(curState == null){
                break;
            }
            state = curState;
        }
        return state.mapping;
    }
    
    private static int char2byte(char c){
        if('а' <= c && c <= 'я'){
            return (c - 'а' + 2);
        }
        if(c == 'ё'){
            return ('е' - 'а' + 2);
        }
        if('А' <= c && c <= 'Я'){
            return (c - 'А' + 2);
        }
        if(c == 'Ё'){
            return ('Е' - 'А' + 2);
        }
        return 1;
    }
}
