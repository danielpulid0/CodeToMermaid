public class NodoPrograma extends NodoTablaSimbolo {
    private NodoBloque bloquePrincipal;

    public NodoPrograma(NodoBloque bloquePrincipal) {
        this.bloquePrincipal = bloquePrincipal;
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto) {
        // 1. Nodo Elipse de Inicio
        String idInicio = "nodo" + contexto.incContador();
        contexto.codigo.append(idInicio).append("([Inicio])\n"); // ([ ]) crea la elipse

        // 2. Generar todo el bloque interior (el main)
        String[] idsBloque = bloquePrincipal.generarMermaid(contexto);

        // 3. Nodo Elipse de Fin
        String idFin = "nodo" + contexto.incContador();
        contexto.codigo.append(idFin).append("([Fin])\n");

        // 4. Conectar Inicio --> Bloque Main --> Fin
        contexto.codigo.append(idInicio).append(" --> ").append(idsBloque[0]).append("\n");
        contexto.codigo.append(idsBloque[1]).append(" --> ").append(idFin).append("\n");

        return new String[]{idInicio, idFin};
    }
} 
