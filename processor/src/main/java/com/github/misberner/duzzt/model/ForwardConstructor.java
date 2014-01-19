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
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

import com.github.misberner.apcommons.util.Visibility;
import com.github.misberner.apcommons.util.annotations.AnnotationUtils;
import com.github.misberner.apcommons.util.methods.MethodUtils;
import com.github.misberner.apcommons.util.methods.ParameterInfo;
import com.github.misberner.duzzt.annotations.DSLConstructor;

public class ForwardConstructor {
	
	public static ForwardConstructor from(ExecutableElement ctor, DSLSettings settings) {
		if(Visibility.of(ctor) == Visibility.PRIVATE) {
			return null;
		}
		
		DSLConstructor annotation = ctor.getAnnotation(DSLConstructor.class);
		
		Visibility vis = settings.getForwardConstructorVisibility();
		
		if(annotation != null) {
			Set<String> valuesSet = AnnotationUtils.getAnnotationValues(ctor, DSLConstructor.class).keySet();
			if(valuesSet.contains("value")) {
				vis = annotation.value();
			}
		}
		else if(!settings.isForwardAllConstructors()) {
			return null;
		}
		
		return new ForwardConstructor(ctor, vis);
	}
	
	private final Visibility effectiveVisibility;
	private final List<ParameterInfo> parameters;
	private final ExecutableElement ctorElement;
	
	public ForwardConstructor(ExecutableElement ctorElem, Visibility visibility) {
		this.effectiveVisibility = visibility.meet(Visibility.fromParamList(ctorElem.getParameters()));
		this.ctorElement = ctorElem;
		this.parameters = MethodUtils.getParameterInfos(ctorElement);
	}
	
	public Visibility getVisibility() {
		return effectiveVisibility;
	}
	
	public Visibility getOriginalVisibility() {
		return Visibility.of(ctorElement);
	}
	
	public List<? extends TypeMirror> getThrownTypes() {
		return ctorElement.getThrownTypes();
	}
	
	public List<? extends TypeParameterElement> getTypeParameters() {
		return ctorElement.getTypeParameters();
	}
	
	public List<? extends ParameterInfo> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

}
