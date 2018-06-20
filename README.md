# Stagirs Lingvo
Для обновления необходимо выкачать актуальные словарь dict.opcorpora.xml.zip и разметку annot.opcorpora.no_ambig.xml.zip OpenCorpora и подложить их в корень проекта:

* com.github.stagirs.lingvo.build.MorphStateMachineBuilder (Строим конечный автомат MorphStateMachine на базе словаря dict.opcorpora.xml.zip).
* com.github.stagirs.lingvo.build.SyntaxStatisticsBuilder (Строим статистику встречаемости форм слов SyntaxStatistics на базе разметки annot.opcorpora.no_ambig.xml.zip).

Для получение морфологической информации для слова из словаря используется метод com.github.stagirs.lingvo.morph.MorphAnalyzer.get(word).

Для предсказания морфологической информации для слова не из словаря используется метод com.github.stagirs.lingvo.morph.MorphPredictor.get(word).

В результате выполнения методов MorphAnalyzer.get(word) и MorphPredictor.get(word) возвращается объект Morph.

Из объекта Morph может быть получено:

* Само слово (getRaw()). 
* Количество различных нормальных форм (getNormCount()).

По индексу нормальной формы: 

* нормализованное слово (getNorm(int normId)).
* варианты морф. описания слова (getRawForms(int normId)), 
* варианты морф. описания нормальной формы (getNormForms(int normId)), 

Для выделения предложений в тексте используется метод com.github.stagirs.lingvo.syntax.SentenceExtractor.extract(text).

Для итерации по текстам с выделением предложений используется класс com.github.stagirs.lingvo.syntax.SentenceIterator.

При выделении предложений может возникнуть неоднозначность в определении правильного морф. описания того или иного слова. Класс com.github.stagirs.lingvo.syntax.DisambiguityProcessor поможет решить эту проблему.
С помощью метода DisambiguityProcessor.process(sentence) для каждого неоднозначного слова в предложении sentence каждому варианту морф. информации ставится в соответствие вес, отражающий его совместимость с предложением sentence. 
Далее для разрешения неоднозначности будет выбран вариант с наибольшим весом.