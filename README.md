# Identificador De Cambios

Aplicación de consola para identificar las ocurrencias de cambios en cadenas obtenidas a partir de una aplicación de reglas de corrección de software externo.

## Funcionamiento

El método encargado de la indentificación de cambios recibe como parametros:
- La cadena original.
- La cadena resultante, transformada previamente por una aplicación externa.

### Salida

Por defecto la aplicación realizará un análisis en masa a todas las cadenas de prueba establecidas en en la clase TestValidator y presenta una salida similar a:
```
------VALID TRANSFORMATION!------
Original: tres vehículos el sesenta por|
Expected: tres vehículos el sesenta por|
Obtained: tres vehículos el sesenta por|

All 2132 tests passed!
Maximum transformations: 4

---TRANSFORMATION STATS---
0 transformations: 1962 tests.
1 transformation: 157 tests.
2 transformations: 12 tests.
3 transformations: 0 tests.
4 transformations: 1 tests.
```
Adicionalmente se pueden habilitar tests individuales donde se detallen las ocurrencias de transformaciones en cada par de cadenas.
En caso de haber detectado algún cambio, la salida de tests individuales tendrá la estructura:
```
  ( IIO (Integer), IFO (Integer), "TT" (String) )
```
donde:
- IIO: Índice inicial de cambio en cadena original.
- IFO: Índice final de cambio en cadena original.
- TTT: Texto transformado detectado en cadena resultante.

Si se tienen como entrada:
```
  Cadena original: "Las veinte. por ciento. Los-los diez casas casos siglo diez"
  Cadena Transformada: "Las 20%. Los-los 10 casas casos X"
```
La salida producida será:
```
  ( 4 , 22 , "20%." )
  ( 32 , 35 , "10" )
  ( 49 , 59 , "X" )
```
## Limitaciones actuales

- Múltiples transformaciones identificadas consecutivamente se muestran como un único cambio.
- En caso de un conjunto de palbras consecutivas transformados en múltiple números:
```
  Cadena original:     "Quinientos cincuenta y cinco noventa y dos veinte números"
  Cadena transformada: "555 92 20 números
```
  solo es posible identificar la palabra inicial asociada al primer número ("Quininetos") y la palabra final asociada al último número ("veinte").
