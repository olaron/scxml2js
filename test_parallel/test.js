var parallel = require("./parallel.js").parallel;

parallel.connectEvent("A",function(){
    console.log("Transition a");
});
parallel.connectEvent("B",function(){
    console.log("Transition b");
});
parallel.connectEvent("C",function(){
    console.log("Transition c");
});

parallel.connectOnEntry("A",function(){
    console.log("Enter A");
});
parallel.connectOnEntry("AA",function(){
    console.log("Enter AA");
});
parallel.connectOnEntry("AB",function(){
    console.log("Enter AB");
});
parallel.connectOnEntry("AC",function(){
    console.log("Enter AC");
});
parallel.connectOnEntry("B",function(){
    console.log("Enter B");
});
parallel.connectOnEntry("BA",function(){
    console.log("Enter BA");
});
parallel.connectOnEntry("BB",function(){
    console.log("Enter BB");
});
parallel.connectOnEntry("BC",function(){
    console.log("Enter BC");
});
parallel.connectOnEntry("C",function(){
    console.log("Enter C");
});
parallel.connectOnEntry("Start",function(){
    console.log("Enter Start");
});
parallel.connectOnEntry("Parallel",function(){
    console.log("Enter Parallel");
});

parallel.connectOnExit("A",function(){
    console.log("Exit A");
});
parallel.connectOnExit("AA",function(){
    console.log("Exit AA");
});
parallel.connectOnExit("AB",function(){
    console.log("Exit AB");
});
parallel.connectOnExit("AC",function(){
    console.log("Exit AC");
});
parallel.connectOnExit("B",function(){
    console.log("Exit B");
});
parallel.connectOnExit("BA",function(){
    console.log("Exit BA");
});
parallel.connectOnExit("BB",function(){
    console.log("Exit BB");
});
parallel.connectOnExit("BC",function(){
    console.log("Exit BC");
});
parallel.connectOnExit("C",function(){
    console.log("Exit C");
});
parallel.connectOnExit("Start",function(){
    console.log("Exit Start");
});
parallel.connectOnExit("Parallel",function(){
    console.log("Exit Parallel");
});

console.log(" - Machine start");
parallel.activate(true);
console.log(" - Submit c");
parallel.submit("c");
console.log(" - Submit a");
parallel.submit("a");
console.log(" - Submit b");
parallel.submit("b");
console.log(" - Submit c");
parallel.submit("c");
console.log(" - Submit a");
parallel.submit("a");
console.log(" - Submit b");
parallel.submit("b");
console.log(" - Submit b");
parallel.submit("b");
console.log(" - Submit c");
parallel.submit("c");
