import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;
import java.util.Base64;

public class TraductorPrincipal {
	public static void main(String[] args) {
		String archivoCodigo = "codigo.c";
		if (args.length > 0)
			archivoCodigo = args[0];

		String entrada = leerArchivo(archivoCodigo);

		if (entrada.isEmpty()) {
			System.out.println("Error. No se ingresó ninguna entrada.");
			return;
		}

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

			System.out.println("--- CÓDIGO MERMAID ---");
			System.out.println(contexto.codigo.toString());
			abrirEnNavegador(contexto.codigo.toString());

			// Solo emitir sequenceDiagram si hay funciones definidas con llamadas
			if (!ast.getFunciones().isEmpty() && ast.tieneLlamadasAFunciones()) {
				ContextoMermaidSequence ctxSeq = new ContextoMermaidSequence();
				ast.generarSequenceDiagram(ctxSeq);
				System.out.println("--- SEQUENCE DIAGRAM ---");
				System.out.println(ctxSeq.codigo.toString());
			}

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

	public static void abrirEnNavegador(String codigo) {
		try {
			// Escapar caracteres especiales para JSON válido
			String codigoEscapado = codigo
					.replace("\\", "\\\\")
					.replace("\"", "\\\"")
					.replace("\n", "\\n")
					.replace("\r", "\\r")
					.replace("\t", "\\t");

			// Mermaid Live espera un JSON con esta estructura exacta
			String json = "{\"code\":\"" + codigoEscapado + "\",\"mermaid\":{\"theme\":\"default\"}}";

			String base64 = Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
			String url = "https://mermaid.live/edit#base64:" + base64;

			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
