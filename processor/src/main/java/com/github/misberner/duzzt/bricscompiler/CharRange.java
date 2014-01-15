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

/**
 * A character range.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
final class CharRange {
	
	private final char low;
	private final char high;
	

	/**
	 * Constructor.
	 * @param low lower bound of the range (inclusive)
	 * @param high upper bound of the range (inclusive)
	 */
	public CharRange(char low, char high) {
		this.low = low;
		this.high = high;
	}
	
	/**
	 * Retrieves the lower bound (inclusive) of the range.
	 * @return the lower bound of the range
	 */
	public char getLow() {
		return low;
	}
	
	/**
	 * Retrieves the upper bound (inclusive) of the range.
	 * @return the upper bound of the range
	 */
	public char getHigh() {
		return high;
	}

}
