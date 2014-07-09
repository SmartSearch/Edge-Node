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
This module waits for events from the KB and creates and stores a video clip"""

import sqlite3
import datetime, calendar
import os
import logging
import random
import time
import subprocess
import ConfigParser
import argparse
import logging
import couchdb
import multiprocessing


def textisotimeToTimestamp(isotext):
    """Conversion from a date in ISO format to a timestamp (in miliseconds).
    SQLITE has some functions for translating dates,
    but I prefer using  the milisecond format as the rest of SMART"""
    dt = datetime.datetime.strptime(isotext, "%Y-%m-%dT%H:%M:%S.%fZ" )
    return (calendar.timegm(dt.timetuple())*1000) + (dt.microsecond/1000)
    # python 3.3 datetime.timestamp()

def getConf(filename,section):
    """Populate the conf object"""
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


def adaptToIPod(file_in, file_out,conf):
    """ Example of video adaptation. Uses ffmpeg to convert from the mkv format
    to a smaller mp4 file.
    The file is valid for several devices, not only for an iPod."""
    cmd = conf["cmd_iphone"].format(file_in, file_out)

    logging.info(cmd)
    ans = subprocess.check_call(cmd, shell=True)
    logging.debug("ffmpeg "+str(ans))

##    try:
##        os.remove(os.path.join(d_public,file_out))
##    except os.OSerror:
##        pass
    #shutil.move(file_out, conf["d_public"])
    return {"format":"iPhone", "res_x":640, "res_y":480,
                          "URL":conf["public_url"].format(file_out) }


def initCouchDB(couchServer, couchDatabase):
    """Connect to the Knoledge Database"""
    couch = couchdb.Server(couchServer) if couchServer else couchdb.Server()
    db = couch[couchDatabase]
    return db


def write_feed(db,eventID,camera,timestamp,clips, carets,lenght, conf):
    """Write the MDM feed in the KB, so it is available for indexing"""
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

    try:
        db.save(doc)
    except:
        logging.exception("Write Feed: error writing {}".format(filename))


def curl_move(path,conf):
    """Moves a file from a temp directory to the video server, using cURL"""
    cmd = conf["cmd_curl"].format(path,conf["user"],conf["passwd"],
                                  conf["target_url"])
    logging.debug(cmd)
    try:
        output = subprocess.check_output(cmd,shell=True)
    except subprocess.CalledProcessError as e:
        #this captures only the most clear errors
        logging.exception("Cmd returned {}".format(e.returncode))
        logging.debug("Cmd : {}".format(e.cmd))
        logging.debug("output : {}".format(e.output))
        os.remove(path) #it is a pity, but we delete the file if we can't trasmit it
        return False

    if (output=="" or "200" in output or "201" in output or "204" in output):
        # no output or something with the 201 means success
        os.remove(path)
        #logging.debug("Moved {}".format(path))
        return True
    else:
        # any other output means fail, for instance a 401 or a 500
        logging.error("Fail moving : {}".format(output))
        #os.remove(path)
        return False



def create_video(beginTime,endTime,event_id, conf):
    """Concatenate several video 10-second-chunks to create a master mkv,
    then convert it, store it and write the feed"""
    logging.debug("Entering create_video {} {}".format(beginTime, endTime))

    try:
        conn = sqlite3.connect(conf["sqlite_file"])
        c = conn.cursor()

        c.execute('''SELECT * FROM chunks WHERE endStamp > ? AND beginStamp < ? ORDER BY beginStamp ''',
            (beginTime,endTime))
    except:
        logging.exception("Error connecting to SQLITE")
        return

    l = c.fetchall()
    if len(l) == 0:
        #none found
        logging.warning("None found")
        return

    #I create the list of files to join
    listFilename = [ os.path.join(i[3],i[2]+".mkv") for i in l ]
    strInput = " +".join(listFilename)
    listInput =  strInput.split()
    #logging.debug(strInput)


    file_out = conf["tmp_file"].format(random.randint(1000,9999))

    cmd = [conf["cmd_merge"],"-o",file_out]+listInput

    try:
        logging.info(cmd)
        ans = subprocess.check_call(cmd, shell=False)
        logging.debug(ans)
    except:
        logging.exception("Error merging mkv file {}".format(file_out))
        return


    #Now the conversions to several formats
    clips = []
    #timestamp = calendar.timegm(timenow.utctimetuple())
    try:
        clipItem = adaptToIPod(file_out,
                "{}{}.iphone.mp4".format(conf["p_output"],beginTime),
                conf)
        clips.append(clipItem)
    except:
        logging.exception("Error adapting {}".format(file_out))
        return

    # the feed
    if curl_move("{}{}.iphone.mp4".format(conf["p_output"],beginTime), conf):
        try:
            logging.debug("Writing the feed")
            db = initCouchDB(conf["couch_server_output"], conf["couch_database_output"])
            write_feed(db,event_id,conf["camera_id"], beginTime, clips,[], endTime-beginTime,conf)
        except:
            logging.exception("Error writing the feed!!")


def readIniFile():
    """Read the configuration file"""
    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--conf_file",type=str,
                    help="configuration file path")
    parser.add_argument("-s", "--section",type=str,
                    help="section of the configuration to apply")
    args = parser.parse_args()
    conf_file = args.conf_file if args.conf_file else "fm_video_generator.ini"
    section = args.section if args.conf_file else "default"
    logging.debug("{} {}".format(conf_file, section))

    conf = {}
    config = ConfigParser.ConfigParser()
    config.read(conf_file)
    options = config.options(section)
    for option in options:
        try:
            conf[option] = config.get(section, option)
        except:
            logging.exception("exception on {}!".format(option))
            conf[option] = None
    # a bit of adapting
    conf["cmd_iphone"] = conf["cmd_iphone"].replace('\n', ' ')
    conf["couch_server_output"] = conf["couch_server_output"] if (conf["couch_server_output"]!="None") else None
    conf["couch_server_input"] = conf["couch_server_input"] if (conf["couch_server_input"]!="None") else None
    return conf

def feed_reader_loop(conf):
    """This loop monitors the KB waiting for relevant events"""
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


# MAIN PROGRAM
logging.basicConfig(level=logging.DEBUG,format='%(asctime)s-> %(message)s')

if __name__ == '__main__':
    conf = readIniFile()

    #t_start = "2013-10-16T10:57:16.528397Z"
    #t_end   = "2013-10-16T10:59:12.528397Z"

    #t_beginStamp = textisotimeToTimestamp(t_start)
    #t_endStamp = textisotimeToTimestamp(t_end)

    #create_video(t_beginStamp, t_endStamp, 0, conf)

    feed_reader_loop(conf)
# END MAIN

