# DAAR_Cloning_egrep-

lien rapport : https://www.overleaf.com/project/6adcc2c5cbee7aeb661130b0/share#95c8e09b69da09d10e1ea476ff594b9eab1e44f69ed2c52d

Lien vers le GitHub: https://github.com/mSETBEL/DAAR_Cloning_egrep-.git

Compilation
javac *.java

Lancer une recherche:

Chaque programme prend deux arguments : le motif et le fichier texte.

java FileSearcher "S(a|g|r)(a|g|r)*on" data/${livre.txt}   
java KMP "Chicago" data/${livre.txt}   
java BoyerMoore "Chicago" data/${livre.txt}    
