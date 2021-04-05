package app.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

	public synchronized static void sendEmail(EmailType tip) {
		final String username = "lukamilicask@gmail.com";
		final String password = "Test2020";

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			String toMail = "bakkiceva99@gmail.com";
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("lukamilicask@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));

			if (tip == EmailType.REGISTRACIJA) {
				message.setSubject("Registracija");
				message.setText("Poštovani korisniče,\n\nUspešno ste se registrovali na servis za prodaju karata za letove!");
			} 
			else if (tip == EmailType.PROMENA_MEJLA) {
				message.setSubject("Promena mejla");
				message.setText("Poštovani korisniče,\n\nUspešno ste promenili e-mail adresu.");
			} 
			else if (tip == EmailType.OTKAZIVANJE_LETA) {
				message.setSubject("Otkazivanje leta");
				message.setText("Poštovani korisniče,\n\nVaš let je otkazan. Novac Vam je vraćen.");
			}

			Transport.send(message);

			System.out.println("Mail je poslat na adresu: " + toMail);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
