/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.scxml2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A <code>PayloadProvider</code> is an element in the SCXML document
 * that can provide payload data for an event or an external process.
 */
public abstract class PayloadProvider extends Action {

    /**
     * Payload data values wrapper list needed when multiple variable entries use the same names.
     * The multiple values are then wrapped in a list. The PayloadBuilder uses this 'marker' list
     * to distinguish between entry values which are a list themselves and the wrapper list.
     */
    private static class DataValueList extends ArrayList {
    }

    /**
     * Adds an attribute and value to a payload data map.
     * <p>
     * As the SCXML specification allows for multiple payload attributes with the same name, this
     * method takes care of merging multiple values for the same attribute in a list of values.
     * </p>
     * <p>
     * Furthermore, as modifications of payload data on either the sender or receiver side should affect the
     * the other side, attribute values (notably: {@link Node} value only for now) is cloned first before being added
     * to the payload data map. This includes 'nested' values within a {@link NodeList}, {@link List} or {@link Map}.
     * </p>
     * @param attrName the name of the attribute to add
     * @param attrValue the value of the attribute to add
     * @param payload the payload data map to be updated
     */
    @SuppressWarnings("unchecked")
    protected void addToPayload(final String attrName, final Object attrValue, Map<String, Object> payload) {
        DataValueList valueList = null;
        Object value = payload.get(attrName);
        if (value != null) {
            if (value instanceof DataValueList) {
                valueList = (DataValueList)value;
            }
            else {
                valueList = new DataValueList();
                valueList.add(value);
                payload.put(attrName, valueList);
            }
        }
        value = attrValue;
        if (value instanceof List) {
            if (valueList == null) {
                valueList = new DataValueList();
                payload.put(attrName, valueList);
            }
            valueList.addAll((List)value);
        }
        else if (valueList != null) {
            valueList.add(value);
        }
        else {
            payload.put(attrName, value);
        }
    }
}
