import org.junit.Test;

public class SimpleTest {

    @Test
    public void name() {
        final String s = "http://localhost:8080/shorter/hello1";
        System.out.println(s.substring(s.lastIndexOf("/")));
    }
}
