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
import com.github.stagirs.lingvo.build.model.Lemma;
import com.github.stagirs.lingvo.model.WordForm;
import com.github.stagirs.lingvo.model.Attr;
import static com.github.stagirs.lingvo.model.Attr.ms_f;
import com.github.stagirs.lingvo.model.Form;
import com.github.stagirs.lingvo.model.Type;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Dmitriy Malakhov
 */
public class XmlParser {
    
    public static List<Lemma> getLemmas(File file) throws IOException{
        List<String> dictLines = extract(file);
        Pattern lemmaPattern = Pattern.compile("<lemma id=\"(.*?)\".*?/lemma>", Pattern.MULTILINE|Pattern.DOTALL);
        Pattern normPattern = Pattern.compile("<l.*?t=\"(.*?)\">(.*?)</l>", Pattern.MULTILINE|Pattern.DOTALL);
        Pattern rawPattern = Pattern.compile("<f.*?t=\"(.*?)\">(.*?)</f>", Pattern.MULTILINE|Pattern.DOTALL);
        Pattern gPattern = Pattern.compile("<g.*?v=\"(.*?)\".*?/>", Pattern.MULTILINE|Pattern.DOTALL);
        List<Lemma> lemmas = new ArrayList<Lemma>();
        for (String dictLine : dictLines) {
            Matcher lemmaMatcher = lemmaPattern.matcher(dictLine);
            if(!lemmaMatcher.find()){
                continue;
            }
            String lemma = lemmaMatcher.group(0);
            Matcher normMatcher = normPattern.matcher(lemma);
            if(!normMatcher.find()){
                continue;
            }
            List<Attr> mainAttr = fillAttributes(new ArrayList<Attr>(), gPattern.matcher(normMatcher.group(2)));
            if(mainAttr.contains(Attr.Init)){
                continue;
            }
            Matcher rawMatcher = rawPattern.matcher(lemma);
            List<WordForm> items = new ArrayList<WordForm>();
            while(rawMatcher.find()){
                String word = rawMatcher.group(1).replace('ё', 'е').replace("’", "");
                if(!word.matches("[а-я\\-]+")){
                    continue;
                }
                WordForm wf = new WordForm(word, new Form(fillAttributes(new ArrayList<Attr>(mainAttr), gPattern.matcher(rawMatcher.group(2)))));
                if(wf.getWord().length() == 1 && !wf.getForm().isStop()){
                    continue;
                }
                if(items.isEmpty()){
                    if(wf.getForm().getAttrs().contains(Attr.Erro) || wf.getForm().getAttrs().contains(Attr.Dist)){
                        break;
                    }
                }
                items.add(wf);
            }
            if(items.isEmpty()){
                continue;
            }
            lemmas.add(new Lemma(items));
        }
        return lemmas;
    }
    
    public static List<Annotation> getAnnotations(File file) throws IOException{
        List<String> annotLines = extract(file);
        Pattern tokenPattern = Pattern.compile("<token.*?text=\"(.*?)\">(.*?)</token>", Pattern.MULTILINE|Pattern.DOTALL);
        Pattern gPattern = Pattern.compile("<g.*?v=\"(.*?)\"", Pattern.MULTILINE|Pattern.DOTALL);
        Pattern mainPattern = Pattern.compile("<source>(.*?)</source>.*?<tokens>(.*?)</tokens>", Pattern.MULTILINE|Pattern.DOTALL);
        List<Annotation> result = new ArrayList<Annotation>();
        StringBuilder sb = null;
        for (String annotLine : annotLines) {
            if(annotLine.trim().startsWith("<sentence ")){
                sb = new StringBuilder();
                continue;
            }
            if(annotLine.trim().startsWith("</sentence>")){
                Matcher mainMatcher = mainPattern.matcher(sb.toString());
                sb = null;
                if(!mainMatcher.find()){
                    continue;
                }
                String text = mainMatcher.group(1).replace("•", "").replace('ё', 'е').replace("’", "").replace("\t", "");
                List<Item> items = new ArrayList<Item>();

                Matcher tokenMatcher = tokenPattern.matcher(mainMatcher.group(2));
                int i = 0;
                while(tokenMatcher.find()){
                    String word = tokenMatcher.group(1).replace('ё', 'е').replace("’", "").toLowerCase();
                    List<Attr> attrs = fillAttributes(new ArrayList<Attr>(), gPattern.matcher(tokenMatcher.group(2)));
                    if(attrs.isEmpty()){
                        continue;
                    }
                    int pos = text.toLowerCase().indexOf(word, i);
                    if(pos < 0){
                        continue;
                    }
                    items.add(new Item(pos, pos + word.length(), new Form(attrs).toString()));
                    i = pos + word.length();
                }
                result.add(new Annotation(text, items));
                continue;
            }
            if(sb != null){
                sb.append(annotLine);
            }
        }
        return result;
    }
    
    private static List<Attr> fillAttributes(List<Attr> list, Matcher attrMatcher){
        while(attrMatcher.find()){
            String attr = attrMatcher.group(1).replace("-", "_");
            if(Character.isDigit(attr.charAt(0))){
                attr = "N" + attr;
            }
            if(attr.toLowerCase().equals("ms_f")){
                if(list.contains(Attr.femn)){
                    list.remove(Attr.femn);
                }
                if(list.contains(Attr.masc)){
                    list.remove(Attr.masc);
                }
                if(list.contains(Attr.neut)){
                    list.remove(Attr.neut);
                }
                if(list.contains(Attr.GNdr)){
                    list.remove(Attr.GNdr);
                }
                list.add(ms_f);
                continue;
            }
            Attr attrVal = Attr.valueOf(attr);
            if(attrVal == Attr.inan && list.contains(Attr.anim)){
                continue;
            }
            if(attrVal == Attr.anim && list.contains(Attr.inan)){
                list.remove(Attr.inan);
            }
            if(attrVal == Attr.femn || attrVal == Attr.masc || attrVal == Attr.neut || attrVal == Attr.GNdr){
                if(list.contains(ms_f)){
                    continue;
                }
            }
            if(attrVal == Attr.loc1 || attrVal == Attr.loc2){
                attrVal = Attr.loct;
            }
            if(attrVal == Attr.gent || attrVal == Attr.gen1 || attrVal == Attr.gen2){
                if(list.get(0) == Attr.ADJS){
                    continue;
                }
                attrVal = Attr.gent;
            }
            if(attrVal == Attr.acc2){
                attrVal = Attr.accs;
            }
            if(attrVal.getType() == Type.Other){
                continue;
            }
            list.add(attrVal);
        }
        if(list.contains(Attr.Name) || list.contains(Attr.Surn) || list.contains(Attr.Patr)){
            list.remove(Attr.inan);
            if(!list.contains(Attr.anim)){
                list.add(Attr.anim);
            }
        }
        if(list.contains(Attr.VERB) && list.contains(Attr.Impe) && list.contains(Attr.neut)){
            list.remove(Attr.neut);
        }
        return list;
    }
    
    private static List<String> extract(File file) throws IOException{
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        try {
            ZipEntry ze = zis.getNextEntry();
            if(ze == null){
                throw new RuntimeException("can't unzip file");
            }
            return IOUtils.readLines(zis, Charset.forName("utf-8"));
        } finally {
            zis.close();
        }
    }
}
