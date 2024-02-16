package com.studentmanagement.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.studentmanagement.model.Admins;
import com.studentmanagement.repository.AdminsRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AdminsServiceImpl implements AdminsService {

	@Autowired
	AdminsRepository repo;
	
	@Autowired
    private JavaMailSender mailSender;

	@Override
	public Admins findByEmailAndPassword(Admins admins) {
		return repo.findByEmailAndPassword(admins.getEmail(), admins.getPassword());
	}

	@Override
	public Admins getByEmail(String email) {
		return repo.findByEmail(email);
	}

	@Override
	public void generateOTP(String email) {

		 Admins admins = repo.findByEmail(email);
	     String OTP = RandomStringUtils.randomAlphanumeric(8);
	     admins.setResetPasswordToken(OTP);
	     repo.save(admins);
	      try {
			sendOTPEmail(admins, OTP);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendOTPEmail(Admins admins, String OTP) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("kk");
        helper.setTo(admins.getEmail());
        String subject = "Here's your One Time Password (OTP) - Expire in 5 minutes!";
        String content = "<p>Hello " + admins.getName() + "</p>"
                + "<p>For security reason, you're required to use the following "
                + "One Time Password to reset your password:</p>"
                + "<p><b>" + OTP + "</b></p>"
                + "<br>"
                + "<p>Note: this OTP is set to expire in 5 minutes.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

    }
}
