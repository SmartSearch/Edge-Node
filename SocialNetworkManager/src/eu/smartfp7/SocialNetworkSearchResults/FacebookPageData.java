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
package eu.smartfp7.SocialNetworkSearchResults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.restfb.types.Page;

@XmlRootElement
public class FacebookPageData {
	@XmlElement
	String Name;
	@XmlElement
	String ID;
	@XmlElement
	String Category;
	@XmlElement
	String Description;
	@XmlElement
	String Location;

	public FacebookPageData() {
	}

	public FacebookPageData(Page FBpage) {
		Name = FBpage.getName();
		Category = FBpage.getCategory();
		ID = FBpage.getId();
		Description = FBpage.getDescription();
		if (FBpage.getLocation() != null)
			Location = FBpage.getLocation().toString();
	}
}
