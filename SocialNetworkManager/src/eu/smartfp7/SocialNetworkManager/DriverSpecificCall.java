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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DriverSpecificCall {
	@XmlElement(name = "ClassName")
	String ClassName = new String();
	@XmlElement(name = "MethodName")
	String MethodName = new String();
	@XmlElement(name = "ArgTypes")
	ArrayList<String> ArgTypes = new ArrayList();
	@XmlElement(name = "ArgValues")
	ArrayList<String> ArgValues = new ArrayList();

	public Object invoke() {
		Class DriverType;
		try {
			DriverType = Class.forName("eu.smartfp7.SocialNetworkDriver." + ClassName);

			Object Driver = DriverType.newInstance();
			
			Class[] types = new Class[ArgTypes.size()];
			
			for (int i = 0; i < ArgTypes.size(); i++) {
				types[i] = Class.forName(ArgTypes.get(i));
			}
			// get the method
			Object[] Args = new Object[ArgTypes.size()];
			Method method = DriverType.getMethod(MethodName, types);
			for (int i = 0; i < ArgTypes.size(); i++) {
				Constructor constructor = types[i].getConstructor(Class
						.forName("java.lang.String"));
				Args[i] = constructor.newInstance(ArgValues.get(i));
			}
			method.invoke(Driver, Args);
			return Driver;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Object invoke(Object Driver) {
		Class DriverType;
		try {
			DriverType = Class.forName("eu.smartfp7.SocialNetworkDriver." + ClassName);

			if (ArgTypes.size() > 0) {
				Class[] types = new Class[ArgTypes.size()];
				for (int i = 0; i < ArgTypes.size(); i++) {
					types[i] = Class.forName(ArgTypes.get(i));
				}
				// get the method
				Object[] Args = new Object[ArgTypes.size()];
				Method method = DriverType.getMethod(MethodName, types);
				for (int i = 0; i < ArgTypes.size(); i++) {
					Constructor constructor = types[i].getConstructor(Class
							.forName("java.lang.String"));
					Args[i] = constructor.newInstance(ArgValues.get(i));
				}
				method.invoke(Driver, Args);
			} else {
				Method method = DriverType.getMethod(MethodName);
				method.invoke(Driver);
			}

			return Driver;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
