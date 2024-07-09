package com.tuempresa.easystay.run;

import org.openxava.util.*;

/**
 * Ejecuta esta clase para arrancar la aplicación.
 *
 * Con OpenXava Studio/Eclipse: Botón derecho del ratón > Run As > Java Application
 */

public class easystay {

	public static void main(String[] args) throws Exception {
		DBServer.start("easystay-dbF1"); // Para usar tu propia base de datos comenta esta línea y configura src/main/webapp/META-INF/context.xml
		AppServer.run("easystay"); // Usa AppServer.run("") para funcionar en el contexto raíz
	}

}
