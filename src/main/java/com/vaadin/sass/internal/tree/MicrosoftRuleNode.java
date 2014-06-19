/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.sass.internal.tree;

import java.util.ArrayList;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.SassListItem;
import com.vaadin.sass.internal.parser.StringInterpolationSequence;
import com.vaadin.sass.internal.parser.StringItem;
import com.vaadin.sass.internal.util.StringUtil;

public class MicrosoftRuleNode extends Node implements IVariableNode {

    private final String name;
    private StringInterpolationSequence value;

    public MicrosoftRuleNode(String name, StringInterpolationSequence value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void replaceVariables() {
        boolean variableReplaced = false;
        value = value.replaceVariables();
        // Replace variables occurring in quoted strings
        ArrayList<SassListItem> items = new ArrayList<SassListItem>();
        for (SassListItem item : value.getItems()) {
            if (!(item instanceof StringItem)) {
                items.add(item);
                continue;
            }
            String stringValue = item.printState();
            for (final VariableNode var : ScssStylesheet.getVariables()) {
                if (StringUtil.containsVariable(stringValue, var.getName())) {
                    variableReplaced = true;
                    stringValue = StringUtil.replaceVariable(stringValue,
                            var.getName(), var.getExpr().printState());
                }
            }
            items.add(new StringItem(stringValue));
        }
        if (variableReplaced) {
            value = new StringInterpolationSequence(items);
        }
    }

    @Override
    public String printState() {
        return name + ": " + value + ";";
    }

    @Override
    public String toString() {
        return "MicrosoftRule node [" + printState() + "]";
    }

    @Override
    public void traverse() {
        replaceVariables();
        traverseChildren();
    }
}