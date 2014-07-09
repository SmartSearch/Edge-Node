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
## Jose Miguel Garrido, jose.garridog at atosresearch.eu
##

## FEED READER (and WRITTER)

import couchdb
import time
import argparse
import datetime
import pymongo
import os
import sys
import ConfigParser
import argparse
import logging
import multiprocessing
import subprocess

def selectBySeq(collection,seq1, num):
    cursor = collection.find(
        { "$and": [
            { "seq":{"$gte":seq1}},
            { "seq":{"$lt":seq1+num}}
        ] } )
    return cursor

def selectByTime(collection, stamp, time_final):
    logging.debug("time initial {}".format(stamp))
    logging.debug("time final {}".format(time_final))
    cursor = collection.find(
            { "$and":[
                {"timestamp":{"$gte":stamp}},
                {"timestamp":{"$lt":time_final}}
                ]
              } )
    return cursor

def getConf(filename,section):
    dict1 = {}
    config = ConfigParser.ConfigParser()
    config.read(filename)    
    options = config.options(section)
    for option in options:
        try:
            dict1[option] = config.get(section, option)
        except:
            logging.exception("exception on {}!".format(option))
            dict1[option] = None
    # a bit of adapting
    dict1["cmd_iphone"] = dict1["cmd_iphone"].replace('\n', ' ')
    dict1["couch_server_input"] = dict1["couch_server_input"] if (dict1["couch_server_input"]!="None") else None
    dict1["couch_server_output"] = dict1["couch_server_output"] if (dict1["couch_server_output"]!="None") else None
    return dict1


def initMongoDB(mongo_uri,database, camera_id):
    logging.debug("Entering in initMongoDB")
    #connection = MongoClient(mongo_uri)
    connection = pymongo.Connection(mongo_uri)
    db = connection[database]
    collection = db[camera_id]
    return collection

def initCouchDB(couchServer, couchDatabase):
    couch = couchdb.Server(couchServer) if couchServer else couchdb.Server() 
    db = couch[couchDatabase]
    return db

