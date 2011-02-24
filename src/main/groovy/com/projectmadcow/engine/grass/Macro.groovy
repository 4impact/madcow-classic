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

package com.projectmadcow.engine.grass

/**
 * Marco handling class for grass code.
 * This currently handles the 'madcow.eval' type functions within
 * Grass code.
 *
 * @author gbunney
 */
class Macro {

     // TODO - Expand this to have a plugin based architecture to allow custom macros

     /**
     * Invoked when macro is 'madcow.'
     */
    public def getMadcow() {
        return this
    }

    /**
     * Eval macro will call the supplied Closure, or Eval.me any other type.
     */
    public static def eval(def expression) {

        switch (expression) {
            case Closure:
                return (expression as Closure).call()
            default:
                return Eval.me(expression)
        }

    }
}
