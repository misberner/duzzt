/*
 * Copyright (c) 2014 by Malte Isberner (https://github.com/misberner).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.misberner.duzzt;

import java.util.Map;

import com.github.misberner.duzzt.automaton.DuzztAutomaton;
import com.github.misberner.duzzt.model.SubExpression;
import com.github.misberner.duzzt.re.DuzztRegExp;

/**
 * Duzzt Regular Expression Compiler interface.
 * <p>
 * A compiler (in the sense of this class) takes a regular expression (along with a
 * possibly empty map of named subexpression) and turns it into an equivalent
 * {@link DuzztAutomaton automaton} ( 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public interface DuzztCompiler {
	DuzztAutomaton compile(DuzztRegExp re, Map<String,SubExpression> subExpressions);

}
