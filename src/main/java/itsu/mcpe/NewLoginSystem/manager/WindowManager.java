package itsu.mcpe.NewLoginSystem.manager;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.window.CustomFormWindow;
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
				new Label(TextFormat.AQUA + "メールアドレスを入力するメリット"), 
				new Label("パスワードを忘れた際、メールアドレスを入力することでパスワードが登録されたメールアドレス宛に送られます。"), 
				new Label(TextFormat.AQUA + "個人情報保護について"), 
				new Label("このプラグインは入力された個人情報をすべて暗号化して保存しています。また、アカウント削除時にはデータをすべて削除しています。ご不明な点がございましたら開発者(Itsu @itsu_dev)もしくはサーバー主までお問い合わせください。")
		};
		
		CustomFormWindow window = new CustomFormWindow(id, "NewLoginSystem ログイン", elements);
		player.sendWindow(window);
	}
	
	public void sendLoginWindow(Player player, int id, String text) {
		Element elements[] = {new Label(TextFormat.GREEN + motd), new Label(text), new Input("パスワード(パスワードを忘れた場合は登録したメールアドレス)", "", ""), new Toggle("サーバーから出る")};
		CustomFormWindow window = new CustomFormWindow(id, "NewLoginSystem ログイン", elements);
		player.sendWindow(window);
	}
	
	public List<String> sendAdminWindow(Player player, int id) {
		List<String> data = new ArrayList<>();
		
		for(Player p :  plugin.getServer().getOnlinePlayers().values()) {
			data.add(p.getName());
		}
		
		Element elements[] = {new Label(TextFormat.GREEN + motd), new Label("インプットとドロップダウン両方を選択した場合はインプットのほうが優先されます。"), new Input("名前", "", ""), new Dropdown("オンラインのプレイヤーから探す", data), new Toggle("BANを解除する")};
		CustomFormWindow window = new CustomFormWindow(id, "NewLoginSystem 設定画面[BAN with NLS]", elements);
		player.sendWindow(window);
		
		return data;
	}

}
