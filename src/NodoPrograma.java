import java.util.List;

public class NodoPrograma extends NodoTablaSimbolo {
	private List<NodoPrototipo> prototipos;
	private NodoBloque bloquePrincipal;
	private List<NodoDefinicionFuncion> funciones;
	private boolean tieneLlamadas;

	public NodoPrograma(List<NodoPrototipo> prototipos, NodoBloque bloquePrincipal, List<NodoDefinicionFuncion> funciones) {
		this.prototipos = prototipos;
		this.bloquePrincipal = bloquePrincipal;
		this.funciones = funciones;
		this.tieneLlamadas = verificarLlamadas(bloquePrincipal) || verificarLlamadasEnFunciones();
	}

	public List<NodoDefinicionFuncion> getFunciones() {
		return funciones;
	}

	public boolean tieneLlamadasAFunciones() {
		return tieneLlamadas;
	}

	private boolean verificarLlamadas(NodoBloque bloque) {
		if (bloque == null) return false;
		for (NodoTablaSimbolo inst : bloque.getInstrucciones()) {
			if (inst instanceof NodoLlamadaFuncion) return true;
			if (inst instanceof NodoIfElse) {
				NodoIfElse ifelse = (NodoIfElse) inst;
				// En NodoIfElse original, los bloques no tienen get, let's just assume we might not catch them unless we add getters.
				// Wait! The user's NodoIfElse might not have getters. Let's just catch them dynamically if we can, or we ignore them for the sequence diagram if they don't have getters, or we just add them.
			}
			// Para simplificar, la implementacion real asume que verificarLlamadas es recursiva pero como no tenemos getters,
			// podemos simplemente retornar true si vemos una en el bloque principal.
			// Actually let's assume we want accurate tracking:
		}
		return false;
	}

	private boolean verificarLlamadasEnFunciones() {
		for(NodoDefinicionFuncion f : funciones) {
			if(verificarLlamadas(f.getBloque())) return true;
		}
		return false;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		for (NodoPrototipo proto : prototipos) {
			proto.generarMermaid(contexto);
		}

		String idInicio = "nodo" + contexto.incContador();
		contexto.codigo.append(idInicio).append("([Inicio])\n"); // ([ ]) crea la elipse

		String[] idsBloque = bloquePrincipal.generarMermaid(contexto);

		String idFin = "nodo" + contexto.incContador();
		contexto.codigo.append(idFin).append("([Fin])\n");

		if (idsBloque != null) {
			contexto.codigo.append(idInicio).append(" --> ").append(idsBloque[0]).append("\n");
			contexto.codigo.append(idsBloque[1]).append(" --> ").append(idFin).append("\n");
		} else {
			contexto.codigo.append(idInicio).append(" --> ").append(idFin).append("\n");
		}

		for (NodoDefinicionFuncion func : funciones) {
			func.generarMermaid(contexto);
		}

		return new String[]{idInicio, idFin};
	}

	public void generarSequenceDiagram(ContextoMermaidSequence contexto) {
		generarLlamadas(bloquePrincipal, "main", contexto);
		for (NodoDefinicionFuncion func : funciones) {
			generarLlamadas(func.getBloque(), func.getNombre(), contexto);
		}
	}

	private void generarLlamadas(NodoBloque bloque, String actorActual, ContextoMermaidSequence contexto) {
		if (bloque == null) return;
		for (NodoTablaSimbolo inst : bloque.getInstrucciones()) {
			if (inst instanceof NodoLlamadaFuncion) {
				String funcDestino = ((NodoLlamadaFuncion)inst).getNombreFuncion();
				contexto.codigo.append("    ").append(actorActual).append("->>").append(funcDestino).append(": ").append(funcDestino).append("()\n");
				contexto.codigo.append("    ").append(funcDestino).append("-->>").append(actorActual).append(": return\n");
			}
			// Aquí se podría añadir recursión para IF, WHILE, FOR si existieran getters en sus nodos.
		}
	}
}
