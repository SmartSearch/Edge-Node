package eu.smartfp7.linkeddatamanager.logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

import eu.smartfp7.linkeddatamanager.datatypes.Mapping;

public class SPARQLtoJSON {
	
	private static final Logger logger = Logger.getLogger(SPARQLtoJSON.class.getCanonicalName());

	String serviceEndpoint = "";
	Mapping[] mappings;

	public SPARQLtoJSON() {

	}

	public SPARQLtoJSON(String serviceEndpoint, Mapping mapping)
			throws IOException {

		this.serviceEndpoint = serviceEndpoint;

		mappings = new Mapping[1];

		mappings[0] = mapping;
	}

	public SPARQLtoJSON(Mapping[] mapping) throws IOException {

		mappings = mapping;
	}

	private boolean addValue(JSONObject json_object, String key, String value)
			throws JSONException {

		if (json_object.has(key)) {
			JSONArray array_values = json_object.getJSONArray(key);
			int i = 0;
			boolean found = false;
			while ((i < array_values.length()) && (!found)) {
				if (array_values.get(i).equals(value))
					found = true;
				i++;
			}
			if (!found)
				json_object.append(key, value);

			return !found;
		} else {
			json_object.append(key, value);
			return true;
		}
	}

	private boolean addLocation(JSONObject json_object, JSONObject new_json_object) throws JSONException {

//		Class c_new = new_json_object.getClass();
//		logger.info("El tipo del elemento a anadir:" + c_new.getCanonicalName());
//		logger.info("El elemento a anadir" + new_json_object.get("location"));
		
		if (json_object.has("location")) {

			JSONArray array_locations = json_object.getJSONArray("location");
			
			int i = 0;
			boolean found = false;
			while ((i < array_locations.length()) && (!found)) {
				Class c = array_locations.get(i).getClass();
				//System.out.println("El tipo de la clase" + c.getCanonicalName() + new_json_object.get("location").toString());
				if (c.getCanonicalName().equals("org.json.JSONObject")){
					JSONObject aux = (JSONObject) array_locations.get(i);
					if (aux.get("location").toString().equals(new_json_object.get("location").toString())) found = true;
				}else if (c.getCanonicalName().equals("java.lang.String")){
					
					String aux = (String) array_locations.get(i);
					if (aux.equals(new_json_object.get("location"))){
						found = true;
						array_locations.put(i, new_json_object);
					}
				}
				i++;
			}
			if (!found)
				json_object.append("location", new_json_object);

			return !found;
		} else {
			json_object.append("location", new_json_object);
			return true;
		}
	}

