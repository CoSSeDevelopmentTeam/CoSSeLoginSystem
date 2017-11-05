package itsu.mcpe.NewLoginSystem.manager;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.window.CustomFormWindow;
import cn.nukkit.window.ModalFormWindow;
import cn.nukkit.window.element.Dropdown;
import cn.nukkit.window.element.Element;
import cn.nukkit.window.element.Input;
import cn.nukkit.window.element.Label;
import cn.nukkit.window.element.Toggle;
import itsu.mcpe.NewLoginSystem.core.NewLoginSystem;

public class WindowManager {

    private NewLoginSystem plugin;
    private String motd;

    public WindowManager(NewLoginSystem plugin, String motd) {
        this.plugin = plugin;
        this.motd = motd;
    }

    public void sendCreateWindow(Player player, int id, String text) {
        String allow = "";

        if(!plugin.allowMail()) allow = TextFormat.RED + "警告: サーバー側でメールの送信が許可されていません。";

        Element elements[] = {
                new Label(TextFormat.GREEN + motd),
                new Label(text),
                new Input("パスワード", "", ""),
                new Input("メールアドレス(任意) " + allow, "", ""),
                new Toggle("次回からのログインを省略"),
                new Label(TextFormat.RED + "必ずお読みください"),
                new Label(TextFormat.AQUA + "自動ログインについて"),
                new Label("「次回からのログインを省略」をオンにすると、自動ログイン機能が有効になります。ただし、IPアドレスまたは端末が変わった場合にログインが要求されます。"),
                new Label(TextFormat.AQUA + "メールアドレスを入力する理由"),
                new Label("パスワードを忘れた際、メールアドレスを入力することでパスワードが登録されたメールアドレス宛に送られます。"),
                new Label(TextFormat.AQUA + "個人情報保護について"),
                new Label("このプラグインは入力された個人情報をすべて暗号化して保存しています。また、アカウント削除時にはデータをすべて削除しています。何か不明な点がありましたら運営メンバーまでお問い合わせください。")
        };

        CustomFormWindow window = new CustomFormWindow(id, "CoSSeLoginSystem アカウント登録", elements);
        player.sendWindow(window);
    }

    public void sendLoginWindow(Player player, int id, String text) {
        Element elements[] = {new Label(TextFormat.GREEN + motd), new Label(text), new Input("パスワード(パスワードを忘れた場合は登録したメールアドレス)", "", ""), new Toggle("サーバーから出る")};
        CustomFormWindow window = new CustomFormWindow(id, "CoSSeLoginSystem ログイン", elements);
        player.sendWindow(window);
    }

    public List<String> sendAdminWindow(Player player, int id) {
        List<String> data = new ArrayList<>();

        for(Player p :  plugin.getServer().getOnlinePlayers().values()) {
            data.add(p.getName());
        }

        Element elements[] = {new Label(TextFormat.GREEN + motd), new Label("インプットとドロップダウン両方を選択した場合はインプットのほうが優先されます。"), new Input("名前", "", ""), new Dropdown("オンラインのプレイヤーから探す", data), new Toggle("BANを解除する")};
        CustomFormWindow window = new CustomFormWindow(id, "CoSSeLoginSystem BAN with CLS", elements);
        player.sendWindow(window);

        return data;
    }
    
    public void sendSettingWindow(Player player, int id) {
    	
    	List<String> settings = new ArrayList<>();
    	settings.add("サーバー/CoSSeLoginSystemの情報を見る");
    	settings.add("登録情報の確認");
    	settings.add("メールアドレスの変更");
    	settings.add("パスワードの変更");
    	settings.add("アカウントの削除");
    	settings.add("何もしない");
    	
    	Element elements[] = {
    			new Label(TextFormat.GREEN + motd), 
    			new Label("アカウント設定"), 
    			new Dropdown("メニュー", settings), 
    			new Input("入力エリア", "", ""), 
    			new Input("パスワード", "", ""), 
    			new Toggle("次回からのログインを省略"), 
    			new Label(TextFormat.YELLOW + "各種設定方法"), 
    			new Label(TextFormat.AQUA + "サーバー/CoSSeLoginSystemの情報を見る"), 
    			new Label(TextFormat.WHITE + "各種情報を確認します。"),
    			new Label(TextFormat.AQUA + "登録情報の確認"), 
    			new Label(TextFormat.WHITE + "あなたが登録している情報を確認します。"),
    			new Label(TextFormat.AQUA + "メールアドレスの登録"), 
    			new Label(TextFormat.WHITE + "メールアドレスを登録していない場合に登録できます。入力エリアにメールアドレス、パスワード欄にパスワードを入力してください。"),
    			new Label(TextFormat.AQUA + "メールアドレスの変更"), 
    			new Label(TextFormat.WHITE + "メールアドレスを登録している場合、登録しているメールアドレスを変更できます。入力エリアにメールアドレス、パスワード欄にパスワードを入力してください。"),
    			new Label(TextFormat.AQUA + "パスワードの変更"), 
    			new Label(TextFormat.WHITE + "パスワードを変更します。入力エリアに新しいパスワード、パスワード欄に古いパスワードを入力してください。"),
    			new Label(TextFormat.AQUA + "アカウントの削除"), 
    			new Label(TextFormat.WHITE + "アカウント削除(退会)をします。入力エリアにパスワード、パスワード欄にもパスワードを入力してください。この操作は取り消せません。操作完了後、サーバーからキックされます。"),
    			new Label(TextFormat.AQUA + "何もしない"), 
    			new Label(TextFormat.WHITE + "何もせずにウィンドウを閉じます。")
    	};
    	
        CustomFormWindow window = new CustomFormWindow(id, "CoSSeLoginSystem アカウント設定", elements);
        player.sendWindow(window);
    }
    
    public void sendModalWindow(Player p, String title, String contents, int id) {
    	ModalFormWindow window = new ModalFormWindow(id, title, contents, "閉じる", "閉じる");
        p.sendWindow(window);
    }

}
