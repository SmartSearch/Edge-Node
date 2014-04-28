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

import java.io.IOException;
import java.util.logging.Logger;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;

public class SimpleQueryBuilder {

	private static final Logger logger = Logger.getLogger(SimpleQueryBuilder.class.getCanonicalName());
	
	Mapping m;
	String activity = "";
	String until = "";
	String since ="";
	float lat1 = -91;
	float lon1 = -181;
	float lat2 = -91;
	float lon2 = -181;
	float radius = -1;
	String query = "";
	
	public SimpleQueryBuilder(){
		m =  null;
	}
	
	public SimpleQueryBuilder(String mapName) throws IOException{
		
		m = new MappingManager().getMapping(mapName);
		
	}
	
	
	public SimpleQueryBuilder(Mapping m){
		
			this.m = m;
		
	}
	
	
	public void setCriteria (String activity, String until, String since, float lat1, float lon1, float lat2, float lon2, float radius){
		
		this.activity = activity;
		
		this.until = until;
		this.since = since;
		
		this.lat1 = lat1;
		this.lon1 = lon1;
		
		this.lat2 = lat2;
		this.lon2 = lon2;
		
		this.radius = radius;
				
	}


	public String getActivity() {
		return activity;
	}


	public String getUntil() {
		return until;
	}


	public String getSince() {
		return since;
	}


	public float getLat1() {
		return lat1;
	}


	public float getLon1() {
		return lon1;
	}


	public float getLat2() {
		return lat2;
	}


	public float getLon2() {
		return lon2;
	}


	public float getRadius() {
		return radius;
	}


	public String getQuery() {
		return query;
	}


	public String printQuery(){
		
		logger.info("Building the query for: activity"+ this.activity+" until: "+ this.until +" since: "+ this.since +" lat1: "+ this.lat1 + " long1: " + lon1 + " lat2: "+ this.lat2 + " long2: " + lon2 + " radius: "+this.radius);
		
		query = "";
		
		
		for(int i = 0;i<m.getPrefixSize();i++){
			
			query = query +
					"PREFIX "+m.getPrefixName(i)+": <"+m.getPrefixURI(i)+">\n";
		}
		
			query = query +
					"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"+
					"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n";
			
		query = query +"SELECT";
		
		if(!activity.contentEquals("") ){
			
			query = query + " ?activity";
			
			for(int i = 0;i<m.getAttributeSize();i++)				
				query = query + " ?attribute"+i;
			
			if(m.hasDate())
					query = query +" ?date";
		}else{
			
			for(int i = 0;i<m.getAttributeLocationsSize();i++)				
				query = query + " ?attribute"+i;
		}
		
		
		if(m.hasLocation())
			query = query +" ?location";
		
		
		if(m.hasSpatialProperties())
			query = query + " ?lat ?long";
		
		
		query = query + "\nWHERE{\n";
		
		if(!activity.contentEquals("") ){
		
			// Donde las actividades
			query = query +
					"	?activity rdf:type "+m.getActivityType()+".\n";
			
			for(int i = 0;i<m.getAttributeSize();i++)
				query = query +
					"	OPTIONAL {?activity "+m.getAttributePredicate(i)+" ?attribute"+i+" }.\n";
			
			// Buscamos las locations de las actividades
			if(m.hasLocation()){
				// Aqui tenemos que sacar la localizacion de las entidades, bien porque exista una serie de predicados que me lo proporcione
				// bien porque sea a traves de la lat, log.
				String locations_activity = "";
				if (m.getLocationsSize() >1){
//					query = query + "	{\n";
//					for(int i = 0;i<m.getLocationsSize();i++){
//						if (i == 0) query = query +"		{?activity "+m.getLocationsPredicate(i)+" ?location}\n";
//						else query = query +"		UNION	{?activity "+m.getLocationsPredicate(i)+" ?location}\n";
//					}
//					query = query + "	}.\n";
					
					for(int i = 0;i<m.getLocationsSize();i++){
						if (!m.getLocationsPredicate(i).equals("")){
							// Estamos en el caso en que hay predicado que asignar a la actividad
							if (locations_activity.equals("")) locations_activity = "		{?activity "+m.getLocationsPredicate(i)+" ?location}\n";
							else locations_activity = locations_activity +"		UNION	{?activity "+m.getLocationsPredicate(i)+" ?location}\n";
						}
					}
					if (!locations_activity.equals("")) query = query + "	{\n" + locations_activity + "	}.\n";
					else if (m.hasSpatialProperties()){
						query = query +
								"	?activity "+m.getLatpred()+ " ?lat.\n"+
								"	?activity "+m.getLongpred()+" ?long.\n";	
					}
					
					
				}else{
					// Caso en que no tengamos una lista d atributos de entidades
					if (!m.getLocationPred().equals (""))
						query = query +
							"	?activity "+m.getLocationPred()+" ?location.\n";
					else if (m.hasSpatialProperties()){
						query = query +
								"	?activity "+m.getLatpred()+ " ?lat.\n"+
								"	?activity "+m.getLongpred()+" ?long.\n";	
					}
				}		
			}

			if(m.hasDate())
				query = query +
					"	OPTIONAL{ ?activity "+m.getDatePred()+" ?date.";
				if(!until.contentEquals("")) query = query +
						"	FILTER(?date < "+until+").";
				if(!since.contentEquals("")) query = query +
						"	FILTER(?date > "+since+").";
							
				query = query +	" }\n";
			
//			if(m.hasDate()){
//				if(!until.contentEquals("")) query = query +
//					"	FILTER(?date < "+until+").\n";
//				
//				if(!since.contentEquals("")) query = query +
//						"	FILTER(?date > "+since+").\n";
								
//			}
			
		} // Fin de meter los parametros para la actividad
		
		else{
			// Donde las localizaciones
			for(int i = 0;i<m.getAttributeLocationsSize();i++)
				query = query +
					"	OPTIONAL {?location "+m.getAttributeLocationsPredicate(i)+" ?attribute"+i+"}.\n";
		}
		
		if(radius != -1)
			query = query +"	"+ m.printCircleExpression(lat1, lon1, radius, "?location")+".\n";
		else if(lat2 !=-91)
			query = query +"	"+ m.printRectangleExpression(lat1, lon1, lat2, lon2, "?location")+".\n";
		
		
		
//		if(m.hasLocation())
//	
//			query = query + 
//				"	?location rdf:type "+m.getLocationType()+".\n";
		
		if(m.hasLocation()){
			if (m.getLocationsSize() >1){
				query = query + "	{\n";
				for(int i = 0;i<m.getLocationsSize();i++){
					if (i == 0) query = query +"		{?location rdf:type "+m.getLocationsObject(i)+"}\n";
					else query = query +"		UNION	{?location rdf:type "+m.getLocationsObject(i)+"}\n";
				}
				query = query + "	}.\n";
			}else{
				query = query + 
						"	?location rdf:type "+m.getLocationType()+".\n";
			}
		}
		
		
		if(m.hasSpatialProperties()){
			query = query +
				"	?location "+m.getLatpred()+ " ?lat.\n"+
				"	?location "+m.getLongpred()+" ?long.\n";	
		}
		
		

		query = query +
				"}";
		
		logger.info("The query: "+query);
		
		return query;
	}
	
}
