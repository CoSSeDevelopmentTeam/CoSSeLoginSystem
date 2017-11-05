package itsu.mcpe.NewLoginSystem.core;

import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerModalFormCloseEvent;
import cn.nukkit.event.player.PlayerModalFormResponseEvent;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import itsu.mcpe.NewLoginSystem.manager.CommandManager;
import itsu.mcpe.NewLoginSystem.manager.MailManager;
import itsu.mcpe.NewLoginSystem.manager.WindowManager;

public class WindowListener implements Listener{

    private static final int TYPE_NEW = 0;
    private static final int TYPE_CHANGED_IP = 1;
    private static final int TYPE_MANUAL_LOGIN = 2;

    private NewLoginSystem plugin;
    private SQLSystem sql;
    private WindowManager manager;
    private MailManager mail;
    private CommandManager command;
    private EventListener l;
    private Random rand = new Random();

    private int type;


    public WindowListener(NewLoginSystem plugin) {
        this.plugin = plugin;
        sql = plugin.getSQLSystem();
        manager = plugin.getWindowManager();
        mail = plugin.getMailManager();
        command = plugin.getCommandManager();
    }


    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEnterWindow(PlayerModalFormResponseEvent e) {
        Player p = e.getPlayer();
        if(l == null) l = plugin.getEventListener();

        if(e.getFormId() == l.getWindowId()) {
        	
        	plugin.getServer().getScheduler().scheduleAsyncTask(new AsyncTask() {
        		
        		@Override
        		public void onRun() {

                    Map<Integer, Object> data = e.getWindow().getResponses();
                    String password;
                    String address;
                    boolean autoLogin;
                    int allow = 0;

                    switch(type) {

                        case TYPE_NEW:
                            password = (String) data.get(2);
                            address = (String) data.get(3);
                            autoLogin = (boolean) data.get(4);

                            if(autoLogin) {
                                allow = 0;
                            } else {
                                allow = 1;
                            }

                            if(address == null || address.equals("null")) {
                                address = "NO";
                            }

                            sql.createAccount(p.getName(), password, p.getAddress() + p.getLoginChainData().getClientId(), allow, address);
                            l.setSendingWindow(false);
                            l.setLoggedIn(p.getName(), true);

                            p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] ログインに成功しました。");
                            p.sendMessage(TextFormat.AQUA + "プレーヤー名: " + p.getName());
                            p.sendMessage(TextFormat.AQUA + "パスワード: " + password);
                            p.sendMessage(TextFormat.AQUA + "メールアドレス: " + address);
                            p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] パスワードは忘れないでください！");

                            if(plugin.allowMail() && !address.equals("NO")) {

                                mail.sendMail("アカウント作成完了のお知らせ", "アカウントを作成しました。", address, password, p.getName(), MailManager.TYPE_ACCOUNT);
                                p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] 確認用メールが送信されました。");

                            }

                            break;

                        case TYPE_CHANGED_IP:
                            password = (String) data.get(2);

                            if(password.equals(sql.getPassword(p.getName()))) {

                                sql.updateAddress(p.getName(), p.getAddress() + p.getLoginChainData().getClientId());
                                l.setSendingWindow(false);
                                l.setLoggedIn(p.getName(), true);

                                p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] ログインに成功しました。");

                            } else {

                                l.setWindowId(getRandom());
                                l.setSendingWindow(true);
                                type = TYPE_MANUAL_LOGIN;
                                manager.sendLoginWindow(p, l.getWindowId(), TextFormat.RED + "パスワードが違います。");

                            }
                            break;

                        case TYPE_MANUAL_LOGIN:
                            password = (String) data.get(2);
                            boolean leave = (boolean) data.get(3);
                            
                            if(leave) {
                            	p.kick("[CoSSeLoginSystem] サーバーから出るが選択されました。", false);
                            	break;
                            }

                            if(password.equals(sql.getPassword(p.getName()))) {

                                l.setSendingWindow(false);
                                l.setLoggedIn(p.getName(), true);

                                p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] ログインに成功しました。");

                            } else if(password.equals(sql.getMail(p.getName()))) {

                                l.setSendingWindow(false);
                                l.setLoggedIn(p.getName(), false);
                                
                                if(plugin.allowMail()) {
                                	
                                	mail.sendMail("パスワード確認", "パスワードお問い合わせの結果です。", password, sql.getPassword(p.getName()), p.getName(), MailManager.TYPE_PASSWORD);
                                    p.kick("[CoSSeLoginSystem] パスワードを登録したメールアドレス宛に送信しました。パスワードを確認した後、再度ログインしてください。", false);
                                
                                } else {
                                	
                                	l.setWindowId(getRandom());
                                    l.setSendingWindow(true);
                                    type = TYPE_MANUAL_LOGIN;
                                    manager.sendLoginWindow(p, l.getWindowId(), TextFormat.RED + "ログインに失敗しました: サーバー側でメールの送信が許可されていません。");
                                	
                                }
                               

                            } else {

                                l.setWindowId(getRandom());
                                l.setSendingWindow(true);
                                type = TYPE_MANUAL_LOGIN;
                                manager.sendLoginWindow(p, l.getWindowId(), TextFormat.RED + "パスワードが違います。");

                            }
                            break;

                        default:
                            p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] ログインに失敗しました。");
                            l.setSendingWindow(false);
                            l.setLoggedIn(p.getName(), false);
                            l.setWindowId(getRandom());
                            l.setSendingWindow(true);
                            type = TYPE_MANUAL_LOGIN;
                            manager.sendLoginWindow(p, l.getWindowId(), TextFormat.RED + "ログインに失敗しました。再度入力してください。");
                    }
        		}
        	});

        } else if(e.getFormId() == command.getWindowId()) {
        	
        	plugin.getServer().getScheduler().scheduleAsyncTask(new AsyncTask() {
        		
        		@Override
        		public void onRun() {
			
			            Map<Integer, Object> data = e.getWindow().getResponses();
			            String name = (String) data.get(2);
			            String drop = (String) data.get(3);
			            boolean pardon = (boolean) data.get(4);
			
			            if(name.equals("null") || name.equals("")) {
			                name = drop;
			            }
			
			            if(plugin.getServer().getPlayer(name) != null || plugin.getServer().getOfflinePlayer(name) != null) {
			
			                if(!sql.existsBAN(name)) { //BANされていなかったら
			
			                    if(pardon) { //解除だったら
			
			                        p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] " + name + "はBAN(CLS)されていません。");
			
			                    } else { //解除しないだったら
			
			                        sql.createBAN(name, plugin.getServer().getPlayer(name).getAddress() + plugin.getServer().getPlayer(name).getClientId());
			                        p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] " + name + "をBAN(CLS)しました。");
			                        plugin.getServer().getPlayer(name).kick("[CoSSeLoginSystem] あなたはBAN(CLS)されました。", false);
			
			                    }
			
			                } else if(sql.existsBAN(name) && pardon) { //BANされていて解除だったら
			
			                    if(sql.deleteBAN(name)) { //BAN解除を試みる
			
			                        p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] " + name + "のBAN(CLS)を解除しました。");
			
			                    } else { //BANされていない
			
			                        p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] " + name + "はBAN(CLS)されていません。");
			                    }
			
			
			                } else { //すでにBANされていたら
			
			                    p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] すでにBAN(CLS)されています。");
			                }
			
			
			            } else { //サーバーにその名前の人がいなかったら
			
			                p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] 指定されたプレイヤーは存在しません。");
			
			            }
        		}
        	});
        	
        } else if(e.getFormId() == command.getSettingWindowId()) {
        	
        	Map<Integer, Object> data = e.getWindow().getResponses();
            String menu = (String) data.get(2);
            String area = (String) data.get(3);
            String pass = (String) data.get(4);
            boolean auto = (boolean) data.get(5);
            int autoInt;
            
            if(auto) autoInt = 0;
            else autoInt = 1;
            
            switch(menu) {
            
            	case "サーバー/CoSSeLoginSystemの情報を見る":
            		manager.sendModalWindow(p, "CoSSeLoginSystem サーバー/CoSSeLoginSystem情報", 
            				TextFormat.GREEN + plugin.getServer().getMotd() + "\n" + 
            				TextFormat.WHITE + "オンライン人数: " + plugin.getServer().getOnlinePlayers().values().size() + "\n" + 
            				TextFormat.WHITE + "サーバーソフト: " + "Jupiter\n" + 
            				TextFormat.GREEN + "CoSSeLoginSystem\n" + 
            				TextFormat.GRAY + "Re-write from NewLoginSystem for Jupiter\n" + 
            				TextFormat.WHITE + "開発: Itsu\n" + 
            				TextFormat.WHITE + "CoSSe向け改変: popkechupki\n"
            				, new Random().nextInt(10000));
            		
                    if(auto) autoInt = 0;
                    else autoInt = 1;
                    
                    sql.updateAutoLogin(p.getName(), autoInt);
                    
            		break;
            		
            	case "登録情報の確認":
            		String temp = "";
            		
            		if(sql.getAutoLogin(p.getName()) == 0) temp = "有効";
            		else temp = "無効";
            		
            		manager.sendModalWindow(p, "CoSSeLoginSystem 登録情報確認", 
            				TextFormat.GREEN + plugin.getServer().getMotd() + "\n" + 
            				TextFormat.WHITE + "メールアドレス: " + sql.getMail(p.getName()) + "\n" + 
            				TextFormat.WHITE + "パスワード: " + sql.getPassword(p.getName()) + "\n" + 
            				TextFormat.WHITE + "自動ログイン: " + temp
            				, new Random().nextInt(10000));
            		
                    if(auto) autoInt = 0;
                    else autoInt = 1;
                    
                    sql.updateAutoLogin(p.getName(), autoInt);
                    
            		break;
            		
            	case "メールアドレスの変更":
            		if(pass.equals(sql.getPassword(p.getName()))) {
            			if(!area.equals(sql.getMail(p.getName()))) {
            				p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] メールアドレスを更新しました。: " + area);
            				
            			} else {
            				p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] 登録されたメールアドレスと同じです。");
            			}
            			
            		} else {
            			p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] パスワードが違います。");
            		}
            		
                    if(auto) autoInt = 0;
                    else autoInt = 1;
                    
                    sql.updateAutoLogin(p.getName(), autoInt);
            		
            		break;
            		
            	case "パスワードの変更": 
            		if(pass.equals(sql.getPassword(p.getName()))) {
        				sql.updatePassword(p.getName(), area);
        				p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] パスワードを更新しました。: " + area);
        				
        				if(plugin.allowMail()) {
                        	mail.sendMail("パスワード変更", "パスワードを変更しました。", sql.getMail(p.getName()), pass, p.getName(), MailManager.TYPE_PASSWORD);
            				p.sendMessage(TextFormat.GREEN + "[CoSSeLoginSystem] パスワードを登録したメールアドレス宛に送信しました。");
        				}
            		} else {
            			p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] パスワードが違います。");
            		}
            		
                    if(auto) autoInt = 0;
                    else autoInt = 1;
                    
                    sql.updateAutoLogin(p.getName(), autoInt);
            		
            		break;
            		
            	case "アカウントの削除":
            		if(pass.equals(sql.getPassword(p.getName()))) {
            			
            			if(pass.equals(area)) {
            				sql.updatePassword(p.getName(), area);
            				if(plugin.allowMail()) {
                            	mail.sendMail("アカウント削除", "アカウントを削除しました。", sql.getMail(p.getName()), pass, p.getName(), MailManager.TYPE_PASSWORD);
                				p.kick("[CoSSeLoginSystem] アカウントを削除しました。");
                				sql.deleteAccount(p.getName());
                				
            				} 
            				
            			} else {
        					p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] 入力されたパスワードが一致しません。");
        				}
            			
            		} else {
    					p.sendMessage(TextFormat.RED + "[CoSSeLoginSystem] パスワードが違います。");
    				}
            		
            		break;
            		
            	case "何もしない":
            		break;
            		
            }
        }
    }

    @EventHandler
    public void onCloseWindow(PlayerModalFormCloseEvent e) {
    	
    	if(l == null) l = plugin.getEventListener();

        Player p = e.getPlayer();

        if(e.getFormId() == l.getWindowId()) {

            switch(type) {

                case TYPE_NEW:
                    l.setWindowId(getRandom());
                    l.setSendingWindow(true);
                    type = TYPE_NEW;
                    manager.sendCreateWindow(p, l.getWindowId(), "ログインに失敗しました。: サーバーへようこそ！ログインをしてください。");
                    break;

                case TYPE_CHANGED_IP:
                    l.setWindowId(getRandom());
                    l.setSendingWindow(true);
                    type = TYPE_CHANGED_IP;
                    manager.sendLoginWindow(p, l.getWindowId(), "ログインに失敗しました。: 前回ログイン時と情報が変わりました。ログインをしてください。");
                    break;

                case TYPE_MANUAL_LOGIN:
                    l.setWindowId(getRandom());
                    l.setSendingWindow(true);
                    type = TYPE_MANUAL_LOGIN;
                    manager.sendLoginWindow(p, l.getWindowId(), "ログインに失敗しました。: 自動ログインをしない設定になっています。");
                    break;

            }
        }
    }


    public int getRandom() {
        return rand.nextInt(100000);
    }

    public void setType(int type) {
        this.type = type;
    }


}
