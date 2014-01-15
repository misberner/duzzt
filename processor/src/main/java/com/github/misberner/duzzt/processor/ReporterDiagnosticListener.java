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

import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;

import com.github.misberner.apcommons.reporting.Reporter;
import com.github.misberner.duzzt.DuzztDiagnosticListener;

public class ReporterDiagnosticListener implements DuzztDiagnosticListener, STErrorListener {
	
	private final Reporter reporter;
	
	private static final String prefix(String exprName) {
		if(exprName == null) {
			return "";
		}
		return "In subexpression <" + exprName + ">: ";
	}
	
	public ReporterDiagnosticListener(Reporter reporter) {
		this.reporter = reporter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#unparseableExpression(java.lang.Exception, java.lang.String)
	 */
	@Override
	public void unparseableExpression(Exception parseException, String exprName) {
		reporter.error(prefix(exprName), "Could not parse regular expression: ", parseException.getMessage());
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#undefinedIdentifier(java.lang.String, java.lang.String)
	 */
	@Override
	public void undefinedIdentifier(String id, String exprName) {
		reporter.error(prefix(exprName), "Undefined identifier '", id, "'");
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#undefinedSubExpression(java.lang.String, java.lang.String)
	 */
	@Override
	public void undefinedSubExpression(String subExprName, String exprName) {
		reporter.error(prefix(exprName), "Undefined subexpression <", subExprName, ">");
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#recursiveSubExpression(java.lang.String)
	 */
	@Override
	public void recursiveSubExpression(String subExprName) {
		reporter.error("Subexpression <", subExprName, "> is recursively defined");
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#unusedSubExpression(java.lang.String)
	 */
	@Override
	public void unusedSubExpression(String subExprName) {
		reporter.warning("Subexpression <", subExprName, "> is never referenced");
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#info(java.lang.Object[])
	 */
	@Override
	public void info(Object... args) {
		reporter.note(args);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#warning(java.lang.Object[])
	 */
	@Override
	public void warning(Object... args) {
		reporter.warning(args);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.DuzztDiagnosticListener#error(java.lang.Object[])
	 */
	@Override
	public void error(Object... args) {
		reporter.error(args);
	}

	/*
	 * (non-Javadoc)
	 * @see org.stringtemplate.v4.STErrorListener#compileTimeError(org.stringtemplate.v4.misc.STMessage)
	 */
	@Override
	public void compileTimeError(STMessage msg) {
		reporter.error("StringTemplate compile-time error: ", msg.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.stringtemplate.v4.STErrorListener#runTimeError(org.stringtemplate.v4.misc.STMessage)
	 */
	@Override
	public void runTimeError(STMessage msg) {
		reporter.error("StringTemplate run-time error: ", msg.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.stringtemplate.v4.STErrorListener#IOError(org.stringtemplate.v4.misc.STMessage)
	 */
	@Override
	public void IOError(STMessage msg) {
		reporter.error("StringTemplate I/O error: ", msg.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.stringtemplate.v4.STErrorListener#internalError(org.stringtemplate.v4.misc.STMessage)
	 */
	@Override
	public void internalError(STMessage msg) {
		reporter.error("StringTemplate internal error: " + msg.toString());
	}

}
