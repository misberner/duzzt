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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Explicitly declares a method as being available as a EDSL action. Furthermore,
 * this option allows to further configure the role of this action in the EDSL.
 * <p>
 * The {@link DSLAction} annotation is used illegally if:
 * <ul>
 * <li>the enclosing type is not annotated with a {@link GenerateEmbeddedDSL} annotation.</li>
 * <lI>it is used on an override of one of the methods defined by the {@link Object} class.</li>
 * </ul>
 * Furthermore, it is discouraged to explicitly specify any other values if {@link #enable()}
 * is set to {@code false}.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface DSLAction {
	
	/**
	 * Enables this method as a DSL action. Defaults to {@code true}. If set to
	 * {@code false}, it is discouraged to explicitly set any other values for
	 * this annotation.
	 */
	public boolean enable() default true;
	
	
	public boolean global() default false;
	
	public boolean terminator() default false;
	
	public boolean autoVarArgs() default true;
	
}
