import java.util.List;
import java.util.Map;

public class Principal {

    public static void main(String[] args) {
        // Genero 5000 viajes simulados (10000 registros entre entradas y salidas)
        List<RegistroViaje> registros = DatosSimulados.generarRegistros(5000, 42L);

        System.out.println("=== SISTEMA DE GESTION DE TRANSPORTE TECNOMOVIL DATA ===");
        System.out.println("Total de registros procesados: " + registros.size());

        // Prueba de funcion de orden superior con expresion lambda
        List<RegistroViaje> soloEntradas = AnalisisTransporte.filtrarRegistros(
                registros, 
                r -> r.esEntrada()
        );
        System.out.println("Total de ingresos validados: " + soloEntradas.size());

        // a) Afluencia por estacion
        System.out.println("\n--- a) Afluencia por estacion ---");
        Map<String, Long> afluencia = AnalisisTransporte.calcularAfluenciaEstacion(registros);
        afluencia.forEach((estacion, total) -> 
                System.out.println(estacion + ": " + total + " ingresos"));

        // b) Identificacion de horas pico (top 5 horas de mayor flujo)
        System.out.println("\n--- b) Top 5 horas pico ---");
        AnalisisTransporte.identificarHorasPico(registros).entrySet().stream()
                .limit(5)
                .forEach(hora -> System.out.println(
                        hora.getKey() + ":00 hrs -> " + hora.getValue() + " registros"));

        // c) Rutas mas utilizadas
        System.out.println("\n--- c) Rutas mas utilizadas ---");
        AnalisisTransporte.obtenerRutasMasUsadas(registros)
                .forEach(ruta -> System.out.println(
                        ruta.getKey() + ": " + ruta.getValue() + " pasajeros"));

        // d) Patrones de viaje por usuario (muestro 3 usuarios de ejemplo)
        System.out.println("\n--- d) Patrones de viaje (muestra de 3 usuarios) ---");
        AnalisisTransporte.obtenerPatronesUsuario(registros).entrySet().stream()
                .limit(3)
                .forEach(usuario -> System.out.println(
                        usuario.getKey() + " -> " + usuario.getValue()));

        // e) Tiempo promedio entre estaciones
        System.out.println("\n--- e) Tiempo promedio de viaje entre estaciones ---");
        double promedioMinutos = AnalisisTransporte.calcularTiempoPromedio(registros);
        System.out.printf("Tiempo promedio: %.2f minutos%n", promedioMinutos);

        // f) Deteccion de sobrecarga en rutas (umbral de 1000 pasajeros)
        System.out.println("\n--- f) Estado de ocupacion de rutas (umbral: 1000) ---");
        Map<String, String> estadoRutas = AnalisisTransporte.detectarSobrecargaRutas(
                registros,
                cantidad -> cantidad > 1000 ? "CRITICA" : "NORMAL"
        );
        estadoRutas.forEach((ruta, estado) -> 
                System.out.println(ruta + " -> Estado: " + estado));
    }
}
