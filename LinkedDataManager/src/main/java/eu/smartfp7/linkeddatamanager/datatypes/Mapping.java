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
package eu.smartfp7.linkeddatamanager.datatypes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.text.ParseException;

import eu.smartfp7.linkeddatamanager.logic.MappingManager;

public class Mapping {
	
	
	private static final Logger logger = Logger.getLogger(Mapping.class.getCanonicalName());
	String endpoint = "";
	List<String[]> prefix = new ArrayList<String[]>();
	String ActivityType = "";
	List<String[]> attributes = new ArrayList<String[]>();
	List<String[]> locations = new ArrayList<String[]>();
	
	String[] location = null;
	String latpred ="";
	String longpred ="";
	String recex = "";
	String circex = "";
	String[] time = null;
	String name ="";
	
	//Nines
	List<String[]> txt_attributes = new ArrayList<String[]>();
	List<String[]> attributesLocations = new ArrayList<String[]>();
	public Mapping() {
		
	}
	
	public Mapping(String filepath) throws IOException{
		
		String l;
		String[] line;
		String[][] map;
		String[] s;
		
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
			
		
		while((l = reader.readLine()) != null){
			
			line = l.split(",");
			
			if(line[0].contentEquals("NAME")) name = line[1];
			if(line[0].contentEquals("ENDPOINT")) endpoint = line[1];
			if(line[0].contentEquals("PREFIX")){
				
				s = new String[2];
				s[0] = line[1];
				s[1] = line[2];
				prefix.add(s);
				
			}
			
			if(line[0].contentEquals("ACTYPE")) ActivityType = line[1];
			
			if(line[0].contentEquals("ACATT")){
				s = new String[2];
				s[0] = line[1];
				s[1] = line[2];
				attributes.add(s);
			}
			
			if(line[0].contentEquals("LOC")){
				if(line.length == 3 ){
					location = new String[2];
					location[0] = line[1];
					location[1] = line[2];
					locations.add(location);
				}
				else{
					location = new String[1];
					location[0] = line[1];
					locations.add(location);
					
					
				}
			}
			
			if(line[0].contentEquals("LOCLAT")) latpred = line[1];
			
			if(line[0].contentEquals("LOCLON")) longpred = line[1];
			
			if(line[0].contentEquals("RECEX")) recex = line[1];
			
			if(line[0].contentEquals("CIRCEX")) circex = line[1];
		
			if(line[0].contentEquals("TIME")){
				
				if(line.length == 3){
				
					time = new String[2];
					time[0] = line[1];
					time[1] = line[2];
				
				}
				else{
					
					time = new String[1];
					time[0] = line[1];
					
				}
			}
			
			// Nines
			if(line[0].contentEquals("TXTATT")){
				s = new String[2];
				s[0] = line[1];
				s[1] = line[2];
				txt_attributes.add(s);
			}
			
			if(line[0].contentEquals("LOCATT")){
				s = new String[2];
				s[0] = line[1];
				s[1] = line[2];
				attributesLocations.add(s);
			}
			
			
			
		}
		// Imprimimos los parametros basicos
		logger.info("Name: "+name);
		logger.info("endpoint: "+endpoint);
		logger.info("location: "+location);
		logger.info("Attributes activities...");
		for (int i=0; i<attributes.size();i++) logger.info(attributes.get(i)[0].toString());
		logger.info("Attributes locations...");
		// Imprimimos los atributos de las localizaciones
		for (int i=0; i<attributesLocations.size();i++) logger.info(attributesLocations.get(i)[0].toString());
		logger.info("Locations...");
		// Imprimimos las localizaciones
		for (int i=0; i<locations.size();i++) logger.info(locations.get(i)[0].toString());
		logger.info("Text...");
		// Imprimimos las txt
		for (int i=0; i<txt_attributes.size();i++)  logger.info(txt_attributes.get(i)[0].toString());
		
		logger.info("Mapped: "+name);
		
	}
	
	public boolean hasTxtAttribute(){
		return locations.size() != 0;
	}
	
	public boolean hasLocation(){
		return location != null;
	}
	
	public boolean hasSpatialProperties(){
		return !(latpred.contentEquals("") && longpred.contentEquals(""));
				
	}
	
	public boolean hasDate(){
		return time != null;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public int getPrefixSize() {
		return prefix.size();
	}

	
	public String getPrefixName(int i){
		return prefix.get(i)[0];
	}
	
	public String getPrefixURI(int i){
		return prefix.get(i)[1];
	}
	

	public String getActivityType() {
		return ActivityType;
	}

	public String getLocationPred(){
		return location[0];
	}
	
	public String getLocationType(){
		return location[1];
	}
	

	public String getLatpred() {
		return latpred;
	}


	public String getLongpred() {
		return longpred;
	}
	
	
	public int getAttributeSize(){
		return attributes.size();
	}
	
	public int getTxtAttributeSize(){
		return txt_attributes.size();
	}
	
	public int getLocationsSize(){
		return locations.size();
	}
	
	public int getAttributeLocationsSize(){
		return attributesLocations.size();
	}
	
	public String getAttributePredicate(int i){
		return attributes.get(i)[0];
	}
	
	public String getTxtAttributePredicate(int i){
		return txt_attributes.get(i)[0];
	}
	
	public String getLocationsPredicate(int i){
		return locations.get(i)[0];
	}
	
	public String getAttributeLocationsPredicate(int i){
		return attributesLocations.get(i)[0];
	}
	
	public String getAttributeObject(int i){
		return attributes.get(i)[1];
	}
	
	public String getTxtAttributeObject(int i){
		return txt_attributes.get(i)[1];
	}
		
	public String getLocationsObject(int i){
		return locations.get(i)[1];
	}
	
	public String getAttributeLocationsObject(int i){
		return attributesLocations.get(i)[1];
	}
	
	public String printRectangleExpression(float lat1, float lon1, float lat2, float lon2, String locPar){
		
		return recex.replace("%LAT1%", String.format("%2f", lat1))
				.replace("%LON1%", String.format("%2f", lon1))
				.replace("%LAT2%", String.format("%2f", lat2))
				.replace("%LON2%", String.format("%2f", lon2))
				.replace("%LOCPAR%", locPar).replace(",", ".");
		
	}
	
	public String printCircleExpression(float lat1, float lon1, float radius, String locPar){
		
		return circex.replace("%LAT1%", String.format("%2f", lat1))
				.replace("%LON1%", String.format("%2f", lon1))
				.replace("%RAD%", String.format("%2f", radius))
				.replace("%LOCPAR%", locPar).replace(",", ".");
	}
	
	public boolean hasRectangleExpression() {
		
		return !recex.contentEquals("");
	
	}
	
	public boolean hasCircleExpression() {
		
		return !circex.contentEquals("");
	
	}
	
	public String getDatePred(){
		return time[0];
	}
	
	public String getDateType(){
		return time[0];
	}

	public boolean hasDateType() {
		return time.length == 2;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
}
