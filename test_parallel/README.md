# Test d'états parallèles

## Fonctionnement

Le test se lance avec le script `run_test.sh`

Ce script va appeler le programme java pour
convertir le fichier `parallel.scxml` en `parallel.js`

Le script va ensuite lancer `test.js` avec `node` pour exécuter le test

## Test

<img src="http://i.imgur.com/3nVDZDk.png" title="Statemachine" alt="Statemachine" width="600px"/>

Le test commence par associer des fonctions à
chaque onEntry et onExit de tous les états de la machine
ainsi qu'aux transitions a, b et c

Le test envoie ensuite les évènements `c a b c a b b c` à la machine, dans cet ordre

## Résultat attendu

```
- Machine start
Enter Start
- Submit c
Exit Start
Transition c
Enter Parallel
Enter B
Enter BA
Enter A
Enter AA
- Submit a
Exit AA
Transition a
Enter AB
- Submit b
Exit BA
Transition b
Enter BB
- Submit c
- Submit a
Exit AB
Transition a
Enter AC
- Submit b
Exit BB
Transition b
Enter BC
- Submit b
Exit BC
Transition b
Enter BA
- Submit c
Exit BA
Exit B
Exit AC
Exit A
Exit Parallel
Transition c
Enter C
```
