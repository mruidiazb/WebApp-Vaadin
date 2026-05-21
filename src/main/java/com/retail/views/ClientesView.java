package com.example.views;

import com.example.model.Cliente;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "clientes", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Clientes | Sistema Gestión")
public class ClientesView extends HorizontalLayout {

    private final MockDataService service;
    private final Grid<Cliente> grid = new Grid<>(Cliente.class, false);
    private final Binder<Cliente> binder = new Binder<>(Cliente.class);

    private final TextField filterText = new TextField();
    
    // Form fields
    private final TextField idField = new TextField("ID (Autogenerado)");
    private final TextField nombreField = new TextField("Nombre Completo");
    private final TextField emailField = new TextField("Correo Electrónico");
    private final TextField telefonoField = new TextField("Teléfono");
    private final TextField empresaField = new TextField("Empresa");

    private Cliente selectedCliente;

    public ClientesView(MockDataService service) {
        this.service = service;
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        // Left Panel (Grid and Top Filters)
        VerticalLayout listPanel = new VerticalLayout();
        listPanel.setSizeFull();
        listPanel.setPadding(false);

        // Filter config
        filterText.setPlaceholder("Buscar por nombre o empresa...");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setWidth("300px");
        filterText.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.setWidthFull();

        // Grid config
        grid.addColumn(Cliente::getId).setHeader("ID").setAutoWidth(true).setSortable(true);
        grid.addColumn(Cliente::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        grid.addColumn(Cliente::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Cliente::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Cliente::getEmpresa).setHeader("Empresa").setAutoWidth(true).setSortable(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> selectCliente(event.getValue()));

        listPanel.add(toolbar, grid);

        // Right Panel (Form Layout & CRUD Operations)
        VerticalLayout formPanel = new VerticalLayout();
        formPanel.setWidth("380px");
        formPanel.setPadding(true);
        formPanel.addClassName("form-panel");

        H3 formTitle = new H3("Datos del Cliente");
        formTitle.getStyle().set("margin-top", "0");

        // Fields config
        idField.setReadOnly(true);
        idField.getStyle().set("opacity", "0.7");
        nombreField.setRequired(true);
        emailField.setRequired(true);

        // Bind fields manually to prevent type/reflection issues
        binder.bind(nombreField, Cliente::getNombre, Cliente::setNombre);
        binder.bind(emailField, Cliente::getEmail, Cliente::setEmail);
        binder.bind(telefonoField, Cliente::getTelefono, Cliente::setTelefono);
        binder.bind(empresaField, Cliente::getEmpresa, Cliente::setEmpresa);

        FormLayout form = new FormLayout();
        form.add(idField, nombreField, emailField, telefonoField, empresaField);

        // Buttons requested by User: Nuevo, Consultar Uno, Consultar Todos, Actualizar Cliente, Eliminar
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

        Button btnActualizar = new Button("Actualizar Cliente", new Icon(VaadinIcon.EDIT));
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
        grid.setItems(service.getClientes(filterText.getValue()));
    }

    private void selectCliente(Cliente cliente) {
        if (cliente == null) {
            clearFormForNew();
        } else {
            selectedCliente = cliente;
            binder.readBean(selectedCliente);
            idField.setValue(cliente.getId() != null ? String.valueOf(cliente.getId()) : "");
        }
    }

    private void clearFormForNew() {
        selectedCliente = new Cliente();
        binder.readBean(selectedCliente);
        idField.setValue("");
        grid.asSingleSelect().clear();
    }

    private void saveOrUpdate() {
        if (selectedCliente == null) {
            Notification.show("Selecciona un cliente o presiona 'Nuevo'").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            binder.writeBean(selectedCliente);
            service.saveCliente(selectedCliente);
            refreshGrid();
            
            String msg = (selectedCliente.getId() == null) ? "Cliente creado con éxito" : "Cliente actualizado con éxito";
            Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearFormForNew();
        } catch (Exception e) {
            Notification.show("Corrige los errores del formulario").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showEliminarConfirmDialog() {
        if (selectedCliente == null || selectedCliente.getId() == null) {
            Notification.show("Selecciona un cliente de la lista para eliminar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Eliminación");
        confirmDialog.add(new Span("¿Está seguro de que desea eliminar al cliente '" + selectedCliente.getNombre() + "'?"));

        Button btnConfirm = new Button("Confirmar", new Icon(VaadinIcon.CHECK), e -> {
            service.deleteCliente(selectedCliente.getId());
            refreshGrid();
            clearFormForNew();
            confirmDialog.close();
            Notification.show("Cliente eliminado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        btnConfirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnCancel = new Button("Cancelar", e -> confirmDialog.close());

        confirmDialog.getFooter().add(btnCancel, btnConfirm);
        confirmDialog.open();
    }

    private void showConsultarUnoDialog() {
        Dialog queryDialog = new Dialog();
        queryDialog.setHeaderTitle("Consultar Cliente por ID");

        TextField idQueryField = new TextField("Ingrese el ID del Cliente");
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
                    service.getCliente(id).ifPresentOrElse(
                        cliente -> {
                            grid.select(cliente);
                            selectCliente(cliente);
                            queryDialog.close();
                            Notification.show("Cliente encontrado").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        },
                        () -> Notification.show("Cliente no encontrado").addThemeVariants(NotificationVariant.LUMO_ERROR)
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
