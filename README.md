# Translator

University course work

Requirements

The programming languages shall include assignment operator, operators for input
and output, as well as conditional operator and cycle operator.The software must read
the code,perform lexical and syntactic analysis of certain algorithms and display the error.

Language grammar
Type: Generating grammar

G = {VT, VN, sigma, P}

VT = {1,2,3,4,5,6,7,8,9,0,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}

VN = {[program], [program name], [ad list], [ad], [type], [id list], [operator list],
      [operator], [input], [output], [appropriation], [expression], [term], [multiplier],
      [id], [kft], [int], [loop], [logical expression], [logical term], [logical multiplier]
      [relation], [relation sign], [condition], [number], [letter], [code]}
      
[program] ::= program [program name] var [ad list] begin { [code] } end
[program name] ::= [id]
[ad list] ::= [ad] | [ad list]; [ad]
[ad] ::= [id list] : [type]
[type] ::= integer | real
[id list] ::= [id] | [id list], [id]
[code] ::= [condition] | [loop] | [operator list] | [code] [comdition] | [code] [loop] | [code] [operator list]
[operator list] ::= [operator]; | [operator list]; [operator]
[operator] ::= [input] | [output] | [appropriation]
[input] ::= read ( [id list] )
[output] ::= write ( [id list] )
[appropriation] ::= [id] = [expression]
[expression] ::= [expression] + [term] | [expression] - [term] | [term]
[term] ::= [multiplier] | [term] * [multiplier] | [term] / [multiplier]
[multiplier] ::= ( [expression] ) | [id] | [kft]
[id] ::= [letter] | [id] [letter]
[kft] ::= [int] | .[int] | [int]. | [int].[int]
[int] ::= [int] | [int] [number]
[loop] ::= while \\[\\ [logical expression] \\]\\ { [code] }
[logical expression] ::= [logical term] | [logical expression] or [logical term]
[logical term] ::= [logical miltiplier] | [logical term] and [logical multiplier]
[logical multiplier] ::= not [logical expression] | \\[\\ [logical expression] \\]\\ | [relation]
[relation] ::= [expression] [relation sign] [expression]
[relation sign] ::= < | > | <= | >= | == | !=
[condition] ::= if \\[\\ [logical expression] \\]\\ { [code] } else { [code] }
[number] ::= 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0
[letter] ::= a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z
