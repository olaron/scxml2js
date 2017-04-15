# SCXML2JS

## Prérequis

Ce projet a été réalisé avec
**Java 1.8**, **Maven 3.3.9** pour la compilation,
ainsi que **NodeJS 4.2.6** pour les tests

## Fonctionnalités supprotés

Le programme supporte les machine à états comprenant :
- des états hierarchiques
- des états `<parallel>`

Dans le cas oú plusieur transitions dans plusieur états parallèlisés peuvent réagir à un même évènement, seulement l'une des deux transitions s'effectuera

- des transitions contenant les actions `<send>` `<log>`

Toutes les transitions agissent comme des transition de type `external` pour le moment.
Le contenu de l'attribut `expr` de l'action `<log>` sera évalué en tant qu'expression et affichera le résultat dans la console.

- des transitions avec des `<send>` temporisés ainsi que `<cancel>`

Les actions `<send>` doivent avoir les attributs `id` et `delay` spécifiés pour être temporisé. Si au moins l'un des deux est manquant, le `<send>` ne sera pas temporisé.

- la possibilité définir une fonction qui sera exécuté lors de
l'entrée ou la sortie d'un état en particulier, ou lors de
l'envoi d'un évènement en particulier


## Conditions sur le fichier SCXML

Le fichier SCXML ne peut être composé que des balises suivantes :

`<scxml>` `<state>` `<parallel>` `<transition>` `<send>` `<log>` `<cancel>`

La présence de toute autre balises peut avoir des effets imprévus

## Compilation

Le programme se compile en exécutant le script `build.sh`

Cela va créer un fichier `.jar` exécutable dans un dossier `target`

## Utilisation

Le programme se lance en exécutant le script
`run.sh <fichier scxml> [fichier de destination]`

Si le fichier de destination n'est pas spécifié, il sera `out.js` par défaut

Ce fichier définit un objet `FSM`
portant le même nom que la machine
(spécifié par l'attribut `name` de la balise `<scxml>`)

Cet objet comprend les méthodes suivantes:

- `fsm.start()`

Permet de démarrer la machine à états.

- `fsm.submit(event)`

Permet d'envoyer l'évènement `event` à la machine.

- `fsm.connectEvent(event,func)`

Permet à la machine d'exécuter la fonction `func` lors de l'envoi d'un évènement `event` (via un `<send>` ou un appel à `submit(event)`).

- `fsm.connectOnEntry(state,func)`
- `fsm.connectOnExit(state,func)`

Permet à la machine d'exécuter la fonction `func` lors de léntrée ou la sortie de l'état `state`.
