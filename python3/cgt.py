'''Defines some basic Combinatorial Games operations in Python 3.x.  
author: Kyle Burke <paithanq@gmail.com>
Right now there are only definitions for Impartial Games.'''

import copy
from abc import ABC, abstractmethod #abstract classes.  Code here modified from alexvassel's answer at: https://stackoverflow.com/questions/13646245/is-it-possible-to-make-abstract-classes-in-python

'''Integer for the left player.'''
LEFT=0

'''Integer for the right player.'''
RIGHT=1
    
def mex(ints):
    '''Returns the minimum natural number (including 0) that doesn't appear in ints, a list of naturals.'''
    i = 0
    while i in ints:
        i += 1
    return i
    


class ImpartialGame(ABC):
    '''Models an impartial game, which is a game where both players have the same move options from all positions.'''
    
    @abstractmethod
    def get_options(self, playerId = LEFT):
        '''Returns the options for this game. Since it's impartial, both right and left options are the same.'''
        pass
    
    def standardize(self):
        '''Returns a standard equivalent version of this game for easier evaluation.'''
        return self
    
    def __hash__(self):
        '''Hashes this just using the string representation.'''
        return str(self.standardize()).__hash__()

class Nim(ImpartialGame):
    '''Models a Nim state.
    attributes: piles, a list of non-negative integers.'''
    
    def __init__(self, piles):
        '''piles is a list of non-negative integers''' 
        self.piles = copy.deepcopy(piles)
    
    def __str__(self):
        '''Returns a string version of this.'''
        return "Nim: " + str(self.piles)
    
    def standardize(self):
        new_piles = copy.deepcopy(self.piles)
        new_piles.sort()
        return Nim(new_piles)
    
    def get_options(self):
        options = []
        for i in range(len(self.piles)):
            piles_copy = copy.deepcopy(self.piles)
            for j in range(self.piles[i]):
                piles_copy[i] = j
                option = Nim(piles_copy)
                options.append(option)
        return options
    
    def __eq__(self, other):
        '''Returns whether two Nim instances are equivalent.'''
        return self.standardize().piles == other.standardize().piles
    
    #Is this really the best way to do this?  If I don't explicitly say that it has a hash function, then it's not hashable and won't compile.
    def __hash__(self):
        return super().__hash__()
  
    

class GrundySmasher(object):
    '''Generates the Grundy value (nimber) of an impartial game.'''
    
    def __init__(self, verbose = False):
        self.memo = {}
        self.verbose = verbose
        
    def __str__(self):
        return "I am a GrundySmasher who has evaluated " + str(len(self.memo)) + " positions!"
    
    def evaluate(self, position):
        '''Returns the Grundy value of position, an instance of an ImpartialGame.'''
        position = position.standardize() #first reduce to a standard version
        if position in self.memo:
            return self.memo[position]
        else:
            options = position.get_options()
            option_values = []
            for option in options:
                option_values.append(self.evaluate(option))
            value = mex(option_values)
            self.memo[position] = value
            if self.verbose:
                print("Discovered that " + str(position) + " = *" + str(value))
            return value
        
    def set_verbose(self, verbosity):
        self.verbose = verbosity
        
    def has_evaluated(self, position):
        return position.standardize() in self.memo
    
   
    
class Nimberizer(ABC):
    '''Abstract superclass for Nimberizers, programs that attempt to find the nimbers of positions using conjectured formulas.'''
    
    @abstractmethod
    def nimberize(self, position):
        '''Returns a value that this thinks is the correct nimber for position.'''
        pass
    
    
    
    
class NimNimberizer(object):
    '''Returns the nimber of a Nim position.  This is known to work correctly (assuming I coded it right) so it can be used with a verifier.'''
    
    def nimberize(self, nim):
        '''Returns the xor of the values of the piles.'''
        nim_sum = 0
        for pile in nim.piles:
            nim_sum ^= pile # ^ is XOR
        return nim_sum
    
       
    
    
class NimberizerVerifier(object):
    '''Tests whether a Nimberizer is working.'''
    
    def __init__(self, nimberizer, smasher = GrundySmasher()):
        self.nimberizer = nimberizer
        self.smasher = smasher
        self.correctness_memo = {} #keeps track of whether the values are correct or incorrect.  Necessary???
        self.incorrect = []
        
    def evaluate(self, position):
        '''Evaluates a single position, first checking whether it already knows the result.'''
        if self.smasher.has_evaluated(position):
            return self.smasher.evaluate(position)
        else:
            guess_nimber = self.nimberizer.nimberize(position)
            nimber = self.smasher.evaluate(position)
            self.correctness_memo[position] = nimber == guess_nimber
            if nimber != guess_nimber:
                self.incorrect.append(position)
                print("Found an inconsistency!!!!")
            return nimber
        
    def verify(self, position):
        '''Verifies a single position, returning whether we got it right.'''
        if position in self.correctness_memo:
            #we've already checked it, so just return whether we were right.
            return self.correctness_memo[position]
        else:
            guess_nimber = self.nimberizer.nimberize(position)
            #do the evaluation ourselves, which will store the value.
            actual_nimber = self.evaluate(position)
            return guess_nimber == actual_nimber
            
    def has_verified(self, position):
        '''Returns whether we have already verified a position.'''
        if position in self.incorrect:
            return False
        else:
            return position in self.correctness_memo
            
    def print_all_incorrect(self):
        '''Prints out all the incorrect values.'''
        print("#incorrectly-evaluated games: " + str(len(self.incorrect)))
        print("Incorrectly-evaluated games:")
        for position in self.incorrect:
            print(str(position) + "\n     Guess: *" + str(self.nimberizer.nimberize(position)) + "\n    Actual: *" + str(self.smasher.evaluate(position)) + "\n\n")
        print("Done with incorrectly-evaluated games!")
        
    def verify_all(self, positions):
        '''Returns whether a set of positions evaluate correctly.'''
        all_correct = True
        for position in positions:
            correctness = self.verify(position)
            all_correct = all_correct and correctness
        
        #print out all the incorrect values before returning.  I probably shouldn't always do this, but I do right now.
        self.print_all_incorrect()
        return all_correct
            
    
def sample_test():
    '''Some code to run a sample test to show how to use this package.'''
    to_nimberize = []
    max_pile_size = 10
    num_piles = 5
    for i0 in range(max_pile_size):
        for i1 in range(max_pile_size):
            for i2 in range(max_pile_size):
                to_nimberize.append(Nim([i0, i1, i2]))
    nimberizer = NimNimberizer()
    verifier = NimberizerVerifier(nimberizer)
    
    verifier.verify_all(to_nimberize)
    
    #TODO: include a nimberizer that fails
    
    
    
#run the tests if we're just executing this file directly and not importing it.
if __name__ == "__main__":
    sample_test()
    
    
