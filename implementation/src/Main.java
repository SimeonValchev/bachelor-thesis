import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {

        BufferedReader reader;
        Rule[] rules = new Rule[20];
        int counter_r = 0;
        char[] vars = new char[10];
        int counter_ch = 0;

        boolean key_2 = false;

        Scanner scanner = new Scanner(System.in);
        String example = "";

        System.out.println("Type a or b for:\na: TRS from sample.txt OR\nb: Choose an example 6-10");
        String temp_scanner = scanner.nextLine();
        while(!(temp_scanner.equals("a") || temp_scanner.equals("b"))){
            System.out.println("Invalid input! (a/b)");
            temp_scanner = scanner.nextLine();
        }

        if(temp_scanner.equals("b")){
            System.out.println("Which one?");
            temp_scanner = scanner.nextLine();
            while(Integer.parseInt(temp_scanner) < 6 || Integer.parseInt(temp_scanner) > 10){
                System.out.println("Example not found, try again! (6 - 10)");
                temp_scanner = scanner.nextLine();
            }
            switch (Integer.parseInt(temp_scanner)) {
                case 6 -> {
                    example = """
                            (VAR X Y )
                            (RULES
                            h(X) -> f(w(g(a(X))),X)
                            f(X,s(Y)) -> c(X,f(X,Y))
                            a(X) -> b(X)
                            g(a(X)) -> q(X)
                            g(b(X)) -> n(X)
                            w(X) -> d
                            q(s(X)) -> e(n(X),q(X))
                            n(s(X)) -> n(X)
                            )""";
                    key_2 = true;
                }
                case 7 -> {
                    example = """
                            (VAR X Y )
                            (RULES
                            h(X) -> f(g(X),X)
                            f(X,s(Y)) -> c(X,f(X,Y))
                            g(s(X)) -> g(X)
                            )
                            """;
                    key_2 = true;
                }
                case 8 -> {
                    example = """
                            (VAR )
                            (RULES
                            g -> f(a)
                            a -> b
                            f(a) -> f(a)
                            )
                            """;
                    key_2 = true;
                }
                case 9 -> {
                    example = """
                            (VAR X Y )
                            (RULES
                            h(X) -> f(f(g(X),X),X)
                            f(X,s(Y)) -> c(X,f(X,Y))
                            g(s(X)) -> g(X)
                            )""";
                    key_2 = true;
                }
                case 10 -> {
                    example = """
                            (VAR X Y Z )
                            (RULES
                            h(X) -> f(X,0,X)
                            f(X,Y,s(Z)) -> f(X,w(g(X,Y)),Z)
                            w(X) -> d(X,X)
                            g(X,Y) -> n(X)
                            n(s(X)) -> n(X)
                            )""";
                    key_2 = true;
                }
                default -> {
                    System.out.println("RIP");
                    return;
                }
            }
        }

        System.out.println("Enable expanded encoding? (y/n)");
        temp_scanner = scanner.nextLine();
        while(!(temp_scanner.equals("y") || temp_scanner.equals("n"))){
            System.out.println("Invalid input! (y/n)");
            temp_scanner = scanner.nextLine();
        }

        boolean expandEncoding = false;
        if(temp_scanner.equals("y")){
            expandEncoding = true;
        }

        try {

            reader = key_2 ? new BufferedReader(new StringReader(example))
                    //SOURCE PATH OF FILE GOES HERE------------v
                    : new BufferedReader(new FileReader("src/sample.txt"));

            String line = reader.readLine();

            //INTERPRETER
            while (line != null) {

                if(line.equals("(RULES") || line.equals(")")){
                    line = reader.readLine();
                    continue;
                }

                if(line.startsWith("(VAR")){
                    int i = 5;
                    while(line.charAt(i) != ')'){
                        if(line.charAt(i) == ' '){
                            i++;
                            continue;
                        }
                        vars[counter_ch] = line.charAt(i);
                        counter_ch++;
                        i++;
                    }
                    line = reader.readLine();
                    continue;
                }

                rules[counter_r] = new Rule(stringToTerm(line.substring(0, line.indexOf('-') - 1)), stringToTerm(line.substring(line.indexOf('>') + 2)));
                counter_r++;

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--- PARSED INPUT ---");
        TRS trs_R = new TRS(rules, vars);
        trs_R.write(true, false, false, false);
        System.out.println();


        if(expandEncoding) {
            Location[] infinity = trs_R.INF_R_2();
            System.out.println("--- REPEATED NESTING FOUND for SYMBOLS ---");
            boolean found_any = false;
            for (Location loc : infinity) {
                if (loc == null) {
                    continue;
                }
                System.out.println(loc.symbolAtLoc() + "  in rule " + loc.getAlpha().write(false) + "  on the " + (loc.left ? "LHS at position " : "RHS at position ") + loc.getPosition());
                found_any = true;
            }
            if (!found_any) {
                System.out.println("None were found");
            }
        }

        System.out.println();
        System.out.println("--- ENCODING in WST FORMAT ---");

        TRS relative = trs_R.relativeTRS(expandEncoding);
        TRS trs_R1 = trs_R.ENCODING(expandEncoding);
        System.out.print("(VAR ");
        for (char ch :
                getUnion(relative.vars, trs_R.vars)) {
            if(ch == 0){
                continue;
            }
            System.out.print(ch + " ");
        }
        System.out.println(")");

        trs_R1.write(false, false, true, false);
        System.out.println();
        relative.write(false,true, false, true);

        System.out.println();


    }

    public static Character[] getUnion(char[] arr1, char[] arr2) {
        Set<Character> set = new HashSet<>();
        for (char c : arr1) {
            set.add(c);
        }
        for (char c : arr2) {
            set.add(c);
        }
        return set.toArray(new Character[0]);
    }

    //USED TO PARSE INPUT
    //DIFFERENT FROM THE ONE IN the TRS class
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

}




