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
 package eu.smartfp7.linkeddatamanager.interfaces;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.json.JSONObject;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;
import eu.smartfp7.linkeddatamanager.logic.MappingManager;
import eu.smartfp7.linkeddatamanager.logic.SPARQLtoJSON;
import eu.smartfp7.linkeddatamanager.logic.SimpleQueryBuilder;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.DefaultValue;

@Path("structuredSearch")
public class StructuredSearch {

	private static final Logger logger = Logger.getLogger(StructuredSearch.class.getCanonicalName());
	
//	@GET
//	@Path("test")
//	public Response test (){
//		return Response.ok("ok").build();
//	}
	
	/**
	 * Finding locations within a geo-spatial rectangle identified by the exact geographical coordinates of its lower-left corner and its upper-right corner
	 * @param lat1: Between 0 and 90 degrees. Positive (North) o negative (South)
	 * @param long1: Between 0 and 180 degrees. Positive (East) o negative (West)
	 * @param lat2: Between 0 and 90 degrees. Positive (North) o negative (South)
	 * @param long2: Between 0 and 180 degrees. Positive (East) o negative (West)
	 * @param dataset: The dataset in which perform the search. By default Factforge
	 * @return A JSON file format with the found venues
	 * @throws IOException
	 */
	@GET
	@Path("locRec")
	@Produces({MediaType.APPLICATION_JSON})
	public Response sLocRec (@QueryParam("lat1") float lat1,
						 @QueryParam("long1") float long1,
						 @QueryParam("lat2") float lat2,
						 @QueryParam("long2") float long2,
						 @QueryParam("dataset") @DefaultValue("factforge") String dataset) throws IOException{
		logger.info("Invoking search Location by coordinates, rectangule");
		logger.info("Dataset: " + dataset);
		logger.info("Lat1: " + lat1);
		logger.info("Long1: " + long1);
		logger.info("Lat2: " + lat2);
		logger.info("Long2: " + long2);
		
		InputStream in = null;
		Mapping m = new MappingManager().getMapping(dataset);
		
		SimpleQueryBuilder builder = new SimpleQueryBuilder (m);
		builder.setCriteria("", "", "", lat1, long1, lat2, long2, -1);
		String query = builder.printQuery();
	
		
		SPARQLtoJSON queryer = new SPARQLtoJSON (m.getEndpoint(), m);
		try{
			JSONObject res = queryer.executeSPARQL(query);
			in = new ByteArrayInputStream(res.toString().getBytes());
		}
		catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
		logger.info("Invoked search Location by coordinates, rectangule");
		return Response.ok(in).build();
		
	}
	

	/**
	 * Finding locations located within a geo-spatial circle identified by the geo-graphical coordinates (a pair of latitude and longitude) of its centre and its radius
	 * @param lat1: The latitude coordinate indicating the centre. Between 0 and 90 degrees. Positive (North) o negative (South)
	 * @param long1: The longitude coordinate indicating the centre. Between 0 and 180 degrees. Positive (East) o negative (West)
	 * @param radius: The radio from the centre
	 * @param dataset: The dataset in which perform the search. By default Factforge
	 * @return A JSON file format with the found venues
	 * @throws IOException 
	 */
	@GET
	@Path("locCirc")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response sLocCirc (@QueryParam("lat1") float lat1,
						 @QueryParam("long1") float long1,
						 @QueryParam("radius") float radius,
						 @QueryParam("dataset") @DefaultValue("factforge") String dataset) throws IOException{
		logger.info("Invoking search Locations by coordinates, circule");
		logger.info("Dataset: " + dataset);
		logger.info("Lat1: " + lat1);
		logger.info("Long1: " + long1);
		logger.info("Radius: " + radius);
		
		InputStream in = null;
		Mapping m = new MappingManager().getMapping(dataset);
		
		SimpleQueryBuilder builder = new SimpleQueryBuilder (m);
		builder.setCriteria("", "", "", lat1, long1, -91, -81, radius);
		String query = builder.printQuery();
		
		
		SPARQLtoJSON queryer = new SPARQLtoJSON (m.getEndpoint(), m);
		try{
			JSONObject res = queryer.executeSPARQL(query);
			in = new ByteArrayInputStream(res.toString().getBytes());
		}
		catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
		logger.info("Invoked search locations by coordinates, circule");
		return Response.ok(in).build();
	}
	

