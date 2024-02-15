import java.util.Scanner;

public class trsProject {

    public static void main(String[] args) {

        //EXAMPLE 7
        /*
        //VARS
        Term x = new Term('X');
        Term y = new Term('Y');

        //h(x)
        Term h_x = new Term('h',1, new Term[]{x});
        //s(y)
        Term s_y = new Term('s',1, new Term[]{y});
        Term s_x = new Term('s',1, new Term[]{x});
        //f(x,s(y))
        Term f_x_sy = new Term('f',2, new Term[]{x,s_y});
        Term f_x_y = new Term('f',2, new Term[]{x,y});
        //g_x
        Term g_x = new Term('g',1,new Term[]{x});
        //g(s(x))
        Term g_sx = new Term('g',1, new Term[]{s_x});
        //f(g(x),x)
        Term f_gx_x = new Term('f',2, new Term[]{g_x,x});
        //c(x, f(x,y))
        Term c_x_fxy = new Term('c',2, new Term[]{x,f_x_y});


        //RULES
        Rule alpha1 = new Rule(h_x,f_gx_x);
        Rule alpha2 = new Rule(f_x_sy,c_x_fxy);
        Rule alpha3 = new Rule(g_sx,g_x);

        //TRS EX_7
        TRS example_7 = new TRS(new Rule[]{alpha1,alpha2,alpha3}, new char[]{'X','Y'});
        */

        //EXAMPLE 8
        /*
        //g
        Term g = new Term('g');
        //a
        Term a = new Term('a');
        //f(a)
        Term f_a = new Term('f',1,new Term[]{a});
        //b
        Term b = new Term('b');

        //RULES
        Rule alpha1 = new Rule(g,f_a);
        Rule alpha2 = new Rule(a,b);
        Rule alpha3 = new Rule(f_a,f_a);

        //TRS EX_8
        TRS example_8 = new TRS(new Rule[]{alpha1,alpha2,alpha3}, null);
        */

        //EXAMPLE 9
        /*
        //VARS
        Term x = new Term('X');
        Term y = new Term('Y');

        //h(x)
        Term h_x = new Term('h',1, new Term[]{x});
        //s(y)
        Term s_y = new Term('s',1, new Term[]{y});
        Term s_x = new Term('s',1, new Term[]{x});
        //f(x,s(y))
        Term f_x_sy = new Term('f',2, new Term[]{x,s_y});
        Term f_x_y = new Term('f',2, new Term[]{x,y});
        //g_x
        Term g_x = new Term('g',1,new Term[]{x});
        //g(s(x))
        Term g_sx = new Term('g',1, new Term[]{s_x});
        //f(g(x),x)
        Term f_gx_x = new Term('f',2, new Term[]{g_x,x});
        //f( f(g(x),x), x)
        Term f_f_gx_x_x = new Term('f',2, new Term[]{f_gx_x,x});
        //c(x, f(x,y))
        Term c_x_fxy = new Term('c',2, new Term[]{x,f_x_y});


        //RULES
        Rule alpha1 = new Rule(h_x,f_f_gx_x_x);
        Rule alpha2 = new Rule(f_x_sy,c_x_fxy);
        Rule alpha3 = new Rule(g_sx,g_x);

        //TRS EX_9
        TRS example_9 = new TRS(new Rule[]{alpha1,alpha2,alpha3}, new char[]{'X','Y'});

         */

        //EXAMPLE 10
        /*
        //VARS
        Term x = new Term('X');
        Term y = new Term('Y');
        Term z = new Term('Z');

        Term zero = new Term('0');

        //h(x)
        Term h_x = new Term('h',1, new Term[]{x});
        //s(x)
        Term s_x = new Term('s',1, new Term[]{x});
        Term s_z = new Term('s',1, new Term[]{z});
        //f(x, 0, x)
        Term f_x_0_x = new Term('f',3, new Term[]{x, zero, x});
        //f(x, y, s(z))
        Term f_x_y_sz = new Term('f',3, new Term[]{x,y,s_z});
        //g_x_y
        Term g_x_y = new Term('g',2,new Term[]{x, y});
        //dbl(x)
        Term dbl_x = new Term('w',1, new Term[]{x});
        //dbl( g(x,y))
        Term dbl_g_xy = new Term('w',1, new Term[]{g_x_y});
        //d(x,x)
        Term d_x_x = new Term('d',2, new Term[]{x,x});
        //lin(x)
        Term lin_x = new Term('l',1, new Term[]{x});
        //lin(s(x))
        Term lin_s_x = new Term('l',1, new Term[]{s_x});
        //f(x, dbl( g(x,y)), z)
        Term f_x_dbl_g_xy_z = new Term('f',3,new Term[]{x, dbl_g_xy, z});

        //RULES
        Rule alpha1 = new Rule(h_x,f_x_0_x);
        Rule alpha2 = new Rule(f_x_y_sz, f_x_dbl_g_xy_z);
        Rule alpha3 = new Rule(dbl_x,d_x_x);
        Rule alpha4 = new Rule(g_x_y,lin_x);
        Rule alpha5 = new Rule(lin_s_x,lin_x);

        //TRS EX_10
        TRS example_10 = new TRS(new Rule[]{alpha1,alpha2,alpha3,alpha4,alpha5}, new char[]{'X','Y','Z'});
        */

        //EXECUTABLE
        /*
        System.out.println("------ ENC -----");
        for (Location lambda : example_10.ENC_R()) {
            if(lambda == null){
                continue;
            }
            System.out.println(lambda.write());
        }
        System.out.println("------ ALEPH -----");
        for (Nest lambda : example_10.theALEPH()) {
            if(lambda == null){
                continue;
            }
            System.out.println(lambda.write());
        }
        System.out.println("------ NST -----");
        for (Nest lambda : example_10.NST_R()) {
            if(lambda == null){
                continue;
            }
            System.out.println(lambda.write());
        }
        System.out.println("------ INF -----");
        for (Location lambda : example_10.INF_R()) {
            if(lambda == null){
                continue;
            }
            System.out.println(lambda.write());
        }
        System.out.println("----------- ENCODED TRS -----------");
        for (Rule lambda : example_10.ENCODING().getRules()) {
            if(lambda == null){
                continue;
            }
            System.out.println(lambda.write());
        }

         */

        /*
        Scanner input = new Scanner(System.in);
        Rule[] rules = new Rule[10];
        Term[] terms = new Term[100];
        char[] vars = new char[10];

        int rules_counter = 0;
        int vars_counter = 0;
        System.out.println("----- TRS -----");
        String r_input = "";
*/

        /* INPUT WORK IN PROGRESS
        while(true){

            r_input = input.nextLine();
            if(r_input.indexOf('>') != -1){
                String left = r_input.substring(0, r_input.indexOf('-') - 1);
                String right = r_input.substring(r_input.indexOf('>') + 2);
                if(isNotCorrect(left) || isNotCorrect(right)){
                    System.out.print(">>> Rule is not correctly written");
                    System.out.println("");
                    continue;
                }
                Rule rule = new Rule(stringToTerm(left), stringToTerm(right));
                rules[rules_counter] = rule;
                rules_counter++;
                System.out.println("-^- processed -^-");
            }else if(r_input.equals("vars")){
                while(true){
                    if(r_input.equals("end")){
                        break;
                    }
                    r_input = input.next();
                    vars[vars_counter] = r_input.charAt(0);
                    vars_counter++;
                }

            }else{
                System.out.println("Interpreted input:");
                for (Rule r : rules) {
                    if(r == null){
                        continue;
                    }
                    System.out.println(r.write());
                }
                System.out.print("Vars: ");
                for (char ch : vars) {
                    if (ch == 0) {
                        continue;
                    }
                    System.out.print(ch + ", ");
                }
                break;
            }
        }

         */



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

    public static boolean piLEQtau(String pi, String tau){
        return pi.length() <= tau.length() && pi.equals(tau.substring(0,pi.length()));
    }

    public static boolean lambaLEQkappa(Location lambda, Location kappa){
        return lambda.alpha.left.equals(kappa.alpha.left) && lambda.left == kappa.left && piLEQtau(lambda.position, kappa.position);
    }
}




