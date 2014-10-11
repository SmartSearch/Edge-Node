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

import eu.smartfp7.SocialNetworkManager.SocialNetworkPostListInterface;

import com.restfb.types.Comment;
import com.restfb.types.Post;
import com.restfb.types.Post.Comments;
import com.restfb.types.Post.Place;

public class FacebookPostData extends SocialNetworkPostListInterface {

	@XmlElementWrapper
	public ArrayList<FacebookPostData> Comments;

	@XmlElement
	public Long Likes;

	FacebookPostData() {
	}

	public FacebookPostData(Post FBpost) {
		UserName = FBpost.getFrom().getName();
		UserID= FBpost.getFrom().getId();
		this.Message = FBpost.getMessage();
		this.CreationDate = FBpost.getCreatedTime();
		this.Likes = FBpost.getLikesCount();
		this.Source="Facebook";
		if (FBpost.getPlace() != null) {
			this.LocationName = FBpost.getPlace().getLocation().getCity();
			this.GeoLatitude = FBpost.getPlace().getLocation().getLatitude();
			this.GeoLongitude = FBpost.getPlace().getLocation().getLatitude();
		}
		if (FBpost.getComments() != null) {
			for (int i = 0; i < FBpost.getComments().getCount(); i++)
				this.Comments.add(new FacebookPostData(FBpost.getComments()
						.getData().get(i)));
		}
	}

	FacebookPostData(Comment FBpost) {
		this.UserName = FBpost.getFrom().getId();
		this.Message = FBpost.getMessage();
		this.CreationDate = FBpost.getCreatedTime();
		this.Likes = FBpost.getLikes();
	}

}
