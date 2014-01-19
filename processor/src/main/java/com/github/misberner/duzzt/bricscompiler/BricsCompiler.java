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
package com.github.misberner.duzzt.bricscompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.misberner.duzzt.DuzztAction;
import com.github.misberner.duzzt.DuzztCompiler;
import com.github.misberner.duzzt.automaton.DuzztAutomaton;
import com.github.misberner.duzzt.automaton.DuzztState;
import com.github.misberner.duzzt.exceptions.RecursiveSubExpressionException;
import com.github.misberner.duzzt.exceptions.UndefinedIdentifierException;
import com.github.misberner.duzzt.exceptions.UndefinedSubExpressionException;
import com.github.misberner.duzzt.model.ImplementationModel;
import com.github.misberner.duzzt.model.SubExpression;
import com.github.misberner.duzzt.re.DuzztREAlt;
import com.github.misberner.duzzt.re.DuzztREConcat;
import com.github.misberner.duzzt.re.DuzztREEnd;
import com.github.misberner.duzzt.re.DuzztREIdentifier;
import com.github.misberner.duzzt.re.DuzztREInner;
import com.github.misberner.duzzt.re.DuzztREModifier;
import com.github.misberner.duzzt.re.DuzztRENonEmpty;
import com.github.misberner.duzzt.re.DuzztREStart;
import com.github.misberner.duzzt.re.DuzztRESubexpr;
import com.github.misberner.duzzt.re.DuzztREUtil;
import com.github.misberner.duzzt.re.DuzztREVisitor;
import com.github.misberner.duzzt.re.DuzztRegExp;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.StatePair;
import dk.brics.automaton.Transition;

