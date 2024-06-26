package co.edu.uniquindio.proyecto_final.proyecto_final.controller;

import co.edu.uniquindio.proyecto_final.proyecto_final.controller.service.IModelFactoryService;
import co.edu.uniquindio.proyecto_final.proyecto_final.exceptions.EmpleadoException;
import co.edu.uniquindio.proyecto_final.proyecto_final.mapping.dto.EmpleadoDto;
import co.edu.uniquindio.proyecto_final.proyecto_final.mapping.mappers.SgreMapper;
import co.edu.uniquindio.proyecto_final.proyecto_final.model.Empleado;
import co.edu.uniquindio.proyecto_final.proyecto_final.model.Sgre;
import co.edu.uniquindio.proyecto_final.proyecto_final.utils.Persistencia;
import co.edu.uniquindio.proyecto_final.proyecto_final.utils.SgreUtils;

import java.io.IOException;
import java.util.List;

public class ModelFactoryController implements IModelFactoryService {
    Sgre sgre;
    SgreMapper mapper = SgreMapper.INSTANCE;

    //------------------------------  Singleton ------------------------------------------------
    // Clase estatica oculta. Tan solo se instanciara el singleton una vez
    private static class SingletonHolder {
        private final static ModelFactoryController eINSTANCE = new ModelFactoryController();
    }

    // Método para obtener la instancia de nuestra clase
    public static ModelFactoryController getInstance() {
        return SingletonHolder.eINSTANCE;
    }

    public ModelFactoryController() {
        System.out.println("invocación clase singleton");
        cargarDatosBase();
        registrarAccionesSistema("Inicio de sesión", 1, "inicioSesion");
        /*

 // cargarDatosBase();
//        salvarDatosPrueba();

        //2. Cargar los datos de los archivos
//		cargarDatosDesdeArchivos();

        //3. Guardar y Cargar el recurso serializable binario
//		cargarResourceBinario();
//		guardarResourceBinario();

        //4. Guardar y Cargar el recurso serializable XML
//		guardarResourceXML();
        cargarResourceXML();

        //Siempre se debe verificar si la raiz del recurso es null

        if(banco == null){
            cargarDatosBase();
            guardarResourceXML();
        }
        registrarAccionesSistema("Inicio de sesión", 1, "inicioSesión");

         */
    }

    private void cargarDatosDesdeArchivos() {
        sgre = new Sgre();
        try {
            Persistencia.cargarDatosArchivos(sgre);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void salvarDatosPrueba() {
        try {
            Persistencia.guardarEmpleados(getSgre().getEmpleados());
            //Persistencia.guardarClientes(getSgre().getUsuarios());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarDatosBase() {
        sgre = SgreUtils.InicializarDatosEmpleados();
    }


    public Sgre getSgre() {
        return sgre;
    }

    public void setSgre(Sgre sgre) {
        this.sgre = sgre;
    }


    @Override
    public List<EmpleadoDto> obtenerEmpleados() {
        return  mapper.getEmpleadosDto(sgre.getEmpleados());
    }

    @Override
    public boolean agregarEmpleado(EmpleadoDto empleadoDto) {
        try{
            if(!sgre.verificarEmpleadoExistente(empleadoDto.id())) {
                Empleado empleado = mapper.empleadoDtoToEmpleado(empleadoDto);
                getSgre().agregarEmpleado(empleado);
                registrarAccionesSistema("Se agrego el empleado"+ empleado.getNombre(),1,"agregarEmpleado");
                guardarResourceXML();
                //
            }
            return true;
        }catch (EmpleadoException e){
            e.getMessage();
            return false;
        }
    }

    @Override
    public boolean eliminarEmpleado(String id) {
        boolean flagExiste = false;
        try {
            flagExiste = getSgre().eliminarEmpleado(id);
        } catch (EmpleadoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flagExiste;
    }

    @Override
    public boolean actualizarEmpleado(String id, EmpleadoDto empleadoDto) {
        try {
            Empleado empleado = mapper.empleadoDtoToEmpleado(empleadoDto);
            getSgre().actualizarEmpleado(id, empleado);
            return true;
        } catch (EmpleadoException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void cargarResourceXML() {
        sgre = Persistencia.cargarRecursosSgreXML();
    }

    private void guardarResourceXML() {
        Persistencia.guardarRecursoSgreXML(sgre);
    }

    private void cargarResourceBinario() {
        sgre = Persistencia.cargarRecursoSgreBinario();
    }

    private void guardarResourceBinario() {
        Persistencia.guardarRecursoSgreBinario(sgre);
    }

    public void registrarAccionesSistema(String mensaje, int nivel, String accion) {
        Persistencia.guardaRegistroLog(mensaje, nivel, accion);
    }
}
