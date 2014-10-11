/* 
 * SMART FP7 - Search engine for MultimediA enviRonment generated contenT
 * Webpage: http://smartfp7.eu
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * The Original Code is Copyright (c) 2012-2013 Athens Information Technology
 * All Rights Reserved
 *
 * Contributor:
 *  Nikolaos Katsarakis nkat@ait.edu.gr
 */

package eu.smartfp7.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple implementation of a Last Recently Used cache storing key-value pairs
 * @author Nikolaos Katsarakis nkat@ait.edu.gr
 *
 * @param <K> type of key
 * @param <V> type of value
 */

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final int limit;

	public LRUCache(int limit) {
		//Use default values if limit <=0
		super((limit<=0?16:limit), 0.75f, true);
		this.limit = limit<=0?16:limit;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > limit;
	}
	
	public int getLimit() {
		return limit;
	}
}