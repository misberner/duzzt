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
package com.github.misberner.duzzt.re;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class DuzztREUtil {
	
	private static final DuzztREVisitor<Void, Collection<? super String>> findIdentifiersVisitor
		= new AbstractDuzztREVisitor<Void,Collection<? super String>>() {
			@Override
			protected Void defaultVisitComplex(DuzztComplexRegExp re,
					Collection<? super String> data) {
				visitChildren(re, data);
				return null;
			}
			@Override
			public Void visit(DuzztREIdentifier re,
					Collection<? super String> data) {
				data.add(re.getName());
				return null;
			}
	};
	
	private static final DuzztREVisitor<Void, Collection<? super String>> findSubexprsVisitor
		= new AbstractDuzztREVisitor<Void,Collection<? super String>>() {
			@Override
			protected Void defaultVisitComplex(DuzztComplexRegExp re,
					Collection<? super String> data) {
				visitChildren(re, data);
				return null;
			}
			@Override
			public Void visit(DuzztRESubexpr re, Collection<? super String> data) {
				data.add(re.getSubexprName());
				return null;
			}
	};

	
	public static void findReferencedSubexprs(DuzztRegExp re, Collection<? super String> coll) {
		re.accept(findSubexprsVisitor, coll);
	}
	
	public static Set<String> findReferencedSubexprs(DuzztRegExp re) {
		Set<String> set = new HashSet<>();
		findReferencedSubexprs(re, set);
		return set;	
	}
	
	public static void findIdentifiers(DuzztRegExp re, Collection<? super String> coll) {
		re.accept(findIdentifiersVisitor, coll);
	}
	
	public static Set<String> findIdentifiers(DuzztRegExp re) {
		Set<String> set = new HashSet<>();
		findIdentifiers(re, set);
		return set;
	}
	
	private DuzztREUtil() {}
}
