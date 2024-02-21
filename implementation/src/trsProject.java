import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

public class trsProject {

    public static void main(String[] args) throws IOException {

        BufferedReader reader;
        Rule[] rules = new Rule[20];
        int counter_r = 0;
        char[] vars = new char[10];
        int counter_ch = 0;

        boolean key = false;
        boolean key_2 = false;

        Scanner scanner = new Scanner(System.in);
        String example = "";

        System.out.println("Type a or b for:\na: Source file path OR\nb: Choose an example 6-10");
        String temp_scanner = scanner.nextLine();
        if(temp_scanner.equals("a")){

        }else {
            System.out.println("Which one?");
            temp_scanner = scanner.nextLine();
            switch (Integer.parseInt(temp_scanner)) {
                case 6 -> {
                    example = "h(X) -> f(w(g(a(X))),X)\nf(X,s(Y)) -> c(X,f(X,Y))\na(X) -> b(X)\ng(a(X)) -> q(X)\ng(b(X)) -> n(X)\nw(X) -> d\nq(s(X)) -> e(n(X),q(X))\nn(s(X)) -> n(X)\nvars\nX\nY";
                    key_2 = true;
                }
                case 7 -> {
                    example = "h(X) -> f(g(X),X)\nf(X,s(Y)) -> c(X,f(X,Y))\ng(s(X)) -> g(X)\nvars\nX\nY";
                    key_2 = true;
                }
                case 8 -> {
                    example = "g -> f(a)\na -> b\nf(a) -> f(a)";
                    key_2 = true;
                }
                case 9 -> {
                    example = "h(X) -> f(f(g(X),X),X)\nf(X,s(Y)) -> c(X,f(X,Y))\ng(s(X)) -> g(X)\nvars\nX\nY";
                    key_2 = true;
                }
                case 10 -> {
                    example = "h(X) -> f(X,0,X)\nf(X,Y,s(Z)) -> f(X,w(g(X,Y)),Z)\nw(X) -> d(X,X)\ng(X,Y) -> n(X)\nn(s(X)) -> n(X)\nvars\nX\nY\nZ";
                    key_2 = true;
                }
                default -> {
                    System.out.println("Example not found RIP");
                    example = "";
                    key_2 =true;
                }
            }
        }

        try {

            reader = key_2 ? new BufferedReader(new StringReader(example))
                    //SOURCE PATH OF FILE GOES HERE------------v
                    : new BufferedReader(new FileReader("E:\\stuff\\Programming\\Java\\TRS\\src\\sample.txt"));

            String line = reader.readLine();

            while (line != null) {

                if(line.equals("vars")){
                    key = true;
                    line = reader.readLine();
                    continue;
                }

                if(!key) {
                    rules[counter_r] = new Rule(stringToTerm(line.substring(0, line.indexOf('-') - 1)), stringToTerm(line.substring(line.indexOf('>') + 2)));
                    counter_r++;
                }else{
                    vars[counter_ch] = line.charAt(0);
                    counter_ch++;
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--- INTERPRETED INPUT ---");
        TRS trs_R = new TRS(rules, vars);
        trs_R.write();
        System.out.println("");
        System.out.println("");

        Location[] infinity = trs_R.INF_R();
        System.out.println("--- NON-TERMINATING RULES FOR SYMBOLS ---");
        for (Location loc : infinity) {
            if(loc == null){
                continue;
            }
            System.out.println(loc.symbolAtLoc() + "  in rule " + loc.getAlpha().write() + "  on the " + (loc.left ? "LHS at position " : "RHS at position ") + loc.getPosition());
        }

        System.out.println("");
        System.out.println("--- ENCODING ---");
        TRS trs_R1 = trs_R.ENCODING();
        trs_R1.write();
        System.out.println("");


    }

    public static Term stringToTerm(String input){
        Term[] array = new Term[input.length()];
        int array_counter = 0;
        char symbol = input.charAt(0);

        if(input.length() == 1){
            return new Term(symbol);
        }

        int braket_counter = 0;
        int arrity = 1;

        //ARRITY CALCULATOR
        for (int i = 1; i < input.length(); i++) {
            if(input.charAt(i) == '('){
                braket_counter++;
            } else if (input.charAt(i) == ')') {
                braket_counter--;
            } else if (input.charAt(i) == ',' && braket_counter == 1) {
                arrity++;
            }
        }

        if(arrity == 1){
            return new Term(symbol, 1, new Term[]{stringToTerm(input.substring(2,input.length() - 1))});
        }

        braket_counter = 0;

        boolean key = true;
        int tempIndex = 0;
        for (int i = 1; i < input.length(); i++) {
            if(input.charAt(i) == '('){
                if(key){
                    tempIndex = i+1;
                    key = false;
                }
                braket_counter++;

            } else if(input.charAt(i) == ')'){
                if(i == input.length() - 1){
                    array[array_counter] = stringToTerm(input.substring(tempIndex,i));
                    array_counter++;
                }
                braket_counter--;

            } else if(input.charAt(i) == ','){
                if(key){
                    tempIndex = i+1;
                    key = false;
                }else if(braket_counter == 1){
                    array[array_counter] = stringToTerm(input.substring(tempIndex,i));
                    array_counter++;
                    tempIndex = i + 1;
                }
            }
        }
        Term[] result = new Term[arrity];

        for (int i = 0; i < arrity; i++) {
            result[i] = array[i];
        }
        return new Term(symbol, arrity, result);

    }

    public static boolean isNotCorrect(String term){
        int temp = 0;
        for (int i = 0; i < term.length(); i++) {
            if(term.charAt(i) == '('){
                temp++;
            }else if(term.charAt(i) == ')'){
                temp--;
            }
        }
        return temp != 0;
    }

}




