'''For testing Quantum Nim (2-width supermoves).
author: Kyle Burke'''

import cgt
import copy

def toQNimString(nim):
    s = "("
    for pile in nim.piles:
        s += str(pile) + ", "
    s = s[: len(s)-2]
    return s + ")"

def nimDominates(a, b):
    '''Returns true if Nim position a "dominates" Nim position b, which means that each pile of a is at least as big as each pile of b.'''
    if len(a.piles) != len(b.piles):
        print("Trying to test domination of two Nims that have different numbers of piles!  a:" + str(a) + "  b: " + str(b))
        return False
    for i in range(len(a.piles)):
        if (a.piles[i] < b.piles[i]):
            return False
    return True


class QuantumNim(cgt.ImpartialGame):
    '''Models a Quantum Nim position, i.e., a superposition of nims.
    attributes: nims, the list of Nim objects in the superposition.'''
    
    def __init__(self, nims):
        '''Constructor.  nims is a list of Nim objects.'''
        if len(nims) == 0:
            print("Can't create a nim object with zero nims!")
        optionNims = copy.deepcopy(nims)
        slimmedOptions = [] #a shorter list of non-dominated positions
        for i in range(len(optionNims)):
            maybeDominated = optionNims[i]
            dominated = False
            for j in range(len(optionNims)):
                other = optionNims[j]
                if (i != j) and (nimDominates(maybeDominated, other)):
                    #check to see whether they're equal and whether one already exists
                    if (maybeDominated == other) and (not maybeDominated in slimmedOptions):
                        pass #we do want to maybe add it
                    else:
                        dominated = True
            if not dominated:
                slimmedOptions.append(maybeDominated)
        self.nims = slimmedOptions
        
        
    def __str__(self):
        if (len(self.nims) == 1):
            return toQNimString(self.nims[0])
        s = "<"
        for nim in self.nims:
            s += toQNimString(nim) + " | "
        s = s[: len(s)-3]
        return s + ">"
        
    def get_options(self):
        '''Returns all options from this position.'''
        #generate the list of maximum pile sizes
        maxes = copy.deepcopy(self.nims[0].piles)
        #print("*******")
        #print("#nims:", len(self.nims))
        #print("nims:", self.nims[0])
        #print("self:", self)
        #print("pre maxes:", maxes)
        for nim in self.nims:
            #print(nim.piles)
            for i in range(len(nim.piles)):
                maxes[i] = max(maxes[i], nim.piles[i])
        
        options = []
        #print("self (B):", self)
        #print("maxes:", maxes)
        #generate all possible moves.
        #first the moves from making one move
        for i in range(len(maxes)):
            size = maxes[i]
            for sticks in range(1, size+1):
                options.append(self.getOptionFromClassicalMove(i, sticks))
                
        #print(len(options), "so far...")
        #print("self (C):", self)
        #now the moves from making a quantum width-2 move:
        for iA in range(len(maxes)-1):
            sizeA = maxes[iA]
            for sticksA in range(1, sizeA+1):
                for iB in range(iA + 1, len(maxes)):
                    sizeB = maxes[iB]
                    for sticksB in range(1, sizeB+1):
                        options.append(self.getOptionFromQuantumMove(iA, sticksA, iB, sticksB))
        #print(len(options), "options for", str(self))
        return options
    
    def __eq__(self, other):
        """Returns whether self equals other."""
        return str(self) == str(other)
    
    def __hash__(self):
        return super().__hash__()
    
    def standardize(self):
        '''Returns a new version of this with the nims ordered.'''
        copyNims = copy.deepcopy(self.nims)
        copyNims.sort(key = str)
        return QuantumNim(copyNims)
        
        
        
    def getOptionFromClassicalMove(self, pileI, sticksTaken):
        '''Returns the QuantumNim position created from one (classical) move.''' 
        optionNims = []
        for nim in self.nims:
            if nim.piles[pileI] >= sticksTaken:
                optionNim = copy.deepcopy(nim)
                optionNim.piles[pileI] -= sticksTaken
                optionNims.append(optionNim)
        return QuantumNim(optionNims)
    
    def getOptionFromQuantumMove(self, pileIA, sticksA, pileIB, sticksB):
        '''Returns the Quantum Nim position created from a quantum move.'''
        qNimA = self.getOptionFromClassicalMove(pileIA, sticksA)
        qNimB = self.getOptionFromClassicalMove(pileIB, sticksB)
        bothNims = copy.deepcopy(qNimA.nims) + copy.deepcopy(qNimB.nims)
        bothNim = QuantumNim(bothNims)
        return bothNim
        
      
      
nimA = cgt.Nim([3, 4])
nimB = cgt.Nim([4,3])
qNim = QuantumNim([nimA, nimB])

#qNim = QuantumNim([cgt.Nim([1,0])])

smasher = cgt.GrundySmasher()
smasher.evaluate(qNim)

cgt.print_impartial_position_and_options(qNim, smasher)

print("******  Printing the zeroes! ******")

smasher.print_zeroes()


#print(nimDominates(cgt.Nim([1,1]), cgt.Nim([1,1])))