	public JSONObject executeSPARQL(String query){

		boolean hasActivity = false;
		boolean hasDate = false;
		boolean hasLocation = false;
		boolean hasSpatialParameters = false;
		// nuevo nines
		boolean hasTxtAttribute = false;

		int attributeVector[] = new int[mappings.length];

		// try {

		Resource res;

		JSONObject result = new JSONObject();

		JSONObject objects = new JSONObject();

		ResultSet results;

		if (!serviceEndpoint.contentEquals(""))
			results = QueryExecutionFactory.sparqlService(serviceEndpoint, query).execSelect();
		else
			results = QueryExecutionFactory.create(QueryFactory.create(query),
					new DatasetImpl(ModelFactory.createDefaultModel()))
					.execSelect();

	
		logger.info("Successed in the query, getting "+ results.getRowNumber()+" results");
		QuerySolution tuple = null;

		String currentID = "";

		JSONObject current = null;

		List<String> vars = results.getResultVars();
		
		ArrayList<QuerySolution> errList = new ArrayList<QuerySolution>();

		for (String var : vars) {
		
			if (var.contentEquals("activity")){
				hasActivity = true;
				logger.info("There is activity");
			}
			// nuevo nines
			if (var.contentEquals("txt")){
				hasTxtAttribute = true;
				logger.info("There are txts");
			}
			if (var.contentEquals("date")){
				hasDate = true;
				logger.info("There is date");
			}
			if (var.contentEquals("location")){
				hasLocation = true;
				logger.info("There is location");
			}
			if (var.contentEquals("lat") || var.contentEquals("long")){
				hasSpatialParameters = true;
				logger.info("There are SpatialParameters");
			}
			if (var.contains("attribute")) {
				logger.info("There are attributes");
				if (serviceEndpoint == "")
					attributeVector[Integer.parseInt("" + var.charAt(9))]++;
				else {
					attributeVector[0]++;
				}
			}

		}
	
		if (hasActivity) {
			// Inicializamos el objecto JSON que contendra las actividades
			try {
				objects.put("activities", new JSONObject());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (results.hasNext()) {

				tuple = results.next();
		
				try {
					
					
					currentID = tuple.getResource("activity").getURI();
//					logger.info("-----------Entidad------------");
//					logger.info("La entidad: " + currentID);
					if (((JSONObject) objects.get("activities")).has(currentID)){
						// Esto es para cuando el objeto json ya esta en las activities
						current = (JSONObject) ((JSONObject) objects.get("activities")).get(currentID);
//						logger.info("La entidad: " + currentID);
					}
					else{
						current = new JSONObject();
						System.out.println("La entidad: " + currentID);
//						logger.info("*********************************Entidad Nueva***************************************");
//						logger.info("La entidad: " + currentID);
					}
						
//					if (!currentID.contentEquals(tuple.getResource("activity")
//							.getURI())) {
//						if (current != null) {
//							objects.accumulate("activity", current);
//							System.out.println("Cambiamos de actividad-----------------------");
//						}
//						current = new JSONObject();
//						currentID = tuple.getResource("activity").getURI();
//						current.put("uri", currentID);
//						System.out.println("La entidad: " + currentID);
//						logger.info("*********************************Entidad Nueva***************************************");
//						logger.info("La entidad: " + currentID);
//						
//					}
					

					if ((hasTxtAttribute) && (tuple.contains("txt"))) {
						// current.append ("txt",tuple.getLiteral("txt"));
						addValue(current, "txt", tuple.getLiteral("txt").getValue().toString());
					}
					
					for (int i = 0; i < mappings.length; i++){
						for (int j = 0; j < attributeVector[i]; j++) {
							res = ResourceFactory.createResource(mappings[i].getAttributePredicate(j));
							if (mappings.length == 1) {
								if (tuple.contains("attribute" + j))
									// current.append(res.getLocalName(),tuple.getLiteral("attribute"+j).getValue().toString());
									addValue(current, res.getLocalName(), tuple.get("attribute" + j).toString());
							} else {
								if (tuple.contains("attribute" + j))
									// current.append(res.getLocalName(),tuple.getLiteral("attribute"+i+""+j).getValue().toString());
									addValue(current,res.getLocalName(),tuple.get("attribute" + i + "" + j).toString());
							}
						}
					}// Fin del for exterior

					if ((hasDate) && (tuple.contains("date")))
						// current.append
						// ("date",tuple.getLiteral("date").getValue().toString());
						addValue(current, "date", tuple.getLiteral("date").getValue().toString());

					if (hasLocation) {
						// Necesitamos comprobar que tiene valor porque ahora
						// son optionales
						if ((hasSpatialParameters) && ((tuple.contains("lat")) && (tuple.contains("long")))){
							// current.put("location", new
							// JSONObject().put("location",tuple.getResource("location").getURI()).put("lat",tuple.get("lat").toString()).put("long",tuple.get("long").toString()));
							addLocation(current, new JSONObject().put("location", tuple.getResource("location").getURI())
											.put("lat",tuple.get("lat"))
											.put("long",tuple.get("long")));
						}else{
							// current.put("location",tuple.get("location").toString());
							addValue(current, "location", tuple.get("location").toString());
						}
					} // Fin del has location
					
					// Y acumulamos 
					((JSONObject) objects.get("activities")).put(currentID, current);
					
				} catch (Exception e) {
					errList.add(tuple);
					Iterator iter = errList.iterator();
					while (iter.hasNext())
						logger.info((String) iter.next());
					e.printStackTrace();
				}

			}// FIn del while
		}

		else if (hasLocation) {

			// Inicializamos el objecto JSON que contendra las actividades
			try {
				objects.put("locations", new JSONObject());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while (results.hasNext()) {

				tuple = results.next();

				try{
						currentID = tuple.getResource("location").getURI();
						if (((JSONObject) objects.get("locations")).has(currentID)){
							// Esto es para cuando el objeto json ya esta en las activities
							current = (JSONObject) ((JSONObject) objects.get("locations")).get(currentID);
						}
						else{
							current = new JSONObject();
							System.out.println("La entidad: " + currentID);
//							logger.info("*********************************Entidad Nueva***************************************");
//							logger.info("La entidad: " + currentID);
						}
						
					
//						if (!currentID.contentEquals(tuple.getResource("location").getURI())) {
//							if (current != null) {
//								objects.accumulate("location", current);
//								System.out.println("Cambiamos de venue-----------------------");
//							}
//							current = new JSONObject();
//							currentID = tuple.getResource("location").getURI();
//							current.put("uri", currentID);
//		
//						}
						
						if ((hasSpatialParameters) && ((tuple.contains("lat")) && (tuple.contains("long")))){
							//current.put("hasLocation", new JSONObject().put("location",tuple.getResource("location").getURI()).put("lat", tuple.getResource("lat").toString()).put("long",tuple.getResource("long").toString()));
							addLocation (current, new JSONObject().put("location",tuple.getResource("location").getURI()).
									put("lat", tuple.get("lat")).
									put("long", tuple.get("long")));
						}
						else
							//current.put("hasLocation", tuple.getResource("location").getURI());
							addValue (current, "location", tuple.get("location").toString());
						

						// Nuevo nines
						if ((hasTxtAttribute) && (tuple.contains("txt")))
							addValue(current, "txt", tuple.getLiteral("txt").getValue().toString());
						

						for (int i = 0; i < mappings.length; i++){
							for (int j = 0; j < attributeVector[i]; j++) {
								res = ResourceFactory.createResource(mappings[i].getAttributeLocationsPredicate(j));
								if (mappings.length == 1) {
									if (tuple.contains("attribute" + j))
										// current.append(res.getLocalName(),tuple.getLiteral("attribute"+j).getValue().toString());
										//addValue(current, res.getLocalName(), tuple.getLiteral("attribute" + j).getValue().toString());
										addValue(current, res.getLocalName(), tuple.get("attribute" + j).toString());
								} else {
									if (tuple.contains("attribute" + j))
										// current.append(res.getLocalName(),tuple.getLiteral("attribute"+i+""+j).getValue().toString());
										//addValue(current,res.getLocalName(),tuple.getLiteral("attribute" + i + "" + j).getValue().toString());
										addValue(current,res.getLocalName(),tuple.get("attribute" + i + "" + j).toString());
								}
							}
						}// Fin del for exterior
						

						// Y acumulamos 
						((JSONObject) objects.get("locations")).put(currentID, current);
						
				} catch (Exception e) {
					
					errList.add(tuple);
					Iterator iter = errList.iterator();
					while (iter.hasNext())
						logger.info((String) iter.next());
					e.printStackTrace();
				}// fin del try
				
				
			} // fin del while

		} // fin del if

		try{
			objects.put("time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").format(new Date()));
			result.put("data", objects);
			logger.info("Mapped to JSON");
			return result;
		}catch (Exception e){
			 e.printStackTrace();
		}


		return null;
	}

}
