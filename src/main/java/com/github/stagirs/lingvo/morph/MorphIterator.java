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

import com.github.stagirs.lingvo.morph.MorphStateMachine.State;
import com.github.stagirs.lingvo.morph.model.Morph;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Dmitriy Malakhov
 */
public class MorphIterator implements Iterator<Morph>{
    
    private class Item{
        State state;
        int childNumber = -1;
        List<String> prefcommons;
        int nextPos = 0;

        public Item(State state) {
            this.state = state;
            if(state.mapping == null){
                return;
            }
            this.prefcommons = new ArrayList<String>(state.mapping.getPrefcommons());
        }
    }
    
    Stack<Item> stack = new Stack();

    public MorphIterator() {
        stack.add(new Item(MorphStateMachine.begin()));
    }
    
    public MorphIterator(MorphStateMachine.State begin) {
        stack.add(new Item(begin));
    }
    
    Morph next;    

    @Override
    public boolean hasNext() {
        if(stack.size() > 0){
            Item item = stack.peek();
            if(item.prefcommons != null && item.nextPos < item.prefcommons.size()){
                String prefcommon = item.prefcommons.get(item.nextPos);
                next = new Morph(prefcommon + item.state.mapping.getSuf(), item.state.mapping.getRule(prefcommon));
                item.nextPos++;
                return true;
            }
            item.childNumber++;
            for(;item.state.states != null && item.childNumber < item.state.states.length; item.childNumber++){
                if(item.state.states[item.childNumber] == null){
                    continue;
                }
                stack.push(new Item(item.state.states[item.childNumber]));
                return hasNext();
            }
            stack.pop();
            return hasNext();
        }
        next = null;
        return false;
    }

    @Override
    public Morph next() {
        return next;
    }
    
}
