/**
 * Created by Robin Alonzo on 2/27/2017.
 */

Set.prototype.union = function(setB) {
    var union = new Set(this);
    for (var elem of setB) {
        union.add(elem);
    }
    return union;
}

var states = {}; // Ensebmble des machines / états
var eventCallbacks = {}; // Ensemble des fonctions callbacks liés à des events
var onEntryCallbacks = {}; // Ensemble des fonctions callbacks liés à l'entée d'états
var onExitCallbacks = {}; // Ensemble des fonctions callbacks liés à la sortie d'états

function FSM(name){
    this.name = name; // Nom de l'état/machine
    this.transitions = {}; // Liste des transitions sortant de cet état
    //this.state = null;

    this.parent = null; // État parent à cet état
    this.active = false; // État courrant ou non
    this.deepStates = new Set(); // Liste des noms des états présents dans cette machine (récursif)
    this.states = new Set(); // Liste des noms des états présents dans cette machine
    this.initialState = null; // Etat initial dans cette machine

    states[name] = this;

    this.activate = function(recursive){
        if(onEntryCallbacks[this.name]){
            onEntryCallbacks[this.name]();
        }
        this.active = true;
        if(recursive){
            if(states[this.initialState]){
                states[this.initialState].activate(true);
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

    this.setInitialState = function(name){
        //this.state = states[name];
        this.initialState = name;
        return this;
    };
    this.addState = function (machine){
        machine.parent = this.name;
        this.states.add(machine.name);
        this.deepStates.add(machine.name);
        this.deepStates = this.deepStates.union(machine.deepStates);

        //console.log("add");
        //console.log(machine);

        return this;
    };
    this.addTransition = function(event_in,action,event_out,target){
          this.transitions[event_in] = {
              action : action,
              event : event_out,
              target : target
          };
          return this;
    };
    this.move = function(transition){
        console.log(this);
        console.log(transition);
        if(this.deepStates.has(transition.target)){
            if(this.states.has(transition.target)){
                for(state of this.states){
                    if(states[state].active){
                        states[state].desactivate();
                    }
                }
                if(eventCallbacks[transition.event]){
                    eventCallbacks[transition.event]();
                }
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
    this.submit = function (event) {
        //if(!this.state) return false;
        //if(this.state.submit(event)) return true;
        //let s = this.state.transitions[event];
        for(state of this.states){
            if(states[state].active){
                if(states[state].submit(event)){
                    return true;
                }
            }
        }
        let s = this.transitions[event];
        if(s) {
            this.move(s);
            return true;
            /*
            if(eventCallbacks[s.event]){
                eventCallbacks[s.event]();
            }
            this.state = states[s.target];
            return true;
            */
        }else{
            return false;
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
    //console.log("end");
    //console.log(this);
}

var testmachine = new FSM("testmachine").addState(
    new FSM("A").addState(
        new FSM("AA").addTransition(
            "a","send","A","AB"
        )
    ).addState(
        new FSM("AB").addTransition(
            "a","send","A","AA"
        )
    ).addTransition(
        "b","send","B","BB"
    ).setInitialState("AA")
).addState(
    new FSM("B").addState(
        new FSM("BA").addTransition(
            "a","send","A","BB"
        )
    ).addState(
        new FSM("BB").addTransition(
            "b","send","B","AA"
        ).addTransition(
            "a","send","A","BA"
        )
    ).setInitialState("BA")
).setInitialState("A");
testmachine.activate(true);
testmachine.connectEvent("A",function(){
    console.log("A");
});
testmachine.connectEvent("B",function(){
    console.log("B");
});
testmachine.connectOnEntry("A",function(){
    console.log("Enter A");
});
testmachine.connectOnEntry("AA",function(){
    console.log("Enter AA");
});
testmachine.connectOnEntry("AB",function(){
    console.log("Enter AB");
});
testmachine.connectOnEntry("B",function(){
    console.log("Enter B");
});
testmachine.connectOnEntry("BA",function(){
    console.log("Enter BA");
});
testmachine.connectOnEntry("BB",function(){
    console.log("Enter BB");
});
testmachine.connectOnExit("A",function(){
    console.log("Exit A");
});
testmachine.connectOnExit("AA",function(){
    console.log("Exit AA");
});
testmachine.connectOnExit("AB",function(){
    console.log("Exit AB");
});
testmachine.connectOnExit("B",function(){
    console.log("Exit B");
});
testmachine.connectOnExit("BA",function(){
    console.log("Exit BA");
});
testmachine.connectOnExit("BB",function(){
    console.log("Exit BB");
});
testmachine.submit("a");
testmachine.submit("b");
testmachine.submit("b");
testmachine.submit("a");
testmachine.submit("b");