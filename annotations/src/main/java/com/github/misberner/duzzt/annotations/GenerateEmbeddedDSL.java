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
package com.github.misberner.duzzt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.misberner.apcommons.util.AFModifier;
import com.github.misberner.apcommons.util.Visibility;

/**
 * Generate an embedded DSL from the target class or interface.
 * <p>
 * 
 * <h3><a name="auto_varargs">Automatic Variable Arguments Overloads</a></h3>
 * 
 * <h3>Regular Expression Syntax</h3>
 * The regular expression syntax used by Duzzt follows common rules. Concatenation of regular
 * expressions is declared implicitly by absence of any other operators, while a pipe character
 * (<tt>|</tt>) is used to separate alternatives (<i>union</i>). Modifiers <tt>*</tt>, <tt>+</tt>
 * or <tt>?</tt> are used to specify that the preceding regular expression must occur an arbitrary
 * number of times (including zero) (<tt>*</tt>), an arbitrary number of times, but at least once
 * (<tt>+</tt>), or zero or one times (<tt>?</tt>).
 * 
 * <h4><a name="named_subexpr">Named Subexpressions</a></h4>
 * To avoid having to repeat common subexpressions that occur multiple times, <i>named subexpressions</i>
 * can be introduced. A named subexpression serves as a placeholder for a regular expression.
 * Named subexpressions are declared in the {@link #where()} field. They can be referenced
 * by enclosing their name in angle brackets (<tt>&lt;&gt;</tt>). Named subexpressions
 * can be referenced by other named subexpressions, but their must not be any cyclic dependencies
 * between named subexpressions.
 * <p>
 * <h4>Precedence rules</h4>
 * The precedence rules are as follows: a modifier operator has precedence over any other operators,
 * concatenation has precedence over union. Parentheses (<tt>()</tt>) can be used to explicitly
 * override these precedence rules.
 * <p>
 * <h4>Syntax Definition</h4>
 * The following BNF describes the syntax of Duzzt Regular Expressions.
 * 
 * <table>
 * <tr>
 * <td><i>regex</i></td><td>::=</td><td><i>union-regex</i></td>
 * </tr>
 * <tr>
 * <td><i>union-regex</i></td><td>::=</td><td><i>concat-regex</i> (<tt>|</tt> <i>concat-regex</i>)<sup>*</sup></td>
 * </tr>
 * <tr>
 * <td><i>concat-regex</i></td><td>::=</td><td><i>mod-regex</i> <i>mod-regex</i><sup>*</sup></td>
 * </tr>
 * <td><i>mod-regex</i></td><td>::=</td><td><i>mod-regex</i> (<tt>?</tt>|<tt>*</tt>|<tt>+</tt>) | <i>atomic-regex</i></td>
 * </tr>
 * <tr>
 * <td><i>atomic-regex</i></td><td>::=</td><td><tt>(</tt> <i>regex</i> <tt>)</tt> | <i>identifier</i> | <tt>&lt;</tt> <i>identifier</i> <tt>&gt;</tt></td>
 * </tr>
 * </table>
 * <p>
 * An <i>identifier</i> is a string consisting of alphanumeric characters and <tt>_</tt> only, and
 * beginning with an alphabetic character or <tt>_</tt>.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateEmbeddedDSL {
	/**
	 * The simple (i.e., unqualified) name of the EDSL class to generate. This is a
	 * mandatory option.
	 */
	public String name();
	
	/**
	 * The package in which the generated EDSL class will be placed.
	 * <p>
	 * The value is interpreted as a package reference relative to the
	 * implementation class annotated with this annotation:
	 * <ul>
	 * <li>if the value is <tt>"."</tt>, the resulting package name is the same
	 * as that of the implementation class. This is the default.</li>
	 * <li>if the value starts with a dot, followed by a legal Java package name,
	 * that package name is appended to the package name of the implementation class,
	 * forming the result package name.</li>
	 * <li>otherwise, the value is taken as the package name as-is.
	 * </ul>
	 * <p>
	 * The default value is {@code "."}.
	 */
	public String packageName() default ".";
	
	/**
	 * The syntax of the embedded DSL, specified as a regular expression. This is a mandatory
	 * option.
	 * <p>
	 * See above for a syntax definition for regular expressions.
	 */
	public String syntax();
	
	/**
	 * Specifies whether the generated DSL class should be declared as {@code public}. If set
	 * to {@code false}, it will have package-private visibility.
	 * <p>
	 * The default setting is {@code true}.
	 */
	public boolean classPublic() default true;
	
	/**
	 * Specifies whether the generated DSL class should be declared as {@code final}. If set to
	 * {@code false}, it will be non-{@code final}.
	 * <p>
	 * The default setting is {@code false}.
	 */
	public AFModifier modifier() default AFModifier.DEFAULT;
	
	/**
	 * Named subexpressions that can be used in the embedded DSL syntax definition.
	 * <p>
	 * Named subexpressions are referenced by their name, enclosed in angle brackets (<tt>&lt;</tt>
	 * and <tt>&gt;</tt>). Subexpressions may reference other subexpressions, but there must
	 * not be any cyclic dependencies between subexpressions.
	 * <p>
	 * Subexpressions are specified as regular expressions. See above for
	 * a syntax definition for regular expressions.
	 * <p>
	 * The default value is <tt>{}</tt>, meaning that no subexpressions are declared.
	 */
	public SubExpr[] where() default {};
	
	/**
	 * Automatically generate variable argument overloads for all suitable
	 * methods.
	 * <p>
	 * The global behavior can be overridden on a per-method basis by setting
	 * {@link DSLAction#autoVarArgs()}.
	 * <p>
	 * The default setting is {@code true}.
	 */
	public boolean autoVarArgs() default true;
	
	/**
	 * <p>
	 * The default setting is {@code false}.
	 */
	public boolean nonVoidTerminators() default false;
	
	/**
	 * Automatically treat all methods (excluding those defined by the
	 * {@link Object} class) as actions of the embedded DSL. If set to {@code false},
	 * only those methods annotated with a {@link DSLAction} annotation and have
	 * {@link DSLAction#disable()} set to {@code false} will be available as
	 * DSL actions.
	 * <p>
	 * The default setting is {@code true}.
	 */
	public boolean enableAllMethods() default true;
	
	/**
	 * Consider inherited methods as potential DSL actions. If set to {@code false},
	 * only those methods directly declared in this class will be regarded as potential
	 * DSL actions.
	 * <p>
	 * The default setting is {@code true}.
	 */
	public boolean includeInherited() default true;
	
	
	/**
	 * Controls whether to automatically generate forwards for all visible constructors.
	 * If set to {@code false}, only those constructors annotated with {@link DSLConstructor}
	 * will be forwarded. If set to {@code true}, forwarding of a single constructor can be
	 * effectively prevented by annotating it with {@link DSLConstructor} and setting
	 * {@link DSLConstructor#value()} to {@link Visibility#PRIVATE}.
	 * <p>
	 * The default setting is {@code true}.
	 */
	public boolean forwardAllConstructors() default true;
	
	/**
	 * Controls the visibility of the delegate constructor, i.e., the constructor which takes
	 * an instance of the DSL implementation as argument. If no such constructor should be visible,
	 * set this to {@link Visibility#PRIVATE}. Note that the visibility of the generated
	 * delegate constructor will never be higher than that of the DSL implementation.
	 * <p>
	 * The default setting is {@link Visibility#PUBLIC}.
	 */
	public Visibility delegateConstructorVisibility() default Visibility.PUBLIC;
	
	public Visibility forwardConstructorVisibility() default Visibility.PUBLIC;

	/**
	 * A flag for controlling whether or not the generated DSL class should be annotated with a
	 * <a href="https://docs.oracle.com/javase/8/docs/api/javax/annotation/Generated.html">javax.annotation.Generated</a>
	 * (for Java 8 or lower) or a
	 * <a href="https://docs.oracle.com/javase/9/docs/api/javax/annotation/processing/Generated.html">javax.annotation.processing.Generated</a>
	 * (Java 9 or newer) annotation.
	 * <p>
	 * When cross-version-compiling (e.g. compiling on Java 8 for Java 9+ environments) you may want to skip this
	 * annotation for compatibility reasons.
	 * <p>
	 * The default behavior is to add the respective annotation.
	 */
	public boolean skipGeneratedAnnotation() default false;
}
