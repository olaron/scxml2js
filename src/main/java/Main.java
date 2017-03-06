import org.apache.commons.scxml2.model.ModelException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * Description
 *
 * @author Robin Alonzo
 */
public class Main {

    public static void main(String[] args){
        try {
            if (args.length < 1) {
                System.out.println("Veuillez specifier un fichier .scxml en argument");
                return;
            }
            SCXML2JS.convert(args[0],"out.js");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ModelException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
