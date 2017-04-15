window.testmachine = {
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
};testmachine.addState("Reseted",{
BStartStop:{target:"Running",
send:"Start",
},
});
testmachine.addState("Stopped",{
BStartStop:{target:"Reseted",
send:"Reset",
},
});
testmachine.addState("Running",{
BPause:{target:"Paused",
send:"Pause",
},BStartStop:{target:"Stopped",
send:"Stop",
},
});
testmachine.addState("Paused",{
BPause:{target:"Running",
send:"Resume",
},BStartStop:{target:"Stopped",
send:"Stop",
},
});
testmachine.setInitialState("Reseted");