##
##SMART FP7 - Search engine for MultimediA enviRonment generated contenT
##Webpage: http://smartfp7.eu
##
## This Source Code Form is subject to the terms of the Mozilla Public
## License, v. 2.0. If a copy of the MPL was not distributed with this
## file, You can obtain one at http://mozilla.org/MPL/2.0/.
##
## The Original Code is Copyright (c) 2012-2013 Atos
## All Rights Reserved
##
## Contributor(s):
## Jose Miguel Garrido, jose.garridog at atos dot net
##

"""The third Multimedia Data Manager.
This module stores the metadata from XML files to a SQLite database.
The video generator uses this database to create the actual video clips"""
# This file must work in python >2.7 and >3.3
import sys
p_v = 2 if sys.version_info < (3,) else 3

if p_v == 2:
    import urllib, urllib2
    import ConfigParser as cp
else:
    import urllib.request, urllib.parse, urllib.error
    import configparser as cp
import json
import couchdb
import argparse
import logging
import time, datetime

def getConf(filename,section):
    dict1 = {}
    config = cp.ConfigParser()
    config.read(filename)    
    options = config.options(section)
    for option in options:
        try:
            dict1[option] = config.get(section, option)
        except:
            print("exception on {}!".format(option))
            dict1[option] = None
    dict1["wait_time"] = int(dict1["wait_time"])
    dict1["couch_server"] = dict1["couch_server"] if (dict1["couch_server"]!="None") else None

    return dict1

def createURL(conf):
    query = { "@id": conf["id"] }
    if conf["search_type"] == "textual":
        command = "txtSearch"
        if conf["search_for"] == "venue":
            target = "venues"
        else:
            target = "activities"
        if p_v == 2:
            url = '{}/{}/{}?label=%22{}%22'.format(conf["url_base"],command,
                                               target,
                                               urllib.quote(conf["keywords"]))
        else:
            url = '{}/{}/{}?label=%22{}%22'.format(conf["url_base"],command,
                                               target,
                                               urllib.parse.quote(conf["keywords"]))

        query.update({ "keywords":conf["keywords"].split(),
                       "searched_item":conf["search_for"],
                       "search_type":"textual" })
    elif conf["search_type"] == "geo-search":
        command = "structuredSearch"
        query.update({"search_type":"geo-search"})
        if conf["search_for"] == "venue":
            query.update({"searched_item":"venues"})
            if conf["coord_type"] == "square":
                target = "locRec"
                query.update({"search_coords":[conf["coord1_long"],conf["coord1_lat"],
                                              conf["coord2_long"],conf["coord2_lat"]]})
            else:
                target = "locCirc"
                query.update({"search_coords":[conf["coord1_long"],conf["coord1_lat"],
                                              conf["radius"]]})
        else:
            query.update({"searched_item":"activities"})
            if conf["coord_type"] == "square":
                target = "actRec"
                query.update({"search_coords":[conf["coord1_long"],conf["coord1_lat"],
                                              conf["coord2_long"],conf["coord2_lat"]]})
            else:
                target = "actCirc"
                query.update({"search_coords":[conf["coord1_long"],conf["coord1_lat"],
                                              conf["radius"]]})
        if target in ("actCirc","locCirc"):
            url = '{}/{}/{}?lat1={}&long1={}&radius={}'.format(conf["url_base"],
                                                                command,
                                                                target,
                                                                conf["coord1_lat"],
                                                                conf["coord1_long"],
                                                                conf["radius"])
        else:
            url = '{}/{}/{}?lat1={}&long1={}&lat2={}&long2={}'.format(conf["url_base"],
                                                                command,target,
                                                                conf["coord1_lat"],
                                                                conf["coord1_long"],
                                                                conf["coord2_lat"],
                                                                conf["coord2_long"])

    logging.debug(url)
    logging.debug(query)

    return url, query

def formatItem(key,doc,time_query,query_info,num):
    data = {}
    data["time"] = time_query

    ldm_result = {}
    ldm_result.update(query_info)
    ldm_result["key"] = key
    if query_info["search_type"] == "textual":
        ldm_result["location"] = doc["location"]
    else:
        ldm_result["location"] = [i["location"] for i in doc["location"]]
        ldm_result["location_long"] = [i["long"] for i in doc["location"]]
        ldm_result["location_lat"] = [i["lat"] for i in doc["location"]]
    if "isPrimaryTopicOf" in doc:
        ldm_result["is_primary_topic_of"] = doc["isPrimaryTopicOf"]
    if "txt" in doc:
        ldm_result["txt"] = doc["txt"]
    if "label" in doc:
        ldm_result["label"] = doc["label"]
    if "date" in doc:
        ldm_result["date"] = doc["date"]
    if "name" in doc:
        ldm_result["name"] = doc["name"]
    if "attendance" in doc:
        ldm_result["attendance"] = doc["attendance"]

    data["ldm_result"] = ldm_result

    timestamp = time.time()+(num/1000.0)
    time_txt = datetime.datetime.utcfromtimestamp(timestamp).isoformat()+"Z"

    item = { "_id":time_txt, "data":data, "timestamp":str(int(timestamp*1000))}

    # check for not intended results
    remainder = set(doc.keys()) - set(("location", "isPrimaryTopicOf", "txt", "label","date","name","attendance") )
    if remainder:
        logging.warning("WARNING")
        logging.warning(remainder)

    logging.debug(item)
    return item

def storeItem(db,item):
    db.save(item)

if __name__ == '__main__':

    #inicialization
    logging.basicConfig(level=logging.DEBUG,format='%(asctime)s-> %(message)s')

    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--conf_file",type=str,
                    help="configuration file path")
    parser.add_argument("-s", "--section",type=str,
                    help="section of the configuration to apply")
    args = parser.parse_args()
    conf_file = args.conf_file if args.conf_file else "ldm_feeder_conf.ini"
    section = args.section if args.conf_file else "default"

    while True:   #until loop
        conf = getConf(conf_file,section)

        couch = couchdb.Server(conf["couch_server"]) if conf["couch_server"] else couchdb.Server()
        db = couch[conf["couch_database"]]

        #the program itself

        url, query_info = createURL(conf)

        if p_v == 2:
            response = urllib2.urlopen(url).read()
        else:
            response = urllib.request.urlopen(url).read()
            response = response.decode("utf-8")

        response = json.loads(response)

        if "locations" in response["data"]:
            items = "locations"
        elif "activities" in response["data"]:
            items = "activities"

        for num, i in enumerate(response["data"][items]):
            responseItem = formatItem(i,response["data"][items][i],
                                      response["data"]["time"],query_info, num)

            storeItem(db, responseItem)

        if conf["wait_time"] == 0:
            break
        else:
            time.sleep(conf["wait_time"])
