import java.io.*;
import java.util.*;

public class TypeMap extends HashMap<Variable, Type> {
    PrintWriter pw;

    void display(String path) throws IOException {
        String Realpath = path.substring(0, path.length()-4);

        pw = new PrintWriter(new BufferedWriter(new FileWriter(Realpath + ".output", true)));
        pw.println("\nBegin type checking...programs/"+ path + "\n");
        pw.println("Type map:");
        pw.print("{ ");
        for(Entry<Variable, Type> entry : this.entrySet()){
            Variable variable = entry.getKey();
            Type type = entry.getValue();

            pw.print("<");
            pw.print(variable+", "+type+">");
            pw.print(", ");
        }
        pw.println("}\n");
        pw.close();
    }
// TypeMap is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.

}
