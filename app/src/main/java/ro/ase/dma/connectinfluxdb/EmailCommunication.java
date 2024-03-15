package ro.ase.dma.connectinfluxdb;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailCommunication {

    private String senderEmail;
    private String senderPassword;
    private String receiverEmail;
    private String hostDomain;

    private Properties properties = System.getProperties();

    private javax.mail.Session session;

    private MimeMessage mimeMessage;

    public EmailCommunication(String senderEmail,String senderPassword, String receiverEmail, String hostDomain) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.hostDomain = hostDomain;
        this.senderPassword=senderPassword;
    }

    public void addProperties(){
        properties.put("mail.smtp.host", hostDomain);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.ssl.enable", "true");

        //properties.put("mail.smtp.starttls.enable", "true");


    }

    public void addSession(){
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail,senderPassword);
            }
        });
        session.setDebug(true);
    }

    public void generateMimeMessage(String emailMessage){

        mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            mimeMessage.setSubject("Subject: Monitor Alert");
            mimeMessage.setText(String.format("Quick Monitor App Alert!  \n \n %s", emailMessage));
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Transport.send(mimeMessage);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getHostDomain() {
        return hostDomain;
    }

    public void setHostDomain(String hostDomain) {
        this.hostDomain = hostDomain;
    }

}
