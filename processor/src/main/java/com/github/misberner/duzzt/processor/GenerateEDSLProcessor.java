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

import com.github.misberner.apcommons.processing.AbstractSingleAnnotationProcessor;
import com.github.misberner.apcommons.processing.exceptions.ProcessingException;
import com.github.misberner.apcommons.reporting.Reporter;
import com.github.misberner.apcommons.util.APUtils;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;

/**
 * The processor that processes {@link GenerateEmbeddedDSL} annotations.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
final class GenerateEDSLProcessor extends AbstractSingleAnnotationProcessor<GenerateEmbeddedDSL> {
	
	private final Duzzt duzzt = new Duzzt();

	/**
	 * Constructor.
	 */
	public GenerateEDSLProcessor() {
		super(GenerateEmbeddedDSL.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.AbstractSingleAnnotationProcessor#pre(com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void pre(APUtils utils) throws Exception, ProcessingException {
		duzzt.init(utils.getReporter());
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.AbstractSingleAnnotationProcessor#process(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror, java.lang.annotation.Annotation, com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void process(Element elem, AnnotationMirror annotationMirror,
			GenerateEmbeddedDSL annotation, APUtils utils) throws Exception,
			ProcessingException {
		
		assert duzzt.isInitialized();
		
		Reporter reporter = utils.getReporter(elem, annotationMirror);
		
		duzzt.process(elem, annotation, utils.getElementUtils(), utils.getTypeUtils(), utils.getFiler(), reporter);
	}
	
	
	
	
}
