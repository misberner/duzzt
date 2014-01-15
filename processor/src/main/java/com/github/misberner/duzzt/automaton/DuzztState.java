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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.misberner.duzzt.DuzztAction;


/**
 * A state in a {@link DuzztAutomaton}.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class DuzztState {
	
	private final int id;
	private final Map<DuzztAction,DuzztTransition> transitions = new HashMap<>();
	private boolean initial = false;
	
	/**
	 * Constructor.
	 * @param stateId the unique id of the state
	 */
	public DuzztState(int stateId) {
		this.id = stateId;
	}
	
	
	/**
	 * Retrieves the ID of this state.
	 * @return the ID of this state
	 */
	public int getId() {
		return id;
	}
	
	
	void setInitial(boolean initial) {
		this.initial = initial;
	}
	
	public boolean isInitial() {
		return initial;
	}

	/**
	 * Adds a transition to this state.
	 * @param action the action on which to trigger this transition
	 * @param succ the successor state
	 */
	public void addTransition(DuzztAction action, DuzztState succ) {
		transitions.put(action, new DuzztTransition(action, succ));
	}
	
	/**
	 * Retrieves the transition triggered by a given action. If no transition
	 * is triggered by this action, <tt>null</tt> is returned.
	 * @param action the action
	 * @return the transition triggered by the specified action, or <tt>null</tt>
	 */
	public DuzztTransition getTransition(DuzztAction action) {
		return transitions.get(action);
	}
	
	/**
	 * Retrieves the successor state reached via a given action. If no transition
	 * is triggered by this action, <tt>null</tt> is returned.
	 * @param action the action
	 * @return the successor state reached via the specified action, or <tt>null</tt>
	 */
	public DuzztState getSuccessor(DuzztAction action) {
		DuzztTransition t = getTransition(action);
		if(t == null) {
			return null;
		}
		return t.getSuccessor();
	}
	
	/**
	 * Retrieves the set of all actions which trigger a transition from this state.
	 * @return all actions which trigger a transition
	 */
	public Set<DuzztAction> getActions() {
		return transitions.keySet();
	}
	
	/**
	 * Retrieves the set of all transitions from this state.
	 * @return all transitions from this state
	 */
	public Collection<DuzztTransition> getTransitions() {
		return transitions.values();
	}
}
