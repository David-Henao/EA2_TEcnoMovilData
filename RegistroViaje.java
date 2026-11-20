import java.time.LocalDateTime;

public record RegistroViaje(
    String idUsuario,
    String ruta,
    String estacion,
    String accion,
    LocalDateTime timestamp
) {
    // Reviso si el registro corresponde a una entrada
    public boolean esEntrada() {
        return "entrada".equalsIgnoreCase(this.accion);
    }

    // Obtengo solo la hora del dia para agrupar los datos
    public int obtenerHora() {
        return this.timestamp.getHour();
    }
}
