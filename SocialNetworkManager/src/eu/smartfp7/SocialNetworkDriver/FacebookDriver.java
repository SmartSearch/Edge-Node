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

package eu.smartfp7.SocialNetworkDriver;

import eu.smartfp7.SocialNetworkManager.SocialNetworkInterface;
import eu.smartfp7.SocialNetworkManager.SocialNetworkPostListInterface;
import eu.smartfp7.SocialNetworkSearchResults.FacebookPageData;
import eu.smartfp7.SocialNetworkSearchResults.FacebookPostData;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Page;
import com.restfb.types.Post;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * 
 * @author OnTheSpot
 */
@XmlRootElement(name = "FacebookSearch")
public class FacebookDriver extends SocialNetworkInterface {
	@XmlElement(name = "FacebookPost")
	ArrayList<FacebookPostData> results;
	@XmlElement(name = "FacebookPage")
	ArrayList<FacebookPageData> resultPages;
	private Connection<Post> publicSearch;
	private FacebookClient facebookClient;
	private Date OldestDate = new Date();

	public void SearchForTermUsingGeolocation(String queryPar, int PageSize,
			double latitude, double longitude, double radius) {
		if (queryPar != null) {
			facebookClient = new DefaultFacebookClient();
			publicSearch = facebookClient.fetchConnection("search", Post.class,
					Parameter.with("q", queryPar),
					Parameter.with("type", "post"),
					Parameter.with("limit", PageSize),
					Parameter.with("place&center", latitude + "," + longitude),
					Parameter.with("distance", radius));

			for (int i = 0; i < publicSearch.getData().size(); i++) {
				if (publicSearch.getData().get(i).getMessage() != null) {
					results.add(new FacebookPostData(publicSearch.getData()
							.get(i)));
					OldestDate = publicSearch.getData().get(i).getCreatedTime();
				}
			}

		}
	}

	public void setNextPage() {

		results = new ArrayList();
		/*
		 * publicSearch = facebookClient.fetchConnectionPage(
		 * publicSearch.getNextPageUrl(), Post.class);
		 */
		facebookClient = new DefaultFacebookClient();
		publicSearch = facebookClient.fetchConnectionPage(
				publicSearch.getNextPageUrl(), Post.class);

		for (int i = 0; i < publicSearch.getData().size(); i++) {
			if (publicSearch.getData().get(i).getMessage() != null) {
				results.add(new FacebookPostData(publicSearch.getData().get(i)));
			}
		}
	}

	public void setPreviousPage() {

		results = new ArrayList();
		/*
		 * publicSearch = facebookClient.fetchConnectionPage(
		 * publicSearch.getNextPageUrl(), Post.class);
		 */
		facebookClient = new DefaultFacebookClient();
		publicSearch = facebookClient.fetchConnectionPage(
				publicSearch.getPreviousPageUrl(), Post.class);

		for (int i = 0; i < publicSearch.getData().size(); i++) {
			if (publicSearch.getData().get(i).getMessage() != null) {
				results.add(new FacebookPostData(publicSearch.getData().get(i)));
			}
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
	public void SearchForTerm(String queryPar, Integer PageSize) {
		if (queryPar != null) {
			results = new ArrayList();
			facebookClient = new DefaultFacebookClient();
			publicSearch = facebookClient.fetchConnection("search", Post.class,
					Parameter.with("q", queryPar),
					Parameter.with("type", "post"),
					Parameter.with("limit", PageSize));

			for (int i = 0; i < publicSearch.getData().size(); i++) {
				if (publicSearch.getData().get(i).getMessage() != null) {
					results.add(new FacebookPostData(publicSearch.getData()
							.get(i)));
				}
			}

		}

	}

	public void SearchFacebookPages(String queryPar, Integer PageSize) {
		Connection<Page> publicSearch;
		if (queryPar != null) {
			resultPages = new ArrayList();
			facebookClient = new DefaultFacebookClient();
			publicSearch = facebookClient.fetchConnection("search", Page.class,
					Parameter.with("q", queryPar),
					Parameter.with("type", "page"),
					Parameter.with("limit", PageSize));

			for (int i = 0; i < publicSearch.getData().size(); i++) {
				if (publicSearch.getData().get(i).getName() != null) {
					resultPages.add(new FacebookPageData(publicSearch.getData()
							.get(i)));
				}
			}

		}

	}
}
