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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.github.misberner.duzzt.annotations.GenerateEmbeddedDSL;
import com.github.misberner.duzzt.annotations.SubExpr;


/**
 * A sample DSL implementation for a date adder.
 *
 * @author Malte Isberner
 */
@GenerateEmbeddedDSL(
		name = "DateAdder",
		syntax = "(<add> days)? (<add> hours)? (<add> minutes)? (<add> seconds)? to",
		where = {
				@SubExpr(name = "add", definedAs="^add|/and")
		})
public final class DateAdderImpl {
	private static final class FieldIncrement {
		public final int field;
		public final int amount;

		public FieldIncrement(int field, int amount) {
			this.field = field;
			this.amount = amount;
		}
	}

	private final List<FieldIncrement> fieldIncrements = new ArrayList<>(4);
	// the last amount that was specified in a call to add()
	private int currentAmount;

	// Helper method
	private void addAs(int field) {
		this.fieldIncrements.add(new FieldIncrement(field, this.currentAmount));
	}

	// Methods corresponding to DSL actions
	public void add(int amount) {
		this.currentAmount = amount;
	}
	
	// Alias for add
	public void and(int amount) {
		add(amount);
	}

	public void days() {
		addAs(Calendar.DATE);
	}

	public void hours() {
		addAs(Calendar.HOUR);
	}

	public void minutes() {
		addAs(Calendar.MINUTE);
	}

	public void seconds() {
		addAs(Calendar.SECOND);
	}

	public Date to(Date otherDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(otherDate);
		for (FieldIncrement fi : fieldIncrements) {
			calendar.add(fi.field, fi.amount);
		}
		return calendar.getTime();
	}

	
	@Override
	public String toString() {
		return "";
	}

}
