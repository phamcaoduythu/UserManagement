package com.example.usermanagement.Service;

import com.example.usermanagement.service_implements.MailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceImplTest {
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    void test_SendMail() throws MessagingException {

        String email = "admin@gmail.com";
        String name = "Admin";
        String password = "1";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendEmail(email, name, password);
        verify(mailSender).send(mimeMessage);
    }
}
