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
import java.util.ArrayList;
import java.util.List;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;

public class EclecticQueryBuilder {

	Mapping[] mappings;
	int i = -1;
	String activity = "";
	String until = "";
	String since ="";
	float lat1 = -91;
	float lon1 = -181;
	float lat2 = -91;
	float lon2 = -181;
	float radius = -1;
	String query = "";
	
	public EclecticQueryBuilder() throws IOException{
		mappings = null;
	}
	
	public EclecticQueryBuilder(String[] mapNames) throws IOException{
		
		
		mappings = new MappingManager().getMappings(mapNames);
		
		
	}

	public void setCriteria (String activity, String until,String since, float lat1, float lon1, float lat2, float lon2, float radius){
		
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
		
		query = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n";
		
		List<String> prefix = new ArrayList<String>();
		boolean inList = false;
		
		for(int j = 0 ; j < i ; j++){
			
			for(int k = 0; k < mappings[j].getPrefixSize(); k++){
				
				inList = false;
				
				for(String pre : prefix)
					if(mappings[j].getPrefixName(k).contentEquals(pre)){
						inList = true;
						break;
					}
				
				if(!inList){
					query = query +
							"PREFIX "+mappings[j].getPrefixName(i)+": <"+mappings[j].getPrefixURI(i)+">\n";
					prefix.add(mappings[j].getPrefixName(i));
				}
				
			}
			
						
		}
		
		query = query +"SELECT";
		
			boolean hasLoc = false;
			boolean hasTime = false;
			boolean hasSpatial = false;
			
			query = query + " ?activity";
			
			for (int j = 0 ; j < i ; j++ ){
				
				
				if(!activity.contentEquals("") ){
					
					for(int k = 0;i<mappings[j].getAttributeSize();i++)				
						query = query + " ?attribute"+j+""+k;
					
					if(mappings[j].hasLocation())
						hasLoc = true;
					
					if(mappings[j].hasSpatialProperties())
						hasSpatial = true;
				
				}
				
				if(mappings[j].hasSpatialProperties())
					hasSpatial = true;
				
				if(mappings[j].hasDate())
					hasTime = true;
				
			}

			if(hasTime) query = query + " ?date";
			
			if(hasLoc) query = query + " ?location";
			
			if(hasSpatial) query = query + " ?lat ?long";
			
			
			query = query + "\nWHERE{\n";
			
			for(int j = 0 ; j < i ;j++){
				
				
				query = query +"	{\n" +
							   "		OPTIONAL{\n";
				
				
				if(!activity.contentEquals("") ){
					
					query = query +
							"			?activity rdf:type "+mappings[j].getActivityType();
					
					for(int i = 0;i<mappings[j].getAttributeSize();i++)
						query = query +
							"			?activity "+mappings[j].getAttributePredicate(i)+" "+mappings[j].getAttributeObject(i)+".\n";
					
					if(mappings[j].hasLocation())
						query = query +
							"			?activity "+mappings[j].getLocationPred()+" ?location.\n";

					if(mappings[j].hasDate())
						query = query +
							"			?activity "+mappings[j].getDatePred()+" ?date";
					
					if(mappings[j].hasDateType())
						query = query+"^^"+mappings[j].getDateType()+".\n";
					else
						query = query +".\n";
				
					
					if(mappings[j].hasDate()){
						if(!until.contentEquals("")) query = query +
							"			FILTER(?date < "+until+").\n";
						
						if(!since.contentEquals("")) query = query +
							"			FILTER(?date > "+since+").\n";
										
					}
				}
				
				if(mappings[j].hasLocation())
					query = query + 
						"			?location rdf:type "+mappings[j].getLocationType()+".\n";
				
				if(mappings[j].hasSpatialProperties()){
					query = query +
						"			?location "+mappings[j].getLatpred()+ " ?lat.\n"+
						"			?location "+mappings[j].getLongpred()+" ?long.\n";	
				}
				
				if(radius != -1)
					query = query + mappings[j].printCircleExpression(lat1, lon1, radius, "?location");
				else if(lat2 !=-91)
					query = query + mappings[j].printRectangleExpression(lat1, lon1, lat2, lon2, "?location");
				
				query = query +
				"		}\n"+
				"	}\n";
				
				if(j<i-1)
					query = query +"	UNION\n";
				
				
			}
		
			query = query +"}";
		
		return query;
			
	}
	
	
	
}
