public class NodoLlamadaFuncion extends NodoTablaSimbolo
{
	private String nombreFuncion;
	private String asignadoA;
	private String parametros;

	public NodoLlamadaFuncion(String nombreFuncion, String asignadoA, String parametros) {
		this.nombreFuncion = nombreFuncion;
		this.asignadoA = asignadoA;
		this.parametros = parametros;
	}

	public String getNombreFuncion() {
		return nombreFuncion;
	}

	public String[] generarMermaid(ContextoMermaid contexto) {
		String id = contexto.getNextID();
		String texto = "";
		if (asignadoA != null && !asignadoA.isEmpty()) texto += asignadoA;
		texto += nombreFuncion + "(" + (parametros != null ? parametros : "") + ")";

		texto = texto.replace("\"", "#quot;");

		// [[doble corchete]] = subproceso en Mermaid
		contexto.codigo.append("    ").append(id)
				.append("[[\"").append(texto).append("\"]]\n");
		return new String[]{id, id};
	}
}
