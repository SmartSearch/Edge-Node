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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.smartfp7.SocialNetworkManager.SocialNetworkInterface;
import eu.smartfp7.SocialNetworkManager.SocialNetworkPostListInterface;
import eu.smartfp7.SocialNetworkSearchResults.FacebookPostData;
import eu.smartfp7.SocialNetworkSearchResults.TwitterPostData;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

@XmlRootElement(name = "TwitterSearch")
public class TwitterDriver extends SocialNetworkInterface {
	private Twitter twitter = new TwitterFactory().getInstance();

	private String queryPar;
	private int pageSize = 25;

	@XmlElement(name = "TwitterPost")
	public ArrayList<TwitterPostData> results = new ArrayList<TwitterPostData>();

	private int pageIndex = 1;

	@Override
	public void SearchForTermUsingGeolocation(String queryPar, int PageSize,
			double latitude, double longitude, double radius) {
		try {
			results = new ArrayList<TwitterPostData>();

			if (queryPar != null) {
				this.pageSize = PageSize;
				this.queryPar = queryPar;
				Query query = new Query(queryPar);
				query.setPage(pageIndex);
				query.setRpp(PageSize);
				query.setResultType(Query.RECENT);
				query.setGeoCode(new GeoLocation(latitude, longitude), radius,
						"km");
				QueryResult result = twitter.search(query);
				ArrayList tweets = (ArrayList) result.getTweets();
				for (int i = 0; i < tweets.size(); i++) {
					results.add(new TwitterPostData((Tweet) tweets.get(i)));
				}

			}
		} catch (TwitterException ex) {
			System.err.println("Twitter Error");
			// Logger.getLogger(TwitterSearch.class.getName()).log(Level.SEVERE,
			// null, ex);
		}

	}

	@Override
	public void setNextPage() {
		results = new ArrayList<TwitterPostData>();
		pageIndex++;

		Query query = new Query(queryPar);
		query.setRpp(pageSize);
		query.setResultType(Query.RECENT);
		query.setPage(pageIndex);

		QueryResult result;
		try {
			result = twitter.search(query);
			ArrayList tweets = (ArrayList) result.getTweets();

			for (int i = 0; i < tweets.size(); i++) {
				results.add(new TwitterPostData((Tweet) tweets.get(i)));
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPreviousPage() {
		results = new ArrayList<TwitterPostData>();
		pageIndex--;

		Query query = new Query(queryPar);
		query.setRpp(pageSize);
		query.setResultType(Query.RECENT);
		query.setPage(pageIndex);

		QueryResult result;
		try {
			result = twitter.search(query);
			ArrayList tweets = (ArrayList) result.getTweets();

			for (int i = 0; i < tweets.size(); i++) {
				results.add(new TwitterPostData((Tweet) tweets.get(i)));
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<SocialNetworkPostListInterface> getCurrentPageResults() {
		ArrayList<SocialNetworkPostListInterface> convertedResults = new ArrayList();

		for (int i = 0; i < results.size(); i++) {
			convertedResults.add(results.get(i));
		}

		return convertedResults;
	}


	@Override
	public void SearchForTerm(String queryPar, Integer PageSize) {
		Query query=null;
		try {
			results = new ArrayList<TwitterPostData>();
			this.pageSize = PageSize;
			if (queryPar != null) {

				this.queryPar = queryPar.replace("%24","#");
				System.out.println(this.queryPar);
				query = new Query(this.queryPar);
				query.setPage(pageIndex);
				query.setRpp(PageSize);
				query.setResultType(Query.RECENT);
				QueryResult result = twitter.search(query);
				ArrayList tweets = (ArrayList) result.getTweets();
				for (int i = 0; i < tweets.size(); i++) {
					results.add(new TwitterPostData((Tweet) tweets.get(i)));
				}

			}
		} catch (TwitterException ex) {
			System.err.println(ex);
			System.err.println(query);
			// Logger.getLogger(TwitterSearch.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
	}
}
