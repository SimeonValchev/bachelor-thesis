import java.util.Arrays;
import java.util.Collections;

public class Term{

    boolean encoded;
    char symbol;
    int arrity;
    Term[] subterms = new Term[arrity];

    public Term(char symbol, int arrity, Term[] subterms) {
        this.symbol = symbol;
        this.arrity = arrity;
        this.subterms = new Term[arrity];

        for (int i = 0; i < arrity; i++) {
            this.subterms[i] = new Term(subterms[i].encoded, subterms[i].getSymbol(),subterms[i].getArrity(),subterms[i].getSubterms());
        }
    }

    public Term(boolean encoded, char symbol, int arrity, Term[] subterms) {
        this.encoded = encoded;
        this.symbol = symbol;
        this.arrity = arrity;
        this.subterms = new Term[arrity];

        for (int i = 0; i < arrity; i++) {
            this.subterms[i] = new Term(subterms[i].encoded, subterms[i].getSymbol(),subterms[i].getArrity(),subterms[i].getSubterms());
        }
    }

    public Term(char symbol){
        this.symbol = symbol;
    }

    public Term phi(Term input, Location loc, boolean nestedTag, Location[] ENC, Nest[] NST){
        String temp_position = "";
        int m = loc.getSizeFromSet(NST);

        if(!inLocSet(loc, ENC)){

            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), nestedTag, ENC, NST);
            }

        }else if(loc.left){

            input.setEncoded(true);
            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), nestedTag, ENC, NST);
            }

        }else if(nestedTag){

            input.setEncoded(true);
            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), nestedTag, ENC, NST);
            }

        }else if(m > 1){

            input.setEncoded(true);
            //ADD m - MANY i-s
            Term result = new Term('i',1,new Term[]{input});
            for (int i = 0; i < m - 1; i++) {
                result =  new Term('i',1,new Term[]{result});
            }

            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), true, ENC, NST);
            }
            return result;

        }else{

            input.setEncoded(true);
            Term el_input = new Term(true, input.getSymbol(), input.getArrity(), input.getSubterms());
            Term result = new Term('i',1,new Term[]{el_input});
            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), nestedTag, ENC, NST);
            }
            return result;

        }

        return input;
    }

    public boolean inLocSet(Location loc, Location[] locations){
        for (Location lambda : locations) {
            if(lambda == null){
                continue;
            }
            if(loc.alpha.equals(lambda.alpha) &&
                    loc.left == lambda.left &&
                    loc.position.equals(lambda.position)){
                return true;
            }
        }
        return false;
    }

    public String write(){
        String res = "";
        int counter = getArrity() - 1;

        if(getArrity() != 0){
            if(encoded){
                res += "l_";
            }

            res += getSymbol() + "(";
            for (Term t : getSubterms()) {
                res += t.write();
                if(counter > 0){
                    res += ", ";
                    counter--;
                }
            }

        }else{
            if(encoded && !Character.isUpperCase(getSymbol())){
                res += "l_";
            }
            res += getSymbol();
            return res;
        }

        res += ")";
        return res;
    }

    public Term stringToTerm(String input){
        Term[] array = new Term[input.length()];
        int array_counter = 0;
        char symbol = input.charAt(0);

        if(input.length() == 1){
            return new Term(symbol);
        }

        int braket_counter = 0;
        int arrity = 1;

        //ARRITY CALCULATOR
        for (int i = 2; i < input.length(); i++) {
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

    public int nestSize(char[] sigma_d, char[] vars){
        if (!inCharSet(symbol, sigma_d) && arrity == 0) {
            return 0;
        }else if(inCharSet(symbol,sigma_d)){
            if(arrity == 0){
                return 1;
            }
            Integer[] temp = new Integer[arrity];
            for (int i = 0; i < arrity; i++) {
                temp[i] = subterms[i].nestSize(sigma_d,vars);
            }
            return 1 + Collections.max(Arrays.asList(temp));
        }else{
            Integer[] temp = new Integer[arrity];
            for (int i = 0; i < arrity; i++) {
                temp[i] = subterms[i].nestSize(sigma_d,vars);
            }
            return Collections.max(Arrays.asList(temp));
        }
    }

    public int posNumber(boolean eps){
        int res = getArrity();

        for (Term t :
                getSubterms()) {
            res += t.posNumber(false);
        }
        if(eps){
            return res + 1;
        }
        return res;
    }

    public String[] positions(boolean eps){
        //changed true -> eps
        String[] res = new String[posNumber(eps)];
        int counter = 0;
        int arrity = getArrity();

        for (int i = 1; i < arrity + 1; i++) {
            res[i-1] = String.valueOf(i);
            Term t = getSubterms()[i-1];
            if(t.getArrity() > 0){
                String[] positionsOfT = t.positions(false);
                for (int j = 0; j < t.posNumber(false); j++) {
                    res[arrity + counter] = res[i-1] + "." + positionsOfT[j];
                    counter++;
                }
            }

        }
        if(eps){
            res[res.length-1] = "eps";
        }
        return res;
    }

    public Term subTermAt(String position){
        Term res = this;
        if(position.equals("eps")){
            return res;
        }

        for (int i = 0; i < position.length(); i += 2) {
            int pos = Integer.parseInt(position.substring(i,i+1));
            res = res.getSubterms()[pos - 1];
        }

        return res;
    }

    public void replaceAtWith(String position, Term t){
        Term temp = new Term(t.getSymbol(),t.getArrity(),t.getSubterms());
        if(position.equals("eps")){
            this.setSymbol(temp.symbol);
            this.setSubterms(temp.subterms);
            this.setArrity(temp.arrity);
        }else if(position.length() == 1){
            this.setSubterm(Integer.parseInt(position.substring(0,1)),temp);
        }else{
            this.subterms[Integer.parseInt(position.substring(0,1)) - 1].replaceAtWith(position.substring(2),temp);
        }
    }

    //ELEMENT IN SET CHECKERS
    public boolean inCharSet(char symbol, char[] set){
        if(set == null){
            return false;
        }
        for (char ch : set) {
            if(ch == symbol){
                return true;
            }
        }
        return false;
    }

    //GETTERS
    public char getSymbol() {
        return symbol;
    }

    public int getArrity() {
        return arrity;
    }

    public Term[] getSubterms() {
        return subterms;
    }

    //SETTERS
    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public void setArrity(int arrity) {
        this.arrity = arrity;
    }

    //ALL SUBTERMS
    public void setSubterms(Term[] subterms) {
        this.subterms = subterms;
    }

    //INDIVIDUAL SUBTERM
    public void setSubterm(int index, Term subterm) {
        this.subterms[index - 1] = subterm;
    }

    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }
}
