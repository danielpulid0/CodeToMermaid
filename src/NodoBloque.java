import java.util.ArrayList;
import java.util.List;

public class NodoBloque extends NodoTablaSimbolo
{
    List<NodoTablaSimbolo> instrucciones = new ArrayList<>();
    public void addInstruccion(NodoTablaSimbolo instruccion)
    {
        instrucciones.add(instruccion);
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto)
    {
        if (instrucciones.isEmpty()) return null;

        String primerID = null;
        String ultimoIDAnterior = null;

        for (NodoTablaSimbolo inst : instrucciones)
        {
            String[] ids = inst.generarMermaid(contexto);
            if (ids == null) continue;

            if (primerID == null)
            {
                primerID = ids[0];  // guardar el inicio del bloque
            }

            //conectar la instruccion anterior con la actual
            if (ultimoIDAnterior != null)
            {
                //generar nodoAnterior --> nodoActual
                contexto.codigo.append("    ").append(ultimoIDAnterior).append(" --> ").append(ids[0]).append("\n");
            }
            ultimoIDAnterior = ids[1];  // actualizar la ultima hoja
        }
        //retornar el inicio del bloque y el final del bloque
        return new String[]{primerID, ultimoIDAnterior};
    }
}
