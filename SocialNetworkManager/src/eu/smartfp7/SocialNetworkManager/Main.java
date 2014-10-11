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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import eu.smartfp7.SocialNetworkDriver.*;
import java.lang.String;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DriverSpecificCall call = new DriverSpecificCall();
		call.ClassName = "FacebookDriver";
		call.MethodName = "SearchForPage";

		call.ArgTypes.add("java.lang.String");
		call.ArgTypes.add("java.lang.Integer");
		call.ArgValues.add("conference");
		call.ArgValues.add("20");
		Object tmp = call.invoke();
		// GeneralSearch tmp = new GeneralSearch();

		// tmp.SearchForTerm("greece", 20);
		 JAXBContext context;
		// }

		try {
			context = JAXBContext.newInstance( Class.forName("SocialNetworkDriver." + call.ClassName));

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(tmp, System.out);
		} catch (Exception e) { // TODO
			e.printStackTrace();
		}
		/*
		 * System.err.println("hhh");
		 * 
		 * tmp.setNextPage(); try { context =
		 * JAXBContext.newInstance(GeneralSearch.class);
		 * 
		 * Marshaller m = context.createMarshaller();
		 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		 * m.marshal(tmp, System.out); } catch (JAXBException e) { // TODO
		 * e.printStackTrace(); }
		 * 
		 * /* TwitterSearch TwitterManager = new TwitterSearch();
		 * TwitterManager.SearchForTermUsingGeolocation("ελλάδα", 20, 39, 22,
		 * 1000); JAXBContext context; try { context =
		 * JAXBContext.newInstance(TwitterSearch.class);
		 * 
		 * Marshaller m = context.createMarshaller();
		 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		 * m.marshal(TwitterManager, System.out); } catch (JAXBException e) { //
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * TwitterManager.setNextPage(); try { context =
		 * JAXBContext.newInstance(TwitterSearch.class);
		 * 
		 * Marshaller m = context.createMarshaller();
		 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		 * m.marshal(TwitterManager, System.out); } catch (JAXBException e) { //
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		/*
		 * FacebookSearch FacebookManager = new FacebookSearch();
		 * FacebookManager.SearchForTerm("ελλάδα",25);
		 * 
		 * try { JAXBContext context =
		 * JAXBContext.newInstance(FacebookSearch.class);
		 * 
		 * Marshaller m = context.createMarshaller();
		 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		 * m.marshal(FacebookManager, System.out); } catch (JAXBException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * FacebookManager.setNextPage(); // System.out.println(x);
		 * 
		 * try { JAXBContext context =
		 * JAXBContext.newInstance(FacebookSearch.class);
		 * 
		 * Marshaller m = context.createMarshaller();
		 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		 * m.marshal(FacebookManager, System.out); } catch (JAXBException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

	}
}
