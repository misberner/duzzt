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
package com.github.misberner.duzzt.model;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.misberner.apcommons.util.AFModifier;
import com.github.misberner.apcommons.util.ElementUtils;
import com.github.misberner.apcommons.util.NameUtils;
import com.github.misberner.apcommons.util.Visibility;
import com.github.misberner.duzzt.re.DuzztRegExp;

public class DSLSpecification {
	
	
	public static DSLSpecification create(
			TypeElement type,
			DSLSettings settings,
			Elements elementUtils,
			Types typeUtils) {
		ImplementationModel model = ImplementationModel.create(type, settings, elementUtils, typeUtils);
		
		return new DSLSpecification(settings, model);
	}
	
	private final DSLSettings settings;
	private final String packageName;
	private final boolean samePackage;
	
	private final ImplementationModel implementation;
	
	
	private DSLSpecification(DSLSettings settings, ImplementationModel model) {
		
		this.settings = settings;
		String implPackage = ElementUtils.getPackageName(model.getType());
		this.packageName = NameUtils.resolvePackageName(settings.getPackageRef(), implPackage);
		this.samePackage = implPackage.equals(packageName);
	
		this.implementation = model;
	}
	
	public List<ForwardConstructor> getForwardConstructors() {
		Visibility minVis = (samePackage) ? Visibility.PACKAGE_PRIVATE : Visibility.PUBLIC;
		return implementation.getForwardConstructors(minVis);
	}
	
	
	public DuzztRegExp getDSLSyntax() {
		return settings.getSyntax();
	}
	
	public Map<String,SubExpression> getSubExpressions() {
		return settings.getSubExpressions();
	}
	
	public Visibility getDelegateConstructorVisibility() {
		return Visibility.of(implementation.getType()).meet(settings.getDelegateConstructorVisibility());
	}
	
	public ImplementationModel getImplementation() {
		return implementation;
	}
	
	
	public String getClassName() {
		return settings.getName();
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getNonDefaultPackageName() {
		return packageName.isEmpty() ? null : packageName;
	}
	
	public String getQualifiedClassName() {
		if(packageName.isEmpty()) {
			return settings.getName();
		}
		return packageName + "." + settings.getName();
	}

	public boolean isClassPublic() {
		return settings.isClassPublic();
	}

	public AFModifier getModifier() {
		return settings.getModifier();
	}


}
