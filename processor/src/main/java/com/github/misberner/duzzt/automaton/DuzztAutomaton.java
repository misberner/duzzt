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
package com.github.misberner.duzzt.automaton;

import com.github.misberner.duzzt.DuzztAction;

import javax.lang.model.util.Types;
import java.util.*;


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
	 * Constructor. Initializes a Duzzt automaton from a collection of states
	 * and an initial states.
	 * 
	 * @param states the states of the automaton
	 * @param init the initial state of the automaton, must be part of {@code states}
	 * @throws IllegalArgumentException if {@code states} does not contain {@code init}.
	 */
	public DuzztAutomaton(Collection<? extends DuzztState> states, DuzztState init)
			throws IllegalArgumentException {
		if(!states.contains(init)) {
			throw new IllegalArgumentException("Initial state not contained in state set");
		}
		this.states = new ArrayList<>(states);
		this.init = init;
		this.init.setInitial(true);
	}


	public void reassignStateIds(Types types) {
		for(DuzztState s : states) {
			s.setId(-1);
		}

		List<DuzztAction> sortedActions = new ArrayList<>(getAllActions());
		Comparator<DuzztAction> actionCmp = new DuzztAction.ActionComparator(types);
		Collections.sort(sortedActions, actionCmp);

		Queue<DuzztState> queue = new ArrayDeque<>();

		int id = 0;
		queue.add(init);
		init.setId(0);

		while(!queue.isEmpty()) {
			DuzztState curr = queue.poll();

			for(DuzztAction a : sortedActions) {
				DuzztState succ = curr.getSuccessor(a);
				if(succ != null && succ.getId() == -1) {
					queue.add(succ);
					succ.setId(id++);
				}
			}
		}
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

	public Set<DuzztAction> getAllActions() {
		Set<DuzztAction> result = new HashSet<>();
		for(DuzztState s : states) {
			result.addAll(s.getActions());
		}
		return result;
	}
}
