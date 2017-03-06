
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Description
 *
 * @author Robin Alonzo
 */
public class SCXML2JS {

    static void convert(String machineFilePath, String outputFilePath) throws IOException, ModelException, XMLStreamException {
        File statexmlfile = new File(machineFilePath);
        SCXML machine = SCXMLReader.read(Files.newBufferedReader(statexmlfile.toPath()));
        File outputFile = new File(outputFilePath);
        outputFile.createNewFile();
        BufferedWriter out = Files.newBufferedWriter(outputFile.toPath());
        out.write("var "+machine.getName()+" = {};");
        out.flush();
    }

}
