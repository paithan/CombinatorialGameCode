import cgt
import copy

 
class AvoidTrue(cgt.ImpartialGame):
    """Models an AvoidTrue state.
    attributes: false variables (a list of indices), true variables (a list of indices), and clauses (a list of lists of indices)."""
    
    def __init__(self, clauses, false_variables=[], true_variables = []):
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
            
            
 
    

avoid_true_a = AvoidTrue([[1, 2, 3], [2, 3, 5], [1]])
smasher = cgt.GrundySmasher()

print("Starting game:")
print(avoid_true_a)

options = avoid_true_a.get_options()

print("Options:")

for option in options:
    print("Move option:")
    print(option)
    print("... has nimber: " + str(smasher.evaluate(option)))
    print()


print("Starting game has nimber: " + str(smasher.evaluate(avoid_true_a)))

#clauses = [[1, 2], [3, 4], [1, 3]]

vars = [1, 2, 3, 4, 5, 6]

print("Generating games with variable indices " + str(vars) + "...")

for index1 in [1]:
    for index2 in [2]:
        for index3 in vars:
            for index4 in vars:
                for index5 in vars:
                    for index6 in vars:
                        for index7 in vars:
                            for index8 in vars:
                                #for index9 in vars:
                                    #for index10 in vars:
                                new_clauses = [[index1, index2], [index3, index4], [index5, index6], [index7, index8]] #, [index9, index10]]
                                position = AvoidTrue(new_clauses)
                                smasher.evaluate(position)
                    #print("depth: 5")
                print("depth: 4")
            print("depth: 3")
        print("depth: 2")
    print("depth: 1")
                
                #print(position)
                #print("... has nimber: " + str(smasher.evaluate(position)))
 
print("Let's look for nimbers above 2...")

seen_nimbers = [0, 1, 2]
for position in list(smasher.memo):
    nimber = smasher.evaluate(position)
    if not nimber in seen_nimbers:
        seen_nimbers.append(nimber)
        print(position)
        print("... has nimber: " + str(nimber))
        print()

print("Done!")
