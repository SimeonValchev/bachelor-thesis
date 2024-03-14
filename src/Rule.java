public class Rule {

    Term left;
    Term right;

    public Rule(Term left, Term right) {
        this.left = new Term(left.encoded, left.getSymbol(),left.getArrity(),left.getSubterms());
        this.right = new Term(right.encoded, right.getSymbol(),right.getArrity(),right.getSubterms());
    }

    public String write(boolean relative){
        return left.write() + " ->" + (relative ? "= " : " ") + right.write();
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
