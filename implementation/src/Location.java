public class Location {

    Rule alpha;
    boolean left;
    String position;

    public Location(Rule alpha, boolean left, String position) {
        this.alpha = alpha;
        this.left = left;
        this.position = position;
    }

    public String write(){
        return "(" + alpha.write() + ", " + (left ? "L" : "R") + ", " + position + ")" ;
    }

    public char symbolAtLoc(){
        return this.left ? this.getAlpha().left.subTermAt(this.getPosition()).getSymbol() : this.getAlpha().right.subTermAt(this.getPosition()).getSymbol();
    }

    public int getSizeFromSet(Nest[] source){
        for (Nest nest : source) {
            if(nest == null){
                continue;
            }
            if(this.getAlpha().equals(nest.getLoc().getAlpha()) &&
                    this.getPosition().equals(nest.getLoc().getPosition()) &&
                    this.left == nest.getLoc().left){
                return nest.getSize();
            }
        }
        return 1;
    }

    //GETTERS AND SETTERS
    public Rule getAlpha() {
        return alpha;
    }

    public void setAlpha(Rule alpha) {
        this.alpha = alpha;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
