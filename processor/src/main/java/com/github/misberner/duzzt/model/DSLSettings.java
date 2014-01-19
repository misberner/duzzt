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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

import com.github.misberner.apcommons.util.Visibility;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;
import com.github.misberner.duzzt.annotations.SubExpr;
import com.github.misberner.duzzt.re.DuzztRegExp;
import com.github.misberner.duzzt.re.parser.DuzztRegExpParser;

public class DSLSettings {
	
	private static final Map<String,SubExpression> parseSubexpressions(SubExpr[] subExprs) {
		Map<String,SubExpression> result = new HashMap<>();
		
		for(SubExpr se : subExprs) {
			SubExpression parsed = new SubExpression(se);
			result.put(parsed.getName(), parsed);
		}
		
		return result;
	}
	
	private final String name;
	private final String packageRef;
	
	private final DuzztRegExp syntax;
	private final Map<String,SubExpression> subExpressions; 
	
	private final boolean enableAllMethods;
	private final boolean autoVarArgs;
	private final boolean forwardAllConstructors;
	private final boolean nonVoidTerminators;
	
	private final boolean includeInherited;
	
	private final Visibility delegateConstructorVisibility;
	private final Visibility forwardConstructorVisibility;
	
	public DSLSettings(GenerateEmbeddedDSL annotation) {
		this.name = annotation.name();
		this.packageRef = annotation.packageName();
		
		this.syntax = DuzztRegExpParser.parse(annotation.syntax());
		this.subExpressions = parseSubexpressions(annotation.where());
		
		this.autoVarArgs = annotation.autoVarArgs();
		this.delegateConstructorVisibility = annotation.delegateConstructorVisibility();
		this.forwardConstructorVisibility = annotation.forwardConstructorVisibility();
		this.enableAllMethods = annotation.enableAllMethods();
		this.forwardAllConstructors = annotation.forwardAllConstructors();
		this.nonVoidTerminators = annotation.nonVoidTerminators();
		
		this.includeInherited = annotation.includeInherited();
	}
	
	public String getName() {
		return name;
	}
	
	public String getPackageRef() {
		return packageRef;
	}
	
	public DuzztRegExp getSyntax() {
		return syntax;
	}
	
	public boolean isAutoVarArgs() {
		return autoVarArgs;
	}
	
	public boolean isEnableAllMethods() {
		return enableAllMethods;
	}
	
	public boolean isForwardAllConstructors() {
		return forwardAllConstructors;
	}
	
	public boolean isIncludeInherited() {
		return includeInherited;
	}
	
	public Visibility getDelegateConstructorVisibility() {
		return delegateConstructorVisibility;
	}
	
	public Map<String,SubExpression> getSubExpressions() {
		return Collections.unmodifiableMap(subExpressions);
	}
	
	public Visibility getForwardConstructorVisibility() {
		return forwardConstructorVisibility;
	}
	
	
	public boolean isDefaultAutoVarArgs(ExecutableElement method) {
		if(!autoVarArgs) {
			return false;
		}
		if(method.isVarArgs()) {
			return false;
		}
		if(method.getParameters().isEmpty()) {
			return false;
		}
		return true;
	}
	
	public boolean isDefaultTerminator(ExecutableElement method) {
		if(!nonVoidTerminators) {
			return false;
		}
		
		return (method.getReturnType().getKind() == TypeKind.VOID);
	}
}
