/*
 * Copyright 2008-2011 4impact Technology Services, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package au.com.ts4impact.madcow.plugins

import au.com.ts4impact.madcow.engine.plugin.Plugin

/**
 * VerifyText
 *
 * @author gbunney
 */
public class VerifyText extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {

        // remap value to text, unless its already defined
        // since verifyText doesn't support value
        def valueAttribute = pluginParameters.get("value")
        if (valueAttribute != null) {
            pluginParameters.remove("value")

            if (pluginParameters.get("text") == null)
                pluginParameters.put("text", valueAttribute)
        }

        antBuilder.verifyText(pluginParameters)
    }
}
