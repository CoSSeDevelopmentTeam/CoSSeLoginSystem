package itsu.mcpe.NewLoginSystem.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import itsu.mcpe.NewLoginSystem.manager.CommandManager;
import itsu.mcpe.NewLoginSystem.manager.MailManager;
import itsu.mcpe.NewLoginSystem.manager.WindowManager;

public class NewLoginSystem extends PluginBase {

    /*public*/
    public static final String PATH = "./plugins/CoSSeLoginSystem/";

    /*Instance*/
    private static NewLoginSystem instance;
    private SQLSystem sql;
    private WindowManager manager;
    private MailManager mail;
    private CommandManager command;
    private EventListener event;
    private WindowListener window;

    /*Config*/
    private Config conf;
    private Map<String, Object> configData = new HashMap<>();

    /*ConfigData*/
    private boolean sendMail = true;
    private String address = "";
    private String password = "";
    /*ConfigData-Account*/
    private String AccountMail = "";
    private String AccountMailType = "";
    private String AccountEncode = "";
    /*ConfigData-Password*/
    private String PasswordMail = "";
    private String PasswordMailType = "";
    private String PasswordEncode = "";
    /*ConfigData-Mail*/
    private String MailDebug = "";
    private String SMTPHost = "";
    private String SMTPPort = "";
    private String SMTPConnectionTimeout = "";
    private String SMTPTimeout = "";
    private String SMTPAuth = "";
    private String SMTPTransportProtocol = "";
    private String SMTPLocalhost = "";
    private String SMTPSocketFactoryClass = "";
    private String SMTPSocketFactoryFallback = "";
    private String SMTPSocketFactoryPort = "";
    private String SessionTransport = "";
    /*Mail-Text*/
    private String mailText;
    private String passMailText;
    /*other*/
    private boolean disable = false;

