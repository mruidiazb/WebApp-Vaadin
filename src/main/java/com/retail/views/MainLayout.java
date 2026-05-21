package com.example.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addNavbarContent();
        addDrawerContent();
    }

    private void addNavbarContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);

        // Spacer to push user info and logout to the right
        Span spacer = new Span();
        layout.setFlexGrow(1, spacer);

        // User info
        String username = "";
        if (VaadinSession.getCurrent() != null && VaadinSession.getCurrent().getAttribute("username") != null) {
            username = VaadinSession.getCurrent().getAttribute("username").toString();
        }
        
        Span userBadge = new Span(username);
        userBadge.addClassNames(
                LumoUtility.Background.PRIMARY_10,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.Padding.Horizontal.SMALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Right.MEDIUM
        );

        Button logoutBtn = new Button("Cerrar Sesión", new Icon(VaadinIcon.SIGN_OUT));
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        logoutBtn.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("username", null);
            UI.getCurrent().navigate(LoginView.class);
        });

        layout.add(toggle, viewTitle, spacer, userBadge, logoutBtn);
        addToNavbar(true, layout);
    }

    private void addDrawerContent() {
        H2 appName = new H2("Shop Retro");
        appName.addClassNames(
                LumoUtility.FontSize.LARGE, 
                LumoUtility.Margin.Vertical.MEDIUM, 
                LumoUtility.Margin.Horizontal.MEDIUM,
                LumoUtility.TextColor.PRIMARY
        );

        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());
        scroller.setClassName(LumoUtility.Padding.Horizontal.SMALL);

        addToDrawer(header, scroller);
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.setWidthFull();

        nav.addItem(new SideNavItem("Clientes", ClientesView.class, VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Productos", ProductosView.class, VaadinIcon.PACKAGE.create()));
        nav.addItem(new SideNavItem("Proveedores", ProveedoresView.class, VaadinIcon.TRUCK.create()));
        nav.addItem(new SideNavItem("Facturas", FacturasView.class, VaadinIcon.FILE_TEXT.create()));
        nav.addItem(new SideNavItem("Usuarios", UsuariosView.class, VaadinIcon.KEY.create()));

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent() == null || VaadinSession.getCurrent().getAttribute("username") == null) {
            event.rerouteTo(LoginView.class);
        }
    }
}
