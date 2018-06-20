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
package com.github.stagirs.lingvo.syntax;

import com.github.stagirs.lingvo.syntax.model.*;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SyntaxItemExtractorTest {
    
    @Test
    public void test1(){
        List<SyntaxItem> items = SyntaxItemExtractor.extract("Или участница «О.С.П.-студии» Татьяна Лазарева.");
        int i = 0;
        assertEquals(items.get(i++).getName(), "Или");
        assertEquals(items.get(i++).getName(), "участница");
        assertEquals(items.get(i++).getName(), "'");
        assertEquals(items.get(i++).getName(), "О.");
        assertEquals(items.get(i++).getName(), "С.");
        assertEquals(items.get(i++).getName(), "П.");
        assertEquals(items.get(i++).getName(), "-");
        assertEquals(items.get(i++).getName(), "студии");
        assertEquals(items.get(i++).getName(), "'");
        assertEquals(items.get(i++).getName(), "Татьяна");
        assertEquals(items.get(i++).getName(), "Лазарева");
        assertEquals(items.get(i++).getName(), ".");
    }
}
