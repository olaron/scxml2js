# Test de transitions temporisés

## Fonctionnement

Le test se lance avec le script `run_test.sh`

Ce script va appeler le programme java pour
convertir le fichier `delay.scxml` en `delay.js`

Le script va ensuite lancer `test.js` avec `node` pour executer le test

## Test

<img src="http://i.imgur.com/hWebhzm.png" title="Statemachine" alt="Statemachine" width="600px"/>

Dans cette machine à états, chaque transition effectue une action `<log/>`
qui a pour effet d'afficher son propre nom dans la console.

La transition `Start` effectue deux `<send/>`:

- Le premier envoie l'évènement `Tick` avec un délai de 1s
- Le deuxième envoie l'évènement `Stop` avec un délai de 5.5s

La transition `Tick` envoie lui aussi un évènement `Tick`
avec un délai de 1s

La transition `Stop` quant à elle, effectue un `<cancel/>` qui interrompt l'évènement temporisé `Tick`

Le script de test active la machine et lui envoie l'évènement `Start`

## Résultat attendu

On s'attend alors à ce que la machine à états fasse la transition `Start`, suivie de 5 fois la transition `Tick`, suivie de la transition `Stop`

Le test devrait afficher le message suivant :

```
Start
Tick
Tick
Tick
Tick
Tick
Stop
```
