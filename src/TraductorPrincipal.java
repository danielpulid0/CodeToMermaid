import java.util.List;

public class TraductorPrincipal {
    public static void main(String[] args) {
        String codigoC = 
            "int main() {\n" +
            "    int x;\n" +
            "    x = 0;\n" +
            "    while (x < 5) {\n" +
            "        x = x + 1;\n" +
            "    }\n" +
            "    int i;\n" +
            "    for (i = 0 ; i < 10; i = i + 1) {\n" +
            "        x=i;\n" +
            "    }\n" +
            "}";

        try {
            // 1. Análisis Léxico
            CLexer lexer = new CLexer(codigoC);
            List<Token> listaTokens = lexer.analizar();

            // 2. Análisis Sintáctico y Construcción del AST
            CParser parser = new CParser(listaTokens);
            NodoBloque ast = parser.analizar(); // Esto construye todo el árbol automáticamente
            
            // 3. Generación de Código Mermaid (Backend)
            ContextoMermaid contexto = new ContextoMermaid();
            ast.generarMermaid(contexto);
            
            System.out.println("--- CÓDIGO MERMAID GENERADO ---");
            System.out.println(contexto.codigo.toString());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
