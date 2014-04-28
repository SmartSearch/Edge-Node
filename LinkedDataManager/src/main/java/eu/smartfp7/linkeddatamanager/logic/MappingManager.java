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
 package eu.smartfp7.linkeddatamanager.logic;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.print.DocFlavor.URL;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;

public class MappingManager {

	String[] mapNames;
	Mapping[] mappings;
	
	
	File resources;
	
	private static final Logger logger = Logger.getLogger(MappingManager.class.getCanonicalName());
	public MappingManager() throws IOException{
		
		//Enumeration<java.net.URL> enum_urls = this.getClass().getClassLoader().getResource("mappings");
		String path = this.getClass().getClassLoader().getResource("mappings").getFile();
		
		resources = new File(path);
		File[] maps = resources.listFiles();
		
		int num_files = maps.length; // quitamos el .svn;
		
		// Creo los arrays que contendran los datos, con longitud menos 1 porque no contendra el .svn
		mappings = new Mapping[num_files];
		mapNames = new String[num_files];
		
		for(int i  = 0; i<num_files; i++){
				mappings[i] = new Mapping(maps[i].getAbsolutePath());
				mapNames[i] = mappings[i].getName();		
		}
		
	
		logger.info("The mapped files: ...");
		for(int i  = 0; i<mappings.length; i++){
			logger.info("Names: " +mapNames[i]);
		}
	}
	
	public Mapping getMapping(String name){
		
		int i;
		
		for(i = 0; i<mapNames.length; i++)
			if(mapNames[i].contentEquals(name))
				break;
			
		if(i == mapNames.length) return null;
		else return mappings[i];
		
	}
	
	
	public Mapping[] getMappings(String names[]){
		
	
		Mapping[] result = new Mapping[names.length];
		int i;
		
		for(int j = 0;j<names.length;j++)
			for(i = 0;i<mapNames.length;i++)
				if(mapNames[i].contentEquals(names[j])){
					result[j] = mappings[i];
					break;
				}
		
		return result;
		
	}
	
}
