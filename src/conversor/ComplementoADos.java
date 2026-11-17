package conversor;

public class ComplementoADos {

    private SistemaBinario sistemaBinario;

    public ComplementoADos() {
        this.sistemaBinario = new SistemaBinario();
    }

    /**
     * Verifica si un número decimal cabe en N bits con signo (Complemento a 2).
     * Rango permitido: [ -2^(N-1) , 2^(N-1) - 1 ]
     */
    public boolean verificarRango(long numero, int nBits) {
        long limiteInferior = -calcularPotenciaDos(nBits - 1);
        long limiteSuperior = calcularPotenciaDos(nBits - 1) - 1;
        return numero >= limiteInferior && numero <= limiteSuperior;
    }

    public long getLimiteInferior(int nBits) {
        return -calcularPotenciaDos(nBits - 1);
    }

    public long getLimiteSuperior(int nBits) {
        return calcularPotenciaDos(nBits - 1) - 1;
    }

    private long calcularPotenciaDos(int exponente) {
        long res = 1;
        for (int i = 0; i < exponente; i++) {
            res = res * 2;
        }
        return res;
    }

    /**
     * FASE 3 - PASO A: Convierte un número decimal (positivo o negativo) a Complemento a 2 en N bits.
     */
    public String convertirAComplementoDos(long numero, int nBits, StringBuilder pasos) {
        pasos.append("=== PASO A: Representación de ")
                .append(numero)
                .append(" en ")
                .append(nBits)
                .append(" bits (Ca2) ===\n");

        // 1. Verificar Overflow de rango
        if (!verificarRango(numero, nBits)) {
            pasos.append("ERROR DE OVERFLOW: El número ")
                    .append(numero)
                    .append(" excede el rango representable para ")
                    .append(nBits)
                    .append(" bits: [")
                    .append(getLimiteInferior(nBits))
                    .append(" a ")
                    .append(getLimiteSuperior(nBits))
                    .append("].\n");
            return null;
        }

        // 2. Caso Positivo o Cero
        if (numero >= 0) {
            pasos.append("1. Como el número es positivo (>= 0), convertimos a binario normal:\n");
            String binPuro = sistemaBinario.convertirDesdeDecimal(numero);
            pasos.append("   Binario sin relleno: ")
                    .append(binPuro).append("\n");
            String binRelleno = rellenarConCeros(binPuro, nBits);
            pasos.append("2. Rellenamos con ceros a la izquierda hasta ")
                    .append(nBits)
                    .append(" bits:\n");
            pasos.append("   Resultado Ca2: ")
                    .append(binRelleno)
                    .append("\n\n");
            return binRelleno;
        }
        // 3. Caso Negativo
        else {
            long valorAbsoluto = -numero;
            pasos.append("1. El número es negativo. Tomamos su valor absoluto: |").append(numero).append("| = ").append(valorAbsoluto).append("\n");

            String binAbs = sistemaBinario.convertirDesdeDecimal(valorAbsoluto);
            String binN = rellenarConCeros(binAbs, nBits);
            pasos.append("2. Convertimos ").append(valorAbsoluto).append(" a binario de ").append(nBits).append(" bits:\n");
            pasos.append("   |X| en binario: ").append(binN).append("\n");

            String c1 = obtenerComplementoUno(binN);
            pasos.append("3. Obtenemos el Complemento a Uno (C1) invirtiendo todos los bits (0->1, 1->0):\n");
            pasos.append("   C1:             ").append(c1).append("\n");

            String ca2 = sumarUnoBinario(c1);
            pasos.append("4. Sumamos 1 al C1 para obtener el Complemento a Dos (Ca2):\n");
            pasos.append("   C1 + 1 (Ca2):   ").append(ca2).append("\n\n");
            return ca2;
        }
    }

    /**
     * FASE 3 - PASO B: Verificación (Proceso inverso de Ca2 a Decimal).
     */
    public long revertirComplementoDosADecimal(String ca2, StringBuilder pasos) {
        pasos.append("=== PASO B: Verificación (Revertir de Ca2 '").append(ca2).append("' a Decimal) ===\n");
        char bitSigno = ca2.charAt(0);

        if (bitSigno == '0') {
            pasos.append("1. El bit más significativo (izquierda) es '0', por lo tanto el número es POSITIVO.\n");
            pasos.append("2. Aplicamos expansión polinómica directa:\n");
            StringBuilder pasosExp = new StringBuilder();
            long valor = sistemaBinario.convertirADecimalConPasos(ca2, pasosExp);
            pasos.append(pasosExp);
            pasos.append("Resultado Decimal Verificado: +").append(valor).append("\n");
            return valor;
        } else {
            pasos.append("1. El bit más significativo (izquierda) es '1', por lo tanto el número es NEGATIVO.\n");
            String c1 = obtenerComplementoUno(ca2);
            pasos.append("2. Invertimos los bits del Ca2 (Complemento a 1):\n");
            pasos.append("   Bits invertidos: ").append(c1).append("\n");

            String magnitudBinaria = sumarUnoBinario(c1);
            pasos.append("3. Sumamos 1 para obtener la magnitud absoluta en binario:\n");
            pasos.append("   Magnitud |X|:    ").append(magnitudBinaria).append("\n");

            StringBuilder pasosExp = new StringBuilder();
            long magnitudDecimal = sistemaBinario.convertirADecimalConPasos(magnitudBinaria, pasosExp);
            long resultadoFinal = -magnitudDecimal;
            pasos.append("4. Convertimos la magnitud a decimal (").append(magnitudDecimal).append(") y colocamos el signo negativo:\n");
            pasos.append("Resultado Decimal Verificado: ").append(resultadoFinal).append("\n");
            return resultadoFinal;
        }
    }

