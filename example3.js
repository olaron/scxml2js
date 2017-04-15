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
        this.initialState = [...this.states];
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
    this.addTransition = function(event_in,target,action){
        this.transitions[event_in] = {
            action : action,
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
            for(state of this.initialState){
                states[state].activate(true);
            }
        }
    };

    this.desactivate = function(){
        for(state of this.states){
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
        for(type in actions){
            let action = actions[type];
            if(type === "send"){
                if(action.id && action.delay){
                    delayedEvents[action.id] =
                        setTimeout(function(rootState,event){
                                rootState.submit(event);},
                            action.delay,
                            states[this.root],
                            action.event
                        );
                }else{
                    states[this.root].submit(action.event);
                }
            }else if(type === "log"){
                console.log(action.expr);
            }else if(type === "cancel"){
                clearTimeout(delayedEvents[action.sendid]);
            }
        }
    };

    this.move = function(transition){
        if(this.deepStates.has(transition.target)){
            if(this.states.has(transition.target)){
                for(state of this.states){
                    if(states[state].active){
                        states[state].desactivate();
                    }
                }
                this.doAction(transition.action);
                for(state of this.states){
                    if(state === transition.target){
                        states[state].activate(true);
                    }
                }
            }else{
                for(state of this.states){
                    if(states[state].deepStates.has(transition.target)){
                        states[state].activate();
                        states[state].move(transition);
                    }
                }
            }
        }else{
            this.desactivate();
            states[this.parent].move(transition);
        }
    };

    this.doSubmit = function (event) {
        for(state of this.states){
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
}

var Story = new FSM("Story").addState(
    new FSM("Room").addState(
        new FSM("Clock").addState(
            new FSM("Ticker").addTransition("Tick","Ticker",
                {"send":{"delay":"1000","event":"Tick","id":"tick",},"log":{"expr":"tick",},})
                .addTransition("Stop","Stopped",
                    {"cancel":{"sendid":"tick",},})
        )
            .addState(
                new FSM("Stopped"))
            .setInitialState("Ticker"))
        .addState(
            new FSM("Bomb").addState(
                new FSM("Start").addTransition("LookTimer","Timer",
                    {})
                    .addTransition("LookButtons","Buttons",
                        {})
            )
                .addState(
                    new FSM("Timer").addTransition("LookButtons","Buttons",
                        {})
                )
                .addState(
                    new FSM("Buttons").addTransition("LookTimer","Timer",
                        {})
                )
                .setInitialState("Start"))
        .addTransition("Boom","Dead",
            {})
        .addTransition("Escape","GoodEnd",
            {})
        .setParallel())
    .addState(
        new FSM("Dead").addTransition("Restart","Room",
            {})
    )
    .addState(
        new FSM("GoodEnd").addTransition("Restart","Room",
            {})
    )
    .setInitialState("Room");
Story.activate(true);
Story.submit("Tick");
