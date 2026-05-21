package com.example.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

@Route("login")
@PageTitle("Iniciar Sesión | Vaadin App")
public class LoginView extends VerticalLayout {

    private final TextField username = new TextField("Usuario");
    private final PasswordField password = new PasswordField("Contraseña");
    private final Button loginButton = new Button("Iniciar Sesión");
    private final Span errorMsg = new Span("Usuario o contraseña incorrectos");

    public LoginView() {
        // Setup container layout
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        addClassName("login-view-container");

        // Card layout
        VerticalLayout card = new VerticalLayout();
        card.addClassName("login-card");
        card.setWidth("380px");
        Image logo = new Image("/images/retail_logo.png", "Retail logo");
        logo.addClassName("login-logo");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);

        H1 title = new H1("     Retails BAQ's");
        title.addClassName("login-title");
        Paragraph subtitle = new Paragraph("Sistema de Gestión");
        subtitle.addClassName("login-subtitle");

        username.setRequired(true);
        username.setPlaceholder("usuario");
        
        password.setRequired(true);
        password.setPlaceholder("••••••••");

        errorMsg.addClassName("login-error-msg");
        errorMsg.setVisible(false);

        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClassName("login-btn");
        loginButton.addClickListener(e -> authenticate());

        // Permit press enter key to login
        password.addKeyDownListener(com.vaadin.flow.component.Key.ENTER, e -> authenticate());

        card.add(logo, title, subtitle, errorMsg, username, password, loginButton);
        add(card);
    }

    private void authenticate() {
        String userVal = username.getValue();
        String passVal = password.getValue();

        if ("admin".equals(userVal) && "admin".equals(passVal)) {
            // Save state in session
            VaadinSession.getCurrent().setAttribute("username", userVal);
            // Redirect to home page
            UI.getCurrent().navigate("");
            
            Notification notification = Notification.show("¡Bienvenido al sistema!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            errorMsg.setVisible(true);
            Notification notification = Notification.show("Credenciales inválidas. Usuario: 'admin' y clave: 'admin'");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
