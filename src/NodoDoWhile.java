public class NodoDoWhile extends NodoTablaSimbolo {
	private String condicion;
	private NodoBloque bloque;

	public NodoDoWhile(String condicion, NodoBloque bloque) {
		this.condicion = condicion;
		this.bloque = bloque;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String idCondicion = contexto.getNextID();
		String idUnion = contexto.getNextID();
		
		contexto.codigo.append("    ").append(idCondicion).append("{\"").append(condicion.replace("\"", "#quot;")).append("\"}\n");
		contexto.codigo.append("    ").append(idUnion).append("(( ))\n");
		
		String[] idsBloque = bloque.generarMermaid(contexto);
		String startId = (idsBloque != null) ? idsBloque[0] : idCondicion;
		
		if (idsBloque != null) {
			contexto.codigo.append("    ").append(idsBloque[1]).append(" --> ").append(idCondicion).append("\n");
		}
		
		contexto.codigo.append("    ").append(idCondicion).append(" -- Sí --> ").append(startId).append("\n");
		contexto.codigo.append("    ").append(idCondicion).append(" -- No --> ").append(idUnion).append("\n");
		
		return new String[]{startId, idUnion};
	}
}
