public class NodoWhile extends NodoTablaSimbolo {
    private String condicion;
    private NodoBloque bloqueTrue;

    public NodoWhile(String condicion, NodoBloque bloqueTrue) {
        this.condicion = condicion;
        this.bloqueTrue = bloqueTrue;
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto) {
        // 1. Crear el nodo de la condición (rombo)
        String idCondicion = "nodo" + contexto.incContador();;
        contexto.codigo.append(idCondicion).append("{\"").append(condicion).append("\"}\n");

        // 2. Crear un nodo dummy de salida para cuando la condición sea FALSA (rompe el ciclo)
        String idFin = "nodo" + contexto.incContador();;
        contexto.codigo.append(idFin).append("(( ))\n");

        // 3. Generar el bloque interno (lo que se repite)
        String[] idsBloque = bloqueTrue.generarMermaid(contexto);

        // 4. Conectar: Condición -- Sí --> Inicio del Bloque interno
        contexto.codigo.append(idCondicion).append(" -- Sí --> ").append(idsBloque[0]).append("\n");

        // 5. EL BUCLE (La Magia): Conectar el final del bloque de regreso a la condición
        contexto.codigo.append(idsBloque[1]).append(" --> ").append(idCondicion).append("\n");

        // 6. Conectar: Condición -- No --> Salida del ciclo
        contexto.codigo.append(idCondicion).append(" -- No --> ").append(idFin).append("\n");

        // El NodoWhile completo se comporta hacia el exterior recibiendo el flujo en la condición
        // y sacándolo por el nodo dummy de fin.
        return new String[]{idCondicion, idFin};
    }
}