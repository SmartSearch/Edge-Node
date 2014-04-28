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
import eu.smartfp7.linkeddatamanager.logic.TextQueryBuilder;
import eu.smartfp7.linkeddatamanager.logic.SPARQLtoJSON;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.DefaultValue;



@Path("txtSearch")
public class TextSearch {

	private static final Logger logger = Logger.getLogger(TextSearch.class.getCanonicalName());
	
//	public void searchURIs(String label, String lang, int page, int pageSize){
//		
//	}
//	
//	
//	public void searchTriples(String label, String lang, int page,int pageSize) throws IOException{
//
//	}
	
	
	/**
	 * Finding activities identified by a certain keywords
	 * @param label: The keywords to search
	 * @param lang: The language of the keywords
	 * @param dataset: The dataset in which perform the search. By default DBpedia
	 * @return A JSON file format with the found activities
	 * @throws IOException
	 */
	@GET
	@Path("activities")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response searchActivity (@QueryParam("label") String label,
									@QueryParam("lang") @DefaultValue("en") String lang,
									@QueryParam("dataset") @DefaultValue("dbpedia") String dataset) throws IOException{
		//public void searchActivity(String label, String lang, int page, int pageSize) throws IOException{
		InputStream in = null;
		Mapping m = new MappingManager().getMapping(dataset);
		logger.info("Invoking searchActivity by text-search");
		logger.info("Dataset: " + dataset);
		logger.info("Label: " + label);
		logger.info("Language: " + lang);
		
		TextQueryBuilder builder = new TextQueryBuilder (m);
		builder.setCriteria(true, false, label, lang );
		String query = builder.printQuery ();

		SPARQLtoJSON queryer = new SPARQLtoJSON (m.getEndpoint(), m);
		try{
			JSONObject res = queryer.executeSPARQL(query);
			//System.out.println("Los resultados: " + res);
			in = new ByteArrayInputStream(res.toString().getBytes());	
		}
		catch (Exception e){
			e.printStackTrace();
			logger.info("Something wrong with the invocation of searchActivity by text-search");
			return Response.serverError().build();
		}
		logger.info("Invoked searchActivity by text-search");
		return Response.ok(in).build();
		
	}
	
	/**
	 * Finding venues identified by a certain keywords
	 * @param label: The keywords to search
	 * @param lang: The language of the keywords
	 * @param dataset: The dataset in which perform the search. By default DBpedia
	 * @return A JSON file format with the found venues
	 */
	//public void seachLocation(String label, String lang, int page, int pageSize){
	@GET
	@Path("venues")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response searchLocation (@QueryParam("label") String label,
									@QueryParam("lang") @DefaultValue("en") String lang,
									@QueryParam("dataset") @DefaultValue("dbpedia") String dataset) throws IOException{
		
		logger.info("Invoking searchLocation by text-search");
		logger.info("Dataset: " + dataset);
		logger.info("Label: " + label);
		logger.info("Language: " + lang);
		
		InputStream in = null;
		Mapping m = new MappingManager().getMapping(dataset);
		
		TextQueryBuilder builder = new TextQueryBuilder (m);
		builder.setCriteria(false, true, label, lang );
		String query = builder.printQuery ();

		SPARQLtoJSON queryer = new SPARQLtoJSON (m.getEndpoint(), m);
		try{
			JSONObject res = queryer.executeSPARQL(query);
			//System.out.println("Los resultados: " + res);
			in = new ByteArrayInputStream(res.toString().getBytes());	
		}
		catch (Exception e){
			e.printStackTrace();
			logger.info("Something wrong with the invocation of searchLocation by text-search");
			return Response.serverError().build();
		}
		logger.info("Invoked searchLocation by text-search");
		return Response.ok(in).build();
		
	}
	
	public static void main(String args[]) throws IOException
	    {
		 TextSearch text_search_demo = new TextSearch();
		 text_search_demo.searchActivity("new york", "en", "dbpedia");
	    }
	
}
