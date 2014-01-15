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
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.github.misberner.apcommons.util.ElementUtils;
import com.github.misberner.apcommons.util.NameUtils;
import com.github.misberner.duzzt.DuzztDiagnosticListener;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;
import com.github.misberner.duzzt.processor.Duzzt;
import com.github.misberner.duzzt.re.DuzztRegExp;
import com.github.misberner.duzzt.re.parser.DuzztRegExpParser;

public class DSLSpecification {
	
	private final String className;
	private final String packageName;
	
	private final ImplementationModel implementation;
	private final DuzztRegExp dslSyntax;
	private final Map<String,DuzztRegExp> subExpressions;
	
	
	public DSLSpecification(TypeElement type, GenerateEmbeddedDSL dsl,
			Elements elementUtils, DuzztDiagnosticListener dl) {
		
		this.className = dsl.name();
		String implPackage = ElementUtils.getPackageName(type);
		this.packageName = NameUtils.resolvePackageName(dsl.packageName(), implPackage);
	
		this.implementation = new ImplementationModel(type, elementUtils, dsl);
		this.dslSyntax = DuzztRegExpParser.parse(dsl.syntax());
		this.subExpressions = Duzzt.getSubexpressions(dl, dsl.where());
	}
	
	public DSLSpecification(String className,
			String packageName,
			ImplementationModel implementation,
			DuzztRegExp dslSyntax,
			Map<String,DuzztRegExp> subExpressions) {
		this.className = className;
		this.packageName = packageName;
		this.implementation = implementation;
		this.dslSyntax = dslSyntax;
		this.subExpressions = subExpressions;
	}
	
	public DuzztRegExp getDSLSyntax() {
		return dslSyntax;
	}
	
	public Map<String,DuzztRegExp> getSubExpressions() {
		return Collections.unmodifiableMap(subExpressions);
	}
	
	public ImplementationModel getImplementation() {
		return implementation;
	}
	
	
	public String getClassName() {
		return className;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getNonDefaultPackageName() {
		return packageName.isEmpty() ? null : packageName;
	}
	
	public String getQualifiedClassName() {
		if(packageName == null || packageName.isEmpty()) {
			return className;
		}
		return packageName + "." + className;
	}

}
