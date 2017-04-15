package olaron.scxml2js;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Transforme un fichier SCXML en un script JavaScript
 *
 * @author Robin Alonzo
 */
public class SCXML2JS {

    private static final String setup =
            "\"use strict\";\n" +
                    "Set.prototype.union = function(setB) {\n" +
                    "    var union = new Set(this);\n" +
                    "    for (var elem of setB) {\n" +
                    "        union.add(elem);\n" +
                    "    }\n" +
                    "    return union;\n" +
                    "};\n" +
                    "\n" +
                    "var states = {}; // Ensebmble des états de la machine\n" +
                    "var eventCallbacks = {}; // Ensemble des fonctions callbacks liés à des events\n" +
                    "var onEntryCallbacks = {}; // Ensemble des fonctions callbacks liés à l'entée d'états\n" +
                    "var onExitCallbacks = {}; // Ensemble des fonctions callbacks liés à la sortie d'états\n" +
                    "var eventQueue = []; // File d'évenements de la machine\n" +
                    "var delayedEvents = {}; // Ensemble des évenements délayés\n" +
                    "\n" +
                    "function FSM(name){\n" +
                    "    this.name = name; // Nom de l'état/machine\n" +
                    "    this.transitions = {}; // Liste des transitions sortant de cet état\n" +
                    "    //this.state = null;\n" +
                    "\n" +
                    "    this.parent = null; // État parent à cet état\n" +
                    "    this.active = false; // État courrant ou non\n" +
                    "    this.deepStates = new Set(); // Liste des noms des états présents dans cette machine (récursif)\n" +
                    "    this.states = new Set(); // Liste des noms des états présents dans cette machine\n" +
                    "    this.initialState = []; // Etat(s) initial(aux) dans cette machine\n" +
                    "\n" +
                    "    this.root = name; // État racine de la machine\n" +
                    "    states[name] = this;\n" +
                    "\n" +
                    "    this.setInitialState = function(name){\n" +
                    "        this.initialState.push(name);\n" +
                    "        return this;\n" +
                    "    };\n" +
                    "    this.setParallel = function(){\n" +
                    "        this.initialState = Array.from(this.states);\n" +
                    "        return this;\n" +
                    "    };\n" +
                    "    this.addState = function (machine){\n" +
                    "        machine.parent = this.name;\n" +
                    "        machine.root = this.root;\n" +
                    "        this.states.add(machine.name);\n" +
                    "        this.deepStates.add(machine.name);\n" +
                    "        this.deepStates = this.deepStates.union(machine.deepStates);\n" +
                    "        return this;\n" +
                    "    };\n" +
                    "    this.addTransition = function(event_in,target,actions){\n" +
                    "          this.transitions[event_in] = {\n" +
                    "              actions : actions,\n" +
                    "              target : target\n" +
                    "          };\n" +
                    "          return this;\n" +
                    "    };\n" +
                    "    \n" +
                    "    \n" +
                    "\n" +
                    "    this.activate = function(recursive){\n" +
                    "        if(onEntryCallbacks[this.name]){\n" +
                    "            onEntryCallbacks[this.name]();\n" +
                    "        }\n" +
                    "        this.active = true;\n" +
                    "        if(recursive){\n" +
                    "            for(var state of this.initialState){\n" +
                    "                states[state].activate(true);\n" +
                    "            }\n" +
                    "        }\n" +
                    "    };\n" +
                    "\n" +
                    "    this.desactivate = function(){\n" +
                    "        for(var state of this.states){\n" +
                    "            if(states[state].active){\n" +
                    "                states[state].desactivate();\n" +
                    "            }\n" +
                    "        }\n" +
                    "        this.active = false;\n" +
                    "        if(onExitCallbacks[this.name]){\n" +
                    "            onExitCallbacks[this.name]();\n" +
                    "        }\n" +
                    "    };\n" +
                    "\n" +
                    "    this.doAction = function(actions){\n" +
                    "        for(var i in actions){\n" +
                    "            let type = actions[i].type;\n" +
                    "            let action = actions[i].action;\n" +
                    "            if(type === \"send\"){\n" +
                    "                if(action.id && action.delay){\n" +
                    "                    delayedEvents[action.id] =\n" +
                    "                        setTimeout(function(rootState,event){\n" +
                    "                          rootState.submit(event);},\n" +
                    "                            action.delay,\n" +
                    "                            states[this.root],\n" +
                    "                            action.event\n" +
                    "                        );\n" +
                    "                }else{\n" +
                    "                    states[this.root].submit(action.event);\n" +
                    "                }\n" +
                    "            }else if(type === \"log\"){\n" +
                    "                console.log(eval(action.expr));\n" +
                    "            }else if(type === \"cancel\"){\n" +
                    "                clearTimeout(delayedEvents[action.sendid]);\n" +
                    "            }\n" +
                    "        }\n" +
                    "    };\n" +
                    "\n" +
                    "    this.move = function(transition,down){\n" +
                    "        if(this.deepStates.has(transition.target)){\n" +
                    "            if(!down){\n" +
                    "                this.doAction(transition.actions);\n" +
                    "            }\n" +
                    "            if(this.states.has(transition.target)){\n" +
                    "                for(var state of this.states){\n" +
                    "                    if(states[state].active){\n" +
                    "                        states[state].desactivate();\n" +
                    "                    }\n" +
                    "                }\n" +
                    "                for(var state of this.states){\n" +
                    "                    if(state === transition.target){\n" +
                    "                        states[state].activate(true);\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }else{\n" +
                    "                for(var state of this.states){\n" +
                    "                    if(states[state].deepStates.has(transition.target)){\n" +
                    "                        states[state].activate();\n" +
                    "                        states[state].move(transition,true);\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }else{\n" +
                    "            this.desactivate();\n" +
                    "            states[this.parent].move(transition,false);\n" +
                    "        }\n" +
                    "    };\n" +
                    "\n" +
                    "    this.doSubmit = function (event) {\n" +
                    "        for(var state of this.states){\n" +
                    "            if(states[state].active){\n" +
                    "                if(states[state].doSubmit(event)){\n" +
                    "                    return true;\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "        let s = this.transitions[event];\n" +
                    "        if(s) {\n" +
                    "            this.move(s);\n" +
                    "            return true;\n" +
                    "        }else{\n" +
                    "            return false;\n" +
                    "        }\n" +
                    "    };\n" +
                    "\n" +
                    "    this.submit = function(event){\n" +
                    "        if(eventCallbacks[event]){\n" +
                    "            eventCallbacks[event]();\n" +
                    "        }\n" +
                    "        eventQueue.push(event);\n" +
                    "        if(eventQueue.length === 1) {\n" +
                    "            while (eventQueue.length > 0) {\n" +
                    "                this.doSubmit(eventQueue.shift());\n" +
                    "            }\n" +
                    "        }\n" +
                    "    };\n" +
                    "\n" +
                    "    this.connectEvent = function(event, func){\n" +
                    "        eventCallbacks[event] = func;\n" +
                    "    };\n" +
                    "    this.connectOnEntry = function(state, func){\n" +
                    "        onEntryCallbacks[state] = func;\n" +
                    "    };\n" +
                    "    this.connectOnExit = function(state, func){\n" +
                    "        onExitCallbacks[state] = func;\n" +
                    "    };\n" +
                    "    this.start = function(){\n" +
                    "        this.activate(true);\n" +
                    "    };\n" +
                    "}";

