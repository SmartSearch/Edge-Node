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
import fnmatch
import os
import sys
import datetime
import calendar
import logging
if sys.version_info < (3,):
    import ConfigParser as cp
else:
    import configparser as cp
import argparse

import sqlite3

import xml.etree.cElementTree as ET


def textisotimeToTimestamp(isotext):
    """Conversion from a date in ISO format to a timestamp (in miliseconds).
    SQLITE has some functions for translating dates,
    but I prefer using  the milisecond format as the rest of SMART"""
    dt = datetime.datetime.strptime(isotext, "%Y-%m-%dT%H:%M:%S.%fZ" )
    if sys.version_info < (3,):
        return (calendar.timegm(dt.timetuple())*1000) + (dt.microsecond/1000)
    else:
        # in python 3, we must use integer division operator //
        return (calendar.timegm(dt.timetuple())*1000) + (dt.microsecond//1000)
        # other method can be use python 3.3 datetime.timestamp()

def createDatabase(listChunks):
    """Creates the database for video metadata and initializaes it with the static data from the
    files"""
    try:
        conn = sqlite3.connect(conf["sqlite_file"])
    except:
        logging.exception("Error conecting to database")
        raise
        # it doesn't make sense to continue

    try:
        conn.execute('''CREATE TABLE IF NOT EXISTS chunks (
            beginStamp integer PRIMARY KEY,
            endStamp integer, recordingToken text, directory text, time text) ''')
        conn.execute('''CREATE INDEX IF NOT EXISTS "main"."timestamp" ON "chunks" ("endStamp" ASC,"beginStamp" DESC)''')
        conn.execute('''CREATE INDEX IF NOT EXISTS "main"."filename" ON "chunks" ("recordingToken" ASC)''')
        conn.execute('''DELETE FROM chunks''')
        conn.executemany('''INSERT INTO chunks VALUES (?,?,?,?,?)''',listChunks)
        conn.commit()
        logging.debug("Initial inserted {}".format(conn.total_changes))
        conn.close()
    except:
        logging.exception("Error initialiting database")
        raise
    finally:
        conn.close()


def storeXML(chunkInfo):
    """Stores the metadata for a video in the SQLITE datebase, reading it from a XML file"""
    try:
        conn = sqlite3.connect(conf["sqlite_file"])
    except:
        logging.exception("Error conecting in storeXLM ")
        return

    try:
        conn.execute('''INSERT INTO chunks VALUES (?,?,?,?,?)''',chunkInfo)
        conn.commit()
        logging.debug("Inserted "+chunkInfo[2])
        logging.debug("Lines: {}".format(conn.total_changes))
    except:
        logging.exception("Error writting in storeXLM "+str(chunkInfo))
    finally:
        conn.close()


def deleteXML(fileName):
    """When a video and the associated XML is deleted, it is necessary to delete de asociated row in the database"""
    try:
        conn = sqlite3.connect(conf["sqlite_file"])
    except:
        logging.exception("Error conecting in deleleXLM ")
        return

    try:
        conn.execute('''DELETE FROM chunks WHERE recordingToken = ?''',(fileName,))
        conn.commit()
        logging.debug("Deleted "+fileName)
        logging.debug("Lines: {}".format(conn.total_changes))
    except:
        logging.exception("Error writting in deleleXLM "+fileName)
    finally:
        conn.close()


def readXML(root,filename):
    """It reads and parsers a XML file, giving as output a tuple for storeXML"""
    try:
        tree = ET.parse(os.path.join(root, filename))
        rootXML = tree.getroot()
    except:
        logging.exception("Bad document "+os.path.join(root, filename))
        return None

    try:
        if rootXML.find("Status").text == "Complete":
            #print rootXML.tag, rootXML.attrib
            start_time = rootXML.find("StartTime").text
            #print start_time
            timestamp_start = textisotimeToTimestamp(start_time)
            #print timestamp_start
            stop_time = rootXML.find("StopTime").text
            timestamp_stop = textisotimeToTimestamp(stop_time)
            #print timestamp_stop
            recordingToken = rootXML.attrib
            chunkinfo = (timestamp_start,
                            timestamp_stop,
                            recordingToken["RecordingBlockToken"],
                            root,
                            start_time)
            return chunkinfo
        else:
            return None
    except:
        logging.exception("Bad format "+os.path.join(root, filename))
        return None

if sys.platform.startswith("linux"):

    import pyinotify

    class HandleEvents(pyinotify.ProcessEvent):

        def process_IN_CLOSE_WRITE(self,event):
            """When a new XML appears, we read it and store the contents"""
            logging.debug("Close "+str(event))
            logging.debug(event.pathname)
            try:
                if (not event.dir) and (event.name.lower().endswith(".xml")):
                    chunkInfo = readXML(event.path,event.name)
                    if chunkInfo: # only if it is complete
                        storeXML(chunkInfo)
            except:
                logging.exception("Error in IN_CLOSE_WRITE ")


        def process_IN_DELETE(self,event):
            """When a XML file is deleted, the associated row in the database
            must be deleted too"""
            logging.debug("Delete "+str(event))
            logging.debug(event.pathname)
            try:
                if (not event.dir) and (event.name.lower().endswith(".xml")):
                    recToken = event.name[:-4]
                    logging.debug(recToken)
                    deleteXML(recToken)
            except:
                logging.exception("Error in IN_DELETE")

        def process_default(self, event):
            """For information only"""
            logging.debug(str(event))
            logging.debug(event.pathname)
            if event.dir:
                logging.debug("Directory")

    def monitor_linux():
        """Initializes the monitoring of events, as new files or deletion"""
        logging.debug("Entering monitor_linux")
        p = HandleEvents()
        wm = pyinotify.WatchManager()
        mask = (pyinotify.IN_CLOSE_WRITE|pyinotify.IN_DELETE|pyinotify.IN_CREATE)
        notifier = pyinotify.Notifier(wm,p)
        wm.add_watch(conf["xml_file_root"],mask,rec=True,auto_add=True)
        notifier.loop()


def scanXML():
    """This function reads all the XML files existing in a directory. This is
    for inicialization. After this, new XML files are processed by the
    event handlers"""
    listChunks = []
    for root, dirnames, filenames in os.walk(conf["xml_file_root"]):
        #print  root, dirnames, filenames
        if "recording.xml" in filenames:
            filenames.remove("recording.xml")
        for filename in fnmatch.filter(filenames, '*.xml'):
            chunkinfo = readXML(root,filename)
            if chunkinfo:
                #chunkinfo is None if the file is not complete
                listChunks.append(chunkinfo)
    return listChunks

def readIniFile():
    """Inicialization, we read the configuration file and store the
    configuration in the conf key-value store"""
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
    if sys.version_info < (3,):
        config = cp.ConfigParser()
    else:
        config = cp.ConfigParser(interpolation=None)
    config.read(conf_file)
    options = config.options(section)
    for option in options:
        try:
            conf[option] = config.get(section, option)
        except:
            logging.exception("exception on {}!".format(option))
            conf[option] = None
    return conf


# MAIN
logging.basicConfig(level=logging.DEBUG,format='%(asctime)s-> %(message)s')

if __name__ == '__main__':

    conf = readIniFile()
    # conf is a global variable. As this program is not multithread, it is
    # not really dangerous, it is only a bit ugly

    listChunks = scanXML()
    createDatabase(listChunks)

    if sys.platform.startswith("linux"):
        monitor_linux()
# END MAIN