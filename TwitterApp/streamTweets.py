'''
Created on 15 July 2014
@author: Angathan FRANCIS
'''

from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
import os
import time
import json

# TWITTER API keys
ckey = "wIWrY1pzbvnJvW6LOsAcUr274"
csecret = "UfZ1qr9vNE6f6JXH8oAmxrdUM2yma9H90AEsgFdZu5rqnwv9aH"
atoken = "2561653453-24PCCPN4rlOAWKEdgFdzGBiT6dJNVBVHpKH5rJZ"
asecret = "Z9aAQRDVjzXywiNIpoaJw3XlSUesQQGfj4MlavNatBi6Y"

class TweetStream(StreamListener):
    """ Storing stream of tweets in a database. The database file is not JSON
    as a whole but contains a JSON object for each tweet. This class needs the
    database filename to be provided.
    """

    def __init__(self, filename, closeSignal):
        self.count = 0
        self.filename = filename
        self.closeSignal = closeSignal
    
    def on_data(self, data):
        if self.closeSignal == False:
            try:
                """ Saving data into a database. """
                self.saveFile = open('Database/' + self.filename, 'a')
                self.saveFile.write(data)
                self.saveFile.close()
                return True

            except BaseException, e:
                """ Errors might due to the quality of the internet connection
                Rate decreasing, connection lost, etc. """
                print 'Failed: ', str(e)
                time.sleep(5)
        else:
            return False
            self.stream.disconnect()
            self.saveFile.close()

    def on_error(self, status):
        """ Displaying the occuring general errors """
        print 'Error :' + str(status)

    def getStream(self):
        try:
            """ Collecting the stream of tweets in a specific location """
            auth = OAuthHandler(ckey, csecret)
            auth.set_access_token(atoken, asecret)
            self.stream = Stream(auth, self)
            self.stream.filter(locations = [-0.489,51.28,0.236,51.686]) #New York : -74.182186, 40.544001, -73.623267, 40.984179

        except KeyboardInterrupt:
            self.saveFile.close()
            print 'File Closed'              
