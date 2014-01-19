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
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.misberner.apcommons.util.Visibility;
import com.github.misberner.apcommons.util.types.TypeUtils;
import com.github.misberner.duzzt.DuzztAction;

public class ImplementationModel {
	
	public static ImplementationModel create(
			TypeElement type,
			DSLSettings settings,
			Elements elementUtils,
			Types typeUtils) {
		ImplementationModel model = new ImplementationModel(type);
		model.initialize(settings, elementUtils, typeUtils);
		
		return model;
	}
	
	private final TypeElement type;
	private final Map<String,List<DuzztAction>> actionLists = new HashMap<>();
	private final List<DuzztAction> allActions = new ArrayList<>();
	private final List<DuzztAction> globalActions = new ArrayList<>();
	private final List<DuzztAction> terminatorActions = new ArrayList<>();
	
	private final List<ForwardConstructor> forwardConstructors = new ArrayList<>();
	
	private ImplementationModel(TypeElement type) {
		if(Visibility.of(type) == Visibility.PRIVATE) {
			throw new IllegalArgumentException("Visibility of implementation class "
					+ type + " must not be private");
		}
		
		this.type = type;
	}
	
	private void initialize(DSLSettings settings, Elements elementUtils, Types typeUtils) {
		findForwardConstructors(settings, typeUtils);
		findActions(settings, elementUtils);
	}
	
	public TypeElement getType() {
		return type;
	}
	
	public Visibility getVisibility() {
		return Visibility.of(type);
	}
	
	public boolean isStandaloneInstantiable() {
		return TypeUtils.isStandaloneInstantiable(type);
	}
	
	public List<? extends TypeParameterElement> getTypeParameters() {
		return type.getTypeParameters();
	}
	
	public List<ForwardConstructor> getForwardConstructors(Visibility minVisibility) {
		if(minVisibility == Visibility.PACKAGE_PRIVATE) {
			return Collections.unmodifiableList(forwardConstructors);
		}
		List<ForwardConstructor> result = new ArrayList<>();
		
		for(ForwardConstructor fwdCtor : forwardConstructors) {
			if(fwdCtor.getOriginalVisibility().compareTo(minVisibility) >= 0) {
				result.add(fwdCtor);
			}
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
	
	
	private void findActions(DSLSettings settings, Elements elementUtils) {
		List<? extends Element> members;
		if(settings.isIncludeInherited()) {
			members = elementUtils.getAllMembers(type);
		}
		else {
			members = type.getEnclosedElements();
		}
		List<? extends ExecutableElement> methods = ElementFilter.methodsIn(members);
		
		for(ExecutableElement m : methods) {
			DuzztAction a = DuzztAction.fromMethod(m, settings);
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
	
	private void findForwardConstructors(DSLSettings settings, Types typeUtils) {
		if(!TypeUtils.isStandaloneInstantiable(type)) {
			return;
		}
		
		List<ExecutableElement> constructors = TypeUtils.getConstructors(type);
		
		for(ExecutableElement ctor : constructors) {
			if(Visibility.of(ctor) == Visibility.PRIVATE) {
				continue;
			}
			
			List<? extends VariableElement> params = ctor.getParameters();
			// Check if the erasure of this parameter list conflicts
			// with the delegate constructor
			if(params.size() == 1) {
				VariableElement param = params.get(0);
				TypeMirror erasedParamType = typeUtils.erasure(param.asType());
				TypeMirror erasedImplType = typeUtils.erasure(type.asType());
				if(erasedParamType.equals(erasedImplType)) {
					continue;
				}
			}
			
			ForwardConstructor fwd = ForwardConstructor.from(ctor, settings);
			
			if(fwd != null) {
				forwardConstructors.add(fwd);
			}
		}
	}

}
