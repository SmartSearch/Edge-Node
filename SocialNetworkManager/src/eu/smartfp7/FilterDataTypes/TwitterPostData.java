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
package eu.smartfp7.FilterDataTypes;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.smartfp7.SocialNetworkManager.SocialNetworkPostListInterface;

public class TwitterPostData extends SocialNetworkPostListInterface {

	@XmlElement
	public Long Retweets;

	TwitterPostData() {
	}

}
