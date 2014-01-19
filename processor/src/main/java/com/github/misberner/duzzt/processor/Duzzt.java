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

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STWriter;

import com.github.misberner.apcommons.reporting.Reporter;
import com.github.misberner.apcommons.util.ElementUtils;
import com.github.misberner.duzzt.DuzztDiagnosticListener;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;
import com.github.misberner.duzzt.automaton.DuzztAutomaton;
import com.github.misberner.duzzt.bricscompiler.BricsCompiler;
import com.github.misberner.duzzt.exceptions.DuzztInitializationException;
import com.github.misberner.duzzt.model.DSLSettings;
import com.github.misberner.duzzt.model.DSLSpecification;
import com.github.misberner.duzzt.model.ImplementationModel;
import com.github.misberner.duzzt.re.DuzztREUtil;
import com.github.misberner.duzzt.re.DuzztRegExp;

public class Duzzt {
	
	
	public static boolean checkExpressions(DuzztDiagnosticListener el, ImplementationModel im, DuzztRegExp re, Map<String,DuzztRegExp> subExpressions) {
		return doCheck(el, im, re, null, subExpressions, new HashMap<String,Integer>());
	}
	
	private static boolean doCheck(DuzztDiagnosticListener el, ImplementationModel im, DuzztRegExp re, String reName,
			Map<String,DuzztRegExp> subExpressions, Map<String,Integer> visited) {
		visited.put(reName, 1);
		
		
		boolean error = false;
		
		Set<String> identifiers = DuzztREUtil.findIdentifiers(re);
		for(String id : identifiers) {
			if(!im.hasActionName(id)) {
				el.undefinedIdentifier(id, reName);
				error = true;
			}
		}
		
		Set<String> subExprs = DuzztREUtil.findReferencedSubexprs(re);
		for(String se : subExprs) {
			DuzztRegExp seRe = subExpressions.get(se);
			if(seRe == null) {
				el.undefinedSubExpression(se, reName);
				error = true;
			}
			else {
				Integer state = visited.get(se);
				if(state == null) {
					error |= doCheck(el, im, seRe, se, subExpressions, visited);
				}
				else if(state == 1) {
					el.recursiveSubExpression(se);
					error = true;
				}
			}
		}
		
		visited.put(reName, 2);
		
		return error;
	}
	
	private static final String ST_ENCODING = "UTF-8";
	private static final char ST_DELIM_START_CHAR = '<';
	private static final char ST_DELIM_STOP_CHAR = '>';
	
	private static final String ST_RESOURCE_NAME = "/stringtemplates/edsl-source.stg";
	
	private static final String ST_MAIN_TEMPLATE_NAME = "edsl_source";
	
	private STGroup sourceGenGroup;
	
	/**
	 * Default constructor.
	 */
	public Duzzt() {
	}
	
	public boolean isInitialized() {
		return (sourceGenGroup != null);
	}
	
	/**
	 * Initialize the Duzzt embedded DSL generator.
	 * 
	 * @param reporter the reporter used to report errors
	 * @throws DuzztInitializationException if a fatal error occurs during initialization
	 */
	public void init(Reporter reporter) throws DuzztInitializationException {
		URL url = GenerateEDSLProcessor.class.getResource(ST_RESOURCE_NAME);
		
		this.sourceGenGroup = new STGroupFile(
				url,
				ST_ENCODING,
				ST_DELIM_START_CHAR,
				ST_DELIM_STOP_CHAR);
		
		sourceGenGroup.setListener(new ReporterDiagnosticListener(reporter));
		
		sourceGenGroup.load();
		if(!sourceGenGroup.isDefined(ST_MAIN_TEMPLATE_NAME)) {
			sourceGenGroup = null;
			throw new DuzztInitializationException("Could not find main template '"
					+ ST_MAIN_TEMPLATE_NAME + "' in template group file " + url.toString());
		}
	}
	
	/**
	 * Process an annotated element.
	 * 
	 * @param elem the element to process (must be class or interface)
	 * @param annotation the annotation specifying the EDSL
	 * @param elementUtils {@link javax.lang.model} element utilities class
	 * @param typeUtils {@link javax.lang.model} type utilities
	 * @param filer the {@link Filer} used to write output files
	 * @param reporter reporter for error and warning reporting
	 * 
	 * @throws IOException if writing the generated source code fails 
	 */
	public void process(Element elem, GenerateEmbeddedDSL annotation,
			Elements elementUtils, Types typeUtils, Filer filer, Reporter reporter) throws IOException {
		
		if(!ElementUtils.checkElementKind(elem, ElementKind.CLASS, ElementKind.INTERFACE)) {
			throw new IllegalArgumentException("Annotation " + GenerateEmbeddedDSL.class.getSimpleName()
					+ " can only be used on class or interface declarations!");
		}
		
		TypeElement te = (TypeElement)elem;
		
		ReporterDiagnosticListener dl = new ReporterDiagnosticListener(reporter);
		
		DSLSettings settings = new DSLSettings(annotation);
		
		DSLSpecification spec = DSLSpecification.create(te, settings, elementUtils, typeUtils);
		
		BricsCompiler compiler = new BricsCompiler(spec.getImplementation());
		
		DuzztAutomaton automaton = compiler.compile(spec.getDSLSyntax(), spec.getSubExpressions());
		
		render(spec, automaton, filer, dl);
	}
	
	private String isoDateFormat(Date date) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		fmt.setTimeZone(tz);
		return fmt.format(date);
	}
	
	private void render(DSLSpecification spec, DuzztAutomaton automaton,
			Filer filer, ReporterDiagnosticListener diagnosticListener) throws IOException {
		ST tpl = sourceGenGroup.getInstanceOf(ST_MAIN_TEMPLATE_NAME);
		tpl.add("spec", spec);
		tpl.add("automaton", automaton);
		tpl.add("generatorClass", getClass());
		tpl.add("generationDate", isoDateFormat(new Date()));
		
		JavaFileObject jfo = filer.createSourceFile(spec.getQualifiedClassName(), spec.getImplementation().getType());
		try(BufferedWriter w = new BufferedWriter(jfo.openWriter())) {
			STWriter stWriter = new AutoIndentWriter(w);
			tpl.write(stWriter, diagnosticListener);
		}
	}
}
