# Exemple d'utilisation

Les scripts générés par SCXML2JS peuvent être utilisés
pour la création de page web intéractives.

Cela peut être pratique pour passer d'une vue à une autre
tout en restant sur une même page web par exemple.

Dans cet exemple, on illustre les possibilités avec une *Aventure dont vous êtes le héros*.

## Explications

Une fois avoir généré le fichié `story.js` avec la commande `../run.sh story.scxml story.js`, on peut le lier à notre page HTML :

`<script src="story.js"></script>`

Cela nous donne accès à un objet `FSM` dans la variable `story`.

On peut alors directement mettre en place des fonctions
qui seront appelés par la machine à états :

- `story.connectEvent()`
- `story.connectOnEntry()`
- `story.connectOnExit()`

Ensuite, on peut démarer la machine avec `story.start()`
et appeler `story.submit()` pour envoyer des évènements dessus.