    /**
     * FASE 4 (AVANZADO): Suma y Resta en Binario usando Complemento a 2.
     */
    public String operarEnComplementoDos(long numA, long numB, boolean esResta, int nBits, StringBuilder pasos) {
        String operacionNombre = esResta ? "RESTA (A - B)" : "SUMA (A + B)";
        long segundoOperando = esResta ? -numB : numB;

        pasos.append("=================================================================\n");
        pasos.append("FASE 4: ").append(operacionNombre).append(" EN COMPLEMENTO A DOS (").append(nBits).append(" BITS)\n");
        pasos.append("=================================================================\n");

        if (esResta) {
            pasos.append("Nota: En Ca2, la resta A - B se realiza sumando A + (-B).\n");
            pasos.append("Operación transformada: (").append(numA).append(") + (").append(segundoOperando).append(")\n\n");
        }

        // Convertimos ambos operandos a Ca2 de N bits
        StringBuilder pasosOp = new StringBuilder();
        String binA = convertirAComplementoDos(numA, nBits, pasosOp);
        if (binA == null) {
            pasos.append("Error en Operando A: ").append(numA).append(" no cabe en ").append(nBits).append(" bits.\n");
            return null;
        }

        String binB = convertirAComplementoDos(segundoOperando, nBits, pasosOp);
        if (binB == null) {
            pasos.append("Error en Operando B: ").append(segundoOperando).append(" no cabe en ").append(nBits).append(" bits.\n");
            return null;
        }

        pasos.append("Representación en ").append(nBits).append(" bits:\n");
        pasos.append("  Operando 1 (").append(numA).append("): \t").append(binA).append("\n");
        pasos.append("+ Operando 2 (").append(segundoOperando).append("): \t").append(binB).append("\n");
        pasos.append("-----------------------------------------------------------------\n");

        // Realizamos la suma binaria bit a bit de derecha a izquierda
        String resultadoSuma = "";
        String filaAcarreos = "";
        int acarreo = 0;

        for (int i = nBits - 1; i >= 0; i--) {
            int bitA = binA.charAt(i) - '0';
            int bitB = binB.charAt(i) - '0';
            int sumaBits = bitA + bitB + acarreo;

            int bitResultado = sumaBits % 2;
            acarreo = sumaBits / 2;

            resultadoSuma = bitResultado + resultadoSuma;
            filaAcarreos = acarreo + filaAcarreos;
        }

        pasos.append("  Acarreos generados: \t").append(filaAcarreos).append(" (Acarreo final descartado: ").append(acarreo).append(")\n");
        pasos.append("  Resultado en Ca2:   \t").append(resultadoSuma).append("\n\n");

        // Verificación de Overflow Aritmético en Complemento a 2:
        // Ocurre si sumamos dos números del mismo signo y el resultado tiene signo opuesto.
        char signoA = binA.charAt(0);
        char signoB = binB.charAt(0);
        char signoRes = resultadoSuma.charAt(0);

        if (signoA == signoB && signoRes != signoA) {
            pasos.append("⚠️ ALERTA DE OVERFLOW ARITMÉTICO DETECTADO:\n");
            pasos.append("Ambos operandos tenían el mismo bit de signo ('").append(signoA)
                    .append("'), pero el resultado cambió su bit de signo a ('").append(signoRes).append("').\n");
            pasos.append("El resultado real (").append(numA + segundoOperando)
                    .append(") excede la capacidad de ").append(nBits).append(" bits.\n");
        } else {
            StringBuilder pasosVerif = new StringBuilder();
            long decimalCalculado = revertirComplementoDosADecimal(resultadoSuma, pasosVerif);
            pasos.append(pasosVerif);
            pasos.append("\n✔ Operación exitosa sin Overflow. Resultado Decimal: ").append(decimalCalculado).append("\n");
        }

        return resultadoSuma;
    }

    // Rellena con '0' a la izquierda hasta alcanzar nBits
    public String rellenarConCeros(String binario, int nBits) {
        String resultado = binario;
        while (resultado.length() < nBits) {
            resultado = "0" + resultado;
        }
        return resultado;
    }

    // Invierte los bits: '0' -> '1' y '1' -> '0'
    public String obtenerComplementoUno(String binario) {
        String invertido = "";
        for (int i = 0; i < binario.length(); i++) {
            if (binario.charAt(i) == '0') {
                invertido = invertido + "1";
            } else {
                invertido = invertido + "0";
            }
        }
        return invertido;
    }

    // Suma 1 a una cadena binaria manteniendo su longitud
    public String sumarUnoBinario(String binario) {
        String resultado = "";
        int acarreo = 1; // Empezamos sumando 1 en el bit menos significativo

        for (int i = binario.length() - 1; i >= 0; i--) {
            int bitActual = binario.charAt(i) - '0';
            int suma = bitActual + acarreo;

            int nuevoBit = suma % 2;
            acarreo = suma / 2;

            resultado = nuevoBit + resultado;
        }
        return resultado;
    }
}