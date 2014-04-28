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

# Program for recovering mp4 files not transmited by temporal errors

#by the moment, we use the .py file for configuration
import sm_chopper_conf as conf

import couchdb
import os, datetime, calendar

import logging

import glob

from subprocess import *

import time

def write_feed(t_now, db):
	# the feed
        doc = {}
        doc["_id"] =  datetime.datetime.utcfromtimestamp(t_now).isoformat()+"Z"
        doc["time"] = doc["_id"]
        doc["timestamp"] = int(t_now) * 1000
        doc["filename"] = "{0}{1}.mp4".format(conf.p_output, int(t_now))
        doc["clips"] = [ {"format":"iPhone.HD", "resX":conf.resX, "resY":conf.resY,
                          "URL":conf.publicURL.format(doc["filename"]) } ]
        doc["carets"] = [{"format":"small","resX":0,"resY":0,
                          "URL":"none yet"
                          }]
        doc["length"] = conf.t_chunk
        doc["camera"] = conf.camera
        doc["eventID"] = 0
        db.save(doc)

def curl_move(path):
    cmd = conf.cmd_curl.format(path,conf.user,conf.passwd,conf.target_url)
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

#Main

logging.basicConfig(level=logging.DEBUG,format='%(asctime)s-> %(message)s')
couch = couchdb.Server(conf.couchServer) if conf.couchServer else couchdb.Server() 
db = couch[conf.couchDatabase]


for filename in glob.glob("s??????????.mp4"):
    # I take the creation time
    file_timestamp = os.path.getctime(filename)
    logging.debug("{} -> {}".format(filename,file_timestamp))
    if time.time() - file_timestamp < 120:
        logging.debug("{} too young".format(filename))
    elif curl_move(filename):
        try:
            logging.debug("Writing the feed")
            write_feed(file_timestamp, db)
        except:
            logging.exception("Error writing the feed!!")
        
    
    
