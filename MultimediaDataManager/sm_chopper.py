# -*- coding: cp1252 -*-
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

## The simple Multimedia Data Manager



from subprocess import *
from multiprocessing import *
import time, shutil
import ConfigParser
import couchdb
import os, datetime, calendar
import sys
import logging
import argparse
import glob
from datetime import date

def write_feed(t_now, db,conf):
    # the feed
    doc = {}
    doc["_id"] =  datetime.datetime.utcfromtimestamp(t_now).isoformat()+"Z"
    doc["timestamp"] = int(t_now) * 1000

    data = {}
    data["time"] = doc["_id"]

    clip_info = {}

    clip_info["@id"] = conf["id"]
    clip_info["eventID"] = 0
    clip_info["camera"] = conf["camera"]
    clip_info["length"] = conf["t_chunk"]
    filename = "{0}{1}.mp4".format(conf["p_output"], int(t_now))
    clip_info["filenames"] = [ filename ]
    clip_info["clip_urls"] = [ conf["public_url"].format(filename,date.today().isoweekday()) ]
    clip_info["clip_res_x"] = conf["res_x"]
    clip_info["clip_res_y"] = conf["res_y"]
    clip_info["clips_formats"] = "iPhone.HD"
    clip_info["thumbnail_urls"] = []
    clip_info["thumbnail_res_x"] = []
    clip_info["thumbnail_res_y"] = []
    clip_info["thumbnail_formats"] = []

    data["clip_info"] = clip_info

    doc["data"] = data
    
    logging.debug(str(doc))
    
    db.save(doc)

#new for cURL
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

def move_to_remote(q,conf):
    couch = couchdb.Server(conf["couch_server"]) if conf["couch_server"] else couchdb.Server() 
    db = couch[conf["couch_database"]]
    while True:
        message = q.get()
        if message == ".":
            break
        path = message["name"]
        t_now = message["time"]
##        try:        
##            shutil.move(path, conf["d_public"])
##            logging.debug("writed "+path)
##        except IOError:
##            logging.exception("I can't write the file!")
##            os.remove(path)
##            continue
        #if os.stat().st_size > 100000:
        if curl_move(path, conf):
            try:
                logging.debug("Writing the feed")
                write_feed(t_now, db,conf)
            except:
                logging.exception("Error writing the feed!!")
        #else:
        #    logging.error("Empty file {}!!".format(path))
        #    os.remove(path)
            
        # I check for old videos here
        #remove_old_files(conf["d_public"], conf["hours_alive"])
        
# we can't use this for non-mounted files
##def remove_old_files(path, limit_hours,conf):
##    for dirpath, dirnames, filenames in os.walk(path):
##        for filex in filenames:
##            if filex.startswith(conf["p_output"]) and filex.endswith(".mp4"):
##                curpath = os.path.join(dirpath, filex)
##                file_modified = datetime.datetime.fromtimestamp(os.path.getmtime(curpath))
##                if datetime.datetime.now() - file_modified > datetime.timedelta(hours=limit_hours):
##                    os.remove(curpath)    
    
def getConf(filename,section):
    dict1 = {}
    config = ConfigParser.ConfigParser()
    config.read(filename)    
    options = config.options(section)
    for option in options:
        try:
            dict1[option] = config.get(section, option)
        except:
            print("exception on {}!".format(option))
            dict1[option] = None
    # a bit of adapting
    dict1["cmd_capture"] = dict1["cmd_capture"].replace('\n', '')
    dict1["res_x"] = int(dict1["res_x"])
    dict1["res_y"] = int(dict1["res_y"])
    dict1["couch_server"] = dict1["couch_server"] if (dict1["couch_server"]!="None") else None
    dict1["t_interval"] = int(dict1["t_interval"])
    dict1["hours_alive"] = int(dict1["hours_alive"])
    return dict1

if __name__ == '__main__':

    logging.basicConfig(level=logging.DEBUG,format='%(asctime)s-> %(message)s')

    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--conf_file",type=str,
                    help="configuration file path")
    parser.add_argument("-s", "--section",type=str,
                    help="section of the configuration to apply")
    args = parser.parse_args()
    conf_file = args.conf_file if args.conf_file else "sm_chopper_conf.ini"
    section = args.section if args.conf_file else "windows"
    #print conf_file, section

    conf = getConf(conf_file,section)

    t_final= time.time() + conf["t_interval"]

    
    
    q = Queue()
    p = Process(target=move_to_remote, args=(q, conf))
    p.start()

    # sweet possible old files
    for filename in glob.glob("{}??????????.mp4".format(conf["p_output"])):
       q.put({ "name":filename,"time":os.path.getctime(filename)})

    # the recording starts
    t_now = time.time()
    while (t_now < t_final ):
        cmd = conf["cmd_capture"].format(conf["t_chunk"], conf["p_output"],
                                         int(t_now), conf["cmd_input"],conf["res_x"],conf["res_y"])
        logging.debug(cmd)
        ans = Popen(cmd, shell=True, stdout=PIPE, stderr=PIPE)
        for line in ans.stderr:
            pass
            #print line

        q.put({ "name":"{0}{1}.mp4".format(conf["p_output"], int(t_now)),"time":t_now })

        t_now = time.time()

    q.put(".")
    p.join()
        

