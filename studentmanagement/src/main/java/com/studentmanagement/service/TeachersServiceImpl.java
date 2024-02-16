package com.studentmanagement.service;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.studentmanagement.dto.TeachersDto;
import com.studentmanagement.model.Teachers;
import com.studentmanagement.repository.TeachersRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class TeachersServiceImpl implements TeachersService {
	
	@Autowired
	TeachersRepository teachersRepository;
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
    private EmailService emailService;

	@Override
	public List<Teachers> getAll() {
		return teachersRepository.findAll();
	}

	@Override
	public Teachers getById(int id) {
		return teachersRepository.findById(id);
	}

	@Override
	public void create(TeachersDto teacherDto) {
		Teachers teacher = new Teachers();
		teacher.setName(teacherDto.getName());
		teacher.setQualification(teacherDto.getQualification());
		teacher.setPhone(teacherDto.getPhone());
		teacher.setEmail(teacherDto.getEmail());
		
		teacher.setAddress(teacherDto.getAddress());
		teacher.setServiceYear(teacherDto.getServiceYear());
		
		String password = RandomStringUtils.randomAlphanumeric(10);
		teacher.setPassword(password);
		
		 String text = "Your account has been created. Your login details are:\n" +
                  "Email: " + teacher.getEmail() + "\n" +
                  "Password:" + password;;
		 	emailService.sendMail(teacher.getEmail(), "Registration Successful", text);
		 	
		 teachersRepository.save(teacher);
	}

	@Override
	public void edit(int id, TeachersDto teacherDto) {
		Teachers teacher = teachersRepository.findById(id);
		teacher.setName(teacherDto.getName());
		teacher.setQualification(teacherDto.getQualification());
		teacher.setPhone(teacherDto.getPhone());
		teacher.setEmail(teacherDto.getEmail());
		teacher.setAddress(teacherDto.getAddress());
		teacher.setServiceYear(teacherDto.getServiceYear());
		teachersRepository.save(teacher);
	}

	@Override
	public void deleteByID(int id) {
		teachersRepository.deleteById(id);
	}
	
	@Override
	public Teachers findByEmailAndPassword(Teachers teachers) {
		return teachersRepository.findByEmailAndPassword(teachers.getEmail(), teachers.getPassword());
	}
	@Override
	public Long countTeacher() {
		return teachersRepository.count();
	}

	@Override
	public Teachers getByEmail(String email) {
		return teachersRepository.findByEmail(email);
	}
	
	@Override
	public void generateOTP(String email) {

		 Teachers teachers = teachersRepository.findByEmail(email);
	     String OTP = RandomStringUtils.randomAlphanumeric(8);
	     teachers.setResetPasswordToken(OTP);
	     teachersRepository.save(teachers);
	      try {
			sendOTPEmail(teachers, OTP);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendOTPEmail(Teachers teachers, String OTP) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("kk");
        helper.setTo(teachers.getEmail());
        String subject = "Here's your One Time Password (OTP) - Expire in 5 minutes!";
        String content = "<p>Hello " + teachers.getName() + "</p>"
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
