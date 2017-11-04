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
    public static final String PATH = "./plugins/NewLoginSystem/";

    /*Instance*/
    private static NewLoginSystem instance;
    private SQLSystem sql;
    private WindowManager manager;
    private MailManager mail;
    private CommandManager command;
    private EventListener event;
    private WindowListener window;

    /*Config*/
    private Config conf = new Config(new File(PATH + "Config.yml"), Config.YAML);
    private Map<String, Object> configData = new HashMap<>();

    /*ConfigData*/
    private boolean sendMail = true;
    private String address = "";
    private String password = "";

    private String mailText;

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

        getLogger().info(TextFormat.GREEN + "起動しました。");
        getLogger().info(TextFormat.AQUA + "二次配布/改造は禁止です。");
        getLogger().info(TextFormat.AQUA + "不具合/質問等ありましたらItsu(@itsu_dev)までお問い合わせください。");
        getLogger().info(TextFormat.BLUE + "メール送信: " + TextFormat.RESET + sendMail);
        getLogger().info(TextFormat.BLUE + "送信アドレス: " + TextFormat.RESET + address);
        getLogger().info(TextFormat.BLUE + "パスワード: " + TextFormat.RESET + pass);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.command.processCommand(sender, command, label, args);
    }

    private void initConfig() {
        conf.load(PATH + "Config.yml");
        configData = conf.getAll();

        if(this.configData.isEmpty()) {
            conf.set("sendMail", true);
            conf.set("Address", "MAIL_ADDRESS");
            conf.set("Password", "PASSWORD");
            conf.save();

            configData.clear();
            configData = conf.getAll();
        }

        sendMail = (boolean) configData.get("sendMail");
        address = (String) configData.get("Address");
        password = String.valueOf(configData.get("Password"));
    }

    private void initMail() {
        try{
            File mailText = new File(PATH + "Mail.html");

            if(!mailText.exists()) {
                Utils.writeFile(mailText, this.getClass().getClassLoader().getResourceAsStream("Mail.html"));
            }

            this.mailText = Utils.readFile(new FileInputStream(mailText));
            this.mailText = new String(this.mailText.getBytes("SHIFT_JIS"), "MS932");
            this.mailText = this.mailText.replaceAll("#SERVER_NAME#", getServer().getMotd());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMailManager() {
        mail = new MailManager(this, address, password, mailText);
        mailText = null;
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
    
    public boolean allowMail() {
    	return sendMail;
    }

}
