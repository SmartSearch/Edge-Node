package eu.smartfp7.linkeddatamanager.logic;

import java.io.IOException;
import java.util.logging.Logger;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;
import eu.smartfp7.linkeddatamanager.interfaces.StructuredSearch;


public class TextQueryBuilder {
	

		private static final Logger logger = Logger.getLogger(TextQueryBuilder.class.getCanonicalName());
		Mapping m;
		Boolean activity = false;
		Boolean venue = false;
		String keywords = "";
		String lang = "en";
		String query = "";
		
		public TextQueryBuilder (){
			this.m = null;
		
		}
		
		public TextQueryBuilder (String mapName) throws IOException{
			
			m = new MappingManager().getMapping(mapName);
			
		}
		
		
		public TextQueryBuilder (Mapping m){
			
				this.m = m;
			
		}
		

		
		
		public void setCriteria (Boolean activity, Boolean venue, String keywords, String lang){
			
			this.activity = activity;
			this.venue = venue;
			this.keywords = keywords;
			this.lang = lang;
	
					
		}




		public String printQuery(){
			
			logger.info("Building the query for: activity"+ this.activity+" locations: "+ this.venue+" keywords: "+this.keywords);
			
			query = "";
			String entity_name = "location";
			
			for(int i = 0;i<m.getPrefixSize();i++){
				
				query = query +
						"PREFIX "+m.getPrefixName(i)+": <"+m.getPrefixURI(i)+">\n";
			}
			
			query = query +
						"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
						"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"+
						"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n";
				
			query = query +"SELECT";
			
			
			if (this.activity){
				query = query + " ?activity";
				
				for(int i = 0;i<m.getAttributeSize();i++)				
					query = query + " ?attribute"+i;
				
				if(m.hasDate())
					query = query +" ?date";
			}
			
			if (this.venue){
				for(int i = 0;i<m.getAttributeLocationsSize();i++)				
					query = query + " ?attribute"+i;
			}
			

			if(m.hasLocation())
				query = query +" ?location";
			
			if (m.hasTxtAttribute())
				query = query +" ?txt";
				
			
			if(m.hasSpatialProperties())
				query = query + " ?lat ?long";
			

			query = query + "\nWHERE{\n";
			
			if (this.activity){
				
				entity_name = "activity";
				query = query +
						"	?activity rdf:type "+m.getActivityType()+".\n";
				
				// Buscamos las locations de las actividades
				if(m.hasLocation()){
					if (m.getLocationsSize() >1){
						query = query + "	{\n";
						for(int i = 0;i<m.getLocationsSize();i++){
							if (i == 0) query = query +"		{?activity "+m.getLocationsPredicate(i)+" ?location}\n";
							else query = query +"		UNION	{?activity "+m.getLocationsPredicate(i)+" ?location}\n";
						}
						query = query + "	}.\n";
					}else{
						query = query +
							"	?activity "+m.getLocationPred()+" ?location.\n";
					}
				}
				
				
				// Buscamos los parametros opcionales de las actividades
				for(int i = 0;i<m.getAttributeSize();i++)
					query = query +
						"	OPTIONAL {?activity "+m.getAttributePredicate(i)+" ?attribute"+i+"}.\n";
				
				if(m.hasDate())
					query = query +
						"	OPTIONAL {?activity "+m.getDatePred()+" ?date }.\n";
			
			}
			
			if (this.venue){
				
				// Buscamos los parametros opcionales de las actividades
				for(int i = 0;i<m.getAttributeLocationsSize();i++)
					query = query +
						"	OPTIONAL {?location "+m.getAttributeLocationsPredicate(i)+" ?attribute"+i+"}.\n";
			}

			
			// Buscamos las keywords en los campos de texto
			if (m.hasTxtAttribute()){
				query = query + "	{\n";
				for(int i = 0; i<m.getTxtAttributeSize(); i++){
					if (i==0)
						query = query + 
							"		{?"+entity_name+" "+m.getTxtAttributePredicate(i)+" ?txt." +
							"	FILTER langMatches ( lang(?txt), \""+ this.lang +"\" )." +
							"	FILTER regex ( ?txt, "+ this.keywords + ",\"i\")} \n";
					else
						query = query + 
							"		UNION	{?"+entity_name+" "+m.getTxtAttributePredicate(i)+" ?txt." +
							"	FILTER langMatches ( lang(?txt), \""+ this.lang +"\" )." +
							"	FILTER regex ( ?txt, "+ this.keywords + ",\"i\")} \n";
				}
				query = query + "	}.\n";
			}
			

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
			
			// Ponemos los parametros opcionales en los dos casos
			if(m.hasSpatialProperties()){
				query = query +
					"	OPTIONAL {?location "+m.getLatpred()+ " ?lat. ?location "+m.getLongpred()+" ?long}.\n";
			}
			
			
			query = query +
					"}";
			
			logger.info("The query: "+query);
			
			return query;
		}
		
	


}
