/* 
 * SMART FP7 - Search engine for MultimediA enviRonment generated contenT
 * Webpage: http://smartfp7.eu
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * The Original Code is Copyright (c) 2012-2013 of Telesto Technologies
 * All Rights Reserved
 *
 * Contributor(s):
 *  Xristos Smailis <smailisxristos@yahoo.com>
 *  Thanos Alexiou <thanos@telesto.gr>
 */
package eu.smartfp7.SocialNetworkManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.WebServiceContext;

// POJO, no interface no extends

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests by default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/General")
public class SocialNetworkManagerRest {
	private static String Term;
	private static int pageSize;

	public SocialNetworkManagerRest() {

	}


	@Context
	HttpServletRequest request;

	HttpSession session;

	@GET
	@Path("/Posts")
	@Produces(MediaType.TEXT_XML)
	public String generalSearch(@Context HttpServletRequest request,
			@QueryParam("term") String Term,
			@DefaultValue("25") @QueryParam("pagesize") int pageSize) {

		session = request.getSession(true);
		session.setAttribute("currentSearch", new GeneralSearch());
		session = request.getSession(true);

		GeneralSearch search = (GeneralSearch) session
				.getAttribute("currentSearch");
		search.SearchForTerm(Term, pageSize);
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(GeneralSearch.class);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(search, sw);
			return sw.toString();
		} catch (JAXBException e) { // TODO
			e.printStackTrace();
			return e.toString();
		}

	}

	@GET
	@Path("{Network}/Posts")
	@Produces(MediaType.TEXT_XML)
	public String NetworkSpecificSearch(@Context HttpServletRequest request,
			@PathParam("Network") String DriverName,
			@QueryParam("term") String Term,
			@DefaultValue("25") @QueryParam("pagesize") int pageSize) {

		JAXBContext context;
		JAXBContext jaxbContext = null;

		try {
			jaxbContext = JAXBContext.newInstance(DriverSpecificCall.class);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String searchMethod = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><driverSpecificCall><ClassName>"
				+ DriverName
				+ "</ClassName><MethodName>SearchForTerm</MethodName><ArgTypes>java.lang.String</ArgTypes><ArgTypes>java.lang.Integer</ArgTypes><ArgValues>"
				+ Term
				+ "</ArgValues><ArgValues>"
				+ pageSize
				+ "</ArgValues></driverSpecificCall>";
		ByteArrayInputStream input = new ByteArrayInputStream(
				searchMethod.getBytes());
		try {

			DriverSpecificCall call = (DriverSpecificCall) jaxbUnmarshaller
					.unmarshal(input);
			Object Driver = call.invoke();
			// GeneralSearch tmp = new GeneralSearch();

			// tmp.SearchForTerm("greece", 20);
			// }
			session = request.getSession(true);
			session.setAttribute("currentSearch", Driver);
			try {
				context = JAXBContext.newInstance(Class
						.forName("eu.smartfp7.SocialNetworkDriver." + call.ClassName));

				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				m.marshal(Driver, sw);
				return sw.toString();
			} catch (Exception e) { // TODO
				e.printStackTrace();
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("{Network}/Posts/nextpage")
	@Produces(MediaType.TEXT_XML)
	public String NetworkSpecificSearchNext(
			@Context HttpServletRequest request,
			@PathParam("Network") String DriverName) {
		session = request.getSession(true);

		Object search = session.getAttribute("currentSearch");

		JAXBContext context;
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(DriverSpecificCall.class);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String searchMethod = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><driverSpecificCall><ClassName>"
				+ DriverName
				+ "</ClassName><MethodName>setNextPage</MethodName></driverSpecificCall>";
		ByteArrayInputStream input = new ByteArrayInputStream(
				searchMethod.getBytes());
		try {

			DriverSpecificCall call = (DriverSpecificCall) jaxbUnmarshaller
					.unmarshal(input);
			call.invoke(search);
			// GeneralSearch tmp = new GeneralSearch();

			// tmp.SearchForTerm("greece", 20);
			// }

			try {
				context = JAXBContext.newInstance(Class
						.forName("eu.smartfp7.SocialNetworkDriver." + call.ClassName));

				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				m.marshal(search, sw);
				return sw.toString();
			} catch (Exception e) { // TODO
				e.printStackTrace();
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("{Network}/Posts/previouspage")
	@Produces(MediaType.TEXT_XML)
	public String NetworkSpecificSearchPrevious(
			@Context HttpServletRequest request,
			@PathParam("Network") String DriverName) {
		session = request.getSession(true);

		Object search = session.getAttribute("currentSearch");

		JAXBContext context;
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(DriverSpecificCall.class);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String searchMethod = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><driverSpecificCall><ClassName>"
				+ DriverName
				+ "</ClassName><MethodName>setPreviousPage</MethodName></driverSpecificCall>";
		ByteArrayInputStream input = new ByteArrayInputStream(
				searchMethod.getBytes());
		try {

			DriverSpecificCall call = (DriverSpecificCall) jaxbUnmarshaller
					.unmarshal(input);
			call.invoke(search);
			// GeneralSearch tmp = new GeneralSearch();

			// tmp.SearchForTerm("greece", 20);
			// }

			try {
				context = JAXBContext.newInstance(Class
						.forName("eu.smartfp7.SocialNetworkDriver." + call.ClassName));

				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				m.marshal(search, sw);
				return sw.toString();
			} catch (Exception e) { // TODO
				e.printStackTrace();
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/Posts/nextpage")
	@Produces(MediaType.TEXT_XML)
	public String nextPage(@Context HttpServletRequest request) {
		session = request.getSession(true);

		GeneralSearch search = (GeneralSearch) session
				.getAttribute("currentSearch");
		// search.SearchForTerm(Term, pageSize);
		search.setNextPage();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(GeneralSearch.class);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(search, sw);
			return sw.toString();
		} catch (JAXBException e) { // TODO
			return e.toString();
		}

	}

	@GET
	@Path("/Posts/previouspage")
	@Produces(MediaType.TEXT_XML)
	public String previousPage(@Context HttpServletRequest request) {
		session = request.getSession(true);

		GeneralSearch search = (GeneralSearch) session
				.getAttribute("currentSearch");
		// search.SearchForTerm(Term, pageSize);
		search.setPreviousPage();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(GeneralSearch.class);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(search, sw);
			return sw.toString();
		} catch (JAXBException e) { // TODO
			return e.toString();
		}

	}

	@PUT
	@Path("/NonStandard/DriverMethod")
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public String DriverSpecific(InputStream requestBodyStream) {
		try {

			JAXBContext jaxbContext = JAXBContext
					.newInstance(DriverSpecificCall.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			DriverSpecificCall call = (DriverSpecificCall) jaxbUnmarshaller
					.unmarshal(requestBodyStream);

			Object tmp = call.invoke();
			// GeneralSearch tmp = new GeneralSearch();

			// tmp.SearchForTerm("greece", 20);
			JAXBContext context;
			// }

			try {
				context = JAXBContext.newInstance(Class
						.forName("eu.smartfp7.SocialNetworkDriver." + call.ClassName));

				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				m.marshal(tmp, sw);
				return sw.toString();
			} catch (Exception e) { // TODO
				e.printStackTrace();
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Context
	  UriInfo uri;
	  
	@GET
	@Path("Filter/{FilterName}")
	@Produces("text/rdf")
	public String filterQuery(@Context HttpServletRequest request,
			@PathParam("FilterName") String FilterClassName,
			@QueryParam("term") String Term) {

		SocialNetworkFilter Filter;

		try {
			Filter = (SocialNetworkFilter) Class.forName(
					"eu.smartfp7.SocialNetworkFilters." + FilterClassName).newInstance();
			return Filter.filter(Term,uri.getBaseUri().toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
}