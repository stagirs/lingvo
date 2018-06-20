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

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

/**
 * https://yandex.ru/company/researches/2012/ya_orfo
 * @author Dmitriy Malakhov
 */
public class MorphPredictorTest {
    @Before
    public void init() throws ClassNotFoundException{
        Class.forName("com.github.stagirs.lingvo.morph.MorphStateMachine");
    }
    
    @Test
    public void getTest1() throws IOException{
        assertEquals(MorphPredictor.get("каровами").getNorm(), "карова");
    }
    
    @Test
    public void getTest2() throws IOException{
        assertEquals(MorphPredictor.get("однокласнику").getNorm(), "однокласник");
    }
    
    @Test
    public void getTest3() throws IOException{
        assertEquals(MorphPredictor.get("таеланде").getNorm(), "таеланд");
    }
    
    @Test
    public void getTest4() throws IOException{
        assertEquals(MorphPredictor.get("агенством").getNorm(), "агенство");
    }
    
    @Test
    public void getTest5() throws IOException{
        assertEquals(MorphPredictor.get("расчитали").getNorm(), "расчитал");
    }
    
    @Test
    public void getTest6() throws IOException{
        assertEquals(MorphPredictor.get("зделали").getNorm(), "зделал");
    }
    
    @Test
    public void getTest7() throws IOException{
        assertEquals(MorphPredictor.get("отзовами").getNorm(), "отзов");
    }
    
    @Test
    public void getTest8() throws IOException{
        assertEquals(MorphPredictor.get("програмой").getNorm(), "програма");
    }
    
    @Test
    public void getTest9() throws IOException{
        assertEquals(MorphPredictor.get("скачять").getNorm(), "скачять");
    }
    @Test
    public void getTest10() throws IOException{
        assertEquals(MorphPredictor.get("рассписанием").getNorm(), "рассписание");
    }
    
    @Test
    public void getTest11() throws IOException{
        assertEquals(MorphPredictor.get("росии").getNorm(), "росия");
    }
    
    @Test
    public void getTest12() throws IOException{
        assertEquals(MorphPredictor.get("скочать").getNorm(), "скочать");
    }
    
    @Test
    public void getTest13() throws IOException{
        assertEquals(MorphPredictor.get("рускому").getNorm(), "руский");
    }
    
    @Test
    public void getTest14() throws IOException{
        assertEquals(MorphPredictor.get("поликлинникой").getNorm(), "поликлинника");
    }
    
    @Test
    public void getTest15() throws IOException{
        assertEquals(MorphPredictor.get("русcификатором").getNorm(), "русcификатор");
    }
    
    @Test
    public void getTest16() throws IOException{
        assertEquals(MorphPredictor.get("офицальным").getNorm(), "офицальный");
    }
    
    @Test
    public void getTest17() throws IOException{
        assertEquals(MorphPredictor.get("Лу".toLowerCase()).getNorm(), "ла");
    }
}
