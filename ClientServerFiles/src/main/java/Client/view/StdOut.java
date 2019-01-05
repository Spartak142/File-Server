package Client.view;

public class StdOut {

    // Prints the specified output to System.out
    synchronized void print(String output) {
        System.out.print(output);
    }

    synchronized void println(String output) {
        System.out.println(output);
    }
}
