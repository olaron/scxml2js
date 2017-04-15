#Test d'états hierarchiques

##Fonctionnement

Le test se lance avec le script `run_test.sh`

Ce script va appeler le programme java pour 
convertir le fichier `hierarchy.scxml` en `hierarchy.js`

Le script va ensuite lancer `test.js` avec `node` pour executer le test

##Test

![Statemachine](http://i.imgur.com/pZl4TVo.png "Statemachine")

Le test commence par associer des fonctions à 
chaque onEntry et onExit de tous les états de la machine
ainsi qu'aux transitions a et b

Le test envoie ensuite les évènements suivants à la machine: `a b b a b` dans cet ordre

Le résultat attendu est le suivant:

```
 - Machine start
Enter A
Enter AA
 - Submit a
Exit AA
Transition a
Enter AB
 - Submit b
Exit AB
Exit A
Transition b
Enter B
Enter BA
 - Submit b
 - Submit a
Exit BA
Transition a
Enter BB
 - Submit b
Exit BB
Exit B
Transition b
Enter A
Enter AA
```