def adaptToIPod(file_in, file_out, d_public,conf):
    cmd = conf["cmd_iphone"].format(file_in, file_out)
    logging.info(cmd)
    ans = subprocess.Popen(cmd,
        shell=False, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    for line in ans.stderr:
        pass
        logging.debug(line)
##    try:
##        os.remove(os.path.join(d_public,file_out))
##    except os.OSerror:
##        pass
    #shutil.move(file_out, conf["d_public"])
    return {"format":"iPhone", "res_x":640, "res_y":480,
                          "URL":conf["public_url"].format(file_out) }


##def writeFeed(db,eventID,camera,timestamp,clips, carets,lenght):
##        # the feed
##        doc = {}
##        doc["id"] = time.ctime(float(timestamp))
##        doc["timestamp"] = int(timestamp)*1000
##        doc["filename"] = "{0}{1}.mp4".format(conf["p_output"], int(timestamp))
##        doc["clips"] = clips
##        doc["carets"] = carets
##        doc["length"] = lenght
##        doc["camera"] = camera
##        doc["eventID"] = eventID
##        db.save(doc)

def write_feed(db,eventID,camera,timestamp,clips, carets,lenght, conf):
    # the feed
    doc = {}
    doc["_id"] =  datetime.datetime.utcfromtimestamp(timestamp/1000).isoformat()+"Z"
    doc["timestamp"] = timestamp

    data = {}
    data["time"] = doc["_id"]

    clip_info = {}

    clip_info["@id"] = conf["id"]
    clip_info["eventID"] = eventID
    clip_info["camera"] = camera
    clip_info["length"] = lenght
    filename = "{}{}.iphone.mp4".format(conf["p_output"],timestamp)
    clip_info["filenames"] = [ filename ]
    clip_info["clip_urls"] = [ clips[0]["URL"] ]
    clip_info["clip_res_x"] = [ clips[0]["res_x"] ]
    clip_info["clip_res_y"] = [ clips[0]["res_y"] ]
    clip_info["clips_formats"] = [ clips[0]["format"] ]
    clip_info["thumbnail_urls"] = []
    clip_info["thumbnail_res_x"] = []
    clip_info["thumbnail_res_y"] = []
    clip_info["thumbnail_formats"] = []

    data["clip_info"] = clip_info

    doc["data"] = data
    
    logging.debug(str(doc))
    
    db.save(doc)

def curl_move(path,conf):
    cmd = conf["cmd_curl"].format(path,conf["user"],conf["passwd"],
                                  conf["target_url"].format(date.today().isoweekday()))
    logging.debug(cmd)
    try:
        output = check_output(cmd,shell=True)
    except CalledProcessError as e:
        #this captures only the most clear errors
        logging.exception("Cmd returned {}".format(e.returncode))
        logging.debug("Cmd : {}".format(e.cmd))
        logging.debug("output : {}".format(e.output))
        return False

    if (output=="" or "201 Created" in output):
        # no output or something with the 201 means success 
        os.remove(path)
        logging.debug("Moved {}".format(path))
        return True
    else:
        # any other output means fail, for instance a 401 or a 500
        logging.error("Fail moving : {}".format(output))
        return False 

def create_video(beginTime,endTime,event_id, conf):
    logging.debug("Entering create_video {}".format(beginTime))

    collection = initMongoDB(conf["mongo_uri"], conf["database"], conf["camera_id"])
    cursor = selectByTime(collection,beginTime, endTime)

    if cursor.count() == 0:
        logging.warning("Nothing to return")
        return

    file_out = open(conf["tmp_file"],"wb")
    #error: the tmp file must be different for each thread
    
    for i in cursor:
        logging.debug("recoved seq {} stamp {}".format(i["seq"],i["stamp"]))
        file_out.write(i["chunk"])
    file_out.close()
    logging.debug("Temp file created")
    
    #Now the conversions to several formats
    clips = []
    #timestamp = calendar.timegm(timenow.utctimetuple())
    clipItem = adaptToIPod(conf["tmp_file"],
                "{}{}.iphone.mp4".format(conf["p_output"],beginTime),
                conf["d_public"], conf)
    clips.append(clipItem)
    
    
    # the feed
    db = initCouchDB(conf["couch_server_output"], conf["couch_database_output"])
    write_feed(db,event_id,conf["camera_id"], beginTime, clips,[], endTime-beginTime,conf)


# Main program
logging.basicConfig(level=logging.DEBUG,format='%(asctime)s-> %(message)s')

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--conf_file",type=str,
                    help="configuration file path")
    parser.add_argument("-s", "--section",type=str,
                    help="section of the configuration to apply")
    args = parser.parse_args()
    conf_file = args.conf_file if args.conf_file else "ts_feed_reader.ini"
    section = args.section if args.conf_file else "windows"
    logging.debug("{} {}".format(conf_file, section))

    conf = getConf(conf_file,section)

    couch = couchdb.Server(conf["couch_server_input"]) if conf["couch_server_input"] else couchdb.Server() 
    db = couch[conf["couch_database_input"]]

    inEvent = False
    beginTime = None
    endTIme = None
    since = 0
    while True:
        logging.debug("Entering...")
        ch = db.changes(since=since, include_docs=True)
        since = ch["last_seq"]
        #logging.debug(since)
        for i in ch["results"]:
            logging.debug(i["doc"])
            if ("Probability" in i["doc"] and
                    "Notification" in i["doc"]["Probability"] and 
                    "timestamp" in i["doc"] 
                ):
                logging.debug(i["doc"]["Probability"]["Notification"]) 
                prob = i["doc"]["Probability"]["Notification"]
                if (float(prob) > 0.9) and inEvent == False:
                    inEvent = True;
                    beginTime = i["doc"]["timestamp"]
                if (float(prob) <0.3) and inEvent == True:
                    inEvent = False;
                    endTime = i["doc"]["timestamp"]
                    logging.info("Event! {} {}".format(beginTime, endTime - beginTime))
                    p = multiprocessing.Process(target=create_video,
                                            args=(beginTime,
                                                endTime,
                                                conf["event_id"], conf))
                    p.start()
                    #fire and forget
                    
        time.sleep(10)
