package conversor;

public abstract class SistemaNumerico {


    protected int base;
    protected String nombreSistema;
    private static final String DIGITOS = "0123456789ABCDEF";

    public SistemaNumerico(int base, String nombreSistema) {
        this.base = base;
        this.nombreSistema = nombreSistema;
    }

    public int getBase() {
        return base;
    }

    public String getNombreSistema() {
        return nombreSistema;
    }

    // Método abstracto que obliga a las subclases a definir cómo validan sus entradas (Polimorfismo)
    public abstract boolean validarEntrada(String numeroStr);

    /**
     * FASE 1: Conversión de Decimal a otra base usando DIVISIONES SUCESIVAS.
     */
    public String convertirDesdeDecimalConPasos(long numeroDecimal, StringBuilder pasos) {
        pasos.append("--- Conversión de Decimal a ")
                .append(nombreSistema)
                .append(" (Base ")
                .append(base)
                .append(") ---\n");

        if (numeroDecimal == 0) {
            pasos.append("El número es 0, por lo tanto el resultado directo es 0.\n");
            return "0";
        }

        String resultadoInvertido = "";
        long cociente = numeroDecimal;

        while (cociente > 0) {
            int residuo = (int) (cociente % base);
            char digitoChar = DIGITOS.charAt(residuo);

            pasos.append(cociente)
                    .append(" ÷ ").append(base)
                    .append(" = ").append(cociente / base)
                    .append(" | Residuo: ").append(residuo)
                    .append(" ('").append(digitoChar).append("')\n");

            resultadoInvertido = resultadoInvertido + digitoChar;
            cociente = cociente / base;
        }

        // Invertimos la cadena de residuos manualmente (de abajo hacia arriba)
        String resultadoFinal = invertirCadena(resultadoInvertido);
        pasos.append("Leyendo los residuos de abajo hacia arriba: ").append(resultadoFinal).append("\n\n");
        return resultadoFinal;
    }

    /**
     * Versión directa sin texto explicativo (útil para llamadas internas).
     */
    public String convertirDesdeDecimal(long numeroDecimal) {
        return convertirDesdeDecimalConPasos(numeroDecimal, new StringBuilder());
    }

    /**
     * FASE 2: Conversión de otra base a Decimal usando EXPANSIÓN POLINÓMICA.
     * Sumatoria de: dígito * (base ^ posición)
     */
    public long convertirADecimalConPasos(String numeroStr, StringBuilder pasos) {
        numeroStr = numeroStr.toUpperCase().trim();
        pasos.append("--- Conversión de ")
                .append(nombreSistema).append(" (")
                .append(numeroStr)
                .append(") a Decimal ---\n");
        pasos.append("Fórmula de Expansión Polinómica: Σ (dígito × ")
                .append(base)
                .append("^posición)\n");

        long sumaTotal = 0;
        int longitud = numeroStr.length();

        for (int i = 0; i < longitud; i++) {
            char caracterActual = numeroStr.charAt(i);
            int valorDigito = obtenerValorDigito(caracterActual);
            int exponente = (longitud - 1) - i;
            long potencia = calcularPotencia(base, exponente);
            long termino = valorDigito * potencia;

            pasos.append("Posición ")
                    .append(exponente)
                    .append(": '")
                    .append(caracterActual)
                    .append("' (")
                    .append(valorDigito)
                    .append(") × ")
                    .append(base)
                    .append("^")
                    .append(exponente)
                    .append(" = ")
                    .append(valorDigito)
                    .append(" × ")
                    .append(potencia)
                    .append(" = ")
                    .append(termino)
                    .append("\n");

            sumaTotal += termino;
        }

        pasos.append("Suma Total en Decimal = ")
                .append(sumaTotal)
                .append("\n\n");
        return sumaTotal;
    }

    // Método auxiliar manual para invertir un String sin usar librerías
    protected String invertirCadena(String texto) {
        String invertida = "";
        for (int i = texto.length() - 1; i >= 0; i--) {
            invertida = invertida + texto.charAt(i);
        }
        return invertida;
    }

    // Método auxiliar manual para convertir un carácter ('0'-'F') a su valor entero (0-15)
    protected int obtenerValorDigito(char c) {
        for (int i = 0; i < DIGITOS.length(); i++) {
            if (DIGITOS.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    // Método auxiliar manual para calcular base^exponente sin Math.pow
    protected long calcularPotencia(int base, int exponente) {
        long resultado = 1;
        for (int i = 0; i < exponente; i++) {
            resultado = resultado * base;
        }
        return resultado;
    }
}