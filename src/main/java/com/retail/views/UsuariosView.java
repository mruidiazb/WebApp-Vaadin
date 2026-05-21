package com.example.views;

import com.example.model.Usuario;
import com.example.service.MockDataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Usuarios | Sistema Gestión")
public class UsuariosView extends HorizontalLayout {

    private final MockDataService service;
    private final Grid<Usuario> grid = new Grid<>(Usuario.class, false);
    private final Binder<Usuario> binder = new Binder<>(Usuario.class);

    private final TextField filterText = new TextField();
    
    // Form fields
    private final TextField idField = new TextField("ID (Autogenerado)");
    private final TextField nombreField = new TextField("Nombre Completo");
    private final TextField usernameField = new TextField("Nombre de Usuario (Login)");
    private final Select<String> rolField = new Select<>();

    private Usuario selectedUsuario;

    public UsuariosView(MockDataService service) {
        this.service = service;
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        // Left Panel (Grid and Top Filters)
        VerticalLayout listPanel = new VerticalLayout();
        listPanel.setSizeFull();
        listPanel.setPadding(false);

        // Filter config
        filterText.setPlaceholder("Buscar por nombre o username...");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setWidth("300px");
        filterText.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.setWidthFull();

        // Grid config
        grid.addColumn(Usuario::getId).setHeader("ID").setAutoWidth(true).setSortable(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        grid.addColumn(Usuario::getUsername).setHeader("Username").setAutoWidth(true).setSortable(true);
        grid.addColumn(Usuario::getRol).setHeader("Rol").setAutoWidth(true).setSortable(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> selectUsuario(event.getValue()));

        listPanel.add(toolbar, grid);

        // Right Panel (Form Layout & CRUD Operations)
        VerticalLayout formPanel = new VerticalLayout();
        formPanel.setWidth("380px");
        formPanel.setPadding(true);
        formPanel.addClassName("form-panel");

        H3 formTitle = new H3("Datos del Usuario");
        formTitle.getStyle().set("margin-top", "0");

        // Fields config
        idField.setReadOnly(true);
        idField.getStyle().set("opacity", "0.7");
        nombreField.setRequired(true);
        usernameField.setRequired(true);
        
        rolField.setLabel("Rol");
        rolField.setItems("Administrador", "Editor", "Soporte");
        rolField.setValue("Editor");

        // Bind fields manually to prevent type/reflection issues
        binder.bind(nombreField, Usuario::getNombre, Usuario::setNombre);
        binder.bind(usernameField, Usuario::getUsername, Usuario::setUsername);
        binder.bind(rolField, Usuario::getRol, Usuario::setRol);

        FormLayout form = new FormLayout();
        form.add(idField, nombreField, usernameField, rolField);

        // Buttons: Nuevo, Consultar Uno, Consultar Todos, Actualizar Usuario, Eliminar
        Button btnNuevo = new Button("Nuevo", new Icon(VaadinIcon.PLUS));
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNuevo.setWidthFull();
        btnNuevo.addClickListener(e -> clearFormForNew());

        Button btnConsultarUno = new Button("Consultar Uno", new Icon(VaadinIcon.SEARCH));
        btnConsultarUno.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnConsultarUno.addClickListener(e -> showConsultarUnoDialog());

        Button btnConsultarTodos = new Button("Consultar Todos", new Icon(VaadinIcon.REFRESH));
        btnConsultarTodos.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        btnConsultarTodos.addClickListener(e -> {
            filterText.clear();
            refreshGrid();
            Notification.show("Registros recargados").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        HorizontalLayout queryRow = new HorizontalLayout(btnConsultarUno, btnConsultarTodos);
        queryRow.setWidthFull();
        btnConsultarUno.setWidthFull();
        btnConsultarTodos.setWidthFull();

        Button btnActualizar = new Button("Actualizar Usuario", new Icon(VaadinIcon.EDIT));
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnActualizar.setWidthFull();
        btnActualizar.addClickListener(e -> saveOrUpdate());

        Button btnEliminar = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnEliminar.setWidthFull();
        btnEliminar.addClickListener(e -> showEliminarConfirmDialog());

        formPanel.add(formTitle, form, btnNuevo, queryRow, btnActualizar, btnEliminar);

        add(listPanel, formPanel);

        // Initial Load
        refreshGrid();
        clearFormForNew();
    }

    private void refreshGrid() {
        grid.setItems(service.getUsuarios(filterText.getValue()));
    }

    private void selectUsuario(Usuario usuario) {
        if (usuario == null) {
            clearFormForNew();
        } else {
            selectedUsuario = usuario;
            binder.readBean(selectedUsuario);
            idField.setValue(usuario.getId() != null ? String.valueOf(usuario.getId()) : "");
        }
    }

    private void clearFormForNew() {
        selectedUsuario = new Usuario();
        binder.readBean(selectedUsuario);
        idField.setValue("");
        grid.asSingleSelect().clear();
    }

    private void saveOrUpdate() {
        if (selectedUsuario == null) {
            Notification.show("Selecciona un usuario o presiona 'Nuevo'").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            binder.writeBean(selectedUsuario);
            service.saveUsuario(selectedUsuario);
            refreshGrid();
            
            String msg = (selectedUsuario.getId() == null) ? "Usuario creado con éxito" : "Usuario actualizado con éxito";
            Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearFormForNew();
        } catch (Exception e) {
            Notification.show("Corrige los errores del formulario").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showEliminarConfirmDialog() {
        if (selectedUsuario == null || selectedUsuario.getId() == null) {
            Notification.show("Selecciona un usuario de la lista para eliminar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Eliminación");
        confirmDialog.add(new Span("¿Está seguro de que desea eliminar al usuario '" + selectedUsuario.getNombre() + "'?"));

        Button btnConfirm = new Button("Confirmar", new Icon(VaadinIcon.CHECK), e -> {
            service.deleteUsuario(selectedUsuario.getId());
            refreshGrid();
            clearFormForNew();
            confirmDialog.close();
            Notification.show("Usuario eliminado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        btnConfirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnCancel = new Button("Cancelar", e -> confirmDialog.close());

        confirmDialog.getFooter().add(btnCancel, btnConfirm);
        confirmDialog.open();
    }

    private void showConsultarUnoDialog() {
        Dialog queryDialog = new Dialog();
        queryDialog.setHeaderTitle("Consultar Usuario por ID");

        TextField idQueryField = new TextField("Ingrese el ID del Usuario");
        idQueryField.setPlaceholder("Ej: 1");
        idQueryField.setRequired(true);

        VerticalLayout layout = new VerticalLayout(idQueryField);
        layout.setPadding(true);
        queryDialog.add(layout);

        Button btnSearch = new Button("Buscar", new Icon(VaadinIcon.SEARCH), e -> {
            String val = idQueryField.getValue();
            if (val != null && !val.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(val);
                    service.getUsuario(id).ifPresentOrElse(
                        usuario -> {
                            grid.select(usuario);
                            selectUsuario(usuario);
                            queryDialog.close();
                            Notification.show("Usuario encontrado").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        },
                        () -> Notification.show("Usuario no encontrado").addThemeVariants(NotificationVariant.LUMO_ERROR)
                    );
                } catch (NumberFormatException nfe) {
                    Notification.show("El ID debe ser un número").addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Por favor, ingrese un ID").addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        btnSearch.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancel = new Button("Cancelar", e -> queryDialog.close());

        queryDialog.getFooter().add(btnCancel, btnSearch);
        queryDialog.open();
    }
}
