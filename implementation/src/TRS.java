import java.util.Arrays;


public class TRS extends VarsCAP {

    Rule[] rules;
    char[] vars;

    //CONSTRUCTOR
    public TRS(Rule[] rules, char[] vars) {
        this.rules = rules;
        this.vars = vars;
    }

    public TRS ENCODING(boolean enableExpanded) {
        Location[] ENC = ENC_R();
        Nest[] NST = new Nest[1];

        if(enableExpanded){
            NST = NST_R_2();
        }

        for (Rule rule : this.getRules()) {
            if (rule == null) {
                continue;
            }
            rule.left = phi(rule.left, new Location(rule, true, "eps"), false, ENC, NST);
            rule.right = phi(rule.right, new Location(rule, false, "eps"), false, ENC, NST);
        }

        return this;
    }

    public Term phi(Term input, Location loc, boolean nestedTag, Location[] ENC, Nest[] NST) {
        String temp_position;
        int m = loc.getSizeFromSet(NST);

        if (!inLocSet(loc, ENC)) {

            for (int i = 1; i < input.arrity + 1; i++) {

                if (loc.position.equals("eps")) {
                    temp_position = String.valueOf(i);
                } else {
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i - 1] = phi(input.subterms[i - 1], new Location(loc.getAlpha(), loc.left, temp_position), nestedTag, ENC, NST);
            }

        } else if (loc.left) {

            input.setEncoded(true);
            for (int i = 1; i < input.arrity + 1; i++) {

                if (loc.position.equals("eps")) {
                    temp_position = String.valueOf(i);
                } else {
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i - 1] = phi(input.subterms[i - 1], new Location(loc.getAlpha(), loc.left, temp_position), nestedTag, ENC, NST);
            }

        } else if (nestedTag) {

            input.setEncoded(true);
            for (int i = 1; i < input.arrity + 1; i++) {

                if (loc.position.equals("eps")) {
                    temp_position = String.valueOf(i);
                } else {
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i - 1] = phi(input.subterms[i - 1], new Location(loc.getAlpha(), loc.left, temp_position), true, ENC, NST);
            }

        } else if (m > 1) {

            input.setEncoded(true);

            for (int i = 1; i < input.arrity + 1; i++) {

                if (loc.position.equals("eps")) {
                    temp_position = String.valueOf(i);
                } else {
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i - 1] = phi(input.subterms[i - 1], new Location(loc.getAlpha(), loc.left, temp_position), true, ENC, NST);
            }
            //ADD m - MANY i-s
            Term result = new Term('i', 1, new Term[]{input});
            for (int i = 0; i < m - 1; i++) {
                result = new Term('i', 1, new Term[]{result});
            }
            return result;

        } else {

            input.setEncoded(true);
            int potentialNest = loc.nextBiggestNest(NST);
            for (int i = 1; i < input.arrity + 1; i++) {

                if (loc.position.equals("eps")) {
                    temp_position = String.valueOf(i);
                } else {
                    temp_position = loc.position + "." + i;
                }
                input.subterms[i - 1] = phi(input.subterms[i - 1], new Location(loc.getAlpha(), loc.left, temp_position), true, ENC, NST);
            }

            //THE WAY IT WAS BEFORE
            //return new Term('i',1,new Term[]{input});
            Term result = new Term('i', 1, new Term[]{input});
            for (int i = 0; i < potentialNest; i++) {
                result = new Term('i', 1, new Term[]{result});
            }
            return result;

        }

        return input;
    }

