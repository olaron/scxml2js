"use strict";
Set.prototype.union = function(setB) {
    var union = new Set(this);
    for (var elem of setB) {
        union.add(elem);
    }
    return union;
};

var states = {}; // Ensebmble des états de la machine
var eventCallbacks = {}; // Ensemble des fonctions callbacks liés à des events
var onEntryCallbacks = {}; // Ensemble des fonctions callbacks liés à l'entée d'états
var onExitCallbacks = {}; // Ensemble des fonctions callbacks liés à la sortie d'états
var eventQueue = []; // File d'évenements de la machine
var delayedEvents = {}; // Ensemble des évenements délayés

function FSM(name){
    this.name = name; // Nom de l'état/machine
    this.transitions = {}; // Liste des transitions sortant de cet état
    //this.state = null;

    this.parent = null; // État parent à cet état
    this.active = false; // État courrant ou non
    this.deepStates = new Set(); // Liste des noms des états présents dans cette machine (récursif)
    this.states = new Set(); // Liste des noms des états présents dans cette machine
    this.initialState = []; // Etat(s) initial(aux) dans cette machine

    this.root = name; // État racine de la machine
    states[name] = this;

    this.setInitialState = function(name){
        this.initialState.push(name);
        return this;
    };
    this.setParallel = function(){
        this.initialState = Array.from(this.states);
        return this;
    };
    this.addState = function (machine){
        machine.parent = this.name;
        machine.root = this.root;
        this.states.add(machine.name);
        this.deepStates.add(machine.name);
        this.deepStates = this.deepStates.union(machine.deepStates);
        return this;
    };
    this.addTransition = function(event_in,target,actions){
        this.transitions[event_in] = {
            actions : actions,
            target : target
        };
        return this;
    };

    this.activate = function(recursive){
        if(onEntryCallbacks[this.name]){
            onEntryCallbacks[this.name]();
        }
        this.active = true;
        if(recursive){
            for(var state of this.initialState){
                states[state].activate(true);
            }
        }
    };

    this.desactivate = function(){
        for(var state of this.states){
            if(states[state].active){
                states[state].desactivate();
            }
        }
        this.active = false;
        if(onExitCallbacks[this.name]){
            onExitCallbacks[this.name]();
        }
    };

    this.doAction = function(actions){
        for(var i in actions){
            let type = actions[i].type;
            let action = actions[i].action;
            if(type === "send"){
                if(action.id && action.delay){
                    delayedEvents[action.id] =
                        setTimeout(function(rootState,event){
                                rootState.submit(event);
                                },
                            action.delay,
                            states[this.root],
                            action.event
                        );
                }else{
                    states[this.root].submit(action.event);
                }
            }else if(type === "log"){
                console.log(eval(action.expr));
            }else if(type === "cancel"){
                clearTimeout(delayedEvents[action.sendid]);
            }
        }
    };

    this.move = function(transition,down){
        if(this.deepStates.has(transition.target)){
            if(!down){
                this.doAction(transition.actions);
            }
            if(this.states.has(transition.target)){
                for(var state of this.states){
                    if(states[state].active){
                        states[state].desactivate();
                    }
                }
                for(var state of this.states){
                    if(state === transition.target){
                        states[state].activate(true);
                    }
                }
            }else{
                for(var state of this.states){
                    if(states[state].deepStates.has(transition.target)){
                        states[state].activate();
                        states[state].move(transition,true);
                    }
                }
            }
        }else{
            this.desactivate();
            states[this.parent].move(transition,false);
        }
    };

    this.doSubmit = function (event) {
        for(var state of this.states){
            if(states[state].active){
                if(states[state].doSubmit(event)){
                    return true;
                }
            }
        }
        let s = this.transitions[event];
        if(s) {
            this.move(s);
            return true;
        }else{
            return false;
        }
    };

    this.submit = function(event){
        if(eventCallbacks[event]){
            eventCallbacks[event]();
        }
        eventQueue.push(event);
        if(eventQueue.length === 1) {
            while (eventQueue.length > 0) {
                this.doSubmit(eventQueue.shift());
            }
        }
    };

    this.connectEvent = function(event, func){
        eventCallbacks[event] = func;
    };
    this.connectOnEntry = function(state, func){
        onEntryCallbacks[state] = func;
    };
    this.connectOnExit = function(state, func){
        onExitCallbacks[state] = func;
    };
    this.start = function(){
        this.activate(true);
    };
}
var story = new FSM("story").addState(
new FSM("Story").addState(
new FSM("Room").addState(
new FSM("Clock").addState(
new FSM("Ticker").addTransition("Tick","Ticker",
[{"type" : "send","action" : {"delay":"1000","event":"Tick","id":"tick",}},{"type" : "log","action" : {"expr":"time",}},])
)
.setInitialState("Ticker"))
.addState(
new FSM("Bomb").addState(
new FSM("Intro").addTransition("C1","Timer",
[])
.addTransition("C2","Buttons",
[])
)
.addState(
new FSM("Timer").addTransition("C1","Buttons",
[])
)
.addState(
new FSM("Buttons").addTransition("C1","Timer",
[])
)
.setInitialState("Intro"))
.addTransition("Boom","Dead",
[])
.addTransition("C3","GoodEnd",
[{"type" : "cancel","action" : {"sendid":"tick",}},])
.setParallel())
.addState(
new FSM("Dead").addTransition("C1","Start",
[])
)
.addState(
new FSM("GoodEnd").addTransition("C1","Start",
[])
)
.setInitialState("Room"))
.addState(
new FSM("Start").addTransition("C1","Story",
[{"type" : "send","action" : {"delay":"1000","event":"Tick","id":"tick",}},])
)
.setInitialState("Start");
if(typeof exports !== 'undefined')exports.story = story;