	/**
	 * Finding activities within a geo-spatial rectangle identified by the exact geographical coordinates of its lower-left corner and its upper-right corner
	 * @param lat1: Between 0 and 90 degrees. Positive (North) o negative (South)
	 * @param long1: Between 0 and 180 degrees. Positive (East) o negative (West)
	 * @param lat2: Between 0 and 90 degrees. Positive (North) o negative (South)
	 * @param long2: Between 0 and 180 degrees. Positive (East) o negative (West)
	 * @param since: To delimit the date (an optional requirement)
	 * @param until: To delimit the date (an optional requirement)
	 * @param dataset. The dataset in which perform the search. By default Factforge
	 * @return A JSON file format with the found activities
	 * @throws IOException
	 */
	@GET
	@Path("actRec")
	@Produces({MediaType.APPLICATION_JSON})
	public Response sActRec (@QueryParam("lat1") float lat1,
						 @QueryParam("long1") float long1,
						 @QueryParam("lat2") float lat2,
						 @QueryParam("long2") float long2,
						 @QueryParam("since") @DefaultValue("") String since,
						 @QueryParam("until") @DefaultValue("") String until,
						 @QueryParam("dataset") @DefaultValue("factforge") String dataset) throws IOException{
		logger.info("Invoking search activities by coordinates, rectangule");
		logger.info("Dataset: " + dataset);
		logger.info("Lat1: " + lat1);
		logger.info("Long1: " + long1);
		logger.info("Lat2: " + lat2);
		logger.info("Long2: " + long2);
		logger.info("Since: " + since);
		logger.info("Until: " + until);
		
		InputStream in = null;
		Mapping m = new MappingManager().getMapping(dataset);
		
		SimpleQueryBuilder builder = new SimpleQueryBuilder (m);
		builder.setCriteria("activity", since, until, lat1, long1, lat2, long2, -1);
		String query = builder.printQuery();
	
		
		SPARQLtoJSON queryer = new SPARQLtoJSON (m.getEndpoint(), m);
		try{
			JSONObject res = queryer.executeSPARQL(query);
			in = new ByteArrayInputStream(res.toString().getBytes());
		}
		catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
		logger.info("Invoked search activities by coordinates, rectangule");
		return Response.ok(in).build();
		
	}
	
	
	
