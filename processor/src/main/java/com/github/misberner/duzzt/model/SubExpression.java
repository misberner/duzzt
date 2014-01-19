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
package com.github.misberner.duzzt.model;

import com.github.misberner.duzzt.annotations.SubExpr;
import com.github.misberner.duzzt.re.DuzztRegExp;
import com.github.misberner.duzzt.re.parser.DuzztRegExpParser;

public class SubExpression {

	private final String name;
	private final DuzztRegExp expression;
	private final boolean ownScope;
	
	public SubExpression(DuzztRegExp rootExpression) {
		this.name = "root-expression"; // note: invalid subexpression identifier!
		this.expression = rootExpression;
		this.ownScope = true;
	}
	
	public SubExpression(SubExpr annotation) {
		this.name = annotation.name();
		this.expression = DuzztRegExpParser.parse(annotation.definedAs());
		this.ownScope = annotation.ownScope();
	}
	
	public String getName() {
		return name;
	}
	
	public DuzztRegExp getExpression() {
		return expression;
	}
	
	public boolean isOwnScope() {
		return ownScope;
	}
}
