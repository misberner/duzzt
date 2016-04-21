/*
 *
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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.github.misberner.apcommons.util.annotations.AnnotationUtils;
import com.github.misberner.apcommons.util.methods.MethodUtils;
import com.github.misberner.apcommons.util.methods.ParameterInfo;
import com.github.misberner.duzzt.annotations.DSLAction;
import com.github.misberner.duzzt.model.DSLSettings;

public class DuzztAction {

	public static class ActionComparator implements Comparator<DuzztAction> {

		private final Types types;

		public ActionComparator(Types types) {
			this.types = types;
		}

		@Override
		public int compare(DuzztAction o1, DuzztAction o2) {
			// identity should the only form of equality of actions
			if(o1 == o2) {
				return 0;
			}

			String name1 = o1.name, name2 = o2.name;
			int nameCmp = name1.compareTo(name2);
			if(nameCmp != 0) {
				return nameCmp;
			}

			String methName1 = o1.methodElement.getSimpleName().toString();
			String methName2 = o2.methodElement.getSimpleName().toString();

			int methNameCmp = methName1.compareTo(methName2);
			if(methNameCmp != 0) {
				return methNameCmp;
			}

			List<ParameterInfo> params1 = o1.parameters, params2 = o2.parameters;

			if(params1.size() != params2.size()) {
				return params1.size() - params2.size();
			}

			Iterator<? extends ParameterInfo> it1 = params1.iterator(), it2 = params2.iterator();

			while(it1.hasNext()) {
				ParameterInfo p1 = it1.next();
				ParameterInfo p2 = it2.next();
				String erased1Name = types.erasure(p1.getType()).toString();
				String erased2Name = types.erasure(p2.getType()).toString();

				int paramCmp = erased1Name.compareTo(erased2Name);
				if(paramCmp != 0) {
					return paramCmp;
				}
			}

			String erased1Ret = types.erasure(o1.getReturnType()).toString();
			String erased2Ret = types.erasure(o2.getReturnType()).toString();

			int retCmp = erased1Ret.compareTo(erased2Ret);
			if(retCmp != 0) {
				return retCmp;
			}

			throw new AssertionError("Duzzt action '" + name1 + "' exists twice");
		}
	}
	
	public static DuzztAction fromMethod(ExecutableElement methodElement, DSLSettings settings, String docComment) {
		String name = methodElement.getSimpleName().toString();
		
		boolean global = false;
		boolean enable = settings.isEnableAllMethods();
		boolean autoVarArgs = settings.isDefaultAutoVarArgs(methodElement);
		boolean terminator = settings.isDefaultTerminator(methodElement);
		
		DSLAction actionAnn = methodElement.getAnnotation(DSLAction.class);
		if(actionAnn != null) {
			Set<String> valuesSet
				= AnnotationUtils.getAnnotationValues(methodElement, DSLAction.class).keySet();
			
			enable = actionAnn.enable();
			if(!enable) {
				return null;
			}
			
			global = actionAnn.global();
			
			if(valuesSet.contains("terminator")) {
				terminator = actionAnn.terminator();
			}
			
			if(valuesSet.contains("autoVarArgs")) {
				autoVarArgs = actionAnn.autoVarArgs(); 
			}
		}
		else if(!enable) {
			return null;
		}
		
		return new DuzztAction(methodElement, name, global, terminator, autoVarArgs, docComment);
	}
	
	
	private final ExecutableElement methodElement;
	private String docComment;
	private final List<ParameterInfo> parameters;
	private final String name;
	
	private final boolean global;
	private final boolean terminator;
	private final boolean autoVarArgs;
	
	
	public DuzztAction(ExecutableElement methodElement, String name, boolean global,
					   boolean terminator, boolean autoVarArgs, String docComment) {
		this.methodElement = methodElement;
		this.docComment = docComment;
		this.parameters = MethodUtils.getParameterInfos(methodElement);
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

	public String getDocComment()
	{
		if (docComment == null) {
			return null;
		}
		return "/**\n*" + docComment.replaceAll("\n", "\n*") + "*/";
	}
}
