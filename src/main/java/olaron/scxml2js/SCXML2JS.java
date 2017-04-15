package olaron.scxml2js;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

/**
 * Transforme un fichier SCXML en un script JavaScript
 *
 * @author Robin Alonzo
 */
public class SCXML2JS {

    private BufferedWriter out;
    private Document scxml;
    private BufferedReader fsmjsIn;

    public SCXML2JS(String machineFilePath, String outputFilePath) throws Exception {
        File statexmlfile = new File(machineFilePath);
        File outputFile = new File(outputFilePath);
        outputFile.createNewFile();

        fsmjsIn = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/FSM.js")));

        out = new BufferedWriter(new FileWriter(outputFile));

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

        String line = null;
        while ((line=fsmjsIn.readLine()) != null) {
            write(line);
            out.newLine();   // Write system dependent end of line.
        }
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