    private BufferedWriter out;
    private Document scxml;

    public SCXML2JS(String machineFilePath, String outputFilePath) throws Exception {
        File statexmlfile = new File(machineFilePath);
        File outputFile = new File(outputFilePath);
        outputFile.createNewFile();

        out = Files.newBufferedWriter(outputFile.toPath());

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        scxml = dBuilder.parse(statexmlfile);
    }

    private void write(String str){
        try {
            out.write(str);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAttribute(Node node, String attribute){
        try {
            return node.getAttributes().getNamedItem(attribute).getNodeValue();
        }catch (Exception e){
            // TODO Faire ça mieux
            return null; // Si l'attribut recherché n'éxiste pas, on renvoie null
        }
    }

    public void convert() throws IOException{
        Node rootNode = scxml.getDocumentElement();
        String machineName = getAttribute(rootNode,"name");

        write(setup+"\n");
        write("var "+machineName+" = new FSM(\""+machineName+"\")");
        Node initialState = convertChilds(rootNode.getChildNodes());
        String initialId = getAttribute(initialState,"id");
        String initialAttribute = getAttribute(rootNode,"initial");
        if(initialAttribute != null) initialId = initialAttribute;
        write(".setInitialState(\""+initialId+"\");\n");
        write("if(typeof exports !== 'undefined')exports."+machineName+" = "+machineName+";\n");
    }

    private Node convertChilds(NodeList childs){
        Node firstState = null;
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            String nodeName = child.getNodeName();
            switch (nodeName){
                case "state":{
                    if(firstState == null){
                        firstState = child;
                    }
                    addState(child);
                    break;
                }
                case "parallel":{
                    if(firstState == null){
                        firstState = child;
                    }
                    addParallelState(child);
                    break;
                }
                case "transition":{
                    addTransition(child);
                    break;
                }
            }
        }
        return firstState;
    }

    private void addState(Node state){
        write(".addState(\n");
        write("new FSM(\""+getAttribute(state,"id")+"\")");
        NodeList childs = state.getChildNodes();
        if(childs.getLength() > 0){
            Node initialState = convertChilds(childs);
            if(initialState != null) {
                String initialId = getAttribute(initialState,"id");
                String initialAttribute = getAttribute(state,"initial");
                if(initialAttribute != null) initialId = initialAttribute;
                write(".setInitialState(\"" + initialId + "\")");
            }
        }
        write(")\n");
    }

    private void addParallelState(Node state) {
        write(".addState(\n");
        write("new FSM(\""+getAttribute(state,"id")+"\")");
        NodeList childs = state.getChildNodes();
        if(childs.getLength() > 0){
            convertChilds(childs);
        }
        write(".setParallel())\n");
    }

    private void addTransition(Node transition) {
        String event = getAttribute(transition,"event");
        String target = getAttribute(transition,"target");
        write(".addTransition(\""+event+"\",\""+target+"\",\n");
        addActions(transition.getChildNodes());
        write(")\n");
    }

    private void addActions(NodeList actions) {
        write("[");
        for (int i = 0; i < actions.getLength(); i++) {
            Node action = actions.item(i);
            if(action instanceof Element) {
                write("{");
                write("\"type\" : \""+action.getNodeName() + "\",");
                write("\"action\" : {");
                NamedNodeMap attributes = action.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribute = attributes.item(j);
                    String value = attribute.getNodeValue();
                    value = value.replaceAll("\"", "\\\\\"");
                    write("\""+attribute.getNodeName() + "\":\"" + value + "\",");
                }
                write("}},");
            }
        }
        write("]");
    }


}
