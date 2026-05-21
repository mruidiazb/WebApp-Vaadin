package com.example.views;

import com.example.model.Producto;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "productos", layout = MainLayout.class)
@PageTitle("Productos | Sistema Gestión")
public class ProductosView extends HorizontalLayout {

    private final MockDataService service;
    private final Grid<Producto> grid = new Grid<>(Producto.class, false);
    private final Binder<Producto> binder = new Binder<>(Producto.class);

    private final TextField filterText = new TextField();
    
    // Form fields
    private final TextField idField = new TextField("ID (Autogenerado)");
    private final TextField nombreField = new TextField("Nombre del Producto");
    private final TextField categoriaField = new TextField("Categoría");
    private final NumberField precioField = new NumberField("Precio ($)");
    private final IntegerField stockField = new IntegerField("Stock/Inventario");

    private Producto selectedProducto;

    public ProductosView(MockDataService service) {
        this.service = service;
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        // Left Panel (Grid and Top Filters)
        VerticalLayout listPanel = new VerticalLayout();
        listPanel.setSizeFull();
        listPanel.setPadding(false);

        // Filter config
        filterText.setPlaceholder("Buscar por nombre o categoría...");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setWidth("300px");
        filterText.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.setWidthFull();

        // Grid config
        grid.addColumn(Producto::getId).setHeader("ID").setAutoWidth(true).setSortable(true);
        grid.addColumn(Producto::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        grid.addColumn(Producto::getCategoria).setHeader("Categoría").setAutoWidth(true).setSortable(true);
        grid.addColumn(p -> String.format("$%.2f", p.getPrecio())).setHeader("Precio").setAutoWidth(true).setSortable(true);
        grid.addColumn(Producto::getStock).setHeader("Stock").setAutoWidth(true).setSortable(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> selectProducto(event.getValue()));

        listPanel.add(toolbar, grid);

        // Right Panel (Form Layout & CRUD Operations)
        VerticalLayout formPanel = new VerticalLayout();
        formPanel.setWidth("380px");
        formPanel.setPadding(true);
        formPanel.addClassName("form-panel");

        H3 formTitle = new H3("Datos del Producto");
        formTitle.getStyle().set("margin-top", "0");

        // Fields config
        idField.setReadOnly(true);
        idField.getStyle().set("opacity", "0.7");
        nombreField.setRequired(true);
        categoriaField.setRequired(true);
        precioField.setMin(0.0);
        stockField.setMin(0);

        // Bind fields manually to prevent type/reflection issues
        binder.bind(nombreField, Producto::getNombre, Producto::setNombre);
        binder.bind(categoriaField, Producto::getCategoria, Producto::setCategoria);
        binder.bind(precioField, Producto::getPrecio, Producto::setPrecio);
        binder.bind(stockField, Producto::getStock, Producto::setStock);

        FormLayout form = new FormLayout();
        form.add(idField, nombreField, categoriaField, precioField, stockField);

        // Buttons: Nuevo, Consultar Uno, Consultar Todos, Actualizar Producto, Eliminar
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

        Button btnActualizar = new Button("Actualizar Producto", new Icon(VaadinIcon.EDIT));
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
        grid.setItems(service.getProductos(filterText.getValue()));
    }

    private void selectProducto(Producto producto) {
        if (producto == null) {
            clearFormForNew();
        } else {
            selectedProducto = producto;
            binder.readBean(selectedProducto);
            idField.setValue(producto.getId() != null ? String.valueOf(producto.getId()) : "");
        }
    }

    private void clearFormForNew() {
        selectedProducto = new Producto();
        binder.readBean(selectedProducto);
        idField.setValue("");
        grid.asSingleSelect().clear();
    }

    private void saveOrUpdate() {
        if (selectedProducto == null) {
            Notification.show("Selecciona un producto o presiona 'Nuevo'").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            binder.writeBean(selectedProducto);
            service.saveProducto(selectedProducto);
            refreshGrid();
            
            String msg = (selectedProducto.getId() == null) ? "Producto creado con éxito" : "Producto actualizado con éxito";
            Notification.show(msg).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearFormForNew();
        } catch (Exception e) {
            Notification.show("Corrige los errores del formulario").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showEliminarConfirmDialog() {
        if (selectedProducto == null || selectedProducto.getId() == null) {
            Notification.show("Selecciona un producto de la lista para eliminar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Eliminación");
        confirmDialog.add(new Span("¿Está seguro de que desea eliminar el producto '" + selectedProducto.getNombre() + "'?"));

        Button btnConfirm = new Button("Confirmar", new Icon(VaadinIcon.CHECK), e -> {
            service.deleteProducto(selectedProducto.getId());
            refreshGrid();
            clearFormForNew();
            confirmDialog.close();
            Notification.show("Producto eliminado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        btnConfirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnCancel = new Button("Cancelar", e -> confirmDialog.close());

        confirmDialog.getFooter().add(btnCancel, btnConfirm);
        confirmDialog.open();
    }

    private void showConsultarUnoDialog() {
        Dialog queryDialog = new Dialog();
        queryDialog.setHeaderTitle("Consultar Producto por ID");

        TextField idQueryField = new TextField("Ingrese el ID del Producto");
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
                    service.getProducto(id).ifPresentOrElse(
                        producto -> {
                            grid.select(producto);
                            selectProducto(producto);
                            queryDialog.close();
                            Notification.show("Producto encontrado").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        },
                        () -> Notification.show("Producto no encontrado").addThemeVariants(NotificationVariant.LUMO_ERROR)
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
