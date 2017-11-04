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

    private String address;
    private String password;
    private String mailText;
    private NewLoginSystem plugin;
    
    private MailManager manager;

    public MailManager(NewLoginSystem plugin, String address, String password, String mailText) {
        this.address = address;
        this.password = password;
        this.mailText = mailText;
        this.plugin = plugin;
        
        this.manager = this;
    }

    @SuppressWarnings("deprecation")
	public void sendMail(String title, String text, String toAddress, String password, String userName) {
    	plugin.getServer().getScheduler().scheduleAsyncTask(new AsyncTask() {
    		@Override
    		public void onRun() {
    			try {
    	              Properties props = System.getProperties();

    	              props.setProperty("mail.smtp.host", "smtp.gmail.com");
    	              props.setProperty("mail.smtp.port", "465");
    	              props.setProperty("mail.smtp.user", manager.address);
    	              props.setProperty("mail.smtp.password", manager.password);
    	              props.setProperty("mail.smtp.connectiontimeout", "60000");
    	              props.setProperty("mail.smtp.timeout", "60000");
    	              props.setProperty("mail.smtp.auth", "true");
    	              props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    	              props.setProperty("mail.smtp.socketFactory.fallback", "false");
    	              props.setProperty("mail.smtp.socketFactory.port", "465");
    	              props.setProperty("mail.smtps.localhost", "localhost");
    	              props.setProperty("mail.transport.protocol", "smtps");

    	              Session session = Session.getInstance(props);

    	              MimeMessage msg = new MimeMessage(session);

    	              msg.setContent(new String(mailText.replaceAll("#TEXT_1#", text).replaceAll("#PASSWORD#", password).replaceAll("#USERNAME#", userName).getBytes("MS932"), "SHIFT_JIS"), "text/html;charset=SHIFT_JIS");
    	              msg.setFrom(new InternetAddress(address, plugin.getServer().getMotd()));
    	              msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
    	              msg.setSubject(title);
    	              msg.setSentDate(new Date());

    	              Transport trans = null;

    	              try {
    	                  trans = session.getTransport("smtps");

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
