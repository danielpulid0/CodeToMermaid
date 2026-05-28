import java.util.List;

public class NodoSwitch extends NodoTablaSimbolo {
	private String condicion;
	private List<String> cases;
	private List<NodoBloque> bloques;
	private NodoBloque bloqueDefault;

	public NodoSwitch(String condicion, List<String> cases, List<NodoBloque> bloques, NodoBloque bloqueDefault) {
		this.condicion = condicion;
		this.cases = cases;
		this.bloques = bloques;
		this.bloqueDefault = bloqueDefault;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String idSwitch = contexto.getNextID();
		String idUnion = contexto.getNextID();
		
		contexto.codigo.append("    ").append(idSwitch).append("{\"switch(").append(condicion.replace("\"", "#quot;")).append(")\"}\n");
		contexto.codigo.append("    ").append(idUnion).append("(( ))\n");
		
		for (int i = 0; i < cases.size(); i++) {
			String[] idsBloque = bloques.get(i).generarMermaid(contexto);
			if (idsBloque != null) {
				contexto.codigo.append("    ").append(idSwitch).append(" -- \"case ").append(cases.get(i).replace("\"", "#quot;")).append("\" --> ").append(idsBloque[0]).append("\n");
				contexto.codigo.append("    ").append(idsBloque[1]).append(" --> ").append(idUnion).append("\n");
			} else {
				contexto.codigo.append("    ").append(idSwitch).append(" -- \"case ").append(cases.get(i).replace("\"", "#quot;")).append("\" --> ").append(idUnion).append("\n");
			}
		}
		
		if (bloqueDefault != null) {
			String[] idsBloque = bloqueDefault.generarMermaid(contexto);
			if (idsBloque != null) {
				contexto.codigo.append("    ").append(idSwitch).append(" -- \"default\" --> ").append(idsBloque[0]).append("\n");
				contexto.codigo.append("    ").append(idsBloque[1]).append(" --> ").append(idUnion).append("\n");
			} else {
				contexto.codigo.append("    ").append(idSwitch).append(" -- \"default\" --> ").append(idUnion).append("\n");
			}
		} else {
			contexto.codigo.append("    ").append(idSwitch).append(" -- \"default\" --> ").append(idUnion).append("\n");
		}
		
		return new String[]{idSwitch, idUnion};
	}
}
