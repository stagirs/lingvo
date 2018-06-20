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

import com.github.stagirs.lingvo.model.Attr;
import static com.github.stagirs.lingvo.model.Attr.*;
import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.morph.MorphStateMachine;
import com.github.stagirs.lingvo.syntax.model.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SyntaxStatistics {
    private static final EnumSet SIGNIFICANT = EnumSet.of(
            NOUN, PREP, CONJ,INFN,VERB,ADJF,ADJS,ADVB,COMP,PRTF,PRTS,GRND,NUMR,NPRO,PRED,PRCL,INTJ,Geox,Orgn,Trad,Name,Surn,Patr,Init,
            masc,femn,neut,
            sing,plur,
            nomn,gent,datv,accs,ablt,loct,voct,
            N1per,N2per,N3per,
            pres,past,futr,
            indc,impr);
    private static final Map<String, Integer> id2count = new HashMap<String, Integer>();
    static {
        try {
            URL url = MorphStateMachine.class.getClassLoader().getResource("SyntaxStatistics");
            InputStream in = url.openStream();
            try{
                for (String line : IOUtils.readLines(in, "utf-8")){
                    String[] parts = line.split("\t");
                    id2count.put(parts[0], NumberUtils.toInt(parts[1]));
                }
            }finally{
                in.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static int count(SyntaxItem item){
        return count(item == null ? "" : item.getType());
    }
    
    public static int count(SyntaxItem left, SyntaxItem right){
        return count((left == null ? "" : left.getType()) + " " + (right == null ? "" : right.getType()));
    }
    
    public static int count(String id){
        return !id2count.containsKey(id) ? 0 : id2count.get(id);
    }
    
    public static String getWordType(String word, Form form){
        List<Attr> res = new ArrayList<Attr>();
        for(Attr a : form.getAttrs()){
            if(!SIGNIFICANT.contains(a)){
                continue;
            }
            res.add(a);
        }
        if(res.isEmpty()){
            return word;
        }
        if(form.isStop()){
            return StringUtils.join(res, ",") + "," + word;
        }else{
            return StringUtils.join(res, ",");
        }
    }
}
