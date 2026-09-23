package com.academy.cursos.config;

import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.ConfigPago;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.LineaInvestigacion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.model.enums.Rol;
import com.academy.cursos.model.enums.TipoDocumento;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.ConfigPagoRepository;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.repository.LineaInvestigacionRepository;
import com.academy.cursos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LineaInvestigacionRepository lineaRepo;
    private final AreaInvestigacionRepository areaRepo;
    private final UsuarioRepository usuarioRepo;
    private final ConfigPagoRepository configPagoRepo;
    private final CursoRepository cursoRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial.email:admin@iiiccd.edu.pe}")
    private String adminEmail;

    @Value("${app.admin.initial.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.initial.name:Administrador IIICCD}")
    private String adminName;

    public DataInitializer(
            LineaInvestigacionRepository lineaRepo,
            AreaInvestigacionRepository areaRepo,
            UsuarioRepository usuarioRepo,
            ConfigPagoRepository configPagoRepo,
            CursoRepository cursoRepo,
            PasswordEncoder passwordEncoder) {
        this.lineaRepo = lineaRepo;
        this.areaRepo = areaRepo;
        this.usuarioRepo = usuarioRepo;
        this.configPagoRepo = configPagoRepo;
        this.cursoRepo = cursoRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initLineasYAreas();
        initAdmin();
        initParticipanteDemo();
        initConfigPago();
        initCursosDemo();
    }

    private void initLineasYAreas() {
        if (lineaRepo.count() == 0) {
            // 1. Ciencias de la Computación
            LineaInvestigacion cc = new LineaInvestigacion();
            cc.setNombre("Ciencias de la Computación");
            cc.setDescripcion("Estudio de fundamentos teóricos de la información y cómputo, así como técnicas para su implementación en sistemas computacionales.");
            cc.setOrden(1);
            cc = lineaRepo.save(cc);

            crearArea(cc, "Inteligencia Artificial", "Diseño de modelos capaces de resolver problemas complejos, razonar y aprender autónomamente.");
            crearArea(cc, "Computación de Alto Desempeño", "Procesamiento paralelo y distribuido para optimizar grandes cargas de trabajo computacional.");
            crearArea(cc, "Desarrollo de Algoritmos Complejos", "Diseño y análisis de estructuras y algoritmos eficientes para problemas desafiantes.");
            crearArea(cc, "Procesamiento de Imágenes", "Análisis, transformación y comprensión automatizada de datos visuales y espaciales.");

            // 2. Ciencia de Datos
            LineaInvestigacion cd = new LineaInvestigacion();
            cd.setNombre("Ciencia de Datos");
            cd.setDescripcion("Extracción sistemática de conocimiento útil a partir de grandes volúmenes de datos heterogéneos.");
            cd.setOrden(2);
            cd = lineaRepo.save(cd);

            crearArea(cd, "Aprendizaje Automático", "Modelos estadísticos y de deep learning para el descubrimiento de patrones.");
            crearArea(cd, "Ingeniería y Analítica de Datos", "Arquitecturas de almacenamiento, canalizaciones ETL y visualización predictiva.");
            crearArea(cd, "Modelos Estocásticos Avanzados", "Simulación probabilística y análisis de incertidumbre en fenómenos complejos.");
            crearArea(cd, "Estadística Bayesiana", "Inferencia basada en probabilidades subjetivas y actualización continua de evidencia.");

            // 3. Investigación
            LineaInvestigacion inv = new LineaInvestigacion();
            inv.setNombre("Investigación");
            inv.setDescripcion("Metodologías y marcos cuantitativos para el desarrollo riguroso de proyectos de ciencia e innovación tecnológica.");
            inv.setOrden(3);
            inv = lineaRepo.save(inv);

            crearArea(inv, "Estadística Aplicada a la Investigación", "Herramientas cuantitativas para el contraste de hipótesis y análisis experimental.");
            crearArea(inv, "Investigación e Innovación Tecnológica", "Transferencia de conocimiento a prototipos, patentes y soluciones productivas.");
            crearArea(inv, "Metodologías de la Investigación", "Estructuración de tesis, artículos científicos y diseño de experimentos.");
        }
    }

    private void crearArea(LineaInvestigacion linea, String nombre, String desc) {
        AreaInvestigacion area = new AreaInvestigacion();
        area.setLineaInvestigacion(linea);
        area.setNombre(nombre);
        area.setDescripcion(desc);
        areaRepo.save(area);
    }

    private void initAdmin() {
        if (!usuarioRepo.existsByCorreo(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setNombreCompleto(adminName);
            admin.setCorreo(adminEmail);
            admin.setNumeroDocumento("00000000");
            admin.setTipoDocumento(TipoDocumento.DNI);
            admin.setTelefono("951000000");
            admin.setInstitucionProcedencia("IIICCD - UNA Puno");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            usuarioRepo.save(admin);
        }
    }

    private void initParticipanteDemo() {
        String emailDemo = "participante@iiiccd.edu.pe";
        if (!usuarioRepo.existsByCorreo(emailDemo)) {
            Usuario demo = new Usuario();
            demo.setNombreCompleto("Carlos Condori Mamani");
            demo.setCorreo(emailDemo);
            demo.setNumeroDocumento("70123456");
            demo.setTipoDocumento(TipoDocumento.DNI);
            demo.setTelefono("951987654");
            demo.setInstitucionProcedencia("Universidad Nacional del Altiplano");
            demo.setPasswordHash(passwordEncoder.encode("participante123"));
            demo.setRol(Rol.PARTICIPANTE);
            demo.setActivo(true);
            usuarioRepo.save(demo);
        }
    }

    private void initConfigPago() {
        if (configPagoRepo.count() == 0) {
            ConfigPago config = new ConfigPago();
            config.setNombreMetodo("Yape");
            config.setNumeroYape("951234567");
            config.setTitularYape("Leonid Alemán Gonzales (Director IIICCD)");
            config.setQrImagenUrl("/images/yape-qr-default.png");
            config.setInstrucciones("1. Realiza el pago por Yape al número o QR indicado.\n" +
                    "2. Guarda la captura de pantalla o comprobante digital.\n" +
                    "3. Sube la imagen y digita el número de operación en el formulario.");
            config.setActivo(true);
            configPagoRepo.save(config);
        }
    }

    private void initCursosDemo() {
        if (cursoRepo.count() == 0) {
            Optional<AreaInvestigacion> areaIA = areaRepo.findAll().stream()
                    .filter(a -> a.getNombre().equalsIgnoreCase("Inteligencia Artificial"))
                    .findFirst();

            Optional<AreaInvestigacion> areaML = areaRepo.findAll().stream()
                    .filter(a -> a.getNombre().equalsIgnoreCase("Aprendizaje Automático"))
                    .findFirst();

            Optional<AreaInvestigacion> areaEst = areaRepo.findAll().stream()
                    .filter(a -> a.getNombre().equalsIgnoreCase("Estadística Aplicada a la Investigación"))
                    .findFirst();

            Optional<AreaInvestigacion> areaImagenes = areaRepo.findAll().stream()
                    .filter(a -> a.getNombre().equalsIgnoreCase("Procesamiento de Imágenes"))
                    .findFirst();

            if (areaIA.isPresent()) {
                Curso c1 = new Curso();
                c1.setNombre("Fundamentos y Aplicaciones de Inteligencia Artificial");
                c1.setDescripcionCorta("Domina los conceptos clave de la IA, agentes inteligentes y algoritmos de búsqueda.");
                c1.setDescripcion("Curso integral diseñado para estudiantes e investigadores interesados en los fundamentos teóricos y prácticos de la Inteligencia Artificial.\n\n" +
                        "Temario:\n" +
                        "- Introducción a los agentes autónomos.\n" +
                        "- Búsqueda heurística y algoritmos genéticos.\n" +
                        "- Razonamiento bajo incertidumbre.\n" +
                        "- Introducción al procesamiento de lenguaje natural y visión computacional.");
                c1.setAreaInvestigacion(areaIA.get());
                c1.setDocenteResponsable("Dr. Leonid Alemán Gonzales");
                c1.setDuracion("48 horas académicas");
                c1.setPrecio(BigDecimal.valueOf(150.00));
                c1.setCuposTotales(30);
                c1.setCuposDisponibles(30);
                c1.setEstado(EstadoCurso.PUBLICADO);
                c1.setFechaInicio(LocalDate.now().plusDays(10));
                c1.setFechaFin(LocalDate.now().plusDays(40));
                c1.setEnlaceClase("https://meet.google.com/iiiccd-ia-2026");
                cursoRepo.save(c1);
            }

            if (areaML.isPresent()) {
                Curso c2 = new Curso();
                c2.setNombre("Machine Learning & Deep Learning con Python y PyTorch");
                c2.setDescripcionCorta("Aprende a entrenar modelos predictivos y redes neuronales profundas con casos reales.");
                c2.setDescripcion("Especialización práctica enfocada en el desarrollo y despliegue de modelos de Machine Learning y Deep Learning.\n\n" +
                        "Temario:\n" +
                        "- Regresión lineal, logística y árboles de decisión con Scikit-Learn.\n" +
                        "- Redes neuronales convolucionales (CNN) y transformadores.\n" +
                        "- Optimización de hiperparámetros y métricas de evaluación.\n" +
                        "- Despliegue de modelos como microservicios.");
                c2.setAreaInvestigacion(areaML.get());
                c2.setDocenteResponsable("Mg. Ángel Javier Quispe Carita");
                c2.setDuracion("60 horas académicas");
                c2.setPrecio(BigDecimal.valueOf(180.00));
                c2.setCuposTotales(25);
                c2.setCuposDisponibles(25);
                c2.setEstado(EstadoCurso.PUBLICADO);
                c2.setFechaInicio(LocalDate.now().plusDays(15));
                c2.setFechaFin(LocalDate.now().plusDays(55));
                c2.setEnlaceClase("https://meet.google.com/iiiccd-ml-2026");
                cursoRepo.save(c2);
            }

            if (areaEst.isPresent()) {
                Curso c3 = new Curso();
                c3.setNombre("Estadística Aplicada a la Investigación Científica con R");
                c3.setDescripcionCorta("Herramientas estadísticas rigurosas para pruebas de hipótesis, contrastes y redacción de tesis.");
                c3.setDescripcion("Programa metodológico y analítico para investigadores y tesistas que buscan rigor cuantitativo en sus publicaciones científicas.\n\n" +
                        "Temario:\n" +
                        "- Análisis exploratorio de datos y visualización con ggplot2.\n" +
                        "- Pruebas paramétricas y no paramétricas.\n" +
                        "- Modelos lineales generalizados y análisis multivariado.\n" +
                        "- Interpretación de resultados para artículos Scopus/WoS.");
                c3.setAreaInvestigacion(areaEst.get());
                c3.setDocenteResponsable("Mg. Renzo Apaza Cutipa");
                c3.setDuracion("40 horas académicas");
                c3.setPrecio(BigDecimal.valueOf(120.00));
                c3.setCuposTotales(35);
                c3.setCuposDisponibles(35);
                c3.setEstado(EstadoCurso.PUBLICADO);
                c3.setFechaInicio(LocalDate.now().plusDays(12));
                c3.setFechaFin(LocalDate.now().plusDays(42));
                c3.setEnlaceClase("https://meet.google.com/iiiccd-est-2026");
                cursoRepo.save(c3);
            }

            if (areaImagenes.isPresent()) {
                Curso c4 = new Curso();
                c4.setNombre("Visión Artificial y Procesamiento Digital de Imágenes");
                c4.setDescripcionCorta("Técnicas avanzadas para detección de objetos, segmentación y reconocimiento facial.");
                c4.setDescripcion("Capacitación especializada en algoritmos de visión computacional y análisis morfológico de imágenes satelitales y médicas.\n\n" +
                        "Temario:\n" +
                        "- Filtrado espacial y transformadas en el dominio frecuencial.\n" +
                        "- Detección de bordes y descriptores locales (SIFT, ORB).\n" +
                        "- Detección de objetos con arquitecturas YOLO.\n" +
                        "- Segmentación semántica e instancias.");
                c4.setAreaInvestigacion(areaImagenes.get());
                c4.setDocenteResponsable("Ing. Roberto Elvis Roque Claros");
                c4.setDuracion("45 horas académicas");
                c4.setPrecio(BigDecimal.valueOf(160.00));
                c4.setCuposTotales(20);
                c4.setCuposDisponibles(20);
                c4.setEstado(EstadoCurso.PUBLICADO);
                c4.setFechaInicio(LocalDate.now().plusDays(20));
                c4.setFechaFin(LocalDate.now().plusDays(60));
                c4.setEnlaceClase("https://meet.google.com/iiiccd-vision-2026");
                cursoRepo.save(c4);
            }
        }
    }
}
