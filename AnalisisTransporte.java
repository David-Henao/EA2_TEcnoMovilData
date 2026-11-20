import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnalisisTransporte {

    // Funcion de orden superior para filtrar registros segun una condicion
    public static List<RegistroViaje> filtrarRegistros(
            List<RegistroViaje> registros, 
            Predicate<RegistroViaje> condicion) {
        return registros.stream()
                .filter(condicion)
                .toList();
    }

    // a) Calculo de afluencia por estacion
    public static Map<String, Long> calcularAfluenciaEstacion(List<RegistroViaje> registros) {
        return registros.parallelStream()
                .filter(RegistroViaje::esEntrada)
                .collect(Collectors.groupingBy(
                        RegistroViaje::estacion,
                        Collectors.counting()
                ));
    }

    // b) Identificacion de horas pico de mayor a menor flujo
    public static Map<Integer, Long> identificarHorasPico(List<RegistroViaje> registros) {
        return registros.parallelStream()
                .collect(Collectors.groupingBy(
                        RegistroViaje::obtenerHora,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // c) Rutas mas utilizadas ordenadas por volumen
    public static List<Map.Entry<String, Long>> obtenerRutasMasUsadas(List<RegistroViaje> registros) {
        return registros.parallelStream()
                .filter(RegistroViaje::esEntrada)
                .collect(Collectors.groupingBy(
                        RegistroViaje::ruta,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();
    }

    // d) Patrones de viaje por usuario en orden cronologico
    public static Map<String, List<String>> obtenerPatronesUsuario(List<RegistroViaje> registros) {
        return registros.stream()
                .sorted(Comparator.comparing(RegistroViaje::timestamp))
                .collect(Collectors.groupingBy(
                        RegistroViaje::idUsuario,
                        Collectors.mapping(RegistroViaje::estacion, Collectors.toList())
                ));
    }

    // e) Calculo de tiempo promedio en minutos entre estaciones consecutivas
    public static double calcularTiempoPromedio(List<RegistroViaje> registros) {
        Map<String, List<RegistroViaje>> registrosPorUsuario = registros.stream()
                .sorted(Comparator.comparing(RegistroViaje::timestamp))
                .collect(Collectors.groupingBy(RegistroViaje::idUsuario));

        return registrosPorUsuario.values().stream()
                .flatMap(listaUsuario -> IntStream.range(0, listaUsuario.size() - 1)
                        .mapToObj(i -> List.of(listaUsuario.get(i), listaUsuario.get(i + 1)))
                )
                .filter(par -> par.get(0).esEntrada() && !par.get(1).esEntrada())
                .mapToLong(par -> Duration.between(par.get(0).timestamp(), par.get(1).timestamp()).toMinutes())
                .average()
                .orElse(0.0);
    }

    // f) Deteccion de sobrecarga en rutas aplicando una funcion clasificadora
    public static Map<String, String> detectarSobrecargaRutas(
            List<RegistroViaje> registros, 
            Function<Long, String> reglaClasificacion) {
        
        Map<String, Long> ingresosPorRuta = registros.parallelStream()
                .filter(RegistroViaje::esEntrada)
                .collect(Collectors.groupingBy(
                        RegistroViaje::ruta,
                        Collectors.counting()
                ));

        return ingresosPorRuta.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        dato -> reglaClasificacion.apply(dato.getValue())
                ));
    }
}
