package util.model;

public class J {
    private final M m;

    public void testJ(){
        this.m.print();
    }

    public J(M m) {
        this.m = m;
    }

    public void print(){
        System.out.println("from J class");
    }
}
