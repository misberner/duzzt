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

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.github.misberner.apcommons.util.ParameterInfo;
import com.github.misberner.apcommons.util.ParameterUtils;
import com.github.misberner.duzzt.annotations.AutoVarArgs;
import com.github.misberner.duzzt.annotations.DSLAction;

public class DuzztAction {
	
	public static DuzztAction get(ExecutableElement methodElement, boolean defaultEnable, boolean defaultAutoVarArgs) {
		boolean global = false;
		boolean terminator = false;
		boolean enable = defaultEnable;
		boolean autoVarArgs = defaultAutoVarArgs;
		String name = methodElement.getSimpleName().toString();
		
		DSLAction actionAnn = methodElement.getAnnotation(DSLAction.class);
		if(actionAnn != null) {
			enable = !actionAnn.disable();
			if(!enable) {
				return null;
			}
			
			global = actionAnn.global();
			terminator = actionAnn.terminator();
			
			AutoVarArgs autoVA = actionAnn.autoVarArgs();
			if(autoVA != AutoVarArgs.DEFAULT) {
				autoVarArgs = (autoVA == AutoVarArgs.ENABLE); 
			}
		}
		else if(!enable) {
			return null;
		}
		
		return new DuzztAction(methodElement, name, global, terminator, autoVarArgs);
	}
	
	
	private final ExecutableElement methodElement;
	private final List<ParameterInfo> parameters;
	private final String name;
	
	private final boolean global;
	private final boolean terminator;
	private final boolean autoVarArgs;
	
	
	public DuzztAction(ExecutableElement methodElement, String name, boolean global,
			boolean terminator, boolean autoVarArgs) {
		this.methodElement = methodElement;
		this.parameters = ParameterUtils.getParameters(methodElement);
		this.name = name;
		this.global = global;
		this.terminator = terminator;
		this.autoVarArgs = !methodElement.isVarArgs() && !methodElement.getParameters().isEmpty() && autoVarArgs;
	}

	public String getName() {
		return name;
	}
	
	public boolean isGlobal() {
		return global;
	}
	
	public boolean isTerminator() {
		return terminator;
	}
	
	public boolean isAutoVarArgs() {
		return autoVarArgs;
	}
	
	public boolean isVoid() {
		return (methodElement.getReturnType().getKind() == TypeKind.VOID);
	}
	
	public ExecutableElement getMethod() {
		return methodElement;
	}
	
	// Signature information
	
	public TypeMirror getReturnType() {
		return methodElement.getReturnType();
	}
	
	/**
	 * Retrieves the list of exceptions thrown by the underlying method.
	 * @return the exceptions thrown by the underlying method
	 * @see ExecutableElement#getThrownTypes()
	 */
	public List<? extends TypeMirror> getThrownTypes() {
		return methodElement.getThrownTypes();
	}
	
	/**
	 * Retrieves the list of type parameters of the underlying method.
	 * @return the type parameters of the underlying method
	 * @see ExecutableElement#getTypeParameters()
	 */
	public List<? extends TypeParameterElement> getTypeParameters() {
		return methodElement.getTypeParameters();
	}
	
	public List<ParameterInfo> getParameters() {
		return parameters;
	}
}
