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

import com.github.stagirs.lingvo.syntax.model.*;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SentenceIterator implements Iterator<List<Sentence>>{
    Iterator<String> points;

    public SentenceIterator(Iterator<String> points) {
        this.points = points;
    }

    @Override
    public boolean hasNext() {
        return points.hasNext();
    }

    @Override
    public List<Sentence> next() {
        return SentenceExtractor.extract(points.next());
    }
    
}
