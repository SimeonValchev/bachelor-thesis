import java.util.Arrays;

public class TRS extends VarsCAP {

    Rule[] rules;
    char[] vars;

    public TRS(Rule[] rules, char[] vars) {
        this.rules = rules;
        this.vars = vars;
    }

    //  THE _ENCODING_ IN ALL ITS GLORY AND MIGHT!
    //MAY IT HAVE THE POWER TO CARRY ITS WRITER TO THE FINISH LINE
    //                 ✝ AMEN ✝
    public TRS ENCODING(){
        Location[] ENC = ENC_R();
        Nest[] NST = NST_R();
        for (Rule rule : this.getRules()) {
            if(rule == null){
                continue;
            }
            rule.left = phi(rule.left, new Location(rule, true, "eps"), false, ENC, NST);
            rule.right = phi(rule.right, new Location(rule, false, "eps"), false, ENC, NST);
        }
        return this;
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

            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), true, ENC, NST);
            }
            //ADD m - MANY i-s
            Term result = new Term('i',1,new Term[]{input});
            for (int i = 0; i < m - 1; i++) {
                result =  new Term('i',1,new Term[]{result});
            }
            return result;

        }else{

            input.setEncoded(true);
            for (int i = 1; i < input.arrity + 1; i++) {

                if(loc.position.equals("eps")){
                    temp_position = String.valueOf(i);
                }else{
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i-1] = phi(input.subterms[i-1], new Location(loc.getAlpha(), loc.left, temp_position), true, ENC, NST);
            }
            return new Term('i',1,new Term[]{input});

        }

        return input;
    }

    //E N C
    public Location[] ENC_R(){
        Location[] result = new Location[loc_R_num()];
        Location[] source = intersection(theX(), theY(defRHS()));
        Location[] varLHS = varLHS();

        int counter = 0;

        for (Location loc : source) {
            if(loc == null){
                continue;
            }
            if(!inLocSet(loc, varLHS)){
                result[counter] = loc;
                counter++;
            }
        }
        Location[] actual_result = new Location[counter];
        for (int i = 0; i < counter; i++) {
            actual_result[i] = result[i];
        }
        return actual_result;
    }

    //N S T
    public Nest[] NST_R(){
        Nest[] source = theALEPH();
        Nest[] result = new Nest[loc_R_num()];
        int counter = 0;

        //ALEPH \ INF
        for (int i = 0; i < source.length; i++) {
            if(source[i] == null){
                continue;
            }
            for (Location loc : INF_R()) {
                if(loc == null){
                    continue;
                }
                if(source[i].getLoc().getAlpha().equals(loc.getAlpha()) && source[i].getLoc().left == loc.left &&
                        source[i].getLoc().position.equals(loc.position)){
                    source[i] = null;
                    break;
                }
            }
        }

        //COPY ALEPH TO RES
        for (int i = 0; i < result.length; i++) {
            if(source[i] == null){
                continue;
            }
            result[i] = source[i];
            counter++;
        }

        for (Nest nest: source) {
            if(nest == null){
                continue;
            }
            Location[] temp = theY(new Location[]{nest.getLoc()});
            for (Location lambda : temp) {
                if(lambda == null){
                    continue;
                }
                int temp_counter = 0;
                boolean key = false;

                //CHECK IF LOCATION IS ALREADY RECEIVING A NEST
                for (int i = 0; i < result.length; i++) {
                    if(result[i] == null){
                        continue;
                    }
                    if(lambda.equals(result[i].getLoc())){
                        key = true;
                        temp_counter = i;
                        break;
                    }
                }

                if(key){
                    int maxNest = Math.max(nest.getSize(), result[temp_counter].getSize());
                    result[temp_counter] = new Nest(lambda, maxNest);
                }else{
                    result[counter] = new Nest(lambda, nest.getSize());
                    counter++;
                }
            }
        }
        return result;
    }

    //I N F
    public Location[] INF_R(){
        Location[] source = loc_R();
        Location[] result = new Location[loc_R_num()];
        int counter = 0;

        for (Location loc : source) {
            if(loc == null){
                continue;
            }

            Term temp = loc.left ? loc.getAlpha().left : loc.getAlpha().right;

            if(inCharSet(temp.subTermAt(loc.position).getSymbol(), sigmaD()) && nonNullElements(intersection(theY(new Location[]{loc}), subLocations(loc))) >= 2){
                result[counter] = loc;
                counter++;
            }

            /*
            if(loc.left && inCharSet(loc.getAlpha().left.subTermAt(loc.position).getSymbol(), sigmaD())){
                if(nonNullElements(intersection(theY(new Location[]{loc}), subLocations(loc))) >= 2){
                    result[counter] = loc;
                    counter++;
                }
            }else if(!loc.left && inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), sigmaD())){
                if (nonNullElements(intersection(theY(new Location[]{loc}), subLocations(loc))) >= 2) {
                    result[counter] = loc;
                    counter++;
                }
            }
             */
        }
        return result;
    }

    //THE X
    public Location[] theX(){
        Location[] result = new Location[loc_R_num()];
        Location[] source = loc_R();
        int counter_res = 0;
        boolean location_added = false;
        boolean made_change = false;

        //BASIS
        for (Location loc : source) {
            //NESTED DEF on LHS
            if(!loc.getPosition().equals("eps") &&
                    loc.left && Arrays.asList(loc.getAlpha().left.positions(true)).contains(loc.getPosition()) &&
                    inCharSet(loc.getAlpha().left.subTermAt(loc.getPosition()).getSymbol(), sigmaD())){
                result[counter_res] = loc;
                counter_res++;
            }

            //DUP-ED VARS on RHS
            if(!loc.left && inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), vars)){
                for (String posi :
                        loc.getAlpha().right.positions(false)) {
                    if (!loc.position.equals(posi) &&
                            loc.getAlpha().right.subTermAt(posi).getSymbol() == loc.getAlpha().right.subTermAt(loc.position).getSymbol()){
                        result[counter_res] = loc;
                        counter_res++;
                        break;
                    }
                }
            }

        }


        do{
            made_change = false;
            for (Location loc : source) {
                location_added = false;
                if(inLocSet(loc,result)){
                    continue;
                }

                //L2R VAR-TO-VAR
                if(loc.left && inCharSet(loc.getAlpha().left.subTermAt(loc.position).getSymbol(), vars)){
                    for (String posi :
                            loc.getAlpha().right.positions(true)){
                        if(inLocSet(new Location(loc.getAlpha(), !loc.left, posi), result) &&
                                loc.getAlpha().right.subTermAt(posi).getSymbol() == loc.getAlpha().left.subTermAt(loc.position).getSymbol()){
                            result[counter_res] = loc;
                            counter_res++;
                            made_change = true;
                            location_added = true;
                            break;
                        }
                    }
                }

                //DATA FLOWs
                if(!loc.left){
                    //R2L
                    String pi = "eps";
                    for (Rule rule : rules) {
                        if(rule == null){
                            continue;
                        }
                        if(!loc.position.equals("eps") && MGU(CAP(loc.getAlpha().right.subTermAt(pi)), rule.getLeft())){
                            for (String omega :
                                    rule.getLeft().positions(true)) {
                                if(n_PAR(loc.position, omega) &&
                                        inLocSet(new Location(rule, true, omega), result)){
                                    result[counter_res] = loc;
                                    counter_res++;
                                    made_change = true;
                                    location_added = true;
                                    break;
                                }
                            }
                        }
                        if(location_added){
                            break;
                        }
                    }
                    if(location_added){
                        continue;
                    }

                    //COPY FOR PI > EPS
                    //notes - - - added " - 1" to condition
                    for (int i = 0; i < loc.position.length() - 1 &&
                            loc.position.length() > 2 &&
                            !loc.position.equals("eps"); i += 2) {

                        pi = loc.position.substring(0,i+1);
                        //COPIED PART
                        for (Rule rule : rules) {
                            if(rule == null){
                                continue;
                            }
                            if(MGU(CAP(loc.getAlpha().right.subTermAt(pi)), rule.getLeft())){
                                for (String omega :
                                        rule.getLeft().positions(true)) {
                                    if(n_PAR(loc.position.substring(i+2), omega) &&
                                            inLocSet(new Location(rule, true, omega), result)){
                                        result[counter_res] = loc;
                                        counter_res++;
                                        made_change = true;
                                        location_added = true;
                                        break;
                                    }
                                }
                            }
                            if(location_added){
                                break;
                            }
                        }
                        if(location_added){
                            break;
                        }
                    }

                    //R2R
                    for (Rule rule : rules) {
                        if(rule == null){
                            continue;
                        }
                        for (String upsilon : rule.right.positions(true)) {
                            if(inCharSet(rule.right.subTermAt(upsilon).getSymbol(), sigmaD()) &&
                                    MGU(CAP(rule.right.subTermAt(upsilon)), loc.getAlpha().left) &&
                                    inLocSet(new Location(rule, false, upsilon), result)){
                                result[counter_res] = loc;
                                counter_res++;
                                made_change = true;
                                location_added = true;
                                break;
                            }
                            if(location_added){
                                break;
                            }
                        }
                        if(location_added){
                            break;
                        }
                    }
                }
            }
        }while(made_change);

        return result;
    }

    //THE Y_DELTA
    public Location[] theY(Location[] defRHS){
        Location[] result = new Location[loc_R_num()];
        int counter_res = 0;

        //DELTA
        for (Location loc: defRHS) {
            if(loc == null){
                continue;
            }
            result[counter_res] = loc;
            counter_res++;
        }

        boolean made_change = false;
        do {
            made_change = false;

            for (Location loc : loc_R()) {
                if(inLocSet(loc, result)){
                    continue;
                }
                boolean location_added = false;

                if (loc.left && !loc.position.equals("eps")) {
                    //R2L
                    for (Rule rule : rules) {
                        if(rule == null){
                            continue;
                        }
                        String pi = "eps";
                        if (MGU(CAP(rule.right.subTermAt(pi)), loc.getAlpha().getLeft())) {
                            for (String tau :
                                    rule.getRight().positions(false)) {
                                if (n_PAR(tau, loc.position) &&
                                        inLocSet(new Location(rule, false, tau), result)) {
                                    result[counter_res] = loc;
                                    counter_res++;
                                    location_added = true;
                                    made_change = true;
                                    break;
                                }
                            }
                            if(location_added){
                                break;
                            }
                        }

                        for (String tau :
                                rule.getRight().positions(false)) {
                            for (int i = 0; i < tau.length() - 1 &&
                                    tau.length() > 2; i += 2) {
                                pi = tau.substring(0,i+1);
                                if(MGU(CAP(rule.right.subTermAt(pi)), loc.getAlpha().left) &&
                                        n_PAR(tau.substring(i+2),loc.position) &&
                                        // changed pi -> tau in next line
                                        inLocSet(new Location( rule, false, tau), result)){
                                    result[counter_res] = loc;
                                    counter_res++;
                                    location_added = true;
                                    made_change = true;
                                    break;
                                }
                            }
                            if(location_added){
                                break;
                            }
                        }
                        if (location_added) {
                            break;
                        }
                    }
                }
                else{
                    //L2R
                    char char_in_question = loc.getAlpha().right.subTermAt(loc.position).getSymbol();
                    if (inCharSet(char_in_question, vars)) {
                        for (String pi :
                                loc.getAlpha().left.positions(false)) {
                            if (loc.getAlpha().left.subTermAt(pi).getSymbol() == char_in_question &&
                                    inLocSet(new Location(loc.getAlpha(), true, pi), result)) {
                                result[counter_res] = loc;
                                counter_res++;
                                location_added = true;
                                made_change = true;
                                break;
                            }
                        }
                        if (location_added) {
                            break;
                        }
                    }
                }

            }
        }while(made_change);

        return result;
    }

    //THE ALEPH
    public Nest[] theALEPH(){
        Location[] source = loc_R();
        Nest[] result = new Nest[loc_R_num()];
        int counter = 0;

        for (Location loc : source) {
            if(!loc.position.equals("eps") && !loc.left &&
                    inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), sigmaD())){
                int temp = loc.getAlpha().right.subTermAt(loc.position).nestSize(sigmaD(),vars);
                if(temp >= 2){
                    result[counter] = new Nest(loc, temp);
                    counter++;
                }
            }
        }
        return result;
    }

    //DEFINED SYMBOLS ON RHS
    public Location[] defRHS(){
        Location[] result = new Location[loc_R_num()];
        Location[] source = loc_R();
        int counter_res = 0;

        for (Location loc : source) {
            if(!loc.left && inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), sigmaD())){
                result[counter_res] = loc;
                counter_res++;
            }
        }
        return result;
    }

    //VAR LOCATIONS ON LHS
    public Location[] varLHS(){
        Location[] result = new Location[loc_R_num()];
        Location[] source = loc_R();
        int counter_res = 0;

        for (Location loc : source) {
            if(loc.left && inCharSet(loc.getAlpha().left.subTermAt(loc.position).getSymbol(), vars)){
                result[counter_res] = loc;
                counter_res++;
            }
        }
        return result;
    }

    //INTERSECTION
    public Location[] intersection(Location[] set_1, Location[] set_2){
        Location[] result = new Location[Math.min(set_1.length, set_2.length)];
        int counter = 0;

        for (Location loc : set_1) {
            if(loc == null){
                continue;
            }
            if(inLocSet(loc,set_2)){
                result[counter] = loc;
                counter++;
            }
        }
        return result;
    }

    public int nonNullElements(Location[] source){
        int result = 0;
        for (Location loc :
                source) {
            if(loc != null){
                result++;
            }
        }
        return result;
    }

    //NESTING SIZE
    public int nestSize(Location loc){
       if(loc.left){
           return loc.getAlpha().left.subTermAt(loc.position).nestSize(sigmaD(), vars);
       }else{
           return loc.getAlpha().right.subTermAt((loc.position)).nestSize(sigmaD(), vars);
       }
    }

    //SUB-LOCATIONS
    public Location[] subLocations(Location loc){
        Location[] result = new Location[loc.left ? loc.getAlpha().left.posNumber(true) : loc.getAlpha().right.posNumber(true)];
        int counter = 0;

        for (Location lambda : loc_R()) {
            if(lambda == null){
                continue;
            }
            if(loc.getAlpha().equals(lambda.getAlpha()) && loc.left == lambda.left && LEQ(loc.position, lambda.position)){
                result[counter] = lambda;
                counter++;
            }
        }
        return result;
    }

    //MGU EX. CHECKER
    public boolean MGU(Term s, Term t){
        boolean res = true;

        /*
        if(inCharSet(s.getSymbol(),vars) || inCharSet(t.getSymbol(),vars)){
            return true;
        }
        */
        if(Character.isUpperCase(s.getSymbol()) || Character.isUpperCase(t.getSymbol())){
            return true;
        }

        if(s.getSymbol() == t.getSymbol() && s.getArrity() == t.getArrity()){
            for (int i = 0; i < s.getArrity(); i++) {
                res = res && MGU(s.getSubterms()[i],t.getSubterms()[i]);
            }
        }else{
            return false;
        }
        return res;
    }

    //CAP-function
    public Term CAP(Term s){
        Term res = new Term(s.getSymbol(),s.getArrity(),s.getSubterms());

        for (int i = 0; i < res.getArrity(); i++) {
            if(inCharSet(res.subterms[i].getSymbol(), sigmaD())){
                char capVar = pick();
                res.subterms[i] = new Term(capVar);
            }else if(!inCharSet(res.subterms[i].getSymbol(), vars)){
                res.subterms[i] = CAP(res.subterms[i]);
            }
        }
        return res;
    }

    //CARDINALITY OF ALL LOCATIONS
    public int loc_R_num(){
        int loc_num = 0;
        for (Rule rule : rules) {
            if(rule == null){
                continue;
            }
            loc_num += rule.getLeft().posNumber(true) + rule.getRight().posNumber(true);
        }
        return loc_num;
    }

    //ALL LOCATIONS
    public Location[] loc_R(){
        int loc_num = loc_R_num();

        Location[] res = new Location[loc_num];
        int counter = 0;

        for (Rule rule : rules) {
            if(rule == null){
                continue;
            }
            for (String pos: rule.getLeft().positions(true)){
                res[counter] = new Location(rule, true, pos);
                counter++;
            }
            for (String pos: rule.getRight().positions(true)){
                res[counter] = new Location(rule, false, pos);
                counter++;
            }
        }

        return res;
    }

    //WRITER
    public void write(){
        for (Rule r :
                rules) {
            if(r == null){
                continue;
            }
            System.out.println(r.write());
        }
        System.out.print("VARS: ");
        for (char ch :
                vars) {
            if(ch == 0){
                continue;
            }
            System.out.print(ch + ", ");
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

    //DEFINED SYMBOLS
    public char[] sigmaD(){
        char[] res = new char[rules.length];
        for (int i = 0; i < rules.length; i++) {
            if(rules[i] == null){
                continue;
            }
            res[i] = rules[i].getLeft().getSymbol();
        }
        return res;
    }

    public boolean LEQ(String pi, String tau){
        return (pi.equals("eps")) ||
                (pi.length() <= tau.length() && pi.equals(tau.substring(0,pi.length())));
    }

    public boolean n_PAR(String pi, String tau){
        return LEQ(pi,tau) || LEQ(tau,pi);
    }

    //GETTERS AND SETTERS
    public Rule[] getRules() {
        return rules;
    }

    public void setRules(Rule[] rules) {
        this.rules = rules;
    }

    public char[] getVars() {
        return vars;
    }

    public void setVars(char[] vars) {
        this.vars = vars;
    }
}
