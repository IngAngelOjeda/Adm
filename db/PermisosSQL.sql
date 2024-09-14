
INSERT INTO public.permiso (descripcion,nombre) VALUES
	 ('Permite ver el agrupador o menú de Oee','ver:menu:oee'),
	 ('Permite ver el menú de Planes TIC','ver:menu:administracion:planestic'),
	 ('Permite editar los Planes TIC','planes:editar'),
	 ('Permite eliminar los Planes TIC','planes:borrar'),
	 ('Permite ver el menú de Dependencias','ver:menu:administracion:dependencias'),
	 ('Permite crear las informaciones de un servicio','servicioOee:servicioInformacion:crear'),
	 ('Permite eliminar las informaciones de un servicio','servicioOee:servicioInformacion:borrar'),
	 ('Mostrar Organigrama con funcionarios','organigrama:mostrarOrganigramaFuncionario');
INSERT INTO public.permiso (descripcion,nombre) VALUES
	 ('Permite ver el botón del detalle de auditoria','auditoria:detalle'),
	 ('Permite ver el menu de Datos OEE','ver:menu:administracion:datosOee'),
	 ('Permite que el usuario pueda modificar su perfil','usuarios:editarPerfil'),
	 ('Permite crear Nuevos Planes TIC','planes:crear'),
	 ('Permite listar los Planes TIC','planes:listar'),
	 ('Permite visualizar Organigrama TIC','ver:menu:administracion:organigramatic'),
	 ('Permite crear nuevo dato de Organigrama','organigrama:crear'),
	 ('Permite listar los datos del Organigrama','organigrama:listar'),
	 ('Permite editar dato de Organigrama','organigrama:editar'),
	 ('Permite ver todos los datos de la tabla sin importar la OEE','PermisoAdmin');
INSERT INTO public.permiso (descripcion,nombre) VALUES
	 ('Permite descargar Documento PDF de Dependencias','dependencia:downloadReport'),
	 ('Permite descargar Documento PDF de Datos OEE','datosOee:descargar'),
	 ('Permite descargar Documento PDF de OEE','oee:downloadReport'),
	 ('Editar','dependencia:pemisoLibre'),
	 ('Permite descargar Documento PDF de Funcionarios (Organigrama)','organigrama:downloadReport'),
	 ('Permite visualizar el boton Ver Organigrama','organigrama:ver'),
	 ('Permite listar datos de la tabla Datos Oee','datosOee:listar'),
	 ('Permite editar Datos del Oee','datosOee:editar'),
	 ('Permite visualizar el boton Ver Organigrama','dependencia:ver');
INSERT INTO public.permiso (descripcion,nombre) VALUES
	 ('Permite listar los datos de las Dependencias','dependencia:listar'),
	 ('Permite crear nuevas Dependencias','dependencia:crear'),
	 ('Permite editar las Dependencias','dependencia:editar'),
	 ('Permite obtener Dependencias por parametros','dependencia:obtenerPorParametros'),
	 ('Ver Organigrama Dependencia','dependencia:getShowDependencia');
