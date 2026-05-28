public class NodoIfElse extends NodoTablaSimbolo {
	String condicion; // Ej: "x > 10"
	NodoBloque bloqueTrue;
	NodoBloque bloqueFalse; // Puede ser null

	public NodoIfElse(String condicion, NodoBloque bloqueTrue, NodoBloque bloqueFalse) {
		this.condicion = condicion;
		this.bloqueTrue = bloqueTrue;
		this.bloqueFalse = bloqueFalse;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String idCondicion = contexto.getNextID();
		// Nodo rombo para condiciones en Mermaid: id{"condicion"}
		contexto.codigo.append("    ").append(idCondicion).append("{\"").append(condicion).append("\"}\n");

		// Unimos el rombo con el bloque True
		String[] idsTrue = bloqueTrue.generarMermaid(contexto);
		if (idsTrue != null) {
			contexto.codigo.append("    ").append(idCondicion).append(" -- Sí --> ").append(idsTrue[0]).append("\n");
		}

		// Unimos el rombo con el bloque False
		String[] idsFalse = null;
		if (bloqueFalse != null) {
			idsFalse = bloqueFalse.generarMermaid(contexto);
			if (idsFalse != null) {
				contexto.codigo.append("    ").append(idCondicion).append(" -- No --> ").append(idsFalse[0]).append("\n");
			}
		}

		// Aquí es donde la arquitectura brilla: No sabemos a dónde van a apuntar todavía.
		// Devolveremos los ID finales de las ramas True y False para que el BlockNode padre
		// (el que contenga este IF) se encargue de conectar estas hojas con la siguiente línea de código.
		// Nota: Para mantener este ejemplo simple, supongamos que el padre maneja múltiples retornos o usamos un nodo "dummy" de unión.

		// Forma simplificada: crear un nodo vacío invisible para unir las ramas y devolver ese
		String idUnion = contexto.getNextID();
		contexto.codigo.append("    ").append(idUnion).append("(( ))\n"); // Círculo pequeño de unión

		if (idsTrue != null) contexto.codigo.append("    ").append(idsTrue[1]).append(" --> ").append(idUnion).append("\n");
		if (idsFalse != null) contexto.codigo.append("    ").append(idsFalse[1]).append(" --> ").append(idUnion).append("\n");
		else contexto.codigo.append("    ").append(idCondicion).append(" -- No --> ").append(idUnion).append("\n"); // Si no hay else

		return new String[]{idCondicion, idUnion};
	}
}