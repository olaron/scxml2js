var hierarchy = require("./hierarchy.js").hierarchy;

hierarchy.connectEvent("A",function(){
    console.log("Transition a");
});
hierarchy.connectEvent("B",function(){
    console.log("Transition b");
});
hierarchy.connectOnEntry("A",function(){
    console.log("Enter A");
});
hierarchy.connectOnEntry("AA",function(){
    console.log("Enter AA");
});
hierarchy.connectOnEntry("AB",function(){
    console.log("Enter AB");
});
hierarchy.connectOnEntry("B",function(){
    console.log("Enter B");
});
hierarchy.connectOnEntry("BA",function(){
    console.log("Enter BA");
});
hierarchy.connectOnEntry("BB",function(){
    console.log("Enter BB");
});
hierarchy.connectOnExit("A",function(){
    console.log("Exit A");
});
hierarchy.connectOnExit("AA",function(){
    console.log("Exit AA");
});
hierarchy.connectOnExit("AB",function(){
    console.log("Exit AB");
});
hierarchy.connectOnExit("B",function(){
    console.log("Exit B");
});
hierarchy.connectOnExit("BA",function(){
    console.log("Exit BA");
});
hierarchy.connectOnExit("BB",function(){
    console.log("Exit BB");
});

console.log(" - Machine start");
hierarchy.start();
console.log(" - Submit a");
hierarchy.submit("a");
console.log(" - Submit b");
hierarchy.submit("b");
console.log(" - Submit b");
hierarchy.submit("b");
console.log(" - Submit a");
hierarchy.submit("a");
console.log(" - Submit b");
hierarchy.submit("b");