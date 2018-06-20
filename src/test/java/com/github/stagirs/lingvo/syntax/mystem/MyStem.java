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
package com.github.stagirs.lingvo.syntax.mystem;

import com.github.stagirs.lingvo.syntax.model.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MyStem {
    
    public static class MyStemSyntaxItem extends SyntaxItem{
        String name;
        String type;

        public MyStemSyntaxItem(String name, String type) {
            super(0, 0);
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public double getScore() {
            return 0;
        }
    }
    
    public static List<SyntaxItem[][]> process(List<String> text) throws IOException, InterruptedException{
        FileUtils.writeLines(new File("mystem_input"), "utf-8", text);
        Process process = Runtime.getRuntime().exec(new String[]{"./mystem", "-c", "-i", "-d", "mystem_input", "mystem_output"});
        process.waitFor();
        InputStream is = process.getInputStream();
        try{
            List<String> lines = FileUtils.readLines(new File("mystem_output"), "utf-8");
            List<SyntaxItem[][]> resultList = new ArrayList<SyntaxItem[][]>();
            for (String line : lines) {
                String[] parts = line.split(" ");
                SyntaxItem[][] result = new SyntaxItem[parts.length][];
                for (int i = 0; i < result.length; i++) {
                    if(parts[i].isEmpty()){
                        result[i] = new SyntaxItem[0];
                        continue;
                    }
                    if(!parts[i].contains("{")){
                        result[i] = new SyntaxItem[]{new MyStemSyntaxItem(parts[i], "")};
                        continue;
                    }
                    final String word = parts[i].substring(0, parts[i].indexOf('{'));
                    final String[] forms = parts[i].substring(parts[i].indexOf('{') + 1, parts[i].indexOf('}')).split("\\|");
                    result[i] = new SyntaxItem[forms.length];
                    for (int j = 0; j < result[i].length; j++) {
                        result[i][j] = new MyStemSyntaxItem(word, forms[j]);
                    }
                }
                resultList.add(result);
            }
            return resultList;
        }finally{
            is.close();
        }
    }
    
    
}
