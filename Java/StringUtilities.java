/*
 * Copyright (c) 2015 Zachary Rauen
 * Website: www.ZackRauen.com
 *
 * All rights reserved. Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * If a copy of the License is not provided with the work, you may
 * obtain a copy at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zackrauen.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringUtilities {

	public static String join(Iterable<? extends Object> joinee, String joiner) {
		String returnMe = "";
		Iterator<? extends Object> iterator = joinee.iterator();
		while (iterator.hasNext()) {
			returnMe += iterator.next().toString();
			if (iterator.hasNext())
				returnMe += joiner;
		}
		return returnMe;
	}
	
	public static void main(String[] args) {
		List<Integer> l = new ArrayList<Integer>();
		l.add(1);l.add(2);l.add(3);
		System.out.println(StringUtilities.join(l, " and "));
	}
}
