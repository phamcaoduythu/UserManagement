package com.example.usermanagement.service_implements;

import com.example.usermanagement.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    @Override
    public void sendEmail(String email, String name, String password) throws MessagingException {
        String subject = "Thông tin tài khoản hệ thống FAMS";
        String template = "Hi, %s,<br>" +
                "<p>Tài khoản đăng nhập vào hệ thống FAMS của bạn đã được tạo thành công.</p>" +
                "<p>Vui lòng truy cập hệ thống theo thông tin sau: </p>" +
                "<ul>" +
                "<li>Username: %s</li>" +
                "<li>Password: %s</li>" +
                "</ul>" +
                "<p>Lưu ý: Vui lòng thay đổi mật khẩu sau khi đăng nhập.</p>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setSubject(subject);
        messageHelper.setTo(email);
        messageHelper.setText(String.format(template, name, email, password), true);
        mailSender.send(mimeMessage);
    }
}
