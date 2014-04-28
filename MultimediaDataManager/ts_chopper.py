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

# MPEG CHOPPER

# This program cuts a MPEG TS in 188 bytes parts and store it.

import sys
import cStringIO
import datetime
import time

from pymongo import *
from pymongo.errors import CollectionInvalid
import bson

#source = "output2.m2v"
source ="-"

use_file = False
use_mongo = True
#camera_id = "reloj"
#database = "SmartVideo"

camera_id = "webcam"
database = "SmartMeeting"

mongo_uri = "mongodb://localhost"

def writeToFile(chunk,file_root, camera_id, secuence_number):
    file_name = "{0}_{1}_{2:05d}.bin".format(file_root,camera_id,secuence_number)
    file_out = open(file_name, "wb")
    file_out.write(chunk.getvalue())
    file_out.close()
    print >>sys.stderr,"writed file ",file_name
    
def initMongoDB(mongo_uri,database, camera_id):
    #connection = MongoClient(mongo_uri, safe=false)
    connection = Connection(mongo_uri)
    db = connection[database]
    if camera_id in db.collection_names():
        db.drop_collection(camera_id)
        print >>sys.stderr, "Collection already created!"    
    else:
        db.create_collection(camera_id, capped=True, size = 200000000)
    collection = db[camera_id]
    collection.create_index("timestamp", ASCENDING)
    collection.create_index("seq", ASCENDING)
    return collection
        

def writeToMongoDB(chunk, collection, secuence_number, time,timestamp):
    register = {
            "seq":secuence_number,
            "stamp":time,
            "timestamp":int(timestamp*1000),
            "chunk":bson.binary.Binary(chunk.getvalue())
        }
    collection.insert(register)
    #print >>sys.stderr,"writed register ",secuence_number

# Main program
if source == "-":
    iFile = sys.stdin
else:
    iFile = open(source,"rb")

if use_mongo:
    collection = initMongoDB(mongo_uri, database, camera_id)

chunk_time = None
chunk_timestamp = None

pos = 0
c = iFile.read(1)

while(c!=""):
    #searching for the first 0x47
    
    if (c != "\x47"):
        pos = pos +1
        c = iFile.read(1)
    else:
        #we have found the first 0x47
    
        print >>sys.stderr, str(pos),"  found!"

        chunk_timestamp = time.time()
        chunk_time = datetime.datetime.utcfromtimestamp(chunk_timestamp)
        block1 = "\x47"+ iFile.read(187)
        pos = pos +187
        block2 = iFile.read(188)
        pos +=188
        if block2 and (block2[0]!="\x47"):
            print >>sys.stderr, "block2 not confirmed"
            c = iFile.read(1)
            #going to the beggining 
        else:
            block3 = iFile.read(188)
            pos +=188
            if block3 and block3[0]!="\x47":
                print >>sys.stderr,"block3 not confirmed"
                c = iFile.read(1)
            else:
                #we have 3 0x47 separated by 188 -> we can read the blocks
                break
else:
    #We have get the end of file without geting the block structure
    iFile.close()
    print >>sys.stderr, "File read without succes"
    sys.exit()


print >>sys.stderr,"starting to read blocks"
chunk = cStringIO.StringIO()
chunk.write(block1)
chunk.write(block2)
chunk.write(block3)
block_counter = 3

file_number = 0

block = iFile.read(188)    
while(block!=""):
    #we read 1024 blocks and write a chunk as a file or in the database
    if(block_counter <= 1024):
        chunk.write(block)
        block_counter += 1
        block = iFile.read(188)    
    else:
        if (use_file):
            writeToFile(chunk, "chunk", camera_id, file_number)
        if (use_mongo):
            writeToMongoDB(chunk, collection, file_number, chunk_time, chunk_timestamp)
        chunk.close()
        file_number += 1
        
        chunk = cStringIO.StringIO()
        chunk.write(block)
        block_counter = 1
        block = iFile.read(188)
        chunk_timestamp = time.time()
        chunk_time = datetime.datetime.utcfromtimestamp(chunk_timestamp)
else:
    if (use_file):
        writeToFile(chunk, "chunk", camera_id, file_number)
    if (use_mongo):
        writeToMongoDB(chunk, collection, file_number, chunk_time, chunk_timestamp)
    chunk.close()

iFile.close()
