'''
Created on 1 August 2014
@author: Angathan FRANCIS
'''

import pickle

def initList():
    wordList = [
        "back", "they", "there", "would", "think", "much", "will", "really",
        "been", "more", "london","well","i'll", "come", "it's", "time", "today",
        "from", "last", "good", "love", "going", "home", "sleep", "house", "some",
        "know", "don't", "like", "about", "people", "the", "with", "just", "have",
        "this", "that","why", "what", "when", "you", "your", "wasn't", "what",
        "where", "who", "then", "can't", "morning", "tonight", "others", "know",
        "don't", "like", "i've", "about", "people", "some", "house", "fuck",
        "london", "greater", "twitter", "tweet", "follow", "follower", "lol", "lmao",
        "through", "tho", "though", "ahah", "haha", "damn", "selfie", "retweet",
        "great", "cheers", "thank", "thanks", "[contract]", "[pic]"
        ]

    with open('wordlist.txt', 'wb') as wordfile:
        words = pickle.Pickler(wordfile)
        words.dump(wordList)

    getWordList()

def getWordList():
    with open('wordlist.txt', 'rb') as wordfile:
        words = pickle.Unpickler(wordfile)
        wordList = words.load()

    print wordList

def addWord(word):
    with open('wordlist.txt', 'rb') as wordfile:
        words = pickle.Unpickler(wordfile)
        wordList = words.load()

    # Write a new word into the list
    wordList.append(word)

    # Save the new list into the file
    with open('wordlist.txt', 'wb') as wordfile:
        words = pickle.Pickler(wordfile)
        words.dump(wordList)

    # Display the new list
    getWordList()
    
def removeWord(word):
    with open('wordlist.txt', 'rb') as wordfile:
        words = pickle.Unpickler(wordfile)
        wordList = words.load()

    # Remove the word from the list
    wordList.remove(word)

    # Display the new list
    getWordList()
