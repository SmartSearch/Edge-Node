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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import com.restfb.types.Post;

import eu.smartfp7.SocialNetworkSearchResults.FacebookPostData;

public abstract class SocialNetworkInterface {

	public abstract void SearchForTerm(String queryPar, Integer PageSize);
	 
	public abstract void SearchForTermUsingGeolocation(String queryPar,
			int PageSize, double latitude, double longitude, double radius);

	public abstract ArrayList<SocialNetworkPostListInterface> getCurrentPageResults();

	public abstract void setNextPage();

	public abstract void setPreviousPage();
}
