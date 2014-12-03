class DictionaryComparator:
    """ Given two dictionaries with words as keys and ranks as values,
    this class is used to make various comparisons between them."""

    def __init__(self, dictionaryOld, dictionaryCurrent):
        self.dictOld = dictionaryOld
        self.dictCurrent = dictionaryCurrent
        self.wordsOld = set(self.dictOld.keys())
        self.wordsCurrent = set(self.dictCurrent.keys())
        self.wordsDifferentOld = set()
        self.wordsDifferentCurrent = set()

    def getOldWordsNotInCurrent(self):
        """ Output:
                A set of words in the old dictionary that are not in the current.
        """
        if not self.wordsDifferentOld:
            self.wordsDifferentOld = self.wordsOld.difference(self.wordsCurrent)
        return self.wordsDifferentOld

    def getCurrentWordsNotInOld(self):
        """ Output:
                A set of words in the current dictionary that are not in the old.
        """
        if not self.wordsDifferentCurrent:
            self.wordsDifferentCurrent = self.wordsCurrent.difference(self.wordsOld)
        return self.wordsDifferentCurrent

    def getCommonWords(self):
        """ Output:
                A set of words that are common to both.
        """
        return self.wordsCurrent.intersection(self.wordsOld)

    def getIndexChangesFromOldToCurrent(self):
        """ Output:
                A dictionary with key the old index/rank of a word in the common
                words and value the new index/rank.
        """
        commonWords = self.getCommonWords()
        indexChanges = {}
        for commonWord in commonWords:
            indexChanges[self.dictOld[commonWord]] = self.dictCurrent[commonWord]
        return indexChanges

    def getIndexChangesFromCurrentToOld(self):
        """ Output:
                A dictionary with key the current index/rank of a word in the common
                words and value the old index/rank.
        """
        commonWords = self.getCommonWords()
        indexChanges = {}
        for commonWord in commonWords:
            indexChanges[self.dictCurrent[commonWord]] = self.dictOld[commonWord]
        return indexChanges

    def getIndexOfWordsNotInCurrent(self):
        """ Output:
                A set of indices/ranks of words that are in the old dictionary
                but not in the current.
        """
        indexNotInCurrent = set()
        for word in self.getOldWordsNotInCurrent():
            indexNotInCurrent.add(self.dictOld[word])
        return indexNotInCurrent

    def getIndexOfWordsNotInOld(self):
        """ Output:
                A set of indices/ranks of words that are in the current dictionary
                but not in the old.
        """
        indexNotInOld = set()
        for word in self.getCurrentWordsNotInOld():
            indexNotInOld.add(self.dictCurrent[word])
        return indexNotInOld
