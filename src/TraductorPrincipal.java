import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TraductorPrincipal {
    public static void main(String[] args) {
        String archivoCodigo = "codigo.c";
        String entrada = leerArchivo(archivoCodigo);
        
        if (entrada.isEmpty()) {
            System.out.println("Error. No se ingresó ninguna entrada.");
            return;
        }

        System.out.println(entrada);


        String codigoC = 
            "int main() {\n" +
            "    int x;\n" +
            "    scanf(\"%d\", &x);\n" +
            "    x = x + 1;\n" +
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
            CLexer lexer = new CLexer(entrada);
            List<Token> listaTokens = lexer.analizar();

            // 2. Análisis Sintáctico y Construcción del AST
            CParser parser = new CParser(listaTokens);
            NodoPrograma ast = parser.analizar(); // Esto construye todo el árbol automáticamente
            
            // 3. Generación de Código Mermaid (Backend)
            ContextoMermaid contexto = new ContextoMermaid();
            ast.generarMermaid(contexto);
            
            System.out.println("--- CÓDIGO MERMAID GENERADO ---");
            System.out.println(contexto.codigo.toString());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String leerArchivo(String archivo) {
        StringBuilder entrada = new StringBuilder();
        try {
            FileReader reader = new FileReader(archivo);
            int caracter;
            while ((caracter = reader.read()) != -1)
                entrada.append((char) caracter);
            reader.close();
            return entrada.toString();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return "";
        }
    }

}
