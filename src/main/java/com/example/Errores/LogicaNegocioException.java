package com.example.Errores;

// DOCUMENTACIÓN USO DE IA: Usamos Claude Opus 5.5.
// Fue usado en carpeta Errores que tiene las clases LogicaNegocioException y AtrapadorErrores,
// porque no sabíamos cómo enviarle a la página un mensaje claro cuando fallaba alguna regla del negocio
// (por ejemplo, un celular ya registrado) sin usar un try/catch en cada controller, entonces nos recomento esto

//La IA nos explicó el manejo de errores con @RestControllerAdvice, @ExceptionHandler y ResponseEntity,
// explicando cada uno de ellos y nos dió el código para las dos clases. Nosotros elegimos los nombres de la carpeta,
//de las clases y de los métodos, lo escribimos y ya definimos los mensajes que verá el usuario.

// Lo qué aprendimos fue que si un error no se atrapa con try/catch, se va subiendo al método que lo llamo hasta que
// alguien lo atrape. Pero si la excepción hereda de RuntimeException no toca poner try/catch en todos los controllers.
// También que con @RestControllerAdvice todos los errores llegan a una sola clase, y que @ExceptionHandler sirve
// para decir qué hacer con cada tipo de error.

public class LogicaNegocioException extends RuntimeException {
    public LogicaNegocioException(String mensaje) {
        super(mensaje);
    }
}
