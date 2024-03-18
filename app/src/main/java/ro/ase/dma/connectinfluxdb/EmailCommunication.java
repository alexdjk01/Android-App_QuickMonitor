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


    public EmailCommunication(){

    }
    public EmailCommunication(String senderEmail,String senderPassword, String receiverEmail, String hostDomain) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.hostDomain = hostDomain;
        this.senderPassword=senderPassword;
    }

    public void sendEmail(){


        try {
            String stringSenderEmail = "ionelalexandru01@gmail.com";
            String stringReceiverEmail = "djkmata.djkmata@gmail.com";
            String stringPasswordSenderEmail = "mfjhltkgndvfbksj";  //mfjh ltkg ndvf bksj

            String stringHost = "smtp.gmail.com";

            Properties properties = new Properties();

//            properties.put("mail.smtp.host", stringHost);
//            properties.put("mail.smtp.port", "465");
//            properties.put("mail.smtp.ssl.enable", "true");
//            properties.put("mail.smtp.auth", "true");
//            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            properties.put("mail.smtp.socketFactory.port", "465");

            //Try using TLS - working!
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true"); //TLS
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            session.setDebug(true);

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject("Subject: QuickMonitor Alert");
            mimeMessage.setText("Hello Programmer, \n\nProgrammer World has sent you this 2nd email. \n\n Cheers!\nProgrammer World");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


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
