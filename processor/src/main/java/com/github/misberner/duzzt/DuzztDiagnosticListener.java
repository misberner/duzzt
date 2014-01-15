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
package com.github.misberner.duzzt;

/**
 * Listener interface for diagnostic messages emitted by Duzzt.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public interface DuzztDiagnosticListener {
	
	/**
	 * Called if an expression could not be parsed.
	 * <p>
	 * If the parse error occurred while parsing a named subexpression, the name
	 * of the subexpression will also be passed as the <tt>exprName</tt> parameter.
	 * Otherwise (that is, if the main expression could not be parsed), <tt>exprName</tt>
	 * will be <tt>null</tt>.
	 * 
	 * @param parseException the exception that was thrown while trying to parse
	 * @param exprName the name of the named subexpression, or <tt>null</tt> if the parse
	 * error occurred while parsing the main expression.
	 */
	void unparseableExpression(Exception parseException, String exprName);
	
	/**
	 * Called if an undefined identifier was referenced.
	 * 
	 * @param id the undefined identifier that was referenced
	 * @param exprName the name of the named subexpression, or <tt>null</tt> if the parse
	 * error occurred while parsing the main expression.
	 */
	void undefinedIdentifier(String id, String exprName);
	
	/**
	 * Called if an undefined subexpression was referenced.
	 * 
	 * @param subExprName the name of the undefined subexpression that was referenced
	 * @param exprName the name of the named subexpression, or <tt>null</tt> if the parse
	 * error occurred while parsing the main expression.
	 */
	void undefinedSubExpression(String subExprName, String exprName);
	
	/**
	 * Called if a subexpression was defined recursively.
	 * 
	 * @param subExprName the name of the subexpression that was defined recursively
	 */
	void recursiveSubExpression(String subExprName);
	
	/**
	 * Called if a defined named subexpression was never used.
	 * 
	 * @param subExprName the name of the subexpression that was never used
	 */
	void unusedSubExpression(String subExprName);
	
	
	/**
	 * Called when a generic information message is emitted.
	 * 
	 * @param args the constituents of the message
	 */
	void info(Object... args);
	
	/**
	 * Called when a generic warning message is emitted.
	 * 
	 * @param args the constituents of the message
	 */
	void warning(Object... args);
	
	/**
	 * Called when a generic error message is emitted.
	 * 
	 * @param args the constituents of the message
	 */
	void error(Object... args);
}
