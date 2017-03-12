
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.*;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author Robin Alonzo
 */
public class SCXML2JS {

    private static String machineBody = "{\n" +
            "    states : {},\n" +
            "    state : null,\n" +
            "    eventCallbacks : {},\n" +
            "    setInitialState : function(name){\n" +
            "        this.state = this.states[name];\n" +
            "    },\n" +
            "    addState : function (name,state){\n" +
            "       this.states[name] = state;\n" +
            "    },\n" +
            "    submit : function (event) {\n" +
            "        let s = this.state[event];\n" +
            "        if(s) {\n" +
            "            this.eventCallbacks[s.send]();\n" +
            "            this.state = this.states[s.target];\n" +
            "        }\n" +
            "    },\n" +
            "    connectEvent : function(event, func){\n" +
            "        this.eventCallbacks[event] = func;\n" +
            "    }\n" +
            "}";

    private BufferedWriter out;
    private SCXML machine;

    public SCXML2JS(String machineFilePath, String outputFilePath) throws IOException, ModelException, XMLStreamException {
        File statexmlfile = new File(machineFilePath);
        File outputFile = new File(outputFilePath);
        outputFile.createNewFile();

        out = Files.newBufferedWriter(outputFile.toPath());
        machine = SCXMLReader.read(Files.newBufferedReader(statexmlfile.toPath()));
    }

    void convert() throws IOException{
        {
            out.write("window."+machine.getName()+" = "+machineBody+";");
            out.flush();
        }

        {
            for(Map.Entry<String, TransitionTarget> target : machine.getTargets().entrySet()){
                addState(target.getKey(), (State) target.getValue());
            }
        }
        {
            setInitialState(machine.getInitial());
        }
    }

    private void setInitialState(String initial) throws IOException {
        out.write(machine.getName()+".setInitialState(\""+initial+"\");");
        out.flush();
    }

    private void addState(String id, State state) throws IOException {
        List<Transition> transitions = state.getTransitionsList();
        out.write(machine.getName()+".addState(\""+state.getId()+"\",{\n");
        for (Transition transition : transitions) {
            out.write(getTransitionCode(transition));
            out.flush();
        }
        out.write("\n});\n");
        out.flush();
    }

    private String getTransitionCode(Transition transition) {
        String target = ((TransitionTarget)(transition.getTargets().toArray())[0]).getId();
        String send = "";
        String raise = "";
        for (Action action : transition.getActions()) {
            if(action instanceof Send){
                send = "send:\""+((Send) action).getEvent()+"\",\n";
            }
            if(action instanceof Raise){
                raise = "raise:\""+((Raise) action).getEvent()+"\",\n";
            }
        }
        return transition.getEvent()+ ":{"+
                "target:\""+target+"\",\n"+send+raise+"},";
    }

}
