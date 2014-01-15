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


public interface DuzztREVisitor<R, D> {
	R visit(DuzztREAlt re, D data);
	R visit(DuzztREConcat re, D data);
	R visit(DuzztREIdentifier re, D data);
	R visit(DuzztREModifier re, D data);
	R visit(DuzztRESubexpr re, D data);
}
