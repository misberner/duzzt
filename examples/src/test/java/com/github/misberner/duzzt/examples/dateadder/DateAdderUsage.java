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
package com.github.misberner.duzzt.examples.dateadder;

import java.util.Date;

/**
 * Usage example for the generated DateAdder.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class DateAdderUsage {
	
	public static void main(String[] args) {
		Date now = new Date();
		Date future = new DateAdder()
			.add(5).days()
			.and(2).minutes()
			.and(10).seconds()
			.to(now);
		
		System.out.println("The date 5 days, 2 minutes, and 10 seconds from now: " + future);
	}
}
