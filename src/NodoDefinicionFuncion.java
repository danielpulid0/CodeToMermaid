public class NodoDefinicionFuncion extends NodoTablaSimbolo
{
	private String tipo;
	private String nombre;
	private String parametros;
	private NodoBloque bloque;

	public NodoDefinicionFuncion(String tipo, String nombre, String parametros, NodoBloque bloque) {
		this.tipo = tipo;
		this.nombre = nombre;
		this.parametros = parametros;
		this.bloque = bloque;
	}

	public String getNombre() {
		return nombre;
	}

	public NodoBloque getBloque() {
		return bloque;
	}

	public String[] generarMermaid(ContextoMermaid contexto) {
		String idEntrada = contexto.getNextID();
		String idSalida  = contexto.getNextID();
		contexto.codigo.append("subgraph ").append(nombre).append("[\"")
				.append(tipo).append(" ").append(nombre).append("(").append(parametros).append(")\"]\n");
		contexto.codigo.append(idEntrada).append("([Entrada])\n");
		String[] idsBloque = bloque.generarMermaid(contexto);
		contexto.codigo.append(idSalida).append("([Salida])\n");
		
		if (idsBloque != null) {
			contexto.codigo.append(idEntrada).append(" --> ").append(idsBloque[0]).append("\n");
			contexto.codigo.append(idsBloque[1]).append(" --> ").append(idSalida).append("\n");
		} else {
			contexto.codigo.append(idEntrada).append(" --> ").append(idSalida).append("\n");
		}
		
		contexto.codigo.append("end\n");
		return new String[]{idEntrada, idSalida};
	}
}
