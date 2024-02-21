public class Nest {
    Location loc;
    int size;

    public Nest(Location loc, int size) {
        this.loc = loc;
        this.size = size;
    }


    public String write(){
        return "[ " + loc.write() + " , " + size + " ]";
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
