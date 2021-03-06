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
package com.github.misberner.duzzt.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A named subexpression, to be used in a DSL syntax definition.
 * <p>
 * <b>Note:</b> This annotation cannot be used to annotate any source code
 * element. Its sole purpose is to be used in the value of {@link GenerateEmbeddedDSL#where()}.
 *  
 * @author Malte Isberner <malte.isberner@gmail.com>
 * 
 * @see GenerateEmbeddedDSL#syntax()
 * @see GenerateEmbeddedDSL#where()
 */
@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface SubExpr {
	/**
	 * The name of this subexpression.
	 */
	public String name();
	
	/**
	 * The definition of this subexpression. The syntax follows that of
	 * {@link GenerateEmbeddedDSL#syntax()}.
	 */
	public String definedAs();
	
	/**
	 * Specifies whether this subexpression defines its own scope. If set to {@code true},
	 * the operators <tt>^</tt>, <tt>/</tt>, and <tt>!</tt> refer to the beginning, middle,
	 * or end, respectively, of this subexpression. The default is {@code false}, meaning they
	 * refer to the nearest enclosing subexpression defining its own scope (or the top-level
	 * expression).
	 */
	public boolean ownScope() default false;
}
