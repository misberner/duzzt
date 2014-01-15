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
package com.github.misberner.duzzt.automaton;

import com.github.misberner.duzzt.DuzztAction;
import com.github.misberner.duzzt.annotations.DSLAction;
import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;

/**
 * A transition in a {@link DuzztAutomaton}.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class DuzztTransition {

	private final DuzztAction action;
	private final DuzztState successor;
	
	/**
	 * Constructor.
	 * @param action the action which triggers this transition
	 * @param successor the successor state (can be <tt>null</tt>
	 * for terminating transitions)
	 */
	public DuzztTransition(DuzztAction action, DuzztState successor) {
		this.action = action;
		this.successor = successor;
	}
	
	/**
	 * Retrieves the action which triggers this transition.
	 * @return the action which triggers this transition
	 */
	public DuzztAction getAction() {
		return action;
	}
	
	/**
	 * Retrieves the successor state of this transition; or <tt>null</tt>
	 * if this is a terminating transition.
	 * @return the successor state or <tt>null</tt>
	 */
	public DuzztState getSuccessor() {
		return successor;
	}
	
	/**
	 * Checks whether a varargs overload can be automatically created for this method.
	 * <p>
	 * This is the case iff all of the following conditions hold:
	 * <ul>
	 * <li>this transition is not a terminating transition,</li>
	 * <li>the action does not prohibit auto varargs creation (e.g., because it is
	 * nullary and auto varargs creation has been disable through {@link GenerateEmbeddedDSL#autoVarArgs()}
	 * or {@link DSLAction#autoVarArgs()},</li>
	 * <li>the action is defined and reflexive in the successor state</li>
	 * </ul>
	 * @return <tt>true</tt> if a varargs overload can be automatically created for this method,
	 * <tt>false</tt> otherwise.
	 */
	public boolean isVarArgsApplicable() {
		if(successor == null || !action.isAutoVarArgs()) {
			return false;
		}
		return (successor == successor.getSuccessor(action));
	}
	

}
