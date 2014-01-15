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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.github.misberner.apcommons.util.ParameterInfo;
import com.github.misberner.apcommons.util.ParameterUtils;
import com.github.misberner.duzzt.DuzztAction;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;

public class ImplementationModel {
	
	public static class ConstructorInfo {
		private static final ConstructorInfo DEFAULT_CTOR = new ConstructorInfo();
		
		public static ConstructorInfo getDefaultInstance() {
			return DEFAULT_CTOR;
		}
		
		private final List<? extends TypeParameterElement> typeParameters;
		private final List<? extends TypeMirror> thrownTypes;
		private final List<ParameterInfo> parameters;
		
		public ConstructorInfo(ExecutableElement elem) {
			this.typeParameters = elem.getTypeParameters();
			this.thrownTypes = elem.getThrownTypes();
			this.parameters = ParameterUtils.getParameters(elem);
		}
		
		private ConstructorInfo() {
			this.typeParameters = Collections.emptyList();
			this.thrownTypes = Collections.emptyList();
			this.parameters = Collections.emptyList();
		}
		
		public List<? extends TypeParameterElement> getTypeParameters() {
			return typeParameters;
		}
		
		public List<ParameterInfo> getParameters() {
			return parameters;
		}
		
		public List<? extends TypeMirror> getThrownTypes() {
			return thrownTypes;
		}
	}
	
	private final TypeElement type;
	private final Map<String,List<DuzztAction>> actionLists = new HashMap<>();
	private final List<DuzztAction> allActions = new ArrayList<>();
	private final List<DuzztAction> globalActions = new ArrayList<>();
	private final List<DuzztAction> terminatorActions = new ArrayList<>();
	
	public ImplementationModel(TypeElement type, Elements elementUtils, GenerateEmbeddedDSL ann) {
		this.type = type;
		findActions(ann, elementUtils);
	}
	
	public TypeElement getType() {
		return type;
	}
	
	public List<? extends TypeParameterElement> getTypeParameters() {
		return type.getTypeParameters();
	}
	

	public List<ConstructorInfo> getPublicConstructorInfos() {
		List<? extends ExecutableElement> allCtors = ElementFilter.constructorsIn(type.getEnclosedElements());
		
		if(allCtors.isEmpty()) {
			return Collections.singletonList(ConstructorInfo.getDefaultInstance());
		}
		
		List<ExecutableElement> publicCtors = new ArrayList<>();
		for(ExecutableElement ctor : allCtors) {
			if(ctor.getModifiers().contains(Modifier.PUBLIC)) {
				publicCtors.add(ctor);
			}
		}
		
		List<ConstructorInfo> result = new ArrayList<>();
		for(ExecutableElement ctor : publicCtors) {
			result.add(new ConstructorInfo(ctor));
		}
		
		return result;
	}
	
	public Set<String> getActionNames() {
		return Collections.unmodifiableSet(actionLists.keySet());
	}
	
	public boolean hasActionName(String actName) {
		return actionLists.containsKey(actName);
	}
	
	public Set<? extends Map.Entry<String,List<DuzztAction>>> getActionLists() {
		return Collections.unmodifiableSet(actionLists.entrySet());
	}
	
	public List<DuzztAction> getAllActions() {
		return Collections.unmodifiableList(allActions);
	}
	
	public List<DuzztAction> getGlobalActions() {
		return Collections.unmodifiableList(globalActions);
	}
	
	public List<DuzztAction> getTerminatorActions() {
		return Collections.unmodifiableList(terminatorActions);
	}
	
	
	private void findActions(GenerateEmbeddedDSL dslAnnotation, Elements elementUtils) {
		List<? extends Element> members;
		if(dslAnnotation.includeInherited()) {
			members = elementUtils.getAllMembers(type);
		}
		else {
			members = type.getEnclosedElements();
		}
		List<? extends ExecutableElement> methods = ElementFilter.methodsIn(members);
		
		boolean defaultEnable = dslAnnotation.enableAllMethods();
		boolean defaultAutoVarArgs = dslAnnotation.autoVarArgs();
		
		for(ExecutableElement m : methods) {
			DuzztAction a = DuzztAction.get(m, defaultEnable, defaultAutoVarArgs);
			if(a != null) {
				String name = a.getName();
				List<DuzztAction> lst = actionLists.get(name);
				if(lst == null) {
					lst = new ArrayList<>();
					actionLists.put(name, lst);
				}
				lst.add(a);
				
				allActions.add(a);
				if(a.isGlobal()) {
					globalActions.add(a);
				}
				if(a.isTerminator()) {
					terminatorActions.add(a);
				}
			}
		}
	}

}
