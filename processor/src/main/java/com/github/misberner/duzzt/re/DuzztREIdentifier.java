/*
 * Copyright (c) 2013 by Malte Isberner (https://github.com/misberner).
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


public class DuzztREIdentifier implements DuzztRegExp {

	private final String name;
	
	public DuzztREIdentifier(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.duzzt.re.DuzztRegExp#accept(com.github.misberner.duzzt.re.DuzztREVisitor, java.lang.Object)
	 */
	@Override
	public <R, D> R accept(DuzztREVisitor<R, D> visitor, D data) {
		return visitor.visit(this, data);
	}

}