    public void onEnable() {
        instance = this;

        getDataFolder().mkdirs();

        initConfig();
        initSQLSystem();
        initWindowManager();
        initMail();

        initMailManager();
        initCommandManager();

        initWindowListener();
        initEventListener();

        getServer().getPluginManager().registerEvents(event, this);
        getServer().getPluginManager().registerEvents(window, this);

        String pass = "";
        for(int i = 0; i < password.length(); i++) {
            pass += "*";
        }

        if(disable) {

            getLogger().info(TextFormat.RED + "Config.ymlが./plugins/CoSSeLoginSystem/に作成されました。適宜設定を行って再起動を行ってください。");
            getServer().getPluginManager().disablePlugin(this);

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.command.processCommand(sender, command, label, args);
    }

    private void initConfig() {
        if(!new File(PATH + "Config.yml").exists()) {
            try {
                Utils.writeFile(new File(PATH + "Config.yml"), this.getClass().getClassLoader().getResourceAsStream("Config.yml"));
                disable = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        conf = new Config(new File(PATH + "Config.yml"), Config.YAML);

        conf.load(PATH + "Config.yml");
        configData = conf.getAll();

        sendMail = (boolean) configData.get("sendMail");
        address = (String) configData.get("Address");
        password = String.valueOf(configData.get("Password"));

        AccountMail = (String) configData.get("Account-Mail");
        AccountMailType = (String) configData.get("Account-MailType");
        AccountEncode = (String) configData.get("Account-Encode");

        PasswordMail = (String) configData.get("Password-Mail");
        PasswordMailType = (String) configData.get("Password-MailType");
        PasswordEncode = (String) configData.get("Password-Encode");

        MailDebug = String.valueOf(configData.get("Mail-Debug"));
        SessionTransport = (String) configData.get("Session-Transport");

        SMTPHost = (String) configData.get("Smtp-Host");
        SMTPPort = String.valueOf(configData.get("Smtp-Port"));
        SMTPConnectionTimeout = String.valueOf(configData.get("Smtp-ConnectionTimeout"));
        SMTPTimeout = String.valueOf(configData.get("Smtp-Timeout"));
        SMTPAuth = String.valueOf(configData.get("Smtp-Auth"));
        SMTPTransportProtocol = (String) configData.get("Smtp-TransportProtocol");
        SMTPLocalhost = (String) configData.get("Smtp-Localhost");
        SMTPSocketFactoryClass = (String) configData.get("Smtp-SocketFactory-Class");
        SMTPSocketFactoryFallback = String.valueOf(configData.get("Smtp-SocketFactory-Fallback"));
        SMTPSocketFactoryPort = String.valueOf(configData.get("Smtp-SocketFactory-Port"));
    }

    private void initMail() {
        try{
            File mailText = new File(PATH + AccountMail);
            File passMailText = new File(PATH + PasswordMail);

            if(!mailText.exists()) {
                Utils.writeFile(mailText, this.getClass().getClassLoader().getResourceAsStream("Mail.html"));
            }

            if(!passMailText.exists()) {
                Utils.writeFile(passMailText, this.getClass().getClassLoader().getResourceAsStream("Mail.html"));
            }

            this.mailText = Utils.readFile(new FileInputStream(mailText));
            this.mailText = new String(this.mailText.getBytes("SHIFT_JIS"), "MS932");
            this.mailText = this.mailText.replaceAll("#SERVER_NAME#", getServer().getMotd());

            this.passMailText = Utils.readFile(new FileInputStream(passMailText));
            this.passMailText = new String(this.mailText.getBytes("SHIFT_JIS"), "MS932");
            this.passMailText = this.mailText.replaceAll("#SERVER_NAME#", getServer().getMotd());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMailManager() {
        mail = new MailManager(this, address, password, mailText, passMailText);
        mailText = null;
        passMailText = null;
    }

    private void initSQLSystem() {
        sql = new SQLSystem(this);
    }

    private void initWindowManager() {
        manager = new WindowManager(this, getServer().getMotd());
    }

    private void initCommandManager() {
        command = new CommandManager(this);
    }

    private void initEventListener() {
        event = new EventListener(this);
    }

    private void initWindowListener() {
        window = new WindowListener(this);
    }

    public NewLoginSystem getInstance() {
        return instance;
    }

    public SQLSystem getSQLSystem() {
        return sql;
    }

    public WindowManager getWindowManager() {
        return manager;
    }

    public MailManager getMailManager() {
        return mail;
    }

    public CommandManager getCommandManager() {
        return command;
    }

    public EventListener getEventListener() {
        return event;
    }

    public WindowListener getWindowListener() {
        return window;
    }


    /*Mail-Account*/

    public boolean allowMail() {
        return sendMail;
    }

    public String getAccountMail() {
        return AccountMail;
    }

    public String getAccountMailType() {
        return AccountMailType;
    }

    public String getAccountEncode() {
        return AccountEncode;
    }

    public String getPasswordMail() {
        return PasswordMail;
    }

    public String getPasswordMailType() {
        return PasswordMailType;
    }

    public String getPasswordEncode() {
        return PasswordEncode;
    }


    /*Mail-Settings*/

    public String getMailDebug() {
        return MailDebug;
    }

    public String getSessionTransport() {
        return SessionTransport;
    }

    public String getSMTPHost() {
        return SMTPHost;
    }

    public String getSMTPPort() {
        return SMTPPort;
    }

    public String getSMTPConnectionTimeout() {
        return SMTPConnectionTimeout;
    }

    public String getSMTPTimeout() {
        return SMTPTimeout;
    }

    public String getSMTPAuth() {
        return SMTPAuth;
    }

    public String getSMTPTransportProtocol() {
        return SMTPTransportProtocol;
    }

    public String getSMTPLocalhost() {
        return SMTPLocalhost;
    }

    public String getSMTPSocketFactoryClass() {
        return SMTPSocketFactoryClass;
    }

    public String getSMTPSocketFactoryFallback() {
        return SMTPSocketFactoryFallback;
    }

    public String getSMTPSocketFactoryPort() {
        return SMTPSocketFactoryPort;
    }

}
