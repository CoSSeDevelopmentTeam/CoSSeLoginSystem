package itsu.mcpe.NewLoginSystem.manager;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import cn.nukkit.scheduler.AsyncTask;
import itsu.mcpe.NewLoginSystem.core.NewLoginSystem;

public class MailManager {

    public static final int TYPE_ACCOUNT = 0;
    public static final int TYPE_PASSWORD = 1;

    private String address;
    private String password;
    private String mailText;
    private String passMailText;
    private NewLoginSystem plugin;

    private MailManager manager;

    public MailManager(NewLoginSystem plugin, String address, String password, String mailText, String passMailText) {
        this.address = address;
        this.password = password;
        this.mailText = mailText;
        this.passMailText = passMailText;
        this.plugin = plugin;

        this.manager = this;
    }

    @SuppressWarnings("deprecation")
    public void sendMail(String title, String text, String toAddress, String password, String userName, int type) {
        plugin.getServer().getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                try {
                      Properties props = System.getProperties();

                      props.setProperty("mail.debug", plugin.getMailDebug());
                      
                      props.setProperty("mail.smtp.host", plugin.getSMTPHost());
                      props.setProperty("mail.smtp.port", plugin.getSMTPPort());
                      props.setProperty("mail.smtp.auth", plugin.getSMTPAuth());
                      
                      props.setProperty("mail.smtp.user", manager.address);
                      props.setProperty("mail.smtp.password", manager.password);

                      if(!plugin.getSMTPConnectionTimeout().equals("none"))
                          props.setProperty("mail.smtp.connectiontimeout", plugin.getSMTPConnectionTimeout());

                      if(!plugin.getSMTPTimeout().equals("none"))
                          props.setProperty("mail.smtp.timeout", plugin.getSMTPTimeout());

                      if(!plugin.getSMTPSocketFactoryClass().equals("none"))
                          props.setProperty("mail.smtp.socketFactory.class", plugin.getSMTPSocketFactoryClass());

                      if(!plugin.getSMTPSocketFactoryFallback().equals("none"))
                          props.setProperty("mail.smtp.socketFactory.fallback", plugin.getSMTPSocketFactoryFallback());

                      if(!plugin.getSMTPSocketFactoryPort().equals("none"))
                          props.setProperty("mail.smtp.socketFactory.port", plugin.getSMTPSocketFactoryPort());

                      if(!plugin.getSMTPLocalhost().equals("none"))
                          props.setProperty("mail.smtps.localhost", plugin.getSMTPLocalhost());

                      if(!plugin.getSMTPTransportProtocol().equals("none"))
                          props.setProperty("mail.transport.protocol", plugin.getSMTPTransportProtocol());

                      Session session = Session.getInstance(props);

                      MimeMessage msg = new MimeMessage(session);

                      if(type == TYPE_ACCOUNT) {
                          msg.setContent(new String(mailText.replaceAll("#TEXT_1#", text).replaceAll("#PASSWORD#", password).replaceAll("#USERNAME#", userName).getBytes("MS932"), "SHIFT_JIS"), plugin.getAccountMailType() + ";charset=" + plugin.getAccountEncode());
                      } else if(type == TYPE_PASSWORD) {
                          msg.setContent(new String(passMailText.replaceAll("#TEXT_1#", text).replaceAll("#PASSWORD#", password).replaceAll("#USERNAME#", userName).getBytes("MS932"), "SHIFT_JIS"), plugin.getPasswordMailType() + ";charset=" + plugin.getPasswordEncode());
                      }

                      msg.setFrom(new InternetAddress(address, plugin.getServer().getMotd()));
                      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
                      msg.setSubject(new String(title.getBytes("MS932"), "SHIFT_JIS"), "SHIFT_JIS");
                      msg.setSentDate(new Date());

                      Transport trans = null;

                      try {
                          trans = session.getTransport(plugin.getSessionTransport());

                          trans.connect(
                              props.getProperty("mail.smtp.host"),
                              Integer.parseInt(props.getProperty("mail.smtp.port")),
                              props.getProperty("mail.smtp.user"),
                              props.getProperty("mail.smtp.password"));

                          trans.sendMessage(msg, msg.getAllRecipients());
                      } finally {
                          if (trans != null) {
                              trans.close();
                          }
                      }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
