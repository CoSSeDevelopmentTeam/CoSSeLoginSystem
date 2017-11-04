package itsu.mcpe.NewLoginSystem.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMessageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;
import itsu.mcpe.NewLoginSystem.manager.WindowManager;

public class EventListener implements cn.nukkit.event.Listener {

    private static final int TYPE_NEW = 0;
    private static final int TYPE_CHANGED_IP = 1;
    private static final int TYPE_MANUAL_LOGIN = 2;

    private NewLoginSystem plugin;
    private SQLSystem sql;
    private WindowManager manager;
    private WindowListener window;
    private Random rand = new Random();

    private int windowId;
    private boolean sendingWindow = false;

    private Map<String, Boolean> loggedInPlayer = new HashMap<>();

    public EventListener(NewLoginSystem plugin) {
        this.plugin = plugin;
        sql = plugin.getSQLSystem();
        manager = plugin.getWindowManager();
    }

    @EventHandler
    public void onJoin(PlayerPreLoginEvent e) {
        Player p = e.getPlayer();
        window = plugin.getWindowListener();
        loggedInPlayer.put(p.getName(), false);

        String name = p.getName();

        if(sql.existsBAN(name)) {
            p.kick("[NewLoginSystem] あなたはBAN(NLS)されています。", false);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        if(!isLoggedIn(name)) {
            if(sql.existsAccount(name)) {

                if(sql.getAddress(name).equals(p.getAddress() + p.getLoginChainData().getClientId())) {

                    if(sql.getAutoLogin(name) == 0) {
                        p.sendMessage(TextFormat.GREEN + "[NewLoginSystem] 自動ログインされました。");
                        setLoggedIn(name, true);
                        sendingWindow = false;

                    } else {
                        if(!sendingWindow) {
                            windowId = getRandom();
                            sendingWindow = true;
                            window.setType(TYPE_CHANGED_IP);
                            e.setCancelled();
                            manager.sendLoginWindow(p, windowId, "自動ログインをしない設定になっています。");
                        }
                    }

                } else {
                    if(!sendingWindow) {
                        windowId = getRandom();
                        sendingWindow = true;
                        window.setType(TYPE_CHANGED_IP);
                        e.setCancelled();
                        manager.sendLoginWindow(p, windowId, "前回ログイン時と情報が変わりました。ログインをしてください。");
                    }
                }

            } else {
                    if(!sendingWindow) {
                        windowId = getRandom();
                        sendingWindow = true;
                        window.setType(TYPE_NEW);
                        e.setCancelled();
                        manager.sendCreateWindow(p, windowId, "サーバーへようこそ！ログインをしてください。");
                    }
            }
        }
    }

    public void onChat(PlayerMessageEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        if(!isLoggedIn(name)) {
            if(sql.existsAccount(name)) {

                if(sql.getAddress(name).equals(p.getAddress() + p.getLoginChainData().getClientId())) {

                    if(sql.getAutoLogin(name) == 0) {
                        p.sendMessage(TextFormat.GREEN + "[NewLoginSystem] 自動ログインされました。");
                        setLoggedIn(name, true);
                        sendingWindow = false;

                    } else {
                        if(!sendingWindow) {
                            windowId = getRandom();
                            sendingWindow = true;
                            window.setType(TYPE_MANUAL_LOGIN);
                            e.setCancelled();
                            manager.sendLoginWindow(p, windowId, "自動ログインをしない設定になっています。");
                        }
                    }

                } else {
                    if(!sendingWindow) {
                        windowId = getRandom();
                        sendingWindow = true;
                        window.setType(TYPE_CHANGED_IP);
                        e.setCancelled();
                        manager.sendLoginWindow(p, windowId, "前回ログイン時と情報が変わりました。ログインをしてください。");
                    }
                }

            } else {
                if(!sendingWindow) {
                    windowId = getRandom();
                    sendingWindow = true;
                    window.setType(TYPE_NEW);
                    e.setCancelled();
                    manager.sendCreateWindow(p, windowId, "サーバーへようこそ！ログインをしてください。");
                }
            }
        }

    }

    public void onQuit(PlayerQuitEvent e) {
        loggedInPlayer.remove(e.getPlayer().getName());
        sendingWindow = false;
    }

    public int getRandom() {
        return rand.nextInt(100000);
    }

    public void setLoggedIn(String name, boolean b) {
        loggedInPlayer.put(name, b);
    }

    public boolean isLoggedIn(String name) {
        if(loggedInPlayer.containsKey(name)) {
            return loggedInPlayer.get(name);
        } else {
            return false;
        }
    }

    public void setWindowId(int id) {
        windowId = id;
    }

    public boolean isSendingWindow() {
        return sendingWindow;
    }

    public void setSendingWindow(boolean b) {
        sendingWindow = b;
    }

    public int getWindowId() {
        return windowId;
    }

}