	/**
	 * Finding locations located within a geo-spatial circle identified by the geo-graphical coordinates (a pair of latitude and longitude) of its centre and its radius
	 * @param lat1: The latitude coordinate indicating the centre. Between 0 and 90 degrees. Positive (North) o negative (South)
	 * @param long1: The longitude coordinate indicating the centre. Between 0 and 180 degrees. Positive (East) o negative (West)
	 * @param radius: The radio from the centre
	 * @param since: To delimit the date (an optional requirement)
	 * @param until: To delimit the date (an optional requirement)
	 * @param dataset: The dataset in which perform the search. By default Factforge
	 * @return A JSON file format with the found activities
	 * @throws IOException 
	 */
	@GET
	@Path("actCirc")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response sActCirc (@QueryParam("lat1") float lat1,
							 @QueryParam("long1") float long1,
							 @QueryParam("radius") float radius,
							 @QueryParam("since") @DefaultValue("") String since,
							 @QueryParam("until") @DefaultValue("") String until,
							 @QueryParam("dataset") @DefaultValue("factforge") String dataset)  throws IOException{
		logger.info("Invoking search activities by coordinates, circule");
		logger.info("Dataset: " + dataset);
		logger.info("Lat1: " + lat1);
		logger.info("Long1: " + long1);
		logger.info("Radius: " + radius);
		logger.info("Since: " + since);
		logger.info("Until: " + until);
		
		InputStream in = null;
		Mapping m = new MappingManager().getMapping(dataset);
		
		SimpleQueryBuilder builder = new SimpleQueryBuilder (m);
		builder.setCriteria("activity", since, until, lat1, long1, -91, -81, radius);
		String query = builder.printQuery();
		
		
		SPARQLtoJSON queryer = new SPARQLtoJSON (m.getEndpoint(), m);
		try{
			JSONObject res = queryer.executeSPARQL(query);
			in = new ByteArrayInputStream(res.toString().getBytes());
		}
		catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
		logger.info("Invoked search activities by coordinates, circule");
		return Response.ok(in).build();
	}
	
	
	
//	/**
//	 * Finding activities within a geo-spatial rectangle identified by the exact geographical coordinates of its lower-left corner and its upper-right corner
//	 * @param label: The activity
//	 * @param lang: the language in which is expresed the activity
//	 * @param lat1. Between 0 y 90 positive North o negative South
//	 * @param long1. Between 0 y 180 positive East o negative West
//	 * @param lat2. Between 0 y 90 positive North o negative South
//	 * @param long2. Between 0 y 180 positive East o negative West
//	 * @param since
//	 * @param until
//	 * @param page
//	 * @param pageSize
//	 * @throws IOException 
//	 */
//	@GET
//	@Path("actLocRec")
//	@Produces({MediaType.APPLICATION_JSON})
//	public void searchActLocRec(String label, String lang, float lat1, float lon1, float lat2, float lon2, String since, String until, int page, int pageSize) throws IOException{
//		
//		Mapping m = new MappingManager().getMapping("factforge");
//		System.out.println("Sacados los mappins de: " + m.getName());
//		SimpleQueryBuilder builder = new SimpleQueryBuilder (m);
//		// Metemos solo la primera latitud  y longitud y el radious para buscar en el circulo
//		builder.setCriteria("activity", since, until, lat1, lon1, lat2, lon2, -1);
//	}
//	
//	/**
//	 * Busca actividades concretas en un circulo
//	 * @param label: The activity
//	 * @param lang: The language in which is expresed the activity
//	 * @param lat: The latitude
//	 * @param lon: The longitude
//	 * @param radius: The radius
//	 * @param page: the page
//	 * @param pageSize: The page size
//	 * @throws IOException 
//	 */
//	@GET
//	@Path("actLocCirc")
//	@Produces({MediaType.APPLICATION_JSON})
//	public void searchActLocCirc(String label, String lang, float lat, float lon, float radius, String since, String until, int page, int pageSize) throws IOException{
//		
//		Mapping m = new MappingManager().getMapping("geosparql");
//		System.out.println("Sacados los mappins de: " + m.getName());
//		SimpleQueryBuilder builder = new SimpleQueryBuilder (m);
//		// Metemos solo la primera latitud  y longitud y el radious para buscar en el circulo
//		builder.setCriteria("activity", since, until, lat, lon, -91, -81, radius);
//	}
//	
//	
//	public static void main(String args[]) throws IOException
//    {
//	 StructuredSearch structured_search_demo = new StructuredSearch();
//	 
//	 // Primer metodo que busca cualquier localizacion en un rectangulo
//	 structured_search_demo.sLocRec(51.139725f, -0.895386f, 51.833232f, 0.645447f, "geosparql");
//	 
////	 // Segundo metodo que busca cualquier localizacion en un circulo
////	 structured_search_demo.sLocCirc(51.139725f, -0.895386f, 30, 1, 1);
////	 
////	 // Tercer metodo que busca actividades en un rectangulo
////	 structured_search_demo.searchActLocRec ("", "", 51.139725f, -0.895386f, 51.833232f, 0.645447f, "", "", 1, 1);
//    }

	
}
