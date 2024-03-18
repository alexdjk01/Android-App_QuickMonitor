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

    private String stringSenderEmail;
    private String stringReceiverEmail;
    private String stringPasswordSenderEmail;

    private Properties properties = System.getProperties();

    private javax.mail.Session session;

    private MimeMessage mimeMessage;


    public EmailCommunication(){

    }
    public EmailCommunication(String senderEmail,String senderPassword, String receiverEmail) {
        this.stringSenderEmail = senderEmail;
        this.stringReceiverEmail = receiverEmail;
        this.stringPasswordSenderEmail=senderPassword;
    }

    public void sendEmail(String text){


        try {
           // String stringSenderEmail = "ionelalexandru01@gmail.com";
           // String stringReceiverEmail = "djkmata.djkmata@gmail.com";
           // String stringPasswordSenderEmail = "mfjhltkgndvfbksj";  //mfjh ltkg ndvf bksj

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
            mimeMessage.setText(text);

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
        return stringPasswordSenderEmail;
    }

    public void setSenderPassword(String senderPassword) {
        this.stringPasswordSenderEmail = senderPassword;
    }

    public String getSenderEmail() {
        return stringSenderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.stringSenderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return stringReceiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.stringReceiverEmail = receiverEmail;
    }


}
