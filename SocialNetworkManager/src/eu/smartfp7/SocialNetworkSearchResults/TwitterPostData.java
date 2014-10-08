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

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import eu.smartfp7.SocialNetworkManager.SocialNetworkPostListInterface;

import com.restfb.types.Comment;
import com.restfb.types.Post;
import com.restfb.types.Post.Comments;
import com.restfb.types.Post.Place;

public class TwitterPostData extends SocialNetworkPostListInterface {

	@XmlElement
	public Long Retweets;

	TwitterPostData() {
	}

	public TwitterPostData(Tweet TwitterPost) {
		Twitter twitter = new TwitterFactory().getInstance();
		UserName = TwitterPost.getFromUser();
		UserID = TwitterPost.getFromUserId() + "";

		this.Message = TwitterPost.getText();
		this.CreationDate = TwitterPost.getCreatedAt();
		this.Source = "Twitter";
		if (TwitterPost.getPlace() != null) {
			this.LocationName = TwitterPost.getPlace().getName();
			this.GeoLatitude = TwitterPost.getPlace().getGeometryCoordinates()[0][0]
					.getLatitude();
			this.GeoLongitude = TwitterPost.getPlace().getGeometryCoordinates()[0][0]
					.getLongitude();
		}
	}
}
