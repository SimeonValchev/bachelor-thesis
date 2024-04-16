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
                    res += ",";
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
