package olaron.scxml2js;

public class Main {

    public static void main(String[] args){
        try {
            if (args.length < 1) {
                System.out.println("Veuillez specifier un fichier .scxml en argument");
                return;
            }
            String output = "out.js";
            if (args.length >= 2){
                output = args[1];
            }
            SCXML2JS scxml2js = new SCXML2JS(args[0],output);
            scxml2js.convert();
            System.out.println("-> "+output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
