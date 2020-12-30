### Authors: Kyle Burke and Matt Ferland

import cgt
import copy

 
class AvoidTrue(cgt.ImpartialGame):
    """Models an AvoidTrue state.
    attributes: false variables (a list of indices), true variables (a list of indices), and clauses (a list of lists of indices)."""
    
    def __init__(self, clauses, falses, trues):
        #print("clauses:", clauses)
        #print("falses:", falses)
        #print("trues:", trues)
        
        false_variables = copy.deepcopy(falses)
        true_variables = copy.deepcopy(trues)
        for clause in clauses:
            for index in clause:
                if not (index in false_variables) and not (index in true_variables):
                    #we forgot to include index, so we need to add it now
                    false_variables.append(index)
        self.clauses = copy.deepcopy(clauses)
        self.false_variables = copy.deepcopy(false_variables)
        self.true_variables = copy.deepcopy(true_variables)
        
    def __str__(self):
        #First the symbolic part
        clausesStrings = []
        for clause in self.clauses:
            clauseStrings = []
            for index in clause:
                clauseStrings.append("x" + str(index))
            clausesStrings.append("(" + " v ".join(clauseStrings) + ")")
        clauseString = " ^ ".join(clausesStrings)
        #Next the evaluations
        evaluatedClausesStrings = []
        for clause in self.clauses:
            evaluatedClauseStrings = []
            for index in clause:
                evaluatedClauseStrings.append(" " + ("F" if (index in self.false_variables) else "T"))
            evaluatedClausesStrings.append("(" + " v ".join(evaluatedClauseStrings) + ")")
        evaluatedClauseString = " ^ ".join(evaluatedClausesStrings)
        falseStrings = []
        for var in self.false_variables:
            falseStrings.append("x" + str(var))
        falseString = ", ".join(falseStrings)
        return clauseString + "\n" + evaluatedClauseString + "\nRemaining to flip: " + falseString
    
    def check_still_false(self):
        '''Returns true iff the formula still evaluates to false.'''
        for clause in self.clauses:
            all_false = True
            for index in clause:
                if index in self.true_variables:
                    all_false = False
            if all_false:
                #there is a clause that is still all false; the whole thing is still false!
                return True
        #Each of the clauses had a true part, so the whole thing is now true!
        return False
        
    def get_options(self):
        '''Returns all possible moves from this position.'''
        options = []
        for move in self.false_variables:
            #create the potential move option
            new_false_vars = copy.deepcopy(self.false_variables)
            new_false_vars.remove(move)
            new_true_vars = copy.deepcopy(self.true_variables)
            new_true_vars.append(move)
            option = AvoidTrue(self.clauses, new_false_vars, new_true_vars)
            #check that the option hasn't become true
            if option.check_still_false():
                options.append(option)
        return options
    
    def __eq__(self, other):
        """Returns whether self equals other."""
        return self.true_variables == other.true_variables and self.false_variables == other.false_variables and self.clauses == other.clauses
    
    def __hash__(self):
        return super().__hash__()
    
    def standardize(self):
        """Returns an equivalent version of self to simplify things."""
        standard = copy.deepcopy(self)
        
        #print("standard:")
        #print(standard)
        
        #remove repeated elements from clauses
        for clause in standard.clauses:
            i = 0
            for element in clause:
                if element in clause[ : i]:
                    #the element has already appeared, so delete this piece
                    del clause[i]
                else:
                    i += 1
                
        
        #change all the variable indices to be 0, 1, ..., to k
        all_vars = standard.true_variables + standard.false_variables
        mex_val = cgt.mex(all_vars)
        maximum = max(all_vars)
        while (mex_val < maximum):
            #print("all_vars:", all_vars)
            #print("mex_val:", mex_val)
            #print("maximum:", maximum)
            
            #swap maximum -> mex
            if maximum in standard.true_variables:
                standard.true_variables.remove(maximum)
                standard.true_variables.append(mex_val)
            elif maximum in standard.false_variables:
                standard.false_variables.remove(maximum)
                standard.false_variables.append(mex_val)
            for clause in standard.clauses:
                if maximum in clause:
                    clause.remove(maximum)
                    clause.append(mex_val)
            
            all_vars = standard.true_variables + standard.false_variables
            mex_val = cgt.mex(all_vars)
            maximum = max(all_vars)
            
            #print("Changed one variable index")
            #print(standard)
            #print()
            #input()
            
            
        #print("Changed variable indices:")
        #print(standard)
        #print()
        #input()
        
        #sort the lists
        standard.true_variables.sort()
        standard.false_variables.sort()
        for clause in standard.clauses:
            clause.sort()
        
        #print("first sort of lists complete")
        #print(standard)
        #print()
        #input()
        
        #drop extra variables from already-true clauses
        #Shouldn't need to do this anymore because we just drop the clause in the next loop!!!!
        #for i in range(len(standard.clauses)):
        #    clause = standard.clauses[i]
        #    for var in clause:
        #        if var in standard.true_variables:
        #            standard.clauses[i] = [var]
        #            break
                
        #drop clauses that are already true
        i = 0
        while i < len(standard.clauses):
        #for i in range(len(standard.clauses)):
            clause = standard.clauses[i]
            for var in clause:
                if var in standard.true_variables:
                    del standard.clauses[i]
                    i -= 1
                    break
            i += 1
        
        
        #print("dropped extra variables from already-true clauses")
        #print(standard)
        #print()
        #input()
        
        #sort clauses from shortest-to-longest, with ties broken by the first index in the clause
        standard.clauses.sort(key=lambda clause: len(clause) * maximum + clause[0])
        
        
        #renumber the variables based on the order they appear in clauses
        #alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        next_index = 0
        changes = {}
        new_clauses = []
        for clause in standard.clauses:
            new_clause = []
            for element in clause:
                if element not in changes:
                    changes[element] = next_index
                    next_index += 1
                new_clause.append(changes[element])
            new_clause.sort()
            new_clauses.append(new_clause)
        
        new_trues = []
        new_falses = []
            
        for var in standard.false_variables:
            if var not in changes:
                changes[var] = next_index
                next_index +=1 
            new_falses.append(changes[var])
        new_falses.sort()
        
        for var in standard.true_variables:
            #print("changes: " + str(changes))
            if var not in changes:
                changes[var] = next_index
                next_index +=1
            new_trues.append(changes[var])
        new_trues.sort()
            
        #print()
        #print("In simplification... after final change:")
        #print("standard: " + str(standard))
            
        standard = AvoidTrue(new_clauses, new_falses, new_trues)
        
        #print("changed to: " + str(standard))
        
        #print("After final sort")
        #print(standard)
        #print()
        #input()
        
        return standard
            
            
 
    
