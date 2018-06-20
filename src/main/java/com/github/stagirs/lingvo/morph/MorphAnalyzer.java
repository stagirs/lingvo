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

import com.github.stagirs.lingvo.morph.model.Morph;
import com.github.stagirs.lingvo.morph.model.Rule;
import com.github.stagirs.lingvo.morph.model.RuleMapping;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MorphAnalyzer {
    
    public static Morph get(String word){
        RuleMapping mapping = MorphStateMachine.getRuleMapping(word);
        if(mapping == null){
            return null;
        }
        Rule rule = mapping.getRule(word.substring(0, word.length() - mapping.getSuf().length()));
        if(rule != null){
            return new Morph(word, rule);
        }
        return null;
    }
}
