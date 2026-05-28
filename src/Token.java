public class Token {
	private TipoToken tipo;
	private String nombre;

	public Token(TipoToken tipo, String nombre){
		this.tipo = tipo;
		this.nombre = nombre;
	}

	public TipoToken getTipo(){
		return tipo;
	}

	public String getNombre(){
		return nombre;
	}
}
