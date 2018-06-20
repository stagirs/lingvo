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
package com.github.stagirs.lingvo.build;

import com.github.stagirs.lingvo.build.model.Annotation;
import com.github.stagirs.lingvo.build.model.Annotation.Item;
import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.syntax.SyntaxItemExtractor;
import com.github.stagirs.lingvo.syntax.SyntaxStatistics;
import com.github.stagirs.lingvo.syntax.model.SyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.AmbigSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.NumberSyntaxItem;
import com.github.stagirs.lingvo.syntax.model.items.WordSyntaxItem;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class SyntaxStatisticsBuilder {
    public static void main(String[] args) throws Exception {
        TObjectIntHashMap<String> map = new TObjectIntHashMap<String>();
        for(Annotation annotation : XmlParser.getAnnotations(new File("annot.opcorpora.no_ambig.xml.zip"))){
            List<String> types = types(annotation);
            if(types.isEmpty()){
                continue;
            }
            map.adjustOrPutValue(types.get(0), 1, 1);
            map.adjustOrPutValue(" " + types.get(0), 1, 1);
            for (int i = 1; i < types.size(); i++) {
                map.adjustOrPutValue(types.get(i), 1, 1);
                map.adjustOrPutValue(types.get(i - 1) + " " + types.get(i), 1, 1);
            }
            map.adjustOrPutValue(types.get(types.size() - 1) + " ", 1, 1);
        }
        final List<String> result = new ArrayList<String>();
        map.forEachEntry(new TObjectIntProcedure<String>(){
            @Override
            public boolean execute(String key, int count) {
                result.add(key + "\t" + count);
                return true;
            }
        });
        FileUtils.writeLines(new File("src/main/resources/SyntaxStatistics"), "utf-8", result);
    }
    
    private static List<String> types(Annotation annotation){
        List<String> types = new ArrayList<String>();
        for(SyntaxItem syntaxItem : SyntaxItemExtractor.extract(annotation.getText())){
            if(syntaxItem instanceof NumberSyntaxItem || syntaxItem instanceof WordSyntaxItem){
                types.add(syntaxItem.getType());
                continue;
            }
            if(!(syntaxItem instanceof AmbigSyntaxItem)){
                continue;
            }
            for (Item item : annotation.getItems()) {
                if(syntaxItem.getIndexFrom() != item.getIndexFrom()){
                    continue;
                }
                types.add(SyntaxStatistics.getWordType(syntaxItem.getName().toLowerCase(), new Form(item.getId())));
                break;
            }
        }
        return types;
    }
}
