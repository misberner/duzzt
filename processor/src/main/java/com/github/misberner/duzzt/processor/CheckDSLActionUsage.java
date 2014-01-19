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

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.github.misberner.apcommons.processing.AbstractSingleAnnotationProcessor;
import com.github.misberner.apcommons.processing.exceptions.ProcessingException;
import com.github.misberner.apcommons.reporting.AnnotationReporter;
import com.github.misberner.apcommons.util.APUtils;
import com.github.misberner.apcommons.util.annotations.AnnotationUtils;
import com.github.misberner.apcommons.util.methods.MethodUtils;
import com.github.misberner.duzzt.annotations.DSLAction;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;

/**
 * Processor for checking the usage of {@link DSLAction} annotations.
 * <p>
 * This processor does not generate any code. It merely checks the usage of {@link DSLAction}
 * annotations and issues errors or warning in case of illegal or discouraged behavior.
 * <p>
 * The {@link DSLAction} annotation is used illegally if:
 * <ul>
 * <li>the enclosing type is not annotated with a {@link GenerateEmbeddedDSL} annotation.</li>
 * <lI>it is used on an override of one of the methods defined by the {@link Object} class.</li>
 * </ul>
 * Furthermore, it is discouraged to specify any other values if {@link DSLAction#enable()}
 * is set to {@code false}.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
final class CheckDSLActionUsage extends AbstractSingleAnnotationProcessor<DSLAction> {
	
	public CheckDSLActionUsage() {
		super(DSLAction.class);
	}

	@Override
	public void process(Element elem, AnnotationMirror annotationMirror,
			DSLAction annotation, APUtils utils) throws Exception,
			ProcessingException {
		if(!(elem instanceof ExecutableElement)) {
			throw new IllegalStateException("Method-only annotation " + DSLAction.class.getSimpleName()
					+ " used on non-method element");
		}
		ExecutableElement methodElem  = (ExecutableElement)elem;
		
		Element enclosing = methodElem.getEnclosingElement();
		
		if(!(enclosing instanceof TypeElement)) {
			throw new IllegalStateException("Enclosing element of method is not a type element");
		}
		
		TypeElement enclosingType = (TypeElement)enclosing;
		
		GenerateEmbeddedDSL generateDslAnnotation
			= enclosingType.getAnnotation(GenerateEmbeddedDSL.class);
		
		AnnotationReporter rep = utils.getReporter(methodElem, DSLAction.class);
		
		if(generateDslAnnotation == null) {
			rep.error("Enclosing class or interface of this method must be annotated ",
					"with @", GenerateEmbeddedDSL.class.getSimpleName(), ", or remove @",
					DSLAction.class.getSimpleName(), " annotation");
		}
		else if(!annotation.enable()) {
			Map<String,AnnotationValue> values = AnnotationUtils.getAnnotationValues(annotationMirror);
			for(Map.Entry<String,AnnotationValue> e : values.entrySet()) {
				String name = e.getKey();
				if("enable".equals(name)) {
					continue;
				}
				rep.forValue(e.getValue()).warning("DSL action is disabled. Value of '", name,
						"' will be ignored");
			}
		}
		else if(MethodUtils.isObjectMethod(methodElem)) {
			rep.error("Using method '", methodElem.getSimpleName(), "' defined by Object class ",
					"as DSL action is illegal");
		}
	}
	
	
}
