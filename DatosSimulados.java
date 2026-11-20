import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class DatosSimulados {

    private static final List<String> RUTAS = List.of(
        "Ruta Troncal Norte", 
        "Ruta Expresa Sur", 
        "Alimentador Centro", 
        "Circular Oriente", 
        "Ruta Occidental"
    );

    private static final List<String> ESTACIONES = List.of(
        "Estacion Central", 
        "Portal Norte", 
        "Terminal Sur", 
        "Estacion Parque", 
        "Estacion Universidad", 
        "Estacion Estadio"
    );

    // Genero una lista inmutable de viajes simulados sin usar ciclos tradicionales
    public static List<RegistroViaje> generarRegistros(int cantidadViajes, long semilla) {
        Random aleatorio = new Random(semilla);
        LocalDateTime fechaBase = LocalDateTime.of(2026, 11, 19, 5, 0);

        return IntStream.range(0, cantidadViajes)
            .boxed()
            .flatMap(i -> {
                String usuario = "USR-" + (1000 + aleatorio.nextInt(150));
                String ruta = RUTAS.get(aleatorio.nextInt(RUTAS.size()));
                String origen = ESTACIONES.get(aleatorio.nextInt(ESTACIONES.size()));
                String destino = ESTACIONES.get((aleatorio.nextInt(ESTACIONES.size() - 1) + 1) % ESTACIONES.size());

                // Simulo horas pico en la manana (7 a 9) y en la tarde (17 a 19)
                int hora = (i % 3 == 0) 
                    ? 7 + aleatorio.nextInt(3) 
                    : (i % 3 == 1 ? 17 + aleatorio.nextInt(3) : 6 + aleatorio.nextInt(15));
                
                int minuto = aleatorio.nextInt(60);
                LocalDateTime horaEntrada = fechaBase.withHour(hora).withMinute(minuto);
                
                // Calculo una duracion de viaje entre 8 y 35 minutos
                int minutosViaje = 8 + aleatorio.nextInt(28);
                LocalDateTime horaSalida = horaEntrada.plusMinutes(minutosViaje);

                RegistroViaje entrada = new RegistroViaje(usuario, ruta, origen, "entrada", horaEntrada);
                RegistroViaje salida = new RegistroViaje(usuario, ruta, destino, "salida", horaSalida);

                return List.of(entrada, salida).stream();
            })
            .toList();
    }
}
