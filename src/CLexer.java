import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLexer {
	private String entrada;
	private int posicion;
	private List<Token> tokens;

	public CLexer(String entrada) {
		this.entrada = entrada;
		this.posicion = 0;
		this.tokens = new ArrayList<>();
	}

	public List<Token> analizar() throws Exception {
		while (posicion < entrada.length()) {
			ignorarEspaciosYComentarios();

			if (posicion >= entrada.length()) {
				break; // Llegamos al final
			}

			boolean matchRealizado = false;

			// Itera sobre cada tipo de token para ver cuál coincide
			for (TipoToken tipo : TipoToken.values()) {
				if (tipo == TipoToken.EOF)
					continue;

				// Compilamos el patrón para buscar desde la posición actual
				// Se usa ^ para asegurar que coincida exactamente en el inicio de la subcadena
				Pattern patron = Pattern.compile("^" + tipo.patron);
				Matcher matcher = patron.matcher(entrada.substring(posicion));

				if (matcher.find()) {
					String lexema = matcher.group();

					// Validar límite de palabra para palabras reservadas (evita que 'intVariable' sea detectado como 'int')
					if ((tipo == TipoToken.INT || tipo == TipoToken.FLOAT || tipo == TipoToken.DOUBLE || tipo == TipoToken.IF || tipo == TipoToken.VOID ||
							tipo == TipoToken.PRINTF || tipo == TipoToken.ELSE ||
							tipo == TipoToken.WHILE || tipo == TipoToken.FOR || tipo == TipoToken.MAIN ||
							tipo == TipoToken.RETURN || tipo == TipoToken.SCANF || tipo == TipoToken.SWITCH ||
							tipo == TipoToken.CASE || tipo == TipoToken.DEFAULT || tipo == TipoToken.BREAK || tipo == TipoToken.DO || tipo == TipoToken.EXIT) &&
							(posicion + lexema.length() < entrada.length()) && // Asegurar que no nos salgamos del string
							Character.isLetterOrDigit(entrada.charAt(posicion + lexema.length()))) {

						continue; // No es la palabra reservada aislada, es parte de un identificador. Pasamos al siguiente TipoToken.
					}

					tokens.add(new Token(tipo, lexema));
					posicion += lexema.length();
					matchRealizado = true;
					break; // Rompemos el for porque ya encontramos el token
				}
			}

			if (!matchRealizado) {
				throw new Exception("Error Léxico: Carácter no reconocido '" + entrada.charAt(posicion) + "' en la posición " + posicion);
			}
		}

		tokens.add(new Token(TipoToken.EOF, ""));
		return tokens;
	}

	private void ignorarEspaciosYComentarios() {
		boolean avanzando = true;
		while (avanzando && posicion < entrada.length()) {
			avanzando = false;

			// 1. Ignorar espacios en blanco, tabs y saltos de línea
			while (posicion < entrada.length() && Character.isWhitespace(entrada.charAt(posicion))) {
				posicion++;
				avanzando = true;
			}

			// 2. Ignorar comentarios de una línea (//)
			if (posicion < entrada.length() - 1 && entrada.startsWith("//", posicion)) {
				posicion += 2;
				while (posicion < entrada.length() && entrada.charAt(posicion) != '\n') {
					posicion++;
				}
				avanzando = true;
			}

			// 3. Ignorar comentarios multilinea (/* ... */)
			if (posicion < entrada.length() - 1 && entrada.startsWith("/*", posicion)) {
				posicion += 2;
				while (posicion < entrada.length() - 1 && !entrada.startsWith("*/", posicion)) {
					posicion++;
				}
				posicion += 2; // Saltar el "*/"
				avanzando = true;
			}
		}
	}

	public List<Token> getTokens() {
		return tokens;
	}
}