/**
 * Regular expression compiler utilizing the
 * <a href="http://www.brics.dk/automata/">Brics Automata Library</a>.
 *  
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class BricsCompiler implements DuzztCompiler {
	
	private class RETranslator implements DuzztREVisitor<Void, StringBuilder> {
		@Override
		public Void visit(DuzztREAlt re, StringBuilder sb) {
			sb.append('(');
			Iterator<? extends DuzztRegExp> childIt = re.getChildren().iterator();
			childIt.next().accept(this, sb);
			while(childIt.hasNext()) {
				sb.append('|');
				childIt.next().accept(this, sb);
			}
			sb.append(')');
			return null;
		}
		@Override
		public Void visit(DuzztREConcat re, StringBuilder sb) {
			for(DuzztRegExp child : re.getChildren()) {
				child.accept(this, sb);
			}
			return null;
		}
		@Override
		public Void visit(DuzztREIdentifier re, StringBuilder sb) {
			CharRange range = id2range.get(re.getName());
			if(range == null) {
				throw new UndefinedIdentifierException(re.getName());
			}
			appendRange(range.getLow(), range.getHigh(), sb);
			return null;
		}
		@Override
		public Void visit(DuzztREModifier re, StringBuilder sb) {
			sb.append('(');
			re.getSub().accept(this, sb);
			sb.append(')');
			sb.append(re.getModChar());
			return null;
		}
		@Override
		public Void visit(DuzztRESubexpr re, StringBuilder sb) {
			sb.append('<');
			sb.append(re.getSubexprName());
			sb.append('>');
			return null;
		}
		@Override
		public Void visit(DuzztRENonEmpty re, StringBuilder sb) {
			sb.append("((");
			re.getSub().accept(this, sb);
			sb.append(")&~())");
			return null;
		}
		@Override
		public Void visit(DuzztREStart re, StringBuilder sb) {
			appendRaw(startChar, sb);
			return null;
		}
		@Override
		public Void visit(DuzztREEnd re, StringBuilder sb) {
			appendRaw(endChar, sb);
			return null;
		}
		@Override
		public Void visit(DuzztREInner re, StringBuilder sb) {
			appendRaw(innerChar, sb);
			return null;
		}
		
	}
	
	private final ImplementationModel impl;
	
	private final Map<String,CharRange> id2range
		= new HashMap<>();
	private final DuzztAction[] actions;
	
	private final char[] globalActionCodes;
	
	private final char overallLow, overallHigh;
	private final char startChar, endChar, innerChar;
	
	public BricsCompiler(ImplementationModel impl) {
		this.impl = impl;
		int numActions = impl.getAllActions().size();
		this.actions = new DuzztAction[numActions];
		this.globalActionCodes = new char[impl.getGlobalActions().size()];
		this.overallLow = Character.MIN_VALUE;
		this.overallHigh = (char)(this.overallLow + numActions - 1);
		this.startChar = (char)(overallHigh + 1);
		this.endChar = (char)(overallHigh + 2);
		this.innerChar = (char)(overallHigh + 3);
		assignActionCodes();
	}
	
	public DuzztAction getAction(char c) {
		if(c < overallLow || c > overallHigh) {
			return null;
		}
		int ofs = c - overallLow;
		assert ofs < actions.length;
		
		return actions[ofs];
	}
	
	
	public DuzztAutomaton compile(DuzztRegExp re, Map<String,SubExpression> subExpressions) {
		Map<String, Automaton> subexprAutomata = new HashMap<>();
		
		SubExpression rootSubExpr = new SubExpression(re);
		
		Automaton bricsAutomaton = doCompile(rootSubExpr, subExpressions, subexprAutomata);
		
		bricsAutomaton = postProcess(bricsAutomaton);
		
		return toDuzztAutomaton(bricsAutomaton);
	}
	
	private Automaton postProcess(Automaton bricsAutomaton) {
		// Determinize & mininimize in order to remove sinks
		bricsAutomaton.determinize();
		bricsAutomaton.minimize();
				
		for(State s : bricsAutomaton.getStates()) {
			// Set accepting (make prefix closed)
			s.setAccept(true);
			// Add global actions
			for(char c : globalActionCodes) {
				State succ = s.step(c);
				if(succ == null) {
					// add self-loop
					s.addTransition(new Transition(c, s));
				}
			}
			Set<Transition> transitions = s.getTransitions();
			// Turn terminator actions into self loops
			Iterator<Transition> transIt = transitions.iterator();
			List<Transition> newTransitions = new ArrayList<>();
			while(transIt.hasNext()) {
				Transition t = transIt.next();

				State succ = t.getDest();
				if(succ != s) {
					char low = t.getMin();
					char high = t.getMax();

					boolean removed = false;
					for(char c = low; c <= high; c++) {
						DuzztAction act = getAction(c);
						if(act.isTerminator() || act == null) {
							if(!removed) {
								transIt.remove();
								removed = true;
							}
							newTransitions.add(new Transition(c, s));
							if(c > low) {
								newTransitions.add(new Transition(low, (char)(c-1), succ));
							}
							low = (char)(c+1);
						}
					}
					if(removed && low <= high) {
						newTransitions.add(new Transition(low, high, succ));
					}
				}
			}
			transitions.addAll(newTransitions);
		}
		
		bricsAutomaton.minimize();
		return bricsAutomaton;
	}
	
	private DuzztAutomaton toDuzztAutomaton(Automaton bricsAutomaton) {
		Map<State,DuzztState> stateMap = new HashMap<>();
		
		int id = 0;
		for(State bricsState : bricsAutomaton.getStates()) {
			if(!bricsState.getTransitions().isEmpty()) {
				DuzztState duzztState = new DuzztState(id++);
				stateMap.put(bricsState, duzztState);
			}
		}
		
		for(Map.Entry<State,DuzztState> e : stateMap.entrySet()) {
			State bricsState = e.getKey();
			DuzztState duzztState = e.getValue();
			
			for(Transition t : bricsState.getTransitions()) {
				State bricsDest = t.getDest();
				DuzztState duzztDest = stateMap.get(bricsDest);
				
				for(char c = t.getMin(); c <= t.getMax(); c++) {
					DuzztAction act = getAction(c);
					if(act != null) {
						DuzztState succ = (act.isTerminator()) ? null : duzztDest;
						duzztState.addTransition(act, succ);
					}
				}
			}
		}
		
		DuzztState duzztInit = stateMap.get(bricsAutomaton.getInitialState());
		
		return new DuzztAutomaton(stateMap.values(), duzztInit);
	}
	
	private Automaton doCompile(SubExpression expr, Map<String,SubExpression> subExpressions, Map<String,Automaton> subexprAutomata)
			throws UndefinedSubExpressionException, RecursiveSubExpressionException {
		Set<String> subExprRefs = DuzztREUtil.findReferencedSubexprs(expr.getExpression());
		
		for(String subExprRef : subExprRefs) {
			SubExpression subExpr = subExpressions.get(subExprRef);
			if(subExpr == null) {
				throw new UndefinedSubExpressionException(subExprRef);
			}
			
			if(!subexprAutomata.containsKey(subExprRef)) {
				subexprAutomata.put(subExprRef, null);
				Automaton a = doCompile(subExpr, subExpressions, subexprAutomata);
				subexprAutomata.put(subExprRef, a);
			}
			else if(subexprAutomata.get(subExprRef) == null) {
				throw new RecursiveSubExpressionException(subExprRef);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		if(expr.isOwnScope()) {
			sb.append('(');
			appendRaw(startChar, sb);
			sb.append("?(");
		}
		expr.getExpression().accept(new RETranslator(), sb);
		if(expr.isOwnScope()) {
			sb.append("))?&(");
			appendRaw(startChar, sb);
			sb.append("[^");
			appendRaw(startChar, sb);
			sb.append("]*");
			sb.append(')');
		}
		String bricsReStr = sb.toString();
		
		RegExp bricsRe = new RegExp(bricsReStr, RegExp.AUTOMATON | RegExp.INTERSECTION | RegExp.COMPLEMENT);
		Automaton automaton = bricsRe.toAutomaton(subexprAutomata);
		
		if(expr.isOwnScope()) {
			automaton = closeScope(automaton);
		}
		return automaton;
	}
	
	private Automaton closeScope(Automaton automaton) {
		// Make sure automaton is deterministic
		automaton.determinize();
		
		// Skip the starting character, as it is not part of the actual sequence
		State oldInit = automaton.getInitialState();
		State realInit = oldInit.step(startChar);
		
		// Duplicate the initial state, as it will *not* receive any epsilon-transitions
		// for the inner character
		State newInit = new State();
		newInit.setAccept(true);
		newInit.getTransitions().addAll(realInit.getTransitions());
		
		automaton.setInitialState(newInit);
		

		automaton.setDeterministic(false);
		
		for(State s : automaton.getStates()) {
			s.setAccept(true);
			if(s.step(endChar) != null) {
				s.getTransitions().clear();
				continue;
			}
			
			if(s != newInit) {
				State innerSucc = s.step(innerChar);
				if(innerSucc != null) {
					automaton.addEpsilons(Collections.singleton(new StatePair(s, innerSucc)));
				}
			}
			
			// Remove all transitions with special characters
			List<Transition> newTransitions = new ArrayList<>();
			Set<Transition> transSet = s.getTransitions();
			Iterator<Transition> transIt = transSet.iterator();
			
			while(transIt.hasNext()) {
				Transition t = transIt.next();
				State succ = t.getDest();
				
				if(t.getMax() > overallHigh) {
					transIt.remove();
					if(t.getMin() <= overallHigh) {
						newTransitions.add(new Transition(t.getMin(), overallHigh, succ));
					}
				}
			}
			
			transSet.addAll(newTransitions);
		}
		
		automaton.determinize();
		automaton.minimize();
		
		return automaton;
	}
	
	private void assignActionCodes() {
		char c = overallLow;
		
		int globalIdx = 0;
		
		for(Map.Entry<String,List<DuzztAction>> entry : impl.getActionLists()) {
			String name = entry.getKey();
			List<DuzztAction> actions = entry.getValue();
			
			char low = c;
			
			for(DuzztAction a : actions) {
				if(a.isGlobal()) {
					globalActionCodes[globalIdx++] = c;
				}
				this.actions[c++ - overallLow] = a;
			}
			char high = (char)(c - 1);
			
			id2range.put(name, new CharRange(low, high));
		}
	}
	
	private static void appendRaw(char c, StringBuilder sb) {
		sb.append('\\').append(c);
	}
	
	private static void appendRange(char low, char high, StringBuilder sb) {
		if(low == high) {
			appendRaw(low, sb);
		}
		else {
			sb.append('[');
			appendRaw(low, sb);
			sb.append('-');
			appendRaw(high, sb);
			sb.append(']');
		}
	}

}
