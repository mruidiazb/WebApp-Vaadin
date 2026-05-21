package com.example.views;

import com.example.model.Factura;
import com.example.service.MockDataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Route(value = "facturas", layout = MainLayout.class)
@PageTitle("Facturas | Sistema Gestión")
public class FacturasView extends HorizontalLayout {

    private final MockDataService service;
    private final Grid<Factura> grid = new Grid<>(Factura.class, false);
    private final Binder<Factura> binder = new Binder<>(Factura.class);

    private final TextField filterText = new TextField();
    
    // Form fields
    private final TextField idField = new TextField("ID (Autogenerado)");
    private final TextField clienteField = new TextField("Cliente");
    private final DatePicker fechaField = new DatePicker("Fecha");
    private final NumberField totalField = new NumberField("Total ($)");
    private final Select<String> estadoField = new Select<>();

    private Factura selectedFactura;

    public FacturasView(MockDataService service) {
        this.service = service;
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        // Left Panel (Grid and Top Filters)
        VerticalLayout listPanel = new VerticalLayout();
        listPanel.setSizeFull();
        listPanel.setPadding(false);

        // Filter config
        filterText.setPlaceholder("Buscar por cliente o estado...");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setWidth("300px");
        filterText.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.setWidthFull();

        // Grid config
        grid.addColumn(Factura::getId).setHeader("ID").setAutoWidth(true).setSortable(true);
        grid.addColumn(Factura::getCliente).setHeader("Cliente").setAutoWidth(true).setSortable(true);
        grid.addColumn(f -> f.getFecha() != null ? f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "").setHeader("Fecha").setAutoWidth(true).setSortable(true);
        grid.addColumn(f -> String.format("$%.2f", f.getTotal())).setHeader("Total").setAutoWidth(true).setSortable(true);
        
        // Custom styling helper for Estado column
        grid.addComponentColumn(f -> {
            Span badge = new Span(f.getEstado());
            if ("Pagada".equals(f.getEstado())) {
                badge.getElement().getThemeList().add("badge success");
            } else {
                badge.getElement().getThemeList().add("badge error");
            }
            return badge;
        }).setHeader("Estado").setAutoWidth(true).setSortable(true);

        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> selectFactura(event.getValue()));

        listPanel.add(toolbar, grid);

        // Right Panel (Form Layout & CRUD Operations)
        VerticalLayout formPanel = new VerticalLayout();
        formPanel.setWidth("380px");
        formPanel.setPadding(true);
        formPanel.addClassName("form-panel");

        H3 formTitle = new H3("Datos de la Factura");
        formTitle.getStyle().set("margin-top", "0");

        // Fields config
        idField.setReadOnly(true);
        idField.getStyle().set("opacity", "0.7");
        clienteField.setRequired(true);
        fechaField.setRequired(true);
        totalField.setMin(0.0);
        
        estadoField.setLabel("Estado");
        estadoField.setItems("Pagada", "Pendiente");
        estadoField.setValue("Pendiente");

        // Bind fields manually to prevent type/reflection issues
        binder.bind(clienteField, Factura::getCliente, Factura::setCliente);
        binder.bind(fechaField, Factura::getFecha, Factura::setFecha);
        binder.bind(totalField, Factura::getTotal, Factura::setTotal);
        binder.bind(estadoField, Factura::getEstado, Factura::setEstado);

        FormLayout form = new FormLayout();
        form.add(idField, clienteField, fechaField, totalField, estadoField);

        // Buttons: Nuevo, Consultar Uno, Consultar Todos, Actualizar Factura, Eliminar
        Button btnNuevo = new Button("Nuevo", new Icon(VaadinIcon.PLUS));
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNuevo.setWidthFull();
        btnNuevo.addClickListener(e -> clearFormForNew());

        Button btnConsultarUno = new Button("Consultar Uno", new Icon(VaadinIcon.SEARCH));
        btnConsultarUno.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
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

        Button btnActualizar = new Button("Actualizar Factura", new Icon(VaadinIcon.EDIT));
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
        grid.setItems(service.getFacturas(filterText.getValue()));
    }

    private void selectFactura(Factura factura) {
        if (factura == null) {
            clearFormForNew();
        } else {
            selectedFactura = factura;
            binder.readBean(selectedFactura);
            idField.setValue(factura.getId() != null ? String.valueOf(factura.getId()) : "");
        }
    }

    private void clearFormForNew() {
        selectedFactura = new Factura();
        selectedFactura.setFecha(LocalDate.now()); // Default date to today
        binder.readBean(selectedFactura);
        idField.setValue("");
        grid.asSingleSelect().clear();
    }

    private void saveOrUpdate() {
        if (selectedFactura == null) {
            Notification.show("Selecciona una factura o presiona 'Nuevo'").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            binder.writeBean(selectedFactura);
            service.saveFactura(selectedFactura);
            refreshGrid();
            
            String msg = (selectedFactura.getId() == null) ? "Factura creada con éxito" : "Factura actualizada con éxito";
            Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearFormForNew();
        } catch (Exception e) {
            Notification.show("Corrige los errores del formulario").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showEliminarConfirmDialog() {
        if (selectedFactura == null || selectedFactura.getId() == null) {
            Notification.show("Selecciona una factura de la lista para eliminar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Eliminación");
        confirmDialog.add(new Span("¿Está seguro de que desea eliminar la factura ID '" + selectedFactura.getId() + "'?"));

        Button btnConfirm = new Button("Confirmar", new Icon(VaadinIcon.CHECK), e -> {
            service.deleteFactura(selectedFactura.getId());
            refreshGrid();
            clearFormForNew();
            confirmDialog.close();
            Notification.show("Factura eliminada correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        btnConfirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnCancel = new Button("Cancelar", e -> confirmDialog.close());

        confirmDialog.getFooter().add(btnCancel, btnConfirm);
        confirmDialog.open();
    }

    private void showConsultarUnoDialog() {
        Dialog queryDialog = new Dialog();
        queryDialog.setHeaderTitle("Consultar Factura por ID");

        TextField idQueryField = new TextField("Ingrese el ID de la Factura");
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
                    service.getFactura(id).ifPresentOrElse(
                        factura -> {
                            grid.select(factura);
                            selectFactura(factura);
                            queryDialog.close();
                            Notification.show("Factura encontrada").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        },
                        () -> Notification.show("Factura no encontrada").addThemeVariants(NotificationVariant.LUMO_ERROR)
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
