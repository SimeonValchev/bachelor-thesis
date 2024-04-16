public class VarsCAP {
    public static char[] CAP_vars = {'O','A','B','C','D','E','F','G','H','I','J','K','L','M','N'};
    static int counter = 0;

    public static char pick(){
        counter += 1;
        counter = counter % CAP_vars.length;

        return CAP_vars[counter];
    }
}
