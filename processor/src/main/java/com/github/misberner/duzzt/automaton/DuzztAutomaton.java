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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The automaton which recognizes the (regular) embedded DSL.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class DuzztAutomaton {
	
	private final List<DuzztState> states;
	private final DuzztState init;
	
	
	/**
	 * Constructor. Constructs a Duzzt automaton from a Brics automaton, using
	 * the specified mapping from characters to actions.
	 * 
	 * @param bricsAutomaton the Brics automaton
	 * @param actions the character to action mapping
	 */
	public DuzztAutomaton(Collection<? extends DuzztState> states, DuzztState init) {
		if(!states.contains(init)) {
			throw new IllegalArgumentException();
		}
		this.states = new ArrayList<>(states);
		this.init = init;
		this.init.setInitial(true);
	}
	
	/**
	 * Retrieves the initial state.
	 * @return the initial state
	 */
	public DuzztState getInitialState() {
		return init;
	}
	
	/**
	 * Retrieves a list of all states.
	 * @return the list of all states
	 */
	public List<DuzztState> getStates() {
		return states;
	}
}
