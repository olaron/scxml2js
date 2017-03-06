/**
 * Created by Robin Alonzo on 2/27/2017.
 */
var testmachine = {
    states : {},
    state : null,
    callbacks : {},
    setInitialState : function(name){
        this.state = this.states[name];
    },
    addState : function (name,state){
       this.states[name] = state;
    },
    submit : function (event) {
        s = this.states[this.state[event].target];
        if(s) {

            this.state = s;
        }
    },
    connectEvent : function(event, func){
        this.callbacks[event] = func;
    }
};
testmachine.addState(
    "Reseted",
    {
        BStartStop:{
            target:"Running",
            send:"Start"
        }
    }
);
testmachine.addState(
    "Running",
    {
        BPause:{
            target:"Paused",
            send:"Pause"
        },
        BStartStop:{
            target:"Stopped",
            send:"Stop"
        }
    }
);
testmachine.addState(
    "Stopped",
    {
        BStartStop : {
            target:"Reseted",
            send:"Reset"
        }
    }
);
testmachine.addState(
    "Paused",
    {
        BPause:{
            target:"Running",
            send:"Resume"
        },
        BStartStop:{
            target:"Stopped",
            send:"Stop"
        }
    }
);
testmachine.setInitialState("Reseted");