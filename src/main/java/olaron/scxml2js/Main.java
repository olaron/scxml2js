package olaron.scxml2js;

public class Main {

    public static void main(String[] args){
        try {
            if (args.length < 1) {
                System.out.println("Veuillez specifier un fichier .scxml en argument");
                return;
            }
            SCXML2JS scxml2js = new SCXML2JS(args[0],"out.js");
            scxml2js.convert();
            System.out.println("-> out.js");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