print("creating avoid_true_a...")
avoid_true_a = AvoidTrue([[1, 2, 3], [2, 3, 7], [1]], [], [])
print("creating avoid_true_b...")
avoid_true_b = AvoidTrue([[1, 4, 2], [5, 2, 4], [1]], [], [])


print("avoid_true_a.standardize():")
standard_a = avoid_true_a.standardize()
print(standard_a)
print()
print("avoid_true_b.standardize():")
standard_b = avoid_true_b.standardize()
print(standard_b)
print()
print("Should be True:", standard_a == standard_b)




smasher = cgt.GrundySmasher()


nimber = smasher.evaluate(avoid_true_a)
nimberB = smasher.evaluate(avoid_true_b)

print(avoid_true_a)
print("... has nimber:", nimber)

print(avoid_true_b)
print("... has nimberB:", nimberB)







game_c = AvoidTrue([[0, 1], [0, 2, 3]], [0, 1, 2, 3, 4], [])

print("game_c:")
print(game_c)
print("... has nimber:",smasher.evaluate(game_c))

game_d = AvoidTrue([[0, 3, 5], [1, 1, 2], [1, 3, 4]], [], [5])
print("*********************")
print()
print("game_d:")
print(game_d)
print(game_d.standardize())
#print("... has nimber:", smasher.evaluate(game_d))
cgt.print_impartial_position_and_options(game_d)

print()
print("******************")
print()

game_e = AvoidTrue([[0, 3, 5], [1, 1, 2], [1, 3, 4]], [], [])
print("game_e:")
print(game_e)
print("... has nimber:", smasher.evaluate(game_e))

cgt.print_impartial_position_and_options(game_e)

input("Press Enter to run the big tests...")

#clauses = [[1, 2], [3, 4], [1, 3]]

vars = [1, 2, 3, 4, 5, 6, 7, 8]

print("Generating games with variable indices " + str(vars) + "...")

print("Trying out 2-CNF positions...")

if False:
    for index1 in [1]:
        for index2 in [2]:
            for index3 in vars[:3]:
                for index4 in vars[:4]:
                    for index5 in vars[:5]:
                        for index6 in vars:
                            for index7 in vars:
                                for index8 in vars:
                                    #for index9 in vars:
                                        #for index10 in vars:
                                    new_clauses = [[index1, index2], [index3, index4], [index5, index6], [index7, index8]] #, [index9, index10]]
                                    position = AvoidTrue(new_clauses, [], [])
                                    smasher.evaluate(position)
                        #print("depth: 5")
                    print("depth: 4")
                print("depth: 3")
            print("depth: 2")
        print("depth: 1")
                
                #print(position)
                #print("... has nimber: " + str(smasher.evaluate(position)))
 


print("Done with 2-CNF")
print()
print("*********************************************************************************")
print()
input("Press enter to try 3-CNF...")


for index1 in [1]:
    for index2 in [2]:
        for index3 in vars[:3]:
            for index4 in vars[:4]:
                for index5 in vars[:5]:
                    for index6 in vars:
                        for index7 in vars:
                            for index8 in vars:
                                for index9 in vars:
                                    #for index10 in vars:
                                    new_clauses = [[index1, index2, index3], [index4, index5, index6], [index7, index8, index9]] #, [index9, index10]]
                                    position = AvoidTrue(new_clauses, [], [])
                                    smasher.evaluate(position)
                    #print("depth: 5")
                print("depth: 4")
            print("depth: 3")
        print("depth: 2")
    print("depth: 1")
                
                #print(position)
                #print("... has nimber: " + str(smasher.evaluate(position)))

n = 3
seen_nimbers = list(range(n+1))
print("Let's look for nimbers above", n, "...")

for position in list(smasher.memo):
    nimber = smasher.evaluate(position)
    if not nimber in seen_nimbers:
        seen_nimbers.append(nimber)
        print("New nimber!!!!!!")
        cgt.print_impartial_position_and_options(position)
        #print(position)
        #print("... has nimber: " + str(nimber))
        print()




print("Done!")
