/* 
 * SMART FP7 - Search engine for MultimediA enviRonment generated contenT
 * Webpage: http://smartfp7.eu
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * The Original Code is Copyright (c) 2012-2013 of Atos 
 * All Rights Reserved
 *
 * Contributor(s):
 *  Sinan Yurtsever,
 *  Nines Sanguino, maria.sanguino at atos dot net
 */
 package test;

import java.io.IOException;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;
import eu.smartfp7.linkeddatamanager.logic.MappingManager;
import eu.smartfp7.linkeddatamanager.logic.SPARQLtoJSON;
import eu.smartfp7.linkeddatamanager.logic.SimpleQueryBuilder;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			SimpleQueryBuilder sqb = new SimpleQueryBuilder("dbpedia");
			
			sqb.setCriteria("http://dbpedia.org/resource/Quasi-War", "", "", -91,-181 ,-91 ,-181 ,-1);
			
			
			Mapping m = new MappingManager().getMapping("dbpedia");
			SPARQLtoJSON sqj = new SPARQLtoJSON(m.getEndpoint() , m);
			
			sqj.executeSPARQL(sqb.printQuery());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