    //E N C
    public Location[] ENC_R() {
        Location[] result = new Location[loc_R_num()];
        Location[] source = intersection(theX(), theY(defRHS()));
        Location[] varLHS = varLHS();

        int counter = 0;

        for (Location loc : source) {
            if (loc == null) {
                continue;
            }
            if (!inLocSet(loc, varLHS)) {
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

    //SECOND VERSIONS of INF and NST
    public Location[] INF_R_2(){
        Location[] source = loc_R();
        Location[] result = new Location[loc_R_num()];
        int counter = 0;
        boolean change_was_made = true;

        while(change_was_made){
            change_was_made = false;

            for (Location loc : source) {
                if(loc == null || inLocSet(loc,result)){
                    continue;
                }

                for (Location subloc : subLocations(loc)) {
                    //unsure of location equality
                    if(subloc == null || subloc.equals(loc) || inLocSet(loc,result)){
                        continue;
                    }
                    Location[] dataflow = theY(new Location[]{loc});
                    if((!equalLocations(subloc,loc) && inLocSet(subloc,dataflow)) || inLocSet(subloc, result)){
                        result[counter] = loc;
                        counter++;
                        change_was_made = true;
                    }
                }

                for (Location mu : result) {
                    if(inLocSet(loc, theY(new Location[]{mu})) && !inLocSet(loc,result)){
                        result[counter] = loc;
                        counter++;
                        change_was_made = true;
                    }
                }
            }
        }
        return result;
    }

    public boolean equalLocations(Location loc1, Location loc2){
        if(loc1 == null || loc2 == null){
            return false;
        }
        if(loc1.getAlpha().equals(loc2.getAlpha()) && loc1.getPosition().equals(loc2.getPosition()) && loc1.left == loc2.left){
            return true;
        }
        return false;
    }

    public Nest[] NST_R_2(){
        Location[] source = loc_R();
        Location[] INFsource = INF_R_2();
        Nest[] aleph = new Nest[loc_R_num()];
        boolean change_was_made = true;
        boolean change_was_made_aleph = true;

        //INIT
        for (int i = 0; i < source.length; i++) {
            aleph[i] = new Nest(source[i],inCharSet(source[i].symbolAtLoc(),sigmaD()) ? 1 : 0);
        }

        //estimate for array size, may not be enough idk
        Nest[] glattN = new Nest[6*loc_R_num()];
        int glattN_counter = 0;

        while(change_was_made_aleph) {
            change_was_made_aleph = false;

            //GLATT N RE-INIT
            glattN_counter = 0;
            for (Nest nest : glattN) {
                if (nest != null){
                    nest = null;
                }
            }

            //GLATT-N BASE
            for (Location loc : source) {
                if (loc == null || inLocSet(loc, INFsource)) {
                    continue;
                }
                int n = nest_depth(loc, aleph);
                glattN[glattN_counter] = new Nest(loc, n);
                glattN_counter++;
            }

            while (change_was_made) {
                change_was_made = false;

                //GLATT-N DATA-FLOWS
                for (Nest nest : glattN) {
                    if(nest == null){
                        continue;
                    }
                    Location loc = nest.getLoc();
                    int n = nest.getSize();

                    for (Location flow_to : theY(new Location[]{loc})) {
                        Nest tempNest = new Nest(flow_to, n);
                        if (flow_to == null || inNestSet(tempNest, glattN)) {
                            continue;
                        }
                        glattN[glattN_counter] = new Nest(flow_to, n);
                        glattN_counter++;
                        change_was_made = true;
                    }
                }
            }

            Location[] rememberer = new Location[glattN_counter];

            //ONLY MAX-SIZE NEST
            for (int i = 0; i < glattN_counter; i++) {
                if (glattN[i] == null) {
                    continue;
                }
                if (inLocSet(glattN[i].getLoc(), rememberer)) {
                    continue;
                }

                int tempMax = glattN[i].getSize();
                Location loc = glattN[i].getLoc();

                for (Nest nest : glattN) {
                    if(nest == null){
                        continue;
                    }
                    if (nest.getLoc().equals(loc) && nest.getSize() > tempMax) {
                        tempMax = nest.getSize();
                    }
                }
                Nest tempNest = new Nest(loc, tempMax);
                if(inNestSet(tempNest,aleph)){
                    continue;
                }

                boolean keyToChange = false;
                int aleph_counter = -1;
                for (int j = 0; j < aleph.length; j++) {
                    if(equalLocations(aleph[j].getLoc(),tempNest.getLoc()) && aleph[j].getSize() < tempMax){
                        aleph_counter = j;
                        keyToChange = true;
                    }
                }
                if(keyToChange) {
                    aleph[aleph_counter] = new Nest(glattN[i].getLoc(), tempMax);
                    rememberer[aleph_counter] = glattN[i].getLoc();
                    change_was_made_aleph = true;
                }
            }
        }
        return aleph;
    }

    public int nest_depth(Location lambda, Nest[] source){
        int sublocNum = lambda.left ? lambda.getAlpha().left.subTermAt(lambda.position).posNumber(true) : lambda.getAlpha().right.subTermAt(lambda.position).posNumber(true);
        int[] temp_values = new int[sublocNum];
        int counter = 0;
        int m = 0;
        for (Nest nest : source) {
            if(equalLocations(lambda,nest.getLoc())){
                m = nest.getSize();
            }
        }

        if(nonNullElements(subLocations(lambda)) == 1){
            return m;
        }else{
            for (Location mu : subLocations(lambda)) {
                if(mu == null){
                    continue;
                }
                //!mu.equals(lambda)
                if(!equalLocations(mu,lambda)) {
                    temp_values[counter] = nest_depth(mu, source);
                    counter++;
                }
            }
            int temp_max = temp_values[0];
            for (int i = 0; i < temp_values.length; i++) {
                if(temp_max < temp_values[i]){
                    temp_max = temp_values[i];
                }
            }

            return !inCharSet(lambda.symbolAtLoc(),sigmaD()) ? 1 + temp_max : temp_max;
        }
    }

    //N S T // not used
    public Nest[] NST_R() {
        Nest[] source = theALEPH();
        Nest[] result = new Nest[loc_R_num()];
        int counter = 0;

        //ALEPH \ INF
        for (int i = 0; i < source.length; i++) {
            if (source[i] == null) {
                continue;
            }
            for (Location loc : INF_R()) {
                if (loc == null) {
                    continue;
                }
                if (source[i].getLoc().getAlpha().equals(loc.getAlpha()) && source[i].getLoc().left == loc.left &&
                        source[i].getLoc().position.equals(loc.position)) {
                    source[i] = null;
                    break;
                }
            }
        }

        //COPY ALEPH TO RES
        for (int i = 0; i < result.length; i++) {
            if (source[i] == null) {
                continue;
            }
            result[i] = source[i];
            counter++;
        }

        for (Nest nest : source) {
            if (nest == null) {
                continue;
            }
            Location[] temp = theY(new Location[]{nest.getLoc()});
            for (Location lambda : temp) {
                if (lambda == null) {
                    continue;
                }
                int temp_counter = 0;
                boolean key = false;

                //CHECK IF LOCATION IS ALREADY RECEIVING A NEST
                for (int i = 0; i < result.length; i++) {
                    if (result[i] == null) {
                        continue;
                    }
                    if (lambda.equals(result[i].getLoc())) {
                        key = true;
                        temp_counter = i;
                        break;
                    }
                }

                if (key) {
                    int maxNest = Math.max(nest.getSize(), result[temp_counter].getSize());
                    result[temp_counter] = new Nest(lambda, maxNest);
                } else {
                    result[counter] = new Nest(lambda, nest.getSize());
                    counter++;
                }
            }
        }
        return result;
    }

    //I N F // not used
    public Location[] INF_R() {
        Location[] source = loc_R();
        Location[] result = new Location[loc_R_num()];
        int counter = 0;

        for (Location loc : source) {
            if (loc == null) {
                continue;
            }

            Term temp = loc.left ? loc.getAlpha().left : loc.getAlpha().right;

            if (inCharSet(temp.subTermAt(loc.position).getSymbol(), sigmaD()) && nonNullElements(intersection(theY(new Location[]{loc}), subLocations(loc))) >= 2) {
                result[counter] = loc;
                counter++;
            }

        }
        return result;
    }

            // not used
    public char[] INF_R_onlychars() {
        char[] res = new char[100];
        int res_counter = 0;

        Location[] source = INF_R();
        for (Location loc : source) {
            if (loc == null) {
                continue;
            }
            res[res_counter] = loc.symbolAtLoc();
            res_counter++;
        }
        return res;
    }

    //THE X i.e. NON-NDG LOCATIONS
    public Location[] theX() {
        Location[] result = new Location[loc_R_num()];
        Location[] source = loc_R();
        int counter_res = 0;
        boolean location_added = false;
        boolean made_change = false;

        //BASIS
        for (Location loc : source) {
            //NESTED DEF on LHS
            if (!loc.getPosition().equals("eps") &&
                    loc.left && Arrays.asList(loc.getAlpha().left.positions(true)).contains(loc.getPosition()) &&
                    inCharSet(loc.getAlpha().left.subTermAt(loc.getPosition()).getSymbol(), sigmaD())) {
                result[counter_res] = loc;
                counter_res++;
            }

            //DUP-ED VARS on RHS
            if (!loc.left && inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), vars)) {
                for (String posi :
                        loc.getAlpha().right.positions(false)) {
                    if (!loc.position.equals(posi) &&
                            loc.getAlpha().right.subTermAt(posi).getSymbol() == loc.getAlpha().right.subTermAt(loc.position).getSymbol()) {
                        result[counter_res] = loc;
                        counter_res++;
                        break;
                    }
                }
            }

        }


        do {
            made_change = false;
            for (Location loc : source) {
                location_added = false;
                if (inLocSet(loc, result)) {
                    continue;
                }

                //L2R VAR-TO-VAR
                if (loc.left && inCharSet(loc.getAlpha().left.subTermAt(loc.position).getSymbol(), vars)) {
                    for (String posi :
                            loc.getAlpha().right.positions(true)) {
                        if (inLocSet(new Location(loc.getAlpha(), !loc.left, posi), result) &&
                                loc.getAlpha().right.subTermAt(posi).getSymbol() == loc.getAlpha().left.subTermAt(loc.position).getSymbol()) {
                            result[counter_res] = loc;
                            counter_res++;
                            made_change = true;
                            location_added = true;
                            break;
                        }
                    }
                }

                //DATA FLOWs
                if (!loc.left) {
                    //R2L
                    String pi = "eps";
                    for (Rule rule : rules) {
                        if (rule == null) {
                            continue;
                        }
                        if (!loc.position.equals("eps") && MGU(CAP(loc.getAlpha().right.subTermAt(pi)), rule.getLeft())) {
                            for (String omega :
                                    rule.getLeft().positions(true)) {
                                if (n_PAR(loc.position, omega) &&
                                        inLocSet(new Location(rule, true, omega), result)) {
                                    result[counter_res] = loc;
                                    counter_res++;
                                    made_change = true;
                                    location_added = true;
                                    break;
                                }
                            }
                        }
                        if (location_added) {
                            break;
                        }
                    }
                    if (location_added) {
                        continue;
                    }

                    //COPY FOR PI > EPS
                    //notes - - - added " - 1" to condition
                    for (int i = 0; i < loc.position.length() - 1 &&
                            loc.position.length() > 2 &&
                            !loc.position.equals("eps"); i += 2) {

                        pi = loc.position.substring(0, i + 1);
                        //COPIED PART
                        for (Rule rule : rules) {
                            if (rule == null) {
                                continue;
                            }
                            if (MGU(CAP(loc.getAlpha().right.subTermAt(pi)), rule.getLeft())) {
                                for (String omega :
                                        rule.getLeft().positions(true)) {
                                    if (n_PAR(loc.position.substring(i + 2), omega) &&
                                            inLocSet(new Location(rule, true, omega), result)) {
                                        result[counter_res] = loc;
                                        counter_res++;
                                        made_change = true;
                                        location_added = true;
                                        break;
                                    }
                                }
                            }
                            if (location_added) {
                                break;
                            }
                        }
                        if (location_added) {
                            break;
                        }
                    }

                    //R2R
                    for (Rule rule : rules) {
                        if (rule == null) {
                            continue;
                        }
                        for (String upsilon : rule.right.positions(true)) {
                            if (inCharSet(rule.right.subTermAt(upsilon).getSymbol(), sigmaD()) &&
                                    MGU(CAP(rule.right.subTermAt(upsilon)), loc.getAlpha().left) &&
                                    inLocSet(new Location(rule, false, upsilon), result)) {
                                result[counter_res] = loc;
                                counter_res++;
                                made_change = true;
                                location_added = true;
                                break;
                            }
                            if (location_added) {
                                break;
                            }
                        }
                        if (location_added) {
                            break;
                        }
                    }
                }
            }
        } while (made_change);

        return result;
    }

    //THE Y_DELTA i.e. WHERE DEFINED SYMBOLS on rhs FLOW
    public Location[] theY(Location[] defRHS) {
        Location[] result = new Location[loc_R_num()];
        int counter_res = 0;

        //DELTA
        for (Location loc : defRHS) {
            if (loc == null) {
                continue;
            }
            result[counter_res] = loc;
            counter_res++;
        }

        boolean made_change = false;
        do {
            made_change = false;

            for (Location loc : loc_R()) {
                if (inLocSet(loc, result)) {
                    continue;
                }
                boolean location_added = false;

                if (loc.left && !loc.position.equals("eps")) {
                    //R2L
                    for (Rule rule : rules) {
                        if (rule == null) {
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
                            if (location_added) {
                                break;
                            }
                        }

                        for (String tau :
                                rule.getRight().positions(false)) {
                            for (int i = 0; i < tau.length() - 1 &&
                                    tau.length() > 2; i += 2) {
                                pi = tau.substring(0, i + 1);
                                if (MGU(CAP(rule.right.subTermAt(pi)), loc.getAlpha().left) &&
                                        n_PAR(tau.substring(i + 2), loc.position) &&
                                        // changed pi -> tau in next line
                                        inLocSet(new Location(rule, false, tau), result)) {
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
                        if (location_added) {
                            break;
                        }
                    }
                } else {
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
        } while (made_change);

        return result;
    }

    //THE ALEPH
    public Nest[] theALEPH() {
        Location[] source = loc_R();
        Nest[] result = new Nest[loc_R_num()];
        int counter = 0;

        for (Location loc : source) {
            if (!loc.left &&
                    inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), sigmaD())) {
                int temp = loc.getAlpha().right.subTermAt(loc.position).nestSize(sigmaD(), vars);
                if (temp >= 2) {
                    result[counter] = new Nest(loc, temp);
                    counter++;
                }
            }
        }
        return result;
    }

    //ONLY GENERATES THE RELATIVE TRS of THE ENCODING
    public TRS relativeTRS(boolean enableExpanded) {
        //SOURCES
        Location[] source_ENC = ENC_R();
        char[] source_INF = INF_R_onlychars();
        Location[] source_all = loc_R();

        //RELATIVE TRS - RULES
        Rule[] relativeRules = new Rule[100];
        int rr_counter = 0;

        //RELATIVE TRS - VARS
        char[] variables = new char[20];
        int var_counter = 0;

        //NON-REPETITION OF SYMBOLS
        char[] rememberer = new char[50];
        int rem_counter = 0;

        //EXECUTION and PROPAGATION for DEFINED SYMBOLS
        for (Location loc : source_ENC) {

            if (inCharSet(loc.symbolAtLoc(), sigmaD()) && !inCharSet(loc.symbolAtLoc(), rememberer)) {
                int arrity = loc.left ? loc.alpha.left.subTermAt(loc.position).arrity : loc.alpha.right.subTermAt(loc.position).arrity;
                rememberer[rem_counter] = loc.symbolAtLoc();
                rem_counter++;

                //EXECUTION
                String tempL = "i(l_" + loc.symbolAtLoc();
                if (arrity > 0) {
                    tempL += "(";
                    for (int i = 0; i < arrity; i++) {
                        char t = (vars[i] != 0 ? vars[i] : (char) (80 + i));
                        if (!inCharSet(t, variables)) {
                            variables[var_counter] = t;
                            var_counter++;
                        }
                        tempL += t + (i == arrity - 1 ? "" : ",");
                    }
                    tempL += ")";
                }
                tempL += ")";
                String tempR = "i(" + tempL.substring(4);

                relativeRules[rr_counter] = new Rule(stringToTerm(tempL, false), stringToTerm(tempR, false));
                rr_counter++;

                //PROPAGATION
                if (arrity > 0) {
                    String propaR;
                    if (inCharSet(loc.symbolAtLoc(), source_INF) || !enableExpanded) {
                        propaR = "i(l_" + loc.symbolAtLoc();
                    } else {
                        propaR = "l_" + loc.symbolAtLoc();
                    }


                    propaR += "(";
                    for (int i = 0; i < arrity; i++) {
                        char t = (vars[i] != 0 ? vars[i] : (char) (80 + i));
                        if (!inCharSet(t, variables)) {
                            variables[var_counter] = t;
                            var_counter++;
                        }
                        propaR += "i(" + t + ")" + (i == arrity - 1 ? "" : ",");
                    }
                    propaR += ")";
                    if (inCharSet(loc.symbolAtLoc(), source_INF) || !enableExpanded) {
                        propaR += ")";
                    }

                    relativeRules[rr_counter] = new Rule(stringToTerm(tempL, false), stringToTerm(propaR, false));
                    rr_counter++;
                }
            }
        }
        //PROPAGATION for CONSTRUCTORS
        for (Location loc : source_all) {
            if (!inCharSet(loc.symbolAtLoc(), sigmaD()) && !inCharSet(loc.symbolAtLoc(), vars) && !inCharSet(loc.symbolAtLoc(), rememberer)) {
                //CONSTRUCTOR PROPAGATION
                int arrity = loc.left ? loc.alpha.left.subTermAt(loc.position).arrity : loc.alpha.right.subTermAt(loc.position).arrity;

                rememberer[rem_counter] = loc.symbolAtLoc();
                rem_counter++;

                String tempL = "i(" + loc.symbolAtLoc();
                if (arrity > 0) {
                    tempL += "(";
                    for (int i = 0; i < arrity; i++) {
                        char t = (vars[i] != 0 ? vars[i] : (char) (80 + i));
                        if (!inCharSet(t, variables)) {
                            variables[var_counter] = t;
                            var_counter++;
                        }
                        tempL += t + (i == arrity - 1 ? "" : ",");
                    }
                    tempL += ")";
                } else {
                    continue;
                }
                tempL += ")";

                String propaR = loc.symbolAtLoc() + "(";

                for (int i = 0; i < arrity; i++) {
                    char t = (vars[i] != 0 ? vars[i] : (char) (80 + i));
                    if (!inCharSet(t, variables)) {
                        variables[var_counter] = t;
                        var_counter++;
                    }
                    propaR += "i(" + t + ")" + (i == arrity - 1 ? "" : ",");
                }
                propaR += ")";

                relativeRules[rr_counter] = new Rule(stringToTerm(tempL, false), stringToTerm(propaR, false));
                rr_counter++;
            }
        }

        relativeRules[rr_counter] = new Rule(stringToTerm("i(X)", false), stringToTerm("X", false));

        return new TRS(relativeRules, variables);
    }

    //DEFINED SYMBOLS ON RHS
    public Location[] defRHS() {
        Location[] result = new Location[loc_R_num()];
        Location[] source = loc_R();
        int counter_res = 0;

        for (Location loc : source) {
            if (!loc.left && inCharSet(loc.getAlpha().right.subTermAt(loc.position).getSymbol(), sigmaD())) {
                result[counter_res] = loc;
                counter_res++;
            }
        }
        return result;
    }

    //VAR LOCATIONS ON LHS
    public Location[] varLHS() {
        Location[] result = new Location[loc_R_num()];
        Location[] source = loc_R();
        int counter_res = 0;

        for (Location loc : source) {
            if (loc.left && inCharSet(loc.getAlpha().left.subTermAt(loc.position).getSymbol(), vars)) {
                result[counter_res] = loc;
                counter_res++;
            }
        }
        return result;
    }

    //INTERSECTION
    public Location[] intersection(Location[] set_1, Location[] set_2) {
        Location[] result = new Location[Math.min(set_1.length, set_2.length)];
        int counter = 0;

        for (Location loc : set_1) {
            if (loc == null) {
                continue;
            }
            if (inLocSet(loc, set_2)) {
                result[counter] = loc;
                counter++;
            }
        }
        return result;
    }

    //COUNTS non-null ELEMENTS IN A LOC[]
    //ONLY USED in Y_DELTA
    public int nonNullElements(Location[] source) {
        int result = 0;
        for (Location loc :
                source) {
            if (loc != null) {
                result++;
            }
        }
        return result;
    }

    //RETURNS ALL SUB-LOCATIONS in a NEW LOC[]
    //ONLY USED in Y_DELTA
    public Location[] subLocations(Location loc) {
        Location[] result = new Location[loc.left ? loc.getAlpha().left.posNumber(true) : loc.getAlpha().right.posNumber(true)];
        int counter = 0;

        for (Location lambda : loc_R()) {
            if (lambda == null) {
                continue;
            }
            if (loc.getAlpha().equals(lambda.getAlpha()) && loc.left == lambda.left && LEQ(loc.position, lambda.position)) {
                result[counter] = lambda;
                counter++;
            }
        }
        return result;
    }

    //MGU CHECKER
    public boolean MGU(Term s, Term t) {
        boolean res = true;

        if (Character.isUpperCase(s.getSymbol()) || Character.isUpperCase(t.getSymbol())) {
            return true;
        }

        if (s.getSymbol() == t.getSymbol() && s.getArrity() == t.getArrity()) {
            for (int i = 0; i < s.getArrity(); i++) {
                res = res && MGU(s.getSubterms()[i], t.getSubterms()[i]);
            }
        } else {
            return false;
        }
        return res;
    }

    //CAP-function
    public Term CAP(Term s) {
        Term res = new Term(s.getSymbol(), s.getArrity(), s.getSubterms());

        for (int i = 0; i < res.getArrity(); i++) {
            if (inCharSet(res.subterms[i].getSymbol(), sigmaD())) {
                char capVar = pick();
                res.subterms[i] = new Term(capVar);
            } else if (!inCharSet(res.subterms[i].getSymbol(), vars)) {
                res.subterms[i] = CAP(res.subterms[i]);
            }
        }
        return res;
    }

    //CARDINALITY OF ALL LOCATIONS
    //USED as UPPER-BOUND for SOME ARRAYS
    public int loc_R_num() {
        int loc_num = 0;
        for (Rule rule : rules) {
            if (rule == null) {
                continue;
            }
            loc_num += rule.getLeft().posNumber(true) + rule.getRight().posNumber(true);
        }
        return loc_num;
    }

    //ALL LOCATIONS
    public Location[] loc_R() {
        int loc_num = loc_R_num();

        Location[] res = new Location[loc_num];
        int counter = 0;

        for (Rule rule : rules) {
            if (rule == null) {
                continue;
            }
            for (String pos : rule.getLeft().positions(true)) {
                res[counter] = new Location(rule, true, pos);
                counter++;
            }
            for (String pos : rule.getRight().positions(true)) {
                res[counter] = new Location(rule, false, pos);
                counter++;
            }
        }

        return res;
    }

    //WRITER
    public void write(boolean showVars, boolean relativeRules, boolean allowContinuation, boolean isContinuation) {
        if (showVars) {
            System.out.print("(VAR ");
            for (char var : vars) {
                if (var == 0) {
                    continue;
                }
                System.out.print(var + " ");
            }
            System.out.println(")");
        }

        if (!isContinuation) {
            System.out.println("(RULES");
        }

        for (Rule r :
                rules) {
            if (r == null) {
                continue;
            }
            System.out.println(r.write(relativeRules));
        }
        if (!allowContinuation) {
            System.out.println(")");
        }

    }

    //ELEMENT IN SET CHECKERS
    public boolean inCharSet(char symbol, char[] set) {
        if (set == null) {
            return false;
        }
        for (char ch : set) {
            if (ch == symbol) {
                return true;
            }
        }
        return false;
    }

    public boolean inLocSet(Location loc, Location[] locations) {
        for (Location lambda : locations) {
            if (lambda == null) {
                continue;
            }
            if (loc.alpha.equals(lambda.alpha) &&
                    loc.left == lambda.left &&
                    loc.position.equals(lambda.position)) {
                return true;
            }
        }
        return false;
    }

    public boolean equalLoc(Location loc1, Location loc2) {
            if (loc1.alpha.equals(loc2.alpha) &&
                    loc1.left == loc2.left &&
                    loc1.position.equals(loc2.position)) {
                return true;
            }
        return false;
    }

    public boolean inNestSet(Nest nest, Nest[] source) {
        for (Nest nesty : source) {
            if (nesty == null) {
                continue;
            }
            if (equalLocations(nest.getLoc(),nesty.getLoc()) &&
                    nest.getSize() == nesty.getSize()) {
                return true;
            }
        }
        return false;
    }

    //RETURNS DEFINED SYMBOLS of a TRS
    public char[] sigmaD() {
        char[] res = new char[rules.length];
        for (int i = 0; i < rules.length; i++) {
            if (rules[i] == null) {
                continue;
            }
            res[i] = rules[i].getLeft().getSymbol();
        }
        return res;
    }

    //USED TO GENERATE OUTPUT ( the rules of the relative TRS are build as Strings and interpreted via this method )
    public static Term stringToTerm(String input, boolean encoded) {
        Term[] array = new Term[input.length()];
        int array_counter = 0;
        char symbol = input.charAt(0);

        if (symbol == 'l' && input.charAt(1) == '_') {
            return stringToTerm(input.substring(2), true);
        }

        if (input.length() == 1) {
            return new Term(encoded, symbol, 0, null);
        }

        int braket_counter = 0;
        int arrity = 1;

        //ARRITY CALCULATOR
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                braket_counter++;
            } else if (input.charAt(i) == ')') {
                braket_counter--;
            } else if (input.charAt(i) == ',' && braket_counter == 1) {
                arrity++;
            }
        }

        if (arrity == 1) {
            return new Term(encoded, symbol, 1, new Term[]{stringToTerm(input.substring(2, input.length() - 1), false)});
        }

        braket_counter = 0;

        boolean key = true;
        int tempIndex = 0;
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                if (key) {
                    tempIndex = i + 1;
                    key = false;
                }
                braket_counter++;

            } else if (input.charAt(i) == ')') {
                if (i == input.length() - 1) {
                    array[array_counter] = stringToTerm(input.substring(tempIndex, i), false);
                    array_counter++;
                }
                braket_counter--;

            } else if (input.charAt(i) == ',') {
                if (key) {
                    tempIndex = i + 1;
                    key = false;
                } else if (braket_counter == 1) {
                    array[array_counter] = stringToTerm(input.substring(tempIndex, i), false);
                    array_counter++;
                    tempIndex = i + 1;
                }
            }
        }
        Term[] result = new Term[arrity];

        for (int i = 0; i < arrity; i++) {
            result[i] = array[i];
        }
        return new Term(encoded, symbol, arrity, result);

    }

    public boolean LEQ(String pi, String tau) {
        return (pi.equals("eps")) ||
                (pi.length() <= tau.length() && pi.equals(tau.substring(0, pi.length())));
    }

    public boolean n_PAR(String pi, String tau) {
        return LEQ(pi, tau) || LEQ(tau, pi);
    }

    //GETTERS AND SETTERS
    public Rule[] getRules() {
        return rules;
    }

}
