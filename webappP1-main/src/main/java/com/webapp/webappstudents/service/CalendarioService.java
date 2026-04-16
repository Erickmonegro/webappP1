package com.webapp.webappstudents.service;

import com.webapp.webappstudents.model.DiaCalendario;
import com.webapp.webappstudents.model.Tarea;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarioService {

    public List<DiaCalendario> generarCalendarioActual(List<Tarea> todasLasTareas) {
        List<DiaCalendario> dias = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);

        // 1. Primer día del mes y cuántos días tiene
        LocalDate primerDia = mesActual.atDay(1);
        int totalDias = mesActual.lengthOfMonth();

        // 2. Calcular huecos al principio (Si empieza en Martes, necesitamos 1 hueco)
        // Monday = 1, Sunday = 7. Restamos 1 para que Lunes sea 0 huecos.
        int huecosIniciales = primerDia.getDayOfWeek().getValue() - 1;

        // Añadir días vacíos
        for (int i = 0; i < huecosIniciales; i++) {
            dias.add(null);
        }

        // 3. Añadir los días reales y asignarles sus tareas
        for (int diaNum = 1; diaNum <= totalDias; diaNum++) {
            DiaCalendario diaObj = new DiaCalendario(diaNum, true);
            LocalDate fechaDia = mesActual.atDay(diaNum);

            // Buscar si hay tareas para esta fecha exacta
            for (Tarea t : todasLasTareas) {
                if (t.getFechaVencimiento() != null && t.getFechaVencimiento().equals(fechaDia)) {
                    diaObj.addTarea(t);
                }
            }
            dias.add(diaObj);
        }

        return dias;
    }
}