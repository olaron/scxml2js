/**
 * Created by Robin Alonzo on 2/27/2017.
 */
var testmachine = {
    states : {},
    state : null,
    eventCallbacks : {},
    setInitialState : function(name){
        this.state = this.states[name];
    },
    addState : function (name,state){
       this.states[name] = state;
    },
    submit : function (event) {
        let s = this.state[event];
        if(s) {
            this.eventCallbacks[s.send]();
            this.state = this.states[s.target];
        }
    },
    connectEvent : function(event, func){
        this.eventCallbacks[event] = func;
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
testmachine.connectEvent("Start",function(){
    console.log("Started");
});
testmachine.connectEvent("Stop",function(){
    console.log("Stopped");
});
testmachine.connectEvent("Reset",function(){
    console.log("Reseted");
});
testmachine.submit("BStartStop");
testmachine.submit("BStartStop");
testmachine.submit("BStartStop");