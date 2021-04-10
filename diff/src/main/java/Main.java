public class Main {
    public static void main(String... args) {
//        Differ.getDiff(args[0], args[1], args[2]);
        Differ.getDiff("src/main/resources/old.txt", "src/main/resources/new.txt", "src/main/resources/diff.html");
    }
}
