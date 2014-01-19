package com.github.misberner.duzzt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.misberner.apcommons.util.Visibility;

/**
 * Configures the role of an implementation class constructor. This annotation allows
 * to override the visibility of forward constructors set using
 * {@link GenerateEmbeddedDSL#forwardConstructorVisibility()}, and allows to include 
 * <p>
 * It is illegal to use the {@link DSLConstructor} annotation on the constructor of a class
 * or interface not annotated with a {@link GenerateEmbeddedDSL} annotation. Furthermore,
 * it is discouraged to use this annotation on constructors of an interface, abstract class,
 * or non-static inner class. 
 *  
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.SOURCE)
public @interface DSLConstructor {
	/**
	 * The visibility of the forwarded constructor for the annotated
	 * constructor. To effectively prevent this constructor from being
	 * forwarded, set this to {@link Visibility#PRIVATE}. If not set,
	 * defaults to {@link GenerateEmbeddedDSL#forwardConstructorVisibility()}.
	 */
	public Visibility value() default Visibility.PUBLIC;
}
