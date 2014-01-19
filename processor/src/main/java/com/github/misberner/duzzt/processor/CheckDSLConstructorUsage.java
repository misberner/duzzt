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
package com.github.misberner.duzzt.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.github.misberner.apcommons.processing.AbstractSingleAnnotationProcessor;
import com.github.misberner.apcommons.processing.exceptions.ProcessingException;
import com.github.misberner.apcommons.reporting.AnnotationReporter;
import com.github.misberner.apcommons.util.APUtils;
import com.github.misberner.apcommons.util.types.TypeUtils;
import com.github.misberner.duzzt.annotations.DSLConstructor;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;

final class CheckDSLConstructorUsage extends
		AbstractSingleAnnotationProcessor<DSLConstructor> {

	public CheckDSLConstructorUsage() {
		super(DSLConstructor.class);
	}

	@Override
	public void process(Element elem, AnnotationMirror annotationMirror,
			DSLConstructor annotation, APUtils utils) throws Exception,
			ProcessingException {
		Element enclosing = elem.getEnclosingElement();
		
		if(!(enclosing instanceof TypeElement)) {
			throw new IllegalStateException("Enclosing element of constructor is not a type element");
		}
		
		TypeElement enclosingType = (TypeElement)enclosing;
		
		GenerateEmbeddedDSL generateDslAnnotation
			= enclosingType.getAnnotation(GenerateEmbeddedDSL.class);
		
		AnnotationReporter rep = utils.getReporter(elem, DSLConstructor.class);
		
		if(generateDslAnnotation == null) {
			rep.error("Enclosing class or interface of this constructor must be annotated ",
					"with @", GenerateEmbeddedDSL.class.getSimpleName(), ", or remove @",
					DSLConstructor.class.getSimpleName(), " annotation");
		}
		else if(TypeUtils.isStandaloneInstantiable(enclosingType)) {
			rep.warning("@", DSLConstructor.class.getSimpleName(), " used on constructors of ",
					"an interface, abstract class, or non-static inner class has no effect");
		}
	}
	
	

}
