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

public class AbstractDuzztREVisitor<R, D> implements DuzztREVisitor<R, D> {

	
	protected void visitChildren(DuzztComplexRegExp re, D data) {
		for(DuzztRegExp child : re.getChildren()) {
			child.accept(this, data);
		}
	}
	
	protected R defaultVisitComplex(DuzztComplexRegExp re, D data) {
		return defaultVisit(re, data);
	}
	
	protected R defaultVisit(DuzztRegExp re, D data) {
		return null;
	}
	
	@Override
	public R visit(DuzztREAlt re, D data) {
		return defaultVisitComplex(re, data);
	}

	@Override
	public R visit(DuzztREConcat re, D data) {
		return defaultVisitComplex(re, data);
	}

	@Override
	public R visit(DuzztREIdentifier re, D data) {
		return defaultVisit(re, data);
	}

	@Override
	public R visit(DuzztREModifier re, D data) {
		return defaultVisitComplex(re, data);
	}

	@Override
	public R visit(DuzztRESubexpr re, D data) {
		return defaultVisit(re, data);
	}
	
	@Override
	public R visit(DuzztRENonEmpty re, D data) {
		return defaultVisitComplex(re, data);
	}

	@Override
	public R visit(DuzztREStart re, D data) {
		return defaultVisit(re, data);
	}

	@Override
	public R visit(DuzztREEnd re, D data) {
		return defaultVisit(re, data);
	}

	@Override
	public R visit(DuzztREInner re, D data) {
		return defaultVisit(re, data);
	}

}
