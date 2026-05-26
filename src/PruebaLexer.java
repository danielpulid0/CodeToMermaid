import java.util.List;

@Deprecated
public class PruebaLexer {
	public static void main(String[] args) {
		String codigoC =
				"""
				// Este es nuestro primer test
				int main() {
					int x;
					x = 15; /* Comentario
							multilinea */
					if (x == 10) {
						x = 1;
					} else {
						x = 0;
					}
				}
				""";

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