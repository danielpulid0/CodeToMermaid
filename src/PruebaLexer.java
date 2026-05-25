import java.util.List;

public class PruebaLexer {
    public static void main(String[] args) {
        String codigoC = 
            "// Este es nuestro primer test\n" +
            "int main() {\n" +
            "    int x;\n" +
            "    x = 15; /* Comentario\n" +
            "               multilinea */\n" +
            "    if (x == 10) {\n" +
            "        x = 1;\n" +
            "    } else {\n" +
            "        x = 0;\n" +
            "    }\n" +
            "}";

        try {
            CLexer lexer = new CLexer(codigoC);
            List<Token> tokens = lexer.analizar();
            
            System.out.println("--- TOKENS RECONOCIDOS ---");
            for (Token t : tokens) {
                System.out.println(t.getTipo() + " -> " + t.getNombre());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}