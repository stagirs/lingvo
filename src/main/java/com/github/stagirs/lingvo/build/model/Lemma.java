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
package com.github.stagirs.lingvo.build.model;

import com.github.stagirs.lingvo.model.WordForm;
import java.util.List;

/**
 *
 * @author Dmitriy Malakhov
 */
public class Lemma {
    List<WordForm> items;
   
    public Lemma(List<WordForm> items) {
        this.items = items;
    }
     
    public WordForm getNorm(){
        return items.get(0);
    }

    public List<WordForm> getItems() {
        return items;
    }
}
