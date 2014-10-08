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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeneralSearch extends SocialNetworkInterface {

	@XmlElement(name = "Post")
	ArrayList<SocialNetworkPostListInterface> results = new ArrayList();
	SocialNetworkInterface Driver[];

	public GeneralSearch() {
		try {
			Driver = new SocialNetworkInterface[getClasses("eu.smartfp7.SocialNetworkDriver").length];
			for (int i = 0; i < Driver.length; i++) {
				System.out.println(getClasses("eu.smartfp7.SocialNetworkDriver")[i]);
				try {
					Driver[i] = (SocialNetworkInterface) getClasses("eu.smartfp7.SocialNetworkDriver")[i]
							.newInstance();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Class[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(new File(directory.toString()
					.replaceAll("%20", " ")), packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		System.out.println(directory);
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {

			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

	@Override
	public void SearchForTerm(String queryPar, Integer PageSize) {
		results = new ArrayList();
		System.out.println(queryPar);
		for (int i = 0; i < Driver.length; i++) {
			// System.out.println(getClasses("SocialNetworkDriver")[i]);

			Driver[i].SearchForTerm(queryPar, PageSize / Driver.length);
			results.addAll(Driver[i].getCurrentPageResults());

		}

	}

	@Override
	public ArrayList<SocialNetworkPostListInterface> getCurrentPageResults() {
		ArrayList<SocialNetworkPostListInterface> convertedResults = new ArrayList();

		for (int i = 0; i < results.size(); i++) {
			convertedResults.add(results.get(i));
		}

		return convertedResults;
	}

	@Override
	public void setNextPage() {
		results = new ArrayList();
		for (int i = 0; i < Driver.length; i++) {

			Driver[i].setNextPage();

			results.addAll(Driver[i].getCurrentPageResults());
		}
	}

	public void setPreviousPage() {
		results = new ArrayList();
		for (int i = 0; i < Driver.length; i++) {

			Driver[i].setPreviousPage();

			results.addAll(Driver[i].getCurrentPageResults());
		}
	}

	@Override
	public void SearchForTermUsingGeolocation(String queryPar, int PageSize,
			double latitude, double longitude, double radius) {
		// TODO Auto-generated method stub

	}
}
