package itsu.mcpe.NewLoginSystem.manager;

import java.util.List;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.utils.TextFormat;
import itsu.mcpe.NewLoginSystem.core.NewLoginSystem;
import itsu.mcpe.NewLoginSystem.core.SQLSystem;

public class CommandManager {

    private NewLoginSystem plugin;
    private WindowManager manager;
    private SQLSystem sql;

    private int id;
    private int settingId;
    private List<String> players;

    public CommandManager(NewLoginSystem plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWindowManager();
        this.sql = plugin.getSQLSystem();
    }

    public boolean processCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName()) {

            case "cls":

                try{if(args[0] == null);}
                catch(ArrayIndexOutOfBoundsException e){

                    if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] ゲーム内から実行してください。");
                        return true;
                    }

                    Player p = (Player) sender;

                    if(!p.isOp()) {
                        sender.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] このコマンドはOPのみ使用できます。");
                        return true;
                    }

                    id = new Random().nextInt(10000);
                    players = manager.sendAdminWindow(p, id);
                    return true;

                }

                if(args[0].equals("ban")) {

                    Player p = (Player) sender;
                    boolean pardon = false;

                    if(!p.isOp()) {
                        sender.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] このコマンドはOPのみ使用できます。");
                        return true;
                    }

                    try{if(args[1] == null);}
                    catch(ArrayIndexOutOfBoundsException e){
                        sender.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] BANする人を入力してください。");
                        return true;
                    }

                    try{if(args[2] == null);}
                    catch(ArrayIndexOutOfBoundsException e){
                        pardon = false;

                    }

                    if(args[2].equals("p")) {
                        pardon = true;
                    }

                    if(plugin.getServer().getPlayer(args[1]) != null || plugin.getServer().getOfflinePlayer(args[1]) != null) {

                        if(!sql.existsBAN(args[1])) { //BANされていなかったら

                            if(pardon) { //解除だったら

                                p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] " + args[1] + "はBAN(CLS)されていません。");

                            } else { //解除しないだったら

                                sql.createBAN(args[1], plugin.getServer().getPlayer(args[1]).getAddress() + plugin.getServer().getPlayer(args[1]).getClientId());
                                p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] " + args[1] + "をBAN(CLS)しました。");
                                plugin.getServer().getPlayer(args[1]).kick("[CoSSeLoginSystem] あなたはBAN(CLS)されました。", false);

                            }

                        } else if(sql.existsBAN(args[1]) && pardon) { //BANされていて解除だったら

                            if(sql.deleteBAN(args[1])) { //BAN解除を試みる

                                p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] " + args[1] + "のBAN(CLS)を解除しました。");

                            } else { //BANされていない

                                p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] " + args[1] + "はBAN(CLS)されていません。");
                            }


                        } else { //すでにBANされていたら

                            p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] すでにBAN(CLS)されています。");
                        }


                    } else { //サーバーにその名前の人がいなかったら

                        p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] 指定されたプレイヤーは存在しません。");

                    }

                } else if(args[0].equals("setting")) {
                	Player p = (Player) sender;
                	
                	settingId = new Random().nextInt(10000);
                	
                	manager.sendSettingWindow(p, settingId);
                }
        }
        return true;
    }

    public int getWindowId() {
        return id;
    }
    
    public int getSettingWindowId() {
        return settingId;
    }

    public List<String> getPlayers() {
        return players;
    }

}
