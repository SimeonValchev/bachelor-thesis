public class Rule {

    Term left;
    Term right;

    public Rule(Term left, Term right) {
        this.left = new Term(left.getSymbol(),left.getArrity(),left.getSubterms());
        this.right = new Term(right.getSymbol(),right.getArrity(),right.getSubterms());
    }

    public String write(){
        return left.write() + " -> " + right.write();
    }

    //GETTERS & SETTERS
    public Term getLeft() {
        return left;
    }

    public void setLeft(Term left) {
        this.left = left;
    }

    public Term getRight() {
        return right;
    }

    public void setRight(Term right) {
        this.right = right;
    }
}
