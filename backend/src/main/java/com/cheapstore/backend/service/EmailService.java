package com.cheapstore.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage());
        }
    }

    public void sendVerificationEmail(String to, String link) {

        String html = """
            <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 30px;">
                <div style="max-width: 500px; margin: auto; background: white; padding: 25px; border-radius: 12px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #333;">Activa tu cuenta</h2>

                    <p style="color: #555; font-size: 15px;">
                        Gracias por registrarte en <b>CheapStore</b>.
                    </p>

                    <p style="color: #555; font-size: 14px;">
                        Para comenzar, activa tu cuenta:
                    </p>

                    <a href="%s"
                       style="display:inline-block;padding:12px 25px;margin:20px 0;background:#39863c;color:white;text-decoration:none;border-radius:6px;font-weight:bold;">
                        Activar cuenta
                    </a>

                    <p style="font-size:12px;color:#999;">
                        Este enlace expira en 1 hora
                    </p>

                    <hr style="margin:20px 0;border:none;border-top:1px solid #eee;">

                    <p style="font-size:12px;color:#aaa;">
                        Si no creaste esta cuenta, ignora este correo.
                    </p>

                    <p style="font-size:12px;color:#bbb;">
                        © 2026 CheapStore
                    </p>
                </div>
            </div>
        """.formatted(link);

        sendEmail(to, "Activa tu cuenta - CheapStore", html);
    }

    public void sendResetPasswordEmail(String to, String link) {

        String html = """
            <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 30px;">
                <div style="max-width: 500px; margin: auto; background: white; padding: 25px; border-radius: 12px; text-align: center;">
                    <h2 style="color:#333;">Recuperar contraseña</h2>

                    <p style="color:#555;">
                        Haz clic en el botón para cambiar tu contraseña:
                    </p>

                    <a href="%s"
                       style="display:inline-block;padding:12px 25px;margin:20px 0;background:#d9534f;color:white;text-decoration:none;border-radius:6px;font-weight:bold;">
                        Cambiar contraseña
                    </a>

                    <p style="font-size:12px;color:#999;">
                        Este enlace expira en 1 hora
                    </p>

                    <hr style="margin:20px 0;border:none;border-top:1px solid #eee;">

                    <p style="font-size:12px;color:#aaa;">
                        Si no solicitaste este cambio, ignora este correo.
                    </p>
                </div>
            </div>
        """.formatted(link);

        sendEmail(to, "Recupera tu contraseña - CheapStore", html);
    }
}