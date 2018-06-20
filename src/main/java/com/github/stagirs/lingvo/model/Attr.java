package com.github.stagirs.lingvo.model;


/**
 * Created by must on 12.12.2016.
 */
public enum Attr {
    NOUN("имя существительное", Type.POST),
    PREP("предлог", Type.POST),
    CONJ("союз", Type.POST),
    INFN("глагол (инфинитив)", Type.POST),
    VERB("глагол (личная форма)", Type.POST),
    ADJF("имя прилагательное (полное)", Type.POST),
    ADJS("имя прилагательное (краткое)", Type.POST),
    ADVB("наречие", Type.POST),
    COMP("компаратив", Type.POST),//плутоватее побыстрее
    PRTF("причастие (полное)", Type.POST),
    PRTS("причастие (краткое)", Type.POST),
    GRND("деепричастие", Type.POST),
    NUMR("числительное", Type.POST),
    NPRO("местоимение", Type.POST),
    PRED("предикатив", Type.POST),//мне каково?
    PRCL("частица", Type.POST),
    INTJ("междометие", Type.POST),
    
    ANim("одушевлённость не выражена", Type.ANim),
    anim("одушевлённое", Type.ANim),
    inan("неодушевлённое", Type.ANim),
    
    GNdr("род не выражен", Type.GNdr),
    masc("мужской род", Type.GNdr),
    femn("женский род", Type.GNdr),
    neut("средний род", Type.GNdr),
    ms_f("общий род", Type.GNdr),
    
    sing("единственное число", Type.NMbr),
    plur("множественное число", Type.NMbr),
    
    nomn("именительный падеж", Type.CAse),
    gent("родительный падеж", Type.CAse),
    datv("дательный падеж", Type.CAse),
    accs("винительный падеж", Type.CAse),
    ablt("творительный падеж", Type.CAse),
    loct("предложный падеж", Type.CAse),
    voct("звательный падеж", Type.CAse),
    
    
    Subx("возможна субстантивация", Type.Subx),
    Supr("превосходная степень", Type.Supr),
    Qual("качественное", Type.Qual),
    Apro("местоименное", Type.Apro),
    Anum("порядковое", Type.Anum),
    Poss("притяжательное", Type.Poss),
    
    perf("совершенный вид", Type.ASpc),
    impf("несовершенный вид", Type.ASpc),
    
    tran("переходный", Type.TRns),
    intr("непереходный", Type.TRns),
    
    Impe("безличный", Type.Impe),
    
    N1per("1 лицо", Type.PErs),
    N2per("2 лицо", Type.PErs),
    N3per("3 лицо", Type.PErs),
    
    pres("настоящее время", Type.TEns),
    past("прошедшее время", Type.TEns),
    futr("будущее время", Type.TEns),
    
    indc("изъявительное наклонение", Type.MOod),
    impr("повелительное наклонение", Type.MOod),
    
    incl("говорящий включён (идем, идемте)", Type.INvl),
    excl("говорящий не включён в действие (иди, идите)", Type.INvl),
    
    actv("действительный залог", Type.VOic),
    pssv("страдательный залог", Type.VOic),
    
    Infr("разговорное", Type.Infr),
    Slng("жаргонное", Type.Slng),
    Arch("устаревшее", Type.Arch),
    Litr("литературный вариант", Type.Litr),
    
    Ques("вопросительное", Type.Ques),
    Dmns("указательное", Type.Dmns),
    Prnt("вводное слово", Type.Prnt),
    
    Fimp("деепричастие от глагола несовершенного вида", Type.Fimp),
    
    Name("имя", Type.Name),
    Surn("фамилия", Type.Surn),
    Patr("отчество", Type.Patr),
    
    Abbr("аббревиатура/сокращение", Type.Abbr),
    Geox("топоним", Type.Geox),
    Orgn("организация", Type.Orgn),
    Trad("торговая марка", Type.Trad),
    
    Erro("опечатка", Type.Error),
    Dist("искажение", Type.Error),
    
    
    Init("Инициал", Type.Other),
    
    SYMB("SYMB", Type.Other),
    ROMN("ROMN", Type.Other),
    LATN("LATN", Type.Other),
    NUMB("NUMB", Type.Other),
    UNKN("UNKN", Type.Other),
    PNCT("PNCT", Type.Other),
    
    Sgtm("только ед. число", Type.Other),
    Pltm("только множ. число", Type.Other),
    Fixd("неизменяемое", Type.Other),
    V_ey("форма на -ею", Type.Other),
    V_oy("форма на -ою", Type.Other),
    V_be("форма на -ье", Type.Other),
    V_en("форма на -енен", Type.Other),
    V_ie("отчество через -ие-", Type.Other),
    V_bi("форма на -ьи", Type.Other),
    Cmp2("сравнительная степень на по-", Type.Other),
    V_ej("форма компаратива на -ей", Type.Other),
    Impx("возможно безличное употребление", Type.Other),
    Prdx("может выступать в роли предикатива", Type.Other),
    Coun("счётная форма", Type.Other),
    Coll("собирательное числительное", Type.Other),
    V_sh("деепричастие на -ши", Type.Other),
    Af_p("форма после предлога", Type.Other),
    Inmx("может использоваться как одуш. / неодуш. ", Type.Other),
    Vpre("Вариант предлога ( со, подо, ...)", Type.Other),
    Anph("Анафорическое (местоимение)", Type.Other),
    Adjx("может выступать в роли прилагательного", Type.Other),    
    gen1("первый родительный падеж", Type.Other),
    gen2("второй родительный (частичный) падеж", Type.Other),
    acc2("второй винительный падеж", Type.Other),
    loc1("первый предложный падеж", Type.Other),
    loc2("второй предложный (местный) падеж", Type.Other);
    

    private final String description;
    private final Type type;

    Attr(String description, Type type) {
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }
}